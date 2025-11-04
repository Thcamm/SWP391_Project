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
                        <a href="${pageContext.request.contextPath}/accountant/invoice"
                           class="btn btn-outline-secondary mb-3"
                           style="border-radius: 8px;">
                            <i class="fas fa-arrow-left me-2"></i>Back
                        </a>
                        <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                            <i class="fas fa-file-invoice-dollar me-2" style="color: #667eea;"></i>
                            Create New Invoice
                        </h2>
                        <p class="text-muted mb-0">Select a completed Work Order to create an invoice</p>
                    </div>

                    <!-- Error Messages -->
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Work Orders List -->
                    <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                        <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                            <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                <i class="fas fa-tasks me-2" style="color: #667eea;"></i>
                                Completed Work Orders without Invoice
                            </h5>
                        </div>
                        <div class="card-body p-0">
                            <c:if test="${not empty workOrders}">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0">
                                        <thead style="background-color: #f9fafb;">
                                        <tr>
                                            <th style="padding: 1rem; color: #374151; font-weight: 600; border-top: none;">Work Order ID</th>
                                            <th style="padding: 1rem; color: #374151; font-weight: 600; border-top: none;">Request ID</th>
                                            <th style="padding: 1rem; color: #374151; font-weight: 600; border-top: none;">Created Date</th>
                                            <th style="padding: 1rem; color: #374151; font-weight: 600; text-align: right; border-top: none;">Estimate</th>
                                            <th style="padding: 1rem; color: #374151; font-weight: 600; text-align: center; border-top: none;">Status</th>
                                            <th style="padding: 1rem; color: #374151; font-weight: 600; text-align: center; border-top: none;">Action</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="wo" items="${workOrders}">
                                            <tr>
                                                <td style="padding: 1rem;">
                                                        <span class="badge bg-primary" style="font-size: 0.9rem;">
                                                            #${wo.workOrderId}
                                                        </span>
                                                </td>
                                                <td style="padding: 1rem;">
                                                        <span class="badge bg-secondary">
                                                            #${wo.requestId}
                                                        </span>
                                                </td>
                                                <td style="padding: 1rem;">
                                                    <i class="fas fa-calendar-alt me-1 text-muted"></i>
                                                    <fmt:formatDate value="${wo.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                </td>
                                                <td style="padding: 1rem; text-align: right;">
                                                    <strong style="color: #111827;">
                                                        <fmt:formatNumber value="${wo.estimateAmount}" pattern="#,###"/> đ
                                                    </strong>
                                                </td>
                                                <td style="padding: 1rem; text-align: center;">
                                                        <span class="badge bg-success" style="padding: 0.5rem 1rem; border-radius: 20px;">
                                                            <i class="fas fa-check-circle me-1"></i>COMPLETE
                                                        </span>
                                                </td>
                                                <td style="padding: 1rem; text-align: center;">
                                                    <form action="${pageContext.request.contextPath}/accountant/invoice"
                                                          method="post"
                                                          onsubmit="return confirm('Confirm creating invoice for Work Order #${wo.workOrderId}?');">
                                                        <input type="hidden" name="action" value="create">
                                                        <input type="hidden" name="workOrderID" value="${wo.workOrderId}">
                                                        <button type="submit"
                                                                class="btn btn-success"
                                                                style="border-radius: 8px; padding: 0.5rem 1.5rem;">
                                                            <i class="fas fa-file-invoice me-2"></i>Create Invoice
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:if>

                            <c:if test="${empty workOrders}">
                                <div class="text-center py-5">
                                    <i class="fas fa-inbox fa-4x mb-3" style="color: #d1d5db;"></i>
                                    <h5 class="text-muted mb-2">No Work Orders Available</h5>
                                    <p class="text-muted mb-0">
                                        All completed Work Orders already have invoices or no Work Orders have been completed yet.
                                    </p>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Info Box -->
                    <div class="alert alert-info mt-4"
                         role="alert"
                         style="border-radius: 12px; border: none; background-color: #e0f2fe;">
                        <div class="d-flex">
                            <div class="me-3">
                                <i class="fas fa-info-circle fa-2x" style="color: #0284c7;"></i>
                            </div>
                            <div>
                                <h6 class="alert-heading mb-2" style="color: #0c4a6e;">
                                    <i class="fas fa-lightbulb me-1"></i>Instructions
                                </h6>
                                <ul class="mb-0" style="color: #075985;">
                                    <li>Only Work Orders with <strong>COMPLETE</strong> status can create invoices</li>
                                    <li>Each Work Order can only create <strong>1 unique invoice</strong></li>
                                    <li>Invoice will be automatically calculated from work details and parts used</li>
                                    <li>10% VAT tax will be applied automatically</li>
                                    <li>Default payment term is <strong>30 days</strong> from invoice creation date</li>
                                </ul>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>