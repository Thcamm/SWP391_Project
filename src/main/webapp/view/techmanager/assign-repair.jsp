<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}"> <%-- Thêm context-path cho JS --%>
    <title>Assign Repair Tasks - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/tech manager/base-techmanager.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/assign-repair.css">
</head>
<body>
    <div class="main-container">
        <c:set var="activeMenu" value="assign-repair" scope="request"/>
        <jsp:include page="sidebar-techmanager.jsp"/>

        <div class="content-wrapper">
            <jsp:include page="header-techmanager.jsp"/>
            
            <div class="container-fluid mt-3"> <%-- Bọc nội dung trong container-fluid --%>
                <div class="page-header">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h2 class="mb-1">
                                <i class="bi bi-tools text-primary"></i>
                                Assign Repair Tasks
                            </h2>
                            <p class="text-muted mb-0">
                                Phase 5: Gán các công việc (REQUEST) hoặc các báo giá (DIAGNOSTIC) đã được duyệt.
                            </p>
                        </div>
                        <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
                            <i class="bi bi-arrow-clockwise"></i> Refresh
                        </button>
                    </div>
                </div>

                <c:if test="${not empty message}">
                    <div class="alert alert-${messageType} alert-dismissible fade show" role="alert">
                        <i class="bi bi-${messageType == 'success' ? 'check-circle' : 'exclamation-triangle'}"></i>
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <div class="row mb-4">
                    <div class="col-md-3">
                        <div class="card bg-primary text-white">
                            <div class="card-body">
                                <h5 class="card-title"><i class="bi bi-check-circle"></i> Approved Repairs</h5>
                                <h2 class="mb-0">${approvedRepairs.size()}</h2>
                                <small>Ready for assignment</small>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card bg-success text-white">
                            <div class="card-body">
                                <h5 class="card-title"><i class="bi bi-hourglass-split"></i> In Progress</h5>
                                <h2 class="mb-0">${totalInProgress}</h2>
                                <small>Currently working</small>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card bg-info text-white">
                            <div class="card-body">
                                <h5 class="card-title"><i class="bi bi-people"></i> Technicians</h5>
                                <h2 class="mb-0">${availableTechnicians.size()}</h2>
                                <small>Available for work</small>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty inProgressTasks}">
                    </c:if>

                <c:choose>
                    <c:when test="${empty approvedRepairs}">
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle"></i>
                            No approved repairs waiting for repair assignment at this time.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:set var="currentWorkOrderId" value="" />
                        
                        <c:forEach var="repair" items="${approvedRepairs}">
                            
                            <c:if test="${repair.workOrderId != currentWorkOrderId}">
                                <c:if test="${currentWorkOrderId != ''}">
                                        </div> <%-- /.list-group --%>
                                    </div> <%-- /.collapse --%>
                                </div> <%-- /.card --%>
                                </c:if>
                                
                                <div class="card shadow-sm mb-3">
                                    <div class="card-header wo-card-header d-flex justify-content-between align-items-center" 
                                         data-bs-toggle="collapse" 
                                         data-bs-target="#wo${repair.workOrderId}"
                                         aria-expanded="true">
                                        <div>
                                            <h5 class="mb-1">
                                                <i class="bi bi-file-earmark-text"></i>
                                                <strong>Work Order #${repair.workOrderId}</strong>
                                                <span class="badge bg-success ms-2">Approved</span>
                                            </h5>
                                            <div class="text-muted small">
                                                <i class="bi bi-car-front"></i> ${repair.vehicleModel} (${repair.licensePlate}) |
                                                <i class="bi bi-person"></i> ${repair.customerName} |
                                                <i class="bi bi-telephone"></i> ${repair.phoneNumber}
                                            </div>
                                        </div>
                                        <i class="bi bi-chevron-down fs-5"></i>
                                    </div>
                                    <div id="wo${repair.workOrderId}" class="collapse show">
                                        <%-- DÙNG LIST-GROUP ĐỂ SẠCH HƠN CARD-TRONG-CARD --%>
                                        <div class="list-group list-group-flush">
                                <c:set var="currentWorkOrderId" value="${repair.workOrderId}" />
                            </c:if>
                            
                            <div class="list-group-item p-3 repair-detail-item">
                                <div class="row align-items-center">
                                    <div class="col-md-8">
                                        <div class="d-flex justify-content-between align-items-start mb-2">
                                            <h6 class="text-primary mb-0">
                                                <i class="bi bi-wrench"></i> Detail #${repair.detailId}
                                            </h6>
                                            <span class="badge bg-info">
                                                <fmt:formatNumber value="${repair.estimateAmount}" type="currency" currencyCode="VND"/>
                                            </span>
                                        </div>
                                        <div class="mb-2">
                                            <strong>Task:</strong> ${repair.taskDescription}
                                        </div>
                                        <div class="d-flex gap-3 mb-2">
                                            <span class="badge bg-secondary">
                                                <i class="bi bi-list-check"></i> Total Assigned: ${repair.totalAssignments}
                                            </span>
                                            <span class="badge ${repair.activeTasks > 0 ? 'bg-warning text-dark' : 'bg-success'}">
                                                <i class="bi bi-person-workspace"></i> Active Tasks: ${repair.activeTasks}
                                            </span>
                                        </div>
                                        
                                        <c:if test="${not empty repair.existingAssignments}">
                                            <div class="mt-2">
                                                <button class="btn btn-sm btn-outline-secondary" type="button" 
                                                        data-bs-toggle="collapse" 
                                                        data-bs-target="#assignments${repair.detailId}">
                                                    <i class="bi bi-eye"></i> View Assignments (${repair.totalAssignments})
                                                </button>
                                                <div class="collapse mt-2" id="assignments${repair.detailId}">
                                                    <div class="table-responsive">
                                                        <table class="table table-sm table-bordered existing-assignments-table">
                                                            <thead class="table-light">
                                                                <tr>
                                                                    <th>ID</th>
                                                                    <th>Technician</th>
                                                                    <th>Task</th>
                                                                    <th>Status</th>
                                                                    <th>Priority</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <c:forEach var="assignment" items="${repair.existingAssignments}">
                                                                    <tr>
                                                                        <td><strong>#${assignment.assignmentId}</strong></td>
                                                                        <td>
                                                                            <i class="bi bi-person-badge"></i> ${assignment.technicianName}<br>
                                                                            <small class="text-muted">${assignment.employeeCode}</small>
                                                                        </td>
                                                                        <td><small>${assignment.taskDescription}</small></td>
                                                                        <td>
                                                                            <span class="badge 
                                                                                ${assignment.status == 'ASSIGNED' ? 'bg-info' : 
                                                                                  assignment.status == 'IN_PROGRESS' ? 'bg-warning text-dark' : 
                                                                                  assignment.status == 'COMPLETE' ? 'bg-success' : 'bg-secondary'}">
                                                                                ${assignment.status}
                                                                            </span>
                                                                        </td>
                                                                        <td>
                                                                            <span class="badge 
                                                                                ${assignment.priority == 'URGENT' ? 'bg-danger' : 
                                                                                  assignment.priority == 'HIGH' ? 'bg-warning text-dark' : 
                                                                                  assignment.priority == 'MEDIUM' ? 'bg-info' : 'bg-secondary'}">
                                                                                ${assignment.priority}
                                                                            </span>
                                                                        </td>
                                                                    </tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>
                                    
                                    <div class="col-md-4 assign-button-col">
                                        <button type="button" 
                                                class="btn btn-primary" <%-- ĐÃ BỎ btn-lg w-100 --%>
                                                data-bs-toggle="modal" 
                                                data-bs-target="#assignModal${repair.detailId}">
                                            <i class="bi bi-person-plus"></i> Assign Technician
                                        </button>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="modal fade" id="assignModal${repair.detailId}" tabindex="-1">
                                <div class="modal-dialog modal-dialog-scrollable modal-dialog-centered modal-lg" style="max-height: 90vh;">
                                    <div class="modal-content" style="max-height: 100vh;">
                                        <div class="modal-header bg-primary text-white">
                                            <h5 class="modal-title">
                                                <i class="bi bi-person-plus"></i> Assign Repair Task
                                            </h5>
                                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                        </div>
                                        <form method="POST" action="${pageContext.request.contextPath}/techmanager/assign-repair" 
                                              onsubmit="return validateForm(this, '${repair.detailId}')"> <%-- Thêm validation onsubmit --%>
                                            <div class="modal-body" style="max-height: calc(90vh - 120px); overflow-y: auto;">
                                                <input type="hidden" name="detailId" value="${repair.detailId}">
                                                
                                                <div class="alert alert-info mb-3">
                                                    <strong>Vehicle:</strong> ${repair.vehicleModel} (${repair.licensePlate})<br>
                                                    <strong>Overall Task:</strong> ${repair.taskDescription}<br>
                                                    <strong>Estimate:</strong> 
                                                    <fmt:formatNumber value="${repair.estimateAmount}" 
                                                                    type="currency" 
                                                                    currencyCode="VND"/>
                                                </div>
                                                
                                                <div class="mb-3">
                                                    <label class="form-label">Specific Task for This Technician <span class="text-danger">*</span></label>
                                                    <textarea name="taskDescription" 
                                                              class="form-control" 
                                                              rows="2" 
                                                              required
                                                              placeholder="E.g., Repair front brake pads"></textarea>
                                                    <small class="text-muted">Describe what THIS technician will do (you can assign this detail to multiple technicians)</small>
                                                </div>
                                                
                                                <div class="mb-3">
                                                    <label class="form-label">Select Technician <span class="text-danger">*</span></label>
                                                    <select name="technicianId" id="technicianId_${repair.detailId}" class="form-select" required onchange="loadSchedule('${repair.detailId}')">
                                                        <option value="">-- Select Technician --</option>
                                                        <c:forEach var="tech" items="${availableTechnicians}">
                                                            <option value="${tech.employeeID}">
                                                                ${tech.fullName} 
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                
                                                <div class="card mb-3 border-info">
                                                    <div class="card-header bg-info text-white">
                                                        <i class="bi bi-calendar-check"></i> Task Scheduling
                                                    </div>
                                                    <div class="card-body">
                                                        <div class="row">
                                                            <div class="col-md-6 mb-3">
                                                                <label for="plannedStart_${repair.detailId}" class="form-label">
                                                                    Planned Start <span class="text-danger">*</span>
                                                                </label>
                                                                <input type="datetime-local" class="form-control" 
                                                                       name="plannedStart" id="plannedStart_${repair.detailId}"
                                                                       required
                                                                       onchange="loadSchedule('${repair.detailId}'); validatePlannedTimes('${repair.detailId}')">
                                                                <div class="invalid-feedback">Planned start time cannot be in the past.</div>
                                                            </div>
                                                            <div class="col-md-6 mb-3">
                                                                <label for="plannedEnd_${repair.detailId}" class="form-label">Planned End</label>
                                                                <input type="datetime-local" class="form-control" 
                                                                       name="plannedEnd" id="plannedEnd_${repair.detailId}"
                                                                       onchange="validatePlannedTimes('${repair.detailId}')">
                                                                <div class="invalid-feedback">Planned end must be after planned start.</div>
                                                            </div>
                                                        </div>
                                                        
                                                        <div id="schedulePreview_${repair.detailId}" class="alert alert-light d-none">
                                                            <strong><i class="bi bi-clock-history"></i> Technician's Schedule:</strong>
                                                            <div id="scheduleContent_${repair.detailId}" class="mt-2"></div>
                                                            <button type="button" class="btn btn-sm btn-outline-primary mt-2" onclick="refreshSchedule('${repair.detailId}')">
                                                                <i class="bi bi-arrow-clockwise"></i> Refresh
                                                            </button>
                                                        </div>
                                                    </div>
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
                                                    <textarea name="notes" class="form-control" rows="3" 
                                                              placeholder="Any special instructions..."></textarea>
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
                        
                        <c:if test="${not empty approvedRepairs}">
                                </div> <%-- /.list-group --%>
                            </div> <%-- /.collapse --%>
                        </div> <%-- /.card --%>
                        </c:if>
                    </c:otherwise>
                </c:choose>

                <jsp:include page="footer-techmanager.jsp"/>
            </div> <%-- /.container-fluid --%>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <script src="${pageContext.request.contextPath}/assets/js/techmanager/assign-repair.js"></script>
</body>
</html>