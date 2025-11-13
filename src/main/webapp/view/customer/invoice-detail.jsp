<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Invoice Details #${invoice.invoiceNumber}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
    <style>
        .invoice-header { background-color: #f8f9fa; padding: 2rem 0; }
        .invoice-details dl { margin-bottom: 0; }
        .invoice-items th { background-color: #e9ecef; }
        .status-badge { font-size: 0.9em; padding: 0.4em 0.8em; border-radius: 0.25rem; color: white; }
        .status-unpaid { background-color: #ffc107; color: #000 !important; }
        .status-partially_paid { background-color: #fd7e14; }
        .status-paid { background-color: #198754; }
        .status-void { background-color: #6c757d; }
        body { padding-top: 56px; }

        @media print {
            .no-print { display: none; }
            body { padding-top: 0; }
        }
    </style>
</head>
<body class="bg-light">

<div class="container my-5">
    <c:choose>
        <%-- Check if invoice object exists --%>
        <c:when test="${not empty invoice}">
            <div class="card shadow-sm">
                <div class="card-header invoice-header text-center">
                    <h1 class="display-6 mb-0">INVOICE</h1>
                    <p class="lead text-muted">#${invoice.invoiceNumber}</p>
                </div>
                <div class="card-body p-4">

                        <%-- Display any error messages from the servlet --%>
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle me-2"></i>${sessionScope.errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>

                        <%-- Display success messages (e.g., after a payment) --%>
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle me-2"></i>${sessionScope.successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>

                        <%-- Invoice Details Section --%>
                    <div class="row mb-4 invoice-details">
                        <div class="col-md-6">
                            <h5>Billed To:</h5>
                                <%--
                                  LƯU Ý (VẤN ĐỀ 1):
                                  Nếu thông tin này bị trống, ông bạn cần kiểm tra
                                  LoginServlet và CustomerDAO.java
                                --%>
                            <c:choose>
                                <c:when test="${not empty customer}">
                                    <p class="mb-1"><strong>Name:</strong> <c:out value="${customer.fullName}"/></p>
                                    <p class="mb-1"><strong>Email:</strong> <c:out value="${customer.email}"/></p>
                                    <p class="mb-0"><strong>Phone:</strong> <c:out value="${customer.phoneNumber}"/></p>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted mb-0">Customer details not available.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <dl class="row">
                                <dt class="col-sm-6 col-md-5">Invoice Date:</dt>
                                <dd class="col-sm-6 col-md-7">
                                    <fmt:formatDate value="${invoice.invoiceDate}" pattern="dd/MM/yyyy"/>
                                </dd>
                                <dt class="col-sm-6 col-md-5">Due Date:</dt>
                                <dd class="col-sm-6 col-md-7">
                                    <c:if test="${not empty invoice.dueDate}">
                                        <fmt:formatDate value="${invoice.dueDate}" pattern="dd/MM/yyyy"/>
                                    </c:if>
                                </dd>
                                <dt class="col-sm-6 col-md-5">Work Order #:</dt>
                                <dd class="col-sm-6 col-md-7">${invoice.workOrderID}</dd>
                            </dl>
                        </div>
                    </div>

                        <%-- Invoice Items Section --%>
                    <h5 class="mb-3">Invoice Items</h5>
                    <div class="table-responsive mb-4 invoice-items">
                        <table class="table table-bordered">
                            <thead class="table-light">
                            <tr>
                                <th class="text-start">Description</th>
                                <th class="text-center">Quantity</th>
                                <th class="text-end">Unit Price</th>
                                <th class="text-end">Amount</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:choose>
                                <c:when test="${not empty items}">
                                    <c:forEach var="item" items="${items}">
                                        <tr>
                                            <td><c:out value="${item.description}"/></td>
                                            <td class="text-center">
                                                <fmt:formatNumber value="${item.quantity}" pattern="#,##0.00"/>
                                            </td>
                                            <td class="text-end">
                                                <fmt:formatNumber value="${item.unitPrice}" pattern="#,###"/> VND
                                            </td>
                                            <td class="text-end">
                                                <fmt:formatNumber value="${item.amount}" pattern="#,###"/> VND
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="text-center text-muted">No items found for this invoice.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                            </tbody>
                        </table>
                    </div>

                        <%-- Totals Section --%>
                    <div class="row justify-content-end mb-4">
                        <div class="col-md-6 col-lg-5">
                            <table class="table table-sm table-borderless mb-0">
                                <tbody>
                                <tr>
                                    <th scope="row">Subtotal:</th>
                                    <td class="text-end">
                                        <fmt:formatNumber value="${invoice.subtotal}" pattern="#,###"/> VND
                                    </td>
                                </tr>
                                <tr>
                                    <th scope="row">Tax (10%):</th>
                                    <td class="text-end">
                                        <fmt:formatNumber value="${invoice.taxAmount}" pattern="#,###"/> VND
                                    </td>
                                </tr>
                                <tr class="border-top">
                                    <th scope="row" class="fs-5 pt-2">Total Amount:</th>
                                    <td class="text-end fs-5 fw-bold pt-2">
                                        <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,###"/> VND
                                    </td>
                                </tr>
                                <tr>
                                    <th scope="row">Amount Paid:</th>
                                    <td class="text-end text-success">
                                        <fmt:formatNumber value="${invoice.paidAmount}" pattern="#,###"/> VND
                                    </td>
                                </tr>
                                <tr class="border-top">
                                    <th scope="row" class="fs-5 text-danger pt-2">Balance Due:</th>
                                    <td class="text-end fs-5 fw-bold text-danger pt-2">
                                        <fmt:formatNumber value="${invoice.balanceAmount}" pattern="#,###"/> VND
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                        <%-- Payment History Section --%>
                    <c:if test="${not empty payments}">
                        <h5 class="mb-3 mt-4">Payment History</h5>
                        <div class="table-responsive mb-4">
                            <table class="table table-striped">
                                <thead>
                                <tr>
                                    <th>Payment Date</th>
                                    <th class="text-end">Amount</th>
                                    <th>Method</th>
                                    <th>Reference No</th>
                                    <th>Note</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="payment" items="${payments}">
                                    <tr>
                                        <td>
                                            <fmt:formatDate value="${payment.paymentDate}" pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                        <td class="text-end text-success fw-bold">
                                            <fmt:formatNumber value="${payment.amount}" pattern="#,###"/> VND
                                        </td>
                                        <td>
                                            <span class="badge ${payment.method == 'ONLINE' ? 'bg-info' : 'bg-secondary'}">
                                                    ${payment.method}
                                            </span>
                                        </td>
                                        <td class="font-monospace">${payment.referenceNo}</td>
                                        <td class="text-muted">${payment.note}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>

                        <%-- Notes Section --%>
                    <c:if test="${not empty invoice.notes}">
                        <div class="alert alert-warning" role="alert">
                            <h6 class="alert-heading"><i class="bi bi-sticky me-2"></i>Notes:</h6>
                            <p class="mb-0">${invoice.notes}</p>
                        </div>
                    </c:if>

                        <%-- Payment Status and Actions --%>
                    <div class="d-flex justify-content-between align-items-center border-top pt-3 no-print">
                        <div>
                            <h5>Payment Status:
                                <span class="badge status-badge status-${fn:toLowerCase(fn:replace(invoice.paymentStatus, '_', '_'))}">
                <c:out value="${fn:replace(invoice.paymentStatus, '_',' ')}"/>
            </span>
                            </h5>
                        </div>
                        <div class="mt-2 mt-md-0">
                                <%--
                                  FIX VẤN ĐỀ 2:
                                  Nút "Back" đã được thay đổi tại đây.
                                --%>
                            <a href="${pageContext.request.contextPath}/customer/repair-tracker?id=${invoice.workOrderID}"
                               class="btn btn-outline-secondary me-2">
                                <i class="bi bi-arrow-left"></i> Back to Tracker
                            </a>

                                <%-- Show Pay (QR Code) ONLY if NOT fully paid --%>
                            <c:if test="${invoice.paymentStatus != 'PAID' && invoice.paymentStatus != 'VOID'}">
                                <button type="button" class="btn btn-info me-2" onclick="showQRCode()">
                                    <i class="bi bi-qr-code"></i> Pay with QR Code
                                </button>
                            </c:if>

                                <%-- Print button - always show --%>
                            <button type="button" class="btn btn-secondary me-2" onclick="window.print();">
                                <i class="bi bi-printer"></i> Print
                            </button>
                        </div>
                    </div>

                        <%-- QR Code Modal (For Payment) --%>
                    <div class="modal fade" id="qrCodeModal" tabindex="-1" aria-labelledby="qrCodeModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-dialog-centered">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="qrCodeModalLabel">Scan VietQR Code to Pay</h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body text-center">
                                    <div id="qrCodeLoading" class="spinner-border text-primary mb-3" role="status">
                                        <span class="visually-hidden">Loading...</span>
                                    </div>
                                    <img id="qrCodeImage" src="" alt="VietQR Code" class="img-fluid mb-3 d-none" style="max-width: 300px;">
                                    <p><strong>Amount:</strong> <fmt:formatNumber value="${invoice.balanceAmount}" pattern="#,###"/> VND</p>
                                    <p><strong>Content:</strong> INV-${invoice.invoiceNumber}</p>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </c:when>

        <%-- Case where invoice object is null or error occurred --%>
        <c:otherwise>
            <div class="alert alert-warning">
                <i class="bi bi-exclamation-triangle me-2"></i>
                <c:choose>
                    <c:when test="${not empty sessionScope.errorMessage}">
                        Error loading invoice: ${sessionScope.errorMessage}
                        <c:remove var="errorMessage" scope="session"/>
                    </c:when>
                    <c:otherwise>
                        Invoice not found or could not be loaded.
                    </c:otherwise>
                </c:choose>
            </div>
            <%-- Point back to Home or Customer Dashboard --%>
            <a href="${pageContext.request.contextPath}/Home" class="btn btn-primary">
                <i class="bi bi-arrow-left me-2"></i>Back to Home
            </a>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function showQRCode() {
        const qrImage = document.getElementById('qrCodeImage');
        const qrLoading = document.getElementById('qrCodeLoading');
        const modal = new bootstrap.Modal(document.getElementById('qrCodeModal'));

        qrLoading.classList.remove('d-none');
        qrLoading.classList.add('d-block');
        qrImage.classList.add('d-none');
        qrImage.src = '';
        modal.show();

        fetch('${pageContext.request.contextPath}/accountant/generateQR?invoiceId=${invoice.invoiceID}')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.success && data.qrImageUrl) {
                    qrImage.src = data.qrImageUrl;
                    qrLoading.classList.add('d-none');
                    qrLoading.classList.remove('d-block');
                    qrImage.classList.remove('d-none');
                } else {
                    throw new Error(data.message || 'Failed to generate QR code');
                }
            })
            .catch(error => {
                console.error('Error fetching QR code:', error);
                qrLoading.classList.remove('spinner-border', 'text-primary');
                qrLoading.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-triangle"></i> Failed to load QR code. ' + error.message + '</span>';
                qrLoading.classList.add('d-block');
                qrImage.classList.add('d-none');
            });
    }
</script>
</body>
</html>