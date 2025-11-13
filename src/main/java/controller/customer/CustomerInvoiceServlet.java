package controller.customer;

import model.customer.Customer;
import model.dto.InvoiceItemDTO;
import model.invoice.Invoice;
import model.payment.Payment;
import service.payment.PaymentService;
import dao.customer.CustomerDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CustomerInvoiceServlet", urlPatterns = {"/customer/invoice"})
public class CustomerInvoiceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    // PAGE_SIZE is no longer needed if listMyInvoices is removed
    // private static final int PAGE_SIZE = 10;

    private PaymentService paymentService;
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        paymentService = new PaymentService();
        customerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        Integer roleID = (Integer) session.getAttribute("roleID");
        Integer userId = (Integer) session.getAttribute("userId");

        // 1. Check Role
        if (roleID == null || roleID != 7) {
            session.setAttribute("errorMessage", "Access denied. Customer login required.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Check UserID
        if (userId == null) {
            System.err.println("ERROR: userId is null in session!");
            session.setAttribute("errorMessage", "Session expired or invalid. Please login again.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 3. Directly attempt to view invoice detail
        // The 'action' parameter is no longer used.
        try {
            // This method handles getting the 'id' parameter internally
            viewInvoiceDetail(request, response, userId);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    // The 'listMyInvoices' method has been removed as it is no longer called.

    /**
     * View invoice detail - with security check
     */
    private void viewInvoiceDetail(HttpServletRequest request, HttpServletResponse response, int userId)
            throws Exception {

        HttpSession session = request.getSession();
        String idStr = request.getParameter("id");

        // Check if ID is provided
        if (idStr == null || idStr.trim().isEmpty()) {
            session.setAttribute("errorMessage", "Invoice ID is required");
            // CRITICAL FIX: Do not redirect to self. Redirect to a safe page (e.g., Home or Dashboard).
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        int invoiceID;
        try {
            invoiceID = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid Invoice ID format.");
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        // Get customer from session
        Customer customer = (Customer) session.getAttribute("customer");

        // Fallback: if not in session, fetch from DB
        if (customer == null) {
            customer = customerDAO.getCustomerByUserId(userId);

            if (customer == null) {
                session.setAttribute("errorMessage", "Customer profile not found");
                response.sendRedirect(request.getContextPath() + "/Home");
                return;
            }

            session.setAttribute("customer", customer);
        }

        // Get invoice
        Invoice invoice = paymentService.getInvoiceById(invoiceID);

        if (invoice == null) {
            session.setAttribute("errorMessage", "Invoice not found");
            // Redirect to a list or dashboard if the specific invoice is missing
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }
        if (!paymentService.invoiceBelongsToCustomer(invoiceID, customer.getCustomerId())) {
            session.setAttribute("errorMessage", "Access denied. This invoice does not belong to you.");
            // Redirect to a safe page
            response.sendRedirect(request.getContextPath() + "/Home");
            return;
        }

        // Get invoice details
        List<Payment> payments = paymentService.getPaymentsByInvoiceID(invoiceID);
        List<InvoiceItemDTO> items = paymentService.getInvoiceItems(invoiceID);

        // Set attributes
        request.setAttribute("invoice", invoice);
        request.setAttribute("payments", payments);
        request.setAttribute("items", items);
        request.setAttribute("customer", customer);

        // Forward to customer invoice detail page
        request.getRequestDispatcher("/view/customer/invoice-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}