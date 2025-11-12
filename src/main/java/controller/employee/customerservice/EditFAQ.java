package controller.employee.customerservice;


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

@WebServlet(urlPatterns = {"/customerservice/faq"})
public class EditFAQ extends HttpServlet {

    private SupportDAO faqDAO;

    @Override
    public void init() throws ServletException {
        faqDAO = new SupportDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if (action == null) {
                action = "list";
            }

            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteFAQ(request, response);
                    break;
                case "search":
                    searchFAQs(request, response);
                    break;
                default:
                    listFAQs(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                addFAQ(request, response);
            } else if ("update".equals(action)) {
                updateFAQ(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private void listFAQs(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        // Get pagination parameters
        int page = 1;
        int recordsPerPage = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Get total records
        int totalRecords = faqDAO.getTotalFAQs();
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        // Get paginated list
        List<SupportFAQ> faqList = faqDAO.getFAQsPaginated(page, recordsPerPage);

        request.setAttribute("faqList", faqList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/view/customerservice/faq-list.jsp").forward(request, response);
    }

    private void searchFAQs(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");

        // Get pagination parameters
        int page = 1;
        int recordsPerPage = 10;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // Get total records for search
        int totalRecords = faqDAO.getTotalSearchResults(keyword);
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

        // Get paginated search results
        List<SupportFAQ> faqList = faqDAO.searchFAQsPaginated(keyword, page, recordsPerPage);

        request.setAttribute("faqList", faqList);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/view/customerservice/faq-list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/customerservice/faq-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        SupportFAQ faq = faqDAO.getFAQById(id);

        if (faq != null) {
            request.setAttribute("faq", faq);
            request.getRequestDispatcher("/view/customerservice/faq-form.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/customerservice/faq?error=notfound");
        }
    }

    private void addFAQ(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String question = request.getParameter("question");
        String answer = request.getParameter("answer");

        if (question == null || question.trim().isEmpty() ||
                answer == null || answer.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() +
                    "/customerservice/faq?action=add&error=empty");
            return;
        }

        SupportFAQ faq = new SupportFAQ();
        faq.setQuestion(question.trim());
        faq.setAnswer(answer.trim());
        faq.setActive(true);

        faqDAO.addFAQ(faq);
        response.sendRedirect(request.getContextPath() +
                "/customerservice/faq?success=added");
    }

    private void updateFAQ(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String question = request.getParameter("question");
        String answer = request.getParameter("answer");
        String isActiveParam = request.getParameter("isActive");

        if (question == null || question.trim().isEmpty() ||
                answer == null || answer.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() +
                    "/customerservice/faq?action=edit&id=" + id + "&error=empty");
            return;
        }

        SupportFAQ faq = new SupportFAQ();
        faq.setFAQId(id);
        faq.setQuestion(question.trim());
        faq.setAnswer(answer.trim());
        faq.setActive("1".equals(isActiveParam) || "true".equals(isActiveParam));

        faqDAO.updateFAQ(faq);
        response.sendRedirect(request.getContextPath() +
                "/customerservice/faq?success=updated");
    }

    private void deleteFAQ(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        faqDAO.deleteFAQ(id);
        response.sendRedirect(request.getContextPath() +
                "/customerservice/faq?success=deleted");
    }
}