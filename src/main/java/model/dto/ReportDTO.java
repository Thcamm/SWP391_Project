package model.dto;

import java.math.BigDecimal;

public class ReportDTO {
    // Revenue fields
    private String label;
    private BigDecimal amount;
    private int count;

    // Date fields
    private int year;
    private int month;
    private String monthYear;

    // Invoice fields
    private BigDecimal totalInvoiced;
    private BigDecimal totalPaid;
    private BigDecimal totalOutstanding;
    private int invoiceCount;
    private int totalInvoices;

    // Customer fields
    private int customerID;
    private String customerName;
    private String customerEmail;
    private String phoneNumber;
    private BigDecimal outstandingBalance;

    // Payment fields
    private String paymentMethod;
    private int paymentCount;
    private BigDecimal paymentAmount;

    // Status fields
    private String status;

    // Constructors
    public ReportDTO() {}

    public ReportDTO(String label, BigDecimal amount, int count) {
        this.label = label;
        this.amount = amount;
        this.count = count;
    }

    // Getters and Setters
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public String getMonthYear() { return monthYear; }
    public void setMonthYear(String monthYear) { this.monthYear = monthYear; }

    /**
     * Alias for getMonthYear() for JSP compatibility
     */
    public String getYearMonth() {
        if (monthYear != null) {
            return monthYear;
        }
        if (year > 0 && month > 0) {
            return String.format("%d-%02d", year, month);
        }
        return "";
    }

    public void setYearMonth(String yearMonth) {
        this.monthYear = yearMonth;
    }

    public BigDecimal getTotalInvoiced() { return totalInvoiced; }
    public void setTotalInvoiced(BigDecimal totalInvoiced) { this.totalInvoiced = totalInvoiced; }

    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }

    public BigDecimal getTotalOutstanding() { return totalOutstanding; }
    public void setTotalOutstanding(BigDecimal totalOutstanding) { this.totalOutstanding = totalOutstanding; }

    public int getInvoiceCount() { return invoiceCount; }
    public void setInvoiceCount(int invoiceCount) { this.invoiceCount = invoiceCount; }

    public int getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(int totalInvoices) { this.totalInvoices = totalInvoices; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public BigDecimal getOutstandingBalance() { return outstandingBalance; }
    public void setOutstandingBalance(BigDecimal outstandingBalance) { this.outstandingBalance = outstandingBalance; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public int getPaymentCount() { return paymentCount; }
    public void setPaymentCount(int paymentCount) { this.paymentCount = paymentCount; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ReportDTO{" +
                "label='" + label + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", amount=" + amount +
                ", count=" + count +
                '}';
    }
}