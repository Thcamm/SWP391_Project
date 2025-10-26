package dao.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.sql.Statement;

import common.DbContext;
import model.employee.Employee;

public class EmployeeDAO extends DbContext {

    /**
     * Create new employee record
     */
    public boolean createEmployee(Employee employee) throws SQLException {
        // CHANGED: Thêm cột CreatedAt vào câu lệnh SQL
        String sql = "INSERT INTO Employee (UserID, EmployeeCode, Salary, ManagedBy, CreatedBy, CreatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, employee.getUserId());
            ps.setString(2, employee.getEmployeeCode());

            // CHANGED: double là kiểu nguyên thủy, không cần kiểm tra null
            ps.setDouble(3, employee.getSalary());

            // CHANGED: int là kiểu nguyên thủy, không cần kiểm tra null.
            // Giả sử giá trị 0 có thể đại diện cho null trong logic ứng dụng nếu cần.
            ps.setInt(4, employee.getManagedBy());
            ps.setInt(5, employee.getCreateBy());

            // ADDED: Thêm giá trị cho cột CreatedAt
            if (employee.getCreateAt() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(employee.getCreateAt()));
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now())); // Hoặc giá trị mặc định
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmployeeId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Employee created successfully - ID: " + employee.getEmployeeId() +
                        ", Code: " + employee.getEmployeeCode());
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error creating employee: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Get employee by UserID
     */
    public Employee getEmployeeByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE UserID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get employee by EmployeeID
     */
    public Employee getEmployeeById(int employeeId) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE EmployeeID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, employeeId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractEmployee(rs);
                }
            }
        }
        return null;
    }

    /**
     * Update employee information
     */
    public boolean updateEmployee(Employee employee) throws SQLException {
        // Lưu ý: Thường không cập nhật CreatedAt
        String sql = "UPDATE Employee SET EmployeeCode=?, Salary=?, ManagedBy=? WHERE EmployeeID=?";
        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, employee.getEmployeeCode());

            // CHANGED: double là kiểu nguyên thủy, không cần kiểm tra null
            ps.setDouble(2, employee.getSalary());

            // CHANGED: int là kiểu nguyên thủy, không cần kiểm tra null
            ps.setInt(3, employee.getManagedBy());

            ps.setInt(4, employee.getEmployeeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Generate unique employee code based on role
     */
    public String generateEmployeeCode(String roleName, int userId) {
        String prefix;

        switch (roleName.toLowerCase()) {
            case "admin":
                prefix = "ADM";
                break;
            case "tech manager":
                prefix = "TM";
                break;
            case "technician":
                prefix = "TECH";
                break;
            case "accountant":
                prefix = "ACC";
                break;
            case "storekeeper":
                prefix = "STORE";
                break;
            default:
                prefix = "EMP";
                break;
        }

        // Generate code: PREFIX + UserID padded to 4 digits
        return prefix + String.format("%04d", userId);
    }

    /**
     * Check if user already has employee record
     */
    public boolean hasEmployeeRecord(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Employee WHERE UserID = ?";

        try (Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Extract Employee object from ResultSet
     */
    private Employee extractEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("EmployeeID"));
        employee.setUserId(rs.getInt("UserID"));
        employee.setEmployeeCode(rs.getString("EmployeeCode"));

        double salary = rs.getDouble("Salary");
        employee.setSalary(salary);

        int managedBy = rs.getInt("ManagedBy");
        employee.setManagedBy(rs.wasNull() ? null : managedBy);

        int createdBy = rs.getInt("CreatedBy");
        employee.setCreateBy(rs.wasNull() ? null : createdBy);

        return employee;
    }
}