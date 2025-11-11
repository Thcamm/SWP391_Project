<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>My Team - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
  </head>
  <body>
    <div class="main-container">
      <!-- Sidebar -->
      <c:set var="activeMenu" value="my-team" scope="request" />
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
                <i class="bi bi-people-fill text-primary"></i>
                My Team
              </h2>
              <p class="text-muted mb-0">UC-TM-11: View your technician team members (Active & Inactive)</p>
            </div>
            <div>
              <span class="badge bg-success me-2">
                <i class="bi bi-check-circle"></i>
                ${activeTechnicians} Active
              </span>
              <span class="badge bg-secondary">
                <i class="bi bi-x-circle"></i>
                ${inactiveTechnicians} Inactive
              </span>
            </div>
          </div>
        </div>

        <!-- Error Message -->
        <c:if test="${not empty errorMessage}">
          <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>

        <!-- Team Statistics Cards -->
        <div class="row mb-4">
          <div class="col-md-4">
            <div class="card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Total Technicians</p>
                    <h3 class="mb-0">${totalTechnicians}</h3>
                  </div>
                  <div class="stats-icon bg-primary bg-opacity-10 text-primary">
                    <i class="bi bi-people"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Active Members</p>
                    <h3 class="mb-0 text-success">${activeTechnicians}</h3>
                  </div>
                  <div class="stats-icon bg-success bg-opacity-10 text-success">
                    <i class="bi bi-person-check"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <p class="text-muted mb-1 small">Inactive Members</p>
                    <h3 class="mb-0 text-secondary">${inactiveTechnicians}</h3>
                  </div>
                  <div class="stats-icon bg-secondary bg-opacity-10 text-secondary">
                    <i class="bi bi-person-x"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Technicians Table -->
        <div class="card">
          <div class="card-header bg-primary text-white">
            <h5 class="mb-0">
              <i class="bi bi-list-ul"></i>
              Team Member List
            </h5>
          </div>
          <div class="card-body">
            <c:choose>
              <c:when test="${empty technicians}">
                <div class="alert alert-info mb-0">
                  <i class="bi bi-info-circle"></i>
                  No technicians are currently assigned to your team.
                </div>
              </c:when>
              <c:otherwise>
                <div class="table-responsive">
                  <table class="table table-hover align-middle">
                    <thead class="table-light">
                      <tr>
                        <th>#</th>
                        <th>Employee Code</th>
                        <th>Full Name</th>
                        <th>Phone Number</th>
                        <th>Email</th>
                        <th class="text-center">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach items="${technicians}" var="tech" varStatus="status">
                        <tr class="${tech.active ? '' : 'table-secondary'}">
                          <td>${status.index + 1}</td>
                          <td>
                            <strong>${tech.employeeCode}</strong>
                          </td>
                          <td>
                            <div class="d-flex align-items-center">
                              <i
                                class="bi bi-person-circle me-2 fs-4 ${tech.active ? 'text-primary' : 'text-secondary'}"></i>
                              <div>
                                <strong>${tech.fullName}</strong>
                                <c:if test="${!tech.active}">
                                  <small class="text-muted d-block">(No longer active)</small>
                                </c:if>
                              </div>
                            </div>
                          </td>
                          <td>
                            <i class="bi bi-telephone text-muted"></i>
                            ${tech.phoneNumber}
                          </td>
                          <td>
                            <i class="bi bi-envelope text-muted"></i>
                            <a href="mailto:${tech.email}">${tech.email}</a>
                          </td>
                          <td class="text-center">
                            <span class="badge ${tech.statusBadgeClass}">
                              <i class="bi ${tech.active ? 'bi-check-circle' : 'bi-x-circle'}"></i>
                              ${tech.statusLabel}
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

        <!-- Info Box -->
        <div class="card mt-4 border-info">
          <div class="card-body">
            <h6 class="text-info">
              <i class="bi bi-info-circle-fill"></i>
              About This Page
            </h6>
            <ul class="mb-0 small text-muted">
              <li>
                <strong>Active</strong>
                technicians can be assigned new tasks.
              </li>
              <li>
                <strong>Inactive</strong>
                technicians cannot receive new assignments but may have ongoing tasks.
              </li>
              <li>
                This page shows all technicians where
                <code>Employee.ManagedBy = Your EmployeeID</code>
                .
              </li>
              <li>To change a technician's status, contact the system administrator.</li>
            </ul>
          </div>
        </div>

        <!-- Footer -->
        <jsp:include page="footer-techmanager.jsp" />
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
