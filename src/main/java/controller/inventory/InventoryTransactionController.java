package controller.inventory;

import dao.inventory.InventoryTransactionDAO;
import dao.inventory.PartDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.inventory.InventoryTransaction;
import model.inventory.PartDetail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "InventoryTransactionController", urlPatterns = {"/transactions"})
public class InventoryTransactionController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private InventoryTransactionDAO transactionDAO;
    private PartDAO partDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        transactionDAO = new InventoryTransactionDAO();
        partDAO = new PartDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    listTransactions(request, response);
                    break;
                case "filter":
                    filterTransactions(request, response);
                    break;
                default:
                    listTransactions(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException("Database error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private void listTransactions(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        // Pagination parameters
        int itemsPerPage = 10;
        int currentPage = 1;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // Get all transactions
        List<InventoryTransaction> allTransactions = transactionDAO.getAllTransactions();

        // Calculate statistics
        int totalTransactions = allTransactions.size();
        long stockInCount = allTransactions.stream()
                .filter(t -> "IN".equals(t.getTransactionType()))
                .count();
        long stockOutCount = allTransactions.stream()
                .filter(t -> "OUT".equals(t.getTransactionType()))
                .count();

        // Calculate pagination
        int totalPages = (int) Math.ceil((double) totalTransactions / itemsPerPage);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        // Get paginated transactions
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalTransactions);
        List<InventoryTransaction> paginatedTransactions = allTransactions.subList(startIndex, endIndex);

        // Get all parts for reference
        List<PartDetail> partDetails = partDAO.getAllPartDetails();

        // Set attributes
        request.setAttribute("transactions", paginatedTransactions);
        request.setAttribute("partDetails", partDetails);
        request.setAttribute("totalTransactions", totalTransactions);
        request.setAttribute("stockInCount", stockInCount);
        request.setAttribute("stockOutCount", stockOutCount);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("itemsPerPage", itemsPerPage);

        // Forward to JSP
        request.getRequestDispatcher("/view/storekeeper/transaction-list.jsp").forward(request, response);
    }

    private void filterTransactions(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String transactionType = request.getParameter("type");

        // Pagination parameters
        int itemsPerPage = 10;
        int currentPage = 1;

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // Get all transactions
        List<InventoryTransaction> allTransactions = transactionDAO.getAllTransactions();

        // Apply filters
        if (transactionType != null && !transactionType.isEmpty()) {
            allTransactions = allTransactions.stream()
                    .filter(t -> transactionType.equals(t.getTransactionType()))
                    .toList();
        }

        // Calculate statistics
        int totalTransactions = allTransactions.size();
        long stockInCount = allTransactions.stream()
                .filter(t -> "IN".equals(t.getTransactionType()))
                .count();
        long stockOutCount = allTransactions.stream()
                .filter(t -> "OUT".equals(t.getTransactionType()))
                .count();

        // Calculate pagination
        int totalPages = (int) Math.ceil((double) totalTransactions / itemsPerPage);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        // Get paginated transactions
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, totalTransactions);
        List<InventoryTransaction> paginatedTransactions = allTransactions.subList(startIndex, endIndex);

        // Get all parts for reference
        List<PartDetail> partDetails = partDAO.getAllPartDetails();

        // Set attributes
        request.setAttribute("transactions", paginatedTransactions);
        request.setAttribute("partDetails", partDetails);
        request.setAttribute("totalTransactions", totalTransactions);
        request.setAttribute("stockInCount", stockInCount);
        request.setAttribute("stockOutCount", stockOutCount);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("itemsPerPage", itemsPerPage);
        request.setAttribute("filterType", transactionType);

        // Forward to JSP
        request.getRequestDispatcher("/view/storekeeper/transaction-list.jsp").forward(request, response);
    }
}
