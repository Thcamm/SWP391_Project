package service.diagnostic;

import common.DbContext;
import common.constant.MessageConstants;
import common.constant.MessageType;
import common.message.ServiceResult;
import common.message.SystemMessage;
import dao.inventory.DiagnosticPartDAO;
import dao.vehicle.VehicleDiagnosticDAO;
import model.customer.CustomerDiagnosticsView;
import model.inventory.DiagnosticPart;
import model.vehicle.VehicleDiagnostic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CustomerDiagnosticService {

    
    private final VehicleDiagnosticDAO diagnosticDAO   = new VehicleDiagnosticDAO();
    private final DiagnosticPartDAO    diagnosticPartDAO = new DiagnosticPartDAO(); // hiện tại chưa dùng, giữ lại nếu sau này cần



    public static class ListVM {
        public List<VehicleDiagnostic> rows = List.of();
        public Map<Integer, Integer> partsTotal    = new HashMap<>();
        public Map<Integer, Integer> partsApproved = new HashMap<>();

        public List<VehicleDiagnostic> getRows() {
            return rows;
        }

        public void setRows(List<VehicleDiagnostic> rows) {
            this.rows = rows;
        }

        public Map<Integer, Integer> getPartsTotal() {
            return partsTotal;
        }

        public void setPartsTotal(Map<Integer, Integer> partsTotal) {
            this.partsTotal = partsTotal;
        }

        public Map<Integer, Integer> getPartsApproved() {
            return partsApproved;
        }

        public void setPartsApproved(Map<Integer, Integer> partsApproved) {
            this.partsApproved = partsApproved;
        }
    }

    public static class DetailVM {
        public VehicleDiagnostic diag;
        public List<DiagnosticPart> parts = List.of();
        public boolean canApprove;
        public boolean canReject;

        public VehicleDiagnostic getDiag() {
            return diag;
        }

        public void setDiag(VehicleDiagnostic diag) {
            this.diag = diag;
        }

        public List<DiagnosticPart> getParts() {
            return parts;
        }

        public void setParts(List<DiagnosticPart> parts) {
            this.parts = parts;
        }

        public boolean isCanApprove() {
            return canApprove;
        }

        public void setCanApprove(boolean canApprove) {
            this.canApprove = canApprove;
        }

        public boolean isCanReject() {
            return canReject;
        }

        public void setCanReject(boolean canReject) {
            this.canReject = canReject;
        }
    }


    public ServiceResult listByRequest(int requestId) {
        try {
            List<VehicleDiagnostic> rows = diagnosticDAO.getDiagnosticsByRequest(requestId);

            ListVM vm = new ListVM();
            vm.rows = rows;

            if (!rows.isEmpty()) {
                List<Integer> ids = rows.stream()
                        .map(VehicleDiagnostic::getVehicleDiagnosticID)
                        .collect(Collectors.toList());

                // Dùng batch để tránh N+1
                Map<Integer, List<DiagnosticPart>> partsMap = diagnosticDAO.getPartsForDiagnostics(ids);

                for (VehicleDiagnostic vd : rows) {
                    int vdId = vd.getVehicleDiagnosticID();
                    List<DiagnosticPart> parts = partsMap.getOrDefault(vdId, List.of());

                    vm.partsTotal.put(vdId, parts.size());

                    int ok = (int) parts.stream()
                            .filter(p -> Boolean.TRUE.equals(p.isApproved()))
                            .count();

                    vm.partsApproved.put(vdId, ok);
                }
            }

            return ServiceResult.success(MessageConstants.DIAG003, vm);

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(
                    new SystemMessage("DIAG_LIST_ERR", MessageType.ERROR, "Diagnostic",
                            "Không tải được danh sách chẩn đoán.")
            );
        }
    }


    public ServiceResult detail(int requestId, int diagnosticId) {
        try {
            VehicleDiagnostic d = diagnosticDAO.getById(diagnosticId);
            if (d == null) {
                return ServiceResult.error(MessageConstants.ERR002); // Not found
            }

            // requestId hiện tại chưa dùng để check, giữ nguyên logic như cũ
            List<DiagnosticPart> parts = diagnosticDAO.getPartsByDiagnostic(diagnosticId);

            DetailVM vm = new DetailVM();
            vm.diag  = d;
            vm.parts = parts;

            int ok = (int) parts.stream()
                    .filter(p -> Boolean.TRUE.equals(p.isApproved()))
                    .count();

            boolean isSubmitted = d.getStatus() == VehicleDiagnostic.DiagnosticStatus.SUBMITTED;
            vm.canApprove = isSubmitted && ok > 0;
            vm.canReject  = isSubmitted && ok == 0;

            return ServiceResult.success(MessageConstants.DIAG003, vm);

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(
                    new SystemMessage("DIAG_DETAIL_ERR", MessageType.ERROR, "Diagnostic",
                            "Không tải được chi tiết chẩn đoán.")
            );
        }
    }


    public ServiceResult getDiagnosticFullInfo(int diagnosticId) {
        try (Connection conn = DbContext.getConnection()) {
            VehicleDiagnostic vd = diagnosticDAO.getDiagnosticWithFullInfo(conn, diagnosticId);
            if (vd == null) {
                return ServiceResult.error(MessageConstants.ERR002);
            }

            // Lấy map parts theo diagnosticId (hiện đang dùng overload không có Connection, giữ nguyên logic)
            Map<Integer, List<DiagnosticPart>> map =
                    diagnosticDAO.getPartsForDiagnostics(List.of(diagnosticId));

            vd.setParts(map.getOrDefault(diagnosticId, List.of()));

            return ServiceResult.success(MessageConstants.DIAG003, vd);

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.DIAG006);
        }
    }


    public ServiceResult updatePartApproval(int diagnosticPartId, boolean approved) {
        try (Connection conn = DbContext.getConnection()) {

            // Chặn chỉnh nếu diagnostic không còn SUBMITTED
            Integer diagId = diagnosticDAO.getDiagnosticIdByPartId(conn, diagnosticPartId);
            if (diagId == null) {
                return ServiceResult.error(MessageConstants.ERR002);
            }

            VehicleDiagnostic vd = diagnosticDAO.getById(diagId); // dùng DAO như cũ, giữ nguyên logic
            if (vd == null) {
                return ServiceResult.error(MessageConstants.ERR002);
            }

            if (vd.getStatus() != VehicleDiagnostic.DiagnosticStatus.SUBMITTED) {
                return ServiceResult.error(
                        new SystemMessage("DIAG_LOCKED", MessageType.WARNING, "Diagnostic",
                                "Chẩn đoán đã chốt, không thể chỉnh từng phần.")
                );
            }

            int updated = diagnosticDAO.updatePartApproval(conn, diagnosticPartId, approved);
            if (updated > 0) {
                DbContext.commit(conn);
                return ServiceResult.success(MessageConstants.PART004);
            } else {
                DbContext.rollback(conn);
                return ServiceResult.error(MessageConstants.ERR002);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.PART005);
        }
    }


    public ServiceResult finalizeDiagnosticDecision(int diagnosticId, boolean approve, String rejectReason) {
        try {
            VehicleDiagnostic vd = diagnosticDAO.getById(diagnosticId);
            if (vd == null) {
                return ServiceResult.error(MessageConstants.ERR002);
            }

            if (vd.getStatus() != VehicleDiagnostic.DiagnosticStatus.SUBMITTED) {
                return ServiceResult.error(
                        new SystemMessage("DIAG_LOCKED", MessageType.WARNING, "Diagnostic",
                                "Chẩn đoán đã được chốt trước đó.")
                );
            }


            List<DiagnosticPart> parts = diagnosticDAO.getPartsByDiagnostic(diagnosticId);
            int approvedCnt = (int) parts.stream()
                    .filter(p -> Boolean.TRUE.equals(p.isApproved()))
                    .count();

            if (approve) {
                if (approvedCnt == 0) {
                    return ServiceResult.error(
                            new SystemMessage("VD_APPROVE_EMPTY", MessageType.WARNING, "Diagnostic",
                                    "Cần chọn ít nhất 1 phụ tùng để duyệt.")
                    );
                }

                return diagnosticDAO.approveDiagnosticManual(diagnosticId);

            } else {

                String reason = (rejectReason == null || rejectReason.isBlank())
                        ? "Khách hàng từ chối chẩn đoán."
                        : rejectReason.trim();

                return diagnosticDAO.rejectDiagnosticManual(diagnosticId, reason);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(
                    new SystemMessage("DIAG_FINAL_ERR", MessageType.ERROR, "Diagnostic",
                            "Không thể chốt quyết định.")
            );
        }
    }


    public CustomerDiagnosticsView getDiagnosticsForRequest(int customerId, int requestId) throws SQLException {
        CustomerDiagnosticsView view = diagnosticDAO.getRequestDiagnosticsTree(requestId);
        if (view == null) {
            return null;
        }

        if (view.customerId != customerId) {
            throw new SecurityException("Request không thuộc về khách hàng này");
        }

        return view;
    }
}
