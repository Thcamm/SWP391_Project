package controller.employee.technician;

import common.DbContext;
import common.constant.MessageConstants;
import common.utils.MessageHelper;
import dao.vehicle.VehicleDiagnosticDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.inventory.DiagnosticPart;
import model.vehicle.VehicleDiagnostic;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@WebServlet("/technician/diagnostic/view")
public class ViewDiagnosticServlet extends HttpServlet {
    private final VehicleDiagnosticDAO dao = new VehicleDiagnosticDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int diagnosticId;
        try {
            diagnosticId = Integer.parseInt(req.getParameter("diagnosticId"));
        } catch (Exception e) {
            MessageHelper.setErrorMessage(req.getSession(), MessageConstants.ERR003);
            resp.sendRedirect(req.getContextPath()+"/technician/home");
            return;
        }

        try (Connection c = DbContext.getConnection()) {
            VehicleDiagnostic vd = dao.getDiagnosticWithFullInfo(c, diagnosticId);
            if (vd == null) {
                MessageHelper.setErrorMessage(req.getSession(), MessageConstants.ERR002);
                resp.sendRedirect(req.getContextPath()+"/technician/home");
                return;
            }

            Map<Integer, List<DiagnosticPart>> partsMap = dao.getPartsForDiagnostics(Collections.singletonList(diagnosticId));
            List<DiagnosticPart> parts = partsMap.getOrDefault(diagnosticId, Collections.emptyList());

            // TÍNH TỔNG PARTS
            BigDecimal partsSum = BigDecimal.ZERO;
            for (DiagnosticPart dp : parts) {
                BigDecimal price = dp.getUnitPrice() == null ? BigDecimal.ZERO : dp.getUnitPrice();
                BigDecimal line = price.multiply(BigDecimal.valueOf(dp.getQuantityNeeded()));
                partsSum = partsSum.add(line);
            }

            //LABOR = estimateCost - partsSum
            BigDecimal totalEstimate = vd.getEstimateCost() == null ? BigDecimal.ZERO : vd.getEstimateCost();
            BigDecimal laborCost = totalEstimate.subtract(partsSum);

            if (laborCost.compareTo(BigDecimal.ZERO) < 0) {
                laborCost = BigDecimal.ZERO;
            }

            vd.setLaborCostCalculated(laborCost);

            req.setAttribute("diagnostic", vd);
            req.setAttribute("parts", parts);
            req.getRequestDispatcher("/view/technician/view-diagnostic.jsp").forward(req, resp);
        } catch (SQLException e) {
            MessageHelper.setErrorMessage(req.getSession(), MessageConstants.ERR001);
            resp.sendRedirect(req.getContextPath()+"/technician/home");
        }
    }
}