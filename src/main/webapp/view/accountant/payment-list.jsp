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
                            <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                                <i class="fas fa-money-bill-wave me-2" style="color: #059669;"></i>
                                Payment History
                            </h2>
                            <p class="text-muted mb-0">Manage all payment transactions</p>
                        </div>
                        <a href="${pageContext.request.contextPath}/accountant/invoice"
                           class="btn btn-primary"
                           style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                  border: none;
                                  padding: 0.625rem 1.5rem;
                                  border-radius: 8px;">
                            <i class="fas fa-arrow-left me-2"></i>Back to Invoices
                        </a>
                    </div>

                    <!-- Payment Table -->
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead style="background-color: #f9fafb; border-bottom: 2px solid #e5e7eb;">
                            <tr>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Payment ID</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Invoice</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Payment Date</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right;">Amount</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center;">Method</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151;">Reference No</th>
                                <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center;">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="payment" items="${payments}">
                                <tr style="transition: all 0.2s;"
                                    onmouseover="this.style.backgroundColor='#f9fafb'"
                                    onmouseout="this.style.backgroundColor='white'">
                                    <td style="padding: 1rem;">
                                        <span class="badge bg-secondary">#${payment.paymentID}</span>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${payment.invoiceID}"
                                           style="color: #667eea; text-decoration: none; font-weight: 600;">
                                            Invoice #${payment.invoiceID}
                                        </a>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <i class="fas fa-calendar-alt me-1 text-muted"></i>
                                        <fmt:formatDate value="${payment.paymentDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td style="padding: 1rem; text-align: right;">
                                        <strong style="color: #059669; font-size: 1.1rem;">
                                            <fmt:formatNumber value="${payment.amount}" pattern="#,###"/> đ
                                        </strong>
                                    </td>
                                    <td style="padding: 1rem; text-align: center;">
                                        <c:choose>
                                            <c:when test="${payment.method == 'ONLINE'}">
                                                    <span class="badge"
                                                          style="background-color: #3b82f6;
                                                                 padding: 0.5rem 1rem;
                                                                 border-radius: 20px;">
                                                        <i class="fas fa-globe me-1"></i>Online
                                                    </span>
                                            </c:when>
                                            <c:otherwise>
                                                    <span class="badge"
                                                          style="background-color: #6b7280;
                                                                 padding: 0.5rem 1rem;
                                                                 border-radius: 20px;">
                                                        <i class="fas fa-money-bill-wave me-1"></i>Offline
                                                    </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="padding: 1rem;">
                                        <code style="background-color: #f3f4f6;
                                                         padding: 0.25rem 0.5rem;
                                                         border-radius: 4px;">
                                                ${payment.referenceNo}
                                        </code>
                                    </td>
                                    <td style="padding: 1rem; text-align: center;">
                                        <div class="btn-group" role="group">
                                            <a href="${pageContext.request.contextPath}/accountant/payment?action=view&id=${payment.paymentID}"
                                               class="btn btn-sm btn-outline-success"
                                               title="View Receipt"
                                               style="border-radius: 6px 0 0 6px;">
                                                <i class="fas fa-receipt"></i> View Receipt
                                            </a>
                                            <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${payment.invoiceID}"
                                               class="btn btn-sm btn-outline-primary"
                                               title="View Invoice"
                                               style="border-radius: 0 6px 6px 0;">
                                                <i class="fas fa-file-invoice"></i> View Invoice
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty payments}">
                                <tr>
                                    <td colspan="7" style="padding: 3rem; text-align: center;">
                                        <i class="fas fa-receipt fa-4x mb-3" style="color: #d1d5db;"></i>
                                        <p class="text-muted mb-0">No payments yet</p>
                                    </td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>