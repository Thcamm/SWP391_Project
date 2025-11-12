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

    // Thêm các method sau vào class SupportFAQDAO của bạn

    public void addFAQ(SupportFAQ faq) throws SQLException {
        String sql = "INSERT INTO SupportFAQ (Question, Answer, IsActive) VALUES (?, ?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, faq.getQuestion());
            ps.setString(2, faq.getAnswer());
            ps.setBoolean(3, faq.isActive());
            ps.executeUpdate();
        }
    }

    public void updateFAQ(SupportFAQ faq) throws SQLException {
        String sql = "UPDATE SupportFAQ SET Question = ?, Answer = ?, IsActive = ? WHERE FAQID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, faq.getQuestion());
            ps.setString(2, faq.getAnswer());
            ps.setBoolean(3, faq.isActive());
            ps.setInt(4, faq.getFAQId());
            ps.executeUpdate();
        }
    }

    public void deleteFAQ(int id) throws SQLException {
        // Soft delete - set IsActive = 0
        String sql = "UPDATE SupportFAQ SET IsActive = 0 WHERE FAQID = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }

        // Nếu muốn xóa vĩnh viễn, dùng câu này thay thế:
        // String sql = "DELETE FROM SupportFAQ WHERE FAQID = ?";
    }

    public int getTotalFAQs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM SupportFAQ WHERE IsActive = 1";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<SupportFAQ> getFAQsPaginated(int page, int recordsPerPage) throws SQLException {
        List<SupportFAQ> list = new ArrayList<>();
        int offset = (page - 1) * recordsPerPage;

        String sql = "SELECT FAQID, Question, Answer " +
                "FROM SupportFAQ " +
                "WHERE IsActive = 1 " +
                "ORDER BY FAQID DESC " +
                "LIMIT ? OFFSET ?";

        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, recordsPerPage);
            ps.setInt(2, offset);

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

    public int getTotalSearchResults(String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM SupportFAQ WHERE IsActive = 1");

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append(" AND LOWER(Question) LIKE ?");
        }

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            if (hasKeyword) {
                String kw = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(1, kw);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<SupportFAQ> searchFAQsPaginated(String keyword, int page, int recordsPerPage) throws SQLException {
        List<SupportFAQ> list = new ArrayList<>();
        int offset = (page - 1) * recordsPerPage;

        StringBuilder sql = new StringBuilder(
                "SELECT FAQID, Question, Answer " +
                        "FROM SupportFAQ " +
                        "WHERE IsActive = 1 "
        );

        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        if (hasKeyword) {
            sql.append("AND LOWER(Question) LIKE ? ");
        }

        sql.append("ORDER BY FAQID DESC LIMIT ? OFFSET ?");

        try (PreparedStatement ps = getConnection().prepareStatement(sql.toString())) {
            int paramIndex = 1;

            if (hasKeyword) {
                String kw = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, kw);
            }

            ps.setInt(paramIndex++, recordsPerPage);
            ps.setInt(paramIndex, offset);

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
    public int countSupportRequests() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM SupportRequest";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
    public int countFilteredSupportRequests(
            Integer categoryId,
            String[] statuses,
            String fromDate,
            String toDate
    ) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) AS total FROM SupportRequest WHERE 1=1 "
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
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }
    public List<SupportRequest> getFilteredSupportRequests(
            Integer categoryId,
            String[] statuses,
            String fromDate,
            String toDate,
            String sortOrder,
            int limit,
            int offset
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
        sql.append(" LIMIT ? OFFSET ?");
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
            ps.setInt(index++, limit);
            ps.setInt(index, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SupportRequest sr = new SupportRequest();
                    sr.setRequestId(rs.getInt("RequestID"));
                    sr.setCustomerId(rs.getInt("CustomerID"));
                    sr.setCategoryId(rs.getInt("CategoryID"));
                    sr.setStatus(rs.getString("Status"));
                    java.sql.Timestamp createdAtTs = rs.getTimestamp("CreatedAt");
                    if (createdAtTs != null) {
                        sr.setCreatedAt(createdAtTs.toLocalDateTime());
                    } else {
                        sr.setCreatedAt(null);
                    }

                    java.sql.Timestamp updatedAtTs = rs.getTimestamp("UpdatedAt");
                    if (updatedAtTs != null) {
                        sr.setUpdatedAt(updatedAtTs.toLocalDateTime());
                    } else {
                        sr.setUpdatedAt(null);
                    }
                    list.add(sr);
                }
            }
        }
        return list;
    }

    public List<SupportRequest> getAllSupportRequestsWithLimit(int limit,int offset) throws SQLException {
        List<SupportRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM SupportRequest order by createdAt desc LIMIT ? OFFSET ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql);
        ){
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SupportRequest sr = new SupportRequest();
                sr.setRequestId(rs.getInt("RequestID"));
                sr.setCustomerId(rs.getInt("CustomerID"));
                sr.setCategoryId(rs.getInt("CategoryID"));
                sr.setStatus(rs.getString("Status"));
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                Timestamp updatedAt = rs.getTimestamp("UpdatedAt");

                sr.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
                sr.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);

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
                    Timestamp createdAt = rs.getTimestamp("CreatedAt");
                    Timestamp updatedAt = rs.getTimestamp("UpdatedAt");

                    sr.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
                    sr.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);


                    return sr;
                }
            }
        }
        return null;
    }



}
