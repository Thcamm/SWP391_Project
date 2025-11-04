package model.dto;

import java.math.BigDecimal;

/**
 * DTO representing a single line item on an invoice (either a service or a part).
 */
public class InvoiceItemDTO {
    private String description;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount; // Calculated: quantity * unitPrice

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public void setQuantity(BigDecimal bigDecimal) {
    }
}
