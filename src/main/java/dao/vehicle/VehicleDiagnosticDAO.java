package dao.vehicle;

import common.DbContext;
import common.constant.MessageType;
import common.message.ServiceResult;
import common.message.SystemMessage;
import model.customer.CustomerDiagnosticsView;
import model.inventory.DiagnosticPart;
import model.vehicle.VehicleDiagnostic;
import model.vehicle.VehicleDiagnostic.DiagnosticTechnician;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class VehicleDiagnosticDAO {

    // ================================================================
    // CREATE OPERATIONS
    // ================================================================

    /**
     * Tạo bản ghi chẩn đoán mới
     */
    public int createDiagnostic(Connection conn, VehicleDiagnostic diagnostic) throws SQLException {
        String sql = "INSERT INTO VehicleDiagnostic " +
                "(AssignmentID, IssueFound, EstimateCost, Status, CreatedAt) " +
                "VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, diagnostic.getAssignmentID());
            ps.setString(2, diagnostic.getIssueFound());
            ps.setBigDecimal(3, diagnostic.getEstimateCost());

            // Use Status enum
            if (diagnostic.getStatus() != null) {
                ps.setString(4, diagnostic.getStatus().name());
            } else {
                ps.setString(4, VehicleDiagnostic.DiagnosticStatus.SUBMITTED.name());
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Thêm technician vào diagnostic (bảng VehicleDiagnosticTechnician)
     * Hỗ trợ nhiều technician cùng làm một diagnostic
     */
    public boolean addTechnicianToDiagnostic(Connection conn, int diagnosticId, int technicianId,
                                             boolean isLead, double hoursSpent) throws SQLException {
        String sql = "INSERT INTO VehicleDiagnosticTechnician " +
                "(VehicleDiagnosticID, TechnicianID, IsLead, HoursSpent, AddedAt) " +
                "VALUES (?, ?, ?, ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE " +
                "IsLead = VALUES(IsLead), HoursSpent = VALUES(HoursSpent)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, technicianId);
            ps.setBoolean(3, isLead);
            ps.setDouble(4, hoursSpent);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // UPDATE OPERATIONS
    // ================================================================

    /**
     * Cập nhật EstimateCost của diagnostic
     * Thường dùng sau khi tính tổng labor + parts cost
     */
    public boolean updateEstimateCost(Connection conn, int diagnosticId, BigDecimal totalEstimate)
            throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET EstimateCost = ? WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, totalEstimate);
            ps.setInt(2, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateDiagnosticIssueOnly(Connection c, int diagnosticId, String issueFound) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET issueFound = ? WHERE vehicleDiagnosticID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, issueFound);
            ps.setInt(2, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật issue found và estimate cost
     */
    public boolean updateDiagnostic(Connection conn, int diagnosticId,
                                    String issueFound, BigDecimal estimateCost) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic " +
                "SET IssueFound = ?, EstimateCost = ? " +
                "WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, issueFound);
            ps.setBigDecimal(2, estimateCost);
            ps.setInt(3, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật số giờ làm việc của technician trong diagnostic
     */
    public boolean updateTechnicianHours(Connection conn, int diagnosticId, int technicianId,
                                         double additionalHours) throws SQLException {
        String sql = "UPDATE VehicleDiagnosticTechnician " +
                "SET HoursSpent = HoursSpent + ? " +
                "WHERE VehicleDiagnosticID = ? AND TechnicianID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, additionalHours);
            ps.setInt(2, diagnosticId);
            ps.setInt(3, technicianId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cập nhật status của diagnostic (1=active, 0=inactive/deleted)
     */
    public boolean updateStatus(Connection conn, int diagnosticId, boolean status) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET Status = ? WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }


    public int updatePartApproval(Connection c, int diagnosticPartId, boolean approved) throws SQLException {
        String sql = "UPDATE DiagnosticPart SET IsApproved = ? WHERE DiagnosticPartID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, approved);
            ps.setInt(2, diagnosticPartId);
            return ps.executeUpdate();
        }
    }

    public int updateStatus(Connection c, int diagnosticId, String status, String reason) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET Status = ?, RejectReason = ? WHERE VehicleDiagnosticID = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, diagnosticId);
            return ps.executeUpdate();
        }
    }


    // ================================================================
    // READ OPERATIONS - SINGLE RECORD
    // ================================================================

    /**
     * Lấy diagnostic theo ID (core fields only)
     */
    public VehicleDiagnostic getDiagnosticById(Connection conn, int diagnosticId) throws SQLException {
        String sql = "SELECT * FROM VehicleDiagnostic WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDiagnostic(rs);
                }
            }
        }
        return null;
    }

    public Integer getAssignmentIdByDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = "SELECT AssignmentID FROm VehicleDiagnostic WHERE VehicleDiagnosticID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("AssignmentID");
                }
            }
        }
        return null;
    }

    public VehicleDiagnostic getByIdWithParts(int diagnosticId) throws SQLException {
        try (Connection conn = DbContext.getConnection()) {
            VehicleDiagnostic diagnostic = getDiagnosticById(conn, diagnosticId);

            if (diagnostic != null) {
                // Load parts using getPartsForDiagnostics method
                Map<Integer, List<DiagnosticPart>> partsMap = getPartsForDiagnostics(
                        Collections.singletonList(diagnosticId)
                );

                List<DiagnosticPart> parts = partsMap.get(diagnosticId);
                if (parts != null) {
                    diagnostic.setParts(parts);
                }
            }

            return diagnostic;
        }
    }

    public boolean update(Connection conn, VehicleDiagnostic diagnostic) throws SQLException {
        String sql = "UPDATE VehicleDiagnostic SET " +
                "IssueFound = ?, " +
                "EstimateCost = ?, " +
                "Status = ? " +
                "WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, diagnostic.getIssueFound());
            ps.setBigDecimal(2, diagnostic.getEstimateCost());

            // Convert enum to String for database
            if (diagnostic.getStatus() != null) {
                ps.setString(3, diagnostic.getStatus().name());
            } else {
                ps.setString(3, VehicleDiagnostic.DiagnosticStatus.SUBMITTED.name());
            }

            ps.setInt(4, diagnostic.getVehicleDiagnosticID());

            return ps.executeUpdate() > 0;
        }
    }

    public VehicleDiagnostic getById(int diagnosticId) throws SQLException {
        try (Connection conn = DbContext.getConnection()) {
            return getDiagnosticById(conn, diagnosticId);
        }
    }
    public VehicleDiagnostic getDiagnosticWithFullInfo(Connection conn, int diagnosticId) throws SQLException {
        String sql =
                "SELECT vd.*, " +
                        "       v.VehicleID, v.LicensePlate, v.Brand, v.Model, v.YearManufacture, " +
                        "       c.CustomerID, u_cust.FullName AS CustomerName, u_cust.PhoneNumber AS CustomerPhone, u_cust.Email AS CustomerEmail, " +
                        "       ta.AssignmentID, ta.TaskDescription, ta.Status AS TaskStatus, " +
                        "       ta.AssignToTechID, u_tech.FullName AS TechnicianName, u_tech.Email AS TechnicianEmail, e.EmployeeCode, " +
                        "       wo.WorkOrderID, wo.Status AS WorkOrderStatus " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +
                        "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                        "JOIN User u_tech ON e.UserID = u_tech.UserID " +
                        "WHERE vd.VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);

                    // Set vehicle info
                    String vehicleInfo = String.format("%s - %s %s (%d)",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"),
                            rs.getInt("YearManufacture"));
                    vd.setVehicleInfo(vehicleInfo);

                    // Set technician info
                    vd.setTechnicianName(rs.getString("TechnicianName"));

                    // Load all technicians (nếu có nhiều người)
                    List<DiagnosticTechnician> technicians = getTechniciansByDiagnostic(conn, diagnosticId);
                    vd.setTechnicians(technicians);

                    return vd;
                }
            }
        }
        return null;
    }

    // ================================================================
    // READ OPERATIONS - LISTS
    // ================================================================

    /**
     * Lấy diagnostics theo AssignmentID
     */
    public List<VehicleDiagnostic> getDiagnosticsByAssignmentId(int assignmentId) throws SQLException {
        String sql = """
        SELECT vd.*, 
               v.LicensePlate, v.Brand, v.Model,
               u.FullName AS TechnicianName
        FROM VehicleDiagnostic vd
        JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID
        JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID
        JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID
        JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID
        JOIN Vehicle v ON sr.VehicleID = v.VehicleID
        JOIN Employee e ON ta.TechnicianID = e.EmployeeID
        JOIN `User` u ON e.UserID = u.UserID
        WHERE ta.AssignmentID = ?
        ORDER BY vd.CreatedAt DESC
    """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                List<VehicleDiagnostic> list = new ArrayList<>();
                while (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);
                    vd.setVehicleInfo(
                            rs.getString("LicensePlate") + " - " +
                                    rs.getString("Brand") + " " +
                                    rs.getString("Model")
                    );
                    vd.setTechnicianName(rs.getString("TechnicianName"));
                    list.add(vd);
                }
                return list;
            }
        }
    }


    /**
     * Lấy danh sách technicians tham gia diagnostic
     */
    public List<DiagnosticTechnician> getTechniciansByDiagnostic(Connection conn, int diagnosticId)
            throws SQLException {
        String sql = "SELECT " +
                "    vdt.TechnicianID, " +
                "    vdt.IsLead, " +
                "    vdt.HoursSpent, " +
                "    vdt.AddedAt, " +
                "    u.FullName AS TechnicianName, " +
                "    u.Email, " +
                "    u.PhoneNumber, " +
                "    e.EmployeeCode " +
                "FROM VehicleDiagnosticTechnician vdt " +
                "JOIN Employee e ON vdt.TechnicianID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE vdt.VehicleDiagnosticID = ? " +
                "ORDER BY vdt.IsLead DESC, vdt.AddedAt";

        List<DiagnosticTechnician> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiagnosticTechnician dt = new DiagnosticTechnician();
                    dt.setTechnicianID(rs.getInt("TechnicianID"));
                    dt.setTechnicianName(rs.getString("TechnicianName"));
                    dt.setLead(rs.getBoolean("IsLead"));
                    dt.setHoursSpent(rs.getDouble("HoursSpent"));
                    list.add(dt);
                }
            }
        }
        return list;
    }

    /**
     * Lấy diagnostics chờ approval (cho Manager review)
     */
    public List<VehicleDiagnostic> getPendingApprovalDiagnostics(Connection conn) throws SQLException {
        String sql = "SELECT " +
                "    vd.*, " +
                "    v.LicensePlate, " +
                "    v.Brand, " +
                "    v.Model, " +
                "    v.YearManufacture, " +
                "    u.FullName AS TechName, " +
                "    u.Email AS TechEmail, " +
                "    ta.AssignToTechID, " +
                "    ta.AssignedDate, " +
                "    wo.WorkOrderID, " +
                "    wo.Status AS WorkOrderStatus, " +
                "    u_cust.FullName AS CustomerName " +
                "FROM VehicleDiagnostic vd " +
                "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +
                "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                "JOIN User u ON e.UserID = u.UserID " +
                "WHERE vd.Status = 1 " +
                "  AND NOT EXISTS ( " +
                "      SELECT 1 FROM WorkOrderDetail wod2 " +
                "      WHERE wod2.diagnostic_id = vd.VehicleDiagnosticID " +
                "        AND wod2.approval_status = 'APPROVED' " +
                "  ) " +
                "ORDER BY vd.CreatedAt DESC";

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                VehicleDiagnostic vd = mapDiagnostic(rs);

                // Set vehicle info
                String vehicleInfo = String.format("%s - %s %s (%d)",
                        rs.getString("LicensePlate"),
                        rs.getString("Brand"),
                        rs.getString("Model"),
                        rs.getInt("YearManufacture"));
                vd.setVehicleInfo(vehicleInfo);

                // Set technician info
                vd.setTechnicianName(rs.getString("TechName"));

                list.add(vd);
            }
        }
        return list;
    }

    // Lấy danh sách diagnostic theo requestId (order mới → cũ)
    public List<VehicleDiagnostic> getDiagnosticsByRequest(int requestId) throws SQLException {
        String sql = """
        SELECT vd.*
        FROM VehicleDiagnostic vd
        JOIN TaskAssignment ta   ON ta.AssignmentID = vd.AssignmentID
        JOIN WorkOrderDetail wod ON wod.DetailID     = ta.DetailID
        JOIN WorkOrder wo        ON wo.WorkOrderID   = wod.WorkOrderID
        WHERE wo.RequestID = ?
        ORDER BY vd.CreatedAt DESC, vd.VehicleDiagnosticID DESC
    """;
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                List<VehicleDiagnostic> out = new ArrayList<>();
                while (rs.next()) out.add(mapDiagnostic(rs)); // dùng mapper bạn đã có
                return out;
            }
        }
    }

    // Lấy full parts của 1 diagnostic (đã có ở phía technician)
   // public List<DiagnosticPart> getPartsByDiagnostic(int diagnosticId) throws SQLException {... }

    // Kiểm tra diagnostic có thuộc requestId không (để bảo vệ URL)
    public boolean diagnosticBelongsToRequest(int diagnosticId, int requestId) throws SQLException {
        String sql = """
        SELECT 1
        FROM VehicleDiagnostic vd
        JOIN TaskAssignment ta   ON ta.AssignmentID = vd.AssignmentID
        JOIN WorkOrderDetail wod ON wod.DetailID     = ta.DetailID
        JOIN WorkOrder wo        ON wo.WorkOrderID   = wod.WorkOrderID
        WHERE vd.VehicleDiagnosticID = ? AND wo.RequestID = ?
        LIMIT 1
    """;
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    /**
     * Lấy diagnostics theo technician (technician xem lịch sử của mình)
     */
    public List<VehicleDiagnostic> getDiagnosticsByTechnician(Connection conn, int technicianId, int limit)
            throws SQLException {
        String sql = "SELECT " +
                "    vd.*, " +
                "    v.LicensePlate, " +
                "    v.Brand, " +
                "    v.Model, " +
                "    v.YearManufacture, " +
                "    ta.TaskDescription, " +
                "    ta.Status AS TaskStatus " +
                "FROM VehicleDiagnostic vd " +
                "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                "WHERE ta.AssignToTechID = ? " +
                "ORDER BY vd.CreatedAt DESC " +
                (limit > 0 ? "LIMIT ?" : "");

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            if (limit > 0) {
                ps.setInt(2, limit);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);

                    String vehicleInfo = String.format("%s - %s %s (%d)",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"),
                            rs.getInt("YearManufacture"));
                    vd.setVehicleInfo(vehicleInfo);

                    list.add(vd);
                }
            }
        }
        return list;
    }

    /**
     * Tìm kiếm diagnostics theo multiple criteria
     */
    public List<VehicleDiagnostic> searchDiagnostics(Connection conn,
                                                     String licensePlate,
                                                     int technicianId,
                                                     LocalDate fromDate,
                                                     LocalDate toDate,
                                                     Boolean status) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT " +
                        "    vd.*, " +
                        "    v.LicensePlate, " +
                        "    v.Brand, " +
                        "    v.Model, " +
                        "    v.YearManufacture, " +
                        "    u.FullName AS TechName, " +
                        "    u_cust.FullName AS CustomerName " +
                        "FROM VehicleDiagnostic vd " +
                        "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                        "JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID " +
                        "JOIN WorkOrder wo ON wod.WorkOrderID = wo.WorkOrderID " +
                        "JOIN ServiceRequest sr ON wo.RequestID = sr.RequestID " +
                        "JOIN Vehicle v ON sr.VehicleID = v.VehicleID " +
                        "JOIN Customer c ON v.CustomerID = c.CustomerID " +
                        "LEFT JOIN User u_cust ON c.UserID = u_cust.UserID " +
                        "JOIN Employee e ON ta.AssignToTechID = e.EmployeeID " +
                        "JOIN User u ON e.UserID = u.UserID " +
                        "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // Filter by license plate
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            sql.append("AND v.LicensePlate LIKE ? ");
            params.add("%" + licensePlate.trim() + "%");
        }

        // Filter by technician
        if (technicianId > 0) {
            sql.append("AND ta.AssignToTechID = ? ");
            params.add(technicianId);
        }

        // Filter by date range
        if (fromDate != null) {
            sql.append("AND DATE(vd.CreatedAt) >= ? ");
            params.add(Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append("AND DATE(vd.CreatedAt) <= ? ");
            params.add(Date.valueOf(toDate));
        }

        // Filter by status
        if (status != null) {
            sql.append("AND vd.Status = ? ");
            params.add(status);
        }

        sql.append("ORDER BY vd.CreatedAt DESC");

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VehicleDiagnostic vd = mapDiagnostic(rs);

                    String vehicleInfo = String.format("%s - %s %s (%d)",
                            rs.getString("LicensePlate"),
                            rs.getString("Brand"),
                            rs.getString("Model"),
                            rs.getInt("YearManufacture"));
                    vd.setVehicleInfo(vehicleInfo);
                    vd.setTechnicianName(rs.getString("TechName"));

                    list.add(vd);
                }
            }
        }
        return list;
    }

    // ================================================================
    // STATISTICS & COUNT OPERATIONS
    // ================================================================

    /**
     * Đếm số diagnostics theo technician
     */
    public int countDiagnosticsByTechnician(Connection conn, int technicianId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total " +
                "FROM VehicleDiagnostic vd " +
                "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                "WHERE ta.AssignToTechID = ? AND vd.Status = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    public List<VehicleDiagnostic> getByAssignmentPaged(int assignmentId, int offset, int limit) throws SQLException {
        String sql = """
                    SELECT vd.*
                    FROM VehicleDiagnostic vd
                    WHERE vd.AssignmentID = ?
                    ORDER BY vd.CreatedAt DESC, vd.VehicleDiagnosticID DESC
                    LIMIT ? OFFSET ?
                """;
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<VehicleDiagnostic> out = new ArrayList<>();
                while (rs.next())
                    out.add(mapDiagnostic(rs));
                return out;
            }
        }
    }

    public Map<Integer, List<DiagnosticPart>> getPartsForDiagnostics(List<Integer> diagIds) throws SQLException {
        Map<Integer, List<DiagnosticPart>> map = new HashMap<>();
        if (diagIds == null || diagIds.isEmpty()) {
            return map;
        }

        String placeholders = diagIds.stream().map(id -> "?").collect(Collectors.joining(", "));
        String sql = """
                SELECT dp.*, pd.SKU, p.PartName, p.PartCode, p.Category
                FROM DiagnosticPart dp
                JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID
                JOIN Part p ON p.PartID = pd.PartID
                WHERE dp.VehicleDiagnosticID IN (%s)
                ORDER BY dp.VehicleDiagnosticID, dp.DiagnosticPartID

                """.formatted(placeholders);

        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : diagIds)
                ps.setInt(idx++, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiagnosticPart d = new DiagnosticPart();
                    int diagId = rs.getInt("VehicleDiagnosticID");
                    d.setDiagnosticPartID(rs.getInt("DiagnosticPartID"));
                    d.setVehicleDiagnosticID(diagId);
                    d.setPartDetailID(rs.getInt("PartDetailID"));
                    d.setQuantityNeeded(rs.getInt("QuantityNeeded"));
                    d.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    d.setPartCondition(rs.getString("PartCondition"));
                    d.setReasonForReplacement(rs.getString("ReasonForReplacement"));
                    d.setApproved(rs.getBoolean("IsApproved"));

                    d.setSku(rs.getString("SKU"));
                    d.setPartName(rs.getString("PartName"));
                    d.setPartCode(rs.getString("PartCode"));
                    d.setCategory(rs.getString("Category"));

                    map.computeIfAbsent(diagId, k -> new ArrayList<>()).add(d);
                }
            }
        }

        return map;
    }

    public int countByAssignment(int assignmentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM VehicleDiagnostic WHERE AssignmentID = ?";
        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("total") : 0;
            }
        }
    }

    /**
     * Đếm diagnostics pending approval
     */
    public int countPendingApprovalDiagnostics(Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) AS total " +
                "FROM VehicleDiagnostic vd " +
                "WHERE vd.Status = 1 " +
                "  AND NOT EXISTS ( " +
                "      SELECT 1 FROM WorkOrderDetail wod " +
                "      WHERE wod.diagnostic_id = vd.VehicleDiagnosticID " +
                "        AND wod.approval_status = 'APPROVED' " +
                "  )";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    /**
     * Đếm diagnostics theo date range
     *
     *
     */
    public int countDiagnosticsByDateRange(Connection conn, LocalDate fromDate, LocalDate toDate)
            throws SQLException {
        String sql = "SELECT COUNT(*) AS total " +
                "FROM VehicleDiagnostic vd " +
                "WHERE vd.Status = 1 " +
                "  AND DATE(vd.CreatedAt) >= ? " +
                "  AND DATE(vd.CreatedAt) <= ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fromDate));
            ps.setDate(2, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }

    // ================================================================
    // VALIDATION & CHECK OPERATIONS
    // ================================================================

    /**
     * Kiểm tra xem một assignment đã có diagnostic chưa
     *
     */
    public boolean hasDiagnostic(Connection conn, int assignmentId) throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt " +
                "FROM VehicleDiagnostic " +
                "WHERE AssignmentID = ? AND Status = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        }
        return false;
    }

    public boolean hasAnyApprovedParts(Connection conn, int diagnosticId) throws SQLException {
        String sql = "SELECT COUNT(*) cnt FROM DiagnosticPart WHERE VehicleDiagnosticID = ? AND IsApproved = 1";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("cnt") > 0;
            }
        }
    }

    public boolean hasApprovedWorkOrderForDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = """
                SELECT 1 FROM WorkOrderDetail wod
                WHERE wod.diagnostic_id = ?
                AND wod.approval_status = 'APPROVED'
                LIMIT 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Xoá hết parts của 1 diagnostic (để re-insert theo form edit) */
    public int deletePartsByDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = "DELETE FROM DiagnosticPart WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            return ps.executeUpdate();
        }
    }

    /** Insert 1 dòng part */
    public int insertDiagnosticPart(Connection conn, DiagnosticPart p) throws SQLException {
        String sql = "INSERT INTO DiagnosticPart " +
                "(VehicleDiagnosticID, PartDetailID, QuantityNeeded, UnitPrice, PartCondition, ReasonForReplacement, IsApproved) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getVehicleDiagnosticID());
            ps.setInt(2, p.getPartDetailID());
            ps.setInt(3, p.getQuantityNeeded());
            ps.setBigDecimal(4, p.getUnitPrice());
            ps.setString(5, p.getPartCondition().name());
            ps.setString(6, p.getReasonForReplacement());
            ps.setBoolean(7, Boolean.TRUE.equals(p.isApproved())); // edit draft: thường false
            return ps.executeUpdate();
        }
    }

    public int autoRejectExpiredDiagnostics(int graceMinutes) {
        String sql = """
        UPDATE VehicleDiagnostic
        SET Status = 'REJECTED',
             RejectReason = 'Auto-rejected: Customer did not respond within 10 minutes'
        WHERE Status = 'SUBMITTED'
          AND TIMESTAMPDIFF(MINUTE, CreatedAt, NOW()) > ?
    """;

        try (Connection conn = DbContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, graceMinutes);
            int rows = ps.executeUpdate();
            System.out.println("Auto-rejected diagnostics: " + rows);
            return rows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Kiểm tra technician có quyền access diagnostic không
     */
    public boolean canTechnicianAccessDiagnostic(Connection conn, int diagnosticId, int technicianId)
            throws SQLException {
        String sql = "SELECT 1 " +
                "FROM VehicleDiagnostic vd " +
                "JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID " +
                "WHERE vd.VehicleDiagnosticID = ? AND ta.AssignToTechID = ? " +
                "UNION ALL " +
                "SELECT 1 " +
                "FROM VehicleDiagnosticTechnician vdt " +
                "WHERE vdt.VehicleDiagnosticID = ? AND vdt.TechnicianID = ? " +
                "LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, technicianId);
            ps.setInt(3, diagnosticId);
            ps.setInt(4, technicianId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ================================================================
    // WRAPPER METHODS (with auto connection management)
    // ================================================================

    /**
     * Lấy diagnostic theo ID (wrapper method - tự quản lý connection)
     */
//    public VehicleDiagnostic getById(int diagnosticId) throws SQLException {
//        try (Connection conn = DbContext.getConnection()) {
//            return getDiagnosticById(conn, diagnosticId);
//        }
//    }

    /**
     * Lấy diagnostic kèm danh sách parts (wrapper method)
     */
//    public VehicleDiagnostic getByIdWithParts(int diagnosticId) throws SQLException {
//        try (Connection conn = DbContext.getConnection()) {
//            VehicleDiagnostic diagnostic = getDiagnosticById(conn, diagnosticId);
//
//            if (diagnostic != null) {
//                // Load parts using getPartsForDiagnostics method
//                Map<Integer, List<DiagnosticPart>> partsMap = getPartsForDiagnostics(
//                        Collections.singletonList(diagnosticId)
//                );
//
//                List<DiagnosticPart> parts = partsMap.get(diagnosticId);
//                if (parts != null) {
//                    diagnostic.setParts(parts);
//                }
//            }
//
//            return diagnostic;
//        }
//    }
//
    /**
     * Update diagnostic (full update including Status enum)
     */
//    public boolean update(Connection conn, VehicleDiagnostic diagnostic) throws SQLException {
//        String sql = "UPDATE VehicleDiagnostic SET " +
//                "IssueFound = ?, " +
//                "EstimateCost = ?, " +
//                "Status = ? " +
//                "WHERE VehicleDiagnosticID = ?";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, diagnostic.getIssueFound());
//            ps.setBigDecimal(2, diagnostic.getEstimateCost());
//
//            // Convert enum to String for database
//            if (diagnostic.getStatus() != null) {
//                ps.setString(3, diagnostic.getStatus().name());
//            } else {
//                ps.setString(3, VehicleDiagnostic.DiagnosticStatus.SUBMITTED.name());
//            }
//
//            ps.setInt(4, diagnostic.getVehicleDiagnosticID());
//
//            return ps.executeUpdate() > 0;
//        }
//    }

    // ================================================================
    // DELETE OPERATIONS
    // ================================================================

    /**
     * Xóa diagnostic (soft delete - set status = 0)
     */
    public boolean deleteDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        return updateStatus(conn, diagnosticId, false);
    }

    /**
     * Xóa vĩnh viễn diagnostic (hard delete - cẩn thận!)
     * Sẽ cascade delete DiagnosticPart và VehicleDiagnosticTechnician
     */
    public boolean permanentlyDeleteDiagnostic(Connection conn, int diagnosticId) throws SQLException {
        String sql = "DELETE FROM VehicleDiagnostic WHERE VehicleDiagnosticID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Xóa technician khỏi diagnostic
     *
     */
    public boolean removeTechnicianFromDiagnostic(Connection conn, int diagnosticId, int technicianId)
            throws SQLException {
        String sql = "DELETE FROM VehicleDiagnosticTechnician " +
                "WHERE VehicleDiagnosticID = ? AND TechnicianID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticId);
            ps.setInt(2, technicianId);
            return ps.executeUpdate() > 0;
        }
    }

    // ================================================================
    // HELPER - MAPPING METHOD
    // ================================================================

    private VehicleDiagnostic mapDiagnostic(ResultSet rs) throws SQLException {
        VehicleDiagnostic vd = new VehicleDiagnostic();

        vd.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
        vd.setAssignmentID(rs.getInt("AssignmentID"));
        vd.setIssueFound(rs.getString("IssueFound"));

        BigDecimal estimateCost = rs.getBigDecimal("EstimateCost");
        vd.setEstimateCost(estimateCost != null ? estimateCost : BigDecimal.ZERO);

        // Map Status ENUM from database to Java enum
        String statusStr = rs.getString("Status");
        if (statusStr != null) {
            try {
                vd.setStatus(VehicleDiagnostic.DiagnosticStatus.valueOf(statusStr));
            } catch (IllegalArgumentException e) {
                // Default to SUBMITTED if invalid value
                vd.setStatus(VehicleDiagnostic.DiagnosticStatus.SUBMITTED);
            }
        }

        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            vd.setCreatedAt(createdAt.toLocalDateTime());
        }

        try {
            vd.setRejectReason(rs.getString("RejectReason"));
        } catch (SQLException ignore) {}

        return vd;
    }

    private static void setStatusParam(PreparedStatement ps, int idx,
                                       VehicleDiagnostic.DiagnosticStatus status) throws SQLException {
        if (status == null) ps.setNull(idx, Types.VARCHAR);
        else ps.setString(idx, status.name());
    }

    private static VehicleDiagnostic.DiagnosticStatus readStatus(ResultSet rs, String col) throws SQLException {
        String raw = rs.getString(col);
        if (raw == null) return null;
        raw = raw.trim();
        if (raw.isEmpty()) return null;

        // Map legacy values
        if ("1".equals(raw) || "true".equalsIgnoreCase(raw)) {
            return VehicleDiagnostic.DiagnosticStatus.SUBMITTED;
        }
        if ("0".equals(raw) || "false".equalsIgnoreCase(raw)) {
            return VehicleDiagnostic.DiagnosticStatus.REJECTED;
        }


        try {
            return VehicleDiagnostic.DiagnosticStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return VehicleDiagnostic.DiagnosticStatus.SUBMITTED;
        }
    }

    public boolean hasSubmittedWithPendingParts(int assignmentId) {
        final String sql =
                "SELECT 1 " +
                        "FROM VehicleDiagnostic vd " +
                        "LEFT JOIN DiagnosticPart dp ON dp.VehicleDiagnosticID = vd.VehicleDiagnosticID " +
                        "WHERE vd.AssignmentID = ? " +
                        "  AND vd.Status = 'SUBMITTED' " +                   // đã submit cho khách
                        "  AND (dp.IsApproved = 0 OR dp.IsApproved IS NULL) " + // còn pending
                        "LIMIT 1";

        try (Connection c = DbContext.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return true;
        }
    }

    public List<DiagnosticPart> getPartsByDiagnostic(int diagnosticId) throws SQLException {

        Map<Integer, List<DiagnosticPart>> map =
                getPartsForDiagnostics(java.util.List.of(diagnosticId));

        return map.getOrDefault(diagnosticId, java.util.Collections.emptyList());
    }

    public ServiceResult approveDiagnosticManual(int diagnosticId) throws SQLException{
        String selectSQL = """
        SELECT vd.AssignmentID, vd.IssueFound, vd.EstimateCost,
               ta.DetailID AS SourceDetailID, wod.WorkOrderID
        FROM VehicleDiagnostic vd
        JOIN TaskAssignment ta ON vd.AssignmentID = ta.AssignmentID
        JOIN WorkOrderDetail wod ON ta.DetailID = wod.DetailID
        WHERE vd.VehicleDiagnosticID = ?
    """;

        String sumAllPartsSQL = """
        SELECT COALESCE(SUM(UnitPrice * QuantityNeeded), 0)
        FROM DiagnosticPart WHERE VehicleDiagnosticID = ?
    """;

        String sumApprovedPartsSQL = """
        SELECT COALESCE(SUM(UnitPrice * QuantityNeeded), 0)
        FROM DiagnosticPart WHERE VehicleDiagnosticID = ? AND IsApproved = 1
    """;

        String insertDetailSQL = """
        INSERT IGNORE INTO WorkOrderDetail
          (WorkOrderID, source, diagnostic_id, approval_status, TaskDescription, EstimateAmount)
        VALUES (?, 'DIAGNOSTIC', ?, 'APPROVED', ?, ?)
    """;

        String selectExistingDetailSQL = """
        SELECT DetailID FROM WorkOrderDetail
        WHERE diagnostic_id = ? LIMIT 1
    """;

        String updateDetailEstimateSQL = """
        UPDATE WorkOrderDetail
        SET EstimateAmount = ?
        WHERE DetailID = ?
    """;

        String insertPartSQL = """
        INSERT IGNORE INTO WorkOrderPart
          (DetailID, PartDetailID, DiagnosticPartID, RequestedByID,
           QuantityUsed, UnitPrice, request_status, requested_at)
        SELECT ?, dp.PartDetailID, dp.DiagnosticPartID, wo.TechManagerID,
               dp.QuantityNeeded, dp.UnitPrice, 'PENDING', NOW()
        FROM DiagnosticPart dp
        JOIN WorkOrderDetail wod_src ON wod_src.DetailID = ?
        JOIN WorkOrder wo ON wo.WorkOrderID = wod_src.WorkOrderID
        WHERE dp.VehicleDiagnosticID = ?
          AND dp.IsApproved = 1
    """;

        try (Connection conn = DbContext.getConnection(false)) {
            conn.setAutoCommit(false);

            int workOrderId, sourceDetailId;
            String issue;
            BigDecimal estimateCost;

            // 1) Lấy data gốc
            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setInt(1, diagnosticId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        DbContext.rollback(conn);
                        return ServiceResult.error(new SystemMessage(
                                "DIAG404", MessageType.ERROR, "Diagnostic",
                                "Không tìm thấy diagnostic."
                        ));
                    }
                    workOrderId     = rs.getInt("WorkOrderID");
                    sourceDetailId  = rs.getInt("SourceDetailID");
                    issue           = rs.getString("IssueFound");
                    estimateCost    = rs.getBigDecimal("EstimateCost");
                    if (estimateCost == null) estimateCost = BigDecimal.ZERO;
                }
            }

            // 2) Tính lại estimate theo tick của KH
            BigDecimal allPartsSum, approvedPartsSum;
            try (PreparedStatement ps = conn.prepareStatement(sumAllPartsSQL)) {
                ps.setInt(1, diagnosticId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    allPartsSum = rs.getBigDecimal(1);
                    if (allPartsSum == null) allPartsSum = BigDecimal.ZERO;
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sumApprovedPartsSQL)) {
                ps.setInt(1, diagnosticId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    approvedPartsSum = rs.getBigDecimal(1);
                    if (approvedPartsSum == null) approvedPartsSum = BigDecimal.ZERO;
                }
            }

            BigDecimal laborCost = estimateCost.subtract(allPartsSum);
            if (laborCost.compareTo(BigDecimal.ZERO) < 0) laborCost = BigDecimal.ZERO;

            BigDecimal newEstimate = laborCost.add(approvedPartsSum);

            // 3) Ghi WorkOrderDetail (EstimateAmount = newEstimate)
            int detailId;
            try (PreparedStatement ps = conn.prepareStatement(insertDetailSQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, workOrderId);
                ps.setInt(2, diagnosticId);
                ps.setString(3, issue);
                ps.setBigDecimal(4, newEstimate);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        detailId = keys.getInt(1);
                    } else {
                        // đã tồn tại -> lấy DetailID & update EstimateAmount cho đúng newEstimate
                        try (PreparedStatement ps2 = conn.prepareStatement(selectExistingDetailSQL)) {
                            ps2.setInt(1, diagnosticId);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (!rs2.next()) {
                                    DbContext.rollback(conn);
                                    return ServiceResult.error(new SystemMessage(
                                            "WOD404", MessageType.ERROR, "WorkOrderDetail",
                                            "Không thể tạo/lấy WorkOrderDetail."
                                    ));
                                }
                                detailId = rs2.getInt(1);
                            }
                        }
                        try (PreparedStatement ps3 = conn.prepareStatement(updateDetailEstimateSQL)) {
                            ps3.setBigDecimal(1, newEstimate);
                            ps3.setInt(2, detailId);
                            ps3.executeUpdate();
                        }
                    }
                }
            }

            // 4) Insert các WorkOrderPart chỉ cho part được KH duyệt
            try (PreparedStatement ps = conn.prepareStatement(insertPartSQL)) {
                ps.setInt(1, detailId);
                ps.setInt(2, sourceDetailId);
                ps.setInt(3, diagnosticId);
                ps.executeUpdate();
            }

            // 5) Update lại EstimateCost & Status cho VehicleDiagnostic
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE VehicleDiagnostic SET EstimateCost = ?, Status = 'APPROVED' WHERE VehicleDiagnosticID = ?")) {
                ps.setBigDecimal(1, newEstimate);
                ps.setInt(2, diagnosticId);
                ps.executeUpdate();
            }

            conn.commit();
            return ServiceResult.success(new SystemMessage(
                    "DIAG200", MessageType.SUCCESS, "Diagnostic",
                    "Đã duyệt diagnostic & tính lại chi phí theo phần KH chọn."
            ));

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(new SystemMessage(
                    "DIAG500", MessageType.ERROR, "Database", e.getMessage()
            ));
        }
    }


    public ServiceResult rejectDiagnosticManual(int diagnosticId, String reason) throws SQLException {
        String selectSQL = """
        SELECT Status FROM VehicleDiagnostic
        WHERE VehicleDiagnosticID = ?
    """;

        String updateSQL = """
        UPDATE VehicleDiagnostic
        SET Status = 'REJECTED', RejectReason = ?
        WHERE VehicleDiagnosticID = ?
    """;

        try (Connection conn = DbContext.getConnection()) {

            // 1️⃣ Kiểm tra diagnostic có tồn tại không
            try (PreparedStatement ps = conn.prepareStatement(selectSQL)) {
                ps.setInt(1, diagnosticId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return ServiceResult.error(new SystemMessage(
                                "DIAG404", MessageType.ERROR, "Diagnostic",
                                "Không tìm thấy diagnostic để từ chối."
                        ));
                    }

                    String currentStatus = rs.getString("Status");
                    if ("APPROVED".equalsIgnoreCase(currentStatus)) {
                        return ServiceResult.error(new SystemMessage(
                                "DIAG406", MessageType.WARNING, "Diagnostic",
                                "Diagnostic đã được duyệt, không thể từ chối."
                        ));
                    }
                    if ("REJECTED".equalsIgnoreCase(currentStatus)) {
                        return ServiceResult.error(new SystemMessage(
                                "DIAG407", MessageType.INFO, "Diagnostic",
                                "Diagnostic này đã bị từ chối trước đó."
                        ));
                    }
                }
            }

            // 2️⃣ Chuẩn hóa lý do
            if (reason == null || reason.trim().isEmpty()) {
                reason = "Khách hàng đã từ chối chẩn đoán.";
            }

            // 3️⃣ Cập nhật trạng thái và lý do
            try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {
                ps.setString(1, reason);
                ps.setInt(2, diagnosticId);
                int updated = ps.executeUpdate();

                if (updated == 0) {
                    return ServiceResult.error(new SystemMessage(
                            "DIAG500", MessageType.ERROR, "Diagnostic",
                            "Không thể cập nhật trạng thái từ chối."
                    ));
                }
            }

            return ServiceResult.success(new SystemMessage(
                    "DIAG200", MessageType.SUCCESS, "Diagnostic",
                    "Đã từ chối diagnostic thành công."
            ));

        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.error(new SystemMessage(
                    "DIAG_SQL_ERR", MessageType.ERROR, "Database",
                    e.getMessage()
            ));
        }
    }

    public Integer getDiagnosticIdByPartId(Connection conn, int diagnosticPartId) throws SQLException{
        String sql = """
        SELECT VehicleDiagnosticID
        FROM DiagnosticPart
        WHERE DiagnosticPartID = ?
        LIMIT 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, diagnosticPartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("VehicleDiagnosticID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // không tìm thấy hoặc lỗi
    }

    public CustomerDiagnosticsView getRequestDiagnosticsTree(int requestId) throws SQLException{
        CustomerDiagnosticsView view = new CustomerDiagnosticsView();

        try (Connection c = DbContext.getConnection()) {

            if (!loadRequestAndVehicle(c, requestId, view)) {
                return null; //
            }

            loadRequestedServices(c, requestId, view);
            loadServiceBlocks(c, view);

            loadDiagnosticsForBlocks(c, view);

            return view;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadRequestedServices(Connection c, int requestId, CustomerDiagnosticsView view)  throws SQLException {

        final String sql = """
        SELECT st.ServiceID, st.ServiceName, st.Category, st.UnitPrice
        FROM ServiceRequestDetail srd
        JOIN Service_Type st ON st.ServiceID = srd.ServiceID
        WHERE srd.RequestID = ?
    """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var info = new CustomerDiagnosticsView.ServiceTypeInfo();
                    info.serviceId   = rs.getInt("ServiceID");
                    info.serviceName = rs.getString("ServiceName");
                    info.category    = rs.getString("Category");
                    info.unitPrice   = rs.getDouble("UnitPrice");
                    view.requestedServices.add(info);
                }
            }
        }
    }

    private boolean loadRequestAndVehicle(Connection c, int requestId, CustomerDiagnosticsView view)
            throws SQLException {

        final String sql = """
        SELECT 
            sr.RequestID, sr.RequestDate, sr.Status AS RequestStatus, sr.Note,
            wo.WorkOrderID,
            c.CustomerID,
            v.VehicleID, v.LicensePlate, v.Brand, v.Model, v.YearManufacture
        FROM ServiceRequest sr
        JOIN WorkOrder wo ON wo.RequestID = sr.RequestID
        JOIN Customer c ON c.CustomerID = sr.CustomerID
        JOIN Vehicle v ON v.VehicleID = sr.VehicleID
        WHERE sr.RequestID = ?
    """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;

                // Fill view
                view.serviceRequestId = rs.getInt("RequestID");
                view.workOrderId      = rs.getInt("WorkOrderID");
                view.customerId       = rs.getInt("CustomerID");

                // Request info
                var ri = new CustomerDiagnosticsView.RequestInfo();
                ri.requestId   = rs.getInt("RequestID");
                ri.requestDate = rs.getTimestamp("RequestDate").toLocalDateTime();
                ri.status      = rs.getString("RequestStatus");
                ri.note        = rs.getString("Note");
                view.request   = ri;

                // Vehicle info
                var vi = new CustomerDiagnosticsView.VehicleInfo();
                vi.vehicleId       = rs.getInt("VehicleID");
                vi.licensePlate    = rs.getString("LicensePlate");
                vi.brand           = rs.getString("Brand");
                vi.model           = rs.getString("Model");
                vi.yearManufacture = rs.getInt("YearManufacture");
                view.vehicle = vi;

                return true;
            }
        }
    }

    private void loadServiceBlocks(Connection c, CustomerDiagnosticsView view)
            throws SQLException {

        final String sql = """
        SELECT DetailID, TaskDescription
        FROM WorkOrderDetail
        WHERE WorkOrderID = ?
        ORDER BY DetailID
    """;

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, view.workOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var block = new CustomerDiagnosticsView.ServiceBlock();
                    block.detailId     = rs.getInt("DetailID");
                    block.serviceLabel = rs.getString("TaskDescription"); // fallback label

                    view.services.add(block);
                }
            }
        }
    }


    private void loadDiagnosticsForBlocks(Connection c, CustomerDiagnosticsView view)
            throws SQLException {

        for (var block : view.services) {

            // Lấy assignment trước
            List<Integer> assignments = loadAssignments(c, block.detailId);
            for (int assignmentId : assignments) {

                // Lấy diagnostic của assignment
                List<VehicleDiagnostic> diagnostics = loadDiagnostics(c, assignmentId);

                for (var vd : diagnostics) {

                    var row = new CustomerDiagnosticsView.DiagnosticRow();
                    row.diagnostic = vd;

                    // Lấy parts của từng diagnostic
                    row.parts = loadDiagnosticParts(c, vd.getVehicleDiagnosticID());

                    block.diagnostics.add(row);
                }
            }
        }
    }



    private List<Integer> loadAssignments(Connection c, int detailId) throws SQLException {

        final String sql = """
        SELECT AssignmentID
        FROM TaskAssignment
        WHERE DetailID = ?
    """;

        List<Integer> list = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, detailId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("AssignmentID"));
                }
            }
        }
        return list;
    }


    private List<VehicleDiagnostic> loadDiagnostics(Connection c, int assignmentId)
            throws SQLException {

        final String sql = """
        SELECT *
        FROM VehicleDiagnostic
        WHERE AssignmentID = ?
    """;

        List<VehicleDiagnostic> list = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVehicleDiagnostic(rs));
                }
            }
        }
        return list;
    }



    private VehicleDiagnostic mapVehicleDiagnostic(ResultSet rs) throws SQLException {
        var vd = new VehicleDiagnostic();
        vd.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
        vd.setAssignmentID(rs.getInt("AssignmentID"));
        vd.setIssueFound(rs.getString("IssueFound"));
        vd.setEstimateCost(rs.getBigDecimal("EstimateCost"));
        vd.setStatusString(rs.getString("Status"));
        vd.setRejectReason(rs.getString("RejectReason"));
        vd.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        return vd;
    }

    private List<DiagnosticPart> loadDiagnosticParts(Connection c, int vdId)
            throws SQLException {

        final String sql = """
        SELECT 
            dp.DiagnosticPartID,
            dp.VehicleDiagnosticID,
            dp.PartDetailID,
            dp.QuantityNeeded,
            dp.UnitPrice,
            dp.PartCondition,
            dp.ReasonForReplacement,
            dp.IsApproved,

            pd.SKU,
            pd.Quantity AS CurrentStock,

            p.PartCode,
            p.PartName,
            p.Category

        FROM DiagnosticPart dp
        JOIN PartDetail pd ON dp.PartDetailID = pd.PartDetailID
        JOIN Part p        ON pd.PartID        = p.PartID
        WHERE dp.VehicleDiagnosticID = ?
        ORDER BY dp.DiagnosticPartID
    """;

        List<DiagnosticPart> list = new ArrayList<>();

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vdId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DiagnosticPart p = new DiagnosticPart();

                    // bảng DiagnosticPart
                    p.setDiagnosticPartID(rs.getInt("DiagnosticPartID"));
                    p.setVehicleDiagnosticID(rs.getInt("VehicleDiagnosticID"));
                    p.setPartDetailID(rs.getInt("PartDetailID"));
                    p.setQuantityNeeded(rs.getInt("QuantityNeeded"));
                    p.setUnitPrice(rs.getBigDecimal("UnitPrice"));
                    p.setPartCondition(rs.getString("PartCondition"));
                    p.setReasonForReplacement(rs.getString("ReasonForReplacement"));
                    p.setApproved(rs.getBoolean("IsApproved"));

                    // bảng PartDetail
                    p.setSku(rs.getString("SKU"));
                    p.setCurrentStock(rs.getInt("CurrentStock"));

                    // bảng Part
                    p.setPartCode(rs.getString("PartCode"));
                    p.setPartName(rs.getString("PartName"));
                    p.setCategory(rs.getString("Category"));

                    list.add(p);
                }
            }
        }

        return list;
    }






}