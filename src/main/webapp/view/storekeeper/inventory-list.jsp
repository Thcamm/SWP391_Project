<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Parts Inventory - Garage Management</title>
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
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
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
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
            height: 100%;
            transition: all 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
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

        .stat-card .stat-icon.blue {
            background: #3b82f6;
        }

        .stat-card .stat-icon.orange {
            background: #f59e0b;
        }

        .stat-card .stat-icon.green {
            background: #10b981;
        }

        .stat-card .stat-icon.purple {
            background: #8b5cf6;
        }

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
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
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
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
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

        /* Characteristics (REMOVED, but style kept just in case) */
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
        <div class="page-header">
            <div class="d-flex justify-content-between align-items-start flex-wrap">
                <div>
                    <h1>
                        <i class="fas fa-warehouse"></i>
                        Inventory Management
                    </h1>
                    <p>Manage stock, track, and update part information</p>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/inventory?action=add" class="btn btn-success">
                        <i class="fas fa-chart-line"></i>
                        Reports
                    </a>
                    <a href="${pageContext.request.contextPath}/stock?action=in" class="btn btn-primary">
                        <i class="fas fa-arrow-down"></i>
                        Stock In
                    </a>
                    <a href="${pageContext.request.contextPath}/stock?action=out" class="btn btn-warning">
                        <i class="fas fa-arrow-up"></i>
                        Stock Out
                    </a>
                </div>
            </div>
        </div>

        <c:if test="${not empty param.message}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <c:choose>
                    <c:when test="${param.message == 'added'}">Part added successfully!</c:when>
                    <c:when test="${param.message == 'updated'}">Update successful!</c:when>
                    <c:when test="${param.message == 'deleted'}">Delete successful!</c:when>
                </c:choose>
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="alert alert-error">
                <i class="fas fa-exclamation-circle"></i>
                <c:choose>
                    <c:when test="${param.error == 'add_failed'}">Failed to add part!</c:when>
                    <c:when test="${param.error == 'update_failed'}">Failed to update part!</c:when>
                    <c:when test="${param.error == 'delete_failed'}">Failed to delete part!</c:when>
                </c:choose>
            </div>
        </c:if>

        <div class="row g-4 mb-4">

            <%-- Card 1 --%>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card">
                    <div class="stat-icon blue">
                        <i class="fas fa-boxes"></i>
                    </div>
                    <div class="stat-info">
                        <h6>Total Parts</h6>
                        <div class="stat-value">${totalItems != null ? totalItems : 0}</div>
                    </div>
                </div>
            </div>

            <%-- Card 2 --%>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card">
                    <div class="stat-icon orange">
                        <i class="fas fa-exclamation-triangle"></i>
                    </div>
                    <div class="stat-info">
                        <h6>Low Stock Alert</h6>
                        <div class="stat-value">${lowStockCount != null ? lowStockCount : 0}</div>
                    </div>
                </div>
            </div>

            <%-- Card 3 --%>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card">
                    <div class="stat-icon green">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stat-info">
                        <h6>Total Value</h6>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalValue != null ? totalValue : 0}" type="number"
                                              maxFractionDigits="0"/> VND
                        </div>
                    </div>
                </div>
            </div>

            <%-- Card 4 --%>
            <div class="col-lg-3 col-md-6">
                <div class="stat-card">
                    <div class="stat-icon purple">
                        <i class="fas fa-layer-group"></i>
                    </div>
                    <div class="stat-info">
                        <h6>Total SKU</h6>
                        <div class="stat-value">${totalItems != null ? totalItems : 0}</div>
                    </div>
                </div>
            </div>

        </div>

        <div class="search-section">
            <div class="card mb-4">
                <div class="card-header">
                    <h5>
                        <i class="fas fa-search"></i>
                        Advanced Search
                    </h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/inventory" method="get">
                        <input type="hidden" name="action" value="search">

                        <div class="row g-3">
                            <div class="col-md-3">
                                <label class="form-label">Name or SKU</label>
                                <input type="text"
                                       name="keyword"
                                       class="form-control"
                                       placeholder="Enter name or SKU"
                                       value="${param.keyword}">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Location</label>
                                <input type="text"
                                       name="location"
                                       class="form-control"
                                       placeholder="Shelf, bin..."
                                       value="${param.location}">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Status</label>
                                <select name="stockStatus" class="form-select">
                                    <option value="">-- All --</option>
                                    <option value="normal" ${param.stockStatus == 'normal' ? 'selected' : ''}>
                                        In Stock
                                    </option>
                                    <option value="low" ${param.stockStatus == 'low' ? 'selected' : ''}>
                                        Low Stock
                                    </option>
                                    <option value="out" ${param.stockStatus == 'out' ? 'selected' : ''}>
                                        Out of Stock
                                    </option>
                                </select>
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Category</label>
                                <input type="text"
                                       name="category"
                                       class="form-control"
                                       placeholder="oil,.."
                                       value="${param.category}">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Price From (VND)</label>
                                <input type="number"
                                       name="priceFrom"
                                       class="form-control"
                                       min="0"
                                       placeholder="0"
                                       value="${param.priceFrom}">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Price To (VND)</label>
                                <input type="number"
                                       name="priceTo"
                                       class="form-control"
                                       min="0"
                                       placeholder="No limit"
                                       value="${param.priceTo}">
                            </div>

                            <div class="col-md-3">
                                <label class="form-label">Manufacturer</label>
                                <input type="text"
                                       name="manufacturer"
                                       class="form-control"
                                       placeholder="honda,.."
                                       value="${param.manufacturer}">
                            </div>
                            <div class="col-md-3 d-flex align-items-end gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-search"></i>
                                    Search
                                </button>
                                <a href="${pageContext.request.contextPath}/inventory?action=list"
                                   class="btn btn-outline-secondary">
                                    <i class="fas fa-redo"></i>
                                    Reset
                                </a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>


            <div class="filter-tabs">
                <a href="${pageContext.request.contextPath}/inventory?action=list"
                   class="filter-tab ${(empty param.action || param.action == 'list') && empty param.category ? 'active' : ''}">
                    <i class="fas fa-list"></i> All
                </a>
                <c:forEach var="category" items="${allCategoriesList}">
                    <a href="${pageContext.request.contextPath}/inventory?action=list&category=${category}"
                       class="filter-tab ${param.category == category ? 'active' : ''}">
                        <i class="fas fa-cube"></i> ${category}
                    </a>
                </c:forEach>
            </div>
        </div>

        <div class="inventory-card">
            <c:choose>
                <c:when test="${empty inventoryList}">
                    <div class="empty-state">
                        <i class="fas fa-inbox"></i>
                        <h3>No Data Found</h3>
                        <p>There are no parts in the inventory yet or no matching results were found.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                            <tr>
                                <th>SKU</th>
                                <th>Part Name</th>
                                <th>Category</th>
                                <th>Manufacturer</th>
                                <th>Quantity</th>
                                <th>Location</th>
                                <th>Unit Price</th>
                                <th>Status</th>
                                <th>Actions</th>
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
                                            <span class="badge badge-category text-black">
                                                <i class="fas fa-tag"></i>
                                                ${item.category}
                                            </span>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.manufacturer}">
                                                ${item.manufacturer}
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #94a3b8; font-style: italic;">N/A</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="stock-info">
                                                <span class="stock-number ${item.quantity <= item.minStock ? 'low' : 'normal'}">
                                                        ${item.quantity}
                                                </span>
                                            <span class="unit-name">${item.unitName}</span>
                                        </div>
                                        <div class="min-stock">Min: ${item.minStock}</div>
                                    </td>
                                    <td>
                                        <i class="fas fa-map-marker-alt" style="color: #3b82f6;"></i>
                                            ${item.location}
                                    </td>
                                    <td>
                                            <span class="price">
                                                <fmt:formatNumber value="${item.unitPrice}" type="number"
                                                                  maxFractionDigits="0"/> VND
                                            </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.quantity == 0}">
                                                    <span class="badge badge-danger">
                                                        <i class="fas fa-times-circle"></i>
                                                        Out of Stock
                                                    </span>
                                            </c:when>
                                            <c:when test="${item.quantity <= item.minStock}">
                                                    <span class="badge badge-warning">
                                                        <i class="fas fa-exclamation-triangle"></i>
                                                        Low Stock
                                                    </span>
                                            </c:when>
                                            <c:otherwise>
                                                    <span class="badge badge-success">
                                                        <i class="fas fa-check-circle"></i>
                                                        In Stock
                                                    </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <button class="btn-table-action btn-history"
                                                    onclick="location.href='${pageContext.request.contextPath}/inventory?action=history&id=${item.partDetailId}'"
                                                    title="View stock history">
                                                <i class="fas fa-history"></i>
                                            </button>
                                            <button class="btn-table-action btn-edit"
                                                    onclick="location.href='${pageContext.request.contextPath}/inventory?action=edit&id=${item.partDetailId}'"
                                                    title="Edit details">
                                                <i class="fas fa-edit"></i>
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

        <c:if test="${not empty inventoryList and totalPages > 1}">
            <div class="pagination-wrapper" style="margin-top: 30px;">
                <nav aria-label="Inventory pagination">
                    <ul class="pagination justify-content-center">

                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <c:choose>
                                <c:when test="${currentPage == 1}">
                            <span class="page-link">
                                <i class="fas fa-chevron-left"></i> Previous
                            </span>
                                </c:when>
                                <c:otherwise>
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage - 1}${not empty param.keyword ? '&keyword='.concat(param.keyword) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}">
                                        <i class="fas fa-chevron-left"></i> Previous
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </li>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:choose>
                                <%-- Show first page --%>
                                <c:when test="${i == 1}">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link"
                                           href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${i}${not empty param.keyword ? '&keyword='.concat(param.keyword) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}">
                                                ${i}
                                        </a>
                                    </li>
                                </c:when>

                                <%-- Show last page --%>
                                <c:when test="${i == totalPages}">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link"
                                           href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${i}${not empty param.keyword ? '&keyword='.concat(param.keyword) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}">
                                                ${i}
                                        </a>
                                    </li>
                                </c:when>

                                <%-- Show pages near current page --%>
                                <c:when test="${i >= currentPage - 2 && i <= currentPage + 2}">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link"
                                           href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${i}${not empty param.keyword ? '&keyword='.concat(param.keyword) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}">
                                                ${i}
                                        </a>
                                    </li>
                                </c:when>

                                <%-- Show ellipsis (...) --%>
                                <c:when test="${(i == 2 && currentPage > 4) || (i == totalPages - 1 && currentPage < totalPages - 3)}">
                                    <li class="page-item disabled">
                                        <span class="page-link">...</span>
                                    </li>
                                </c:when>
                            </c:choose>
                        </c:forEach>

                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <c:choose>
                                <c:when test="${currentPage == totalPages}">
                            <span class="page-link">
                                Next <i class="fas fa-chevron-right"></i>
                            </span>
                                </c:when>
                                <c:otherwise>
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/inventory?action=${param.action != null ? param.action : 'list'}&page=${currentPage + 1}${not empty param.keyword ? '&keyword='.concat(param.keyword) : ''}${not empty param.category ? '&category='.concat(param.category) : ''}">
                                        Next <i class="fas fa-chevron-right"></i>
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </nav>

                <div class="pagination-info text-center mt-3" style="color: #64748b; font-size: 14px;">
                    Showing
                    <strong>${(currentPage - 1) * itemsPerPage + 1}</strong> -
                    <strong>${currentPage * itemsPerPage > totalItems ? totalItems : currentPage * itemsPerPage}</strong>
                    of <strong>${totalItems}</strong> parts
                </div>
            </div>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(id, name) {
        if (confirm('Are you sure you want to delete part "' + name + '"?')) {
            // Create and submit form
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

    // Auto-hide alerts after 5 seconds
    setTimeout(function () {
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