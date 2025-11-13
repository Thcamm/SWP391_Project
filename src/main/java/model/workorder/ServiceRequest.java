package model.workorder;

import java.sql.Timestamp;

public class ServiceRequest {
    private int requestID;
    private int customerID;
    private int vehicleID;
    private Integer appointmentID; // Dùng Integer để có thể nhận giá trị null
    private Timestamp requestDate;
    private String status;
    private String note;
    private Timestamp updatedAt;

    public ServiceRequest() {
    }

    public ServiceRequest(int requestID, int customerID, int vehicleID, int serviceID, Integer appointmentID,
            Timestamp requestDate, String status, String note, Timestamp updatedAt) {
        this.requestID = requestID;
        this.customerID = customerID;
        this.vehicleID = vehicleID;
        this.appointmentID = appointmentID;
        this.requestDate = requestDate;
        this.status = status;
        this.note = note;
        this.updatedAt = updatedAt;
    }



    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }


    public Integer getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(Integer appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Business methods
    public boolean isApproved() {
        return "APPROVED".equalsIgnoreCase(this.status);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(this.status);
    }

    public boolean canCreateWorkOrder() {
        return isApproved();
    }
}