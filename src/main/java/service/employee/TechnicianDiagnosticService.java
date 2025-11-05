package service.employee;

import common.DbContext;
import common.constant.MessageConstants;
import common.message.ServiceResult;
import dao.vehicle.VehicleDiagnosticDAO;
import dao.inventory.DiagnosticPartDAO;
import dao.employee.technician.TechnicianActivityDAO;
import model.vehicle.VehicleDiagnostic;
import model.inventory.DiagnosticPart;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

/**
 * Service layer for Technician Diagnostic operations
 * Handles business logic for creating, updating, and managing diagnostics
 *
 * @author vuthithuy-qh
 * @version 1.0
 * @since 2025-11-01
 */
public class TechnicianDiagnosticService {

    private final VehicleDiagnosticDAO diagnosticDAO = new VehicleDiagnosticDAO();
    private final DiagnosticPartDAO partDAO = new DiagnosticPartDAO();
    private final TechnicianActivityDAO activityDAO = new TechnicianActivityDAO();

    // ================================================================
    // CREATE OPERATIONS
    // ================================================================

    /**
     * Tạo diagnostic với parts (full transaction)
     */
    public ServiceResult createDiagnosticWithParts(int technicianId, VehicleDiagnostic diagnostic) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            if (!canTechnicianCreateDiagnostic(conn, technicianId, diagnostic.getAssignmentID())) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.TASK011);
            }

            // VALIDATE DATA
            if (!diagnostic.isValid()) {
                conn.rollback();
                List<String> errors = diagnostic.getValidationErrors();
                return ServiceResult.error(MessageConstants.ERR003, errors);
            }

            // TÍNH TOTAL ESTIMATE TRƯỚC KHI CREATE
            // LẤY LABOR TỪ laborCostInput (nếu có)
            BigDecimal laborCost = diagnostic.getLaborCostInput() != null
                    ? diagnostic.getLaborCostInput()
                    : BigDecimal.ZERO;

            BigDecimal totalPartsCost = BigDecimal.ZERO;
            int partsCount = 0;

            if (diagnostic.getParts() != null && !diagnostic.getParts().isEmpty()) {
                for (DiagnosticPart part : diagnostic.getParts()) {
                    BigDecimal price = part.getUnitPrice() == null ? BigDecimal.ZERO : part.getUnitPrice();
                    BigDecimal line = price.multiply(BigDecimal.valueOf(part.getQuantityNeeded()));
                    totalPartsCost = totalPartsCost.add(line);
                    partsCount++;
                }
            }

            BigDecimal totalEstimate = laborCost.add(totalPartsCost);
            diagnostic.setEstimateCost(totalEstimate); // Set total estimate

            // CREATE DIAGNOSTIC (với estimateCost đã đúng)
            int diagnosticId = diagnosticDAO.createDiagnostic(conn, diagnostic);
            if (diagnosticId <= 0) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR001);
            }

            // ADD TECHNICIAN AS LEAD
            boolean techAdded = diagnosticDAO.addTechnicianToDiagnostic(
                    conn, diagnosticId, technicianId, true, 0.0);

            if (!techAdded) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR002);
            }

            // ADD PARTS (IF ANY)
            if (diagnostic.getParts() != null && !diagnostic.getParts().isEmpty()) {
                for (DiagnosticPart part : diagnostic.getParts()) {
                    part.setVehicleDiagnosticID(diagnosticId);
                    part.setApproved(false);

                    int partId = partDAO.addPartToDiagnostic(conn, part);
                    if (partId <= 0) {
                        conn.rollback();
                        return ServiceResult.error(MessageConstants.ERR003,
                                "Failed to add part: " + part.getPartName());
                    }
                }
            }

            // LOG ACTIVITY
            logActivity(conn, technicianId, "DIAGNOSTIC_CREATED",
                    diagnostic.getAssignmentID(),
                    String.format("Created diagnostic with %d part(s), Total: $%.2f",
                            partsCount, totalEstimate));

            conn.commit();
            return ServiceResult.success(MessageConstants.DIAG001, diagnosticId);

        } catch (Exception e) {
            DbContext.rollback(conn);
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.DIAG001);
        } finally {
            DbContext.close(conn);
        }
    }

    /**
     * Thêm parts vào diagnostic đã tồn tại
     * (Trường hợp technician quên thêm parts lúc tạo)
     *
     * @param technicianId EmployeeID
     * @param diagnosticId VehicleDiagnosticID
     * @param parts        List of DiagnosticPart to add
     * @return ServiceResult
     */
    public ServiceResult addPartsToDiagnostic(int technicianId, int diagnosticId,
            List<DiagnosticPart> parts) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            // Validate permission
            if (!diagnosticDAO.canTechnicianAccessDiagnostic(conn, diagnosticId, technicianId)) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.TASK009);
            }

            // Get current diagnostic
            VehicleDiagnostic diagnostic = diagnosticDAO.getDiagnosticById(conn, diagnosticId);
            if (diagnostic == null) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR002);
            }
            BigDecimal currentTotal = diagnostic.getEstimateCost();
            BigDecimal oldParts = partDAO.calculateTotalPartsCost(conn, diagnosticId);
            BigDecimal labor = currentTotal.subtract(oldParts);

            // Add parts
            int addedCount = 0;
            for (DiagnosticPart part : parts) {
                part.setVehicleDiagnosticID(diagnosticId);
                part.setApproved(false);

                int partId = partDAO.addPartToDiagnostic(conn, part);
                if (partId > 0) {
                    addedCount++;
                }
            }

            if (addedCount == 0) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR001);
            }

            // Recalculate total estimate
            BigDecimal newParts = partDAO.calculateTotalPartsCost(conn, diagnosticId);
            BigDecimal totalEstimate = labor.add(newParts);
            diagnosticDAO.updateEstimateCost(conn, diagnosticId, totalEstimate);

            // Log activity
            logActivity(conn, technicianId, "DIAGNOSTIC_UPDATED",
                    diagnostic.getAssignmentID(),
                    String.format("Added %d part(s) to diagnostic", addedCount));

            conn.commit();
            return ServiceResult.success(MessageConstants.DIAG002, addedCount);

        } catch (Exception e) {
            DbContext.rollback(conn);
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    // ================================================================
    // READ OPERATIONS
    // ================================================================

    /**
     * Lấy diagnostic detail với đầy đủ thông tin
     * (Vehicle, Customer, Technicians, Parts)
     *
     * @param technicianId EmployeeID (để check permission)
     * @param diagnosticId VehicleDiagnosticID
     * @return ServiceResult with VehicleDiagnostic object
     */
    public ServiceResult getDiagnosticDetail(int technicianId, int diagnosticId) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();

            // Check permission
            if (!diagnosticDAO.canTechnicianAccessDiagnostic(conn, diagnosticId, technicianId)) {
                return ServiceResult.error(MessageConstants.TASK009);
            }

            // Get full diagnostic info
            VehicleDiagnostic diagnostic = diagnosticDAO.getDiagnosticWithFullInfo(conn, diagnosticId);
            if (diagnostic == null) {
                return ServiceResult.error(MessageConstants.ERR002);
            }

            // Get parts
            List<DiagnosticPart> parts = partDAO.getPartsByDiagnosticId(conn, diagnosticId);
            diagnostic.setParts(parts);

            return ServiceResult.success(MessageConstants.MSG001, diagnostic);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    /**
     * Lấy danh sách diagnostics của technician
     *
     * @param technicianId EmployeeID
     * @param limit        Số lượng records (0 = unlimited)
     * @return ServiceResult with List<VehicleDiagnostic>
     */
    public ServiceResult getMyDiagnostics(int technicianId, int limit) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();

            List<VehicleDiagnostic> diagnostics = diagnosticDAO.getDiagnosticsByTechnician(
                    conn, technicianId, limit);

            if (diagnostics.isEmpty()) {
                return ServiceResult.success(MessageConstants.MSG001, diagnostics);
            }

            return ServiceResult.success(MessageConstants.MSG001, diagnostics);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    /**
     * Đếm số diagnostics của technician (cho statistics)
     *
     * @param technicianId EmployeeID
     * @return ServiceResult with Integer count
     */
    public ServiceResult countMyDiagnostics(int technicianId) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();

            int count = diagnosticDAO.countDiagnosticsByTechnician(conn, technicianId);

            return ServiceResult.success(MessageConstants.MSG001, count);

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    // ================================================================
    // UPDATE OPERATIONS
    // ================================================================

    /**
     * Cập nhật diagnostic info (issue, estimate cost)
     * Chỉ cho phép khi diagnostic chưa được approve
     *
     * @param technicianId EmployeeID
     * @param diagnosticId VehicleDiagnosticID
     * @param issueFound   Mô tả issue mới
     * @param laborCost    Chi phí công mới
     * @return ServiceResult
     */
    public ServiceResult updateDiagnostic(int technicianId, int diagnosticId,
            String issueFound, BigDecimal laborCost) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            // Validate permission
            if (!diagnosticDAO.canTechnicianAccessDiagnostic(conn, diagnosticId, technicianId)) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.TASK009);
            }

            // Get current diagnostic
            VehicleDiagnostic diagnostic = diagnosticDAO.getDiagnosticById(conn, diagnosticId);
            if (diagnostic == null) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR002);
            }

            // CHỈ UPDATE issueFound
            boolean updated = diagnosticDAO.updateDiagnosticIssueOnly(conn, diagnosticId, issueFound);
            if (!updated) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR001);
            }

            // TÍNH LẠI TOTAL ESTIMATE = laborCost + partsCost
            BigDecimal partsCost = partDAO.calculateTotalPartsCost(conn, diagnosticId);
            BigDecimal totalEstimate = laborCost.add(partsCost);

            // UPDATE ESTIMATE COST
            diagnosticDAO.updateEstimateCost(conn, diagnosticId, totalEstimate);

            // Log activity
            logActivity(conn, technicianId, "DIAGNOSTIC_UPDATED",
                    diagnostic.getAssignmentID(),
                    "Updated diagnostic info");

            conn.commit();
            return ServiceResult.success(MessageConstants.DIAG002);

        } catch (Exception e) {
            DbContext.rollback(conn);
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    /**
     * Xóa part khỏi diagnostic
     *
     * @param technicianId     EmployeeID
     * @param diagnosticPartId DiagnosticPartID
     * @return ServiceResult
     */
    public ServiceResult removePartFromDiagnostic(int technicianId, int diagnosticPartId) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            // Get part info để lấy diagnosticId
            DiagnosticPart part = partDAO.getDiagnosticPartById(conn, diagnosticPartId);
            if (part == null) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR002);
            }

            // Check permission
            if (!diagnosticDAO.canTechnicianAccessDiagnostic(
                    conn, part.getVehicleDiagnosticID(), technicianId)) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.TASK009);
            }

            // Check if part is already approved
            if (part.isApproved()) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR003,
                        "Cannot remove approved part");
            }

            VehicleDiagnostic diagnostic = diagnosticDAO.getDiagnosticById(conn, part.getVehicleDiagnosticID());

            BigDecimal currentTotal = diagnostic.getEstimateCost();
            BigDecimal oldParts = partDAO.calculateTotalPartsCost(conn, part.getVehicleDiagnosticID());
            BigDecimal labor = currentTotal.subtract(oldParts);

            // Remove part
            boolean removed = partDAO.removePartFromDiagnostic(conn, diagnosticPartId);
            if (!removed) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR001);
            }

            // Recalculate total estimate
            BigDecimal newParts = partDAO.calculateTotalPartsCost(conn, part.getVehicleDiagnosticID());
            BigDecimal totalEstimate = labor.add(newParts);
            diagnosticDAO.updateEstimateCost(conn, part.getVehicleDiagnosticID(), totalEstimate);

            // Log activity
            logActivity(conn, technicianId, "DIAGNOSTIC_UPDATED",
                    diagnostic.getAssignmentID(),
                    "Removed part: " + part.getPartName());

            conn.commit();
            return ServiceResult.success(MessageConstants.DIAG002);

        } catch (Exception e) {
            DbContext.rollback(conn);
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    // ================================================================
    // DELETE OPERATIONS
    // ================================================================

    /**
     * Xóa diagnostic (soft delete)
     * Chỉ cho phép xóa khi chưa được approve
     *
     * @param technicianId EmployeeID
     * @param diagnosticId VehicleDiagnosticID
     * @return ServiceResult
     */
    public ServiceResult deleteDiagnostic(int technicianId, int diagnosticId) {
        Connection conn = null;
        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false);

            // Check permission
            if (!diagnosticDAO.canTechnicianAccessDiagnostic(conn, diagnosticId, technicianId)) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.TASK009);
            }

            // Get diagnostic
            VehicleDiagnostic diagnostic = diagnosticDAO.getDiagnosticById(conn, diagnosticId);
            if (diagnostic == null) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR002);
            }

            // TODO: Check if any parts are approved
            // If yes, cannot delete

            // Soft delete
            boolean deleted = diagnosticDAO.deleteDiagnostic(conn, diagnosticId);
            if (!deleted) {
                conn.rollback();
                return ServiceResult.error(MessageConstants.ERR001);
            }

            // Log activity
            logActivity(conn, technicianId, "DIAGNOSTIC_DELETED",
                    diagnostic.getAssignmentID(),
                    "Deleted diagnostic");

            conn.commit();
            return ServiceResult.success(MessageConstants.DIAG002);

        } catch (Exception e) {
            DbContext.rollback(conn);
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR001);
        } finally {
            DbContext.close(conn);
        }
    }

    // ================================================================
    // VALIDATION HELPERS
    // ================================================================

    /**
     * Kiểm tra technician có quyền tạo diagnostic cho assignment không
     * (Phải là người được assign task đó và task phải IN_PROGRESS)
     *
     * @param conn         Database connection
     * @param technicianId EmployeeID
     * @param assignmentId TaskAssignmentID
     * @return true nếu có quyền
     */
    private boolean canTechnicianCreateDiagnostic(Connection conn, int technicianId, int assignmentId) {
        try {// BỎ điều kiện task_type vì không chắc cột này tồn tại
            String sql = "SELECT COUNT(*) AS cnt " +
                    "FROM TaskAssignment " +
                    "WHERE AssignmentID = ? " +
                    "  AND AssignToTechID = ? " +
                    "  AND Status = 'IN_PROGRESS' ";

            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, assignmentId);
                ps.setInt(2, technicianId);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("cnt") > 0;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Log activity vào TechnicianActivityLog
     *
     * @param conn         Database connection
     * @param technicianId EmployeeID
     * @param activityType Activity type (DIAGNOSTIC_CREATED, etc.)
     * @param assignmentId TaskAssignmentID
     * @param description  Description
     */
    private void logActivity(Connection conn, int technicianId, String activityType,
            int assignmentId, String description) {
        try {
            activityDAO.logActivity(conn, technicianId, activityType, assignmentId, description);
        } catch (Exception e) {
            // Log error but don't fail transaction
            e.printStackTrace();
        }
    }
}