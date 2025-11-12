<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
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

        <div class="container-fluid mt-3">
          <!-- Page Header -->
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h3>
                    <i class="bi bi-speedometer2 text-primary"></i>
                    Tech Manager Dashboard
                  </h3>
                  <p class="text-muted mb-0">Real-time overview and workflow management</p>
                </div>
                <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
                  <i class="bi bi-arrow-clockwise"></i>
                  Refresh
                </button>
              </div>
            </div>
          </div>

          <!-- Critical Alerts -->
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
                        </div>
                        <a href="${pageContext.request.contextPath}/techmanager/reassign-tasks" class="btn btn-danger">
                          <i class="bi bi-arrow-right"></i>
                          Reassign
                        </a>
                      </div>
                    </c:if>
                    <c:if test="${stats.overdueTasks > 0}">
                      <div class="alert alert-danger d-flex align-items-center mb-2">
                        <i class="bi bi-exclamation-octagon me-3 fs-4"></i>
                        <div class="flex-grow-1">
                          <strong>${stats.overdueTasks} Overdue Task(s)</strong>
                        </div>
                        <a href="${pageContext.request.contextPath}/techmanager/overdue-tasks" class="btn btn-danger">
                          <i class="bi bi-arrow-right"></i>
                          View
                        </a>
                      </div>
                    </c:if>
                    <c:if test="${stats.declinedTasks > 0}">
                      <div class="alert alert-warning d-flex align-items-center mb-0">
                        <i class="bi bi-person-x me-3 fs-4"></i>
                        <div class="flex-grow-1">
                          <strong>${stats.declinedTasks} Declined Task(s)</strong>
                        </div>
                        <a href="${pageContext.request.contextPath}/techmanager/declined-tasks" class="btn btn-warning">
                          <i class="bi bi-arrow-right"></i>
                          View
                        </a>
                      </div>
                    </c:if>
                  </div>
                </div>
              </div>
            </div>
          </c:if>

          <!-- Quick Stats -->
          <div class="row mb-4">
            <div class="col-md-3">
              <div class="card bg-primary text-white">
                <div class="card-body text-center">
                  <h4>${stats.pendingRequests}</h4>
                  <small>Pending Approval</small>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div
                class="card bg-purple text-white"
                style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
                <div class="card-body text-center">
                  <h4>${stats.pendingTriage}</h4>
                  <small>🎯 Awaiting Triage</small>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="card bg-info text-white">
                <div class="card-body text-center">
                  <h4>${stats.unassignedWorkOrderDetails}</h4>
                  <small>Need Assignment</small>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="card bg-warning text-white">
                <div class="card-body text-center">
                  <h4>${stats.activeRepairs}</h4>
                  <small>Active Repairs</small>
                </div>
              </div>
            </div>
          </div>

          <!-- Second Row Stats -->
          <div class="row mb-4">
            <div class="col-md-3">
              <div class="card bg-success text-white">
                <div class="card-body text-center">
                  <h4>${stats.workOrdersReadyForClosure}</h4>
                  <small>Ready to Close</small>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="card bg-secondary text-white">
                <div class="card-body text-center">
                  <h4>${stats.assignedDiagnosis}</h4>
                  <small>Need Diagnosis Assignment</small>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="card bg-dark text-white">
                <div class="card-body text-center">
                  <h4>${stats.pendingQuotes}</h4>
                  <small>Pending Customer Approval</small>
                </div>
              </div>
            </div>
            <div class="col-md-3">
              <div class="card bg-light text-dark">
                <div class="card-body text-center">
                  <h4>${stats.totalWorkOrders}</h4>
                  <small>Total Work Orders</small>
                </div>
              </div>
            </div>
          </div>

          <!-- Main Actions -->
          <div class="row mb-4">
            <div class="col-12">
              <div class="card">
                <div class="card-header bg-primary text-white">
                  <h5 class="mb-0">
                    <i class="bi bi-list-check"></i>
                    Quick Actions
                  </h5>
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    <!-- GĐ1: Service Requests -->
                    <div class="col-md-3">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/service-requests"
                        class="btn btn-outline-primary w-100 p-3">
                        <i class="bi bi-clipboard-check fs-3 d-block mb-2"></i>
                        <strong>GĐ1: Service Requests</strong>
                        <small class="text-muted d-block">Approve & Create WODs</small>
                        <span class="badge bg-primary d-block mt-2">${stats.pendingRequests} Pending</span>
                      </a>
                    </div>
                    
                    <!-- GĐ2: Triage (NEW) -->
                    <div class="col-md-3">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/dashboard"
                        class="btn btn-outline-purple w-100 p-3"
                        style="border-color: #764ba2; color: #764ba2;">
                        <i class="bi bi-funnel-fill fs-3 d-block mb-2"></i>
                        <strong>GĐ2: Triage (Phân Loại)</strong>
                        <small class="text-muted d-block">Classify Services</small>
                        <span class="badge d-block mt-2" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                          ${stats.pendingTriage} Awaiting
                        </span>
                      </a>
                    </div>
                    
                    <!-- GĐ3: Assign Diagnosis -->
                    <div class="col-md-3">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
                        class="btn btn-outline-info w-100 p-3">
                        <i class="bi bi-person-plus fs-3 d-block mb-2"></i>
                        <strong>GĐ3: Assign Diagnosis</strong>
                        <small class="text-muted d-block">DIAGNOSTIC only</small>
                        <span class="badge bg-info d-block mt-2">
                          ${stats.assignedDiagnosis + stats.inProgressDiagnosis} Active
                        </span>
                      </a>
                    </div>
                    
                    <!-- GĐ5: Assign Repair -->
                    <div class="col-md-3">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/assign-repair"
                        class="btn btn-outline-warning w-100 p-3">
                        <i class="bi bi-tools fs-3 d-block mb-2"></i>
                        <strong>GĐ5: Assign Repair</strong>
                        <small class="text-muted d-block">REQUEST + DIAGNOSTIC</small>
                        <span class="badge bg-warning d-block mt-2">${stats.unassignedWorkOrderDetails} Waiting</span>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Task Management -->
          <div class="row mb-4">
            <div class="col-12">
              <div class="card">
                <div class="card-header bg-secondary text-white">
                  <h5 class="mb-0">
                    <i class="bi bi-list-task"></i>
                    Task Management
                  </h5>
                </div>
                <div class="card-body">
                  <div class="row g-3">
                    <div class="col-md-4">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/overdue-tasks"
                        class="btn btn-outline-danger w-100 p-3">
                        <i class="bi bi-exclamation-octagon fs-3 d-block mb-2 text-danger"></i>
                        <strong>Overdue Tasks</strong>
                        <span class="badge bg-danger d-block mt-2">${stats.overdueTasks} Tasks</span>
                      </a>
                    </div>
                    <div class="col-md-4">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/declined-tasks"
                        class="btn btn-outline-warning w-100 p-3">
                        <i class="bi bi-person-x fs-3 d-block mb-2 text-warning"></i>
                        <strong>Declined Tasks</strong>
                        <span class="badge bg-warning d-block mt-2">${stats.declinedTasks} Tasks</span>
                      </a>
                    </div>
                    <div class="col-md-4">
                      <a
                        href="${pageContext.request.contextPath}/techmanager/reassign-tasks"
                        class="btn btn-outline-info w-100 p-3">
                        <i class="bi bi-arrow-repeat fs-3 d-block mb-2 text-info"></i>
                        <strong>Reassign Tasks</strong>
                        <span class="badge bg-info d-block mt-2">${stats.tasksNeedReassignment} Tasks</span>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- /.container-fluid -->

        <!-- Footer -->
        <jsp:include page="footer-techmanager.jsp" />
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
