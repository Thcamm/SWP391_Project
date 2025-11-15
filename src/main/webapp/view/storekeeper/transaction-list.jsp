<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transaction History - Garage Management System</title>

    <!-- Bootstrap 5 & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        :root {
            --primary-color: #2563eb;
            --secondary-color: #64748b;
            --success-color: #10b981;
            --warning-color: #f59e0b;
            --danger-color: #ef4444;
        }

        body {
            background-color: #f8fafc;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .stat-card {
            border-radius: 12px;
            border: none;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        }

        .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.5rem;
        }

        .transaction-table {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        }

        .transaction-table thead {
            background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
        }

        .transaction-table th {
            font-weight: 600;
            color: #475569;
            border-bottom: 2px solid #cbd5e1;
            padding: 1rem;
        }

        .transaction-table td {
            padding: 1rem;
            vertical-align: middle;
            border-bottom: 1px solid #f1f5f9;
        }

        .transaction-table tbody tr:hover {
            background-color: #f8fafc;
        }

        .badge-in {
            color: #166534;
            padding: 0.4rem 0.8rem;
            border-radius: 6px;
            font-weight: 500;
        }

        .badge-out {
            background-color: #fee2e2;
            color: #991b1b;
            padding: 0.4rem 0.8rem;
            border-radius: 6px;
            font-weight: 500;
        }

        .filter-card {
            background: white;
            border-radius: 12px;
            padding: 1.5rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            margin-bottom: 2rem;
        }

        .btn-filter {
            border-radius: 8px;
            padding: 0.6rem 1.5rem;
            font-weight: 500;
        }

        .empty-state {
            text-align: center;
            padding: 4rem 2rem;
        }

        .empty-state i {
            font-size: 4rem;
            color: #cbd5e1;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid px-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-auto" style="flex:0 0 280px; width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content -->
        <div class="col" style="min-width:0;">
            <main class="p-3 pb-0">

                <!-- Page Header -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex align-items-center justify-content-between">
                        <div>
                            <h2 class="h4 mb-1">
                                <i class="fas fa-exchange-alt me-2"></i>Transaction History
                            </h2>
                            <p class="text-muted mb-0">
                                Track all inventory movements
                            </p>
                        </div>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/inventory?action=list"
                               class="btn btn-outline-primary">
                                <i class="fas fa-arrow-left me-2"></i>Back to Inventory
                            </a>
                        </div>
                    </div>
                </div>

                    <!-- Statistics Cards -->
                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <div class="card stat-card shadow-sm">
                                <div class="card-body d-flex align-items-center">
                                    <div class="stat-icon bg-primary bg-opacity-10 text-primary me-3">
                                        <i class="fas fa-list"></i>
                                    </div>
                                    <div>
                                        <div class="text-muted small">Total Transactions</div>
                                        <h3 class="mb-0">${totalTransactions}</h3>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="card stat-card shadow-sm">
                                <div class="card-body d-flex align-items-center">
                                    <div class="stat-icon bg-success bg-opacity-10 text-success me-3">
                                        <i class="fas fa-arrow-down"></i>
                                    </div>
                                    <div>
                                        <div class="text-muted small">Stock In</div>
                                        <h3 class="mb-0 text-success">${stockInCount}</h3>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="card stat-card shadow-sm">
                                <div class="card-body d-flex align-items-center">
                                    <div class="stat-icon bg-danger bg-opacity-10 text-danger me-3">
                                        <i class="fas fa-arrow-up"></i>
                                    </div>
                                    <div>
                                        <div class="text-muted small">Stock Out</div>
                                        <h3 class="mb-0 text-danger">${stockOutCount}</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Filter Card -->
                    <div class="filter-card">
                        <form method="get" action="${pageContext.request.contextPath}/transactions">
                            <input type="hidden" name="action" value="filter">
                            <div class="row g-3 align-items-end">
                                <div class="col-md-3">
                                    <label for="transactionType" class="form-label fw-semibold">
                                        <i class="fas fa-filter me-1"></i>Transaction Type
                                    </label>
                                    <select class="form-select" id="transactionType" name="type">
                                        <option value="">All Transactions</option>
                                        <option value="IN" ${filterType == 'IN' ? 'selected' : ''}>Stock In</option>
                                        <option value="OUT" ${filterType == 'OUT' ? 'selected' : ''}>Stock Out</option>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <label for="fromDate" class="form-label fw-semibold">
                                        <i class="fas fa-calendar me-1"></i>From Date
                                    </label>
                                    <input type="date" class="form-control" id="fromDate" name="fromDate"
                                           value="${param.fromDate}">
                                </div>

                                <div class="col-md-3">
                                    <label for="toDate" class="form-label fw-semibold">
                                        <i class="fas fa-calendar me-1"></i>To Date
                                    </label>
                                    <input type="date" class="form-control" id="toDate" name="toDate"
                                           value="${param.toDate}">
                                </div>

                                <div class="col-md-3">
                                    <button type="submit" class="btn btn-primary btn-filter w-100">
                                        <i class="fas fa-search me-2"></i>Apply Filter
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>

                    <!-- Transactions Table -->
                    <div class="transaction-table">
                        <c:choose>
                            <c:when test="${not empty transactions}">
                                <table class="table table-hover mb-0">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Date & Time</th>
                                            <th>Type</th>
                                            <th>Part Detail ID</th>
                                            <th>Quantity</th>
                                            <th>Unit Price</th>
                                            <th>Total Value</th>
                                            <th>Note</th>
                                            <th>Storekeeper ID</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="transaction" items="${transactions}">
                                            <tr>
                                                <td>
                                                    <strong>#${transaction.transactionId}</strong>
                                                </td>
                                                <td>
                                                    <c:out value="${transaction.transactionDate}"/>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${transaction.transactionType == 'IN'}">
                                                            <span class="badge-in">
                                                                Stock In
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge-out">
                                                                <i class="fas fa-arrow-up me-1"></i>Stock Out
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <code>#${transaction.partDetailId}</code>
                                                </td>
                                                <td>
                                                    <strong>
                                                        <c:choose>
                                                            <c:when test="${transaction.transactionType == 'IN'}">
                                                                <span class="text-success">+${transaction.quantity}</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-danger">-${transaction.quantity}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </strong>
                                                </td>
                                                <td>
                                                    <c:if test="${transaction.unitPrice != null}">
                                                        <fmt:formatNumber value="${transaction.unitPrice}"
                                                                          type="currency" currencySymbol="$"/>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <c:if test="${transaction.unitPrice != null}">
                                                        <strong>
                                                            <fmt:formatNumber value="${transaction.unitPrice * transaction.quantity}"
                                                                              type="currency" currencySymbol="$"/>
                                                        </strong>
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <small class="text-muted">
                                                        ${transaction.note != null ? transaction.note : '-'}
                                                    </small>
                                                </td>
                                                <td>
                                                    <span class="badge bg-secondary">
                                                        SK-${transaction.storeKeeperId}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-inbox"></i>
                                    <h5 class="text-muted">No Transactions Found</h5>
                                    <p class="text-muted">There are no transactions matching your criteria.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="card border-0 shadow-sm mt-3">
                            <div class="card-body">
                                <nav aria-label="Page navigation">
                                    <ul class="pagination justify-content-center mb-0">
                                        <!-- Previous Button -->
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/transactions?action=${param.action != null ? param.action : 'list'}&page=${currentPage - 1}&type=${param.type}&fromDate=${param.fromDate}&toDate=${param.toDate}"
                                               aria-label="Previous">
                                                <span aria-hidden="true">&laquo;</span>
                                            </a>
                                        </li>

                                        <!-- Page Numbers -->
                                        <c:forEach begin="1" end="${totalPages}" var="pageNum">
                                            <c:choose>
                                                <c:when test="${pageNum == currentPage}">
                                                    <li class="page-item active">
                                                        <span class="page-link">${pageNum}</span>
                                                    </li>
                                                </c:when>
                                                <c:otherwise>
                                                    <li class="page-item">
                                                        <a class="page-link"
                                                           href="${pageContext.request.contextPath}/transactions?action=${param.action != null ? param.action : 'list'}&page=${pageNum}&type=${param.type}&fromDate=${param.fromDate}&toDate=${param.toDate}">
                                                            ${pageNum}
                                                        </a>
                                                    </li>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>

                                        <!-- Next Button -->
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link"
                                               href="${pageContext.request.contextPath}/transactions?action=${param.action != null ? param.action : 'list'}&page=${currentPage + 1}&type=${param.type}&fromDate=${param.fromDate}&toDate=${param.toDate}"
                                               aria-label="Next">
                                                <span aria-hidden="true">&raquo;</span>
                                            </a>
                                        </li>
                                    </ul>
                                </nav>

                                <div class="text-center mt-2">
                                    <small class="text-muted">
                                        Page ${currentPage} of ${totalPages} | Total: ${totalTransactions} transactions
                                    </small>
                                </div>
                            </div>
                        </div>
                    </c:if>

                </main>
            </div>
        </div>
    </div>



<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>

