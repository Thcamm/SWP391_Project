package service.diagnostic;

import common.DbContext;
import common.constant.MessageConstants;
import common.message.ServiceResult;
import dao.vehicle.VehicleDiagnosticDAO;
import model.vehicle.VehicleDiagnostic;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CustomerDiagnosticService {
    private final VehicleDiagnosticDAO diagnosticDAO = new VehicleDiagnosticDAO();

    public ServiceResult getDiagnosticFullInfo(int diagnosticId){
        try (Connection conn = DbContext.getConnection()) {
            VehicleDiagnostic vd = diagnosticDAO.getDiagnosticWithFullInfo(conn, diagnosticId);
            if (vd == null) {
                return ServiceResult.error(MessageConstants.ERR002); // not found
            }

            var map = diagnosticDAO.getPartsForDiagnostics(List.of(diagnosticId));
            vd.setParts(map.getOrDefault(diagnosticId, List.of()));
            return ServiceResult.success(MessageConstants.DIAG003);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.DIAG006);
        }
    }

    public ServiceResult updatePartApproval(int diagnosticPartId, boolean approved) {
        try (Connection conn = DbContext.getConnection()) {
            int updated = diagnosticDAO.updatePartApproval(conn, diagnosticPartId, approved);
            if (updated > 0) {
                return ServiceResult.success(MessageConstants.PART004);
            }
            return ServiceResult.error(MessageConstants.ERR002);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.PART005);
        }


    }

    public ServiceResult finalizeDiagnosticDecision(int diagnosticId, boolean approve, String rejectReason) {
        try (Connection conn = DbContext.getConnection()) {
            if (approve) {
                diagnosticDAO.updateStatus(conn, diagnosticId, "APPROVED", null);
            } else {
                diagnosticDAO.updateStatus(conn, diagnosticId, "REJECTED", rejectReason);
            }
            conn.commit();
            return ServiceResult.success(MessageConstants.DIAG005);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(MessageConstants.ERR004);
        }
    }
}