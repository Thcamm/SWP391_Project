package model.servicetype;

public class Service {
    private int serviceTypeID;
    private String serviceName;
    private String category;
    private double price;

    public Service() {
    }

    public Service(int serviceTypeID, String serviceName, String category, double price) {
        this.serviceTypeID = serviceTypeID;
        this.serviceName = serviceName;
        this.category = category;
        this.price = price;
    }

    public int getServiceTypeID() {
        return serviceTypeID;
    }

    public void setServiceTypeID(int serviceTypeID) {
        this.serviceTypeID = serviceTypeID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
