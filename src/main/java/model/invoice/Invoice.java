package model.invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;

public class Invoice {
    private int invoiceID;
    private int workOrderID;
    private String invoiceNumber;
    private Date invoiceDate;
    private Date dueDate;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceAmount;
    private String paymentStatus;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructors
    public Invoice() {}

    // Getters and Setters
    public int getInvoiceID() { return invoiceID; }
    public void setInvoiceID(int invoiceID) { this.invoiceID = invoiceID; }

    public int getWorkOrderID() { return workOrderID; }
    public void setWorkOrderID(int workOrderID) { this.workOrderID = workOrderID; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(BigDecimal balanceAmount) { this.balanceAmount = balanceAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Calculate payment percentage
     * @return Percentage of total amount paid (0-100)
     */
    public BigDecimal getPaymentPercentage() {
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        if (paidAmount == null) {
            return BigDecimal.ZERO;
        }

        return paidAmount
                .multiply(new BigDecimal("100"))
                .divide(totalAmount, 2, RoundingMode.HALF_UP);
    }

    /**
     * Check if invoice is overdue
     * @return true if past due date and not fully paid
     */
    public boolean isOverdue() {
        if (dueDate == null) return false;
        if ("PAID".equals(paymentStatus)) return false;

        return dueDate.before(new Date(System.currentTimeMillis()));
    }

    /**
     * Get payment status display text
     * @return Human-readable payment status
     */
    public String getPaymentStatusDisplay() {
        if (paymentStatus == null) return "Unknown";

        switch (paymentStatus) {
            case "UNPAID": return "Unpaid";
            case "PARTIALLY_PAID": return "Partially Paid";
            case "PAID": return "Paid";
            case "VOID": return "Void";
            default: return paymentStatus;
        }
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID=" + invoiceID +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}