package service.appointment;

import dao.appointment.AppointmentDAO;
import model.appointment.Appointment;

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

    public void insertAppointment(model.appointment.Appointment appointment) {
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

}
