package model.workorder;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class WorkOrder {

        private int workOrderId;
        private int techManagerId;
        private int requestId;
        private double estimateAmount;
        private String status; // PENDING, IN_PROCESS, COMPLETE
        private Date createdAt;

        private List<WorkOrderDetail> details = new ArrayList<>();

        public WorkOrder() {
        }

        public WorkOrder(int workOrderId, int techManagerId, int requestId, double estimateAmount, String status, Date createdAt) {
            this.workOrderId = workOrderId;
            this.techManagerId = techManagerId;
            this.requestId = requestId;
            this.estimateAmount = estimateAmount;
            this.status = status;
            this.createdAt = createdAt;
        }

        public int getWorkOrderId() {
            return workOrderId;
        }

        public void setWorkOrderId(int workOrderId) {
            this.workOrderId = workOrderId;
        }

        public int getTechManagerId() {
            return techManagerId;
        }

        public void setTechManagerId(int techManagerId) {
            this.techManagerId = techManagerId;
        }

        public int getRequestId() {
            return requestId;
        }

        public void setRequestId(int requestId) {
            this.requestId = requestId;
        }

        public double getEstimateAmount() {
            return estimateAmount;
        }

        public void setEstimateAmount(double estimateAmount) {
            this.estimateAmount = estimateAmount;
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

        public List<WorkOrderDetail> getDetails() {
            return details;
        }

        public void setDetails(List<WorkOrderDetail> details) {
            this.details = details;
        }

        @Override
        public String toString() {
            return "WorkOrder{" +
                    "workOrderId=" + workOrderId +
                    ", techManagerId=" + techManagerId +
                    ", requestId=" + requestId +
                    ", estimateAmount=" + estimateAmount +
                    ", status='" + status + '\'' +
                    ", createdAt=" + createdAt +
                    ", details=" + details +
                    '}';
        }
}
