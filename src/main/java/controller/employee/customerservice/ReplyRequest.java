package controller.employee.customerservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        request.getRequestDispatcher("/view/customerservice/reply-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String requestId = request.getParameter("requestId");
        String toEmail = request.getParameter("toEmail");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        boolean sent = MailService.sendEmail(toEmail, subject, message);

        if (sent) {
            request.setAttribute("messageSent", "Email sent successfully to " + toEmail);
        } else {
            request.setAttribute("error", " Failed to send email. Please check server logs.");
        }

        request.setAttribute("requestId", requestId);
        request.setAttribute("toEmail", toEmail);
        request.getRequestDispatcher("/view/customerservice/reply-request.jsp").forward(request, response);
    }
}