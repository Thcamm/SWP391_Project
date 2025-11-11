package controller.employee.customerservice;

import dao.support.SupportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.MailService;

import java.io.IOException;

@WebServlet("/customerservice/reply-request")
public class ReplyRequest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestId = request.getParameter("id");
        String toEmail = request.getParameter("email");

        request.setAttribute("requestId", requestId);
        request.setAttribute("toEmail", toEmail);
        HttpSession session = request.getSession();
        // 1. Lấy URL của trang trước đó (người dùng đến từ đâu)
        String referer = request.getHeader("Referer");

        // 2. Lấy URL của trang HIỆN TẠI (trang chi tiết)
        String currentPageUrl = request.getRequestURL().toString();
        if (request.getQueryString() != null) {
            currentPageUrl += "?" + request.getQueryString();
        }

        // 3. Đặt URL dự phòng (nếu không tìm thấy trang trước)
        String fallbackUrl = request.getContextPath() + "/customerservice/view-support-request";
        String backUrl = fallbackUrl; // Mặc định là trang danh sách

        // 4. KIỂM TRA LOGIC
        // Kiểm tra xem 'referer' có tồn tại, có phải từ web của bạn, VÀ KHÔNG PHẢI là trang hiện tại
        if (referer != null && !referer.isEmpty()
                && referer.contains(request.getServerName())
                && !referer.equals(currentPageUrl)) {

            // Nếu 'referer' hợp lệ (ví dụ: trang list?page=2) -> dùng nó
            backUrl = referer;
            // Lưu URL "tốt" này vào session phòng khi người dùng update status
            session.setAttribute("backUrl", backUrl);

        } else {
            // Nếu 'referer' KHÔNG hợp lệ (ví dụ: người dùng F5, hoặc vừa update status)
            // -> Thử tìm URL "tốt" mà chúng ta đã lưu trong session
            String sessionBackUrl = (String) session.getAttribute("backUrl");
            if (sessionBackUrl != null && !sessionBackUrl.isEmpty()) {
                backUrl = sessionBackUrl;
            }
            // Nếu không có gì trong session, 'backUrl' vẫn là 'fallbackUrl' (đã gán ở bước 3)
        }

        // 5. Gửi URL cuối cùng sang cho JSP
        request.setAttribute("backUrlForJsp", backUrl);
        request.getRequestDispatcher("/view/customerservice/reply-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Set encoding và lấy session
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        // 2. Lấy tham số
        String requestIdStr = request.getParameter("requestId");
        String toEmail = request.getParameter("toEmail");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        int requestId;

        // 3. Kiểm tra Request ID *trước tiên*
        try {
            requestId = Integer.parseInt(requestIdStr);
        } catch (NumberFormatException | NullPointerException e) {
            session.setAttribute("message", "Invalid Request ID. Failed to send email.");
            session.setAttribute("messageType", "error");
            response.sendRedirect(request.getContextPath() + "/customerservice/view-support-request");
            return;
        }

        // 4. Lấy URL để quay lại (từ session mà doGet đã lưu)
        String redirectUrl = (String) session.getAttribute("backUrl");
        if (redirectUrl == null || redirectUrl.isEmpty()) {
            // Tạo URL dự phòng nếu session không có
            redirectUrl = request.getContextPath() + "/customerservice/view-support-request";
        }

        // 5. Gửi email
        boolean sent = MailService.sendEmail(toEmail, subject, message);

        // 6. Đặt thông báo vào SESSION và CẬP NHẬT STATUS
        if (sent) {
            try {
                // === KÍCH HOẠT LẠI LOGIC CỦA BẠN ===
                SupportDAO dao = new SupportDAO();
                dao.updateSupportRequestStatus(requestId, "RESOLVED");
                // ===================================

                session.setAttribute("message", "Email sent and request marked as RESOLVED.");
                session.setAttribute("messageType", "success");

            } catch (Exception e) {
                // Xử lý nếu gửi mail thành công NHƯNG update status thất bại
                e.printStackTrace();
                session.setAttribute("message", "Email sent, but failed to update status: " + e.getMessage());
                session.setAttribute("messageType", "warning"); // Dùng 'warning' vì email đã đi
            }

        } else {
            session.setAttribute("message", "Failed to send email. Please check server logs.");
            session.setAttribute("messageType", "error");
        }

        // 7. Dùng REDIRECT (PRG)
        response.sendRedirect(redirectUrl);
    }
}