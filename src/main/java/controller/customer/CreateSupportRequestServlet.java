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

    // Thư mục lưu file cố định ngoài webapp
    private static final String UPLOAD_DIR = "C:/garage_support_uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_TYPES = {"image/png", "image/jpeg", "application/pdf"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SupportDAO dao = new SupportDAO();
        try {
            request.setAttribute("categories", dao.getAllSupportCategories());
            request.getRequestDispatcher("/view/customer/create-support-request.jsp").forward(request, response);
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
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer;
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

        Integer appointmentId = (appointmentIdParam != null && !appointmentIdParam.isEmpty())
                ? Integer.parseInt(appointmentIdParam) : null;
        Integer categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty())
                ? Integer.parseInt(categoryIdParam) : null;
        Integer workOrderId = (workOrderIdParam != null && !workOrderIdParam.isEmpty())
                ? Integer.parseInt(workOrderIdParam) : null;

        // Xử lý file upload
        Part filePart = request.getPart("attachment");
        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            // Kiểm tra kích thước
            if (filePart.getSize() > MAX_FILE_SIZE) {
                request.setAttribute("message", "File quá lớn, tối đa 5MB!");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/view/customer/create-support-request.jsp").forward(request, response);
                return;
            }

            // Kiểm tra loại file
            boolean allowed = false;
            String contentType = filePart.getContentType();
            for (String type : ALLOWED_TYPES) {
                if (type.equals(contentType)) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                request.setAttribute("message", "Chỉ cho phép file PNG, JPG hoặc PDF!");
                request.setAttribute("messageType", "error");
                request.getRequestDispatcher("/view/customer/create-support-request.jsp").forward(request, response);
                return;
            }

            // Tạo tên file duy nhất
            String originalFileName = filePart.getSubmittedFileName();
            fileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("\\s+", "_");

            // Tạo thư mục nếu chưa tồn tại
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Lưu file
            filePart.write(UPLOAD_DIR + File.separator + fileName);
        }

        // Tạo SupportRequest và lưu DB
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
            request.setAttribute("messageType", "success");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("message", "Error occurred while submitting the support request.");
            request.setAttribute("messageType", "error");
        }

        request.getRequestDispatcher("/view/customer/create-support-request.jsp").forward(request, response);
    }
}
