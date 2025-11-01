<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>
        <c:choose>
            <c:when test="${not empty sessionScope.roleName}">
                ${sessionScope.roleName} - Home
            </c:when>
            <c:otherwise>
                Home
            </c:otherwise>
        </c:choose>
    </title>
<%--    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />--%>
<%--    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />--%>
    <jsp:include page="/common/employee/component/header.jsp" />
</head>
<body>
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <jsp:include page="/common/employee/component/sidebar.jsp" />

        <div class="layout-page">
            <jsp:include page="/common/employee/component/navbar.jsp" />

            <div class="content-wrapper">
                <div class="container-fluid flex-grow-1 container-p-y">

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

                </div>
                <jsp:include page="/common/employee/component/footer.jsp" />

                <div class="content-backdrop fade"></div>
            </div>
        </div>
    </div>
    <div class="layout-overlay layout-menu-toggle"></div>
</div>
<jsp:include page="/common/employee/component/script.jsp" />

<%--<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>--%>

</body>



</html>
