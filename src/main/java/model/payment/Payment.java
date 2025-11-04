package model.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {

    private int paymentID;
    private int invoiceID;
    private int workOrderID;
    private Timestamp paymentDate;
    private BigDecimal amount;
    private String method;      // ENUM('ONLINE','OFFLINE')
    private String referenceNo;
    private int accountantID;
    private String note;

    // Default constructor
    public Payment() {}

    // Constructor with essential fields
    public Payment(int invoiceID, int workOrderID, BigDecimal amount,
                   String method, int accountantID) {
        this.invoiceID = invoiceID;
        this.workOrderID = workOrderID;
        this.amount = amount;
        this.method = method;
        this.accountantID = accountantID;
        this.paymentDate = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public int getWorkOrderID() {
        return workOrderID;
    }

    public void setWorkOrderID(int workOrderID) {
        this.workOrderID = workOrderID;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public int getAccountantID() {
        return accountantID;
    }

    public void setAccountantID(int accountantID) {
        this.accountantID = accountantID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentID=" + paymentID +
                ", invoiceID=" + invoiceID +
                ", amount=" + amount +
                ", method='" + method + '\'' +
                ", referenceNo='" + referenceNo + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}