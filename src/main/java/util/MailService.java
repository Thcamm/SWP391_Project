package util;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import model.invoice.Invoice;
import model.payment.Payment;

import java.util.Date;
import java.util.Properties;

public class MailService {

    // Cấu hình SMTP (ví dụ với Gmail)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "gara.tuanduong.auto2929@gmail.com"; // Thay bằng email của bạn
    private static final String SMTP_PASSWORD = "pgwk tplf uexx ubxv"; // Thay bằng app password

    public static boolean sendEmail(String toEmail, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // Tạo session với authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            // Tạo message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(content);

            // Gửi email
            Transport.send(message);

            System.out.println("Email sent successfully to: " + toEmail);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Send HTML email
     */
    public static boolean sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_USERNAME, "Garage Management System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            message.setSentDate(new Date());

            Transport.send(message);

            System.out.println(" HTML email sent to: " + toEmail);
            return true;

        } catch (Exception e) {
            System.err.println(" Failed to send email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Send payment confirmation email
     * Uses EmailTemplates for HTML generation
     */
    public static boolean sendPaymentConfirmationEmail(Invoice invoice, Payment payment, String customerEmail) {

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            System.err.println(" Customer email is empty");
            return false;
        }

        String subject = "Xác nhận thanh toán - Hóa đơn " + invoice.getInvoiceNumber();
        String htmlBody = EmailTemplates.buildPaymentConfirmationEmail(invoice, payment, SMTP_USERNAME);

        return sendHtmlEmail(customerEmail, subject, htmlBody);
    }

    /**
     * Send invoice created notification
     */
    public static boolean sendInvoiceCreatedEmail(Invoice invoice, String customerEmail) {

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            System.err.println(" Customer email is empty");
            return false;
        }

        String subject = "Hóa đơn mới - " + invoice.getInvoiceNumber();
        String htmlBody = EmailTemplates.buildInvoiceCreatedEmail(invoice, SMTP_USERNAME);

        return sendHtmlEmail(customerEmail, subject, htmlBody);
    }

    public static void main(String[] args) {
        // Test 1: Simple email
        // System.out.println("Test 1: Simple Email");
        // sendEmail("conbodoan29102005@gmail.com", "Test Simple", "Test content");

        // Test 2: Payment confirmation
        System.out.println("\nTest 2: Payment Confirmation Email");
        try {
            Invoice mockInvoice = new Invoice();
            mockInvoice.setInvoiceNumber("INV-20251101-TEST");
            mockInvoice.setInvoiceDate(new java.sql.Date(System.currentTimeMillis()));
            mockInvoice.setDueDate(new java.sql.Date(System.currentTimeMillis()));
            mockInvoice.setTotalAmount(new java.math.BigDecimal("1320000"));
            mockInvoice.setPaidAmount(new java.math.BigDecimal("1320000"));
            mockInvoice.setBalanceAmount(java.math.BigDecimal.ZERO);
            mockInvoice.setPaymentStatus("PAID");

            Payment mockPayment = new Payment();
            mockPayment.setAmount(new java.math.BigDecimal("1320000"));
            mockPayment.setMethod("ONLINE");
            mockPayment.setReferenceNo("TEST-" + System.currentTimeMillis());
            mockPayment.setPaymentDate(new java.sql.Timestamp(System.currentTimeMillis()));
            mockPayment.setNote("Test payment - Email template version");

            sendPaymentConfirmationEmail(mockInvoice, mockPayment, "vuthithuy.qc1984@gmail.com");

            System.out.println("\n All tests completed!");

        } catch (Exception e) {
            System.err.println(" Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

