package util;


import jakarta.mail.internet.InternetAddress;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class MailService {

    // Cấu hình SMTP (ví dụ với Gmail)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "conmechung29102005@gmail.com"; // Thay bằng email của bạn
    private static final String SMTP_PASSWORD = "rssn inzi pxti qtuh"; // Thay bằng app password


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

    public static void main(String[] args) {
        sendEmail("conbodoan29102005@gmail.com", "Test Subject", "This is a test email from Java.");
    }
}

