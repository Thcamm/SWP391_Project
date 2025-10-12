<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo mới khách hàng</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background-color: white;
            border-radius: 12px;
            padding: 40px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .header {
            text-align: center;
            margin-bottom: 10px;
        }

        .header h1 {
            font-size: 32px;
            font-weight: 600;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
        }

        .header p {
            color: #666;
            font-size: 16px;
        }

        .section {
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            padding: 30px;
            margin-top: 30px;
        }

        .section-title {
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        label {
            font-size: 14px;
            font-weight: 500;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .required {
            color: #dc3545;
        }

        input, select, textarea {
            padding: 12px 16px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            font-size: 14px;
            background-color: #f8f9fa;
            transition: all 0.2s;
        }

        input:focus, select:focus, textarea:focus {
            outline: none;
            border-color: #007bff;
            background-color: white;
        }

        input::placeholder, textarea::placeholder {
            color: #999;
        }

        .note {
            font-size: 13px;
            color: #666;
            margin-top: 8px;
        }

        .toggle-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            margin-top: 20px;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .toggle-section:hover {
            background-color: #f8f9fa;
        }

        .toggle-content {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .toggle-title {
            font-weight: 600;
            font-size: 16px;
        }

        .toggle-subtitle {
            color: #666;
            font-size: 14px;
            margin-top: 2px;
        }

        .toggle-switch {
            width: 50px;
            height: 26px;
            background-color: #000;
            border-radius: 13px;
            position: relative;
            cursor: pointer;
        }

        .toggle-switch::after {
            content: '';
            position: absolute;
            width: 22px;
            height: 22px;
            background-color: white;
            border-radius: 50%;
            top: 2px;
            right: 2px;
            transition: transform 0.2s;
        }

        .toggle-switch.off {
            background-color: #ccc;
        }

        .toggle-switch.off::after {
            transform: translateX(-24px);
        }

        .vehicle-section {
            border: 1px solid #e0e0e0;
            border-radius: 12px;
            padding: 25px;
            margin-top: 20px;
        }

        .vehicle-header {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .add-vehicle-btn {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            padding: 12px 24px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background-color: white;
            color: #333;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            margin: 20px auto;
            transition: all 0.2s;
        }

        .add-vehicle-btn:hover {
            background-color: #f8f9fa;
            border-color: #999;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 15px;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 32px;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
            border: none;
        }

        .btn-cancel {
            background-color: white;
            color: #333;
            border: 1px solid #e0e0e0;
        }

        .btn-cancel:hover {
            background-color: #f8f9fa;
        }

        .btn-submit {
            background-color: #000;
            color: white;
        }

        .btn-submit:hover {
            background-color: #333;
        }

        .delete-vehicle-btn {
            display: flex;
            align-items: center;
            gap: 6px;
            padding: 8px 16px;
            border: 1px solid #dc3545;
            border-radius: 6px;
            background-color: white;
            color: #dc3545;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
        }

        .delete-vehicle-btn:hover {
            background-color: #dc3545;
            color: white;
        }

        .icon {
            width: 20px;
            height: 20px;
        }

        textarea {
            min-height: 100px;
            resize: vertical;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="header">
        <h1>
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
            </svg>
            Tạo mới khách hàng
        </h1>
        <p>Nhập thông tin khách hàng</p>
    </div>
    <c:if test="${not empty message}">
        <div class="alert
                ${messageType == 'success' ? 'alert-success' :
                messageType == 'warning' ? 'alert-warning' :
                'alert-danger'}" role="alert">
                ${message}
        </div>
    </c:if>
    <form action="create-customer" method="post" id="customerForm">
        <!-- Thông tin khách hàng -->
        <div class="section">
            <div class="section-title">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                </svg>
                Thông tin khách hàng
            </div>

            <div class="form-group full-width">
                <label>
                    Họ và tên
                    <span class="required">*</span>
                </label>
                <input type="text" name="fullName" placeholder="Nhập họ và tên đầy đủ" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="16" rx="2"></rect>
                            <polyline points="3 10 12 15 21 10"></polyline>
                        </svg>
                        Email
                        <span class="required">*</span>
                    </label>
                    <input type="email" name="email" placeholder="example@email.com">
                </div>

                <div class="form-group">
                    <label>
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                        </svg>
                        Số điện thoại
                    </label>
                    <input type="tel" name="phone" placeholder="0123456789">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Giới tính</label>
                    <select name="gender">
                        <option value="">Chọn giới tính</option>
                        <option value="male">Nam</option>
                        <option value="female">Nữ</option>
                        <option value="other">Khác</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                        Ngày sinh
                    </label>
                    <input type="date" name="birthDate" placeholder="dd/mm/yyyy">
                </div>
            </div>

            <div class="form-group full-width">
                <label>Địa chỉ</label>
                <textarea name="address" placeholder="Nhập địa chỉ đầy đủ"></textarea>
            </div>

            <div class="form-group full-width">
                <label>Ghi chú</label>
                <textarea name="note" placeholder="Ghi chú thêm về khách hàng"></textarea>
            </div>
        </div>

        <div class="form-actions">
            <button type="button" class="btn btn-cancel" onclick="window.history.back()">Hủy</button>
            <button type="submit" class="btn btn-submit">Tạo khách hàng</button>
        </div>
    </form>
</div>



</body>
</html>