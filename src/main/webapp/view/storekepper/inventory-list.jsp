<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kho Linh Kiện - Garage Inventory</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        /* Main Content Area */
        .inventory-main {
            min-height: calc(100vh - 200px);
            background: #f8f9fa;
            padding: 40px 0;
        }

        .container {
            max-width: 1400px;
        }

        /* Page Header */
        .page-header {
            background: white;
            border-radius: 15px;
            padding: 30px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .page-header h1 {
            color: #1e293b;
            font-size: 32px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .page-header h1 i {
            color: #3b82f6;
        }

        .page-header p {
            color: #64748b;
            margin: 0;
        }

        .header-actions {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
            margin-top: 20px;
        }

        /* Buttons */
        .btn {
            padding: 10px 20px;
            border-radius: 8px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            text-decoration: none;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        }

        .btn-success {
            background: #10b981;
            color: white;
            border: none;
        }

        .btn-primary {
            background: #3b82f6;
            color: white;
            border: none;
        }

        .btn-warning {
            background: #f59e0b;
            color: white;
            border: none;
        }

        /* Stats Cards */
        .stats-row {
            margin-bottom: 30px;
        }

        .stat-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
            height: 100%;
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
        }

        .stat-card .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            color: white;
            margin-bottom: 15px;
        }

        .stat-card .stat-icon.blue { background: #3b82f6; }
        .stat-card .stat-icon.orange { background: #f59e0b; }
        .stat-card .stat-icon.green { background: #10b981; }
        .stat-card .stat-icon.purple { background: #8b5cf6; }

        .stat-card h6 {
            color: #64748b;
            font-size: 13px;
            font-weight: 600;
            text-transform: uppercase;
            margin-bottom: 8px;
        }

        .stat-card .stat-value {
            color: #1e293b;
            font-size: 28px;
            font-weight: 700;
        }

        /* Search & Filter Section */
        .search-filter-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        .search-bar {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
        }

        .search-input-wrapper {
            flex: 1;
            position: relative;
        }

        .search-input-wrapper i {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #94a3b8;
        }

        .search-input {
            width: 100%;
            padding: 12px 15px 12px 45px;
            border: 2px solid #e2e8f0;
            border-radius: 8px;
            font-size: 15px;
            transition: all 0.3s ease;
        }

        .search-input:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        .filter-tabs {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .filter-tab {
            padding: 8px 16px;
            border: 2px solid #e2e8f0;
            background: white;
            border-radius: 8px;
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
        .table-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .table-responsive {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: #f8fafc;
            border-bottom: 2px solid #e2e8f0;
        }

        th {
            padding: 15px;
            text-align: left;
            color: #475569;
            font-weight: 700;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        tbody tr {
            border-bottom: 1px solid #f1f5f9;
            transition: all 0.2s ease;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        td {
            padding: 18px 15px;
            color: #334155;
            font-size: 14px;
        }

        /* Badge Styles */
        .badge {
            padding: 5px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            display: inline-flex;
            align-items: center;
            gap: 5px;
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

        .badge-info {
            background: #dbeafe;
            color: #1e40af;
        }

        /* SKU Code */
        .sku-code {
            font-family: 'Courier New', monospace;
            background: #f1f5f9;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
            color: #3b82f6;
        }

        /* Stock Info */
        .stock-info {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .stock-number {
            font-size: 16px;
            font-weight: 700;
        }

        .stock-number.low {
            color: #dc2626;
        }

        .stock-number.normal {
            color: #059669;
        }

        .min-stock {
            font-size: 11px;
            color: #64748b;
            margin-top: 2px;
        }

        /* Price */
        .price {
            font-size: 15px;
            font-weight: 700;
            color: #059669;
        }

        /* Characteristics */
        .characteristics {
            display: flex;
            flex-wrap: wrap;
            gap: 5px;
        }

        .char-tag {
            background: #f1f5f9;
            color: #475569;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 500;
        }

        /* Action Buttons */
        .action-buttons {
            display: flex;
            gap: 6px;
        }

        .btn-icon {
            width: 36px;
            height: 36px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: all 0.3s ease;
            font-size: 14px;
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
        }

        /* Empty State */
        .empty-state {
            text-align: center;
            padding: 60px 20px;
        }

        .empty-state i {
            font-size: 60px;
            color: #cbd5e1;
            margin-bottom: 20px;
        }

        .empty-state h3 {
            color: #475569;
            font-size: 20px;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #94a3b8;
        }

        /* Alert Messages */
        .alert {
            border-radius: 10px;
            margin-bottom: 20px;
            padding: 15px 20px;
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .alert i {
            font-size: 18px;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .header-actions {
                flex-direction: column;
            }

            .btn {
                width: 100%;
                justify-content: center;
            }

            .search-bar {
                flex-direction: column;
            }

            table {
                font-size: 12px;
            }

            th, td {
                padding: 10px 8px;
            }
        }
    </style>
</head>
<body>
    <%@ include file="/common/header.jsp" %>

    <div class="inventory-main">
        <div class="container">
            <!-- Page Header -->
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-start flex-wrap">
                    <div>
                        <h1>
                            <i class="fas fa-warehouse"></i>
                            Quản Lý Kho Linh Kiện
                        </h1>
                        <p>Quản lý tồn kho, theo dõi và cập nhật thông tin linh kiện</p>
                    </div>
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

            <div class="row g-4 mb-4"> <%-- SỬA Ở ĐÂY --%>

                <%-- Card 1 --%>
                <div class="col-lg-3 col-md-6"> <%-- THÊM DÒNG NÀY --%>
                    <div class="stat-card">
                        <div class="stat-icon blue">
                            <i class="fas fa-boxes"></i>
                        </div>
                        <div class="stat-info">
                            <h6>Tổng Linh Kiện</h6> <%-- Dùng H6 cho đẹp hơn --%>
                            <div class="stat-value">${totalItems != null ? totalItems : 0}</div>
                        </div>
                    </div>
                </div> <%-- THÊM DÒNG NÀY --%>

                <%-- Card 2 --%>
                <div class="col-lg-3 col-md-6"> <%-- THÊM DÒNG NÀY --%>
                    <div class="stat-card">
                        <div class="stat-icon orange">
                            <i class="fas fa-exclamation-triangle"></i>
                        </div>
                        <div class="stat-info">
                            <h6>Cảnh Báo Tồn</h6>
                            <div class="stat-value">${lowStockCount != null ? lowStockCount : 0}</div>
                        </div>
                    </div>
                </div> <%-- THÊM DÒNG NÀY --%>

                <%-- Card 3 --%>
                <div class="col-lg-3 col-md-6"> <%-- THÊM DÒNG NÀY --%>
                    <div class="stat-card">
                        <div class="stat-icon green">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <div class="stat-info">
                            <h6>Tổng Giá Trị</h6>
                            <div class="stat-value">
                                <fmt:formatNumber value="${totalValue != null ? totalValue : 0}" type="number" maxFractionDigits="0"/>đ
                            </div>
                        </div>
                    </div>
                </div> <%-- THÊM DÒNG NÀY --%>

                <%-- Card 4 --%>
                <div class="col-lg-3 col-md-6"> <%-- THÊM DÒNG NÀY --%>
                    <div class="stat-card">
                        <div class="stat-icon purple">
                            <i class="fas fa-layer-group"></i>
                        </div>
                        <div class="stat-info">
                            <h6>Tổng SKU</h6> <%-- Sửa H3 thành H6 và "SKU" thành "Tổng SKU" --%>
                            <div class="stat-value">${totalItems != null ? totalItems : 0}</div>
                        </div>
                    </div>
                </div> <%-- THÊM DÒNG NÀY --%>

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
                                    <%-- ... các <td> khác ... --%>
                                <td>
                                    <div class="characteristics">
                                            <%-- SỬA LỖI Ở ĐÂY: Dùng "item" thay vì "partDetail" --%>
                                        <c:forEach var="c" items="${item.characteristics}">
                                            <span class="char-tag">${c.fullName}</span>
                                        </c:forEach>

                                            <%-- Phần này đã đúng --%>
                                        <c:if test="${empty item.characteristics}">
                                            <span style="color: #94a3b8; font-size: 13px;">-</span>
                                        </c:if>
                                    </div>
                                </td>
                                    <%-- ... các <td> khác ... --%>
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
    </div>

    <!-- Scripts -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
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

    <%@ include file="/common/footer.jsp" %>
</body>
</html>
