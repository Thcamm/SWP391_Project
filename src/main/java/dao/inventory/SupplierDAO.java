package dao.inventory;

import common.DbContext;
import model.inventory.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO extends DbContext {

    /**
     * Get all active suppliers
     */
    public List<Supplier> getAllActiveSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Supplier ";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
        }
        return suppliers;
    }

    /**
     * Get supplier by ID
     */
    public Supplier getSupplierById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM Supplier WHERE SupplierID = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get supplier by name
     */
    public Supplier getSupplierByName(String supplierName) throws SQLException {
        String sql = "SELECT * FROM Supplier WHERE SupplierName = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, supplierName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
        }
        return null;
    }

    /**
     * Insert new supplier
     */
    public boolean insertSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Supplier (SupplierName) " +
                "VALUES (?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, supplier.getSupplierName());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setSupplierId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }


    /**
     * Map ResultSet to Supplier object
     */
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("SupplierID"));
        supplier.setSupplierName(rs.getString("SupplierName"));
        return supplier;
    }

    public static void main(String[] args) {
        System.out.println("Testing SupplierDAO...");
        SupplierDAO dao = new SupplierDAO();
        try {
            List<Supplier> suppliers = dao.getAllActiveSuppliers();
            for (Supplier supplier : suppliers) {
                System.out.println("ID: " + supplier.getSupplierId() + ", Name: " + supplier.getSupplierName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

