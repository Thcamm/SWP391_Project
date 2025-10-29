package dao.payment;

import common.DbContext;
import model.payment.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for the 'Payment' table.
 */
public class PaymentDAO extends DbContext {

    /**
     * Records a new payment in the database.
     *
     * @param payment The Payment object containing payment details.
     * @return The newly generated PaymentID if successful, or -1 on failure.
     * @throws SQLException If a database access error occurs.
     */
    public int recordPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payment (InvoiceID, WorkOrderID, PaymentDate, Amount, Method, ReferenceNo, AccountantID, Note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, payment.getInvoiceID());
            ps.setInt(2, payment.getWorkOrderID());
            ps.setTimestamp(3, payment.getPaymentDate()); // java.sql.Timestamp
            ps.setBigDecimal(4, payment.getAmount());
            ps.setString(5, payment.getMethod()); // 'ONLINE' or 'OFFLINE'
            ps.setString(6, payment.getReferenceNo());
            ps.setInt(7, payment.getAccountantID());
            ps.setString(8, payment.getNote());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return the new PaymentID
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Retrieves all payments associated with a specific invoice.
     *
     * @param invoiceId The ID of the invoice.
     * @return A list of Payment objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<Payment> getPaymentsByInvoiceId(int invoiceId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE InvoiceID = ? ORDER BY PaymentDate DESC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapRowToPayment(rs));
                }
            }
        }
        return payments;
    }

    /**
     * Helper method to map a ResultSet row to a Payment object.
     * @param rs The ResultSet containing payment data.
     * @return A Payment object.
     * @throws SQLException
     */
    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentID(rs.getInt("PaymentID"));
        payment.setInvoiceID(rs.getInt("InvoiceID"));
        payment.setWorkOrderID(rs.getInt("WorkOrderID"));
        payment.setPaymentDate(rs.getTimestamp("PaymentDate"));
        payment.setAmount(rs.getBigDecimal("Amount"));
        payment.setMethod(rs.getString("Method"));
        payment.setReferenceNo(rs.getString("ReferenceNo"));
        payment.setAccountantID(rs.getInt("AccountantID"));
        payment.setNote(rs.getString("Note"));
        return payment;
    }

}
