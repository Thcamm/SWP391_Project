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
                                        Customers with outstanding balances requiring follow-up
                                    </c:when>
                                    <c:otherwise>
                                        Top customers ranked by payment value
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
                                    <c:choose>
                                        <c:when test="${reportType == 'outstanding'}">
                                            Customers with Outstanding Balance
                                        </c:when>
                                        <c:otherwise>
                                            Top ${limit} Customers
                                        </c:otherwise>
                                    </c:choose>
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
                                        <%-- Calculate outstanding balance --%>
                                        <c:set var="outstanding" value="${reportType == 'outstanding' ? customer.outstandingBalance : (customer.totalInvoiced - customer.totalPaid)}" />

                                        <tr style="transition: all 0.2s;"
                                            onmouseover="this.style.backgroundColor='#f9fafb'"
                                            onmouseout="this.style.backgroundColor='white'">
                                            <td style="padding: 1rem;">
                                                <c:choose>
                                                    <c:when test="${status.index == 0 && reportType != 'outstanding'}">
                                                        <span class="badge" style="background-color: #f59e0b; padding: 0.5rem 0.75rem; border-radius: 50%; font-size: 1rem;">
                                                            <i class="bi bi-trophy-fill"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${status.index == 1 && reportType != 'outstanding'}">
                                                        <span class="badge" style="background-color: #94a3b8; padding: 0.5rem 0.75rem; border-radius: 50%; font-size: 1rem;">
                                                            <i class="bi bi-trophy-fill"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${status.index == 2 && reportType != 'outstanding'}">
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
                                                    <c:if test="${not empty customer.phoneNumber}">
                                                        <br>
                                                        <small class="text-muted">
                                                            <i class="bi bi-telephone me-1"></i>${customer.phoneNumber}
                                                        </small>
                                                    </c:if>
                                                </div>
                                            </td>
                                            <td style="padding: 1rem; text-align: center;">
                                                <span class="badge bg-info" style="padding: 0.5rem 1rem; border-radius: 20px;">
                                                        ${customer.totalInvoices}
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
                                                <strong style="color: ${outstanding > 0 ? '#dc2626' : '#6b7280'};">
                                                    <fmt:formatNumber value="${outstanding}" pattern="#,###"/> VND
                                                </strong>
                                                <c:if test="${outstanding > 0}">
                                                    <br>
                                                    <span class="badge bg-danger" style="font-size: 0.75rem; padding: 0.25rem 0.5rem; border-radius: 12px; margin-top: 0.25rem;">
                                                        <i class="bi bi-exclamation-circle"></i> Debt
                                                    </span>
                                                </c:if>
                                            </td>
                                            <td style="padding: 1rem; text-align: center;">
                                                <button class="btn btn-sm btn-outline-primary"
                                                        style="border-radius: 6px;"
                                                        onclick="viewCustomerDetail(${customer.customerID})">
                                                    <i class="bi bi-eye"></i> View
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty customers}">
                                        <tr>
                                            <td colspan="7" style="padding: 3rem; text-align: center;">
                                                <i class="bi bi-inbox" style="color: #d1d5db; font-size: 3rem;"></i>
                                                <p class="text-muted mb-0 mt-3">
                                                    <c:choose>
                                                        <c:when test="${reportType == 'outstanding'}">
                                                            Great! No customers with outstanding balance.
                                                        </c:when>
                                                        <c:otherwise>
                                                            No customer data available for the selected period.
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>
                                            </td>
                                        </tr>
                                    </c:if>
                                    </tbody>

                                    <%-- Total Row --%>
                                    <c:if test="${not empty customers}">
                                        <tfoot style="background-color: #f3f4f6; border-top: 2px solid #e5e7eb;">
                                        <tr>
                                            <td colspan="2" style="padding: 1rem; font-weight: 700; color: #111827;">
                                                <i class="bi bi-calculator me-2"></i>TOTAL (${customers.size()} customers)
                                            </td>
                                            <td style="padding: 1rem; text-align: center; font-weight: 700;">
                                                <c:set var="totalInvoiceCount" value="0" />
                                                <c:forEach var="c" items="${customers}">
                                                    <c:set var="totalInvoiceCount" value="${totalInvoiceCount + c.totalInvoices}" />
                                                </c:forEach>
                                                    ${totalInvoiceCount}
                                            </td>
                                            <c:if test="${reportType != 'outstanding'}">
                                                <td style="padding: 1rem; text-align: right; font-weight: 700; color: #111827;">
                                                    <c:set var="sumInvoiced" value="0" />
                                                    <c:forEach var="c" items="${customers}">
                                                        <c:set var="sumInvoiced" value="${sumInvoiced + c.totalInvoiced}" />
                                                    </c:forEach>
                                                    <fmt:formatNumber value="${sumInvoiced}" pattern="#,###"/> VND
                                                </td>
                                            </c:if>
                                            <td style="padding: 1rem; text-align: right; font-weight: 700; color: #059669;">
                                                <c:set var="sumPaid" value="0" />
                                                <c:forEach var="c" items="${customers}">
                                                    <c:set var="sumPaid" value="${sumPaid + c.totalPaid}" />
                                                </c:forEach>
                                                <fmt:formatNumber value="${sumPaid}" pattern="#,###"/> VND
                                            </td>
                                            <td style="padding: 1rem; text-align: right; font-weight: 700; color: #dc2626;">
                                                <c:set var="sumOutstanding" value="0" />
                                                <c:forEach var="c" items="${customers}">
                                                    <c:set var="calc" value="${reportType == 'outstanding' ? c.outstandingBalance : (c.totalInvoiced - c.totalPaid)}" />
                                                    <c:set var="sumOutstanding" value="${sumOutstanding + calc}" />
                                                </c:forEach>
                                                <fmt:formatNumber value="${sumOutstanding}" pattern="#,###"/> VND
                                            </td>
                                            <td></td>
                                        </tr>
                                        </tfoot>
                                    </c:if>
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
        alert("Function are going to be developed soon for customer ID: " + customerID);
    }
</script>

<jsp:include page="footer.jsp"/>