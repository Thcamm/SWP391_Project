package util;

import model.invoice.Invoice;
import model.payment.Payment;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Email Templates - Store HTML templates as strings
 */
public class EmailTemplates {

    /**
     * Payment Confirmation Email Template
     */
    public static String getPaymentConfirmationTemplate() {
        return """
            <!DOCTYPE html>
            <html lang='vi'>
            <head>
                <meta charset='UTF-8'>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; background: #f5f5f5; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #16a34a 0%, #15803d 100%); color: white; padding: 30px 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .header h1 { margin: 0 0 10px 0; font-size: 28px; }
                    .icon { font-size: 48px; margin-bottom: 10px; }
                    .content { padding: 30px 20px; }
                    .info-box { background: #f9fafb; border-left: 4px solid #16a34a; padding: 15px; margin: 15px 0; border-radius: 4px; }
                    .info-row { display: table; width: 100%; padding: 8px 0; border-bottom: 1px solid #e5e7eb; }
                    .info-row:last-child { border-bottom: none; }
                    .label { display: table-cell; font-weight: 600; color: #6b7280; width: 50%; }
                    .value { display: table-cell; color: #111827; text-align: right; width: 50%; }
                    .amount-box { background: linear-gradient(135deg, #16a34a 0%, #15803d 100%); color: white; padding: 20px; text-align: center; border-radius: 8px; margin: 20px 0; }
                    .amount-box .amount { font-size: 36px; font-weight: bold; margin: 10px 0; }
                    .status-badge { display: inline-block; padding: 6px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; text-transform: uppercase; }
                    .status-paid { background: #dcfce7; color: #166534; }
                    .status-partial { background: #fef3c7; color: #92400e; }
                    .footer { background: #f9fafb; padding: 20px; text-align: center; color: #6b7280; font-size: 12px; border-top: 1px solid #e5e7eb; }
                </style>
            </head>
            <body>
                <div class='container'>
                    <div class='header'>
                        <div class='icon'></div>
                        <h1>Thanh toán thành công!</h1>
                        <p>Cảm ơn quý khách đã thanh toán</p>
                    </div>
                    
                    <div class='content'>
                        <p>Kính gửi quý khách,</p>
                        <p>Chúng tôi đã nhận được khoản thanh toán của quý khách. Dưới đây là chi tiết giao dịch:</p>
                        
                        <div class='info-box'>
                            <div class='info-row'>
                                <span class='label'>Số hóa đơn:</span>
                                <span class='value'><strong>{{INVOICE_NUMBER}}</strong></span>
                            </div>
                            <div class='info-row'>
                                <span class='label'>Ngày thanh toán:</span>
                                <span class='value'>{{PAYMENT_DATE}}</span>
                            </div>
                            <div class='info-row'>
                                <span class='label'>Phương thức:</span>
                                <span class='value'>{{PAYMENT_METHOD}}</span>
                            </div>
                            {{REFERENCE_NUMBER}}
                        </div>
                        
                        <div class='amount-box'>
                            <div style='font-size:14px;opacity:0.9;'>Số tiền đã thanh toán</div>
                            <div class='amount'>{{PAYMENT_AMOUNT}}</div>
                        </div>
                        
                        <div class='info-box'>
                            <h3 style='margin-top:0;color:#111827;'>Tổng quan hóa đơn</h3>
                            <div class='info-row'>
                                <span class='label'>Tổng hóa đơn:</span>
                                <span class='value'>{{INVOICE_TOTAL}}</span>
                            </div>
                            <div class='info-row'>
                                <span class='label'>Đã thanh toán:</span>
                                <span class='value'>{{TOTAL_PAID}}</span>
                            </div>
                            <div class='info-row'>
                                <span class='label'>Còn lại:</span>
                                <span class='value'><strong style='font-size:18px;color:#dc2626;'>{{BALANCE}}</strong></span>
                            </div>
                            <div class='info-row'>
                                <span class='label'>Trạng thái:</span>
                                <span class='value'>{{STATUS_BADGE}}</span>
                            </div>
                        </div>
                        
                        {{NOTE}}
                        
                        <p style='margin-top:30px;font-size:14px;color:#6b7280;'>
                            Nếu quý khách có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi.
                        </p>
                    </div>
                    
                    <div class='footer'>
                        <p><strong>Hệ thống quản lý Garage</strong></p>
                        <p>Email: {{GARAGE_EMAIL}}</p>
                        <p>Đây là email tự động, vui lòng không trả lời.</p>
                        <p>© 2025 Garage Management System. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Build payment confirmation email from template
     */
    public static String buildPaymentConfirmationEmail(Invoice invoice, Payment payment, String garageEmail) {
        String template = getPaymentConfirmationTemplate();

        // Replace placeholders
        String html = template
                .replace("{{INVOICE_NUMBER}}", invoice.getInvoiceNumber())
                .replace("{{PAYMENT_DATE}}", formatDateTime(payment.getPaymentDate()))
                .replace("{{PAYMENT_METHOD}}", getMethodDisplayName(payment.getMethod()))
                .replace("{{PAYMENT_AMOUNT}}", formatCurrency(payment.getAmount()))
                .replace("{{INVOICE_TOTAL}}", formatCurrency(invoice.getTotalAmount()))
                .replace("{{TOTAL_PAID}}", formatCurrency(invoice.getPaidAmount()))
                .replace("{{BALANCE}}", formatCurrency(invoice.getBalanceAmount()))
                .replace("{{GARAGE_EMAIL}}", garageEmail);

        // Reference number (conditional)
        if (payment.getReferenceNo() != null && !payment.getReferenceNo().isEmpty()) {
            String refRow = """
                <div class='info-row'>
                    <span class='label'>Mã giao dịch:</span>
                    <span class='value'><code>%s</code></span>
                </div>
                """.formatted(payment.getReferenceNo());
            html = html.replace("{{REFERENCE_NUMBER}}", refRow);
        } else {
            html = html.replace("{{REFERENCE_NUMBER}}", "");
        }

        // Status badge
        String statusClass = "PAID".equals(invoice.getPaymentStatus()) ? "status-paid" : "status-partial";
        String statusText = "PAID".equals(invoice.getPaymentStatus()) ? "Đã thanh toán" : "Thanh toán một phần";
        String statusBadge = "<span class='status-badge " + statusClass + "'>" + statusText + "</span>";
        html = html.replace("{{STATUS_BADGE}}", statusBadge);

        // Note (conditional)
        if (payment.getNote() != null && !payment.getNote().trim().isEmpty()) {
            String noteBox = """
                <div style='background:#fef9c3;border-left:4px solid #eab308;padding:15px;margin:15px 0;border-radius:4px;'>
                    <p style='margin:0;'><strong>Ghi chú:</strong> %s</p>
                </div>
                """.formatted(payment.getNote());
            html = html.replace("{{NOTE}}", noteBox);
        } else {
            html = html.replace("{{NOTE}}", "");
        }

        return html;
    }

    /**
     * Invoice Created Email Template
     */
    public static String buildInvoiceCreatedEmail(Invoice invoice, String garageEmail) {
        return """
            <!DOCTYPE html>
            <html lang='vi'>
            <head><meta charset='UTF-8'>
            <style>
                body{font-family:Arial,sans-serif;background:#f5f5f5;margin:0;padding:0}
                .container{max-width:600px;margin:20px auto;background:white;border-radius:8px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}
                .header{background:linear-gradient(135deg,#3b82f6,#2563eb);color:white;padding:30px;text-align:center;border-radius:8px 8px 0 0}
                .content{padding:30px}
                .info-box{background:#f9fafb;border-left:4px solid #3b82f6;padding:15px;margin:15px 0;border-radius:4px}
                .footer{background:#f9fafb;padding:20px;text-align:center;color:#6b7280;font-size:12px;border-top:1px solid #e5e7eb}
            </style>
            </head>
            <body>
            <div class='container'>
                <div class='header'>
                    <h1>📄 Hóa đơn mới</h1>
                    <p>%s</p>
                </div>
                <div class='content'>
                    <p>Kính gửi quý khách,</p>
                    <p>Hóa đơn mới đã được tạo cho dịch vụ của quý khách.</p>
                    <div class='info-box'>
                        <p><strong>Ngày tạo:</strong> %s</p>
                        <p><strong>Hạn thanh toán:</strong> %s</p>
                        <p><strong>Tổng tiền:</strong> <span style='font-size:20px;color:#dc2626;font-weight:bold'>%s</span></p>
                    </div>
                    <p>Vui lòng thanh toán trước hạn để tránh phí phạt.</p>
                </div>
                <div class='footer'>
                    <p><strong>Garage Management System</strong></p>
                    <p>Email: %s</p>
                    <p>© 2025</p>
                </div>
            </div>
            </body>
            </html>
            """.formatted(
                invoice.getInvoiceNumber(),
                formatDate(invoice.getInvoiceDate()),
                formatDate(invoice.getDueDate()),
                formatCurrency(invoice.getTotalAmount()),
                garageEmail
        );
    }

    // UTILITY METHODS

    private static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount.longValue()) + " ₫";
    }

    private static String formatDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private static String formatDateTime(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date);
    }

    private static String getMethodDisplayName(String method) {
        if (method == null) return "N/A";
        return switch (method.toUpperCase()) {
            case "ONLINE" -> "Chuyển khoản / QR Code";
            case "OFFLINE" -> "Tiền mặt";
            default -> method;
        };
    }
}