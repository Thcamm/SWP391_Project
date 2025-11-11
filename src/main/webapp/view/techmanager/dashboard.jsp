<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css" />
    <style>
      /* Workflow Pipeline Styles */
      .workflow-pipeline {
        display: flex;
        flex-wrap: wrap;
        gap: 15px;
        margin: 20px 0;
      }
      .workflow-phase {
        flex: 1;
        min-width: 200px;
        background: white;
        border-radius: 8px;
        padding: 15px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        border-left: 4px solid #6c757d;
        transition: all 0.3s ease;
      }
      .workflow-phase:hover {
        transform: translateY(-5px);
        box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
      }
      .workflow-phase.active {
        border-left-color: #0d6efd;
      }
      .workflow-phase.warning {
        border-left-color: #ffc107;
      }
      .workflow-phase.success {
        border-left-color: #198754;
      }
      .phase-number {
        display: inline-block;
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background: #e9ecef;
        text-align: center;
        line-height: 30px;
        font-weight: bold;
        margin-right: 10px;
      }
      .phase-count {
        font-size: 2rem;
        font-weight: bold;
        margin: 10px 0;
      }
      .phase-action {
        margin-top: 10px;
      }
      .activity-log-table {
        font-size: 0.9rem;
      }
      .activity-time {
        color: #6c757d;
        font-size: 0.85rem;
      }
    </style>
  </head>
  <body>
    <div class="main-container">
      <!-- Sidebar -->
      <c:set var="activeMenu" value="dashboard" scope="request" />
      <jsp:include page="sidebar-techmanager.jsp" />

      <!-- Main Content -->
      <div class="content-wrapper">
        <!-- Header -->
        <jsp:include page="header-techmanager.jsp" />

        <!-- Page Header -->
        <div class="page-header">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-1">
                <i class="bi bi-speedometer2 text-primary"></i>
                Tech Manager Dashboard
              </h2>
              <p class="text-muted mb-0">
                Real-time overview of 7-phase workflow •
                <span class="badge bg-info">${stats.totalWorkOrders} Total Work Orders</span>
              </p>
            </div>
            <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
              <i class="bi bi-arrow-clockwise"></i>
              Refresh
            </button>
          </div>
        </div>

        <!-- ============================================ -->
        <!-- SECTION 1: CRITICAL ALERTS -->
        <!-- ============================================ -->
        <c:if test="${stats.tasksNeedReassignment > 0 || stats.overdueTasks > 0 || stats.declinedTasks > 0}">
          <div class="row mb-4">
            <div class="col-12">
              <div class="card border-danger">
                <div class="card-header bg-danger text-white">
                  <h5 class="mb-0">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    Critical Alerts
                  </h5>
                </div>
                <div class="card-body">
                  <c:if test="${stats.tasksNeedReassignment > 0}">
                    <div class="alert alert-danger d-flex align-items-center mb-2">
                      <i class="bi bi-exclamation-triangle-fill me-3 fs-4"></i>
                      <div class="flex-grow-1">
                        <strong>${stats.tasksNeedReassignment} Task(s) Need Immediate Reassignment</strong>
                        <p class="mb-0 small">
                          <c:if test="${stats.overdueTasks > 0}">
                            <span class="badge bg-danger">${stats.overdueTasks} Overdue (SLA Violation)</span>
                          </c:if>
                          <c:if test="${stats.declinedTasks > 0}">
                            <span class="badge bg-warning text-dark ms-1">
                              ${stats.declinedTasks} Declined by Technicians
                            </span>
                          </c:if>
                        </p>
                      </div>
                      <a href="${pageContext.request.contextPath}/techmanager/reassign-tasks" class="btn btn-danger">
                        <i class="bi bi-arrow-right"></i>
                        View & Reassign
                      </a>
                    </div>
                  </c:if>
                  <c:if test="${stats.overdueTasks > 0}">
                    <div class="alert alert-danger d-flex align-items-center mb-2">
                      <i class="bi bi-exclamation-octagon me-3 fs-4"></i>
                      <div class="flex-grow-1">
                        <strong>${stats.overdueTasks} Overdue Task(s) (SLA Violation)</strong>
                        <p class="mb-0 small">Tasks have passed planned start time without being started.</p>
                      </div>
                      <a href="${pageContext.request.contextPath}/techmanager/overdue-tasks" class="btn btn-danger">
                        <i class="bi bi-arrow-right"></i>
                        View Tasks
                      </a>
                    </div>
                  </c:if>
                  <c:if test="${stats.declinedTasks > 0}">
                    <div class="alert alert-warning d-flex align-items-center mb-2">
                      <i class="bi bi-person-x me-3 fs-4"></i>
                      <div class="flex-grow-1">
                        <strong>${stats.declinedTasks} Declined Task(s)</strong>
                        <p class="mb-0 small">
                          Technicians have declined these assignments. Immediate reassignment needed.
                        </p>
                      </div>
                      <a href="${pageContext.request.contextPath}/techmanager/declined-tasks" class="btn btn-warning">
                        <i class="bi bi-arrow-right"></i>
                        View Tasks
                      </a>
                    </div>
                  </c:if>
                  <c:if test="${stats.overdueDiagnostics > 0}">
                    <div class="alert alert-warning d-flex align-items-center mb-0">
                      <i class="bi bi-clock-history me-3 fs-4"></i>
                      <div class="flex-grow-1">
                        <strong>${stats.overdueDiagnostics} Diagnostic Quote(s) Overdue (>2 days)</strong>
                        <p class="mb-0 small">Customer hasn't responded to quotes. Consider follow-up call.</p>
                      </div>
                      <a href="#pendingDiagnosticsSection" class="btn btn-warning">
                        <i class="bi bi-arrow-down"></i>
                        See Below
                      </a>
                    </div>
                  </c:if>
                </div>
              </div>
            </div>
          </div>
        </c:if>

        <!-- ============================================ -->
        <!-- SECTION 2: QUICK STATS -->
        <!-- ============================================ -->
        <div class="row mb-4">
          <div class="col-md-3">
            <div class="stats-card card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Today's Requests</p>
                    <h3 class="mb-0">${stats.todayRequests}</h3>
                  </div>
                  <div class="stats-icon bg-primary bg-opacity-10 text-primary">
                    <i class="bi bi-calendar-day"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="stats-card card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Week Completed</p>
                    <h3 class="mb-0">${stats.thisWeekCompleted}</h3>
                  </div>
                  <div class="stats-icon bg-success bg-opacity-10 text-success">
                    <i class="bi bi-check-circle"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="stats-card card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Active Repairs</p>
                    <h3 class="mb-0">${stats.activeRepairs}</h3>
                  </div>
                  <div class="stats-icon bg-warning bg-opacity-10 text-warning">
                    <i class="bi bi-tools"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="stats-card card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Ready to Close</p>
                    <h3 class="mb-0">${stats.workOrdersReadyForClosure}</h3>
                  </div>
                  <div class="stats-icon bg-info bg-opacity-10 text-info">
                    <i class="bi bi-folder-check"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- ============================================ -->
        <!-- SECTION 3: WORKFLOW PIPELINE (7 PHASES) -->
        <!-- ============================================ -->
        <div class="row mb-4">
          <div class="col-12">
            <div class="card">
              <div
                class="card-header bg-gradient"
                style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <h5 class="mb-0 text-white">
                  <i class="bi bi-diagram-3"></i>
                  7-Phase Workflow Pipeline
                </h5>
              </div>
              <div class="card-body">
                <div class="workflow-pipeline">
                  <!-- GĐ0→1: Pending Approval -->
                  <div class="workflow-phase ${stats.pendingRequests > 0 ? 'warning' : ''}">
                    <div>
                      <span class="phase-number">0→1</span>
                      <strong>Pending Approval</strong>
                    </div>
                    <div class="phase-count text-primary">${stats.pendingRequests}</div>
                    <p class="small text-muted mb-0">Service requests awaiting approval</p>
                    <div class="phase-action">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/service-requests"
                        class="btn btn-sm btn-outline-primary ${stats.pendingRequests == 0 ? 'disabled' : ''}">
                        <i class="bi bi-check-circle"></i>
                        Approve
                      </a>
                    </div>
                  </div>

                  <!-- GĐ1: Assign Diagnosis -->
                  <div
                    class="workflow-phase ${stats.assignedDiagnosis > 0 || stats.inProgressDiagnosis > 0 ? 'active' : ''}">
                    <div>
                      <span class="phase-number">1</span>
                      <strong>Diagnosis Phase</strong>
                    </div>
                    <div class="phase-count text-info">
                      ${stats.assignedDiagnosis + stats.inProgressDiagnosis} Technicians
                    </div>
                    <p class="small text-muted mb-0">
                      <span class="badge bg-secondary">${stats.assignedDiagnosis} Assigned</span>
                      <span class="badge bg-warning">${stats.inProgressDiagnosis} In Progress</span>
                    </p>
                    <div class="phase-action">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
                        class="btn btn-sm btn-outline-info">
                        <i class="bi bi-person-plus"></i>
                        Assign
                      </a>
                    </div>
                  </div>

                  <!-- GĐ3: Customer Approval -->
                  <div class="workflow-phase ${stats.pendingQuotes > 0 ? 'warning' : ''}">
                    <div>
                      <span class="phase-number">3</span>
                      <strong>Customer Decision</strong>
                    </div>
                    <div class="phase-count text-warning">${stats.pendingQuotes}</div>
                    <p class="small text-muted mb-0">Quotes awaiting customer approval</p>
                    <c:if test="${stats.overdueDiagnostics > 0}">
                      <p class="small mb-0">
                        <span class="badge bg-danger">${stats.overdueDiagnostics} Overdue</span>
                      </p>
                    </c:if>
                    <div class="phase-action">
                      <a href="#pendingDiagnosticsSection" class="btn btn-sm btn-outline-warning">
                        <i class="bi bi-eye"></i>
                        Monitor
                      </a>
                    </div>
                  </div>

                  <!-- GĐ4→5: Ready for Repair Assignment -->
                  <div class="workflow-phase ${stats.unassignedWorkOrderDetails > 0 ? 'warning' : ''}">
                    <div>
                      <span class="phase-number">4→5</span>
                      <strong>Assign Repair</strong>
                    </div>
                    <div class="phase-count text-danger">${stats.unassignedWorkOrderDetails}</div>
                    <p class="small text-muted mb-0">Approved quotes need repair assignment</p>
                    <div class="phase-action">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/assign-repair"
                        class="btn btn-sm btn-outline-danger ${stats.unassignedWorkOrderDetails == 0 ? 'disabled' : ''}">
                        <i class="bi bi-wrench"></i>
                        Assign
                      </a>
                    </div>
                  </div>

                  <!-- GĐ5→6: Repair Execution -->
                  <div class="workflow-phase ${stats.activeRepairs > 0 ? 'active' : ''}">
                    <div>
                      <span class="phase-number">5→6</span>
                      <strong>Repair Phase</strong>
                    </div>
                    <div class="phase-count text-primary">${stats.activeRepairs}</div>
                    <p class="small text-muted mb-0">
                      <span class="badge bg-secondary">${stats.assignedRepairs} Assigned</span>
                      <span class="badge bg-primary">${stats.inProgressRepairs} Working</span>
                    </p>
                    <div class="phase-action">
                      <button class="btn btn-sm btn-outline-secondary" disabled>
                        <i class="bi bi-hourglass-split"></i>
                        In Progress
                      </button>
                    </div>
                  </div>

                  <!-- GĐ6→7: Completed → Close WO -->
                  <div class="workflow-phase ${stats.workOrdersReadyForClosure > 0 ? 'success' : ''}">
                    <div>
                      <span class="phase-number">6→7</span>
                      <strong>Close WorkOrder</strong>
                    </div>
                    <div class="phase-count text-success">${stats.workOrdersReadyForClosure}</div>
                    <p class="small text-muted mb-0">Work orders ready for closure</p>
                    <div class="phase-action">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/close-workorders"
                        class="btn btn-sm btn-outline-success ${stats.workOrdersReadyForClosure == 0 ? 'disabled' : ''}">
                        <i class="bi bi-folder-check"></i>
                        Close
                      </a>
                    </div>
                  </div>

                  <!-- Completed -->
                  <div class="workflow-phase success">
                    <div>
                      <span class="phase-number">✓</span>
                      <strong>Completed</strong>
                    </div>
                    <div class="phase-count text-success">${stats.closedWorkOrders}</div>
                    <p class="small text-muted mb-0">Total closed work orders</p>
                    <div class="phase-action">
                      <span class="badge bg-success-subtle text-success">Archived</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- ============================================ -->
        <!-- SECTION 4: PENDING DIAGNOSTICS (GĐ3 Detail) -->
        <!-- ============================================ -->
        <div class="row mb-4" id="pendingDiagnosticsSection">
          <div class="col-12">
            <div class="card">
              <div class="card-header bg-warning text-dark">
                <h5 class="mb-0">
                  <i class="bi bi-hourglass-split"></i>
                  Diagnostics Awaiting Customer Approval (GĐ3)
                  <span class="badge bg-dark">${stats.pendingQuotes}</span>
                </h5>
              </div>
              <div class="card-body">
                <c:choose>
                  <c:when test="${empty pendingDiagnostics}">
                    <div class="alert alert-success mb-0">
                      <i class="bi bi-check-circle"></i>
                      All diagnostics have been approved or rejected. No pending approvals.
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="table-responsive">
                      <table class="table table-hover">
                        <thead>
                          <tr>
                            <th>WO #</th>
                            <th>Vehicle</th>
                            <th>Customer</th>
                            <th>Issue Found</th>
                            <th>Estimate</th>
                            <th>Technician</th>
                            <th>Days Pending</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach items="${pendingDiagnostics}" var="diag">
                            <tr class="${diag.overdue ? 'table-danger' : ''}">
                              <td><strong>#${diag.workOrderID}</strong></td>
                              <td>
                                <i class="bi bi-car-front"></i>
                                ${diag.vehicleInfo}
                              </td>
                              <td>
                                <div><strong>${diag.customerName}</strong></div>
                                <div><small class="text-muted">${diag.customerPhone}</small></div>
                              </td>
                              <td>
                                <small>${diag.issueFound}</small>
                              </td>
                              <td>
                                <strong>
                                  <fmt:formatNumber
                                    value="${diag.estimateCost}"
                                    type="currency"
                                    currencyCode="VND"
                                    currencySymbol="₫" />
                                </strong>
                              </td>
                              <td>
                                <small>
                                  <i class="bi bi-person"></i>
                                  ${diag.technicianName}
                                </small>
                              </td>
                              <td>
                                <span class="badge ${diag.overdue ? 'bg-danger' : 'bg-warning text-dark'}">
                                  ${diag.daysPending} days
                                </span>
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
        </div>

        <!-- ============================================ -->
        <!-- SECTION 5: RECENT ACTIVITIES -->
        <!-- ============================================ -->
        <div class="row mb-4">
          <div class="col-12">
            <div class="card">
              <div class="card-header bg-secondary text-white">
                <h5 class="mb-0">
                  <i class="bi bi-activity"></i>
                  Recent Technician Activities
                </h5>
              </div>
              <div class="card-body">
                <c:choose>
                  <c:when test="${empty recentActivities}">
                    <p class="text-muted mb-0">No recent activities recorded.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="table-responsive">
                      <table class="table table-sm activity-log-table">
                        <thead>
                          <tr>
                            <th>Time</th>
                            <th>Technician</th>
                            <th>Activity</th>
                            <th>Description</th>
                            <th>Brand</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach items="${recentActivities}" var="activity">
                            <tr>
                              <td class="activity-time">
                                <fmt:formatDate value="${activity.activityTime}" pattern="MM/dd HH:mm" />
                              </td>
                              <td>
                                <i class="bi bi-person-badge"></i>
                                ${activity.technicianName}
                              </td>
                              <td>
                                <span class="badge ${activity.activityBadgeClass}">${activity.activityTypeLabel}</span>
                              </td>
                              <td>
                                <small>${activity.description}</small>
                              </td>
                              <td>
                                <c:choose>
                                  <c:when test="${not empty activity.vehicleInfo}">
                                    <span class="badge bg-secondary bg-opacity-10 text-secondary">
                                      ${activity.vehicleInfo}
                                    </span>
                                  </c:when>
                                  <c:otherwise>
                                    <small class="text-muted fst-italic">—</small>
                                  </c:otherwise>
                                </c:choose>
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
        </div>

        <!-- Footer -->
        <jsp:include page="footer-techmanager.jsp" />
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      // Auto-refresh dashboard every 30 seconds
      setTimeout(function () {
        console.log('Auto-refreshing dashboard...');
        // Uncomment the line below to enable auto-refresh
        // window.location.reload();
      }, 30000);
    </script>
  </body>
</html>
