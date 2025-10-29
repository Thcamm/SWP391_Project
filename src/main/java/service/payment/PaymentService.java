package service.payment;

import dao.invoice.InvoiceDAO;
import dao.payment.PaymentDAO;
import model.invoice.Invoice;
import model.payment.Payment;
import common.DbContext;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects; // For null checks

/**
 * Handles business logic related to payments, coordinating PaymentDAO and InvoiceDAO.
 * Manages database transactions for payment processing. Includes validation.
 */
public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final InvoiceDAO invoiceDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.invoiceDAO = new InvoiceDAO();
    }

    /**
     * Processes a new payment: validates input, records it, and updates the corresponding invoice.
     * This operation is performed within a database transaction.
     *
     * @param payment The Payment object containing details of the payment to record.
     * @return true if the payment was processed successfully, false otherwise.
     * @throws IllegalArgumentException if the input payment data is invalid.
     * @throws SQLException if a database error occurs that prevents processing.
     */
    public boolean processPayment(Payment payment) throws SQLException, IllegalArgumentException {
        // --- STEP 0: VALIDATION ---
        validatePaymentInput(payment);

        Connection conn = null;
        boolean success = false;

        try {
            conn = DbContext.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // --- STEP 1: GET INVOICE AND VALIDATE STATUS ---
            Invoice invoice = invoiceDAO.getInvoiceById(payment.getInvoiceID());
            if (invoice == null) {
                throw new IllegalArgumentException("Invoice with ID " + payment.getInvoiceID() + " not found.");
            }

            // Check if invoice is already paid or voided
            if ("PAID".equals(invoice.getPaymentStatus()) || "VOID".equals(invoice.getPaymentStatus())) {
                throw new IllegalArgumentException("Cannot process payment for an invoice that is already '" + invoice.getPaymentStatus() + "'.");
            }

            // --- STEP 2: RECORD PAYMENT ---
            int paymentId = paymentDAO.recordPayment(payment);
            if (paymentId == -1) {
                throw new SQLException("Failed to record payment details in the database.");
            }

            // --- STEP 3: CALCULATE NEW INVOICE STATE ---
            BigDecimal newPaidAmount = invoice.getPaidAmount().add(payment.getAmount());
            String newStatus;
            BigDecimal totalAmount = invoice.getTotalAmount(); // Store for clarity

            // Use compareTo for accurate BigDecimal comparison
            int comparison = newPaidAmount.compareTo(totalAmount);

            if (comparison >= 0) { // Paid amount is >= total amount
                newStatus = "PAID";
                // Cap the paid amount at the total amount to avoid negative balance
                newPaidAmount = totalAmount;
            } else { // Paid amount is > 0 but < total (already checked amount > 0 in validation)
                newStatus = "PARTIALLY_PAID";
            }

            // --- STEP 4: UPDATE INVOICE ---
            boolean invoiceUpdated = invoiceDAO.updateInvoicePayment(invoice.getInvoiceID(), newPaidAmount, newStatus);
            if (!invoiceUpdated) {
                // This indicates a DB error during update
                throw new SQLException("Failed to update invoice status after payment recording.");
            }

            // If all steps succeeded, commit the transaction
            conn.commit();
            success = true;

        } catch (SQLException | IllegalArgumentException e) {
            // Rollback on ANY error (DB or validation logic)
            if (conn != null) {
                try {
                    System.err.println("Transaction is being rolled back due to error: " + e.getMessage());
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback failure, but the original exception is more important
                    System.err.println("Error during transaction rollback: " + ex.getMessage());
                }
            }
            // Re-throw the exception to let the caller (Servlet) know about the failure
            throw e;
        } finally {
            // Ensure connection resources are always released
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }

        return success; // Will only be reached if commit happens
    }

    /**
     * Validates the essential fields of the Payment object.
     * @param payment The Payment object to validate.
     * @throws IllegalArgumentException if validation fails.
     */
    private void validatePaymentInput(Payment payment) throws IllegalArgumentException {
        Objects.requireNonNull(payment, "Payment object cannot be null.");

        if (payment.getInvoiceID() <= 0) {
            throw new IllegalArgumentException("Invalid Invoice ID.");
        }
        if (payment.getWorkOrderID() <= 0) { // Assuming WorkOrderID is also mandatory
            throw new IllegalArgumentException("Invalid WorkOrder ID.");
        }
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }
        if (payment.getMethod() == null || (!"ONLINE".equals(payment.getMethod()) && !"OFFLINE".equals(payment.getMethod()))) {
            throw new IllegalArgumentException("Invalid payment method.");
        }
        if (payment.getAccountantID() <= 0) { // Assuming AccountantID is mandatory
            throw new IllegalArgumentException("Invalid Accountant ID.");
        }
        if (payment.getPaymentDate() == null) {
            throw new IllegalArgumentException("Payment date cannot be null.");
        }
    }
}

