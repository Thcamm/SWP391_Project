package dao.inventory;

import common.DbContext;
import model.inventory.CharacteristicValue;
import model.inventory.PartDetail;
import model.inventory.WorkOrderPart;
import org.checkerframework.checker.units.qual.A;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static common.DbContext.getConnection;

public class PartInventoryDAO {

    public List<PartDetail> searchAvailableParts(String keyword) throws SQLException {
        String sql = "SELECT pd.PartDetailID, pd.PartID, pd.SKU, pd.Quantity, pd.MinStock, pd.UnitPrice, pd.Location, "
                +
                "       p.PartCode, p.PartName, p.Category, u.name AS UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON p.PartID = pd.PartID " +
                "JOIN Unit u ON u.unit_id = p.base_unit_id " +
                "WHERE pd.Quantity > 0 AND (" +
                "      LOWER(p.PartName) LIKE LOWER(CONCAT('%', ?, '%')) OR " +
                "      LOWER(pd.SKU)     LIKE LOWER(CONCAT('%', ?, '%')) OR " +
                "      LOWER(p.Category) LIKE LOWER(CONCAT('%', ?, '%')) OR " +
                "      LOWER(p.Description) LIKE LOWER(CONCAT('%', ?, '%'))" +
                ") " +
                "ORDER BY p.PartName ASC";

        List<PartDetail> out = new ArrayList<>();
        try (Connection c = DbContext.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++)
                ps.setString(i, keyword == null ? "" : keyword.trim());
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    PartDetail d = new PartDetail();
                    d.setPartDetailId(rs.getInt("PartDetailID"));
                    d.setPartId(rs.getInt("PartID"));
                    d.setSku(rs.getString("SKU"));
                    d.setQuantity(rs.getInt("Quantity"));
                    d.setMinStock(rs.getInt("MinStock"));
                    d.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    d.setLocation(rs.getString("Location"));
                    d.setPartCode(rs.getString("PartCode"));
                    d.setPartName(rs.getString("PartName"));
                    d.setCategory(rs.getString("Category"));
                    d.setUnitName(rs.getString("UnitName"));
                    out.add(d);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return out;
        }
    }


    public List<PartDetail> getAllAvailableParts() throws SQLException {
        return searchAvailableParts(null);
    }

    public PartDetail getPartDetailById(int partDetailId) {
        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, p.Description, "+
                "u.name as UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "LEFT JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.PartDetailID = ?";
        try (Connection conn = DbContext.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, partDetailId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    return mapResultSetToPartDetail(rs);
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return null;

    }

    public PartDetail getPartDetailBySku(String sku) {
        if(sku == null || sku.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, p.Description, "+
                "u.name as UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "LEFT JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.SKU = ?";
        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, sku.trim());
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPartDetail(rs);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;

    }

    public boolean hasEnoughQuantity(int partDetailId, Integer requestdQty) {
        if(requestdQty == null || requestdQty <= 0) {
            return false;
        }

        String sql = "SELECT Quantity FROM PartDetail WHERE PartDetailID = ?";
        try(Connection conn = DbContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, partDetailId);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int availableQty = rs.getInt("Quantity");
                    return availableQty >= requestdQty;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    public Integer getPartDetailQuantity(int partDetailId) {
        String sql = "SELECT Quantity FROM PartDetail WHERE PartDetailID = ?";
        try(Connection conn = DbContext.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setInt(1, partDetailId);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Quantity");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    public List<PartDetail> getPartsByCategory(String category) {
        List<PartDetail> parts = new ArrayList<>();

        if(category == null || category.trim().isEmpty()) {
            return parts;
        }

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, p.Description, u.name as UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "LEFT JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.Quantity > 0 AND p.Category = ? " +
                "ORDER BY p.PartName, pd.SKU";

        try(Connection conn = DbContext.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, category.trim());

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    parts.add(mapResultSetToPartDetail(rs));
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return parts;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();

        String sql = "SELECT DISTINCT Category FROM Part WHERE Category IS NOT NULL ORDER BY Category";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("Category");
                if (category != null && !category.trim().isEmpty()) {
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public List<PartDetail> getLowStockParts() {
        List<PartDetail> parts = new ArrayList<>();

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, p.Description, u.name as UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "LEFT JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.Quantity > 0 AND pd.Quantity <= pd.MinStock " +
                "ORDER BY pd.Quantity ASC, p.PartName";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                parts.add(mapResultSetToPartDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parts;
    }

    public List<PartDetail> getOutOfStockParts() {
        List<PartDetail> parts = new ArrayList<>();

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, p.Description, u.name as UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "LEFT JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.Quantity = 0 " +
                "ORDER BY p.PartName";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                parts.add(mapResultSetToPartDetail(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parts;
    }

    public List<CharacteristicValue> getPartDetailCharacteristics(int partDetailId) {
        List<CharacteristicValue> characteristics = new ArrayList<>();

        String sql = "SELECT cv.*, ct.Name AS TypeName " +
                "FROM PartDetail_Characteristic pdc " +
                "JOIN CharacteristicValue cv ON pdc.ValueID = cv.ValueID " +
                "JOIN CharacteristicType ct ON cv.TypeID = ct.TypeID " +
                "WHERE pdc.PartDetailID = ? " +
                "ORDER BY ct.Name, cv.ValueName";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partDetailId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CharacteristicValue cv = new CharacteristicValue();
                    cv.setValueId(rs.getInt("ValueID"));
                    cv.setValueName(rs.getString("ValueName"));
                    cv.setTypeId(rs.getInt("TypeID"));
                    cv.setTypeName(rs.getString("TypeName"));
                    characteristics.add(cv);

                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return characteristics;
    }

    public PartDetail getPartDetailWithCharacteristics(int partDetailId) {
        PartDetail part = getPartDetailById(partDetailId);
        if (part != null) {
            List<CharacteristicValue> characteristics = getPartDetailCharacteristics(partDetailId);
            part.setCharacteristics(characteristics);
        }
        return part;
    }

    public List<PartDetail> searchPartsWithCharacteristics(String keyword) throws SQLException {
        List<PartDetail> parts = searchAvailableParts(keyword);

        // Load characteristics for each part
        for (PartDetail part : parts) {
            List<CharacteristicValue> characteristics =
                    getPartDetailCharacteristics(part.getPartDetailId());
            part.setCharacteristics(characteristics);
        }

        return parts;
    }

    public boolean partDetailExists(int partDetailId) {
        String sql = "SELECT 1 FROM PartDetail WHERE PartDetailID = ?";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, partDetailId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<PartDetail> getPartsByLocation(String location) {
        List<PartDetail> parts = new ArrayList<>();

        if (location == null || location.trim().isEmpty()) {
            return parts;
        }

        String sql = "SELECT pd.*, p.PartCode, p.PartName, p.Category, p.Description, u.name as UnitName " +
                "FROM PartDetail pd " +
                "JOIN Part p ON pd.PartID = p.PartID " +
                "LEFT JOIN Unit u ON p.base_unit_id = u.unit_id " +
                "WHERE pd.Location = ? " +
                "ORDER BY p.PartName, pd.SKU";

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, location.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parts.add(mapResultSetToPartDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return parts;
    }

    public List<WorkOrderPart> getPartsByWorkOrderId(int workOrderId) {
        List<WorkOrderPart> parts = new ArrayList<>();
        // We JOIN WorkOrderPart (aliased 'p') with WorkOrderDetail (aliased 'd')
        // to find all parts associated with the target WorkOrderID.
        String sql = "SELECT p.* " +
                "FROM WorkOrderPart p " +
                "JOIN WorkOrderDetail d ON p.DetailID = d.DetailID " +
                "WHERE d.WorkOrderID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    parts.add(mapResultSetToWorkOrderPart(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting WorkOrderParts by WorkOrderID: " + e.getMessage());
        }
        return parts;
    }

    private WorkOrderPart mapResultSetToWorkOrderPart(ResultSet rs) throws SQLException {
        WorkOrderPart part = new WorkOrderPart();
        part.setWorkOrderPartId(rs.getInt("WorkOrderPartID"));
        part.setDetailID(rs.getInt("DetailID"));
        part.setPartDetailID(rs.getInt("PartDetailID"));
        part.setRequestByID(rs.getInt("RequestedByID"));
        part.setQuantityUsed(rs.getInt("QuantityUsed"));
        part.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        part.setRequestStatus(rs.getString("request_status"));
        part.setRequestedAt(rs.getTimestamp("requested_at").toLocalDateTime());
        return part;
    }

    private PartDetail mapResultSetToPartDetail(ResultSet rs) throws SQLException {
        PartDetail part = new PartDetail();

        // Database fields
        part.setPartDetailId(rs.getInt("PartDetailID"));
        part.setPartId(rs.getInt("PartID"));
        part.setSku(rs.getString("SKU"));

        int quantity = rs.getInt("Quantity");
        part.setQuantity(rs.wasNull() ? null : quantity);

        int minStock = rs.getInt("MinStock");
        part.setMinStock(rs.wasNull() ? null : minStock);

        part.setUnitPrice(rs.getBigDecimal("UnitPrice"));
        part.setLocation(rs.getString("Location"));

        // JOIN fields
        part.setPartCode(rs.getString("PartCode"));
        part.setPartName(rs.getString("PartName"));
        part.setCategory(rs.getString("Category"));
        part.setUnitName(rs.getString("UnitName"));

        return part;
    }
}
