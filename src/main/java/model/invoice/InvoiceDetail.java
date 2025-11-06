package model.invoice;

import model.payment.Payment;
import java.util.List;

/**
 * DTO chứa Invoice và các Payment liên quan
 */
public class InvoiceDetail {
    private Invoice invoice;
    private List<Payment> payments;
    private String customerName;
    private String vehiclePlate;
    private String workOrderDescription;

    public InvoiceDetail() {}

    public InvoiceDetail(Invoice invoice, List<Payment> payments) {
        this.invoice = invoice;
        this.payments = payments;
    }

    // Getters and Setters
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getWorkOrderDescription() {
        return workOrderDescription;
    }

    public void setWorkOrderDescription(String workOrderDescription) {
        this.workOrderDescription = workOrderDescription;
    }

    public int getTotalPayments() {
        return payments != null ? payments.size() : 0;
    }
}