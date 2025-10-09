<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Quản lý Users - Admin</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <!-- Header -->
    <nav class="navbar navbar-dark bg-dark">
      <div class="container-fluid">
        <span class="navbar-brand"
          ><i class="bi bi-people"></i> User Management</span
        >
        <span class="navbar-text">
          <i class="bi bi-person-circle"></i> ${currentUser} |
          <fmt:formatDate
            value="<%= new java.util.Date() %>"
            pattern="dd/MM/yyyy HH:mm"
          />
        </span>
      </div>
    </nav>

    <div class="container-fluid mt-3">
      <!-- Alert Messages -->
      <c:if test="${not empty message}">
        <div
          class="alert alert-${messageType == 'success' ? 'success' : 'danger'} alert-dismissible fade show"
        >
          <i
            class="bi bi-${messageType == 'success' ? 'check-circle' : 'exclamation-triangle'}"
          ></i>
          ${message}
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="alert"
          ></button>
        </div>
      </c:if>

      <!-- Action Menu -->
      <div class="row mb-3">
        <div class="col-md-8">
          <h3><i class="bi bi-search"></i> Tìm kiếm Users</h3>
        </div>
        <div class="col-md-4 text-end">
          <div class="btn-group">
            <a
              href="${pageContext.request.contextPath}/admin/users/create"
              class="btn btn-success"
            >
              <i class="bi bi-plus-circle"></i> Create User
            </a>
            <button
              class="btn btn-outline-secondary dropdown-toggle"
              data-bs-toggle="dropdown"
            >
              <i class="bi bi-gear"></i> Actions
            </button>
            <ul class="dropdown-menu">
              <li>
                <a
                  class="dropdown-item"
                  href="${pageContext.request.contextPath}/admin/users/bulk-disable"
                >
                  <i class="bi bi-lock"></i> Bulk Disable
                </a>
              </li>
              <li>
                <a
                  class="dropdown-item"
                  href="${pageContext.request.contextPath}/admin/roles/assign"
                >
                  <i class="bi bi-shield"></i> Assign Roles
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>

<<<<<<< Updated upstream
    <!-- Create User Form (Collapse) -->
    <div class="collapse mb-4" id="createUserForm">
        <div class="card">
            <div class="card-header">
                <h5>Tạo User Mới</h5>
            </div>
            <div class="card-body">
                <form method="POST" action="${pageContext.request.contextPath}/admin/users">
                    <input type="hidden" name="action" value="createUser">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="fullName" class="form-label">Họ tên *</label>
                                <input type="text" class="form-control" id="fullName" name="fullName" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="userName" class="form-label">Username *</label>
                                <input type="text" class="form-control" id="userName" name="userName" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email *</label>
                                <input type="email" class="form-control" id="email" name="email" required>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="phoneNumber" class="form-label">Số điện thoại</label>
                                <input type="text" class="form-control" id="phoneNumber" name="phoneNumber">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="role" class="form-label">Vai trò *</label>
                                <select class="form-select" id="role" name="role" required>
                                    <option value="">Chọn vai trò</option>
                                    <option value="2">Manager</option>
                                    <option value="3">Employee</option>
                                    <option value="4">User</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label class="form-label">&nbsp;</label>
                                <div>
                                    <button type="submit" class="btn btn-success">
                                        <i class="bi bi-person-plus"></i> Tạo User
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
=======
      <!-- Quick Stats -->
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body text-center">
              <h4>${totalResults}</h4>
              <small>Total Users</small>
