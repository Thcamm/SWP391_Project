<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="context-path" content="${pageContext.request.contextPath}">
    <title>Reassign Tasks - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css">
</head>
<body>
    <%@ include file="header-techmanager.jsp" %>
    
    <div class="main-container">
        <jsp:include page="sidebar-techmanager.jsp">
            <jsp:param name="activeMenu" value="reassign-tasks" />
        </jsp:include>
        
        <div class="content-wrapper">
            <!-- Page Header -->
            <div class="page-header">
                <h1 class="h2">
                    <i class="bi bi-arrow-repeat text-primary"></i> Task Reassignment
                </h1>
                <p class="text-muted">Reassign cancelled tasks (overdue or declined) to other technicians</p>
            </div>

            <!-- Messages -->
            <c:if test="${not empty param.message}">
                <c:set var="alertType" value="${not empty param.type ? param.type : 'info'}" />
                <div class="alert alert-${alertType} alert-dismissible fade show" role="alert">
                    <c:choose>
                        <c:when test="${param.type == 'success'}">
                            <i class="bi bi-check-circle-fill"></i>
                        </c:when>
                        <c:when test="${param.type == 'error' or param.type == 'danger'}">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-info-circle-fill"></i>
                        </c:otherwise>
                    </c:choose>
                    <strong><c:out value="${param.message}" /></strong>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Statistics -->
            <div class="row mb-4">
                <div class="col-md-3">
                    <div class="card text-white bg-danger">
                        <div class="card-body">
                            <h5 class="card-title"><i class="bi bi-exclamation-circle"></i> Total Cancelled</h5>
                            <h2>${totalCancelled}</h2>
                            <small>Need reassignment</small>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="card text-white bg-info">
                        <div class="card-body">
                            <h5 class="card-title"><i class="bi bi-people"></i> Technicians</h5>
                            <h2>${technicians.size()}</h2>
                            <small>Available</small>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Cancelled Tasks List -->
            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="bi bi-list-check"></i> Tasks Need Reassignment</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty cancelledTasks}">
                            <div class="alert alert-success">
                                <i class="bi bi-check-circle"></i> Excellent! No tasks need reassignment at this time.
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:set var="currentWOId" value="" />
                            <c:forEach items="${cancelledTasks}" var="task">
                                <c:if test="${task.workOrderId != currentWOId}">
                                    <c:if test="${currentWOId != ''}">
                                        </tbody></table></div></div></div></div>
                                    </c:if>
                                    <div class="card mb-3" style="border-left: 4px solid #dc3545;">
                                        <div class="card-header bg-light d-flex justify-content-between" style="cursor: pointer;" data-bs-toggle="collapse" data-bs-target="#woCancel${task.workOrderId}">
                                            <div>
                                                <h6 class="mb-1">
                                                    <i class="bi bi-file-earmark-x"></i>
                                                    <strong>Work Order #${task.workOrderId}</strong>
                                                    <span class="badge bg-danger ms-2">Has Cancelled Tasks</span>
                                                </h6>
                                                <small class="text-muted">
                                                    <i class="bi bi-car-front"></i> ${task.vehicleInfo} |
                                                    <i class="bi bi-person"></i> ${task.customerName}
                                                </small>
                                            </div>
                                            <i class="bi bi-chevron-down"></i>
                                        </div>
                                        <div id="woCancel${task.workOrderId}" class="collapse show">
                                            <div class="card-body p-0">
                                                <div class="table-responsive">
                                                    <table class="table table-hover mb-0">
                                                        <thead class="table-light">
                                                            <tr>
                                                                <th>Detail #</th>
                                                                <th>Assignment #</th>
                                                                <th>Type</th>
                                                                <th>Cancel Reason</th>
                                                                <th>Previous Tech</th>
                                                                <th>Original Schedule</th>
                                                                <th>Action</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                    <c:set var="currentWOId" value="${task.workOrderId}" />
                                </c:if>
                                <tr>
                                    <td><strong class="text-primary">#${task.detailId}</strong></td>
                                    <td><strong>#${task.assignmentId}</strong></td>
                                    <td>
                                        <span class="badge bg-primary">${task.taskType}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${task.cancelReasonType == 'DECLINED'}">
                                                <span class="badge bg-warning">
                                                    <i class="bi bi-hand-thumbs-down"></i> Declined
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">
                                                    <i class="bi bi-clock"></i> Overdue
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <i class="bi bi-person-badge"></i> ${task.technicianName}
                                    </td>
                                    <td>
                                        <small>
                                            <i class="bi bi-clock"></i>
                                            ${task.plannedStart}<br>
                                            → ${task.plannedEnd}
                                        </small>
                                    </td>
                                    <td>
                                        <button type="button" 
                                                class="btn btn-sm btn-primary" 
                                                data-bs-toggle="modal" 
                                                data-bs-target="#reassignModal${task.assignmentId}">
                                            <i class="bi bi-arrow-repeat"></i> Reassign
                                        </button>
                                        
                                        <c:if test="${task.cancelReasonType == 'DECLINED'}">
                                            <button type="button" 
                                                    class="btn btn-sm btn-outline-warning" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#reasonModal${task.assignmentId}">
                                                <i class="bi bi-chat-text"></i> Reason
                                            </button>
                                        </c:if>
                                    </td>
                                </tr>                                            <!-- Reassignment Modal -->
                                            <div class="modal fade" id="reassignModal${task.assignmentId}" tabindex="-1">
                                                <div class="modal-dialog modal-lg">
                                                    <div class="modal-content">
                                                        <form method="post" 
                                                              action="${pageContext.request.contextPath}/techmanager/reassign-tasks"
                                                              onsubmit="return handleReassignSubmit(this, '${task.assignmentId}')">
                                                            <div class="modal-header bg-primary text-white">
                                                                <h5 class="modal-title">
                                                                    <i class="bi bi-arrow-repeat"></i> Reassign Task #${task.assignmentId}
                                                                </h5>
                                                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <input type="hidden" name="assignmentId" value="${task.assignmentId}">
                                                                
                                                                <!-- Task Info -->
                                                                <div class="alert alert-light border">
                                                                    <strong>Task Information:</strong><br>
                                                                    <strong>Vehicle:</strong> ${task.vehicleInfo}<br>
                                                                    <strong>Customer:</strong> ${task.customerName}<br>
                                                                    <strong>Previous Task:</strong> ${task.taskDescription}<br>
                                                                    <strong>Previous Tech:</strong> ${task.technicianName}<br>
                                                                    <strong>Cancel Reason:</strong>
                                                                    <c:choose>
                                                                        <c:when test="${task.cancelReasonType == 'DECLINED'}">
                                                                            <span class="badge bg-warning">Declined by Technician</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge bg-danger">SLA Violation (Overdue)</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>

                                                                <c:if test="${task.cancelReasonType == 'DECLINED' && !empty task.declineReason}">
                                                                    <div class="alert alert-warning">
                                                                        <strong><i class="bi bi-chat-square-text"></i> Decline Reason:</strong>
                                                                        <p class="mb-0 mt-1">${task.declineReason}</p>
                                                                    </div>
                                                                </c:if>

                                                                <!-- NEW: Task Description for Reassignment -->
                                                                <div class="mb-3">
                                                                    <label class="form-label">
                                                                        New Task Description <span class="text-danger">*</span>
                                                                    </label>
                                                                    <textarea name="taskDescription" 
                                                                              class="form-control" 
                                                                              rows="2" 
                                                                              required
                                                                              placeholder="Describe the specific task for the new technician..."></textarea>
                                                                    <small class="text-muted">You can assign different tasks to different technicians for the same WorkOrderDetail</small>
                                                                </div>

                                                                <!-- Select New Technician -->
                                                                <div class="mb-3">
                                                                    <label class="form-label">
                                                                        Select New Technician <span class="text-danger">*</span>
                                                                    </label>
                                                                        <select name="newTechnicianId" 
                                                                            id="techSelect_${task.assignmentId}" 
                                                                            class="form-select" 
                                                                            required 
                                                                            onchange="loadTechSchedule('${task.assignmentId}')">
                                                                        <option value="">-- Choose Technician --</option>
                                                                        <c:forEach items="${technicians}" var="tech">
                                                                            <option value="${tech.employeeId}">
                                                                                ${tech.fullName} - ${tech.employeeCode}
                                                                            </option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </div>

                                                                <!-- NEW Scheduling -->
                                                                <div class="card mb-3 border-info">
                                                                    <div class="card-header bg-info text-white">
                                                                        <i class="bi bi-calendar-check"></i> New Schedule
                                                                    </div>
                                                                    <div class="card-body">
                                                                        <div class="row">
                                                                            <div class="col-md-6 mb-3">
                                                                                <label class="form-label">Planned Start</label>
                                                                                <input type="datetime-local" 
                                                                                       class="form-control" 
                                                                                       name="plannedStart" 
                                                                                       id="startInput_${task.assignmentId}"
                                                                                       onchange="loadTechSchedule('${task.assignmentId}')">
                                                                            </div>
                                                                            <div class="col-md-6 mb-3">
                                                                                <label class="form-label">Planned End</label>
                                                                                <input type="datetime-local" 
                                                                                       class="form-control" 
                                                                                       name="plannedEnd" 
                                                                                       id="endInput_${task.assignmentId}">
                                                                            </div>
                                                                        </div>
                                                                        
                                                                        <!-- Schedule Preview -->
                                                                        <div id="scheduleDiv_${task.assignmentId}" class="alert alert-light d-none">
                                                                            <strong><i class="bi bi-clock-history"></i> Technician Schedule:</strong>
                                                                            <div id="scheduleContent_${task.assignmentId}" class="mt-2"></div>
                                                                            <button type="button" 
                                                                                    class="btn btn-sm btn-outline-primary mt-2" 
                                                                                    onclick="refreshTechSchedule('${task.assignmentId}')">
                                                                                <i class="bi bi-arrow-clockwise"></i> Refresh
                                                                            </button>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                                <button type="submit" class="btn btn-primary" id="submitBtn${task.assignmentId}">
                                                                    <i class="bi bi-check-circle"></i> Reassign Task
                                                                </button>
                                                                <button type="button" class="btn btn-primary d-none" id="loadingBtn${task.assignmentId}" disabled>
                                                                    <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                                                                    Processing...
                                                                </button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- Decline Reason Modal (if declined) -->
                                            <c:if test="${task.cancelReasonType == 'DECLINED'}">
                                                <div class="modal fade" id="reasonModal${task.assignmentId}" tabindex="-1">
                                                    <div class="modal-dialog">
                                                        <div class="modal-content">
                                                            <div class="modal-header bg-warning text-dark">
                                                                <h5 class="modal-title">
                                                                    <i class="bi bi-chat-square-text"></i> Decline Reason
                                                                </h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body">
                                                                <p><strong>Declined By:</strong> ${task.technicianName}</p>
                                                                <p><strong>Declined At:</strong> ${task.declinedAt}</p>
                                                                <div class="alert alert-warning">
                                                                    <strong>Reason:</strong>
                                                                    <p class="mb-0 mt-1">
                                                                        <c:choose>
                                                                            <c:when test="${empty task.declineReason}">
                                                                                No reason provided
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                ${task.declineReason}
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </p>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer">
                                                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                            <c:if test="${not empty cancelledTasks}">
                                </tbody></table></div></div></div></div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/techmanager/reassign-tasks.js"></script>
    
    <%@ include file="footer-techmanager.jsp" %>
</body>
</html>
