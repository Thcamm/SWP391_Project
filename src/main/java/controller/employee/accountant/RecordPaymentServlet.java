package controller.employee.accountant;

import dao.invoice.InvoiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.invoice.Invoice;
import model.payment.Payment;
import model.user.User;
import service.payment.PaymentService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Servlet to handle recording payments for invoices.
 */
@WebServlet(name = "RecordPaymentServlet", urlPatterns = {"/accountant/recordPayment"})
public class RecordPaymentServlet extends HttpServlet {

    private PaymentService paymentService;
    private InvoiceDAO invoiceDAO; // Needed for doGet

    @Override
    public void init() throws ServletException {
        this.paymentService = new PaymentService();
        this.invoiceDAO = new InvoiceDAO(); // Instantiate DAO
    }

    /**
     * Handles GET request: Displays the form to record a payment for a specific invoice.
     * Expects an 'invoiceId' parameter.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        String roleCode = (session != null) ? (String) session.getAttribute("roleCode") : null;

        // Security check
        if (currentUser == null || !"ACCOUNTANT".equals(roleCode)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String invoiceIdParam = request.getParameter("invoiceId");
        if (invoiceIdParam == null || invoiceIdParam.isEmpty()) {
            request.setAttribute("error", "Invoice ID is required to record a payment.");
            // Redirect to a more appropriate page, maybe invoice list?
            response.sendRedirect(request.getContextPath() + "/accountant/invoices"); // Assuming you have this
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdParam);
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);

            if (invoice == null) {
                request.setAttribute("error", "Invoice not found.");
                response.sendRedirect(request.getContextPath() + "/accountant/invoices");
                return;
            }
            // Check if already paid or void
            if ("PAID".equals(invoice.getPaymentStatus()) || "VOID".equals(invoice.getPaymentStatus())) {
                session.setAttribute("warningMessage", "Invoice #" + invoiceId + " is already " + invoice.getPaymentStatus() + ". No further payment needed.");
                response.sendRedirect(request.getContextPath() + "/accountant/invoices/details?invoiceId=" + invoiceId); // Redirect to details
                return;
            }


            // Pass the invoice object to the JSP to display details
            request.setAttribute("invoice", invoice);
            request.getRequestDispatcher("/view/accountant/recordPayment.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid Invoice ID format.");
            response.sendRedirect(request.getContextPath() + "/accountant/invoices");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error loading invoice details.");
            response.sendRedirect(request.getContextPath() + "/accountant/invoices");
        }
    }

    /**
     * Handles POST request: Processes the submitted payment information.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("user") : null;
        String roleCode = (session != null) ? (String) session.getAttribute("roleCode") : null;

        // Security check
        if (currentUser == null || !"ACCOUNTANT".equals(roleCode)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 1. Get parameters from the form
        String invoiceIdParam = request.getParameter("invoiceId");
        String workOrderIdParam = request.getParameter("workOrderId"); // From hidden field
        String amountParam = request.getParameter("amount");
        String method = request.getParameter("method");
        String paymentDateStr = request.getParameter("paymentDate"); // Expecting yyyy-MM-ddTHH:mm format
        String referenceNo = request.getParameter("referenceNo");
        String note = request.getParameter("note");

        String redirectUrl = request.getContextPath() + "/accountant/recordPayment?invoiceId=" + invoiceIdParam; // Default redirect back to form on error

        try {
            // 2. Parse and Validate Basic Input
            int invoiceId = Integer.parseInt(invoiceIdParam);
            int workOrderId = Integer.parseInt(workOrderIdParam);
            BigDecimal amount = new BigDecimal(amountParam);
            Timestamp paymentDate;

            try {
                // Parse LocalDateTime and convert to Timestamp for DB
                LocalDateTime ldt = LocalDateTime.parse(paymentDateStr); // Handles yyyy-MM-ddTHH:mm
                paymentDate = Timestamp.valueOf(ldt);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use the date picker.");
            }


            // 3. Create Payment Object
            Payment payment = new Payment();
            payment.setInvoiceID(invoiceId);
            payment.setWorkOrderID(workOrderId);
            payment.setAmount(amount);
            payment.setMethod(method);
            payment.setPaymentDate(paymentDate);
            payment.setReferenceNo(referenceNo);
            payment.setNote(note);
            payment.setAccountantID(currentUser.getUserId()); // Get ID of logged-in accountant

            // 4. Call the Service Layer to process payment
            boolean success = paymentService.processPayment(payment);

            // 5. Set feedback message and redirect
            if (success) {
                session.setAttribute("successMessage", "Payment recorded successfully for Invoice #" + invoiceId);
                redirectUrl = request.getContextPath() + "/accountant/invoices/details?invoiceId=" + invoiceId; // Redirect to invoice details on success
            } else {
                // If service returns false without exception (less likely with current setup)
                session.setAttribute("errorMessage", "Payment processing failed unexpectedly.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Invalid number format for ID or Amount.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) { // Catch validation errors from Service
            session.setAttribute("errorMessage", "Validation Error: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) { // Catch database errors from Service/DAO
            session.setAttribute("errorMessage", "Database Error: Failed to process payment. " + e.getMessage());
            e.printStackTrace();
        }

        // Redirect based on outcome
        response.sendRedirect(redirectUrl);
    }
}
