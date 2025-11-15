package service.appointment;

import dao.appointment.AppointmentDAO;
import model.appointment.Appointment;
import util.MailService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AppointmentService {
    private AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public boolean updateAppointment(Appointment appointment) throws Exception {
        Appointment current = appointmentDAO.getAppointmentById(appointment.getAppointmentID());
        if (current == null) {
            throw new Exception("Appointment not found.");
        }

        // Giả sử chỉ cho phép reschedule 1 lần
        if (current.getRescheduleCount() >= 1) {
            throw new Exception("This appointment has already been rescheduled once and cannot be changed again.");
        }

        return appointmentDAO.updateAppointment(appointment);
    }


//    public void getAllAppointments() {
//        appointmentDAO.getAllAppointments();
//    }

    public void getAppointmentById(int appointmentID) {
        appointmentDAO.getAppointmentById(appointmentID);
    }

    public void insertAppointment(Appointment appointment) {
        appointmentDAO.insertAppointment(appointment);
    }
    public boolean checkUpdateStatus(int appointmentID, String newStatus) throws Exception {
        // Lấy thông tin hiện tại của appointment
        Appointment apm = appointmentDAO.getAppointmentById(appointmentID);
        if (apm == null) {
            throw new Exception("Không tìm thấy appointment ID: " + appointmentID);
        }

        String currentStatus = apm.getStatus();

        // 1️⃣ Nếu đã hoàn tất hoặc đã huỷ thì không được đổi
        if (currentStatus.equals("ACCEPTED") || currentStatus.equals("CANCELLED")) {
            throw new Exception("Không thể thay đổi trạng thái của cuộc hẹn đã " + currentStatus.toLowerCase());
        }

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new Exception("Không thể chuyển từ " + currentStatus + " sang " + newStatus);
        }

        return true;
    }
    private boolean isValidTransition(String current, String next) {
        switch (current) {
            case "PENDING":
                return next.equals("ACCEPTED") || next.equals("REJECTED")  || next.equals("CANCELLED");
            default:
                return false;
        }
    }
    public void processReminders() throws SQLException {
        List<Map<String, Object>> upcoming = appointmentDAO.getAcceptedAppointmentsWithinDays(2);

        for (Map<String, Object> record : upcoming) {
            Appointment app = (Appointment) record.get("appointment");
            String customerName = (String) record.get("customerName");
            String customerEmail = (String) record.get("customerEmail");

            String subject = "Nhắc hẹn mang xe đến sửa chữa";
            String message = String.format("""
            Xin chào %s,
            Đây là lời nhắc từ Garage: bạn có lịch hẹn vào ngày %s.
            Vui lòng mang xe đến đúng giờ để đảm bảo lịch sửa chữa.
            """, customerName, app.getAppointmentDate().toLocalDate());

            try {
                MailService.sendEmail(customerEmail, subject, message);
                System.out.println("Sent reminder to " + customerEmail);
            } catch (Exception e) {
                System.err.println(" Failed to send reminder to " + customerEmail);
                e.printStackTrace();
            }
        }

        System.out.println(" Reminder emails sent for " + upcoming.size() + " appointments.");
    }


    // Tự động reject các appointment quá hạn chưa đến
    public void processAutoReject() throws SQLException {
        int updated = appointmentDAO.autoRejectNoShowAppointments();
        System.out.println(" Auto-rejected " + updated + " appointments (no ServiceOrder).");
    }

}
