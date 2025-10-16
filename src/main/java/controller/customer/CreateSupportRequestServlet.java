package controller.customer;

import dao.customer.CustomerDAO;
import dao.support.SupportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.customer.Customer;
import model.support.SupportRequest;
import model.user.User;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/customer/create-support-request")
@MultipartConfig
public class CreateSupportRequestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SupportDAO dao = new SupportDAO();
        try {
            request.setAttribute("categories", dao.getAllSupportCategories());
            request.getRequestDispatcher("/view/customer/create_support_request.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Unable to load support categories.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = null;

        try {
            customer = customerDAO.getCustomerByUserId(user.getUserId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int customerId = customer.getCustomerId();

        String description = request.getParameter("description");
        String categoryIdParam = request.getParameter("categoryId");
        String workOrderIdParam = request.getParameter("workOrderId");
        String appointmentIdParam = request.getParameter("appointmentId");

        Integer appointmentId = appointmentIdParam != null && !appointmentIdParam.isEmpty()
                ? Integer.parseInt(appointmentIdParam)
                : null;
        Integer categoryId = categoryIdParam != null && !categoryIdParam.isEmpty()
                ? Integer.parseInt(categoryIdParam)
                : null;
        Integer workOrderId = workOrderIdParam != null && !workOrderIdParam.isEmpty()
                ? Integer.parseInt(workOrderIdParam)
                : null;

        Part filePart = request.getPart("attachment");
        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            fileName = System.currentTimeMillis() + "_" + filePart.getSubmittedFileName();

            String uploadPath = getServletContext().getRealPath("/support/upload");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            filePart.write(uploadPath + File.separator + fileName);
        }

        SupportRequest sr = new SupportRequest();
        sr.setCustomerId(customerId);
        sr.setWorkOrderId(workOrderId);
        sr.setAppointmentId(appointmentId);
        sr.setCategoryId(categoryId);
        sr.setDescription(description);
        sr.setAttachmentPath(fileName);

        SupportDAO dao = new SupportDAO();
        try {
            dao.insertSupportRequest(sr);
            request.setAttribute("message", "Support request submitted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Error occurred while submitting the support request.");
        }

        request.getRequestDispatcher("/view/customer/create_support_request.jsp").forward(request, response);
    }
}
