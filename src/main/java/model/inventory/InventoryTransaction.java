package model.inventory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryTransaction {

    private long transactionId;
    private int partId;
    private int partDetailId;
    private String transactionType;
    private LocalDateTime transactionDate;
    private int storeKeeperId;
    private String note;
    private int quantity;
    private BigDecimal unitPrice;
    private Integer workOrderPartId;
    private Integer supplierId;
    private String employeeCode;

    public InventoryTransaction() {
    }

    public InventoryTransaction(long transactionId, int partId, int partDetailId, String transactionType,
                                LocalDateTime transactionDate, int storeKeeperId, String note, int quantity,
                                BigDecimal unitPrice, Integer workOrderPartId, Integer supplierId) {
        this.transactionId = transactionId;
        this.partId = partId;
        this.partDetailId = partDetailId;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.storeKeeperId = storeKeeperId;
        this.note = note;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.workOrderPartId = workOrderPartId;
        this.supplierId = supplierId;
    }

    // ----- Getters and Setters -----

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public int getPartId() {
        return partId;
    }

    public void setPartId(int partId) {
        this.partId = partId;
    }

    public int getPartDetailId() {
        return partDetailId;
    }

    public void setPartDetailId(int partDetailId) {
        this.partDetailId = partDetailId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getStoreKeeperId() {
        return storeKeeperId;
    }

    public void setStoreKeeperId(int storeKeeperId) {
        this.storeKeeperId = storeKeeperId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getWorkOrderPartId() {
        return workOrderPartId;
    }

    public void setWorkOrderPartId(Integer workOrderPartId) {
        this.workOrderPartId = workOrderPartId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    // Helper method to format date for display
    public String getFormattedTransactionDate() {
        if (transactionDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return transactionDate.format(formatter);
        }
        return "";
    }
}