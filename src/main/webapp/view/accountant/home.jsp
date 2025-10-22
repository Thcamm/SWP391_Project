<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Accountant Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
</head>
<body class="bg-light">

<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
    <span class="navbar-brand">
      <i class="bi bi-wallet2"></i> Accountant Dashboard
    </span>
        <ul class="navbar-nav">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-person-circle me-1"></i>
                    <span class="d-none d-lg-inline">${sessionScope.userName}</span>
                    <span class="badge bg-secondary ms-1"
                    >${sessionScope.roleName}</span
                    >
                </a>
                <ul class="dropdown-menu dropdown-menu-end">
                    <li>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
                            <i class="bi bi-person me-2"></i>My Profile
                        </a>
                    </li>
                    <li><hr class="dropdown-divider" /></li>
                    <li>
                        <a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/Home?action=logout">
                            <i class="bi bi-box-arrow-right me-2"></i>Logout
                        </a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</nav>

<div class="container-fluid mt-4">

    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card text-white bg-success">
                <div class="card-body">
                    <h5 class="card-title">Total Revenue (This Month)</h5>
                    <h2 class="mb-0">
                        <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="" maxFractionDigits="0"/> VND
                    </h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-white bg-warning">
                <div class="card-body">
                    <h5 class="card-title">Unpaid Invoices</h5>
                    <h2 class="mb-0">${unpaidCount}</h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-white bg-danger">
                <div class="card-body">
                    <h5 class="card-title">Overdue Invoices</h5>
                    <h2 class="mb-0">${overdueCount}</h2>
                </div>
            </div>
        </div>
    </div>

    <div class="row mb-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header"><h5 class="mb-0">Quick Actions</h5></div>
                <div class="card-body d-flex gap-2 flex-wrap">
                    <a href="#" class="btn btn-primary"><i class="bi bi-receipt"></i> View Invoices</a>
                    <a href="#" class="btn btn-success"><i class="bi bi-cash-coin"></i> Record a Payment</a>
                    <a href="#" class="btn btn-info"><i class="bi bi-graph-up"></i> View Reports</a>
                </div>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header"><h5 class="mb-0">Recent Invoices</h5></div>
                <div class="card-body">
                    <p class="text-muted">_Recent invoice data will be displayed here_</p>
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>Invoice #</th>
                                <th>Customer</th>
                                <th>Date</th>
                                <th>Total Amount</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="invoice" items="${recentInvoices}">
                                <tr>
                                    <td>${invoice.invoiceNumber}</td>
                                    <td>${invoice.customerName}</td>
                                    <td><fmt:formatDate value="${invoice.invoiceDate}" pattern="dd/MM/yyyy"/></td>
                                    <td><fmt:formatNumber value="${invoice.totalAmount}" type="currency" currencySymbol="" maxFractionDigits="0"/> VND</td>
                                    <td><span class="badge bg-warning">${invoice.paymentStatus}</span></td>
                                    <td><a href="#" class="btn btn-sm btn-outline-primary">View</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
