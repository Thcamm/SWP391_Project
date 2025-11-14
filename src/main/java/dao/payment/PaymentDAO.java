package dao.payment;

import common.DbContext;
import model.payment.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public int insert(Payment payment) throws Exception {
        String sql = "INSERT INTO Payment (InvoiceID, WorkOrderID, PaymentDate, Amount, " +
                "Method, ReferenceNo, AccountantID, Note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, payment.getInvoiceID());
            stmt.setInt(2, payment.getWorkOrderID());
            stmt.setTimestamp(3, payment.getPaymentDate());
            stmt.setBigDecimal(4, payment.getAmount());
            stmt.setString(5, payment.getMethod());
            stmt.setString(6, payment.getReferenceNo());
            stmt.setInt(7, payment.getAccountantID());
            stmt.setString(8, payment.getNote());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        payment.setPaymentID(generatedId);
                        return generatedId;
                    }
                }
            }
            throw new Exception("Tạo payment thất bại, không có ID được tạo.");
        }
    }

    public Payment getById(int paymentID) throws Exception {
        String sql = "SELECT * FROM Payment WHERE PaymentID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        return null;
    }


    public List<Payment> getByInvoiceID(int invoiceID) throws Exception {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE InvoiceID = ? ORDER BY PaymentDate DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        return payments;
    }

    public List<Payment> getAll() throws Exception {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment ORDER BY PaymentDate DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        }
        return payments;
    }


    public void delete(int paymentID) throws Exception {
        String sql = "DELETE FROM Payment WHERE PaymentID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, paymentID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new Exception("Xóa payment thất bại, không tìm thấy payment với ID: " + paymentID);
            }
        }
    }

    public BigDecimal getTotalRevenueByDateRange(Date startDate, Date endDate) throws Exception {
        String sql = "SELECT COALESCE(SUM(Amount), 0) FROM Payment " +
                "WHERE DATE(PaymentDate) BETWEEN ? AND ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
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