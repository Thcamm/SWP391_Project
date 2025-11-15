<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Assign Diagnosis - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css">
    <style>
        /* Minimal Bootstrap styles for collapse functionality */
        .collapse:not(.show) {
            display: none;
        }
        .collapsing {
            height: 0;
            overflow: hidden;
            transition: height 0.35s ease;
        }
    </style>
<body>
    <%@ include file="header-techmanager.jsp" %>
    
    <div class="main-container">
        <jsp:include page="sidebar-techmanager.jsp">
            <jsp:param name="activeMenu" value="assign-diagnosis" />
        </jsp:include>
        
        <div class="content-wrapper">
            <div class="page-header">
                <h1 class="h2">
                    <i class="bi bi-person-check"></i> Assign Diagnosis Tasks
                </h1>
                <p class="text-muted">
                    <span class="badge bg-success">NEW:</span> You can assign the same WorkOrderDetail to <strong>multiple technicians</strong> for different diagnosis tasks.
                </p>
                <div class="btn-toolbar">
                    <a href="${pageContext.request.contextPath}/techmanager/service-requests" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Back to Requests
                    </a>
                </div>
            </div>

            <c:if test="${param.message != null}">
                <div class="alert alert-${param.type} alert-dismissible fade show" role="alert">
                    <c:out value="${param.message}" />
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <div class="row">

                <div class="col-lg-8">

                    <c:if test="${not empty inProgressTasks}">
                        <div class="card shadow-sm mb-4">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0"><i class="bi bi-hourglass-split"></i> Diagnosis Tasks In Progress</h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>Assignment #</th>
                                                <th>WO #</th>
                                                <th>Vehicle</th>
                                                <th>Customer</th>
                                                <th>Technician</th>
                                                <th>Started At</th>
                                                <th>Task Description</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${inProgressTasks}" var="task">
                                                <tr>
                                                    <td><strong>#${task.assignmentId}</strong></td>
                                                    <td><strong>#${task.workOrderId}</strong></td>
                                                    <td>
                                                        <i class="bi bi-car-front"></i>
                                                        ${task.vehicleInfo}
                                                    </td>
                                                    <td>
                                                        <i class="bi bi-person"></i>
                                                        ${task.customerName}
                                                    </td>
                                                    <td>
                                                        <i class="bi bi-person-badge"></i>
                                                        <strong>${task.technicianName}</strong><br>
                                                        <small class="text-muted">${task.technicianCode}</small>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${task.startAt}" pattern="dd/MM/yyyy HH:mm" />
                                                    </td>
                                                    <td>${task.workOrderDetailDescription}</td>
                                                    <td>
                                                        <span class="badge bg-success">
                                                            <i class="bi bi-hourglass-split"></i> ${task.status}
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

                    <div class="card shadow-sm">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0"><i class="bi bi-list-check"></i> WorkOrders Needing Diagnosis Assignment</h5>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${empty pendingDetails}">
                                    <div class="alert alert-info">
                                        <i class="bi bi-info-circle"></i> No diagnosis assignments pending. All approved ServiceRequests have been assigned!
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="currentWO" value="" />
                                    <c:forEach items="${pendingDetails}" var="detail" varStatus="loop">
                                        
                                        <c:if test="${detail.workOrderId != currentWO}">
                                            
                                            <c:if test="${currentWO != ''}">
                                                </tbody></table></div></div></div></div>
                                            </c:if>
                                            
                                            <div class="card mb-3" style="border-left: 4px solid #0d6efd;">
                                                <div class="card-header bg-light d-flex justify-content-between align-items-center" style="cursor: pointer;" data-bs-toggle="collapse" data-bs-target="#woGroup${detail.workOrderId}">
                                                    <div>
                                                        <h6 class="mb-1">
                                                            <i class="bi bi-file-earmark-text"></i>
                                                            <strong>Work Order #${detail.workOrderId}</strong>
                                                        </h6>
                                                        <small class="text-muted">
                                                            <i class="bi bi-car-front"></i> ${detail.vehicleInfo} |
                                                            <i class="bi bi-person"></i> ${detail.customerName}
                                                        </small>
                                                    </div>
                                                    <i class="bi bi-chevron-down"></i>
                                                </div>
                                                <div id="woGroup${detail.workOrderId}" class="collapse show">
                                                    <div class="card-body p-0">
                                                        <div class="table-responsive">
                                                            <table class="table table-hover mb-0">
                                                                <thead class="table-light">
                                                                    <tr>
                                                                        <th>Detail #</th>
                                                                        <th>Task Description</th>
                                                                        <th>Est. Hours</th>
                                                                        <th>Created At</th>
                                                                        <th>Action</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                            <c:set var="currentWO" value="${detail.workOrderId}" />
                                        </c:if>
                                        
                                        <tr>
                                            <td><strong class="text-primary">#${detail.detailId}</strong></td>
                                            <td>
                                                ${detail.taskDescription}
                                                <div class="mt-1">
                                                    <span class="badge bg-secondary">
                                                        <i class="bi bi-list-check"></i> Total: ${detail.totalAssignments}
                                                    </span>
                                                    <span class="badge ${detail.activeTasks > 0 ? 'bg-warning text-dark' : 'bg-success'}">
                                                        <i class="bi bi-person-workspace"></i> Active: ${detail.activeTasks}
                                                    </span>
                                                </div>
                                                
                                                <c:if test="${not empty detail.existingAssignments}">
                                                    <div class="mt-2">
                                                        <button class="btn btn-sm btn-outline-info" type="button" 
                                                                data-bs-toggle="collapse" 
                                                                data-bs-target="#diagAssignments${detail.detailId}"
                                                                aria-expanded="false"
                                                                aria-controls="diagAssignments${detail.detailId}">
                                                            <i class="bi bi-eye"></i> View Assignments
                                                        </button>
                                                        <div class="collapse mt-2" id="diagAssignments${detail.detailId}">
                                                            <table class="table table-sm table-bordered">
                                                                <thead class="table-light">
                                                                    <tr>
                                                                        <th>ID</th>
                                                                        <th>Technician</th>
                                                                        <th>Status</th>
                                                                        <th>Priority</th>
                                                                    </tr>
                                                                </thead>
                                                                <tbody>
                                                                    <c:forEach var="assignment" items="${detail.existingAssignments}">
                                                                        <tr>
                                                                            <td><strong>#${assignment.assignmentId}</strong></td>
                                                                            <td>
                                                                                <i class="bi bi-person-badge"></i> ${assignment.technicianName}<br>
                                                                                <small class="text-muted">${assignment.employeeCode}</small>
                                                                            </td>
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
                                                </c:if>
                                            </td>
                                            <td>${detail.estimateHours} hrs</td>
                                            <td>
                                                <fmt:formatDate value="${detail.workOrderCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </td>
                                            <td>
                                                <button type="button" class="btn btn-sm btn-primary" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#assignModal"
                                                        data-detail-id="${detail.detailId}"
                                                        data-wo-id="${detail.workOrderId}"
                                                        data-vehicle="${detail.vehicleInfo}"
                                                        data-task="${detail.taskDescription}"
                                                        onclick="prepareAssignment(this)">
                                                    <i class="bi bi-person-plus"></i> Assign
                                                </button>
                                            </td>
                                        </tr>

                                        <c:if test="${loop.last}">
                                            </tbody></table></div></div></div></div>
                                        </c:if>

                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div> <div class="col-lg-4">

                    <div class="card shadow-sm mb-4">
                        <div class="card-header bg-light">
                            <h5 class="mb-0"><i class="bi bi-bar-chart-line-fill"></i> At a Glance</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-12 mb-3">
                                    <div class="card text-white bg-warning">
                                        <div class="card-body">
                                            <h5 class="card-title"><i class="bi bi-clipboard-pulse"></i> Pending Diagnosis</h5>
                                            <h2>${totalPending}</h2>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-12 mb-3">
                                    <div class="card text-white bg-success">
                                        <div class="card-body">
                                            <h5 class="card-title"><i class="bi bi-hourglass-split"></i> In Progress</h5>
                                            <h2>${totalInProgress}</h2>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-12">
                                    <div class="card text-white bg-info">
                                        <div class="card-body">
                                            <h5 class="card-title"><i class="bi bi-people"></i> Available Technicians</h5>
                                            <h2>${technicians.size()}</h2>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="card shadow-sm">
                        <div class="card-header bg-secondary text-white">
                            <h5 class="mb-0"><i class="bi bi-people-fill"></i> Available Technicians</h5>
                        </div>
                        <div class="card-body" style="max-height: 500px; overflow-y: auto;">
                            <c:choose>
                                <c:when test="${empty technicians}">
                                    <div class="alert alert-warning mb-0">No technicians available.</div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${technicians}" var="tech">
                                        <div class="card mb-2">
                                            <div class="card-body py-2 px-3">
                                                <h6 class="card-title mb-1">
                                                    <i class="bi bi-person-badge"></i> ${tech.fullName}
                                                </h6>
                                                <p class="card-text small text-muted mb-0">
                                                    Code: ${tech.employeeCode}<br>
                                                    Phone: ${tech.phoneNumber}
                                                </p>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                
                </div> </div> </div> </div> <div class="modal fade" id="assignModal" tabindex="-1">
         <div class="modal-dialog modal-lg" style="max-height: 90vh; overflow-y: auto;">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/techmanager/assign-diagnosis">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title"><i class="bi bi-clipboard-check"></i> Assign Diagnosis Task</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="detailId" id="modalDetailId">
                        
                        <div class="alert alert-light mb-2 py-2">
                            <small>
                                <strong>WO:</strong> <span id="modalWorkOrderInfo"></span><br>
                                <strong>Vehicle:</strong> <span id="modalVehicleInfo"></span><br>
                                <strong>Overall Task:</strong> <span id="modalTaskDesc"></span>
                            </small>
                        </div>

                        <div class="mb-2">
                            <label for="taskDescription" class="form-label small">Specific Task for This Technician <span class="text-danger">*</span></label>
                            <textarea class="form-control form-control-sm" name="taskDescription" id="taskDescription" rows="2" 
                                      required
                                      placeholder="E.g., 'Kiểm tra hệ thống phanh' or 'Chẩn đoán động cơ'"></textarea>
                            <small class="text-muted">Describe what THIS technician will diagnose (you can assign this detail to multiple technicians)</small>
                        </div>

                        <div class="mb-2">
                            <label for="technicianId" class="form-label small">Select Technician <span class="text-danger">*</span></label>
                            <select class="form-select form-select-sm" name="technicianId" id="technicianId" required onchange="loadTechnicianSchedule()">
                                <option value="">-- Choose Technician --</option>
                                <c:forEach items="${technicians}" var="tech">
                                    <option value="${tech.employeeId}">
                                        ${tech.fullName} - ${tech.employeeCode}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="card mb-2 border-info">
                            <div class="card-header bg-info text-white py-1">
                                <small><i class="bi bi-calendar-check"></i> Task Scheduling (Optional)</small>
                            </div>
                            <div class="card-body py-2">
                                <div class="row">
                                    <div class="col-md-6 mb-2">
                                        <label for="plannedStart" class="form-label small">Planned Start</label>
                                        <input type="datetime-local" class="form-control form-control-sm" name="plannedStart" id="plannedStart">
                                    </div>
                                    <div class="col-md-6 mb-2">
                                        <label for="plannedEnd" class="form-label small">Planned End</label>
                                        <input type="datetime-local" class="form-control form-control-sm" name="plannedEnd" id="plannedEnd">
                                    </div>
                                </div>
                                
                                <div id="schedulePreview" class="alert alert-light d-none py-2 mb-0">
                                    <small><strong><i class="bi bi-clock-history"></i> Technician's Schedule:</strong></small>
                                    <div id="scheduleContent" class="mt-1"></div>
                                    <button type="button" class="btn btn-sm btn-outline-primary mt-1" onclick="refreshSchedule()">
                                        <i class="bi bi-arrow-clockwise"></i> Refresh
                                    </button>
                                </div>
                            </div>
                        </div>

                        <div class="mb-2">
                            <label for="priority" class="form-label small">Priority <span class="text-danger">*</span></label>
                            <select class="form-select form-select-sm" name="priority" id="priority" required>
                                <option value="LOW">Low</option>
                                <option value="MEDIUM" selected>Medium</option>
                                <option value="HIGH">High</option>
                                <option value="URGENT">Urgent</option>
                            </select>
                        </div>

                        <div class="mb-2">
                            <label for="notes" class="form-label small">Notes (Optional)</label>
                            <textarea class="form-control form-control-sm" name="notes" id="notes" rows="2" 
                                      placeholder="Any special instructions..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer py-2">
                        <button type="button" class="btn btn-sm btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-sm btn-primary">
                            <i class="bi bi-check-circle"></i> Assign Task
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/techmanager/assign-diagnosis.js"></script>
    
    <%@ include file="footer-techmanager.jsp" %>
</body>
</html>