package model.workorder;

/**
 * Model for ServiceRequestDetail table
 * Represents individual services within a Service Request
 * 
 * @author SWP391 Team - Refactored for Triage Workflow
 */
public class ServiceRequestDetail {
    private int detailId;
    private int requestId;
    private int serviceId;

    // Transient fields for display (not in DB)
    private String serviceName;
    private String serviceDescription;
    private java.math.BigDecimal serviceUnitPrice;
    private java.math.BigDecimal estimateHours; // For TechManager to input during approval

    public ServiceRequestDetail() {
    }

    public ServiceRequestDetail(int detailId, int requestId, int serviceId) {
        this.detailId = detailId;
        this.requestId = requestId;
        this.serviceId = serviceId;
    }

    // Getters and Setters
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
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

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public java.math.BigDecimal getServiceUnitPrice() {
        return serviceUnitPrice;
    }

    public void setServiceUnitPrice(java.math.BigDecimal serviceUnitPrice) {
        this.serviceUnitPrice = serviceUnitPrice;
    }

    public java.math.BigDecimal getEstimateHours() {
        return estimateHours;
    }

    public void setEstimateHours(java.math.BigDecimal estimateHours) {
        this.estimateHours = estimateHours;
    }

    @Override
    public String toString() {
        return "ServiceRequestDetail{" +
                "detailId=" + detailId +
                ", requestId=" + requestId +
                ", serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
