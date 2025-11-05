package controller.customer;

import dao.workorder.WorkOrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.workorder.WorkOrder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/customer/workorder-list")
public class WorkOrderList extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WorkOrderDAO workOrderDAO = new WorkOrderDAO();

        try {
            List<WorkOrder> workOrders = workOrderDAO.getAllWorkOrders();
            request.setAttribute("workOrders", workOrders);

            request.getRequestDispatcher("/view/customer/view-workorder-list.jsp").forward(request, response);


        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error");
        }
    }
}