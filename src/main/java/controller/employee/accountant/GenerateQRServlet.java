package controller.employee.accountant;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.invoice.Invoice;
import service.payment.PaymentService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

@WebServlet(name = "GenerateQRServlet", urlPatterns = {"/accountant/generateQR"})
public class GenerateQRServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PaymentService paymentService;
    private Gson gson;

    private static final String BANK_ID = "970422"; // MB Bank
    private static final String ACCOUNT_NUMBER = "0975383173";
    private static final String ACCOUNT_NAME = "NGUYEN DUC THIEN CAM";
    private static final String BANK_NAME = "MB BANK";
    private static final String QR_TEMPLATE = "compact2";

    @Override
    public void init() throws ServletException {
        super.init();
        paymentService = new PaymentService();
        gson = new Gson();

        // Log configuration on startup
        log("GenerateQRServlet initialized");
        log("Bank: " + BANK_NAME + " (" + BANK_ID + ")");
        log("Account: " + maskAccountNumber(ACCOUNT_NUMBER));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JsonObject jsonResponse = new JsonObject();

        try {
            String invoiceIdStr = request.getParameter("invoiceId");

            if (invoiceIdStr == null || invoiceIdStr.trim().isEmpty()) {
                sendErrorResponse(out, "Invoice ID is required", jsonResponse);
                return;
            }

            int invoiceId;
            try {
                invoiceId = Integer.parseInt(invoiceIdStr);
            } catch (NumberFormatException e) {
                sendErrorResponse(out, "Invalid invoice ID format. Must be a number.", jsonResponse);
                return;
            }

            log("Generating QR code for Invoice ID: " + invoiceId);

            Invoice invoice = paymentService.getInvoiceById(invoiceId);

            if (invoice == null) {
                sendErrorResponse(out, "Invoice not found with ID: " + invoiceId, jsonResponse);
                return;
            }

            // Check if already fully paid
            if ("PAID".equals(invoice.getPaymentStatus())) {
                sendErrorResponse(out, "This invoice has already been paid in full.", jsonResponse);
                return;
            }

            // Check if voided
            if ("VOID".equals(invoice.getPaymentStatus())) {
                sendErrorResponse(out, "This invoice has been voided and cannot be paid.", jsonResponse);
                return;
            }

            // Check if balance is zero or negative
            if (invoice.getBalanceAmount() == null ||
                    invoice.getBalanceAmount().longValue() <= 0) {
                sendErrorResponse(out, "No balance due for this invoice.", jsonResponse);
                return;
            }

            String qrImageUrl = generateVietQRUrl(invoice);

            log("QR Code generated successfully: " + qrImageUrl);

            // Build Success Response

            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("qrImageUrl", qrImageUrl);
            jsonResponse.addProperty("amount", invoice.getBalanceAmount().longValue());
            jsonResponse.addProperty("amountFormatted", formatCurrency(invoice.getBalanceAmount().longValue()));
            jsonResponse.addProperty("invoiceNumber", invoice.getInvoiceNumber());
            jsonResponse.addProperty("bankName", BANK_NAME);
            jsonResponse.addProperty("accountNumber", maskAccountNumber(ACCOUNT_NUMBER));
            jsonResponse.addProperty("accountName", ACCOUNT_NAME);
            jsonResponse.addProperty("message", "QR code generated successfully");

            out.print(gson.toJson(jsonResponse));

        } catch (Exception e) {
            log("Error generating QR code: " + e.getMessage(), e);
            e.printStackTrace();
            sendErrorResponse(out, "Server error: " + e.getMessage(), jsonResponse);
        } finally {
            out.flush();
        }
    }

    /**
     * Generate VietQR URL for the given invoice
     */
    private String generateVietQRUrl(Invoice invoice) throws Exception {

        long amount = invoice.getBalanceAmount().longValue();

        String description = invoice.getInvoiceNumber();

        String encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8);
        String encodedAccountName = URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8);

        // Build VietQR URL
        // Format: https://img.vietqr.io/image/{BANK_ID}-{ACCOUNT_NO}-{TEMPLATE}.jpg?amount={AMOUNT}&addInfo={DESCRIPTION}&accountName={ACCOUNT_NAME}
        StringBuilder qrUrl = new StringBuilder();
        qrUrl.append("https://img.vietqr.io/image/");
        qrUrl.append(BANK_ID);
        qrUrl.append("-");
        qrUrl.append(ACCOUNT_NUMBER);
        qrUrl.append("-");
        qrUrl.append(QR_TEMPLATE);
        qrUrl.append(".jpg");
        qrUrl.append("?amount=").append(amount);
        qrUrl.append("&addInfo=").append(encodedDescription);
        qrUrl.append("&accountName=").append(encodedAccountName);

        return qrUrl.toString();
    }

    /**
     * Send error response as JSON
     */
    private void sendErrorResponse(PrintWriter out, String message, JsonObject jsonResponse) {
        jsonResponse.addProperty("success", false);
        jsonResponse.addProperty("message", message);
        out.print(gson.toJson(jsonResponse));
        log("Error response sent: " + message);
    }

    /**
     * Format currency to Vietnamese format (1,234,567 VND)
     */
    private String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }

        int length = accountNumber.length();
        String masked = "*".repeat(length - 4);
        String lastFour = accountNumber.substring(length - 4);

        return masked + lastFour;
    }

    @Override
    public String getServletInfo() {
        return "Generate VietQR Code Servlet for Invoice Payment";
    }
}