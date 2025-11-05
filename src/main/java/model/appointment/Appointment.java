package model.appointment;

import java.time.LocalDateTime;


public class Appointment {
    private int AppointmentID;
    private int CustomerID;
    private int VehicleID;
    private LocalDateTime AppointmentDate;
    private String Status;
    private String Description;
    private int rescheduleCount;

    public Appointment() {
    }
    public Appointment(int AppointmentID, int CustomerID, int VehicleID,LocalDateTime AppointmentDate, String Status,String Description,int rescheduleCount) {
        this.AppointmentID = AppointmentID;
        this.CustomerID = CustomerID;
        this.VehicleID = VehicleID;
        this.Status = Status;
        this.AppointmentDate = AppointmentDate;
        this.Description = Description;
        this.rescheduleCount= rescheduleCount;
    }

    public LocalDateTime getAppointmentDate() {
        return AppointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        AppointmentDate = appointmentDate;
    }

    public int getAppointmentID() {
        return AppointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        AppointmentID = appointmentID;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getVehicleID() {
        return VehicleID;
    }

    public void setVehicleID(int vehicleID) {
        VehicleID = vehicleID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getRescheduleCount() {
        return rescheduleCount;
    }

    public void setRescheduleCount(int rescheduleCount) {
        this.rescheduleCount = rescheduleCount;
    }
}
