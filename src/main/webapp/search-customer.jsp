<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tìm kiếm khách hàng</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background-color: #f5f7fa; }
        h2 { color: #2c3e50; }
        form { background: white; padding: 20px; border-radius: 8px; margin-bottom: 25px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        label { display: inline-block; width: 160px; font-weight: bold; }
        input[type="text"] { width: 250px; padding: 6px; margin: 5px 0; }
        button { padding: 8px 16px; border: none; background-color: #3498db; color: white; border-radius: 5px; cursor: pointer; }
        button:hover { background-color: #2980b9; }
        table { width: 100%; border-collapse: collapse; background: white; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        th, td { padding: 10px; border-bottom: 1px solid #ddd; text-align: left; }
        th { background-color: #3498db; color: white; }
        .toggle-btn { border: none; background: none; color: #2980b9; cursor: pointer; font-weight: bold; }
        .vehicle-row { display: none; background-color: #f9f9f9; }
        .vehicle-table { width: 95%; margin: 8px auto; border-collapse: collapse; }
        .vehicle-table th, .vehicle-table td { border: 1px solid #ccc; padding: 6px; text-align: left; }
        .no-result { color: red; font-weight: bold; padding: 8px; }
    </style>
    <script>
        function toggleVehicles(id) {
            const row = document.getElementById('vehicles-' + id);
            row.style.display = (row.style.display === 'none' || row.style.display === '') ? 'table-row' : 'none';
        }
    </script>
</head>
<body>

<h2>🔍 Tìm kiếm khách hàng</h2>

<form action="search-customer" method="get">
    <div>
        <label for="fullName">Tên khách hàng:</label>
        <input type="text" id="fullName" name="fullName" value="${param.fullName}">
    </div>
    <div>
        <label for="contact">Email hoặc SĐT:</label>
        <input type="text" id="contact" name="contact" value="${param.contact}">
    </div>
    <div>
        <label for="licensePlate">Biển số xe:</label>
        <input type="text" id="licensePlate" name="licensePlate" value="${param.licensePlate}">
    </div>
    <button type="submit">Tìm kiếm</button>
</form>

<c:if test="${not empty customerList}">
    <table>
        <thead>
        <tr>
            <th>Tên khách hàng</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Giới tính</th>
            <th>Ngày sinh</th>
            <th>Địa chỉ</th>
            <th>Xe</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="c" items="${customerList}">
            <tr>
                <td>${c.fullName}</td>
                <td>${c.email}</td>
                <td>${c.phoneNumber}</td>
                <td>${c.gender}</td>
                <td>${c.birthDate}</td>
                <td>${c.address}</td>
                <td>
                    <c:set var="count" value="${fn:length(c.vehicles)}"/>
                    <c:choose>
                        <c:when test="${count > 0}">
                            <button type="button" class="toggle-btn" onclick="toggleVehicles(${c.customerID})">
                                🚗 ${count} xe
                            </button>
                        </c:when>
                        <c:otherwise>Không có xe</c:otherwise>
                    </c:choose>
                </td>
            </tr>

            <!-- Dòng ẩn danh sách xe -->
            <tr id="vehicles-${c.customerId}" class="vehicle-row">
                <td colspan="7">
                    <table class="vehicle-table">
                        <thead>
                        <tr>
                            <th>Biển số</th>
                            <th>Hãng xe</th>
                            <th>Mẫu xe</th>
                            <th>Năm SX</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="v" items="${c.vehicles}">
                            <tr>
                                <td>${v.licensePlate}</td>
                                <td>${v.brand}</td>
                                <td>${v.model}</td>
                                <td>${v.yearManufacture}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

<c:if test="${empty customerList and param.fullName != null}">
    <div class="no-result">❌ Không tìm thấy khách hàng phù hợp.</div>
</c:if>

</body>
</html>
