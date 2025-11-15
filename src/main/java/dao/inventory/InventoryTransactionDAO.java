package dao.inventory;

import common.DbContext;
import model.inventory.InventoryTransaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryTransactionDAO extends DbContext {

    public List<InventoryTransaction> getAllTransactions() throws SQLException {
        List<InventoryTransaction> transactions = new ArrayList<>();
        String sql = "SELECT it.*, e.EmployeeCode " +
                     "FROM InventoryTransaction it " +
                     "LEFT JOIN Employee e ON it.StoreKeeperID = e.EmployeeID " +
                     "ORDER BY it.TransactionDate DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setTransactionId(rs.getLong("TransactionID"));
                transaction.setPartId(rs.getInt("PartID"));
                transaction.setPartDetailId(rs.getInt("PartDetailID"));
                transaction.setTransactionType(rs.getString("TransactionType"));
                transaction.setTransactionDate(rs.getTimestamp("TransactionDate").toLocalDateTime());
                transaction.setStoreKeeperId(rs.getInt("StoreKeeperID"));
                transaction.setNote(rs.getString("Note"));
                transaction.setQuantity(rs.getInt("Quantity"));

                // Get UnitPrice (can be null)
                BigDecimal unitPrice = rs.getBigDecimal("UnitPrice");
                transaction.setUnitPrice(unitPrice);

                transaction.setWorkOrderPartId(rs.getObject("WorkOrderPartID") != null ? rs.getInt("WorkOrderPartID") : null);
                transaction.setSupplierId(rs.getObject("SupplierID") != null ? rs.getInt("SupplierID") : null);

                // Get EmployeeCode
                transaction.setEmployeeCode(rs.getString("EmployeeCode"));

                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public boolean stockIn(InventoryTransaction transaction) throws SQLException {
        String sql = "INSERT INTO InventoryTransaction (PartID, PartDetailID, TransactionType, TransactionDate, StoreKeeperID, Note, Quantity, SupplierID, UnitPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getPartId());
            pstmt.setInt(2, transaction.getPartDetailId());
            pstmt.setString(3, "IN");
            pstmt.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
            pstmt.setInt(5, transaction.getStoreKeeperId());
            pstmt.setString(6, transaction.getNote());
            pstmt.setInt(7, transaction.getQuantity());

            // SupplierID (optional)
            if (transaction.getSupplierId() != null) {
                pstmt.setInt(8, transaction.getSupplierId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            // UnitPrice (optional - purchase price at this time)
            if (transaction.getUnitPrice() != null) {
                pstmt.setBigDecimal(9, transaction.getUnitPrice());
            } else {
                pstmt.setNull(9, Types.DECIMAL);
            }
            insertIntoPartDetail(transaction.getPartDetailId(), transaction.getQuantity(), transaction.getUnitPrice());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    private void insertIntoPartDetail(int partDetailId, int quantity, BigDecimal unitPrice) throws SQLException {
        // Lấy thông tin hiện tại của PartDetail
        String selectSql = "SELECT Quantity, UnitPrice FROM PartDetail WHERE PartDetailID = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setInt(1, partDetailId);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int oldQuantity = rs.getInt("Quantity");
                BigDecimal oldUnitPrice = rs.getBigDecimal("UnitPrice");

                // Tính giá trung bình mới
                // newPrice = (oldPrice * oldQty + newPrice * newQty) / totalQty
                BigDecimal oldTotal = oldUnitPrice.multiply(BigDecimal.valueOf(oldQuantity));
                BigDecimal newTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                int totalQuantity = oldQuantity + quantity;
                BigDecimal avgUnitPrice = oldTotal.add(newTotal)
                        .divide(BigDecimal.valueOf(totalQuantity), 2, java.math.RoundingMode.HALF_UP);

                // Cập nhật Quantity và UnitPrice
                String updateSql = "UPDATE PartDetail SET Quantity = Quantity + ?, UnitPrice = ? WHERE PartDetailID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, quantity);
                    updateStmt.setBigDecimal(2, avgUnitPrice);
                    updateStmt.setInt(3, partDetailId);
                    updateStmt.executeUpdate();
                }
            }
        }
    }


    public boolean stockOut(InventoryTransaction transaction) throws SQLException {
        String sql = "INSERT INTO InventoryTransaction (PartID, PartDetailID, TransactionType, TransactionDate, StoreKeeperID, Note, Quantity, WorkOrderPartID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transaction.getPartId());
            pstmt.setInt(2, transaction.getPartDetailId());
            pstmt.setString(3, "OUT");
            pstmt.setTimestamp(4, Timestamp.valueOf(transaction.getTransactionDate()));
            pstmt.setInt(5, transaction.getStoreKeeperId());
            pstmt.setString(6, transaction.getNote());
            pstmt.setInt(7, transaction.getQuantity());
            if (transaction.getWorkOrderPartId() != null) {
                pstmt.setInt(8, transaction.getWorkOrderPartId());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

}
