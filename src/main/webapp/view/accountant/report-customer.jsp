<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);">

                    <!-- Page Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <a href="${pageContext.request.contextPath}/accountant/report"
                               class="btn btn-outline-secondary mb-3"
                               style="border-radius: 8px;">
                                <i class="bi bi-arrow-left me-2"></i>Back to Dashboard
                            </a>
                            <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                                <i class="bi bi-people me-2" style="color: #667eea;"></i>
                                Customer Report
                            </h2>
                            <p class="text-muted mb-0">
                                <c:choose>
                                    <c:when test="${reportType == 'outstanding'}">
                                        List of customers with outstanding balances
                                    </c:when>
                                    <c:otherwise>
                                        Top customers by payments
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <!-- Filter Tabs -->
                        <div class="btn-group" role="group">
                                <a href="${pageContext.request.contextPath}/accountant/report?action=customer&type=top&limit=10"
                               class="btn ${reportType != 'outstanding' ? 'btn-primary' : 'btn-outline-primary'}"
                               style="border-radius: 8px 0 0 8px;">
                                <i class="bi bi-trophy"></i> Top Customers
                            </a>
                                <a href="${pageContext.request.contextPath}/accountant/report?action=customer&type=outstanding"
                               class="btn ${reportType == 'outstanding' ? 'btn-warning' : 'btn-outline-warning'}"
                               style="border-radius: 0 8px 8px 0;">
                                <i class="bi bi-exclamation-triangle"></i> Outstanding
                            </a>
                        </div>
                    </div>

                    <!-- Customer Table -->
                    <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                        <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                            <div class="d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                    <i class="bi bi-table me-2" style="color: #667eea;"></i>
                                    Customer List
                                </h5>

                                <c:if test="${reportType != 'outstanding'}">
                                    <form method="get" action="${pageContext.request.contextPath}/accountant/report" class="d-flex gap-2">
                                        <input type="hidden" name="action" value="customer">
                                        <input type="hidden" name="type" value="top">
                                        <select name="limit" class="form-select form-select-sm" style="border-radius: 6px; width: 150px;" onchange="this.form.submit()">
                                            <option value="5" ${limit == 5 ? 'selected' : ''}>Top 5</option>
                                            <option value="10" ${limit == 10 ? 'selected' : ''}>Top 10</option>
                                            <option value="20" ${limit == 20 ? 'selected' : ''}>Top 20</option>
                                            <option value="50" ${limit == 50 ? 'selected' : ''}>Top 50</option>
                                        </select>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0">
                                    <thead style="background-color: #f9fafb;">
                                        <tr>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; border-top: none;">Rank</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; border-top: none;">Customer</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center; border-top: none;">Invoices</th>
                                            <c:if test="${reportType != 'outstanding'}">
                                            <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right; border-top: none;">Total Invoiced</th>
                                        </c:if>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right; border-top: none;">Collected</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right; border-top: none;">Outstanding</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center; border-top: none;">Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="customer" items="${customers}" varStatus="status">
                                        <tr style="transition: all 0.2s;"
                                            onmouseover="this.style.backgroundColor='#f9fafb'"
                                            onmouseout="this.style.backgroundColor='white'">
                                            <td style="padding: 1rem;">
                                                <c:choose>
                                                    <c:when test="${status.index == 0}">
                                                            <span class="badge" style="background-color: #f59e0b; padding: 0.5rem 0.75rem; border-radius: 50%; font-size: 1rem;">
                                                                <i class="bi bi-trophy-fill"></i>
                                                            </span>
                                                    </c:when>
                                                    <c:when test="${status.index == 1}">
                                                            <span class="badge" style="background-color: #94a3b8; padding: 0.5rem 0.75rem; border-radius: 50%; font-size: 1rem;">
                                                                <i class="bi bi-trophy-fill"></i>
                                                            </span>
                                                    </c:when>
                                                    <c:when test="${status.index == 2}">
                                                            <span class="badge" style="background-color: #d97706; padding: 0.5rem 0.75rem; border-radius: 50%; font-size: 1rem;">
                                                                <i class="bi bi-trophy-fill"></i>
                                                            </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                            <span class="badge bg-secondary" style="padding: 0.5rem 0.75rem; border-radius: 50%;">
                                                                    ${status.index + 1}
                                                            </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="padding: 1rem;">
                                                <div>
                                                    <div class="fw-semibold" style="color: #111827;">
                                                            ${customer.customerName}
                                                    </div>
                                                    <small class="text-muted">
                                                        <i class="bi bi-envelope me-1"></i>${customer.customerEmail}
                                                    </small>
                                                </div>
                                            </td>
                                            <td style="padding: 1rem; text-align: center;">
                                                    <span class="badge bg-info" style="padding: 0.5rem 1rem; border-radius: 20px;">
                                                        ${customer.totalInvoices} invoices
                                                    </span>
                                            </td>
                                            <c:if test="${reportType != 'outstanding'}">
                                                <td style="padding: 1rem; text-align: right;">
                                                    <strong style="color: #111827;">
                                                        <fmt:formatNumber value="${customer.totalInvoiced}" pattern="#,###"/> VND
                                                    </strong>
                                                </td>
                                            </c:if>
                                            <td style="padding: 1rem; text-align: right;">
                                                    <strong style="color: #059669;">
                                                    <fmt:formatNumber value="${customer.totalPaid}" pattern="#,###"/> VND
                                                </strong>
                                            </td>
                                            <td style="padding: 1rem; text-align: right;">
                                                    <strong style="color: ${customer.outstandingBalance > 0 ? '#dc2626' : '#6b7280'};">
                                                    <fmt:formatNumber value="${customer.outstandingBalance}" pattern="#,###"/> VND
                                                </strong>
                                                <c:if test="${customer.outstandingBalance > 0}">
                                                    <br>
                                                    <span class="badge bg-danger" style="font-size: 0.75rem; padding: 0.25rem 0.5rem; border-radius: 12px;">
                                                            <i class="bi bi-exclamation-circle"></i> Owed
                                                        </span>
                                                </c:if>
                                            </td>
                                            <td style="padding: 1rem; text-align: center;">
                                                <button class="btn btn-sm btn-outline-primary"
                                                        style="border-radius: 6px;"
                                                        onclick="viewCustomerDetail(${customer.customerID})">
                                                    <i class="bi bi-eye"></i> Details
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty customers}">
                                        <tr>
                                            <td colspan="7" style="padding: 3rem; text-align: center;">
                                                <i class="bi bi-inbox fa-4x mb-3" style="color: #d1d5db; font-size: 3rem;"></i>
                                                <p class="text-muted mb-0">No customer data available</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<script>
    function viewCustomerDetail(customerID) {
        // TODO: Implement customer detail view
        alert('View customer details #' + customerID);
// window.location.href = '${pageContext.request.contextPath}/accountant/customer?id=' + customerID;
    }
</script>

<jsp:include page="footer.jsp"/>