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
                        <p class="text-muted mb-0">List of invoices past due</p>
                    </div>

                    <!-- Alert Summary -->
                    <div class="alert alert-danger d-flex align-items-center mb-4"
                         role="alert"
                         style="border-radius: 12px; border: none; background-color: #fee2e2;">
                        <i class="bi bi-exclamation-triangle-fill me-3" style="font-size: 2rem; color: #dc2626;"></i>
                        <div>
                            <h5 class="alert-heading mb-2" style="color: #991b1b;">
                                Overdue Receivables Warning
                            </h5>
                            <c:forEach var="summary" items="${overdueReport}">
                                <p class="mb-0" style="color: #991b1b;">
                                    There are <strong>${summary.count} invoices</strong> overdue
                                    with total value <strong><fmt:formatNumber value="${summary.amount}" pattern="#,###"/> VND</strong>
                                </p>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- Quick Action Buttons -->
                    <div class="d-flex gap-2 mb-4">
                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=overdue"
                           class="btn btn-danger"
                           style="border-radius: 8px;">
                            <i class="bi bi-list-check me-2"></i>View all overdue invoices
                        </a>
                        <button class="btn btn-outline-primary" onclick="sendReminders()" style="border-radius: 8px;">
                            <i class="bi bi-send me-2"></i>Send bulk reminders
                        </button>
                        <button class="btn btn-outline-success" onclick="exportOverdueReport()" style="border-radius: 8px;">
                            <i class="bi bi-file-earmark-excel me-2"></i>Export report
                        </button>
                    </div>

                    <!-- Info Card -->
                    <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                        <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                            <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                <i class="bi bi-info-circle me-2" style="color: #667eea;"></i>
                                Hướng dẫn Xử lý
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

<script src="${pageContext.request.contextPath}/assets/js/accountant/report.js"></script>

<jsp:include page="footer.jsp"/>