package model.customer;

import model.user.User;
import model.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer extends User {
    private int customerId;
    private int userId;
    private int pointLoyalty;

    public Customer() {
        super();
    }

    public Customer(int customerId, int userId, int pointLoyalty) {
        super();
        this.customerId = customerId;
        this.userId = userId;
        this.pointLoyalty = pointLoyalty;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
                userId == customer.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), customerId, userId);
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
