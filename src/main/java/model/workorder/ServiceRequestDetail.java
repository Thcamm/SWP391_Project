package model.workorder;

public class ServiceRequestDetail {
    private int detailID;
    private int requestID;
    private int serviceID;

    public ServiceRequestDetail() {
    }

    public ServiceRequestDetail(int detailID, int requestID, int serviceID) {
        this.detailID = detailID;
        this.requestID = requestID;
        this.serviceID = serviceID;
    }

    public int getDetailID() {
        return detailID;
    }

    public void setDetailID(int detailID) {
        this.detailID = detailID;
    }

    public int getRequestID() {
        return requestID;
    }

    public void setRequestID(int requestID) {
        this.requestID = requestID;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }
}
