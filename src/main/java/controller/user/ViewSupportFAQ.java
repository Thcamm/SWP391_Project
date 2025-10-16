package controller.user;

import dao.support.SupportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.support.SupportFAQ;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/support-faq")
public class ViewSupportFAQ extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("q");
        String idParam = request.getParameter("id");

        SupportDAO dao = new SupportDAO();

        try {
            if (idParam != null) {
                int id = Integer.parseInt(idParam);
                SupportFAQ faq = dao.getFAQById(id);
                request.setAttribute("faqDetail", faq);
                request.getRequestDispatcher("/view/common/supportFAQ_detail.jsp").forward(request, response);
            } else {
                List<SupportFAQ> faqs;
                if (keyword != null && !keyword.trim().isEmpty()) {
                    faqs = dao.searchFAQs(keyword);
                } else {
                    faqs = dao.getAllFAQs();
                }
                request.setAttribute("faqs", faqs);
                request.getRequestDispatcher("/view/common/supportFAQ.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(500, "Error retrieving FAQ data from the database.");
        }
    }
}
