<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> <%-- Add JSTL functions --%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Invoice Details #${invoice.invoiceNumber}</title>
    <!-- Bootstrap 5 -->
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
        body { padding-top: 56px; /* Adjust if using a fixed navbar */ }
    </style>
</head>
<body class="bg-light">

<%-- (Optional) Include header --%>
<%-- <jsp:include page="/common/header.jsp"/> --%>

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

                        <%-- Display any error messages passed from servlet --%>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                        <%-- Invoice Details Section --%>
                    <div class="row mb-4 invoice-details">
                        <div class="col-md-6">
                            <h5>Billed To:</h5>
                                <%-- Use data from billedToCustomer attribute (fetched by servlet) --%>
                            <c:if test="${not empty billedToCustomer}">
                                <p class="mb-1"><strong>Name:</strong> <c:out value="${billedToCustomer.fullName}"/></p>
                                <p class="mb-1"><strong>Email:</strong> <c:out value="${billedToCustomer.email}"/></p>
                                <p class="mb-0"><strong>Phone:</strong> <c:out value="${billedToCustomer.phoneNumber}"/></p>
                            </c:if>
                            <c:if test="${empty billedToCustomer}">
                                <p class="text-muted mb-0">Customer details not available.</p>
                            </c:if>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <dl class="row">
                                <dt class="col-sm-6 col-md-5">Invoice Date:</dt>
                                <dd class="col-sm-6 col-md-7"><fmt:formatDate value="${invoice.invoiceDate}" pattern="dd/MM/yyyy"/></dd>
                                <dt class="col-sm-6 col-md-5">Due Date:</dt>
                                <dd class="col-sm-6 col-md-7"><c:if test="${not empty invoice.dueDate}"><fmt:formatDate value="${invoice.dueDate}" pattern="dd/MM/yyyy"/></c:if></dd>
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
                                <%-- Loop through the combined invoiceItems list from servlet --%>
                            <c:choose>
                                <c:when test="${not empty invoiceItems}">
                                    <c:forEach var="item" items="${invoiceItems}">
                                        <tr>
                                            <td><c:out value="${item.description}"/></td>
                                            <td class="text-center">${item.quantity}</td>
                                            <td class="text-end"><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol=""/> VND</td>
                                            <td class="text-end"><fmt:formatNumber value="${item.amount}" type="currency" currencySymbol=""/> VND</td>
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
                                    <td class="text-end"><fmt:formatNumber value="${invoice.subtotal}" type="currency" currencySymbol=""/> VND</td>
                                </tr>
                                <tr>
                                    <th scope="row">Tax (Est.):</th> <%-- Clarify tax calculation if needed --%>
                                    <td class="text-end"><fmt:formatNumber value="${invoice.taxAmount}" type="currency" currencySymbol=""/> VND</td>
                                </tr>
                                <tr class="border-top">
                                    <th scope="row" class="fs-5 pt-2">Total Amount:</th>
                                    <td class="text-end fs-5 fw-bold pt-2"><fmt:formatNumber value="${invoice.totalAmount}" type="currency" currencySymbol=""/> VND</td>
                                </tr>
                                <tr>
                                    <th scope="row">Amount Paid:</th>
                                    <td class="text-end"><fmt:formatNumber value="${invoice.paidAmount}" type="currency" currencySymbol=""/> VND</td>
                                </tr>
                                <tr class="border-top">
                                    <th scope="row" class="fs-5 text-danger pt-2">Balance Due:</th>
                                    <td class="text-end fs-5 fw-bold text-danger pt-2"><fmt:formatNumber value="${invoice.balanceAmount}" type="currency" currencySymbol=""/> VND</td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                        <%-- Payment Status and Actions --%>
                    <div class="d-flex flex-wrap justify-content-between align-items-center border-top pt-3">
                        <div>
                            <h5>Payment Status:
                                <span class="badge status-badge status-${fn:toLowerCase(invoice.paymentStatus)}">
                                        <%-- Convert underscores for CSS class compatibility --%>
                                        <c:out value="${invoice.paymentStatus.replace('_',' ')}"/>
                                    </span>
                            </h5>
                        </div>
                        <div class="mt-2 mt-md-0">
                                <%-- Show Record Payment button only if unpaid/partially paid AND user is Accountant/Admin etc. --%>
                            <c:if test="${(invoice.paymentStatus == 'UNPAID' || invoice.paymentStatus == 'PARTIALLY_PAID') && (sessionScope.roleCode == 'ACCOUNTANT' || sessionScope.roleCode == 'ADMIN')}">
                                <a href="${pageContext.request.contextPath}/accountant/recordPayment?invoiceId=${invoice.invoiceID}" class="btn btn-success me-2">
                                    <i class="bi bi-cash-coin"></i> Record Payment
                                </a>
                            </c:if>
                                <%-- Show QR Code button only if unpaid/partially paid --%>
                            <c:if test="${invoice.paymentStatus == 'UNPAID' || invoice.paymentStatus == 'PARTIALLY_PAID'}">
                                <button type="button" class="btn btn-info me-2" onclick="showQRCode()">
                                    <i class="bi bi-qr-code"></i> Show QR Code
                                </button>
                            </c:if>
                            <button type="button" class="btn btn-secondary" onclick="window.print();">
                                <i class="bi bi-printer"></i> Print
                            </button>
                        </div>
                    </div>

                        <%-- QR Code Modal (Hidden initially) --%>
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
                                    <p><strong>Amount:</strong> <fmt:formatNumber value="${invoice.balanceAmount}" type="currency" currencySymbol=""/> VND</p>
                                        <%-- Content suggestion: INV-<InvoiceNumber> or WO-<WorkOrderID> --%>
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
                <c:choose>
                    <c:when test="${not empty error}">
                        Error loading invoice: ${error}
                    </c:when>
                    <c:otherwise>
                        Invoice not found or could not be loaded.
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function showQRCode() {
        const qrImage = document.getElementById('qrCodeImage');
        const qrLoading = document.getElementById('qrCodeLoading');
        const modal = new bootstrap.Modal(document.getElementById('qrCodeModal'));

        // Show loading spinner, hide image
        qrLoading.classList.remove('d-none');
        qrLoading.classList.add('d-block');
        qrImage.classList.add('d-none');
        qrImage.src = ''; // Clear previous image

        modal.show();

        fetch('${pageContext.request.contextPath}/invoices/generateQR?invoiceId=${invoice.invoiceID}')
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error('Network response was not ok: ' + text) });
                }
                return response.json(); // Expecting JSON like { qrImageUrl: "https://..." }
            })
            .then(data => {
                // SỬA Ở ĐÂY: Dùng qrImageUrl thay vì qrDataURL
                if (data && data.qrImageUrl) {
                    qrImage.src = data.qrImageUrl; // Gán thẳng URL từ API vào src
                    // Hide loading, show image
                    qrLoading.classList.add('d-none');
                    qrLoading.classList.remove('d-block');
                    qrImage.classList.remove('d-none');
                } else {
                    throw new Error('Invalid or missing qrImageUrl in response');
                }
            })
            .catch(error => {
                console.error('Error fetching QR code URL:', error);
                qrLoading.classList.remove('spinner-border', 'text-primary');
                qrLoading.innerHTML = `<span class="text-danger">Failed to load QR code. ${error.message}</span>`;
                qrLoading.classList.add('d-block');
                qrImage.classList.add('d-none');
            });
    }

</script>
</body>
</html>

