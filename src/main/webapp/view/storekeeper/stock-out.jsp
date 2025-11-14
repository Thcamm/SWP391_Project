<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Part Requests - Import to Work Order</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid py-4">
    <div class="row">
        <div class="col-12">
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4><i class="fas fa-box-open me-2"></i>Pending Part Requests</h4>
                </div>

                <div class="card-body">
                    <!-- Success/Error Messages -->
                    <c:if test="${param.message == 'approved'}">
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="fas fa-check-circle me-2"></i>Request approved and stock deducted successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${param.error == 'insufficient_stock'}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="fas fa-exclamation-triangle me-2"></i>Insufficient stock! Request has been automatically rejected.
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Pending Requests Table -->
                    <div class="table-responsive">
                        <table class="table table-hover table-bordered">
                            <thead class="table-light">
                            <tr>
                                <th>Request ID</th>
                                <th>Part Name</th>
                                <th>Quantity Requested</th>
                                <th>Current Stock</th>
                                <th>Status</th>
                                <th>Unit Price</th>
                                <th>Total Value</th>
                                <th>Requested At</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="request" items="${pendingRequests}">
                                <tr>
                                    <td>${request.workOrderPartId}</td>
                                    <td>${request.partName}</td>
                                    <td>
                                        <span class="badge bg-info">${request.quantityUsed}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${request.currentStock >= request.quantityUsed}">
                                                <span class="badge bg-success">${request.currentStock}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">${request.currentStock}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <span class="badge bg-warning text-dark">
                                            <i class="fas fa-clock me-1"></i>PENDING
                                        </span>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${request.unitPrice}" type="currency"/>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${request.unitPrice * request.quantityUsed}" type="currency"/>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${request.requestedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${request.currentStock >= request.quantityUsed}">
                                                <!-- Approve Button -->
                                                <form method="post" style="display:inline;">
                                                    <input type="hidden" name="action" value="approve">
                                                    <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                                    <button type="submit" class="btn btn-sm btn-success"
                                                            onclick="return confirm('Approve this request and deduct stock?')">
                                                        <i class="fas fa-check me-1"></i>Approve
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- Cannot approve -->
                                                <button class="btn btn-sm btn-secondary" disabled>
                                                    <i class="fas fa-times me-1"></i>Insufficient
                                                </button>
                                            </c:otherwise>
                                        </c:choose>

                                        <!-- Reject Button -->
                                        <form method="post" style="display:inline;">
                                            <input type="hidden" name="action" value="reject">
                                            <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                            <button type="submit" class="btn btn-sm btn-danger"
                                                    onclick="return confirm('Reject this request?')">
                                                <i class="fas fa-times me-1"></i>Reject
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>

                            <c:if test="${empty pendingRequests}">
                                <tr>
                                    <td colspan="9" class="text-center text-muted py-4">
                                        <i class="fas fa-inbox fa-3x mb-3 d-block"></i>
                                        No pending requests
                                    </td>
                                </tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
