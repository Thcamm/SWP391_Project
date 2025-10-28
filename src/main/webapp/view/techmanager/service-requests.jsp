<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pending Service Requests - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <div class="container-fluid mt-4">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-md-8">
                <h2><i class="bi bi-clipboard-check"></i> Pending Service Requests</h2>
                <p class="text-muted">Review and approve customer service requests</p>
            </div>
            <div class="col-md-4 text-end">
                <a href="${pageContext.request.contextPath}/techmanager/dashboard" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty param.message}">
            <c:choose>
                <c:when test="${param.type == 'success'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="bi bi-check-circle"></i> ${param.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:when>
                <c:when test="${param.type == 'warning'}">
                    <div class="alert alert-warning alert-dismissible fade show">
                        <i class="bi bi-exclamation-triangle"></i> ${param.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="bi bi-x-circle"></i> ${param.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- Stats Card -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card bg-warning text-white">
                    <div class="card-body text-center">
                        <h3>${totalPending}</h3>
                        <small>Pending Requests</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- Requests Table -->
        <div class="card">
            <div class="card-header">
                <h5><i class="bi bi-list-ul"></i> Pending Service Requests</h5>
            </div>
            <div class="card-body p-0">
                <c:choose>
                    <c:when test="${empty pendingRequests}">
                        <div class="text-center p-5">
                            <i class="bi bi-inbox display-1 text-muted"></i>
                            <h5 class="text-muted mt-3">No Pending Requests</h5>
                            <p class="text-muted">All service requests have been processed.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-hover mb-0">
                                <thead class="table-dark">
                                    <tr>
                                        <th>Request ID</th>
                                        <th>Date</th>
                                        <th>Customer</th>
                                        <th>Vehicle</th>
                                        <th>Service</th>
                                        <th>Price</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="req" items="${pendingRequests}">
                                        <tr>
                                            <td><span class="badge bg-info">#${req.requestId}</span></td>
                                            <td>
                                                <fmt:formatDate value="${req.requestDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td>
                                                <strong>${req.customerName}</strong>
                                            </td>
                                            <td>
                                                <small class="text-muted">${req.vehicleInfo}</small>
                                            </td>
                                            <td>${req.serviceName}</td>
                                            <td>
                                                <strong class="text-success">
                                                    <fmt:formatNumber value="${req.servicePrice}" type="currency" currencyCode="VND"/>
                                                </strong>
                                            </td>
                                            <td>
                                                <button class="btn btn-sm btn-success" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#approveModal"
                                                        data-request-id="${req.requestId}"
                                                        data-customer="${req.customerName}"
                                                        data-vehicle="${req.vehicleInfo}"
                                                        data-service="${req.serviceName}">
                                                    <i class="bi bi-check-circle"></i> Approve
                                                </button>
                                                <button class="btn btn-sm btn-danger" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#rejectModal"
                                                        data-request-id="${req.requestId}">
                                                    <i class="bi bi-x-circle"></i> Reject
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Approve Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="POST" action="${pageContext.request.contextPath}/techmanager/service-requests">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title"><i class="bi bi-check-circle"></i> Approve Service Request</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="action" value="approve">
                        <input type="hidden" name="requestId" id="approveRequestId">
                        
                        <div class="mb-3">
                            <label class="form-label"><strong>Customer:</strong></label>
                            <p id="approveCustomer" class="text-muted"></p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label"><strong>Vehicle:</strong></label>
                            <p id="approveVehicle" class="text-muted"></p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label"><strong>Service:</strong></label>
                            <p id="approveService" class="text-muted"></p>
                        </div>
                        
                        <div class="mb-3">
                            <label for="taskDescription" class="form-label">Initial Task Description *</label>
                            <textarea class="form-control" id="taskDescription" name="taskDescription" 
                                      rows="3" required 
                                      placeholder="Describe the initial work to be done..."></textarea>
                            <small class="text-muted">This will create the first WorkOrderDetail for this request.</small>
                        </div>

                        <div class="alert alert-info">
                            <i class="bi bi-info-circle"></i>
                            <strong>Action:</strong> This will create a WorkOrder and initial WorkOrderDetail (source=REQUEST).
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-success">
                            <i class="bi bi-check-circle"></i> Approve & Create WorkOrder
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Reject Modal -->
    <div class="modal fade" id="rejectModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="POST" action="${pageContext.request.contextPath}/techmanager/service-requests">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="bi bi-x-circle"></i> Reject Service Request</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="action" value="reject">
                        <input type="hidden" name="requestId" id="rejectRequestId">
                        
                        <div class="mb-3">
                            <label for="rejectionReason" class="form-label">Reason for Rejection</label>
                            <textarea class="form-control" id="rejectionReason" name="rejectionReason" 
                                      rows="3" placeholder="Why is this request being rejected?"></textarea>
                        </div>

                        <div class="alert alert-warning">
                            <i class="bi bi-exclamation-triangle"></i>
                            <strong>Warning:</strong> This will mark the request as REJECTED. Customer will be notified.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-x-circle"></i> Reject Request
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Populate Approve Modal
        document.getElementById('approveModal').addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            document.getElementById('approveRequestId').value = button.getAttribute('data-request-id');
            document.getElementById('approveCustomer').textContent = button.getAttribute('data-customer');
            document.getElementById('approveVehicle').textContent = button.getAttribute('data-vehicle');
            document.getElementById('approveService').textContent = button.getAttribute('data-service');
            document.getElementById('taskDescription').value = 'Initial service: ' + button.getAttribute('data-service');
        });

        // Populate Reject Modal
        document.getElementById('rejectModal').addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            document.getElementById('rejectRequestId').value = button.getAttribute('data-request-id');
        });
    </script>
</body>
</html>
