package controller.employee.customerservice;

import model.dto.WorkOrderDetailView;
import service.work.WorkOrderDetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/customerservice/workorder-detail")
public class ViewWorkOrderDetail extends HttpServlet {

    private final WorkOrderDetailService service = new WorkOrderDetailService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Lấy ID từ parameter
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID của Work Order.");
                return;
            }

            int workOrderID = Integer.parseInt(idParam);

            // 2. Gọi Service để lấy DTO
            WorkOrderDetailView view = service.getWorkOrderDetailView(workOrderID);

            // 3. Kiểm tra kết quả
            if (view == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy Work Order.");
                return;
            }

            // 4. Gửi dữ liệu sang JSP
            request.setAttribute("workOrderView", view);
            request.getRequestDispatcher("/view/customerservice/workorder-detail.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi truy vấn cơ sở dữ liệu: " + e.getMessage(), e);
        }
    }
}