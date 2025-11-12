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

                    <!-- Action Buttons -->
                    <div class="d-flex justify-content-between align-items-center mb-4 no-print">
                        <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${invoice.invoiceID}"
                           class="btn btn-outline-secondary"
                           style="border-radius: 8px; padding: 0.625rem 1.25rem;">
                            <i class="fas fa-arrow-left me-2"></i>Back to Invoice
                        </a>
                        <div>
                            <button onclick="window.print()"
                                    class="btn btn-primary"
                                    style="border-radius: 8px; padding: 0.625rem 1.25rem;">
                                <i class="fas fa-print me-2"></i>Print Receipt
                            </button>
                        </div>
                    </div>

                    <!-- Payment Receipt -->
                    <div id="printableArea">
                        <!-- Receipt Header -->
                        <div class="text-center mb-4 pb-4" style="border-bottom: 3px double #e5e7eb;">
                            <h1 class="mb-2" style="font-size: 2.5rem; font-weight: 700; color: #111827;">
                                <i class="fas fa-receipt me-2" style="color: #059669;"></i>
                                PAYMENT RECEIPT
                            </h1>
                            <p class="text-muted mb-0">Payment Receipt</p>
                            <div class="mt-3">
                                <span class="badge bg-success"
                                      style="font-size: 1rem; padding: 0.75rem 1.5rem; border-radius: 25px;">
                                    <i class="fas fa-check-circle me-2"></i>PAID
                                </span>
                            </div>
                        </div>

                        <!-- Payment Details Card -->
                        <div class="row mb-4">
                            <!-- Left Column - Payment Info -->
                            <div class="col-md-6">
                                <div class="card" style="border: 2px solid #e5e7eb; border-radius: 12px; height: 100%;">
                                    <div class="card-header"
                                         style="background: linear-gradient(135deg, #059669 0%, #047857 100%);
                                                color: white;
                                                border-radius: 10px 10px 0 0;
                                                padding: 1.25rem;">
                                        <h5 class="mb-0">
                                            <i class="fas fa-info-circle me-2"></i>
                                            Payment Information
                                        </h5>
                                    </div>
                                    <div class="card-body" style="padding: 2rem;">
                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-hashtag me-1"></i>Payment ID
                                            </small>
                                            <h4 class="mb-0" style="color: #059669; font-weight: 700;">
                                                #${payment.paymentID}
                                            </h4>
                                        </div>

                                        <hr style="border-color: #e5e7eb;">

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-calendar-alt me-1"></i>Payment Date
                                            </small>
                                            <p class="mb-0 fw-bold" style="color: #111827;">
                                                <fmt:formatDate value="${payment.paymentDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </p>
                                        </div>

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-money-bill-wave me-1"></i>Amount
                                            </small>
                                            <h3 class="mb-0" style="color: #059669; font-weight: 700;">
                                                <fmt:formatNumber value="${payment.amount}" pattern="#,###"/> đ
                                            </h3>
                                        </div>

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-credit-card me-1"></i>Method
                                            </small>
                                            <c:choose>
                                                <c:when test="${payment.method == 'ONLINE'}">
                                                    <span class="badge"
                                                          style="background-color: #3b82f6;
                                                                 padding: 0.75rem 1.5rem;
                                                                 border-radius: 25px;
                                                                 font-size: 1rem;">
                                                        <i class="fas fa-globe me-2"></i>Online Payment
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge"
                                                          style="background-color: #6b7280;
                                                                 padding: 0.75rem 1.5rem;
                                                                 border-radius: 25px;
                                                                 font-size: 1rem;">
                                                        <i class="fas fa-money-bill-wave me-2"></i>Cash
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-barcode me-1"></i>Reference Number
                                            </small>
                                            <code style="background-color: #f3f4f6;
                                                         padding: 0.5rem 1rem;
                                                         border-radius: 6px;
                                                         font-size: 1rem;
                                                         color: #111827;">
                                                ${payment.referenceNo}
                                            </code>
                                        </div>

                                        <c:if test="${not empty payment.note}">
                                            <hr style="border-color: #e5e7eb;">
                                            <div>
                                                <small class="text-muted d-block mb-2">
                                                    <i class="fas fa-sticky-note me-1"></i>Note
                                                </small>
                                                <div class="alert alert-info mb-0"
                                                     role="alert"
                                                     style="border-radius: 8px;
                                                            background-color: #dbeafe;
                                                            border: none;">
                                                    <i class="fas fa-comment-dots me-2"></i>
                                                        ${payment.note}
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <!-- Right Column - Invoice Info -->
                            <div class="col-md-6">
                                <div class="card" style="border: 2px solid #e5e7eb; border-radius: 12px; height: 100%;">
                                    <div class="card-header"
                                         style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                                color: white;
                                                border-radius: 10px 10px 0 0;
                                                padding: 1.25rem;">
                                        <h5 class="mb-0">
                                            <i class="fas fa-file-invoice me-2"></i>
                                            Invoice Information
                                        </h5>
                                    </div>
                                    <div class="card-body" style="padding: 2rem;">
                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-file-invoice-dollar me-1"></i>Invoice Number
                                            </small>
                                            <h4 class="mb-0">
                                                <a href="${pageContext.request.contextPath}/accountant/invoice?action=view&id=${invoice.invoiceID}"
                                                   style="color: #667eea; text-decoration: none; font-weight: 700;">
                                                    ${invoice.invoiceNumber}
                                                </a>
                                            </h4>
                                        </div>

                                        <hr style="border-color: #e5e7eb;">

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-briefcase me-1"></i>Work Order
                                            </small>
                                            <p class="mb-0 fw-bold">
                                                <span class="badge bg-secondary" style="font-size: 0.95rem;">
                                                    #${invoice.workOrderID}
                                                </span>
                                            </p>
                                        </div>

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-calculator me-1"></i>Total Invoice Amount
                                            </small>
                                            <h5 class="mb-0" style="color: #111827; font-weight: 700;">
                                                <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,###"/> đ
                                            </h5>
                                        </div>

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-check-circle me-1"></i>Amount Paid
                                            </small>
                                            <h5 class="mb-0 text-success" style="font-weight: 700;">
                                                <fmt:formatNumber value="${invoice.paidAmount}" pattern="#,###"/> đ
                                            </h5>
                                            <div class="progress mt-2" style="height: 10px; border-radius: 5px;">
                                                <div class="progress-bar bg-success"
                                                     role="progressbar"
                                                     style="width: ${invoice.paymentPercentage}%"
                                                     aria-valuenow="${invoice.paymentPercentage}"
                                                     aria-valuemin="0"
                                                     aria-valuemax="100">
                                                    <fmt:formatNumber value="${invoice.paymentPercentage}" pattern="##0.0"/>%
                                                </div>
                                            </div>
                                        </div>

                                        <div class="mb-3">
                                            <small class="text-muted d-block mb-1">
                                                <i class="fas fa-exclamation-circle me-1"></i>Balance
                                            </small>
                                            <h5 class="mb-0" style="color: #dc2626; font-weight: 700;">
                                                <fmt:formatNumber value="${invoice.balanceAmount}" pattern="#,###"/> đ
                                            </h5>
                                        </div>

                                        <hr style="border-color: #e5e7eb;">

                                        <div class="mb-0">
                                            <small class="text-muted d-block mb-2">
                                                <i class="fas fa-flag me-1"></i>Invoice Status
                                            </small>
                                            <c:choose>
                                                <c:when test="${invoice.paymentStatus == 'UNPAID'}">
                                                    <span class="badge"
                                                          style="background-color: #dc2626;
                                                                 padding: 0.75rem 1.5rem;
                                                                 border-radius: 25px;
                                                                 font-size: 1rem;">
                                                        <i class="fas fa-times-circle me-2"></i>Unpaid
                                                    </span>
                                                </c:when>
                                                <c:when test="${invoice.paymentStatus == 'PARTIALLY_PAID'}">
                                                    <span class="badge"
                                                          style="background-color: #f59e0b;
                                                                 padding: 0.75rem 1.5rem;
                                                                 border-radius: 25px;
                                                                 font-size: 1rem;">
                                                        <i class="fas fa-hourglass-half me-2"></i>Partially Paid
                                                    </span>
                                                </c:when>
                                                <c:when test="${invoice.paymentStatus == 'PAID'}">
                                                    <span class="badge"
                                                          style="background-color: #059669;
                                                                 padding: 0.75rem 1.5rem;
                                                                 border-radius: 25px;
                                                                 font-size: 1rem;">
                                                        <i class="fas fa-check-circle me-2"></i>Fully Paid
                                                    </span>
                                                </c:when>
                                                <c:when test="${invoice.paymentStatus == 'VOID'}">
                                                    <span class="badge"
                                                          style="background-color: #6b7280;
                                                                 padding: 0.75rem 1.5rem;
                                                                 border-radius: 25px;
                                                                 font-size: 1rem;">
                                                        <i class="fas fa-ban me-2"></i>Voided
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Payment Summary Box -->
                        <div class="card mb-4"
                             style="border: 3px solid #059669;
                                    border-radius: 12px;
                                    background: linear-gradient(to right, #f0fdf4, #dcfce7);">
                            <div class="card-body text-center" style="padding: 2rem;">
                                <div class="row align-items-center">
                                    <div class="col-md-4">
                                        <small class="text-muted d-block mb-1">THIS PAYMENT</small>
                                        <h2 class="mb-0" style="color: #059669; font-weight: 700;">
                                            <fmt:formatNumber value="${payment.amount}" pattern="#,###"/> đ
                                        </h2>
                                    </div>
                                    <div class="col-md-4">
                                        <i class="fas fa-arrow-right fa-2x" style="color: #6b7280;"></i>
                                    </div>
                                    <div class="col-md-4">
                                        <small class="text-muted d-block mb-1">TOTAL PAID</small>
                                        <h2 class="mb-0" style="color: #059669; font-weight: 700;">
                                            <fmt:formatNumber value="${invoice.paidAmount}" pattern="#,###"/> đ
                                        </h2>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Company Info & Signatures -->
                        <div class="row mt-5 pt-4" style="border-top: 2px dashed #e5e7eb;">
                            <div class="col-md-6">
                                <h6 class="mb-3" style="color: #111827; font-weight: 600;">
                                    <i class="fas fa-building me-2"></i>GARAGE MANAGEMENT SYSTEM
                                </h6>
                                <p class="mb-1"><i class="fas fa-map-marker-alt me-2 text-muted"></i>Address: 123 ABC Street, XYZ District</p>
                                <p class="mb-1"><i class="fas fa-phone me-2 text-muted"></i>Hotline: 1900-xxxx</p>
                                <p class="mb-1"><i class="fas fa-envelope me-2 text-muted"></i>Email: support@garage.com</p>
                                <p class="mb-0"><i class="fas fa-globe me-2 text-muted"></i>Website: www.garage.com</p>
                            </div>
                            <div class="col-md-6 text-end">
                                <p class="mb-1"><strong>Processed by:</strong> ${sessionScope.userName != null ? sessionScope.userName : 'Thcamm'}</p>
                                <p class="mb-1"><strong>Print date:</strong>
                                    <jsp:useBean id="now" class="java.util.Date"/>
                                    <fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>
                                <div class="mt-4">
                                    <p class="mb-5"><strong>Signature</strong></p>
                                    <p style="border-top: 1px solid #374151; display: inline-block; padding-top: 0.5rem; min-width: 200px;">
                                        Accountant
                                    </p>
                                </div>
                            </div>
                        </div>

                        <!-- Footer -->
                        <div class="text-center mt-5 pt-4" style="border-top: 1px solid #e5e7eb;">
                            <p class="text-muted mb-1">
                                <i class="fas fa-heart text-danger me-1"></i>
                                Thank you for using our services
                            </p>
                            <small class="text-muted">
                                This receipt is automatically generated by the system -
                                <fmt:formatDate value="${now}" pattern="dd/MM/yyyy HH:mm:ss"/>
                            </small>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<style>
    @media print {
        body * {
            visibility: hidden;
        }

        #printableArea, #printableArea * {
            visibility: visible;
        }

        #printableArea {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
            background: white;
            padding: 2rem;
        }

        .no-print {
            display: none !important;
        }

        .btn, nav, .sidebar, header, footer {
            display: none !important;
        }

        .content-card {
            border: none !important;
            box-shadow: none !important;
            padding: 20px !important;
        }

        .container-fluid, .row, .col {
            padding: 0 !important;
            margin: 0 !important;
        }

        @page {
            size: A4;
            margin: 1cm;
        }
    }
</style>

<jsp:include page="footer.jsp"/>