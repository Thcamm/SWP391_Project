<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Part Requests</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .card {
            border: none;
            border-radius: 10px;
        }
        .table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
    </style>
</head>
<body>
<div class="container py-4">
    <div class="card shadow-sm">
        <div class="card-header bg-white border-0 pt-4">
            <h4 class="mb-0"><i class="fas fa-box-open text-primary me-2"></i>Pending Part Requests</h4>
        </div>

        <div class="card-body">
            <!-- Success/Error Messages -->
            <c:if test="${param.message == 'approved'}">
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="fas fa-check-circle me-2"></i>Request approved successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${param.message == 'rejected'}">
                <div class="alert alert-info alert-dismissible fade show">
                    <i class="fas fa-info-circle me-2"></i>Request rejected.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${param.error == 'insufficient_stock'}">
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="fas fa-exclamation-triangle me-2"></i>Insufficient stock! Request automatically rejected.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Requests Table -->
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Part Name</th>
                        <th>Quantity</th>
                        <th>Unit Price</th>
                        <th>Status</th>
                        <th class="text-end">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="request" items="${pendingRequests}">
                        <tr>
                            <td><strong>#${request.workOrderPartId}</strong></td>
                            <td>${request.partName}</td>
                            <td>
                                <span class="badge bg-info">${request.quantityUsed}</span>
                            </td>
                            <td>
                                <fmt:formatNumber value="${request.unitPrice}" type="currency"/>
                            </td>
                            <td>
                                    <span class="badge bg-warning text-dark">
                                        <i class="fas fa-clock me-1"></i>PENDING
                                    </span>
                            </td>
                            <td class="text-end">
                                <!-- Approve Button -->
                                <form action="${pageContext.request.contextPath}/stock-out"
                                      method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                    <button type="submit" class="btn btn-sm btn-success me-1"
                                            onclick="return confirm('Approve this request?')">
                                        <i class="fas fa-check"></i> Approve
                                    </button>
                                </form>

                                <!-- Reject Button -->
                                <form action="${pageContext.request.contextPath}/stock-out"
                                      method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="reject">
                                    <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                    <input type="hidden" name="reason" value="Rejected by storekeeper">
                                    <button type="submit" class="btn btn-sm btn-danger"
                                            onclick="return confirm('Reject this request?')">
                                        <i class="fas fa-times"></i> Reject
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty pendingRequests}">
                        <tr>
                            <td colspan="6" class="text-center text-muted py-5">
                                <i class="fas fa-inbox fa-3x mb-3 d-block opacity-50"></i>
                                <p class="mb-0">No pending requests</p>
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>