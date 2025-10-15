package controller.support;

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

@WebServlet("/app/create-support-request")
@MultipartConfig
public class CreateSupportRequest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Load danh mục hỗ trợ (nếu có)
        SupportDAO dao = new SupportDAO();
        try {
            request.setAttribute("categories", dao.getAllSupportCategories());
            request.getRequestDispatcher("/support/create_support_request.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Không thể tải danh mục hỗ trợ");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = null; // lấy Customer từ UserID
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
        Integer appointmentId = appointmentIdParam != null && !appointmentIdParam.isEmpty() ? Integer.parseInt(appointmentIdParam) : null;
        Integer categoryId = categoryIdParam != null && !categoryIdParam.isEmpty() ? Integer.parseInt(categoryIdParam) : null;
        Integer workOrderId = workOrderIdParam != null && !workOrderIdParam.isEmpty() ? Integer.parseInt(workOrderIdParam) : null;

        // Upload file (nếu có)
        Part filePart = request.getPart("attachment");
        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            // 3. Tạo tên file duy nhất
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
            request.setAttribute("message", "Gửi yêu cầu hỗ trợ thành công!");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Lỗi khi gửi yêu cầu!");
        }

        request.getRequestDispatcher("/support/create_support_request.jsp").forward(request, response);
    }
}
