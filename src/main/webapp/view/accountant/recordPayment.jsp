<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Record Payment for Invoice #${invoice.invoiceNumber}</title>
    <link href="https://cdn.jsdelivr.net.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
    <style>
        body {
            background-color: #f8f9fa;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
        }


        .sidebar {
            width: 140px;
            height: calc(100vh - 56px);
            position: fixed;
            top: 56px;
            left: 0;
            background-color: #fff;
            border-right: 1px solid #dee2e6;
            overflow-y: auto;
        }


        main {
            margin-left: 120px;
            margin-top: 56px;
            padding: 20px;
            flex-grow: 1;
        }


        footer {
            background-color: #fff;
            border-top: 1px solid #dee2e6;
            padding: 10px 20px;
            text-align: right;
            font-size: 0.9rem;
            color: #6c757d;
        }
    </style>

</head>
<body class="bg-light">

 <jsp:include page="header.jsp"/>
 <jsp:include page="sidebar.jsp"/>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
            <div class="card shadow-sm">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0"><i class="bi bi-cash-coin"></i> Record Payment</h4>
                </div>
                <div class="card-body">

                    <%-- Display Invoice Information --%>
                    <h5 class="card-title mb-3">Invoice Details</h5>
                    <dl class="row mb-4">
                        <dt class="col-sm-4">Invoice #:</dt>
                        <dd class="col-sm-8">${invoice.invoiceNumber}</dd>

                        <dt class="col-sm-4">Total Amount:</dt>
                        <dd class="col-sm-8"><fmt:formatNumber value="${invoice.totalAmount}" type="currency" currencySymbol=""/> VND</dd>

                        <dt class="col-sm-4">Amount Paid:</dt>
                        <dd class="col-sm-8"><fmt:formatNumber value="${invoice.paidAmount}" type="currency" currencySymbol=""/> VND</dd>

                        <dt class="col-sm-4">Balance Due:</dt>
                        <dd class="col-sm-8 fw-bold text-danger"><fmt:formatNumber value="${invoice.balanceAmount}" type="currency" currencySymbol=""/> VND</dd>
                    </dl>
                    <hr/>

                    <%-- Payment Recording Form --%>
                    <form action="${pageContext.request.contextPath}/accountant/recordPayment" method="POST">

                        <%-- Hidden fields to pass IDs --%>
                        <input type="hidden" name="invoiceId" value="${invoice.invoiceID}" />
                        <input type="hidden" name="workOrderId" value="${invoice.workOrderID}" />

                        <%-- Amount --%>
                        <div class="mb-3">
                            <label for="amount" class="form-label">Payment Amount: <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <input type="number" id="amount" name="amount" class="form-control"
                                       placeholder="Enter amount paid" step="0.01" min="0.01"
                                       max="${invoice.balanceAmount}" required /> <%-- Suggest max based on balance --%>
                                <span class="input-group-text">VND</span>
                            </div>
                        </div>

                        <%-- Method --%>
                        <div class="mb-3">
                            <label for="method" class="form-label">Payment Method: <span class="text-danger">*</span></label>
                            <select id="method" name="method" class="form-select" required>
                                <option value="">-- Select Method --</option>
                                <option value="OFFLINE">Offline (Cash/Transfer)</option>
                                <option value="ONLINE">Online (QR/Gateway)</option>
                            </select>
                        </div>

                        <%-- Payment Date --%>
                        <div class="mb-3">
                            <label for="paymentDate" class="form-label">Payment Date & Time: <span class="text-danger">*</span></label>
                            <input type="datetime-local" id="paymentDate" name="paymentDate" class="form-control" required />
                        </div>

                        <%-- Reference Number (Optional) --%>
                        <div class="mb-3">
                            <label for="referenceNo" class="form-label">Reference No (Optional):</label>
                            <input type="text" id="referenceNo" name="referenceNo" class="form-control" placeholder="e.g., Transaction ID, Check #" />
                        </div>

                        <%-- Note (Optional) --%>
                        <div class="mb-3">
                            <label for="note" class="form-label">Note (Optional):</label>
                            <textarea id="note" name="note" class="form-control" rows="3" placeholder="Any additional details"></textarea>
                        </div>

                        <%-- Buttons --%>
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="${pageContext.request.contextPath}/accountant/invoices" class="btn btn-secondary me-md-2">
                                <i class="bi bi-x-circle"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-lg"></i> Record Payment
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
 <jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Set default payment date to now
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset()); // Adjust for local timezone
    const formattedNow = now.toISOString().slice(0, 16); // Format YYYY-MM-DDTHH:mm
    document.getElementById('paymentDate').value = formattedNow;
</script>
</body>
</html>
