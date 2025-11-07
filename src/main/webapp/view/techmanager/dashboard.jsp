<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css" />
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
              <p class="text-muted mb-0">Overview of all service operations</p>
            </div>
            <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
              <i class="bi bi-arrow-clockwise"></i>
              Refresh
            </button>
          </div>
        </div>

        <!-- Quick Stats -->
        <div class="row mb-4">
          <!-- ALERT 1: Tasks Need Reassignment (Overdue + Declined) -->
          <c:if test="${stats.tasksNeedReassignment > 0}">
            <div class="col-md-12 mb-3">
              <div class="alert alert-danger d-flex align-items-center">
                <i class="bi bi-exclamation-triangle-fill me-3"></i>
                <div class="flex-grow-1">
                  <strong>⚠️ ${stats.tasksNeedReassignment} Task(s) Need Reassignment</strong>
                  <p class="mb-0">
                    <c:if test="${stats.overdueTasks > 0}">
                      <span class="badge bg-danger">${stats.overdueTasks} Overdue (SLA)</span>
                    </c:if>
                    <c:if test="${stats.declinedTasks > 0}">
                      <span class="badge bg-warning text-dark ms-2">
                        ${stats.declinedTasks} Declined by Technicians
                      </span>
                    </c:if>
                  </p>
                </div>
                <a href="${pageContext.request.contextPath}/techmanager/tasks-need-reassignment" class="btn btn-danger">
                  <i class="bi bi-arrow-right"></i>
                  View & Reassign
                </a>
              </div>
            </div>
          </c:if>

          <!-- ALERT 2: Overdue Tasks (SLA Violation) -->
          <c:if test="${stats.overdueTasks > 0}">
            <div class="col-md-12 mb-3">
              <div class="alert alert-warning d-flex align-items-center">
                <i class="bi bi-clock-history me-3"></i>
                <div class="flex-grow-1">
                  <strong>🕐 ${stats.overdueTasks} Task(s) Overdue</strong>
                  <p class="mb-0">Technicians haven't started these tasks past the planned start time.</p>
                </div>
                <a href="${pageContext.request.contextPath}/techmanager/overdue-tasks" class="btn btn-warning">
                  <i class="bi bi-arrow-right"></i>
                  Monitor & Cancel
                </a>
              </div>
            </div>
          </c:if>

          <!-- ALERT 3: Declined Tasks -->
          <c:if test="${stats.declinedTasks > 0}">
            <div class="col-md-12 mb-3">
              <div class="alert alert-info d-flex align-items-center">
                <i class="bi bi-hand-thumbs-down-fill me-3"></i>
                <div class="flex-grow-1">
                  <strong> ${stats.declinedTasks} Task(s) Declined by Technicians</strong>
                  <p class="mb-0">Technicians proactively declined these tasks with reasons.</p>
                </div>
                <a href="${pageContext.request.contextPath}/techmanager/declined-tasks" class="btn btn-info">
                  <i class="bi bi-arrow-right"></i>
                  View Reasons
                </a>
              </div>
            </div>
          </c:if>

          <!-- OLD ALERT: Rejected Tasks (Keep for backward compatibility) -->
          <c:if test="${stats.rejectedTasks > 0}">
            <div class="col-md-12 mb-3">
              <div class="alert alert-danger d-flex align-items-center">
                <i class="bi bi-exclamation-triangle-fill me-3"></i>
                <div class="flex-grow-1">
                  <strong>⚠️ ${stats.rejectedTasks} Task(s) Rejected by Technicians</strong>
                  <p class="mb-0">These tasks need to be reassigned to other technicians.</p>
                </div>
                <a href="${pageContext.request.contextPath}/techmanager/rejected-tasks" class="btn btn-danger">
                  <i class="bi bi-arrow-right"></i>
                  View & Reassign
                </a>
              </div>
            </div>
          </c:if>

          <!-- Row 1: Overview Stats -->
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
                    <p class="text-muted mb-1 small">Total Work Orders</p>
                    <h3 class="mb-0">${stats.totalWorkOrders}</h3>
                  </div>
                  <div class="stats-icon bg-info bg-opacity-10 text-info">
                    <i class="bi bi-folder"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Service Requests Section -->
        <div class="row mb-4">
          <div class="col-md-12">
            <div class="card">
              <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                  <i class="bi bi-clipboard-check"></i>
                  Service Requests
                </h5>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-6">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-primary text-white">
                            <i class="bi bi-hourglass-split"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">Pending Service Requests</p>
                            <h4 class="mb-0">${stats.pendingRequests}</h4>
                          </div>
                          <a
                            href="${pageContext.request.contextPath}/techmanager/service-requests"
                            class="btn btn-sm btn-primary">
                            <i class="bi bi-arrow-right"></i>
                            View
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-success text-white">
                            <i class="bi bi-check-circle"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">Approved Quotes</p>
                            <h4 class="mb-0">${stats.approvedQuotes}</h4>
                          </div>
                          <a
                            href="${pageContext.request.contextPath}/techmanager/assign-repair"
                            class="btn btn-sm btn-success">
                            <i class="bi bi-arrow-right"></i>
                            Assign
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Diagnosis Tasks Section -->
        <div class="row mb-4">
          <div class="col-md-12">
            <div class="card">
              <div class="card-header bg-info text-white">
                <h5 class="mb-0">
                  <i class="bi bi-search"></i>
                  Diagnosis Tasks
                </h5>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-4">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-info text-white">
                            <i class="bi bi-person-plus"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">Assigned Diagnosis</p>
                            <h4 class="mb-0">${stats.assignedDiagnosis}</h4>
                          </div>
                          <a
                            href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
                            class="btn btn-sm btn-info">
                            <i class="bi bi-arrow-right"></i>
                            Assign
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-warning text-white">
                            <i class="bi bi-tools"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">In-Progress</p>
                            <h4 class="mb-0">${stats.inProgressDiagnosis}</h4>
                          </div>
                          <span class="badge bg-warning">Working</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-4">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-secondary text-white">
                            <i class="bi bi-file-earmark-text"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">Pending Customer Approval</p>
                            <h4 class="mb-0">${stats.pendingQuotes}</h4>
                          </div>
                          <span class="badge bg-secondary">Waiting</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Repair Tasks Section -->
        <div class="row mb-4">
          <div class="col-md-12">
            <div class="card">
              <div class="card-header bg-warning text-dark">
                <h5 class="mb-0">
                  <i class="bi bi-wrench"></i>
                  Repair Tasks
                </h5>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-6">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-warning text-white">
                            <i class="bi bi-gear"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">Active Repairs</p>
                            <h4 class="mb-0">${stats.activeRepairs}</h4>
                          </div>
                          <span class="badge bg-warning">In Progress</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="card border-0 bg-light mb-3">
                      <div class="card-body">
                        <div class="d-flex align-items-center gap-3">
                          <div class="stats-icon bg-success text-white">
                            <i class="bi bi-check-circle"></i>
                          </div>
                          <div class="flex-grow-1">
                            <p class="mb-0 text-muted small">Completed Repairs</p>
                            <h4 class="mb-0">${stats.completedRepairs}</h4>
                          </div>
                            <span class="badge bg-warning">In Progress</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <jsp:include page="footer-techmanager.jsp" />
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
