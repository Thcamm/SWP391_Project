package model.appointment;

import java.time.LocalDateTime;
import java.util.Objects;


public class Appointment {
    private int AppointmentID;
    private int CustomerID;
    private LocalDateTime AppointmentDate;
    private LocalDateTime createdAt;
    private String Status;
    private String Description;
    private int rescheduleCount;
    private LocalDateTime updatedAt;

    public Appointment() {
    }

    public Appointment(int appointmentID, int customerID, LocalDateTime appointmentDate, LocalDateTime createdAt, String status, String description, LocalDateTime updatedAt, int rescheduleCount) {
        AppointmentID = appointmentID;
        CustomerID = customerID;
        AppointmentDate = appointmentDate;
        this.createdAt = createdAt;
        Status = status;
        Description = description;
        this.updatedAt = updatedAt;
        this.rescheduleCount = rescheduleCount;
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

    public LocalDateTime getAppointmentDate() {
        return AppointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        AppointmentDate = appointmentDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Appointment that)) return false;
        return AppointmentID == that.AppointmentID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(AppointmentID);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "AppointmentID=" + AppointmentID +
                ", CustomerID=" + CustomerID +
                ", createdAt=" + createdAt +
                ", Status='" + Status + '\'' +
                ", Description='" + Description + '\'' +
                ", rescheduleCount=" + rescheduleCount +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


