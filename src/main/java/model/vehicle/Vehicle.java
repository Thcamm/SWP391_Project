package model.vehicle;

public class Vehicle {
    private int vehicleID;
    private int customerID;
    private String licensePlate;
    private String brand;
    private String model;
    private int yearManufacture;

    public Vehicle() {
    }

    public Vehicle(int vehicleID, int customerID, String licensePlate, String brand, String model, int yearManufacture) {
        this.vehicleID = vehicleID;
        this.customerID = customerID;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.yearManufacture = yearManufacture;
    }

    public Vehicle(String brand, int customerID, String licensePlate, String model, int yearManufacture) {
        this.brand = brand;
        this.customerID = customerID;
        this.licensePlate = licensePlate;
        this.model = model;
        this.yearManufacture = yearManufacture;
    }

    public int getVehicleID() { return vehicleID; }
    public void setVehicleID(int vehicleID) { this.vehicleID = vehicleID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYearManufacture() { return yearManufacture; }
    public void setYearManufacture(int yearManufacture) { this.yearManufacture = yearManufacture; }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleID=" + vehicleID +
                ", customerID=" + customerID +
                ", licensePlate='" + licensePlate + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", yearManufacture=" + yearManufacture +
                '}';
    }
}

