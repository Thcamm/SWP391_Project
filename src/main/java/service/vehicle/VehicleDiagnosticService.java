package service.vehicle;

import common.DbContext;
import common.constant.MessageConstants;
import common.message.ServiceResult;
import common.utils.PaginationUtils;
import dao.vehicle.VehicleDiagnosticDAO;
import model.inventory.DiagnosticPart;
import model.inventory.PartDetail;
import model.pagination.PaginationResponse;
import model.vehicle.VehicleDiagnostic;
import service.inventory.PartService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class VehicleDiagnosticService {
    private final VehicleDiagnosticDAO diagnosticDAO = new VehicleDiagnosticDAO();
    private final PartService partService = new PartService();


    public static  class DiagnosticPageView {

        public PaginationResponse<VehicleDiagnostic> page ;
        public Map<Integer, List<DiagnosticPart>> partsMap = Collections.emptyMap();

        public Map<Integer, BigDecimal> partsTotal = new HashMap<>();
        public Map<Integer, BigDecimal> grandTotal = new HashMap<>();
        public Map<Integer, Integer>   approvedCount = new HashMap<>();
        public Map<Integer, Integer>   pendingCount  = new HashMap<>();
        public Integer latestDiagnosticId;

        // Thêm các convenience getters để JSP dễ truy cập
        public List<VehicleDiagnostic> getDiagnostics() {
            return page != null ? page.getData() : Collections.emptyList();
        }

        public int getTotalItems() {
            return page != null ? page.getTotalItems() : 0;
        }

        public int getCurrentPage() {
            return page != null ? page.getCurrentPage() : 1;
        }

        public int getTotalPages() {
            return page != null ? page.getTotalPages() : 0;
        }

        public int getItemsPerPage() {
            return page != null ? page.getItemsPerPage() : 10;
        }

        public PaginationResponse<VehicleDiagnostic> getPage() {
            return page;
        }

        public void setPage(PaginationResponse<VehicleDiagnostic> page) {
            this.page = page;
        }

        public Map<Integer, List<DiagnosticPart>> getPartsMap() {
            return partsMap;
        }

        public void setPartsMap(Map<Integer, List<DiagnosticPart>> partsMap) {
            this.partsMap = partsMap;
        }

        public Map<Integer, BigDecimal> getPartsTotal() {
            return partsTotal;
        }

        public void setPartsTotal(Map<Integer, BigDecimal> partsTotal) {
            this.partsTotal = partsTotal;
        }

        public Map<Integer, BigDecimal> getGrandTotal() {
            return grandTotal;
        }

        public void setGrandTotal(Map<Integer, BigDecimal> grandTotal) {
            this.grandTotal = grandTotal;
        }

        public Map<Integer, Integer> getApprovedCount() {
            return approvedCount;
        }

        public void setApprovedCount(Map<Integer, Integer> approvedCount) {
            this.approvedCount = approvedCount;
        }

        public Map<Integer, Integer> getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(Map<Integer, Integer> pendingCount) {
            this.pendingCount = pendingCount;
        }

        public Integer getLatestDiagnosticId() {
            return latestDiagnosticId;
        }

        public void setLatestDiagnosticId(Integer latestDiagnosticId) {
            this.latestDiagnosticId = latestDiagnosticId;
        }

        // tiện đánh dấu bản mới nhất

    }

    public static class EditDiagnosticVM {
        public VehicleDiagnostic diagnostic;
        public List<DiagnosticPart> parts = Collections.emptyList();
        public boolean locked; // true: đã có Approved -> không cho sửa

        public VehicleDiagnostic getDiagnostic() {
            return diagnostic;
        }

        public void setDiagnostic(VehicleDiagnostic diagnostic) {
            this.diagnostic = diagnostic;
        }

        public List<DiagnosticPart> getParts() {
            return parts;
        }

        public void setParts(List<DiagnosticPart> parts) {
            this.parts = parts;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }

    public ServiceResult getDiagnosticsWithPartsPaged (int assignmentId, int currentPage, int itemsPerPage) {
        try {
            int totalItems = diagnosticDAO.countByAssignment(assignmentId);
            int size = Math.max(5, Math.min(itemsPerPage > 0 ? itemsPerPage : 10, 50));

            PaginationUtils.PaginationCalculation calc = PaginationUtils.calculateParams(totalItems, Math.max(1, currentPage), size);

            List<VehicleDiagnostic> rows = totalItems == 0 ? Collections.emptyList()
                    : diagnosticDAO.getByAssignmentPaged(assignmentId, calc.getOffset(), size);

            PaginationResponse<VehicleDiagnostic> page = new PaginationResponse<>(
                    rows,
                    calc.getSafePage(),
                    size,
                    totalItems,
                    calc.getTotalPages()
            );

            DiagnosticPageView vm = new DiagnosticPageView();
            vm.setPage(page);
            vm.setLatestDiagnosticId(rows.isEmpty() ? null : rows.get(0).getVehicleDiagnosticID());

            if(!rows.isEmpty()) {
                List<Integer> ids = new ArrayList<>(rows.size());
                for (VehicleDiagnostic vd : rows) {
                    ids.add(vd.getVehicleDiagnosticID());
                }

                Map<Integer, List<DiagnosticPart>> partsMap = diagnosticDAO.getPartsForDiagnostics(ids);
                vm.setPartsMap(partsMap);

                for (VehicleDiagnostic vd : rows) {
                    int id = vd.getVehicleDiagnosticID();
                    List<DiagnosticPart> list = partsMap.getOrDefault(id, Collections.emptyList());
                    BigDecimal partsSum = BigDecimal.ZERO;
                    int approved = 0;
                    int pending = 0;
                    for (DiagnosticPart dp : list) {
                        BigDecimal price = dp.getUnitPrice() == null ? BigDecimal.ZERO : dp.getUnitPrice();
                        BigDecimal line = price.multiply(BigDecimal.valueOf(dp.getQuantityNeeded()));
                        partsSum = partsSum.add(line);
                        if(Boolean.TRUE.equals(dp.isApproved())) {
                            approved++;
                        }else  {
                            pending++;
                        }
                    }

                    vm.partsTotal.put(id, partsSum);

                    // labor = estimateCost - partsSum
                    BigDecimal totalEstimate = vd.getEstimateCost() == null ? BigDecimal.ZERO : vd.getEstimateCost();
                    BigDecimal laborCost = totalEstimate.subtract(partsSum);
                    if (laborCost.compareTo(BigDecimal.ZERO) < 0) {
                        laborCost = BigDecimal.ZERO;
                    }

                    // SET laborCostCalculated vào object
                    vd.setLaborCostCalculated(laborCost);

                    vm.grandTotal.put(id, totalEstimate); // Total = estimateCost (đã bao gồm labor + parts)
                    vm.approvedCount.put(id, approved);
                    vm.pendingCount.put(id, pending);
                }
            }
            return ServiceResult.success(MessageConstants.DIAG003, vm);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ServiceResult loadForEdit (int technicianId, int diagnosticId) {
        try (Connection c = DbContext.getConnection()){
            VehicleDiagnostic vd = diagnosticDAO.getDiagnosticWithFullInfo(c, diagnosticId);

            if(vd == null){
                return ServiceResult.error(MessageConstants.ERR002);
            }

            if(!diagnosticDAO.canTechnicianAccessDiagnostic(c, diagnosticId, technicianId)){
                return ServiceResult.error(MessageConstants.AUTH001);
            }

            if (vd.isApproved()) {
                return ServiceResult.error(MessageConstants.DIAG005 /* "Diagnostic is approved and cannot be edited." */);
            }

            boolean locked = diagnosticDAO.hasAnyApprovedParts(c, diagnosticId)
                    || diagnosticDAO.hasApprovedWorkOrderForDiagnostic(c, diagnosticId);

            Map<Integer, List<DiagnosticPart>> map = diagnosticDAO.getPartsForDiagnostics(Collections.singletonList(diagnosticId));
            List<DiagnosticPart> parts = map.getOrDefault(diagnosticId, Collections.emptyList());

            // TÍNH TỔNG PARTS
            BigDecimal partsSum = BigDecimal.ZERO;
            for (DiagnosticPart dp : parts) {
                BigDecimal price = dp.getUnitPrice() == null ? BigDecimal.ZERO : dp.getUnitPrice();
                BigDecimal line = price.multiply(BigDecimal.valueOf(dp.getQuantityNeeded()));
                partsSum = partsSum.add(line);
            }

            //TÍNH NGƯỢC LABOR = estimateCost - partsSum
            BigDecimal totalEstimate = vd.getEstimateCost() == null ? BigDecimal.ZERO : vd.getEstimateCost();
            BigDecimal laborCost = totalEstimate.subtract(partsSum);

            // Đảm bảo labor không âm
            if (laborCost.compareTo(BigDecimal.ZERO) < 0) {
                laborCost = BigDecimal.ZERO;
            }

            //SET VÀO FIELD RIÊNG
            vd.setLaborCostCalculated(laborCost);

            EditDiagnosticVM vm = new EditDiagnosticVM();
            vm.setDiagnostic(vd);
            vm.setParts(parts);
            vm.setLocked(locked);

            return ServiceResult.success(MessageConstants.DIAG003, vm);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** cap nhat va chuan doan khi van con la pending */
    public ServiceResult updateDiagnosticDraf(
            int technicianId,
            int diagnosticId,
            String issueFound,
            BigDecimal laborCost,
            List<DiagnosticPart> newParts
    ){
        try (Connection c = DbContext.getConnection(false)){
            VehicleDiagnostic vd = diagnosticDAO.getDiagnosticWithFullInfo(c, diagnosticId);
            if(vd == null){
                return ServiceResult.error(MessageConstants.ERR002);
            }

            if(!diagnosticDAO.canTechnicianAccessDiagnostic(c, diagnosticId, technicianId)){
                return ServiceResult.error(MessageConstants.AUTH001);
            }

            boolean locked = diagnosticDAO.hasAnyApprovedParts(c, diagnosticId)
                    || diagnosticDAO.hasApprovedWorkOrderForDiagnostic(c, diagnosticId);

            if(locked){
                return ServiceResult.error(MessageConstants.AUTH001);
            }

            if (issueFound == null || issueFound.trim().length() < 20) {
                return ServiceResult.error(MessageConstants.ERR003);
            }

            if (laborCost == null || laborCost.compareTo(BigDecimal.ZERO) < 0) {
                laborCost = BigDecimal.ZERO;
            }


            String updateSQL = "UPDATE VehicleDiagnostic SET IssueFound = ? WHERE VehicleDiagnosticID = ?";
            try (PreparedStatement ps = c.prepareStatement(updateSQL)) {
                ps.setString(1, issueFound.trim());
                ps.setInt(2, diagnosticId);
                if (ps.executeUpdate() == 0) {
                    DbContext.rollback(c);
                    return ServiceResult.error(MessageConstants.DIAG004);
                }
            }

            // XÓA PARTS CŨ
            diagnosticDAO.deletePartsByDiagnostic(c, diagnosticId);

            // TÍNH TỔNG PARTS MỚI
            BigDecimal partsSum = BigDecimal.ZERO;
            if(newParts != null){
                for (DiagnosticPart p : newParts){
                    ServiceResult pr = partService.getPartDetailById(p.getPartDetailID());
                    if(pr.isError() || pr.getData() == null){
                        DbContext.rollback(c);
                        return ServiceResult.error(pr.getMessage());
                    }

                    PartDetail pd = pr.getData(PartDetail.class);

                    int qty = Math.max(1, p.getQuantityNeeded());
                    BigDecimal unit = pd.getUnitPrice() == null ? BigDecimal.ZERO : pd.getUnitPrice();

                    DiagnosticPart row = new DiagnosticPart();
                    row.setVehicleDiagnosticID(diagnosticId);
                    row.setPartDetailID(p.getPartDetailID());
                    row.setQuantityNeeded(qty);
                    row.setUnitPrice(unit);
                    row.setPartCondition(p.getPartCondition());
                    row.setReasonForReplacement(p.getReasonForReplacement());
                    row.setApproved(false);

                    diagnosticDAO.insertDiagnosticPart(c, row);

                    partsSum = partsSum.add(unit.multiply(BigDecimal.valueOf(qty)));
                }
            }


            BigDecimal totalEstimate = laborCost.add(partsSum);
            diagnosticDAO.updateEstimateCost(c, diagnosticId, totalEstimate);

            DbContext.commitAndClose(c);
            return ServiceResult.success(MessageConstants.DIAG002, diagnosticId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
