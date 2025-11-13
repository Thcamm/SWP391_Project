package dao.invoice;

import common.DbContext;
import model.invoice.Invoice;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public int insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO Invoice (" +
                "WorkOrderID, InvoiceNumber, InvoiceDate, DueDate, " +
                "Subtotal, TaxAmount, PaidAmount, PaymentStatus, Notes" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, invoice.getWorkOrderID());
            stmt.setString(2, invoice.getInvoiceNumber());
            stmt.setDate(3, invoice.getInvoiceDate());
            stmt.setDate(4, invoice.getDueDate());
            stmt.setBigDecimal(5, invoice.getSubtotal());
            stmt.setBigDecimal(6, invoice.getTaxAmount());
            stmt.setBigDecimal(7, invoice.getPaidAmount());
            stmt.setString(8, invoice.getPaymentStatus());
            stmt.setString(9, invoice.getNotes());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating invoice failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating invoice failed, no ID obtained.");
                }
            }
        }
    }

    public void delete(int invoiceId) throws SQLException {
        String sql = "DELETE FROM Invoice WHERE InvoiceID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            stmt.executeUpdate();
        }
    }

    public void update(Invoice invoice) throws SQLException {
        String sql = "UPDATE Invoice SET " +
                "InvoiceDate = ?, DueDate = ?, Subtotal = ?, TaxAmount = ?, " +
                "PaidAmount = ?, PaymentStatus = ?, Notes = ?, UpdatedAt = ? " +
                "WHERE InvoiceID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, invoice.getInvoiceDate());
            stmt.setDate(2, invoice.getDueDate());
            stmt.setBigDecimal(3, invoice.getSubtotal());
            stmt.setBigDecimal(4, invoice.getTaxAmount());
            stmt.setBigDecimal(5, invoice.getPaidAmount());
            stmt.setString(6, invoice.getPaymentStatus());
            stmt.setString(7, invoice.getNotes());
            stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(9, invoice.getInvoiceID());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update invoice failed, invoice not found: " + invoice.getInvoiceID());
            }
        }
    }

    public void updatePaymentInfo(Connection conn, int invoiceID,
                                  BigDecimal newPaidAmount, String newStatus) throws SQLException {
        String sql = "UPDATE Invoice SET " +
                "PaidAmount = ?, PaymentStatus = ?, UpdatedAt = NOW() " +
                "WHERE InvoiceID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newPaidAmount);
            stmt.setString(2, newStatus);
            stmt.setInt(3, invoiceID);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Invoice not found: " + invoiceID);
            }
        }
    }

    public void updatePaymentInfo(int invoiceID, BigDecimal newPaidAmount, String newStatus)
            throws SQLException {
        try (Connection conn = DbContext.getConnection()) {
            updatePaymentInfo(conn, invoiceID, newPaidAmount, newStatus);
        }
    }

    public Invoice getById(int invoiceID) throws SQLException {
        String sql = "SELECT * FROM Invoice WHERE InvoiceID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        }
        return null;
    }

    public Invoice getByWorkOrderID(int workOrderID) throws SQLException {
        String sql = "SELECT * FROM Invoice WHERE WorkOrderID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workOrderID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInvoice(rs);
                }
            }
        }
        return null;
    }

    public List<Invoice> getAll() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice ORDER BY CreatedAt DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        }
        return invoices;
    }

    public boolean existsByWorkOrderID(int workOrderID) throws SQLException {
        String sql = "SELECT COUNT(*) as Count FROM Invoice WHERE WorkOrderID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, workOrderID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Count") > 0;
                }
            }
        }
        return false;
    }

    public String generateInvoiceNumber(Connection conn) throws SQLException {
        String sql = "SELECT CONCAT('INV-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', " +
                "LPAD((SELECT COUNT(*) + 1 FROM Invoice " +
                "WHERE DATE(InvoiceDate) = CURDATE() FOR UPDATE), 4, '0'))";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
            throw new SQLException("Failed to generate invoice number");
        }
    }

    public List<Invoice> getByStatus(String status) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice WHERE PaymentStatus = ? ORDER BY CreatedAt DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }
        return invoices;
    }

    public List<Invoice> getOverdueInvoices() throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM Invoice " +
                "WHERE PaymentStatus IN ('UNPAID', 'PARTIALLY_PAID') " +
                "AND DueDate < CURDATE() " +
                "ORDER BY DueDate ASC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                invoices.add(mapResultSetToInvoice(rs));
            }
        }
        return invoices;
    }

    public List<Invoice> search(String keyword) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT i.* FROM Invoice i " +
                "WHERE i.InvoiceNumber LIKE ? " +
                "OR i.Notes LIKE ? " +
                "ORDER BY i.CreatedAt DESC";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }
        return invoices;
    }

    public List<Invoice> getInvoicesWithPagination(int page, int pageSize) throws SQLException {
        List<Invoice> invoices = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String sql = "SELECT * FROM Invoice " +
                "ORDER BY CreatedAt DESC " +
                "LIMIT ? OFFSET ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }
        return invoices;
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Invoice";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Invoice> getInvoicesByCustomerID(int customerID) throws SQLException {
        String sql =
                "SELECT DISTINCT i.* " +
                        "FROM Invoice i " +
                        "INNER JOIN WorkOrder wo ON i.WorkOrderID = wo.WorkOrderID " +
                        "INNER JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "INNER JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "WHERE v.CustomerID = ? " +
                        "ORDER BY i.InvoiceDate DESC";

        List<Invoice> invoices = new ArrayList<>();

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToInvoice(rs));
                }
            }
        }

        return invoices;
    }

    private Invoice mapResultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setInvoiceID(rs.getInt("InvoiceID"));
        invoice.setWorkOrderID(rs.getInt("WorkOrderID"));
        invoice.setInvoiceNumber(rs.getString("InvoiceNumber"));
        invoice.setInvoiceDate(rs.getDate("InvoiceDate"));
        invoice.setDueDate(rs.getDate("DueDate"));
        invoice.setSubtotal(rs.getBigDecimal("Subtotal"));
        invoice.setTaxAmount(rs.getBigDecimal("TaxAmount"));
        invoice.setTotalAmount(rs.getBigDecimal("TotalAmount"));
        invoice.setPaidAmount(rs.getBigDecimal("PaidAmount"));
        invoice.setBalanceAmount(rs.getBigDecimal("BalanceAmount"));
        invoice.setPaymentStatus(rs.getString("PaymentStatus"));
        invoice.setNotes(rs.getString("Notes"));
        invoice.setCreatedAt(rs.getTimestamp("CreatedAt"));
        invoice.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        return invoice;
    }
}