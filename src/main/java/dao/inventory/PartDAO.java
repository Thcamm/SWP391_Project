package dao.inventory;

import common.DbContext;
import model.inventory.CharacteristicValue;
import model.inventory.Part;
import model.inventory.PartDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartDAO extends DbContext {
    // Get all PartDetails
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

    public List<CharacteristicValue> getCharacteristicsByPartDetailId(Integer partDetailId) throws SQLException {
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
    public Part getPartById(int partDetailId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT p.* " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "WHERE pd.PartDetailID = ?";
        Part part = new Part();
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, partDetailId);
            rs = ps.executeQuery();

            if (rs.next()) {

                part.setPartId(rs.getInt("PartID"));
                part.setPartCode(rs.getString("PartCode"));
                part.setPartName(rs.getString("PartName"));
                part.setCategory(rs.getString("Category"));
                part.setDescription(rs.getString("Description"));
                part.setBaseUnitId(rs.getInt("base_unit_id"));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return part;

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

        String sql = "INSERT INTO PartDetail (PartID, SKU, Quantity, MinStock, UnitPrice, Location, Manufacturer, Description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, partDetail.getPartId());
            ps.setString(2, partDetail.getSku());
            ps.setInt(3, partDetail.getQuantity());
            ps.setInt(4, partDetail.getMinStock());
            ps.setBigDecimal(5, partDetail.getUnitPrice());
            ps.setString(6, partDetail.getLocation());
            ps.setString(7, partDetail.getManufacturer());
            ps.setString(8, partDetail.getDescription());

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

    // Update
    public boolean update(PartDetail partDetail) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;

        String sql = "UPDATE PartDetail SET SKU=?, Location=?, MinStock=?, UnitPrice=?, Manufacturer=?, Description=? " +
                "WHERE PartDetailID=?";
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, partDetail.getSku());
            ps.setString(2, partDetail.getLocation());
            ps.setInt(3, partDetail.getMinStock());
            ps.setBigDecimal(4, partDetail.getUnitPrice());
            ps.setString(5, partDetail.getManufacturer());
            ps.setString(6, partDetail.getDescription());
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

    public List<PartDetail> getAllWithCharacteristicsByCategory(String category) throws SQLException {
        List<PartDetail> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "INNER JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE p.Category = ? " +
                "ORDER BY p.PartName, pd.SKU";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, category);
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

    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT DISTINCT Category FROM Part ORDER BY Category";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return categories;
    }

    public String getPartDetailBySku(String sku) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT p.PartName, pd.SKU, pd.Quantity, pd.UnitPrice " +
                "FROM PartDetail pd " +
                "INNER JOIN Part p ON pd.PartID = p.PartID " +
                "WHERE pd.SKU = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, sku);
            rs = ps.executeQuery();

            if (rs.next()) {
                String partName = rs.getString("PartName");
                int quantity = rs.getInt("Quantity");
                return partName + " (SKU: " + sku + ") - Quantity: " + quantity;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    //getAllCharacteristicValues
    public List<CharacteristicValue> getAllCharacteristicValues() throws SQLException {
        List<CharacteristicValue> list = new ArrayList<>();
        String sql = "SELECT cv.ValueID, cv.TypeID, cv.ValueName, ct.Name " +
                "FROM characteristicvalue cv " +
                "JOIN characteristictype ct ON cv.TypeID = ct.TypeID " +
                "ORDER BY ct.Name, cv.ValueName";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CharacteristicValue cv = new CharacteristicValue(
                        rs.getInt("ValueID"),
                        rs.getInt("TypeID"),
                        rs.getString("ValueName")
                );
                cv.setTypeName(rs.getString("Name"));
                list.add(cv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<PartDetail> searchWithFilters(String keyword, String category, String location,
                                              String stockStatus, String priceFrom, String priceTo,
                                              String manufacturer)
            throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT pd.*, p.PartName, p.Category " +
                        "FROM partdetail pd " +
                        "JOIN part p ON pd.PartID = p.PartID " +
                        "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        // Keyword filter
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(p.PartName) LIKE ? OR LOWER(pd.SKU) LIKE ?)");
            String searchPattern = "%" + keyword.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Category filter
        if (category != null && !category.isEmpty()) {
            sql.append(" AND p.Category = ?");
            params.add(category);
        }

        // Location filter
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND LOWER(pd.Location) LIKE ?");
            params.add("%" + location.toLowerCase() + "%");
        }

        // 2. BỔ SUNG KHỐI LỌC MANUFACTURER
        // (Dùng tên cột 'Manufacturer' bạn vừa thêm)
        if (manufacturer != null && !manufacturer.isEmpty()) {
            sql.append(" AND pd.Manufacturer = ?");
            params.add(manufacturer);
        }

        // Stock status filter
        if (stockStatus != null && !stockStatus.isEmpty()) {
            switch (stockStatus) {
                case "low":
                    sql.append(" AND pd.Quantity > 0 AND pd.Quantity <= pd.MinStock");
                    break;
                case "out":
                    sql.append(" AND pd.Quantity = 0");
                    break;
                case "normal":
                    sql.append(" AND pd.Quantity > pd.MinStock");
                    break;
            }
        }

        // Price range filter
        if (priceFrom != null && !priceFrom.isEmpty()) {
            sql.append(" AND pd.UnitPrice >= ?");
            params.add(new java.math.BigDecimal(priceFrom));
        }

        if (priceTo != null && !priceTo.isEmpty()) {
            sql.append(" AND pd.UnitPrice <= ?");
            params.add(new java.math.BigDecimal(priceTo));
        }

        sql.append(" ORDER BY p.PartName");

        List<PartDetail> results = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PartDetail pd = new PartDetail();

                    pd.setPartDetailId(rs.getInt("PartDetailID"));
                    pd.setPartId(rs.getInt("PartID"));
                    pd.setSku(rs.getString("SKU"));
                    pd.setQuantity(rs.getInt("Quantity"));
                    pd.setMinStock(rs.getInt("MinStock"));
                    pd.setLocation(rs.getString("Location"));
                    pd.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    pd.setPartName(rs.getString("PartName"));
                    pd.setCategory(rs.getString("Category"));

                    // 3. ĐỌC GIÁ TRỊ TỪ CỘT "Manufacturer"
                    // (Giả sử model PartDetail của bạn đã có
                    // trường 'manufacturer' và hàm 'setManufacturer()')
                    pd.setManufacturer(rs.getString("Manufacturer"));

                    results.add(pd);
                }
            }
        }

        return results;
    }

    public int countTotalPrice() throws SQLException {
        String sql = "SELECT SUM(UnitPrice * Quantity) AS TotalPrice FROM PartDetail";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("TotalPrice");
            }
        }
        return 0;
    }

    public int countLowStockItems() throws SQLException {
        String sql = "SELECT COUNT(*) AS LowStockCount FROM PartDetail WHERE Quantity <= MinStock";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("LowStockCount");
            }
        }
        return 0;
    }

    /**
     * Add new part with all related information
     * This method handles: Part, Unit, PartDetail, and Characteristics
     * Initial quantity is set to 0 by default
     */
    public boolean addNewPart(String partCode, String partName, String category, String description,
                               String sku, String location, String manufacturer, int minStock,
                               java.math.BigDecimal unitPrice, String unitName) throws SQLException {

        Connection conn = null;
        PreparedStatement psUnit = null;
        PreparedStatement psPart = null;
        PreparedStatement psPartDetail = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert or get Unit
            int unitId = -1;
            String sqlCheckUnit = "SELECT unit_id FROM Unit WHERE name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckUnit)) {
                ps.setString(1, unitName);
                rs = ps.executeQuery();
                if (rs.next()) {
                    unitId = rs.getInt("unit_id");
                } else {
                    // Insert new unit
                    String sqlInsertUnit = "INSERT INTO Unit (name) VALUES (?)";
                    psUnit = conn.prepareStatement(sqlInsertUnit, Statement.RETURN_GENERATED_KEYS);
                    psUnit.setString(1, unitName);
                    psUnit.executeUpdate();

                    ResultSet rsUnit = psUnit.getGeneratedKeys();
                    if (rsUnit.next()) {
                        unitId = rsUnit.getInt(1);
                    }
                    rsUnit.close();
                }
            }

            if (unitId == -1) {
                conn.rollback();
                return false;
            }

            // 2. Insert Part
            String sqlInsertPart = "INSERT INTO Part (PartCode, PartName, Category, Description, base_unit_id) " +
                                   "VALUES (?, ?, ?, ?, ?)";
            psPart = conn.prepareStatement(sqlInsertPart, Statement.RETURN_GENERATED_KEYS);
            psPart.setString(1, partCode);
            psPart.setString(2, partName);
            psPart.setString(3, category);
            psPart.setString(4, description);
            psPart.setInt(5, unitId);
            psPart.executeUpdate();

            ResultSet rsPart = psPart.getGeneratedKeys();
            int partId = -1;
            if (rsPart.next()) {
                partId = rsPart.getInt(1);
            }
            rsPart.close();

            if (partId == -1) {
                conn.rollback();
                return false;
            }

            // 3. Insert PartDetail with default quantity = 0
            String sqlInsertPartDetail = "INSERT INTO PartDetail (PartID, SKU, Quantity, MinStock, UnitPrice, Location, Manufacturer, Description) " +
                                         "VALUES (?, ?, 0, ?, ?, ?, ?, ?)";
            psPartDetail = conn.prepareStatement(sqlInsertPartDetail, Statement.RETURN_GENERATED_KEYS);
            psPartDetail.setInt(1, partId);
            psPartDetail.setString(2, sku);
            psPartDetail.setInt(3, minStock);
            psPartDetail.setBigDecimal(4, unitPrice);
            psPartDetail.setString(5, location);
            psPartDetail.setString(6, manufacturer);
            psPartDetail.setString(7, description);
            psPartDetail.executeUpdate();

            ResultSet rsPartDetail = psPartDetail.getGeneratedKeys();
            int partDetailId = -1;
            if (rsPartDetail.next()) {
                partDetailId = rsPartDetail.getInt(1);
            }
            rsPartDetail.close();

            if (partDetailId == -1) {
                conn.rollback();
                return false;
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
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
        pd.setManufacturer(rs.getString("Manufacturer"));
        pd.setDescription(rs.getString("Description"));
        pd.setPartCode(rs.getString("PartCode"));
        pd.setPartName(rs.getString("PartName"));
        pd.setCategory(rs.getString("Category"));
        pd.setUnitName(rs.getString("UnitName"));
        return pd;
    }

}