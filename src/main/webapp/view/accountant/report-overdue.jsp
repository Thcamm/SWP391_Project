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
                    <div class="mb-4">
                        <a href="${pageContext.request.contextPath}/accountant/report"
                           class="btn btn-outline-secondary mb-3"
                           style="border-radius: 8px;">
                            <i class="bi bi-arrow-left me-2"></i>Back to Dashboard
                        </a>
                        <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                            <i class="bi bi-clock-history me-2" style="color: #dc2626;"></i>
                            Overdue Invoice Report
                        </h2>
                        <p class="text-muted mb-0">Invoices past their due date requiring attention</p>
                    </div>

                    <!-- Alert Summary - FIXED -->
                    <div class="alert alert-danger d-flex align-items-center mb-4"
                         role="alert"
                         style="border-radius: 12px; border: none; background-color: #fee2e2;">
                        <i class="bi bi-exclamation-triangle-fill me-3" style="font-size: 2rem; color: #dc2626;"></i>
                        <div>
                            <h5 class="alert-heading mb-2" style="color: #991b1b;">
                                Overdue Receivables Warning
                            </h5>
                            <c:choose>
                                <c:when test="${not empty overdueInvoices && overdueInvoices.count > 0}">
                                    <p class="mb-0" style="color: #991b1b;">
                                        There are <strong>${overdueInvoices.count} invoices</strong> overdue
                                        with total value <strong><fmt:formatNumber value="${overdueInvoices.amount}" pattern="#,###"/> VND</strong>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <p class="mb-0" style="color: #059669;">
                                        <i class="bi bi-check-circle me-2"></i>
                                        Great! No overdue invoices at this time.
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Quick Action Buttons -->
                    <div class="d-flex gap-2 mb-4">
                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=overdue"
                           class="btn btn-danger"
                           style="border-radius: 8px;">
                            <i class="bi bi-list-check me-2"></i>View all overdue invoices
                        </a>
                        <button class="btn btn-outline-primary"
                                onclick="sendReminders()"
                                style="border-radius: 8px;"
                        ${empty customersWithOutstanding ? 'disabled' : ''}>
                            <i class="bi bi-send me-2"></i>Send bulk reminders
                        </button>
                        <button class="btn btn-outline-success"
                                onclick="exportOverdueReport()"
                                style="border-radius: 8px;">
                            <i class="bi bi-file-earmark-excel me-2"></i>Export report
                        </button>
                    </div>

                    <!-- Customers with Outstanding Table -->
                    <c:if test="${not empty customersWithOutstanding}">
                        <div class="card mb-4" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                            <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                    <i class="bi bi-people me-2" style="color: #f59e0b;"></i>
                                    Customers with Outstanding Balance
                                </h5>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead style="background-color: #f9fafb;">
                                        <tr>
                                            <th style="padding: 1rem; border-top: none;">#</th>
                                            <th style="padding: 1rem; border-top: none;">Customer</th>
                                            <th style="padding: 1rem; border-top: none;">Contact</th>
                                            <th style="padding: 1rem; border-top: none; text-align: right;">Invoices</th>
                                            <th style="padding: 1rem; border-top: none; text-align: right;">Outstanding</th>
                                            <th style="padding: 1rem; border-top: none; text-align: center;">Action</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="customer" items="${customersWithOutstanding}" varStatus="status">
                                            <tr>
                                                <td style="padding: 1rem;">${status.index + 1}</td>
                                                <td style="padding: 1rem;">
                                                    <div class="fw-semibold">${customer.customerName}</div>
                                                    <small class="text-muted">ID: ${customer.customerID}</small>
                                                </td>
                                                <td style="padding: 1rem;">
                                                    <div>
                                                        <i class="bi bi-envelope me-1"></i>
                                                        <small>${customer.customerEmail}</small>
                                                    </div>
                                                    <c:if test="${not empty customer.phoneNumber}">
                                                        <div>
                                                            <i class="bi bi-telephone me-1"></i>
                                                            <small>${customer.phoneNumber}</small>
                                                        </div>
                                                    </c:if>
                                                </td>
                                                <td style="padding: 1rem; text-align: right;">
                                                    <span class="badge bg-primary">${customer.totalInvoices}</span>
                                                </td>
                                                <td style="padding: 1rem; text-align: right;">
                                                    <strong class="text-danger">
                                                        <fmt:formatNumber value="${customer.outstandingBalance}" pattern="#,###"/> VND
                                                    </strong>
                                                </td>
                                                <td style="padding: 1rem; text-align: center;">
                                                    <a href="${pageContext.request.contextPath}/accountant/report?action=customer&type=outstanding"
                                                       class="btn btn-sm btn-outline-primary"
                                                       style="border-radius: 6px;">
                                                        <i class="bi bi-eye"></i> View
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                        <tfoot style="background-color: #f9fafb; font-weight: bold;">
                                        <tr>
                                            <td colspan="4" style="padding: 1rem; text-align: right;">Total:</td>
                                            <td style="padding: 1rem; text-align: right; color: #dc2626;">
                                                <c:set var="totalOutstanding" value="0" />
                                                <c:forEach var="customer" items="${customersWithOutstanding}">
                                                    <c:set var="totalOutstanding" value="${totalOutstanding + customer.outstandingBalance}" />
                                                </c:forEach>
                                                <fmt:formatNumber value="${totalOutstanding}" pattern="#,###"/> VND
                                            </td>
                                            <td></td>
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Info Card -->
                    <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                        <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                            <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                <i class="bi bi-info-circle me-2" style="color: #667eea;"></i>
                                Collection Process Guidelines
                            </h5>
                        </div>
                        <div class="card-body" style="padding: 1.5rem;">
                            <div class="row">
                                <div class="col-md-4">
                                    <div class="d-flex align-items-start gap-3">
                                        <div class="p-3" style="background-color: #dbeafe; border-radius: 12px;">
                                            <i class="bi bi-1-circle" style="font-size: 1.5rem; color: #3b82f6;"></i>
                                        </div>
                                        <div>
                                            <h6 class="mb-2" style="color: #111827;">Contact Customer</h6>
                                            <p class="text-muted mb-0" style="font-size: 0.875rem;">
                                                Send email or call customers to remind about overdue invoices
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="d-flex align-items-start gap-3">
                                        <div class="p-3" style="background-color: #fef3c7; border-radius: 12px;">
                                            <i class="bi bi-2-circle" style="font-size: 1.5rem; color: #f59e0b;"></i>
                                        </div>
                                        <div>
                                            <h6 class="mb-2" style="color: #111827;">Negotiate</h6>
                                            <p class="text-muted mb-0" style="font-size: 0.875rem;">
                                                Investigate reasons for late payment and propose solutions
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="d-flex align-items-start gap-3">
                                        <div class="p-3" style="background-color: #d1fae5; border-radius: 12px;">
                                            <i class="bi bi-3-circle" style="font-size: 1.5rem; color: #059669;"></i>
                                        </div>
                                        <div>
                                            <h6 class="mb-2" style="color: #111827;">Record Payment</h6>
                                            <p class="text-muted mb-0" style="font-size: 0.875rem;">
                                                Update records immediately when payment is received from the customer
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<script>
    function sendReminders() {
        const customerCount = ${not empty customersWithOutstanding ? customersWithOutstanding.size() : 0};

        if (customerCount === 0) {
            alert('No customers with outstanding balance.');
            return;
        }

        if (confirm('Send reminder emails to ' + customerCount + ' customers with overdue invoices?')) {
            alert('Feature is under development!\nWill send emails to ' + customerCount + ' customers.');
        }
    }

    function exportOverdueReport() {
        const reportDate = new Date().toISOString().split('T')[0];
        alert('Exporting overdue report for ' + reportDate + '...\nFeature is under development!');
    }
</script>

<jsp:include page="footer.jsp"/>