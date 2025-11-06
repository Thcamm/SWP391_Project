package model.support;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

public class SupportRequest {
    private Integer requestId;
    private Integer customerId;
    private Integer workOrderId;
    private Integer appointmentId;
    private Integer categoryId;
    private String description;
    private String attachmentPath;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SupportRequest() {
    }

    public SupportRequest(Integer requestId, Integer customerId, Integer appointmentId, Integer workOrderId, Integer categoryId, String description, String status, String attachmentPath,LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Integer getCustomerId() {
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

    public Integer getCategoryId() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SupportRequest that)) return false;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requestId);
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