>>>>>>> Stashed changes
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-success text-white">
            <div class="card-body text-center">
              <h4>${activeUsersCount}</h4>
              <small>Active</small>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-warning text-white">
            <div class="card-body text-center">
              <h4>${inactiveUsersCount}</h4>
              <small>Inactive</small>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-danger text-white">
            <div class="card-body text-center">
              <h4>${adminUsersCount}</h4>
              <small>Admins</small>
            </div>
          </div>
        </div>
      </div>

      <!-- Search Form -->
      <div class="card mb-4">
        <div class="card-header">
          <h5><i class="bi bi-funnel"></i> Search & Filter</h5>
        </div>
        <div class="card-body">
          <form method="GET" class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Keyword</label>
              <input
                type="text"
                class="form-control"
                name="keyword"
                value="${searchKeyword}"
                placeholder="Username, email, name..."
              />
            </div>
            <div class="col-md-3">
              <label class="form-label">Role</label>
              <select class="form-select" name="role">
                <option value="">All roles</option>
                <c:forEach var="roleOption" items="${availableRoles}">
                  <c:choose>
                    <c:when test="${selectedRole == roleOption.roleId}">
                      <option value="${roleOption.roleId}" selected>
                        ${roleOption.roleName}
                      </option>
                    </c:when>
                    <c:otherwise>
                      <option value="${roleOption.roleId}">
                        ${roleOption.roleName}
                      </option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Status</label>
              <select class="form-select" name="status">
                <option value="">All status</option>
                <c:choose>
                  <c:when test="${selectedStatus == 'active'}">
                    <option value="active" selected>Active</option>
                  </c:when>
                  <c:otherwise>
                    <option value="active">Active</option>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${selectedStatus == 'inactive'}">
                    <option value="inactive" selected>Inactive</option>
                  </c:when>
                  <c:otherwise>
                    <option value="inactive">Inactive</option>
                  </c:otherwise>
                </c:choose>
              </select>
            </div>
            <div class="col-md-2">
              <label class="form-label">&nbsp;</label>
              <div class="d-grid">
                <button type="submit" class="btn btn-primary">
                  <i class="bi bi-search"></i> Search
                </button>
              </div>
            </div>
          </form>
        </div>
      </div>

      <!-- Quick Filters -->
      <div class="mb-3">
        <div class="btn-group">
          <c:choose>
            <c:when test="${selectedStatus == 'active'}">
              <a href="?status=active" class="btn btn-success btn-sm">
                <i class="bi bi-check-circle"></i> Active
              </a>
            </c:when>
            <c:otherwise>
              <a href="?status=active" class="btn btn-outline-success btn-sm">
                <i class="bi bi-check-circle"></i> Active
              </a>
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${selectedStatus == 'inactive'}">
              <a href="?status=inactive" class="btn btn-warning btn-sm">
                <i class="bi bi-x-circle"></i> Inactive
              </a>
            </c:when>
            <c:otherwise>
              <a href="?status=inactive" class="btn btn-outline-warning btn-sm">
                <i class="bi bi-x-circle"></i> Inactive
              </a>
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${selectedRole == 1}">
              <a href="?role=1" class="btn btn-danger btn-sm">
                <i class="bi bi-shield"></i> Admins
              </a>
            </c:when>
            <c:otherwise>
              <a href="?role=1" class="btn btn-outline-danger btn-sm">
                <i class="bi bi-shield"></i> Admins
              </a>
            </c:otherwise>
          </c:choose>
          <a href="?" class="btn btn-outline-secondary btn-sm">
            <i class="bi bi-arrow-clockwise"></i> Reset
          </a>
        </div>
      </div>

      <!-- Results Table -->
      <div class="card">
        <div class="card-header d-flex justify-content-between">
          <span>Search Results (${totalResults} users)</span>
          <small class="text-muted">
            <c:if test="${hasSearchCriteria}">
              Filtered results |
              <a href="?" class="text-decoration-none">Show all</a>
            </c:if>
          </small>
        </div>
        <div class="card-body p-0">
          <c:choose>
            <c:when test="${empty users}">
              <div class="text-center p-5">
                <i class="bi bi-search display-4 text-muted"></i>
                <h5 class="text-muted mt-3">No users found</h5>
                <p class="text-muted">Try adjusting your search criteria</p>
                <a
                  href="${pageContext.request.contextPath}/admin/users/create"
                  class="btn btn-success"
                >
                  <i class="bi bi-plus-circle"></i> Create First User
                </a>
              </div>
            </c:when>
            <c:otherwise>
              <div class="table-responsive">
                <table class="table table-hover mb-0">
                  <thead class="table-dark">
                    <tr>
                      <th>
                        <a
                          href="?sort=userid&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}"
                          class="text-white text-decoration-none"
                        >
                          ID
                          <c:if test="${currentSort == 'userid'}">
                            <i class="bi bi-arrow-up"></i>
                          </c:if>
                        </a>
                      </th>
                      <th>
                        <a
                          href="?sort=username&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}"
                          class="text-white text-decoration-none"
                        >
                          Username
                          <c:if test="${currentSort == 'username'}">
                            <i class="bi bi-arrow-up"></i>
                          </c:if>
                        </a>
                      </th>
                      <th>
                        <a
                          href="?sort=fullname&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}"
                          class="text-white text-decoration-none"
                        >
                          Full Name
                          <c:if test="${currentSort == 'fullname'}">
                            <i class="bi bi-arrow-up"></i>
                          </c:if>
                        </a>
                      </th>
                      <th>
                        <a
                          href="?sort=email&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}"
                          class="text-white text-decoration-none"
                        >
                          Email
                          <c:if test="${currentSort == 'email'}">
                            <i class="bi bi-arrow-up"></i>
                          </c:if>
                        </a>
                      </th>
                      <th>
                        <a
                          href="?sort=rolename&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}"
                          class="text-white text-decoration-none"
                        >
                          Role
                          <c:if test="${currentSort == 'rolename'}">
                            <i class="bi bi-arrow-up"></i>
                          </c:if>
                        </a>
                      </th>
                      <th>
                        <a
                          href="?sort=status&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}"
                          class="text-white text-decoration-none"
                        >
                          Status
                          <c:if test="${currentSort == 'status'}">
                            <i class="bi bi-arrow-up"></i>
                          </c:if>
                        </a>
                      </th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="user" items="${users}">
                      <tr>
                        <td>
                          <span class="badge bg-light text-dark"
                            >${user.userId}</span
                          >
                        </td>
                        <td>
                          <strong>${user.userName}</strong>
                          <c:if test="${user.userName == currentUser}">
                            <span class="badge bg-warning text-dark ms-1"
                              >You</span
                            >
                          </c:if>
                        </td>
                        <td>${user.fullName}</td>
                        <td>
                          <c:choose>
                            <c:when test="${not empty user.email}">
                              <a
                                href="mailto:${user.email}"
                                class="text-decoration-none"
                                >${user.email}</a
                              >
                            </c:when>
                            <c:otherwise
                              ><span class="text-muted"
                                >No email</span
                              ></c:otherwise
                            >
                          </c:choose>
                        </td>
                        <td>
                          <span class="badge ${user.roleBadgeClass}"
                            >${user.roleName}</span
                          >
                        </td>
                        <td>
                          <span
                            class="badge ${user.activeStatus ? 'bg-success' : 'bg-danger'}"
                          >
                            ${user.activeStatus ? 'Active' : 'Inactive'}
                          </span>
                        </td>
                        <td>
                          <div class="btn-group btn-group-sm">
                            <a
                              href="${pageContext.request.contextPath}/admin/users/view/${user.userId}"
                              class="btn btn-outline-info"
                              title="View"
                            >
                              <i class="bi bi-eye"></i>
                            </a>
                            <a
                              href="${pageContext.request.contextPath}/admin/users/edit/${user.userId}"
                              class="btn btn-outline-primary"
                              title="Edit"
                            >
                              <i class="bi bi-pencil"></i>
                            </a>
                            <a
                              href="${pageContext.request.contextPath}/admin/users/roles/${user.userId}"
                              class="btn btn-outline-warning"
                              title="Manage Roles"
                            >
                              <i class="bi bi-shield"></i>
                            </a>
                            <c:if test="${user.userName != currentUser}">
                              <a
                                href="${pageContext.request.contextPath}/admin/users/disable/${user.userId}"
                                class="btn ${user.activeStatus ? 'btn-outline-danger' : 'btn-outline-success'}"
                                title="${user.activeStatus ? 'Disable' : 'Enable'}"
                                onclick="return confirm('Are you sure?')"
                              >
                                <i
                                  class="bi ${user.activeStatus ? 'bi-lock' : 'bi-unlock'}"
                                ></i>
                              </a>
                            </c:if>
                          </div>
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

      <!-- Footer Info -->
      <div class="row mt-4">
        <div class="col-12">
          <div class="alert alert-light">
            <small class="text-muted">
              <i class="bi bi-info-circle"></i>
              <strong>Quick Actions:</strong>
              <a
                href="${pageContext.request.contextPath}/admin/users/create"
                class="text-decoration-none"
                >Create User</a
              >
              |
              <a
                href="${pageContext.request.contextPath}/admin/users/bulk-disable"
                class="text-decoration-none"
                >Bulk Disable</a
              >
              |
              <a
                href="${pageContext.request.contextPath}/admin/roles/"
                class="text-decoration-none"
                >Manage Roles</a
              >
              |
              <a
                href="${pageContext.request.contextPath}/admin/reports"
                class="text-decoration-none"
                >View Reports</a
              >
            </small>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
