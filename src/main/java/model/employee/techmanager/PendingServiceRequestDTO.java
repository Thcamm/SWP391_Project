package model.employee.techmanager;

import model.workorder.ServiceRequestDetail;
import java.sql.Timestamp;
import java.util.List;

/**
 * DTO for displaying pending service requests in TechManager view (GĐ1).
 * Contains all necessary information for approval decision.
 * 
 * LUỒNG MỚI (Triage Workflow):
 * - After approval, creates N WorkOrderDetails (one per service)
 * - Each WOD starts with source=NULL, awaiting GĐ2 Triage classification
 * 
 * @author SWP391 Team
 * @version 2.0 (Updated for LUỒNG MỚI)
 */
public class PendingServiceRequestDTO {
    private int requestId;
    private int customerId;
    private String customerName;
    private String phoneNumber;
    private int vehicleId;
    private String licensePlate;
    private String vehicleBrand;
    private String vehicleModel;
    private int yearManufacture;
    private int serviceId;
    private String serviceName;
    private String serviceCategory;
    private double serviceUnitPrice;
    private Timestamp requestDate;
    private String status;
    private Integer appointmentId;

    // LUỒNG 4.0: List of services for this request
    private List<ServiceRequestDetail> services;

    // Constructors
    public PendingServiceRequestDTO() {
    }

    // Getters and Setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public int getYearManufacture() {
        return yearManufacture;
    }

    public void setYearManufacture(int yearManufacture) {
        this.yearManufacture = yearManufacture;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public double getServiceUnitPrice() {
        return serviceUnitPrice;
    }

    public void setServiceUnitPrice(double serviceUnitPrice) {
        this.serviceUnitPrice = serviceUnitPrice;
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

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public List<ServiceRequestDetail> getServices() {
        return services;
    }

    public void setServices(List<ServiceRequestDetail> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "PendingServiceRequestDTO{" +
                "requestId=" + requestId +
                ", customerName='" + customerName + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", requestDate=" + requestDate +
                '}';
    }
}
