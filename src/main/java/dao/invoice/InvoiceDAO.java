package dao.invoice;

import common.DbContext;
import model.invoice.Invoice;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles database operations for the 'Invoice' table.
 */
public class InvoiceDAO extends DbContext {

    /**
     * Creates a new invoice in the database.
     * Assumes Subtotal and TaxAmount are calculated beforehand.
     * Generated columns (TotalAmount, BalanceAmount) and defaults (PaymentStatus, CreatedAt) are handled by the DB.
     *
     * @param invoice The Invoice object to insert.
     * @return The newly generated InvoiceID if successful, or -1 on failure.
     * @throws SQLException If a database access error occurs.
     */
    public int createInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO Invoice (WorkOrderID, InvoiceNumber, InvoiceDate, DueDate, Subtotal, TaxAmount, Notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getWorkOrderID());
            ps.setString(2, invoice.getInvoiceNumber());
            ps.setDate(3, invoice.getInvoiceDate()); // java.sql.Date

            if (invoice.getDueDate() != null) {
                ps.setDate(4, invoice.getDueDate());
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setBigDecimal(5, invoice.getSubtotal());
            ps.setBigDecimal(6, invoice.getTaxAmount());
            ps.setString(7, invoice.getNotes());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new InvoiceID
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves a single invoice by its ID.
     *
     * @param invoiceId The ID of the invoice to retrieve.
     * @return An Invoice object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Invoice getInvoiceById(int invoiceId) throws SQLException {
        Invoice invoice = null;
        // Select all columns, including generated ones
        String sql = "SELECT * FROM Invoice WHERE InvoiceID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invoice = mapRowToInvoice(rs);
                }
            }
        }
        return invoice;
    }

    /**
     * Retrieves an invoice by its associated WorkOrderID.
     * Since WorkOrderID is unique in the Invoice table, this should return at most one invoice.
     *
     * @param workOrderId The ID of the WorkOrder.
     * @return An Invoice object if found, otherwise null.
     * @throws SQLException If a database access error occurs.
     */
    public Invoice getInvoiceByWorkOrderId(int workOrderId) throws SQLException {
        Invoice invoice = null;
        String sql = "SELECT * FROM Invoice WHERE WorkOrderID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invoice = mapRowToInvoice(rs);
                }
            }
        }
        return invoice;
    }


    /**
     * Updates the payment-related fields of an invoice.
     * Typically called after a payment is recorded.
     *
     * @param invoiceId The ID of the invoice to update.
     * @param newPaidAmount The total amount paid so far.
     * @param newPaymentStatus The new payment status ('UNPAID', 'PARTIALLY_PAID', 'PAID', 'VOID').
     * @return true if the update was successful, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean updateInvoicePayment(int invoiceId, BigDecimal newPaidAmount, String newPaymentStatus) throws SQLException {
        // Only update PaidAmount and PaymentStatus
        // TotalAmount and BalanceAmount are generated columns and will update automatically.
        String sql = "UPDATE Invoice SET PaidAmount = ?, PaymentStatus = ? WHERE InvoiceID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, newPaidAmount);
            ps.setString(2, newPaymentStatus);
            ps.setInt(3, invoiceId);

            return ps.executeUpdate() > 0;
        }
    }


    /**
     * Helper method to map a ResultSet row to an Invoice object.
     * @param rs The ResultSet containing invoice data.
     * @return An Invoice object.
     * @throws SQLException
     */
    private Invoice mapRowToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(rs.getInt("InvoiceID"));
        invoice.setWorkOrderID(rs.getInt("WorkOrderID"));
        invoice.setInvoiceNumber(rs.getString("InvoiceNumber"));
        invoice.setInvoiceDate(rs.getDate("InvoiceDate"));
        invoice.setDueDate(rs.getDate("DueDate"));
        invoice.setSubtotal(rs.getBigDecimal("Subtotal"));
        invoice.setTaxAmount(rs.getBigDecimal("TaxAmount"));
       // invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        invoice.setPaidAmount(rs.getBigDecimal("PaidAmount"));
       // invoice.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
        invoice.setPaymentStatus(rs.getString("PaymentStatus"));
        invoice.setNotes(rs.getString("Notes"));
        invoice.setCreatedAt(rs.getTimestamp("CreatedAt"));
        invoice.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return invoice;
    }
    public List<Invoice> getInvoicesByStatus(String... statuses) throws SQLException {
        // Handle empty input gracefully
        if (statuses == null || statuses.length == 0) {
            return Collections.emptyList(); // Return an empty list immediately
        }

        List<Invoice> invoices = new ArrayList<>();

        // Build the SQL query dynamically based on the number of statuses
        // Creates placeholders like (?, ?) or (?, ?, ?)
        StringBuilder sql = new StringBuilder("SELECT * FROM Invoice WHERE PaymentStatus IN (");
        for (int i = 0; i < statuses.length; i++) {
            sql.append("?");
            if (i < statuses.length - 1) {
                sql.append(", ");
            }
        }
        sql.append(") ORDER BY InvoiceDate DESC"); // Order by newest invoice date

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set the status parameters in the PreparedStatement
            for (int i = 0; i < statuses.length; i++) {
                ps.setString(i + 1, statuses[i]); // Parameter index starts at 1
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapRowToInvoice(rs)); // Use the existing helper method
                }
            }
        }
        return invoices;
    }

}
