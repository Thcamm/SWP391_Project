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

    //Constants
    private static final String ATTR_EMPLOYEE_ID = "employeeID";
    private static final String ATTR_SUCCESS_MSG = "successMessage";
    private static final String ATTR_ERROR_MSG = "errorMessage";

    @Override
    public void init() throws ServletException {
        super.init();
        paymentService = new PaymentService();}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            request.setAttribute(ATTR_ERROR_MSG, "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            HttpSession session = request.getSession();
            session.setAttribute(ATTR_ERROR_MSG, "Lỗi khi xử lý: " + e.getMessage());

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
        String invoiceIDStr = request.getParameter("invoiceID");
        if (invoiceIDStr == null || invoiceIDStr.isEmpty()) {
            request.getRequestDispatcher("/view/accountant/create-payment.jsp").forward(request, response);
            return;
        }
        int invoiceID = Integer.parseInt(invoiceIDStr);
        Invoice invoice = paymentService.getInvoiceById(invoiceID);
        if (invoice == null) {
            throw new Exception("Không tìm thấy hóa đơn #" + invoiceID);
        }
        if ("PAID".equals(invoice.getPaymentStatus())) {
            HttpSession session = request.getSession();
            session.setAttribute(ATTR_ERROR_MSG, "Hóa đơn này đã được thanh toán đầy đủ!");
            response.sendRedirect(request.getContextPath() +
                    "/accountant/invoice?action=view&id=" + invoiceID);
            return;
        }
        request.setAttribute("invoice", invoice);
        request.getRequestDispatcher("/view/accountant/create-payment.jsp").forward(request, response);
    }

    private void createPayment(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        // --- STEP 1: Get and validate parameters
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
            throw new Exception("Vui lòng nhập số tiền thanh toán!");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Số tiền thanh toán không hợp lệ!");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("Số tiền thanh toán phải lớn hơn 0!");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new Exception("Vui lòng chọn phương thức thanh toán!");
        }

        // --- STEP 2: Get EmployeeID
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new Exception("Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại!");
        }

        // Lấy trực tiếp từ session
        Integer employeeID = (Integer) session.getAttribute(ATTR_EMPLOYEE_ID);

        if (employeeID == null) {
            throw new Exception("Không thể xác thực nhân viên. Vui lòng đăng nhập lại!");
        }

        // --- STEP 3: Process Payment
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
        message.append("Thanh toán thành công! ");
        message.append("Mã thanh toán: ").append(payment.getReferenceNo());

        if ("PAID".equals(invoice.getPaymentStatus())) {
            message.append(" - Hóa đơn đã thanh toán đầy đủ!");
        } else if ("PARTIALLY_PAID".equals(invoice.getPaymentStatus())) {
            message.append(" - Còn lại: ")
                    .append(formatCurrency(invoice.getBalanceAmount()));
        }
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
            throw new Exception("Không tìm thấy thanh toán #" + paymentID);
        }
        Invoice invoice = paymentService.getInvoiceById(payment.getInvoiceID());
        request.setAttribute("payment", payment);
        request.setAttribute("invoice", invoice);
        request.getRequestDispatcher("/view/accountant/payment-detail.jsp").forward(request, response);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 VNĐ";
        return String.format("%,d VNĐ", amount.longValue());
    }
}