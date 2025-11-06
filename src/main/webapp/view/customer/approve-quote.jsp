<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quote Approval - Garage Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .quote-container {
            max-width: 900px;
            margin: 40px auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            padding: 30px;
        }
        .quote-header {
            border-bottom: 2px solid #007bff;
            padding-bottom: 15px;
            margin-bottom: 25px;
        }
        .vehicle-info {
            background-color: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
        }
        .issue-section {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin-bottom: 20px;
        }
        .parts-table th {
            background-color: #007bff;
            color: white;
        }
        .total-row {
            font-size: 1.2em;
            font-weight: bold;
            background-color: #e9ecef;
        }
        .btn-approve {
            background-color: #28a745;
            color: white;
            padding: 12px 30px;
            font-size: 1.1em;
        }
        .btn-approve:hover {
            background-color: #218838;
            color: white;
        }
        .btn-reject {
            background-color: #dc3545;
            color: white;
            padding: 12px 30px;
            font-size: 1.1em;
        }
        .btn-reject:hover {
            background-color: #c82333;
            color: white;
        }
    </style>
</head>
<body>
<jsp:include page="/common/customer/header.jsp" />

<div class="quote-container">
    <div class="quote-header">
        <h2 class="mb-0">
            <i class="bi bi-file-earmark-text"></i> Repair Quote Approval
        </h2>
        <small class="text-muted">Quote ID: #${diagnostic.vehicleDiagnosticID}</small>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Vehicle Information -->
    <div class="vehicle-info">
        <h5 class="mb-3">
            <i class="bi bi-car-front"></i> Vehicle Information
        </h5>
        <div class="row">
            <div class="col-md-12">
                <p class="mb-1"><strong>Vehicle:</strong> ${diagnostic.vehicleInfo}</p>
            </div>
        </div>
    </div>

    <!-- Issue Found -->
    <div class="issue-section">
        <h5 class="mb-2">
            <i class="bi bi-exclamation-circle"></i> Diagnosis Report
        </h5>
        <p class="mb-0">${diagnostic.issueFound}</p>
    </div>

    <!-- Parts & Services Table -->
    <h5 class="mb-3">
        <i class="bi bi-list-check"></i> Required Parts & Services
    </h5>
    <table class="table table-bordered parts-table">
        <thead>
            <tr>
                <th style="width: 5%;">#</th>
                <th style="width: 40%;">Part Name</th>
                <th style="width: 15%;">Quantity</th>
                <th style="width: 20%;">Unit Price</th>
                <th style="width: 20%;">Subtotal</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${diagnostic.parts}" var="part" varStatus="status">
                <tr>
                    <td>${status.index + 1}</td>
                    <td>${part.partName}</td>
                    <td class="text-center">${part.quantity}</td>
                    <td class="text-end">
                        <fmt:formatNumber value="${part.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </td>
                    <td class="text-end">
                        <fmt:formatNumber value="${part.quantity * part.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </td>
                </tr>
            </c:forEach>
            
            <c:if test="${empty diagnostic.parts}">
                <tr>
                    <td colspan="5" class="text-center text-muted">No parts required for this repair</td>
                </tr>
            </c:if>
        </tbody>
        <tfoot>
            <tr class="total-row">
                <td colspan="4" class="text-end">TOTAL ESTIMATE:</td>
                <td class="text-end">
                    <fmt:formatNumber value="${diagnostic.estimateCost}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                </td>
            </tr>
        </tfoot>
    </table>

    <!-- Important Notice -->
    <div class="alert alert-info mt-4">
        <h6 class="alert-heading">
            <i class="bi bi-info-circle-fill"></i> Important Information
        </h6>
        <ul class="mb-0">
            <li>This is an estimated quote. Final costs may vary based on actual repair conditions.</li>
            <li>Parts availability will be confirmed after approval.</li>
            <li>Estimated repair time: 2-5 business days (depending on parts availability).</li>
            <li>If you reject this quote, please provide a reason so we can better assist you.</li>
        </ul>
    </div>

    <!-- Action Buttons -->
    <div class="mt-4 d-flex justify-content-between">
        <a href="${pageContext.request.contextPath}/customer/appointment-history" class="btn btn-secondary">
            <i class="bi bi-arrow-left"></i> Back to History
        </a>

        <div>
            <c:if test="${diagnostic.status == 'SUBMITTED'}">
                <button type="button" class="btn btn-reject me-2" data-bs-toggle="modal" data-bs-target="#rejectModal">
                    <i class="bi bi-x-circle"></i> Reject Quote
                </button>
                <button type="button" class="btn btn-approve" data-bs-toggle="modal" data-bs-target="#approveModal">
                    <i class="bi bi-check-circle"></i> Approve Quote
                </button>
            </c:if>

            <c:if test="${diagnostic.status == 'APPROVED'}">
                <span class="badge bg-success fs-5 p-3">
                    <i class="bi bi-check-circle-fill"></i> Quote Approved
                </span>
            </c:if>

            <c:if test="${diagnostic.status == 'REJECTED'}">
                <span class="badge bg-danger fs-5 p-3">
                    <i class="bi bi-x-circle-fill"></i> Quote Rejected
                </span>
            </c:if>
        </div>
    </div>

    <!-- Creation Date -->
    <div class="text-muted mt-4 text-end">
        <small>Quote created: ${diagnostic.createdAt}</small>
    </div>
</div>

<!-- Approve Confirmation Modal -->
<div class="modal fade" id="approveModal" tabindex="-1" aria-labelledby="approveModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title" id="approveModalLabel">
                    <i class="bi bi-check-circle"></i> Confirm Approval
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/customer/approve-quote" method="post">
                <div class="modal-body">
                    <input type="hidden" name="diagnosticId" value="${diagnostic.vehicleDiagnosticID}"/>
                    <input type="hidden" name="action" value="approve"/>
                    
                    <p class="lead">Are you sure you want to approve this quote?</p>
                    <p>By approving, you authorize us to:</p>
                    <ul>
                        <li>Proceed with ordering the required parts</li>
                        <li>Schedule your vehicle for repair</li>
                        <li>Charge the estimated amount: <strong>
                            <fmt:formatNumber value="${diagnostic.estimateCost}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </strong></li>
                    </ul>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-circle"></i> Yes, Approve
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Reject Modal -->
<div class="modal fade" id="rejectModal" tabindex="-1" aria-labelledby="rejectModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="rejectModalLabel">
                    <i class="bi bi-x-circle"></i> Reject Quote
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/customer/approve-quote" method="post">
                <div class="modal-body">
                    <input type="hidden" name="diagnosticId" value="${diagnostic.vehicleDiagnosticID}"/>
                    <input type="hidden" name="action" value="reject"/>
                    
                    <p class="lead">Why are you rejecting this quote?</p>
                    <div class="mb-3">
                        <label for="rejectReason" class="form-label">Reason (required)</label>
                        <textarea class="form-control" id="rejectReason" name="rejectReason" rows="4" 
                                  placeholder="Please provide a reason for rejection..." required></textarea>
                    </div>
                    <div class="alert alert-warning">
                        <small>
                            <i class="bi bi-info-circle"></i> 
                            After rejection, our team will review your feedback and may provide an alternative quote.
                        </small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-x-circle"></i> Confirm Rejection
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="/common/customer/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
</body>
</html>
