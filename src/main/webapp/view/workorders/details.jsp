<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WorkOrder Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <!-- Header -->
    <nav class="navbar navbar-dark bg-dark">
        <div class="container-fluid">
            <span class="navbar-brand">
                <i class="bi bi-tools"></i> WorkOrder Details #${workOrder.workOrderId}
            </span>
            <div>
                <a href="${pageContext.request.contextPath}/techmanager/workorders/list" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-arrow-left"></i> Back to List
                </a>
                <a href="${pageContext.request.contextPath}/techmanager/dashboard" class="btn btn-outline-light btn-sm">
                    <i class="bi bi-house"></i> Dashboard
                </a>
            </div>
        </div>
    </nav>

    <div class="container-fluid mt-4">
        <!-- Alert Messages -->
        <c:if test="${not empty param.success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle"></i>
                <c:choose>
                    <c:when test="${param.success == 'detail_added'}">WorkOrder detail added successfully!</c:when>
                    <c:when test="${param.success == 'detail_updated'}">WorkOrder detail updated successfully!</c:when>
                    <c:when test="${param.success == 'detail_deleted'}">WorkOrder detail deleted successfully!</c:when>
                    <c:when test="${param.success == 'detail_approved'}">WorkOrder detail approved!</c:when>
                    <c:when test="${param.success == 'detail_declined'}">WorkOrder detail declined!</c:when>
                    <c:otherwise>Operation completed successfully!</c:otherwise>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle"></i> Error: ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- WorkOrder Info -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">WorkOrder Information</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>ID:</strong> ${workOrder.workOrderId}</p>
                                <p><strong>Request ID:</strong> ${workOrder.requestId}</p>
                                <p><strong>Status:</strong>
                                    <c:choose>
                                        <c:when test="${workOrder.status == 'PENDING'}">
                                            <span class="badge bg-warning">Pending</span>
                                        </c:when>
                                        <c:when test="${workOrder.status == 'IN_PROCESS'}">
                                            <span class="badge bg-primary">In Process</span>
                                        </c:when>
                                        <c:when test="${workOrder.status == 'COMPLETE'}">
                                            <span class="badge bg-success">Complete</span>
                                        </c:when>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Estimate Amount:</strong>
                                    <fmt:formatNumber value="${workOrder.estimateAmount}" type="currency" currencySymbol="$"/>
                                </p>
                                <p><strong>Created Date:</strong>
                                    <fmt:formatDate value="${workOrder.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>
                                <p><strong>Updated Date:</strong>
                                    <fmt:formatDate value="${workOrder.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- WorkOrder Details -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">WorkOrder Details</h5>
                        <a href="${pageContext.request.contextPath}/techmanager/workorders/add-detail?workOrderId=${workOrder.workOrderId}"
                           class="btn btn-success btn-sm">
                            <i class="bi bi-plus-circle"></i> Add Detail
                        </a>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty workOrderDetails}">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Source</th>
                                                <th>Task Description</th>
                                                <th>Estimate Hours</th>
                                                <th>Estimate Amount</th>
                                                <th>Approval Status</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${workOrderDetails}">
                                                <tr>
                                                    <td>${detail.detailId}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${detail.source == 'CUSTOMER'}">
                                                                <span class="badge bg-info">Customer</span>
                                                            </c:when>
                                                            <c:when test="${detail.source == 'TECHNICIAN'}">
                                                                <span class="badge bg-secondary">Technician</span>
                                                            </c:when>
                                                            <c:when test="${detail.source == 'SYSTEM'}">
                                                                <span class="badge bg-dark">System</span>
                                                            </c:when>
                                                        </c:choose>
                                                    </td>
                                                    <td>${detail.taskDescription}</td>
                                                    <td>${detail.estimateHours}</td>
                                                    <td>
                                                        <fmt:formatNumber value="${detail.estimateAmount}" type="currency" currencySymbol="$"/>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${detail.approvalStatus == 'PENDING'}">
                                                                <span class="badge bg-warning">Pending</span>
                                                            </c:when>
                                                            <c:when test="${detail.approvalStatus == 'APPROVED'}">
                                                                <span class="badge bg-success">Approved</span>
                                                            </c:when>
                                                            <c:when test="${detail.approvalStatus == 'DECLINED'}">
                                                                <span class="badge bg-danger">Declined</span>
                                                            </c:when>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <a href="${pageContext.request.contextPath}/techmanager/workorders/edit-detail?workOrderId=${workOrder.workOrderId}&detailId=${detail.detailId}"
                                                               class="btn btn-sm btn-outline-primary">
                                                                <i class="bi bi-pencil"></i> Edit
                                                            </a>
                                                            <c:if test="${detail.approvalStatus == 'PENDING'}">
                                                                <form method="post" action="${pageContext.request.contextPath}/techmanager/workorders/approve-detail" style="display: inline;">
                                                                    <input type="hidden" name="workOrderId" value="${workOrder.workOrderId}">
                                                                    <input type="hidden" name="detailId" value="${detail.detailId}">
                                                                    <button type="submit" class="btn btn-sm btn-outline-success">
                                                                        <i class="bi bi-check-circle"></i> Approve
                                                                    </button>
                                                                </form>
                                                                <form method="post" action="${pageContext.request.contextPath}/techmanager/workorders/decline-detail" style="display: inline;">
                                                                    <input type="hidden" name="workOrderId" value="${workOrder.workOrderId}">
                                                                    <input type="hidden" name="detailId" value="${detail.detailId}">
                                                                    <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                        <i class="bi bi-x-circle"></i> Decline
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                            <form method="post" action="${pageContext.request.contextPath}/techmanager/workorders/delete-detail" style="display: inline;">
                                                                <input type="hidden" name="workOrderId" value="${workOrder.workOrderId}">
                                                                <input type="hidden" name="detailId" value="${detail.detailId}">
                                                                <button type="submit" class="btn btn-sm btn-outline-danger"
                                                                        onclick="return confirm('Are you sure you want to delete this detail?')">
                                                                    <i class="bi bi-trash"></i> Delete
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-4">
                                    <i class="bi bi-info-circle" style="font-size: 3rem; color: #6c757d"></i>
                                    <h5 class="mt-3 text-muted">No Details Found</h5>
                                    <p class="text-muted">This work order has no details yet.</p>
                                    <a href="${pageContext.request.contextPath}/techmanager/workorders/add-detail?workOrderId=${workOrder.workOrderId}"
                                       class="btn btn-primary">
                                        Add First Detail
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>