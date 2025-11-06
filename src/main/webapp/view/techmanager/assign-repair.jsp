<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assign Repair Tasks - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css">
</head>
<body>
    <div class="main-container">
        <!-- Sidebar -->
        <c:set var="activeMenu" value="assign-repair" scope="request"/>
        <jsp:include page="sidebar-techmanager.jsp"/>

        <!-- Main Content -->
        <div class="content-wrapper">
            <!-- Header -->
            <jsp:include page="header-techmanager.jsp"/>

            <!-- Page Header -->
            <div class="page-header">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2 class="mb-1">
                            <i class="bi bi-tools text-primary"></i>
                            Assign Repair Tasks
                        </h2>
                        <p class="text-muted mb-0">
                            Phase 3: Assign approved repairs to technicians
                        </p>
                    </div>
                    <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
                        <i class="bi bi-arrow-clockwise"></i> Refresh
                    </button>
                </div>
            </div>

            <!-- Messages -->
            <c:if test="${not empty message}">
                <div class="alert alert-${messageType} alert-dismissible fade show" role="alert">
                    <i class="bi bi-${messageType == 'success' ? 'check-circle' : 'exclamation-triangle'}"></i>
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Statistics -->
            <div class="row mb-4">
                <div class="col-md-3">
                    <div class="card bg-primary text-white">
                        <div class="card-body">
                            <h5 class="card-title">
                                <i class="bi bi-check-circle"></i> Approved Quotes
                            </h5>
                            <h2 class="mb-0">${approvedQuotes.size()}</h2>
                            <small>Ready for assignment</small>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card bg-info text-white">
                        <div class="card-body">
                            <h5 class="card-title">
                                <i class="bi bi-people"></i> Technicians
                            </h5>
                            <h2 class="mb-0">${availableTechnicians.size()}</h2>
                            <small>Available for work</small>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Approved Quotes List -->
            <c:choose>
                <c:when test="${empty approvedQuotes}">
                    <div class="alert alert-info">
                        <i class="bi bi-info-circle"></i>
                        No approved quotes waiting for repair assignment at this time.
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="quote" items="${approvedQuotes}">
                        <div class="card mb-3" style="border-left: 4px solid #0d6efd;">
                            <div class="card-body">
                                <div class="row">
                                    <!-- Quote Info -->
                                    <div class="col-md-8">
                                        <h5 class="card-title">
                                            <i class="bi bi-car-front"></i>
                                            ${quote.vehicleModel}
                                            <span class="badge bg-success ms-2">
                                                <i class="bi bi-check-circle"></i> Approved
                                            </span>
                                        </h5>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <div class="mb-2">
                                                    <strong>License Plate:</strong>
                                                    <span class="text-primary">${quote.licensePlate}</span>
                                                </div>
                                                <div class="mb-2">
                                                    <strong>Customer:</strong> ${quote.customerName}
                                                </div>
                                                <div class="mb-2">
                                                    <strong>Phone:</strong> ${quote.phoneNumber}
                                                </div>
                                            </div>
                                            <div class="col-md-6">
                                                <div class="mb-2">
                                                    <strong>Work Order:</strong> #${quote.workOrderId}
                                                </div>
                                                <div class="mb-2">
                                                    <strong>Approved At:</strong>
                                                    <fmt:formatDate value="${quote.approvedAt}" pattern="dd/MM/yyyy HH:mm" />
                                                </div>
                                                <div class="mb-2">
                                                    <strong>Estimate Amount:</strong>
                                                    <h5 class="d-inline text-success">
                                                        <fmt:formatNumber value="${quote.estimateAmount}" 
                                                                        type="currency" 
                                                                        currencySymbol="$"/>
                                                    </h5>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        <div class="mt-3">
                                            <strong>Task Description:</strong>
                                            <p class="mb-0">${quote.taskDescription}</p>
                                        </div>
                                    </div>

                                    <!-- Assign Button -->
                                    <div class="col-md-4 text-end">
                                        <button type="button" 
                                                class="btn btn-primary btn-lg w-100"
                                                data-bs-toggle="modal" 
                                                data-bs-target="#assignModal${quote.detailId}">
                                            <i class="bi bi-person-plus"></i> Assign Technician
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Assignment Modal -->
                        <div class="modal fade" id="assignModal${quote.detailId}" tabindex="-1">
                            <div class="modal-dialog">
                                <div class="modal-content">
                                    <div class="modal-header bg-primary text-white">
                                        <h5 class="modal-title">
                                            <i class="bi bi-person-plus"></i> Assign Repair Task
                                        </h5>
                                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                    </div>
                                    <form method="POST" action="${pageContext.request.contextPath}/techmanager/assign-repair">
                                        <div class="modal-body">
                                            <input type="hidden" name="detailId" value="${quote.detailId}">
                                            
                                            <div class="alert alert-info mb-3">
                                                <strong>Vehicle:</strong> ${quote.vehicleModel} (${quote.licensePlate})<br>
                                                <strong>Task:</strong> ${quote.taskDescription}<br>
                                                <strong>Estimate:</strong> 
                                                <fmt:formatNumber value="${quote.estimateAmount}" 
                                                                type="currency" 
                                                                currencySymbol="$"/>
                                            </div>
                                            
                                            <div class="mb-3">
                                                <label class="form-label">Select Technician <span class="text-danger">*</span></label>
                                                <select name="technicianId" class="form-select" required>
                                                    <option value="">-- Select Technician --</option>
                                                    <c:forEach var="tech" items="${availableTechnicians}">
                                                        <option value="${tech.employeeId}">
                                                            ${tech.fullName} 
                                                            (Active Tasks: ${tech.activeTasks})
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <small class="text-muted">
                                                    Technicians are sorted by workload (least busy first)
                                                </small>
                                            </div>
                                            
                                            <div class="mb-3">
                                                <label class="form-label">Priority <span class="text-danger">*</span></label>
                                                <select name="priority" class="form-select" required>
                                                    <option value="MEDIUM" selected>Medium</option>
                                                    <option value="HIGH">High</option>
                                                    <option value="LOW">Low</option>
                                                    <option value="URGENT">Urgent</option>
                                                </select>
                                            </div>
                                            
                                            <div class="mb-3">
                                                <label class="form-label">Additional Notes (Optional)</label>
                                                <textarea name="notes" 
                                                          class="form-control" 
                                                          rows="3" 
                                                          placeholder="Any special instructions for the technician..."></textarea>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-check-circle"></i> Assign Task
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>

            <!-- Footer -->
            <jsp:include page="footer-techmanager.jsp"/>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
