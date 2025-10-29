package controller.user;

import dao.carservice.ServiceRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.customer.Customer;
import model.dto.ServiceHistoryDTO;
import model.user.User;
import service.user.UserProfileService;
import service.user.UserProfileService.ValidationResult;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

// Change the URL pattern here
@WebServlet(name = "ProfileController", urlPatterns = {"/user/profile"})
public class ProfileController extends HttpServlet {
    private UserProfileService userProfileService;

    @Override
    public void init() {
        this.userProfileService = new UserProfileService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        String contextPath = request.getContextPath();

        if (sessionUser == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("edit".equals(action)) {
            showEditProfilePage(request, response, sessionUser.getUserId());
        } else {
            showViewProfilePage(request, response, sessionUser.getUserId());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set encoding để xử lý tiếng Việt (Giống Register.java)
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User sessionUser = (User) session.getAttribute("user");
        String contextPath = request.getContextPath();

        if (sessionUser == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        // Lấy các tham số
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String gender = request.getParameter("gender");
        String birthDateStr = request.getParameter("birthDate");

        // --- BẮT ĐẦU THAY ĐỔI: Lấy thông tin địa chỉ giống Register.java ---
        String province = request.getParameter("province");
        String district = request.getParameter("district");
        String detailAddress = request.getParameter("addressDetail");

        // Kết hợp 3 trường địa chỉ lại
        String address = detailAddress + ", " + district + ", " + province;
        // --- KẾT THÚC THAY ĐỔI ---

        // Tạo đối tượng User để cập nhật
        User updatedUser = new User();
        updatedUser.setUserId(sessionUser.getUserId()); // Rất quan trọng: Phải có ID của user
        updatedUser.setFullName(fullName);
        updatedUser.setEmail(email);
        updatedUser.setPhoneNumber(phoneNumber);
        updatedUser.setAddress(address); // Gán địa chỉ đã được kết hợp
        updatedUser.setGender(gender);

        try {
            if (birthDateStr != null && !birthDateStr.isEmpty()) {
                // Giữ logic parse ngày sinh
                updatedUser.setBirthDate(Date.valueOf(birthDateStr));
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Cân nhắc thêm xử lý lỗi nếu ngày sinh không hợp lệ
        }

        // Gọi service để validate và cập nhật
        ValidationResult result = userProfileService.updateUserProfile(updatedUser);

        if (result.isValid()) {
            session.setAttribute("success", result.getMessage());
            // Cập nhật lại thông tin user trong session
            session.setAttribute("user", userProfileService.getUserProfile(sessionUser.getUserId()));
            response.sendRedirect(contextPath + "/user/profile");
        } else {
            // Nếu thất bại, gửi lại object 'updatedUser' để điền lại form
            // Lưu ý: Form editProfile.jsp của ông bạn phải có logic
            // để xử lý lại ${user.address} (địa chỉ tổng) ra 3 trường
            // province, district, addressDetail (thường là bằng JavaScript)
            request.setAttribute("error", result.getMessage());
            request.setAttribute("user", updatedUser);
            request.getRequestDispatcher("/view/user/editProfile.jsp").forward(request, response);
        }
    }

    private void showViewProfilePage(HttpServletRequest request, HttpServletResponse response, int userId) throws ServletException, IOException {
        User user = userProfileService.getUserProfile(userId);
        request.setAttribute("user", user);
        HttpSession session = request.getSession(false);

        Customer customer = (session != null) ? (Customer) session.getAttribute("customer") : null;

        if (customer != null) {
            try {
                ServiceRequestDAO serviceRequestDAO = new ServiceRequestDAO();
                List<ServiceHistoryDTO> serviceHistory = serviceRequestDAO.getServiceHistoryByCustomerId(customer.getCustomerId());
                request.setAttribute("serviceHistory", serviceHistory);
            } catch (SQLException e) {
                e.printStackTrace();
                request.setAttribute("historyError", "Could not load service history.");
            }
        }
        request.getRequestDispatcher("/view/user/viewProfile.jsp").forward(request, response);
    }


    private void showEditProfilePage(HttpServletRequest request, HttpServletResponse response, int userId) throws ServletException, IOException {
        User user = userProfileService.getUserProfile(userId);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/view/user/editProfile.jsp").forward(request, response);
    }
}