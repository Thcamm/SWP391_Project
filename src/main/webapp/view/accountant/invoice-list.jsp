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

                    <!-- Success/Error Messages -->
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>${sessionScope.successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>

                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>${sessionScope.errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>

                    <!-- Page Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                                <i class="fas fa-file-invoice-dollar me-2" style="color: #667eea;"></i>
                                <c:choose>
                                    <c:when test="${isOverdueList}">Overdue Invoices</c:when>
                                    <c:otherwise>Invoice Management</c:otherwise>
                                </c:choose>
                            </h2>
                            <p class="text-muted mb-0">Manage and track payment invoices</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=create"
                           class="btn btn-primary"
                           style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none; padding: 0.625rem 1.5rem; border-radius: 8px;">
                            <i class="fas fa-plus me-2"></i>Create New Invoice
                        </a>
                    </div>

                    <!-- Filter Section -->
                    <div class="card mb-4" style="border: 1px solid #e5e7eb; border-radius: 8px;">
                        <div class="card-body" style="padding: 1.5rem;">
                            <div class="row g-3">
                                <!-- Search -->
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold" style="color: #374151;">
                                        <i class="bi bi-search me-1"></i>Search
                                    </label>
                                    <form action="${pageContext.request.contextPath}/accountant/invoice" method="get">
                                        <input type="hidden" name="action" value="search">
                                        <div class="input-group">
                                            <input type="text"
                                                   class="form-control"
                                                   name="keyword"
                                                   placeholder="Enter invoice code..."
                                                   value="${keyword}"
                                                   style="border-radius: 6px 0 0 6px; border-right: none;">
                                            <button class="btn btn-primary"
                                                    type="submit"
                                                    style="border-radius: 0 6px 6px 0;
                           background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                           border: none;
                           padding: 0 1.25rem;">
                                                <i class="bi bi-search"></i>
                                            </button>
                                        </div>
                                    </form>
                                </div>

                                <!-- Status Filter -->
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold" style="color: #374151;">
                                        <i class="fas fa-filter me-1"></i>Filter by Status
                                    </label>
                                    <select class="form-select"
                                            id="statusFilter"
                                            onchange="filterByStatus(this.value)"
                                            style="border-radius: 6px;">
                                        <option value="ALL" ${empty selectedStatus || selectedStatus == 'ALL' ? 'selected' : ''}>All</option>
                                        <option value="UNPAID" ${selectedStatus == 'UNPAID' ? 'selected' : ''}>Unpaid</option>
                                        <option value="PARTIALLY_PAID" ${selectedStatus == 'PARTIALLY_PAID' ? 'selected' : ''}>Partially Paid</option>
                                        <option value="PAID" ${selectedStatus == 'PAID' ? 'selected' : ''}>Paid</option>
                                        <option value="VOID" ${selectedStatus == 'VOID' ? 'selected' : ''}>Voided</option>
                                    </select>
                                </div>

                                <!-- Quick Actions -->
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold" style="color: #374151;">
                                        <i class="fas fa-bolt me-1"></i>Quick Actions
                                    </label>
                                    <div class="d-grid gap-2">
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/accountant/invoice?action=overdue"
                                               class="btn btn-warning"
                                               style="border-radius: 6px 0 0 6px;">
                                                <i class="fas fa-exclamation-triangle me-1"></i>Overdue
                                            </a>
                                            <a href="${pageContext.request.contextPath}/accountant/invoice"
                                               class="btn btn-secondary"
                                               style="border-radius: 0 6px 6px 0;">
                                                <i class="fas fa-redo me-1"></i>Refresh
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Invoice Table -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle" id="invoiceTable">
                            <thead style="background-color: #f9fafb; border-bottom: 2px solid #e5e7eb;">
                            <tr>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Invoice #</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Work Order</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Created Date</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Due Date</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right;">Total Amount</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right;">Paid</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right;">Balance</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center;">Status</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center;">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="invoice" items="${invoices}">
                                <tr>
                                    <!-- Invoice Number -->
                                    <td>
                                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${invoice.invoiceID}"
                                           class="text-primary fw-semibold">
                                                ${invoice.invoiceNumber}
                                        </a>
                                    </td>

                                    <!-- Work Order -->
                                    <td>
                                        <span class="badge bg-secondary">#${invoice.workOrderID}</span>
                                    </td>

                                    <!-- Created Date -->
                                    <td>
                                        <fmt:formatDate value="${invoice.invoiceDate}" pattern="dd/MM/yyyy"/>
                                    </td>

                                    <!-- Due Date -->
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty invoice.dueDate}">
                                                <fmt:formatDate value="${invoice.dueDate}" pattern="dd/MM/yyyy"/>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">N/A</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <!-- Total Amount - HANDLE NULL -->
                                    <td class="text-end">
                                        <c:choose>
                                            <c:when test="${not empty invoice.totalAmount}">
                                                <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,###"/> VND
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-danger">Error</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <!-- Paid Amount - HANDLE NULL -->
                                    <td class="text-end">
                                        <c:choose>
                                            <c:when test="${not empty invoice.paidAmount}">
                                                <fmt:formatNumber value="${invoice.paidAmount}" pattern="#,###"/> VND
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">0 VND</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <!-- Balance - HANDLE NULL -->
                                    <td class="text-end">
                                        <c:choose>
                                            <c:when test="${not empty invoice.balanceAmount}">
                        <span class="${invoice.balanceAmount > 0 ? 'text-danger fw-bold' : 'text-success'}">
                            <fmt:formatNumber value="${invoice.balanceAmount}" pattern="#,###"/> VND
                        </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-danger">Error</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <!-- Status - HANDLE NULL -->
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty invoice.paymentStatus}">
                        <span class="badge ${invoice.paymentStatus == 'PAID' ? 'bg-success' :
                                          invoice.paymentStatus == 'PARTIALLY_PAID' ? 'bg-warning' :
                                          invoice.paymentStatus == 'VOID' ? 'bg-secondary' : 'bg-danger'}">
                                ${invoice.paymentStatus}
                        </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">ERROR</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <!-- Actions -->
                                    <td>
                                        <div class="btn-group" role="group">
                                            <!-- View Button -->
                                            <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${invoice.invoiceID}"
                                               class="btn btn-sm btn-outline-primary"
                                               title="View Details">
                                                <i class="bi bi-eye"></i>
                                            </a>

                                            <!-- Delete Button (for ERROR invoices) -->
                                            <c:if test="${empty invoice.totalAmount or empty invoice.paymentStatus}">
                                                <button type="button"
                                                        class="btn btn-sm btn-outline-danger"
                                                        onclick="deleteInvoice(${invoice.invoiceID})"
                                                        title="Delete Error Invoice">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/accountant/invoice?page=${currentPage - 1}&status=${selectedStatus}"
                                       style="border-radius: 6px 0 0 6px;">
                                        <i class="fas fa-chevron-left"></i> Previous
                                    </a>
                                </li>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link"
                                           href="${pageContext.request.contextPath}/accountant/invoice?page=${i}&status=${selectedStatus}">
                                                ${i}
                                        </a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link"
                                       href="${pageContext.request.contextPath}/accountant/invoice?page=${currentPage + 1}&status=${selectedStatus}"
                                       style="border-radius: 0 6px 6px 0;">
                                        Next <i class="fas fa-chevron-right"></i>
                                    </a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>

                </div>
            </main>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/accountant/invoice.js"></script>

<jsp:include page="footer.jsp"/>