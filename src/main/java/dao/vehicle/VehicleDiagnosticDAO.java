package dao.vehicle;

import common.DbContext;
import model.inventory.DiagnosticPart;
import model.vehicle.VehicleDiagnostic;
import model.vehicle.VehicleDiagnostic.DiagnosticTechnician;


import javax.tools.Diagnostic;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class VehicleDiagnosticDAO {

    // ================================================================
    // CREATE OPERATIONS
    // ================================================================

    /**
     * Tạo bản ghi chẩn đoán mới
     */
    public int createDiagnostic(Connection conn, VehicleDiagnostic diagnostic) throws SQLException {
        String sql = "INSERT INTO VehicleDiagnostic " +
                "(AssignmentID, IssueFound, EstimateCost, Status, CreatedAt) " +
                "VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, diagnostic.getAssignmentID());
            ps.setString(2, diagnostic.getIssueFound());
            ps.setBigDecimal(3, diagnostic.getEstimateCost());
            ps.setBoolean(4, diagnostic.isStatus());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Thêm technician vào diagnostic (bảng VehicleDiagnosticTechnician)
     * Hỗ trợ nhiều technician cùng làm một diagnostic
     */
    public boolean addTechnicianToDiagnostic(Connection conn, int diagnosticId, int technicianId,
                                             boolean isLead, double hoursSpent) throws SQLException {
        String sql = "INSERT INTO VehicleDiagnosticTechnician " +
                "(VehicleDiagnosticID, TechnicianID, IsLead, HoursSpent, AddedAt) " +
                "VALUES (?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "IsLead = VALUES(IsLead), HoursSpent = VALUES(HoursSpent)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, technicianId);
            ps.setBoolean(3, isLead);
            ps.setDouble(4, hoursSpent);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // UPDATE OPERATIONS
    // ================================================================

    /**
     * Cập nhật EstimateCost của diagnostic
     * Thường dùng sau khi tính tổng labor + parts cost
     */
    public boolean updateEstimateCost(Connection conn, int diagnosticId, BigDecimal totalEstimate)
            throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET EstimateCost = ? WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, totalEstimate);
            ps.setInt(2, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateDiagnosticIssueOnly(Connection c, int diagnosticId, String issueFound) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET issueFound = ? WHERE vehicleDiagnosticID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, issueFound);
            ps.setInt(2, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật issue found và estimate cost
     */
    public boolean updateDiagnostic(Connection conn, int diagnosticId,
                                    String issueFound, BigDecimal estimateCost) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic " +
                "SET IssueFound = ?, EstimateCost = ? " +
                "WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, issueFound);
            ps.setBigDecimal(2, estimateCost);
            ps.setInt(3, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật số giờ làm việc của technician trong diagnostic
     */
    public boolean updateTechnicianHours(Connection conn, int diagnosticId, int technicianId,
                                         double additionalHours) throws SQLException {
        String sql = "UPDATE VehicleDiagnosticTechnician " +
                "SET HoursSpent = HoursSpent + ? " +
                "WHERE VehicleDiagnosticID = ? AND TechnicianID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, additionalHours);
            ps.setInt(2, diagnosticId);
            ps.setInt(3, technicianId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật status của diagnostic (1=active, 0=inactive/deleted)
     */
    public boolean updateStatus(Connection conn, int diagnosticId, boolean status) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET Status = ? WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // READ OPERATIONS - SINGLE RECORD
    // ================================================================

    /**
     * Lấy diagnostic theo ID (core fields only)
     */
    public VehicleDiagnostic getDiagnosticById(Connection conn, int diagnosticId) throws SQLException {
        String sql = "SELECT * FROM VehicleDiagnostic WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDiagnostic(rs);
                }
            }
        }
        return null;
    }

    public Integer getAssignmentIdByDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = "SELECT AssignmentID FROm VehicleDiagnostic WHERE VehicleDiagnosticID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AssignmentID");
                }
            }
        }
        return null;
    }

    /**
     * Lấy diagnostic với đầy đủ thông tin (vehicle, customer, technician)
     */
    public VehicleDiagnostic getDiagnosticWithFullInfo(Connection conn, int diagnosticId)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    vd.*, " +
                        // Vehicle info
                        "    v.VehicleID, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model, " +
                        "    v.YearManufacture, " +
                        // Customer info
                        "    c.CustomerID, " +
                        "    u_cust.FullName AS CustomerName, " +
                        "    u_cust.PhoneNumber AS CustomerPhone, " +
                        "    u_cust.Email AS CustomerEmail, " +
                        // Task info
                        "    ta.AssignmentID, " +
                        "    ta.TaskDescription, " +
                        "    ta.Status AS TaskStatus, " +
                        // Lead Technician info
                        "    ta.AssignToTechID, " +
                        "    u_tech.FullName AS TechnicianName, " +
                        "    u_tech.Email AS TechnicianEmail, " +
                        "    e.EmployeeCode, " +
                        // WorkOrder info
                        "    wo.WorkOrderID, " +
                        "    wo.Status AS WorkOrderStatus " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +
                        "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                        "JOIN User u_tech ON e.UserID = u_tech.UserID " +
                        "WHERE vd.VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);

                    // Set vehicle info
                    String vehicleInfo = String.format("%s - %s %s (%d)",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"),
                            rs.getInt("YearManufacture")
                    );
                    vd.setVehicleInfo(vehicleInfo);

                    // Set technician info
                    vd.setTechnicianName(rs.getString("TechnicianName"));

                    // Load all technicians (nếu có nhiều người)
                    List<DiagnosticTechnician> technicians = getTechniciansByDiagnostic(conn, diagnosticId);
                    vd.setTechnicians(technicians);

                    return vd;
                }
            }
        }
        return null;
    }

    // ================================================================
    // READ OPERATIONS - LISTS
    // ================================================================

    /**
     * Lấy diagnostics theo AssignmentID
     */
    public List<VehicleDiagnostic> getDiagnosticsByAssignment(Connection conn, int assignmentId)
            throws SQLException {
        String sql = "SELECT * FROM VehicleDiagnostic " +
                "WHERE AssignmentID = ? " +
                "ORDER BY CreatedAt DESC";

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapDiagnostic(rs));
                }
            }
        }
        return list;
    }

    /**
     * Lấy danh sách technicians tham gia diagnostic
     */
    public List<DiagnosticTechnician> getTechniciansByDiagnostic(Connection conn, int diagnosticId)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    vdt.TechnicianID, " +
                        "    vdt.IsLead, " +
                        "    vdt.HoursSpent, " +
                        "    vdt.AddedAt, " +
                        "    u.FullName AS TechnicianName, " +
                        "    u.Email, " +
                        "    u.PhoneNumber, " +
                        "    e.EmployeeCode " +
                        "FROM VehicleDiagnosticTechnician vdt " +
                        "JOIN Employee e ON vdt.TechnicianID = e.EmployeeID " +
                        "JOIN User u ON e.UserID = u.UserID " +
                        "WHERE vdt.VehicleDiagnosticID = ? " +
                        "ORDER BY vdt.IsLead DESC, vdt.AddedAt";

        List<DiagnosticTechnician> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiagnosticTechnician dt = new DiagnosticTechnician();
                    dt.setTechnicianID(rs.getInt("TechnicianID"));
                    dt.setTechnicianName(rs.getString("TechnicianName"));
                    dt.setLead(rs.getBoolean("IsLead"));
                    dt.setHoursSpent(rs.getDouble("HoursSpent"));
                    list.add(dt);
                }
            }
        }
        return list;
    }

    /**
     * Lấy diagnostics chờ approval (cho Manager review)
     */
    public List<VehicleDiagnostic> getPendingApprovalDiagnostics(Connection conn) throws SQLException {
        String sql =
                "SELECT " +
                        "    vd.*, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model, " +
                        "    v.YearManufacture, " +
                        "    u.FullName AS TechName, " +
                        "    u.Email AS TechEmail, " +
                        "    ta.AssignToTechID, " +
                        "    ta.AssignedDate, " +
                        "    wo.WorkOrderID, " +
                        "    wo.Status AS WorkOrderStatus, " +
                        "    u_cust.FullName AS CustomerName " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +
                        "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                        "JOIN User u ON e.UserID = u.UserID " +
                        "WHERE vd.Status = 1 " +
                        "  AND NOT EXISTS ( " +
                        "      SELECT 1 FROM WorkOrderDetail wod2 " +
                        "      WHERE wod2.diagnostic_id = vd.VehicleDiagnosticID " +
                        "        AND wod2.approval_status = 'APPROVED' " +
                        "  ) " +
                        "ORDER BY vd.CreatedAt DESC";

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VehicleDiagnostic vd = mapDiagnostic(rs);

                // Set vehicle info
                String vehicleInfo = String.format("%s - %s %s (%d)",
                        rs.getString("LicensePlate"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getInt("YearManufacture")
                );
                vd.setVehicleInfo(vehicleInfo);

                // Set technician info
                vd.setTechnicianName(rs.getString("TechName"));

                list.add(vd);
            }
        }
        return list;
    }

    /**
     * Lấy diagnostics theo technician (technician xem lịch sử của mình)
     */
    public List<VehicleDiagnostic> getDiagnosticsByTechnician(Connection conn, int technicianId, int limit)
            throws SQLException {
        String sql =
                "SELECT " +
                        "    vd.*, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model, " +
                        "    v.YearManufacture, " +
                        "    ta.TaskDescription, " +
                        "    ta.Status AS TaskStatus " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE ta.AssignToTechID = ? " +
                        "ORDER BY vd.CreatedAt DESC " +
                        (limit > 0 ? "LIMIT ?" : "");

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            if (limit > 0) {
                ps.setInt(2, limit);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);

                    String vehicleInfo = String.format("%s - %s %s (%d)",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"),
                            rs.getInt("YearManufacture")
                    );
                    vd.setVehicleInfo(vehicleInfo);

                    list.add(vd);
                }
            }
        }
        return list;
    }

    /**
     * Tìm kiếm diagnostics theo multiple criteria
     */
    public List<VehicleDiagnostic> searchDiagnostics(Connection conn,
                                                     String licensePlate,
                                                     int technicianId,
                                                     LocalDate fromDate,
                                                     LocalDate toDate,
                                                     Boolean status) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "    vd.*, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model, " +
                        "    v.YearManufacture, " +
                        "    u.FullName AS TechName, " +
                        "    u_cust.FullName AS CustomerName " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +
                        "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                        "JOIN User u ON e.UserID = u.UserID " +
                        "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        // Filter by license plate
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            sql.append("AND v.LicensePlate LIKE ? ");
            params.add("%" + licensePlate.trim() + "%");
        }

        // Filter by technician
        if (technicianId > 0) {
            sql.append("AND ta.AssignToTechID = ? ");
            params.add(technicianId);
        }

        // Filter by date range
        if (fromDate != null) {
            sql.append("AND DATE(vd.CreatedAt) >= ? ");
            params.add(Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append("AND DATE(vd.CreatedAt) <= ? ");
            params.add(Date.valueOf(toDate));
        }

        // Filter by status
        if (status != null) {
            sql.append("AND vd.Status = ? ");
            params.add(status);
        }

        sql.append("ORDER BY vd.CreatedAt DESC");

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);

                    String vehicleInfo = String.format("%s - %s %s (%d)",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"),
                            rs.getInt("YearManufacture")
                    );
                    vd.setVehicleInfo(vehicleInfo);
                    vd.setTechnicianName(rs.getString("TechName"));

                    list.add(vd);
                }
            }
        }
        return list;
    }

    // ================================================================
    // STATISTICS & COUNT OPERATIONS
    // ================================================================

    /**
     * Đếm số diagnostics theo technician
     */
    public int countDiagnosticsByTechnician(Connection conn, int technicianId) throws SQLException {
        String sql =
                "SELECT COUNT(*) AS total " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "WHERE ta.AssignToTechID = ? AND vd.Status = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public List<VehicleDiagnostic> getByAssignmentPaged(int assignmentId, int offset, int limit) throws SQLException {
        String sql = """
        SELECT vd.*
        FROM VehicleDiagnostic vd
        WHERE vd.AssignmentID = ?
        ORDER BY vd.CreatedAt DESC, vd.VehicleDiagnosticID DESC
        LIMIT ? OFFSET ?
    """;
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<VehicleDiagnostic> out = new ArrayList<>();
                while (rs.next()) out.add(mapDiagnostic(rs));
                return out;
            }
        }
    }

    public Map<Integer, List<DiagnosticPart>> getPartsForDiagnostics(List<Integer> diagIds) throws SQLException {
        Map<Integer, List<DiagnosticPart>> map = new HashMap<>();
        if(diagIds == null || diagIds.isEmpty()){return map;}

        String placeholders = diagIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = """
                SELECT dp.*, pd.SKU, p.PartName, p.PartCode, p.Category
                FROM DiagnosticPart dp
                JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID
                JOIN Part p ON p.PartID = pd.PartID
                WHERE dp.VehicleDiagnosticID IN (%s)
                ORDER BY dp.VehicleDiagnosticID, dp.DiagnosticPartID
                
                """.formatted(placeholders);

        try (Connection c = DbContext.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)){
            int idx = 1;
            for (Integer id : diagIds)
                ps.setInt(idx++, id);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    DiagnosticPart d = new DiagnosticPart();
                    int diagId = rs.getInt("VehicleDiagnosticID");
                    d.setDiagnosticPartID(rs.getInt("DiagnosticPartID"));
                    d.setVehicleDiagnosticID(diagId);
                    d.setPartDetailID(rs.getInt("PartDetailID"));
                    d.setQuantityNeeded(rs.getInt("QuantityNeeded"));
                    d.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    d.setPartCondition(rs.getString("PartCondition"));
                    d.setReasonForReplacement(rs.getString("ReasonForReplacement"));
                    d.setApproved(rs.getBoolean("IsApproved"));

                    d.setSku(rs.getString("SKU"));
                    d.setPartName(rs.getString("PartName"));
                    d.setPartCode(rs.getString("PartCode"));
                    d.setCategory(rs.getString("Category"));

                    map.computeIfAbsent(diagId, k -> new ArrayList<>()).add(d);
                }
            }
        }

        return map;
    }

    public int countByAssignment(int assignmentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM VehicleDiagnostic WHERE AssignmentID = ?";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        }
    }



    /**
     * Đếm diagnostics pending approval
     */
    public int countPendingApprovalDiagnostics(Connection conn) throws SQLException {
        String sql =
                "SELECT COUNT(*) AS total " +
                        "FROM VehicleDiagnostic vd " +
                        "WHERE vd.Status = 1 " +
                        "  AND NOT EXISTS ( " +
                        "      SELECT 1 FROM WorkOrderDetail wod " +
                        "      WHERE wod.diagnostic_id = vd.VehicleDiagnosticID " +
                        "        AND wod.approval_status = 'APPROVED' " +
                        "  )";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Đếm diagnostics theo date range
     *

     */
    public int countDiagnosticsByDateRange(Connection conn, LocalDate fromDate, LocalDate toDate)
            throws SQLException {
        String sql =
                "SELECT COUNT(*) AS total " +
                        "FROM VehicleDiagnostic vd " +
                        "WHERE vd.Status = 1 " +
                        "  AND DATE(vd.CreatedAt) >= ? " +
                        "  AND DATE(vd.CreatedAt) <= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    // ================================================================
    // VALIDATION & CHECK OPERATIONS
    // ================================================================

    /**
     * Kiểm tra xem một assignment đã có diagnostic chưa

     */
    public boolean hasDiagnostic(Connection conn, int assignmentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt " +
                "FROM VehicleDiagnostic " +
                "WHERE AssignmentID = ? AND Status = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        }
        return false;
    }

    public boolean hasAnyApprovedParts(Connection conn, int diagnosticId) throws SQLException {
        String sql = "SELECT COUNT(*) cnt FROM DiagnosticPart WHERE VehicleDiagnosticID = ? AND IsApproved = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0;
            }
        }
    }

    public boolean hasApprovedWorkOrderForDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = """
                SELECT 1 FROM WorkOrderDetail wod
                WHERE wod.diagnostic_id = ?
                AND wod.approval_status = 'APPROVED'
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                   return rs.next();
            }
        }
    }

    /** Xoá hết parts của 1 diagnostic (để re-insert theo form edit) */
    public int deletePartsByDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = "DELETE FROM DiagnosticPart WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            return ps.executeUpdate();
        }
    }

    /** Insert 1 dòng part */
    public int insertDiagnosticPart(Connection conn, DiagnosticPart p) throws SQLException{
        String sql = "INSERT INTO DiagnosticPart " +
                "(VehicleDiagnosticID, PartDetailID, QuantityNeeded, UnitPrice, PartCondition, ReasonForReplacement, IsApproved) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getVehicleDiagnosticID());
            ps.setInt(2, p.getPartDetailID());
            ps.setInt(3, p.getQuantityNeeded());
            ps.setBigDecimal(4, p.getUnitPrice());
            ps.setString(5, p.getPartCondition().name());
            ps.setString(6, p.getReasonForReplacement());
            ps.setBoolean(7, Boolean.TRUE.equals(p.isApproved())); // edit draft: thường false
            return ps.executeUpdate();
        }
    }



    /**
     * Kiểm tra technician có quyền access diagnostic không
     */
    public boolean canTechnicianAccessDiagnostic(Connection conn, int diagnosticId, int technicianId)
            throws SQLException {
        String sql =
                "SELECT 1 " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "WHERE vd.VehicleDiagnosticID = ? AND ta.AssignToTechID = ? " +
                        "UNION ALL " +
                        "SELECT 1 " +
                        "FROM VehicleDiagnosticTechnician vdt " +
                        "WHERE vdt.VehicleDiagnosticID = ? AND vdt.TechnicianID = ? " +
                        "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, technicianId);
            ps.setInt(3, diagnosticId);
            ps.setInt(4, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ================================================================
    // DELETE OPERATIONS
    // ================================================================

    /**
     * Xóa diagnostic (soft delete - set status = 0)
     */
    public boolean deleteDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        return updateStatus(conn, diagnosticId, false);
    }

    /**
     * Xóa vĩnh viễn diagnostic (hard delete - cẩn thận!)
     * Sẽ cascade delete DiagnosticPart và VehicleDiagnosticTechnician
     */
    public boolean permanentlyDeleteDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = "DELETE FROM VehicleDiagnostic WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Xóa technician khỏi diagnostic
     *
     */
    public boolean removeTechnicianFromDiagnostic(Connection conn, int diagnosticId, int technicianId)
            throws SQLException {
        String sql = "DELETE FROM VehicleDiagnosticTechnician " +
                "WHERE VehicleDiagnosticID = ? AND TechnicianID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, technicianId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // HELPER - MAPPING METHOD
    // ================================================================


    private VehicleDiagnostic mapDiagnostic(ResultSet rs) throws SQLException {
        VehicleDiagnostic vd = new VehicleDiagnostic();

        vd.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
        vd.setAssignmentID(rs.getInt("AssignmentID"));
        vd.setIssueFound(rs.getString("IssueFound"));

        BigDecimal estimateCost = rs.getBigDecimal("EstimateCost");
        vd.setEstimateCost(estimateCost != null ? estimateCost : BigDecimal.ZERO);

        vd.setStatus(rs.getBoolean("Status"));

        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            vd.setCreatedAt(createdAt.toLocalDateTime());
        }

        return vd;
    }
}