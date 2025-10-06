<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo mới khách hàng</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        h2 {
            margin-bottom: 10px;
        }
        label {
            display: block;
            margin-top: 10px;
        }
        input, select, textarea {
            width: 100%;
            padding: 6px;
            margin-top: 4px;
        }
        .vehicle {
            border: 1px solid #ccc;
            padding: 10px;
            margin-top: 10px;
        }
        button {
            margin-top: 10px;
            padding: 6px 12px;
            cursor: pointer;
        }
        .message {
            padding: 10px;
            border-radius: 6px;
            margin-bottom: 15px;
            font-weight: bold;
        }
        .success {
            background-color: #e0f7e9;
            color: #2d7a3e;
            border: 1px solid #7ed68a;
        }
        .error {
            background-color: #fdecea;
            color: #b71c1c;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>

<h2>Tạo mới khách hàng</h2>

<%-- Hiển thị thông báo (nếu có) --%>
<%
    String message = (String) request.getAttribute("message");
    if (message != null) {
        String messageType = (String) request.getAttribute("messageType");
%>
<div class="message <%= "success".equals(messageType) ? "success" : "error" %>">
    <%= message %>
</div>
<%
    }
%>

<form id="customerForm" action="create-customer" method="post">
    <label>Họ và tên *</label>
    <input type="text" name="fullName" required>

    <label>Email</label>
    <input type="email" name="email">

    <label>Số điện thoại</label>
    <input type="tel" name="phone">

    <label>Giới tính</label>
    <select name="gender">
        <option value="">-- Chọn --</option>
        <option value="male">Nam</option>
        <option value="female">Nữ</option>
    </select>

    <label>Ngày sinh</label>
    <input type="date" name="birthDate">

    <label>Địa chỉ</label>
    <textarea name="address"></textarea>

    <hr>
    <label>
        <input type="checkbox" id="toggleVehicle"> Thêm thông tin xe
    </label>

    <div id="vehicleSection" style="display:none;">
        <div id="vehicleList"></div>
        <button type="button" onclick="addVehicle()">+ Thêm xe</button>
    </div>

    <hr>
    <button type="submit">Tạo khách hàng</button>
</form>

<script>
    const toggle = document.getElementById("toggleVehicle");
    const vehicleSection = document.getElementById("vehicleSection");
    const vehicleList = document.getElementById("vehicleList");

    toggle.addEventListener("change", () => {
        vehicleSection.style.display = toggle.checked ? "block" : "none";
    });

    function addVehicle() {
        const vehicleDiv = document.createElement("div");
        vehicleDiv.className = "vehicle";
        vehicleDiv.innerHTML = `
        <label>Biển số xe *</label>
        <input type="text" name="licensePlate[]" required>
        <label>Loại xe *</label>
        <input type="text" name="vehicleType[]" required>
        <label>Mẫu xe</label>
        <input type="text" name="vehicleModel[]">
        <label>Năm sản xuất</label>
        <input type="number" name="yearOfManufacture[]" min="1900" max="2025">
        <label>Màu xe</label>
        <input type="text" name="vehicleColor[]">
        <button type="button" onclick="this.parentElement.remove()">Xóa xe</button>
      `;
        vehicleList.appendChild(vehicleDiv);
    }

    document.getElementById("customerForm").addEventListener("submit", function(e) {
        const email = this.email.value.trim();
        const phone = this.phone.value.trim();
        if (!email && !phone) {
            e.preventDefault();
            alert("Vui lòng nhập ít nhất Email hoặc Số điện thoại!");
        }
    });
</script>

</body>
</html>
