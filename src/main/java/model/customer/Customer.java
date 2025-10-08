package model.customer;

import model.vehicle.Vehicle;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Customer {
    private int customerId;
    private int userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String gender;
    private LocalDate birthDate;
    private int pointLoyalty;
    private Date createdAt;

    private List<Vehicle> vehicles;

    public Customer() {
    }

    public Customer(String fullName, String email, String phoneNumber, String gender,String address,  LocalDate birthDate) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public Customer(int userId, int customerId, String fullName, String email, String phoneNumber, String address, String gender, int pointLoyalty, Date createdAt) {
        this.userId = userId;
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.gender = gender;
        this.birthDate = birthDate;
        this.pointLoyalty = pointLoyalty;
        this.createdAt = createdAt;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getPointLoyalty() {
        return pointLoyalty;
    }

    public void setPointLoyalty(int pointLoyalty) {
        this.pointLoyalty = pointLoyalty;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Customer customer)) return false;
        return customerId == customer.customerId && userId == customer.userId && pointLoyalty == customer.pointLoyalty && Objects.equals(fullName, customer.fullName) && Objects.equals(email, customer.email) && Objects.equals(phoneNumber, customer.phoneNumber)  && Objects.equals(createdAt, customer.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, userId, fullName, email, phoneNumber, pointLoyalty, createdAt);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", pointLoyalty=" + pointLoyalty +
                ", createdAt=" + createdAt +
                '}';
    }
}
