package dao.inventory;

import common.DbContext;
import model.inventory.CharacteristicValue;
import model.inventory.PartDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartDAO extends DbContext {
    // Lấy tất cả PartDetail
    public List<PartDetail> getAllPartDetails() throws SQLException {
        List<PartDetail> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "ORDER BY p.PartName, pd.SKU";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                PartDetail pd = extractPartDetailFromResultSet(rs);
                list.add(pd);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private List<CharacteristicValue> getCharacteristicsByPartDetailId(Integer partDetailId) throws SQLException {
        List<CharacteristicValue> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT cv.*, ct.Name AS TypeName " +
                "FROM PartDetail_Characteristic pdc " +
                "INNER JOIN CharacteristicValue cv ON pdc.ValueID = cv.ValueID " +
                "INNER JOIN CharacteristicType ct ON cv.TypeID = ct.TypeID " +
                "WHERE pdc.PartDetailID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, partDetailId);
            rs = ps.executeQuery();

            while (rs.next()) {
                CharacteristicValue cv = new CharacteristicValue();
                cv.setValueId(rs.getInt("ValueID"));
                cv.setTypeId(rs.getInt("TypeID"));
                cv.setValueName(rs.getString("ValueName"));
                cv.setTypeName(rs.getString("TypeName"));
                list.add(cv);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public PartDetail getById(Integer id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.PartDetailID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                PartDetail pd = extractPartDetailFromResultSet(rs);
                pd.setCharacteristics(getCharacteristicsByPartDetailId(id));
                return pd;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public PartDetail getBySku(String sku) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.SKU = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, sku);
            rs = ps.executeQuery();

            if (rs.next()) {
                PartDetail pd = extractPartDetailFromResultSet(rs);
                pd.setCharacteristics(getCharacteristicsByPartDetailId(pd.getPartDetailId()));
                return pd;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<PartDetail> search(String keyword) throws SQLException {
        List<PartDetail> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE p.PartName LIKE ? OR p.PartCode LIKE ? OR pd.SKU LIKE ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            rs = ps.executeQuery();

            while (rs.next()) {
                PartDetail pd = extractPartDetailFromResultSet(rs);
                list.add(pd);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public List<PartDetail> getLowStockItems() throws SQLException {
        List<PartDetail> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.Quantity <= pd.MinStock " +
                "ORDER BY pd.Quantity ASC";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                PartDetail pd = extractPartDetailFromResultSet(rs);
                list.add(pd);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean insert(PartDetail partDetail) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "INSERT INTO PartDetail (PartID, SKU, Quantity, MinStock, UnitPrice, Location) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, partDetail.getPartId());
            ps.setString(2, partDetail.getSku());
            ps.setInt(3, partDetail.getQuantity());
            ps.setInt(4, partDetail.getMinStock());
            ps.setBigDecimal(5, partDetail.getUnitPrice());
            ps.setString(6, partDetail.getLocation());

            int result = ps.executeUpdate();

            if (result > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    partDetail.setPartDetailId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    // Cập nhật
    public boolean update(PartDetail partDetail) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "UPDATE PartDetail SET PartID=?, SKU=?, Quantity=?, MinStock=?, UnitPrice=?, Location=? " +
                "WHERE PartDetailID=?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, partDetail.getPartId());
            ps.setString(2, partDetail.getSku());
            ps.setInt(3, partDetail.getQuantity());
            ps.setInt(4, partDetail.getMinStock());
            ps.setBigDecimal(5, partDetail.getUnitPrice());
            ps.setString(6, partDetail.getLocation());
            ps.setInt(7, partDetail.getPartDetailId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Cập nhật số lượng
    public boolean updateQuantity(Integer partDetailId, Integer newQuantity) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "UPDATE PartDetail SET Quantity = ? WHERE PartDetailID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, newQuantity);
            ps.setInt(2, partDetailId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Xóa
    public boolean delete(Integer id) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "DELETE FROM PartDetail WHERE PartDetailID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PartDetail> getAllWithCharacteristics() throws SQLException {
        List<PartDetail> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "ORDER BY p.PartName, pd.SKU";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                PartDetail pd = extractPartDetailFromResultSet(rs);
                pd.setCharacteristics(getCharacteristicsByPartDetailId(pd.getPartDetailId()));
                list.add(pd);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    private PartDetail extractPartDetailFromResultSet(ResultSet rs) throws SQLException {
        PartDetail pd = new PartDetail();
        pd.setPartDetailId(rs.getInt("PartDetailID"));
        pd.setPartId(rs.getInt("PartID"));
        pd.setSku(rs.getString("SKU"));
        pd.setQuantity(rs.getInt("Quantity"));
        pd.setMinStock(rs.getInt("MinStock"));
        pd.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        pd.setLocation(rs.getString("Location"));
        pd.setPartCode(rs.getString("PartCode"));
        pd.setPartName(rs.getString("PartName"));
        pd.setCategory(rs.getString("Category"));
        pd.setUnitName(rs.getString("UnitName"));
        return pd;
    }
}
