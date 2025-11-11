package controller.employee.accountant;

import model.invoice.Invoice;
import model.payment.Payment;
import service.payment.PaymentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/accountant/payment"})
public class PaymentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private PaymentService paymentService;

    private static final String ATTR_SUCCESS_MSG = "successMessage";
    private static final String ATTR_ERROR_MSG = "errorMessage";

    @Override
    public void init() throws ServletException {
        super.init();
        paymentService = new PaymentService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer roleID = (Integer) session.getAttribute("roleID");

        if (roleID == null || roleID != 5) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action) {
                case "list":
                    listPayments(request, response);
                    break;
                case "create":
                    showCreateForm(request, response);
                    break;
                case "view":
                    viewPayment(request, response);
                    break;
                default:
                    listPayments(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute(ATTR_ERROR_MSG, "Error: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer roleID = (Integer) session.getAttribute("roleID");

        if (roleID == null || roleID != 5) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/accountant/payment");
            return;
        }

        try {
            switch (action) {
                case "create":
                case "record":
                    createPayment(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/accountant/payment");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute(ATTR_ERROR_MSG, "Error processing: " + e.getMessage());

            String invoiceID = request.getParameter("invoiceID");
            if (invoiceID != null && !invoiceID.isEmpty()) {
                response.sendRedirect(request.getContextPath() +
                        "/accountant/invoice?action=view&id=" + invoiceID);
            } else {
                response.sendRedirect(request.getContextPath() + "/accountant/payment");
            }
        }
    }

    private void listPayments(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        List<Payment> payments = paymentService.getAllPayments();
        request.setAttribute("payments", payments);
        request.getRequestDispatcher("/view/accountant/payment-list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        String invoiceIDStr = request.getParameter("invoiceID");

        if (invoiceIDStr == null || invoiceIDStr.isEmpty()) {
            request.getRequestDispatcher("/view/accountant/create-payment.jsp").forward(request, response);
            return;
        }

        int invoiceID = Integer.parseInt(invoiceIDStr);
        Invoice invoice = paymentService.getInvoiceById(invoiceID);

        if (invoice == null) {
            session.setAttribute(ATTR_ERROR_MSG, "Invoice not found: #" + invoiceID);
            response.sendRedirect(request.getContextPath() + "/accountant/invoice");
            return;
        }

        if ("VOID".equals(invoice.getPaymentStatus())) {
            session.setAttribute(ATTR_ERROR_MSG, "Cannot process payment for voided invoice!");
            response.sendRedirect(request.getContextPath() +
                    "/accountant/invoice?action=view&id=" + invoiceID);
            return;
        }

        if ("PAID".equals(invoice.getPaymentStatus())) {
            session.setAttribute(ATTR_ERROR_MSG, "This invoice is already fully paid!");
            response.sendRedirect(request.getContextPath() +
                    "/accountant/invoice?action=view&id=" + invoiceID);
            return;
        }

        request.setAttribute("invoice", invoice);
        request.getRequestDispatcher("/view/accountant/create-payment.jsp").forward(request, response);
    }

    private void createPayment(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();

        String invoiceIDStr = request.getParameter("invoiceID");
        String amountStr = request.getParameter("amount");
        String method = request.getParameter("method");
        String referenceNo = request.getParameter("referenceNo");
        String note = request.getParameter("note");

        if (invoiceIDStr == null || invoiceIDStr.trim().isEmpty()) {
            throw new Exception("Invoice ID is required");
        }
        int invoiceID = Integer.parseInt(invoiceIDStr);

        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new Exception("Payment amount is required!");
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Invalid payment amount!");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Payment amount must be greater than zero!");
        }

        if (method == null || method.trim().isEmpty()) {
            throw new Exception("Please select a payment method!");
        }

        Integer employeeID = (Integer) session.getAttribute("employeeID");

        if (employeeID == null) {
            throw new Exception("Employee ID not found in session. Please login again!");
        }

        Payment payment = paymentService.processPayment(
                invoiceID,
                amount,
                method,
                referenceNo,
                employeeID,
                note
        );

        Invoice invoice = paymentService.getInvoiceById(invoiceID);

        StringBuilder message = new StringBuilder();
        message.append("Payment successful! ");
        message.append("Reference: ").append(payment.getReferenceNo());

        if ("PAID".equals(invoice.getPaymentStatus())) {
            message.append(" - Invoice fully paid!");
        } else if ("PARTIALLY_PAID".equals(invoice.getPaymentStatus())) {
            message.append(" - Remaining: ").append(formatCurrency(invoice.getBalanceAmount()));
        }

        String userName = (String) session.getAttribute("userName");
        System.out.println(String.format(
                "[PAYMENT] User: %s (Employee #%d) processed payment #%d for Invoice #%d - Amount: %s",
                userName, employeeID, payment.getPaymentID(), invoiceID, formatCurrency(amount)
        ));

        session.setAttribute(ATTR_SUCCESS_MSG, message.toString());

        response.sendRedirect(request.getContextPath() +
                "/accountant/invoice?action=view&id=" + invoiceID);
    }

    private void viewPayment(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            throw new Exception("Payment ID is required");
        }

        int paymentID = Integer.parseInt(idStr);
        Payment payment = paymentService.getPaymentById(paymentID);

        if (payment == null) {
            throw new Exception("Payment not found: #" + paymentID);
        }

        Invoice invoice = paymentService.getInvoiceById(payment.getInvoiceID());

        request.setAttribute("payment", payment);
        request.setAttribute("invoice", invoice);

        request.getRequestDispatcher("/view/accountant/payment-detail.jsp").forward(request, response);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VND";
        return String.format("%,d VND", amount.longValue());
    }
}