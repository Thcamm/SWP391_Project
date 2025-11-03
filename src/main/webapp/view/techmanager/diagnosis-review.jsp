<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Completed Diagnosis - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
  
</head>
<body>
    <div class="main-container">
        <!-- Sidebar -->
        <c:set var="activeMenu" value="diagnosis-review" scope="request"/>
        <jsp:include page="sidebar-techmanager.jsp"/>

        <!-- Main Content -->
        <div class="content-wrapper">
            <!-- Page Header -->
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2 class="mb-1">
                            <i class="bi bi-clipboard-check text-primary"></i>
                            Review Completed Diagnosis
                        </h2>
                        <p class="text-muted mb-0">Review diagnosis reports and approve/reject for customer approval</p>
                    </div>
                    <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
                        <i class="bi bi-arrow-clockwise"></i> Refresh
                    </button>
                </div>
            </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle"></i> ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Statistics -->
                <div class="row mb-4">
                    <div class="col-md-4">
                        <div class="card bg-primary text-white">
                            <div class="card-body">
                                <h5 class="card-title">
                                    <i class="bi bi-clipboard-check"></i> Completed Diagnosis
                                </h5>
                                <h2 class="mb-0">${reviewList.size()}</h2>
                                <small>Awaiting review</small>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Diagnosis List -->
                <c:choose>
                    <c:when test="${empty reviewList}">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle"></i>
                            No completed diagnosis tasks to review at this time.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="item" items="${reviewList}">
                            <div class="card diagnostic-card">
                                <div class="card-body">
                                    <div class="row">
                                        <!-- Task Info -->
                                        <div class="col-md-8">
                                            <h5 class="card-title">
                                                <i class="bi bi-tools"></i>
                                                Task #${item.task.assignmentID}
                                                <span class="status-badge status-complete ms-2">
                                                    <i class="bi bi-check-circle"></i> COMPLETE
                                                </span>
                                            </h5>
                                            
                                            <div class="mb-2">
                                                <strong>Vehicle:</strong> 
                                                <span class="text-primary">${item.task.vehicleInfo}</span>
                                            </div>
                                            
                                            <div class="mb-2">
                                                <strong>Customer:</strong> ${item.task.customerName}
                                            </div>
                                            
                                            <div class="mb-2">
                                                <strong>Technician:</strong> ${item.task.technicianName}
                                            </div>
                                            
                                            <div class="mb-2">
                                                <strong>Priority:</strong>
                                                <span class="priority-${item.task.priority.toString().toLowerCase()}">
                                                    ${item.task.priority}
                                                </span>
                                            </div>
                                            
                                            <div class="mb-2">
                                                <strong>Completed At:</strong>
                                                <fmt:formatDate value="${item.task.completeAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </div>
                                        </div>

                                        <!-- Quick Actions -->
                                        <div class="col-md-4 text-end">
                                            <c:if test="${item.hasDiagnostic()}">
                                                <div class="mb-2">
                                                    <strong>Total Estimate:</strong><br>
                                                    <h4 class="text-success">
                                                        <fmt:formatNumber value="${item.diagnostic.totalEstimate}" 
                                                                        type="currency" 
                                                                        currencySymbol="$"/>
                                                    </h4>
                                                </div>
                                                
                                                <form method="POST" 
                                                      action="${pageContext.request.contextPath}/techmanager/approve-diagnosis" 
                                                      class="d-inline"
                                                      onsubmit="return confirm('Are you sure you want to APPROVE this diagnosis and send it to the customer?');">
                                                    <input type="hidden" name="assignmentId" value="${item.task.assignmentID}">
                                                    <input type="hidden" name="action" value="approve">
                                                    <button type="submit" class="btn btn-success btn-lg w-100 mb-2">
                                                        <i class="bi bi-check-circle"></i> Approve
                                                    </button>
                                                </form>
                                                
                                                <button type="button" 
                                                        class="btn btn-outline-danger w-100"
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#rejectModal${item.task.assignmentID}">
                                                    <i class="bi bi-x-circle"></i> Reject
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>

                                    <!-- Diagnostic Details -->
                                    <c:if test="${item.hasDiagnostic()}">
                                        <hr>
                                        <h6 class="text-muted mb-3">
                                            <i class="bi bi-clipboard-data"></i> Diagnostic Report
                                        </h6>
                                        
                                        <div class="row">
                                            <div class="col-md-12">
                                                <div class="alert alert-light mb-3">
                                                    <strong>Issue Found:</strong><br>
                                                    ${item.diagnostic.issueFound}
                                                </div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-md-4">
                                                <div class="card bg-light">
                                                    <div class="card-body">
                                                        <h6 class="card-title">Labor Cost</h6>
                                                        <h5 class="text-primary">
                                                            <fmt:formatNumber value="${item.diagnostic.estimateCost}" 
                                                                            type="currency" 
                                                                            currencySymbol="$"/>
                                                        </h5>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            <div class="col-md-4">
                                                <div class="card bg-light">
                                                    <div class="card-body">
                                                        <h6 class="card-title">Parts Cost</h6>
                                                        <h5 class="text-warning">
                                                            <fmt:formatNumber value="${item.diagnostic.totalPartsCost}" 
                                                                            type="currency" 
                                                                            currencySymbol="$"/>
                                                        </h5>
                                                    </div>
                                                </div>
                                            </div>
                                            
                                            <div class="col-md-4">
                                                <div class="card bg-light">
                                                    <div class="card-body">
                                                        <h6 class="card-title">Hours Spent</h6>
                                                        <h5 class="text-info">
                                                            ${item.diagnostic.totalHoursSpent} hrs
                                                        </h5>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <!-- Parts List -->
                                        <c:if test="${not empty item.diagnostic.parts}">
                                            <div class="mt-3">
                                                <h6 class="text-muted">
                                                    <i class="bi bi-gear"></i> Required Parts (${item.diagnostic.parts.size()})
                                                </h6>
                                                <div class="table-responsive">
                                                    <table class="table table-sm">
                                                        <thead>
                                                            <tr>
                                                                <th>Part Name</th>
                                                                <th>Quantity</th>
                                                                <th>Unit Price</th>
                                                                <th>Total</th>
                                                                <th>Condition</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="part" items="${item.diagnostic.parts}">
                                                                <tr class="part-${part.partCondition.toString().toLowerCase()}">
                                                                    <td>${part.partName}</td>
                                                                    <td>${part.quantity}</td>
                                                                    <td>
                                                                        <fmt:formatNumber value="${part.unitPrice}" 
                                                                                        type="currency" 
                                                                                        currencySymbol="$"/>
                                                                    </td>
                                                                    <td>
                                                                        <fmt:formatNumber value="${part.totalPrice}" 
                                                                                        type="currency" 
                                                                                        currencySymbol="$"/>
                                                                    </td>
                                                                    <td>
                                                                        <span class="badge bg-secondary">
                                                                            ${part.partCondition}
                                                                        </span>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </c:if>
                                    </c:if>
                                </div>
                            </div>
                            
                            <!-- Reject Modal -->
                            <div class="modal fade" id="rejectModal${item.task.assignmentID}" tabindex="-1">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header bg-danger text-white">
                                            <h5 class="modal-title">
                                                <i class="bi bi-x-circle"></i> Reject Diagnosis
                                            </h5>
                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form method="POST" action="${pageContext.request.contextPath}/techmanager/approve-diagnosis">
                                            <div class="modal-body">
                                                <input type="hidden" name="assignmentId" value="${item.task.assignmentID}">
                                                <input type="hidden" name="action" value="reject">
                                                
                                                <div class="alert alert-warning">
                                                    <i class="bi bi-exclamation-triangle"></i>
                                                    This will send the diagnosis back to the technician for revision.
                                                </div>
                                                
                                                <div class="mb-3">
                                                    <label class="form-label">Rejection Notes (Optional):</label>
                                                    <textarea name="notes" 
                                                              class="form-control" 
                                                              rows="4" 
                                                              placeholder="Explain why this diagnosis is being rejected..."></textarea>
                                                    <small class="text-muted">
                                                        The technician will see these notes.
                                                    </small>
                                                </div>
                                            </div>
                                            <div class="modal-footer">
                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                <button type="submit" class="btn btn-danger">
                                                    <i class="bi bi-x-circle"></i> Reject Diagnosis
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
