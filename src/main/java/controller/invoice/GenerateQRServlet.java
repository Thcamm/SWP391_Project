package controller.invoice;

import dao.invoice.InvoiceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.invoice.Invoice;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * Servlet responsible for generating VietQR code *image URLs* using an external API.
 * Responds with a JSON object containing the URL of the QR code image.
 */
@WebServlet(name = "GenerateQRServlet", urlPatterns = {"/invoices/generateQR"})
public class GenerateQRServlet extends HttpServlet {

    private InvoiceDAO invoiceDAO;

    // --- BANK ACCOUNT CONFIGURATION (FOR DEMO) ---
    private static final String BANK_BIN = "970422"; // MB Bank BIN
    private static final String ACCOUNT_NO = "0975383173"; // User's provided account number
    private static final String ACCOUNT_NAME = "NGUYEN DUC THIEN CAM"; // User's provided account name
    private static final String VIETQR_API_TEMPLATE = "compact"; // Mẫu QR (compact hoặc qr_only)
    // ---------------------------------------------

    @Override
    public void init() throws ServletException {
        this.invoiceDAO = new InvoiceDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();

        String invoiceIdParam = request.getParameter("invoiceId");
        if (invoiceIdParam == null || invoiceIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", "Missing invoiceId parameter");
            response.getWriter().write(jsonResponse.toString());
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdParam);
            Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);

            if (invoice == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                jsonResponse.put("error", "Invoice not found");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            BigDecimal amount = invoice.getBalanceAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.put("error", "Invoice is already paid or has no balance due");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Nội dung chuyển khoản (Ví dụ: INV-HD001)
            String content = "INV-" + invoice.getInvoiceNumber();

            // === Tạo URL cho API VietQR ===
            // Sử dụng api.vietqr.io/v2/generate
            String apiUrl = String.format(
                    "https://api.vietqr.io/v2/generate?accountNo=%s&accountName=%s&acqId=%s&amount=%s&addInfo=%s&template=%s",
                    URLEncoder.encode(ACCOUNT_NO, StandardCharsets.UTF_8),
                    URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8),
                    URLEncoder.encode(BANK_BIN, StandardCharsets.UTF_8),
                    amount.longValueExact(), // API này yêu cầu số tiền nguyên
                    URLEncoder.encode(content, StandardCharsets.UTF_8),
                    URLEncoder.encode(VIETQR_API_TEMPLATE, StandardCharsets.UTF_8)
            );

            // === Gửi URL ảnh về cho JavaScript ===
            // API VietQR sẽ trả về ảnh trực tiếp từ URL này
            // Mình chỉ cần gửi URL này về cho client là đủ
            jsonResponse.put("qrImageUrl", apiUrl); // Đổi tên key thành qrImageUrl
            response.getWriter().write(jsonResponse.toString());

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonResponse.put("error", "Invalid invoiceId format");
            response.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Failed to generate QR code URL: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
        }
    }
}
