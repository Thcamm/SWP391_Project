<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kho Linh Kiện - Garage Inventory</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #1e3a8a 0%, #312e81 100%);
            min-height: 100vh;
            padding: 20px;
        }

        .container {
            max-width: 1600px;
            margin: 0 auto;
        }

        /* Header */
        .header {
            background: white;
            border-radius: 20px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
        }

        .header-top {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .header h1 {
            color: #1e293b;
            font-size: 36px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .header h1 i {
            color: #3b82f6;
            font-size: 40px;
        }

        .header-actions {
            display: flex;
            gap: 15px;
        }

        /* Buttons */
        .btn {
            padding: 12px 25px;
            border: none;
            border-radius: 12px;
            cursor: pointer;
            font-size: 15px;
            font-weight: 600;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 10px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.2);
        }

        .btn-primary {
            background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
            color: white;
        }

        .btn-success {
            background: linear-gradient(135deg, #10b981 0%, #059669 100%);
            color: white;
        }

        .btn-warning {
            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
            color: white;
        }

        .btn-info {
            background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
            color: white;
        }

        /* Stats Cards */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.15);
            display: flex;
            align-items: center;
            gap: 20px;
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 50px rgba(0,0,0,0.25);
        }

        .stat-icon {
            width: 70px;
            height: 70px;
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 32px;
            color: white;
        }

        .stat-icon.blue { background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); }
        .stat-icon.orange { background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); }
        .stat-icon.green { background: linear-gradient(135deg, #10b981 0%, #059669 100%); }
        .stat-icon.purple { background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); }

        .stat-info h3 {
            color: #64748b;
            font-size: 14px;
            font-weight: 600;
            margin-bottom: 8px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .stat-info .stat-value {
            color: #1e293b;
            font-size: 32px;
            font-weight: 700;
        }

        /* Search & Filter */
        .search-section {
            background: white;
            border-radius: 16px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.15);
        }

        .search-bar {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }

        .search-input-wrapper {
            flex: 1;
            position: relative;
        }

        .search-input-wrapper i {
            position: absolute;
            left: 20px;
            top: 50%;
            transform: translateY(-50%);
            color: #94a3b8;
            font-size: 18px;
        }

        .search-input {
            width: 100%;
            padding: 15px 20px 15px 55px;
            border: 2px solid #e2e8f0;
            border-radius: 12px;
            font-size: 16px;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
        }

        .filter-tabs {
            display: flex;
            gap: 10px;
        }

        .filter-tab {
            padding: 10px 20px;
            border: 2px solid #e2e8f0;
            background: white;
            border-radius: 10px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            color: #64748b;
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .filter-tab:hover, .filter-tab.active {
            background: #3b82f6;
            color: white;
            border-color: #3b82f6;
        }

        /* Inventory Table */
        .inventory-card {
            background: white;
            border-radius: 16px;
            padding: 30px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.15);
            overflow: hidden;
        }

        .table-wrapper {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
        }

        th {
            padding: 18px 20px;
            text-align: left;
            color: white;
            font-weight: 600;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        tbody tr {
            border-bottom: 1px solid #e2e8f0;
            transition: all 0.2s ease;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        td {
            padding: 20px;
            color: #334155;
            font-size: 15px;
        }

        /* Badge Styles */
        .badge {
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 13px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .badge-success {
            background: #d1fae5;
            color: #065f46;
        }

        .badge-warning {
            background: #fed7aa;
            color: #92400e;
        }

        .badge-danger {
            background: #fee2e2;
            color: #991b1b;
        }

        .badge-category {
            background: #dbeafe;
            color: #1e40af;
        }

        /* SKU Code */
        .sku-code {
            font-family: 'Courier New', monospace;
            background: #f1f5f9;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #3b82f6;
        }

        /* Stock Info */
        .stock-info {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .stock-number {
            font-size: 18px;
            font-weight: 700;
        }

        .stock-number.low {
            color: #dc2626;
        }

        .stock-number.normal {
            color: #059669;
        }

        .min-stock {
            font-size: 12px;
            color: #64748b;
        }

        /* Price */
        .price {
            font-size: 17px;
            font-weight: 700;
            color: #059669;
        }

        /* Characteristics */
        .characteristics {
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
        }

        .char-tag {
            background: #f1f5f9;
            color: #475569;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 500;
        }

        /* Action Buttons */
        .action-buttons {
            display: flex;
            gap: 8px;
        }

        .btn-icon {
            width: 38px;
            height: 38px;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;
            font-size: 16px;
        }

        .btn-icon.btn-view {
            background: #dbeafe;
            color: #1e40af;
        }

        .btn-icon.btn-edit {
            background: #fef3c7;
            color: #92400e;
        }

        .btn-icon.btn-delete {
            background: #fee2e2;
            color: #991b1b;
        }

        .btn-icon:hover {
            transform: scale(1.1);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
        }

        .empty-state i {
            font-size: 80px;
            color: #cbd5e0;
            margin-bottom: 20px;
        }

        .empty-state h3 {
            color: #475569;
            font-size: 24px;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #94a3b8;
            font-size: 16px;
        }

        /* Alert Messages */
        .alert {
            padding: 15px 20px;
            border-radius: 12px;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 12px;
            font-weight: 500;
        }

        .alert-success {
            background: #d1fae5;
            color: #065f46;
            border-left: 4px solid #10b981;
        }

        .alert-error {
            background: #fee2e2;
            color: #991b1b;
            border-left: 4px solid #ef4444;
        }

        .alert i {
            font-size: 20px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .header-top {
                flex-direction: column;
                gap: 20px;
            }

            .header-actions {
                width: 100%;
                flex-direction: column;
            }

            .btn {
                width: 100%;
                justify-content: center;
            }

            .search-bar {
                flex-direction: column;
            }

            .filter-tabs {
                overflow-x: auto;
                padding-bottom: 10px;
            }

            table {
                font-size: 13px;
            }

            th, td {
                padding: 12px 10px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <!-- Header -->
    <div class="header">
        <div class="header-top">
            <h1>
                <i class="fas fa-warehouse"></i>
                Kho Linh Kiện
            </h1>
            <div class="header-actions">
                <a href="${pageContext.request.contextPath}/inventory?action=add" class="btn btn-success">
                    <i class="fas fa-plus"></i>
                    Thêm Linh Kiện
                </a>
                <a href="${pageContext.request.contextPath}/stock?action=in" class="btn btn-primary">
                    <i class="fas fa-arrow-down"></i>
                    Nhập Kho
                </a>
                <a href="${pageContext.request.contextPath}/stock?action=out" class="btn btn-warning">
                    <i class="fas fa-arrow-up"></i>
                    Xuất Kho
                </a>
            </div>
        </div>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty param.message}">
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            <c:choose>
                <c:when test="${param.message == 'added'}">Thêm linh kiện thành công!</c:when>
                <c:when test="${param.message == 'updated'}">Cập nhật thành công!</c:when>
                <c:when test="${param.message == 'deleted'}">Xóa thành công!</c:when>
            </c:choose>
        </div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i>
            <c:choose>
                <c:when test="${param.error == 'add_failed'}">Thêm linh kiện thất bại!</c:when>
                <c:when test="${param.error == 'update_failed'}">Cập nhật thất bại!</c:when>
                <c:when test="${param.error == 'delete_failed'}">Xóa thất bại!</c:when>
            </c:choose>
        </div>
    </c:if>

    <!-- Stats Cards -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon blue">
                <i class="fas fa-boxes"></i>
            </div>
            <div class="stat-info">
                <h3>Tổng Linh Kiện</h3>
                <div class="stat-value">${totalItems != null ? totalItems : 0}</div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon orange">
                <i class="fas fa-exclamation-triangle"></i>
            </div>
            <div class="stat-info">
                <h3>Cảnh Báo Tồn</h3>
                <div class="stat-value">${lowStockCount != null ? lowStockCount : 0}</div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon green">
                <i class="fas fa-dollar-sign"></i>
            </div>
            <div class="stat-info">
                <h3>Tổng Giá Trị</h3>
                <div class="stat-value">
                    <fmt:formatNumber value="${totalValue != null ? totalValue : 0}" type="number" maxFractionDigits="0"/>đ
                </div>
            </div>
        </div>

        <div class="stat-card">
            <div class="stat-icon purple">
                <i class="fas fa-layer-group"></i>
            </div>
            <div class="stat-info">
                <h3>SKU</h3>
                <div class="stat-value">${totalItems != null ? totalItems : 0}</div>
            </div>
        </div>
    </div>

    <!-- Search & Filter -->
    <div class="search-section">
        <form action="${pageContext.request.contextPath}/inventory" method="get" class="search-bar">
            <input type="hidden" name="action" value="search">
            <div class="search-input-wrapper">
                <i class="fas fa-search"></i>
                <input type="text" name="keyword" class="search-input"
                       placeholder="Tìm kiếm theo tên, mã linh kiện, SKU..."
                       value="${keyword}">
            </div>
            <button type="submit" class="btn btn-primary">
                <i class="fas fa-search"></i>
                Tìm Kiếm
            </button>
        </form>

        <div class="filter-tabs">
            <a href="${pageContext.request.contextPath}/inventory?action=list"
               class="filter-tab ${empty param.action || param.action == 'list' ? 'active' : ''}">
                <i class="fas fa-list"></i> Tất Cả
            </a>
            <a href="${pageContext.request.contextPath}/inventory?action=lowstock"
               class="filter-tab ${param.action == 'lowstock' ? 'active' : ''}">
                <i class="fas fa-exclamation-triangle"></i> Sắp Hết
            </a>
            <a href="${pageContext.request.contextPath}/inventory?action=list&category=Phanh"
               class="filter-tab">
                <i class="fas fa-cog"></i> Phanh
            </a>
            <a href="${pageContext.request.contextPath}/inventory?action=list&category=Lọc"
               class="filter-tab">
                <i class="fas fa-filter"></i> Lọc
            </a>
            <a href="${pageContext.request.contextPath}/inventory?action=list&category=Động cơ"
               class="filter-tab">
                <i class="fas fa-car"></i> Động Cơ
            </a>
        </div>
    </div>

    <!-- Inventory Table -->
    <div class="inventory-card">
        <c:choose>
            <c:when test="${empty inventoryList}">
                <div class="empty-state">
                    <i class="fas fa-inbox"></i>
                    <h3>Không có dữ liệu</h3>
                    <p>Chưa có linh kiện nào trong kho hoặc không tìm thấy kết quả phù hợp.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrapper">
                    <table>
                        <thead>
                        <tr>
                            <th>SKU</th>
                            <th>Tên Linh Kiện</th>
                            <th>Danh Mục</th>
                            <th>Đặc Tính</th>
                            <th>Số Lượng</th>
                            <th>Vị Trí</th>
                            <th>Đơn Giá</th>
                            <th>Trạng Thái</th>
                            <th>Thao Tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${inventoryList}">
                            <tr>
                                <td>
                                    <span class="sku-code">${item.sku}</span>
                                </td>
                                <td>
                                    <strong>${item.partName}</strong><br>
                                    <small style="color: #64748b;">${item.partCode}</small>
                                </td>
                                <td>
                                            <span class="badge badge-category">
                                                <i class="fas fa-tag"></i>
                                                ${item.category}
                                            </span>
                                </td>
                                <td>
                                    <div class="characteristics">
                                        <c:forEach var="c" items="${partDetail.characteristics}">
                                            ${c.fullName}
                                        </c:forEach>
                                        <c:if test="${empty item.characteristics}">
                                            <span style="color: #94a3b8; font-size: 13px;">-</span>
                                        </c:if>
                                    </div>
                                </td>
                                <td>
                                    <div class="stock-info">
                                                <span class="stock-number ${item.quantity <= item.minStock ? 'low' : 'normal'}">
                                                        ${item.quantity}
                                                </span>
                                        <span class="unit-name">${item.unitName}</span>
                                    </div>
                                    <div class="min-stock">Tối thiểu: ${item.minStock}</div>
                                </td>
                                <td>
                                    <i class="fas fa-map-marker-alt" style="color: #3b82f6;"></i>
                                        ${item.location}
                                </td>
                                <td>
                                            <span class="price">
                                                <fmt:formatNumber value="${item.unitPrice}" type="number" maxFractionDigits="0"/>đ
                                            </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.quantity == 0}">
                                                    <span class="badge badge-danger">
                                                        <i class="fas fa-times-circle"></i>
                                                        Hết hàng
                                                    </span>
                                        </c:when>
                                        <c:when test="${item.quantity <= item.minStock}">
                                                    <span class="badge badge-warning">
                                                        <i class="fas fa-exclamation-triangle"></i>
                                                        Sắp hết
                                                    </span>
                                        </c:when>
                                        <c:otherwise>
                                                    <span class="badge badge-success">
                                                        <i class="fas fa-check-circle"></i>
                                                        Đủ
                                                    </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <button class="btn-icon btn-view"
                                                onclick="location.href='${pageContext.request.contextPath}/inventory?action=detail&id=${item.partDetailId}'"
                                                title="Xem chi tiết">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button class="btn-icon btn-edit"
                                                onclick="location.href='${pageContext.request.contextPath}/inventory?action=edit&id=${item.partDetailId}'"
                                                title="Chỉnh sửa">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button class="btn-icon btn-delete"
                                                onclick="confirmDelete(${item.partDetailId}, '${item.partName}')"
                                                title="Xóa">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    function confirmDelete(id, name) {
        if (confirm('Bạn có chắc muốn xóa linh kiện "' + name + '" không?')) {
            // Tạo form và submit
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = '${pageContext.request.contextPath}/inventory';

            const actionInput = document.createElement('input');
            actionInput.type = 'hidden';
            actionInput.name = 'action';
            actionInput.value = 'delete';

            const idInput = document.createElement('input');
            idInput.type = 'hidden';
            idInput.name = 'id';
            idInput.value = id;

            form.appendChild(actionInput);
            form.appendChild(idInput);
            document.body.appendChild(form);
            form.submit();
        }
    }

    // Tự động ẩn alert sau 5 giây
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(alert => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        });
    }, 5000);
</script>
</body>
</html>
