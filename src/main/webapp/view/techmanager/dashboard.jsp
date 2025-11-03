<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Dashboard - Tech Manager</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />
    <style>
      .stats-card {
        border-radius: 12px;
        border: 1px solid #e5e7eb;
        transition: all 0.2s ease;
      }
      .stats-card:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        transform: translateY(-2px);
      }
      .stats-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.5rem;
      }
      .phase-section {
        background: white;
        border-radius: 12px;
        padding: 1.5rem;
        margin-bottom: 1.5rem;
        border: 1px solid #e5e7eb;
      }
      .phase-header {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        margin-bottom: 1.25rem;
      }
      .phase-badge {
        padding: 0.25rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.75rem;
        font-weight: 600;
      }
      .badge-active {
        background: #dcfce7;
        color: #16a34a;
      }
      .badge-soon {
        background: #f3f4f6;
        color: #6b7280;
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
              <p class="text-muted mb-0">Overview of all service operations</p>
            </div>
            <button
              type="button"
              class="btn btn-outline-secondary"
              onclick="window.location.reload();"
            >
              <i class="bi bi-arrow-clockwise"></i> Refresh
            </button>
          </div>
        </div>

        <!-- Quick Stats -->
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

        <!-- Phase 1: Reception & Diagnosis -->
        <div class="phase-section">
          <div class="phase-header">
            <i
              class="bi bi-1-circle-fill text-success"
              style="font-size: 1.75rem"
            ></i>
            <div>
              <h5 class="mb-0">Phase 1: Reception & Diagnosis</h5>
              <small class="text-muted"
                >Accept requests and assign diagnosis tasks</small
              >
            </div>
            <span class="badge-active phase-badge ms-auto">Active</span>
          </div>
          <div class="row">
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-primary text-white">
                      <i class="bi bi-clipboard-check"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">
                        Pending Service Requests
                      </p>
                      <h4 class="mb-0">${stats.pendingRequests}</h4>
                    </div>
                    <a
                      href="${pageContext.request.contextPath}/techmanager/service-requests"
                      class="btn btn-sm btn-primary"
                    >
                      <i class="bi bi-arrow-right"></i>
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-info text-white">
                      <i class="bi bi-person-plus"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">
                        Assigned Diagnosis Tasks
                      </p>
                      <h4 class="mb-0">${stats.assignedDiagnosis}</h4>
                    </div>
                    <a
                      href="${pageContext.request.contextPath}/techmanager/assign-diagnosis"
                      class="btn btn-sm btn-info"
                    >
                      <i class="bi bi-arrow-right"></i>
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Phase 2: Review & Quote -->
        <div class="phase-section">
          <div class="phase-header">
            <i
              class="bi bi-2-circle-fill text-primary"
              style="font-size: 1.75rem"
            ></i>
            <div>
              <h5 class="mb-0">Phase 2: Review & Quote</h5>
              <small class="text-muted"
                >Review diagnosis and approve quotes</small
              >
            </div>
            <span class="badge-active phase-badge ms-auto">Active</span>
          </div>
          <div class="row">
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-success text-white">
                      <i class="bi bi-file-earmark-text"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">Completed Diagnosis</p>
                      <h4 class="mb-0">${stats.completedDiagnosis}</h4>
                    </div>
                    <a
                      href="${pageContext.request.contextPath}/techmanager/diagnosis-review"
                      class="btn btn-sm btn-success"
                    >
                      <i class="bi bi-arrow-right"></i>
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-warning text-white">
                      <i class="bi bi-hourglass-split"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">
                        Pending Customer Approval
                      </p>
                      <h4 class="mb-0">${stats.pendingApproval}</h4>
                    </div>
                    <span class="badge bg-warning">Waiting</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Phase 3: Repair Assignment -->
        <div class="phase-section">
          <div class="phase-header">
            <i
              class="bi bi-3-circle-fill text-info"
              style="font-size: 1.75rem"
            ></i>
            <div>
              <h5 class="mb-0">Phase 3: Repair Assignment</h5>
              <small class="text-muted"
                >Assign approved repairs to technicians</small
              >
            </div>
            <span class="badge-active phase-badge ms-auto">Active</span>
          </div>
          <div class="row">
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-primary text-white">
                      <i class="bi bi-check-circle"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">Approved Quotes</p>
                      <h4 class="mb-0">${stats.approvedQuotes}</h4>
                    </div>
                    <a
                      href="${pageContext.request.contextPath}/techmanager/assign-repair"
                      class="btn btn-sm btn-primary"
                    >
                      <i class="bi bi-arrow-right"></i>
                    </a>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-warning text-white">
                      <i class="bi bi-tools"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">Active Repair Tasks</p>
                      <h4 class="mb-0">${stats.activeRepairs}</h4>
                    </div>
                    <span class="badge bg-warning">In Progress</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Phase 4: Monitor & Complete -->
        <div class="phase-section">
          <div class="phase-header">
            <i
              class="bi bi-4-circle text-secondary"
              style="font-size: 1.75rem"
            ></i>
            <div>
              <h5 class="mb-0">Phase 4: Monitor & Complete</h5>
              <small class="text-muted"
                >Monitor progress and complete work orders</small
              >
            </div>
            <span class="badge-soon phase-badge ms-auto">Soon</span>
          </div>
          <div class="row">
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-success text-white">
                      <i class="bi bi-check-circle"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">Completed Repairs</p>
                      <h4 class="mb-0">${stats.completedRepairs}</h4>
                    </div>
                    <button class="btn btn-sm btn-secondary" disabled>
                      Soon
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="card border-0 bg-light">
                <div class="card-body">
                  <div class="d-flex align-items-center gap-3">
                    <div class="stats-icon bg-info text-white">
                      <i class="bi bi-list-task"></i>
                    </div>
                    <div class="flex-grow-1">
                      <p class="mb-0 text-muted small">Ready for Completion</p>
                      <h4 class="mb-0">0</h4>
                    </div>
                    <button class="btn btn-sm btn-secondary" disabled>
                      Soon
                    </button>
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
