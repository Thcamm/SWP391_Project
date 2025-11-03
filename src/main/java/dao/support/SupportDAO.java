package dao.support;

import common.DbContext;
import model.support.SupportCategory;
import model.support.SupportFAQ;
import model.support.SupportRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportDAO extends DbContext {


        public List<SupportFAQ> getAllFAQs() throws SQLException {
            List<SupportFAQ> list = new ArrayList<>();
            String sql = "SELECT * FROM SupportFAQ WHERE IsActive = 1  ORDER BY FAQID DESC";
            try (
                 PreparedStatement ps = getConnection().prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupportFAQ(
                            rs.getInt("FAQID"),
                            rs.getString("Question"),
                            rs.getString("Answer")
                    ));
                }
            }
            return list;
        }

    public List<SupportFAQ> searchFAQs(String keyword) throws SQLException {
        List<SupportFAQ> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT FAQID, Question, Answer " +
                        "FROM SupportFAQ "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (hasKeyword) {
            sql.append("WHERE LOWER(Question) LIKE ? ");
        }

        sql.append("ORDER BY FAQID DESC");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            if (hasKeyword) {
                String kw = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(1, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new SupportFAQ(
                            rs.getInt("FAQID"),
                            rs.getString("Question"),
                            rs.getString("Answer")
                    ));
                }
            }
        }

        return list;
    }


    public SupportFAQ getFAQById(int id) throws SQLException {
            String sql = "SELECT * FROM SupportFAQ WHERE FAQID = ?";
            try (
                 PreparedStatement ps = getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new SupportFAQ(
                                rs.getInt("FAQID"),
                                rs.getString("Question"),
                                rs.getString("Answer")
                        );
                    }
                }
            }
            return null;
        }

    public List<SupportCategory> getAllSupportCategories() throws SQLException {
        List<SupportCategory> list = new ArrayList<>();
        String sql = "SELECT CategoryID, CategoryName FROM SupportCategory ";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SupportCategory c = new SupportCategory();
                c.setCategoryId(rs.getInt("CategoryID"));
                c.setCategoryName(rs.getString("CategoryName"));
                list.add(c);
            }
        }
        return list;
    }
    public List<String> getAllStatuses() throws SQLException {
        List<String> statuses = new ArrayList<>();
        String sql = "SHOW COLUMNS FROM SupportRequest LIKE 'Status'";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String type = rs.getString("Type"); // enum('Pending','InProgress',...)
                type = type.replaceAll("^enum\\('", "")
                        .replaceAll("'\\)$", "")
                        .replace("'", "");
                String[] parts = type.split(",");
                for (String s : parts) {
                    statuses.add(s.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statuses;
    }


    // Thêm yêu cầu hỗ trợ mới
    public void insertSupportRequest(SupportRequest req) throws SQLException {
        String sql = "INSERT INTO SupportRequest (CustomerID, WorkOrderID,AppointmentID, CategoryID, Description, AttachmentPath, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'Pending')";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, req.getCustomerId());
            if (req.getWorkOrderId() != null)
                ps.setInt(2, req.getWorkOrderId());
            else
                ps.setNull(2, Types.INTEGER);
            if (req.getAppointmentId() != null)
                ps.setInt(3, req.getWorkOrderId());
            else
                ps.setNull(3, Types.INTEGER);
            ps.setInt(4,req.getCategoryId());
            ps.setString(5, req.getDescription());
            ps.setString(6, req.getAttachmentPath());
            ps.executeUpdate();
        }
    }
    public List<SupportRequest> getFilteredSupportRequests(
            Integer categoryId,
            String[] statuses,
            String fromDate,
            String toDate,
            String sortOrder
    ) throws SQLException {

        List<SupportRequest> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM SupportRequest WHERE 1=1 "
        );

        if (categoryId != null) {
            sql.append(" AND CategoryID = ? ");
        }
        if (statuses != null && statuses.length > 0) {
            sql.append(" AND Status IN (");
            for (int i = 0; i < statuses.length; i++) {
                sql.append("?");
                if (i < statuses.length - 1) sql.append(",");
            }
            sql.append(")");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND CreatedAt >= ? ");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND CreatedAt <= ? ");
        }

        if ("oldest".equalsIgnoreCase(sortOrder)) {
            sql.append(" ORDER BY CreatedAt ASC");
        } else {
            sql.append(" ORDER BY CreatedAt DESC");
        }

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            if (categoryId != null) {
                ps.setInt(index++, categoryId);
            }
            if (statuses != null && statuses.length > 0) {
                for (String s : statuses) {
                    ps.setString(index++, s);
                }
            }
            if (fromDate != null && !fromDate.isEmpty()) {
                ps.setString(index++, fromDate + " 00:00:00");
            }
            if (toDate != null && !toDate.isEmpty()) {
                ps.setString(index++, toDate + " 23:59:59");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportRequest sr = new SupportRequest();
                    sr.setRequestId(rs.getInt("RequestID"));
                    sr.setCustomerId(rs.getInt("CustomerID"));
                    sr.setCategoryId(rs.getInt("CategoryID"));
                    sr.setStatus(rs.getString("Status"));
                    sr.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    sr.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
                    list.add(sr);
                }
            }
        }
        return list;
    }

    public List<SupportRequest> getAllSupportRequests() throws SQLException {
        List<SupportRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM SupportRequest order by createdAt desc ";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){
            while (rs.next()) {
                SupportRequest sr = new SupportRequest();
                sr.setRequestId(rs.getInt("RequestID"));
                sr.setCustomerId(rs.getInt("CustomerID"));
                sr.setCategoryId(rs.getInt("CategoryID"));
                sr.setStatus(rs.getString("Status"));
                sr.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                sr.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());

                list.add(sr);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void updateSupportRequestStatus(int requestId, String newStatus) throws SQLException {
        String sql = "UPDATE SupportRequest SET Status = ?, UpdatedAt = NOW() WHERE RequestId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    public String getCurrentStatus(int requestId) throws SQLException {
        String sql = "SELECT Status FROM SupportRequest WHERE RequestId = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Status");
            }
        }
        return null;
    }

    public SupportRequest getSupportRequestById(int requestId) throws SQLException {
        String sql = "SELECT * FROM SupportRequest WHERE RequestID = ?";
        try (PreparedStatement st = getConnection().prepareStatement(sql)) {
            st.setInt(1, requestId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    SupportRequest sr = new SupportRequest();
                    sr.setRequestId(rs.getInt("RequestID"));
                    sr.setCustomerId(rs.getInt("CustomerID"));


                    sr.setWorkOrderId((Integer) rs.getObject("WorkOrderID"));
                    sr.setAppointmentId((Integer) rs.getObject("AppointmentID"));

                    sr.setCategoryId(rs.getInt("CategoryID"));
                    sr.setDescription(rs.getString("Description"));
                    sr.setAttachmentPath(rs.getString("AttachmentPath"));
                    sr.setStatus(rs.getString("Status"));
                    sr.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                    sr.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());

                    return sr;
                }
            }
        }
        return null;
    }



}
