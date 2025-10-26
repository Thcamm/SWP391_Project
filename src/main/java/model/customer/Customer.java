package model.customer;

import model.user.User;
import model.vehicle.Vehicle;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Customer extends User {
    private int customerId;
    private int userId;
    private int pointLoyalty;
    private List<Vehicle> vehicles;

    public Customer() {
        super();
    }

    public Customer(int customerId, int userId, int pointLoyalty) {
        super();
        this.customerId = customerId;
        this.userId = userId;
        this.pointLoyalty = pointLoyalty;
    }

    // Constructor đầy đủ
    public Customer(String address, boolean activeStatus, Date birthDate, Timestamp createdAt, String email,
            String fullName, String gender, String passwordHash, String phoneNumber, int roleId, Timestamp updatedAt,
            Integer userId, String userName, int customerId, int pointLoyalty) {
        super();
        this.customerId = customerId;
        this.pointLoyalty = pointLoyalty;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getPointLoyalty() {
        return pointLoyalty;
    }

    public void setPointLoyalty(int pointLoyalty) {
        this.pointLoyalty = pointLoyalty;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Customer))
            return false;
        if (!super.equals(o))
            return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customerId);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", userId=" + userId +
                ", pointLoyalty=" + pointLoyalty +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}