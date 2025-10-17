package model.support;

import java.sql.Date;

public class SupportRequest {
    private int requestId;
    private int customerId;
    private Integer workOrderId;
    private Integer appointmentId;
    private int categoryId;
    private String description;
    private String attachmentPath;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    public SupportRequest() {
    }

    public SupportRequest(int requestId, int customerId, Integer appointmentId, Integer workOrderId, int categoryId, String description, String status, String attachmentPath, Date createdAt, Date updatedAt) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.appointmentId = appointmentId;
        this.workOrderId = workOrderId;
        this.categoryId = categoryId;
        this.description = description;
        this.status = status;
        this.attachmentPath = attachmentPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public Integer getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Integer workOrderId) {
        this.workOrderId = workOrderId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SupportRequest{" +
                "requestId=" + requestId +
                ", customerId=" + customerId +
                ", workOrderId=" + workOrderId +
                ", appointmentId=" + appointmentId +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                ", attachmentPath='" + attachmentPath + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
