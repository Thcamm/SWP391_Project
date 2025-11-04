package dao.invoice;

import common.DbContext;
import model.invoice.Invoice;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    /**
     * Tạo invoice mới
     */
    public int insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO Invoice (" +
                "WorkOrderID, InvoiceNumber, InvoiceDate, DueDate, " +
                "Subtotal, TaxAmount, PaidAmount, PaymentStatus, Notes, " +
                "CreatedAt, UpdatedAt" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            stmt.setTimestamp(10, invoice.getCreatedAt());
            stmt.setTimestamp(11, invoice.getUpdatedAt());

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

    public void update(Invoice invoice) throws Exception {
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
                throw new Exception("Cập nhật invoice thất bại, không tìm thấy invoice với ID: " + invoice.getInvoiceID());
            }
        }
    }

    public Invoice getById(int invoiceID) throws Exception {
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

    public List<Invoice> getAll() throws Exception {
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

    /**
     * Check if invoice exists for a WorkOrder
     */
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

    public int getNextSequenceForDate(LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) + 1 as NextSequence " +
                "FROM Invoice " +
                "WHERE DATE(InvoiceDate) = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("NextSequence");
                }
            }
        }

        return 1;
    }

    public List<Invoice> getByStatus(String status) throws Exception {
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

    public List<Invoice> getOverdueInvoices() throws Exception {
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

    public List<Invoice> search(String keyword) throws Exception {
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

    public List<Invoice> getInvoicesWithPagination(int page, int pageSize) throws Exception {
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

    public int getTotalCount() throws Exception {
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