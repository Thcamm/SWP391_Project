package service.appointment;

import dao.appointment.AppointmentDAO;

public class Appointment {
    private AppointmentDAO appointmentDAO;

    public Appointment() {
        this.appointmentDAO = new AppointmentDAO();
    }

    public boolean updateAppointment(model.appointment.Appointment appointment) {
        return appointmentDAO.updateAppointment(appointment);
    }

    public void getAllAppointments() {
        appointmentDAO.getAllAppointments();
    }

    public void getAppointmentById(int appointmentID) {
        appointmentDAO.getAppointmentById(appointmentID);
    }

    public void insertAppointment(model.appointment.Appointment appointment) {
        appointmentDAO.insertAppointment(appointment);
    }

}
