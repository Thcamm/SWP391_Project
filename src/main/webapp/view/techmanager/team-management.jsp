<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Team Management - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
  </head>
  <body>
    <div class="main-container">
      <!-- Sidebar -->
      <c:set var="activeMenu" value="team-management" scope="request" />
      <jsp:include page="sidebar-techmanager.jsp" />

      <!-- Main Content -->
      <div class="content-wrapper">
        <!-- Header -->
        <jsp:include page="header-techmanager.jsp" />

        <div class="container-fluid mt-3">
          <!-- Alert Messages -->
          <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger alert-dismissible fade show">
              <i class="bi bi-exclamation-triangle"></i>
              ${errorMessage}
              <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
          </c:if>

          <!-- Page Header -->
          <div class="row mb-3">
            <div class="col-md-12">
              <h3>
                <i class="bi bi-people-fill text-primary"></i>
                Team Management
              </h3>
              <p class="text-muted">View and manage all technicians in the system</p>
            </div>
          </div>

          <!-- Quick Stats -->
          <div class="row mb-4">
            <div class="col-md-4">
              <div class="card bg-primary text-white">
                <div class="card-body text-center">
                  <h4>${totalTechnicians}</h4>
                  <small>Total Technicians</small>
                </div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="card bg-success text-white">
                <div class="card-body text-center">
                  <h4>${activeTechnicians}</h4>
                  <small>Active</small>
                </div>
              </div>
            </div>
            <div class="col-md-4">
              <div class="card bg-warning text-white">
                <div class="card-body text-center">
                  <h4>${inactiveTechnicians}</h4>
                  <small>Inactive</small>
                </div>
              </div>
            </div>
          </div>

          <!-- Technicians Table -->
          <div class="card">
            <div class="card-header d-flex justify-content-between">
              <span>
                <i class="bi bi-list-ul"></i>
                All Technicians (${totalTechnicians} members)
              </span>
            </div>
            <div class="card-body p-0">
              <c:choose>
                <c:when test="${empty technicians}">
                  <div class="text-center p-5">
                    <i class="bi bi-people display-4 text-muted"></i>
                    <h5 class="text-muted mt-3">No technicians found</h5>
                    <p class="text-muted">No technicians are currently registered in the system</p>
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="table-responsive">
                    <table class="table table-hover mb-0">
                      <thead class="table-dark">
                        <tr>
                          <th>Employee Code</th>
                          <th>Full Name</th>
                          <th>Email</th>
                          <th>Phone Number</th>
                          <th>Status</th>
                        </tr>
                      </thead>
                      <tbody>
                        <c:forEach items="${technicians}" var="tech" varStatus="status">
                          <tr>
                            <td>
                              <strong>${tech.employeeCode}</strong>
                            </td>
                            <td>
                              <div class="d-flex align-items-center">
                                <i
                                  class="bi bi-person-circle me-2 fs-5 ${tech.active ? 'text-primary' : 'text-secondary'}"></i>
                                <div>
                                  <strong>${tech.fullName}</strong>
                                  <c:if test="${!tech.active}">
                                    <small class="text-muted d-block">(Inactive)</small>
                                  </c:if>
                                </div>
                              </div>
                            </td>
                            <td>
                              <c:choose>
                                <c:when test="${not empty tech.email}">
                                  <a href="mailto:${tech.email}" class="text-decoration-none">
                                    <i class="bi bi-envelope text-muted"></i>
                                    ${tech.email}
                                  </a>
                                </c:when>
                                <c:otherwise>
                                  <span class="text-muted">No email</span>
                                </c:otherwise>
                              </c:choose>
                            </td>
                            <td>
                              <i class="bi bi-telephone text-muted"></i>
                              ${tech.phoneNumber}
                            </td>
                            <td>
                              <c:choose>
                                <c:when test="${tech.active}">
                                  <span class="badge bg-success">
                                    <i class="bi bi-check-circle"></i>
                                    Active
                                  </span>
                                </c:when>
                                <c:otherwise>
                                  <span class="badge bg-danger">
                                    <i class="bi bi-x-circle"></i>
                                    Inactive
                                  </span>
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
        <!-- /.container-fluid -->

        <!-- Footer -->
        <jsp:include page="footer-techmanager.jsp" />
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
