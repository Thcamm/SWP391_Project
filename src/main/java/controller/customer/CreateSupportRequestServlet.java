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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/customer/create-support-request")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,      // 5MB per file
        maxRequestSize = 20 * 1024 * 1024   // 20MB total
)
public class CreateSupportRequestServlet extends HttpServlet {

    // Thư mục lưu file cố định ngoài webapp
    private static final String UPLOAD_DIR = "C:/garage_support_uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_FILES = 4; // Maximum 4 files
    private static final String[] ALLOWED_TYPES = {"image/png", "image/jpeg", "application/pdf"};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        // Handle flash messages
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            request.setAttribute("messageType", session.getAttribute("messageType"));
            session.removeAttribute("message");
            session.removeAttribute("messageType");
        }

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

        // Check user authentication
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get customer information
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer;
        try {
            customer = customerDAO.getCustomerByUserId(user.getUserId());
            if (customer == null) {
                session.setAttribute("message", "Customer information not found!");
                session.setAttribute("messageType", "error");
                response.sendRedirect(request.getContextPath() + "/customer/create-support-request");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("message", "Error retrieving customer information.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customer/create-support-request");
            return;
        }

        // Get form parameters
        int customerId = customer.getCustomerId();
        String description = request.getParameter("description");
        String categoryIdParam = request.getParameter("categoryId");
        String workOrderIdParam = request.getParameter("workOrderId");
        String appointmentIdParam = request.getParameter("appointmentId");

        // Parse optional IDs
        Integer appointmentId = (appointmentIdParam != null && !appointmentIdParam.isEmpty())
                ? Integer.parseInt(appointmentIdParam) : null;
        Integer categoryId = (categoryIdParam != null && !categoryIdParam.isEmpty())
                ? Integer.parseInt(categoryIdParam) : null;
        Integer workOrderId = (workOrderIdParam != null && !workOrderIdParam.isEmpty())
                ? Integer.parseInt(workOrderIdParam) : null;

        // Validate required fields
        if (description == null || description.trim().isEmpty() || categoryId == null) {
            session.setAttribute("message", "Please fill in all required fields!");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customer/create-support-request");
            return;
        }

        // Process multiple file uploads
        Collection<Part> fileParts = request.getParts();
        List<String> uploadedFileNames = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Create upload directory if not exists
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Process each file
        int fileCount = 0;
        for (Part filePart : fileParts) {
            // Skip non-file parts
            if (!filePart.getName().equals("attachments") || filePart.getSize() == 0) {
                continue;
            }

            fileCount++;

            // Check maximum number of files
            if (fileCount > MAX_FILES) {
                errors.add("Maximum " + MAX_FILES + " files allowed!");
                break;
            }

            // Check file size
            if (filePart.getSize() > MAX_FILE_SIZE) {
                errors.add("File too large (max 5MB): " + filePart.getSubmittedFileName());
                continue;
            }

            // Check file type
            String contentType = filePart.getContentType();
            boolean typeAllowed = false;
            for (String allowedType : ALLOWED_TYPES) {
                if (allowedType.equals(contentType)) {
                    typeAllowed = true;
                    break;
                }
            }

            if (!typeAllowed) {
                errors.add("Invalid file type: " + filePart.getSubmittedFileName() + " (only PNG, JPG, PDF allowed)");
                continue;
            }

            // Generate unique filename
            String originalFileName = filePart.getSubmittedFileName();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String uniqueFileName = System.currentTimeMillis() + "_" + sanitizedFileName;

            try {
                // Save file
                String filePath = UPLOAD_DIR + File.separator + uniqueFileName;
                filePart.write(filePath);
                uploadedFileNames.add(uniqueFileName);
            } catch (IOException e) {
                errors.add("Failed to save file: " + originalFileName);
                e.printStackTrace();
            }
        }

        // If there were errors during upload
        if (!errors.isEmpty()) {
            session.setAttribute("message", "Upload errors: " + String.join("; ", errors));
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customer/create-support-request");
            return;
        }

        // Combine uploaded filenames (separated by semicolon for multiple files)
        String attachmentPaths = uploadedFileNames.isEmpty() ? null : String.join(";", uploadedFileNames);

        // Create SupportRequest object
        SupportRequest sr = new SupportRequest();
        sr.setCustomerId(customerId);
        sr.setWorkOrderId(workOrderId);
        sr.setAppointmentId(appointmentId);
        sr.setCategoryId(categoryId);
        sr.setDescription(description.trim());
        sr.setAttachmentPath(attachmentPaths);

        // Save to database
        SupportDAO dao = new SupportDAO();
        try {
            dao.insertSupportRequest(sr);
            session.setAttribute("message", "Support request submitted successfully!");
            session.setAttribute("messageType", "success");
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("message", "Error occurred while submitting the support request.");
            session.setAttribute("messageType", "error");

            // Clean up uploaded files if database insert fails
            for (String fileName : uploadedFileNames) {
                File file = new File(UPLOAD_DIR + File.separator + fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/customer/create-support-request");
    }
}