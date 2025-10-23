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
    <%@ include file="header.jsp" %>

    <div class="container-fluid mt-3">
      <!-- Alert Messages -->
      <c:if test="${not empty message}">
        <c:choose>
          <c:when test="${messageType == 'success'}">
            <div class="alert alert-success alert-dismissible fade show">
              <i class="bi bi-check-circle"></i>
              ${message}
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="alert"
              ></button>
            </div>
          </c:when>
          <c:otherwise>
            <div class="alert alert-danger alert-dismissible fade show">
              <i class="bi bi-exclamation-triangle"></i>
              ${message}
              <button
                type="button"
                class="btn-close"
                data-bs-dismiss="alert"
              ></button>
            </div>
          </c:otherwise>
        </c:choose>
      </c:if>

      <!-- Action Menu -->
      <div class="row mb-3">
        <div class="col-md-8">
          <h3><i class="bi bi-search"></i> Tìm kiếm Users</h3>
        </div>
        <div class="col-md-4 text-end">
          <div class="btn-group">
            <a
              href="${pageContext.request.contextPath}/admin/users/choose-type"
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
                  href="${pageContext.request.contextPath}/Home"
                >
                  <i class="bi bi-house"></i> Home
                </a>
              </li>
              <li>
                <a
                  class="dropdown-item"
                  href="${pageContext.request.contextPath}/admin/rbac/roles"
                >
                  <i class="bi bi-shield"></i> Manage Roles
                </a>
              </li>
              <li>
                <a
                  class="dropdown-item"
                  href="${pageContext.request.contextPath}/techmanager/dashboard"
                >
                  <i class="bi bi-tools"></i> TechManager Dashboard
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Quick Stats -->
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body text-center">
              <h4>${totalResults}</h4>
              <small>Total Users</small>
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
                  href="${pageContext.request.contextPath}/admin/users/choose-type"
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
                            <c:otherwise>
                              <span class="text-muted">No email</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <span class="badge ${user.roleBadgeClass}"
                            >${user.roleName}</span
                          >
                        </td>
                        <td>
                          <c:choose>
                            <c:when test="${user.activeStatus}">
                              <span class="badge bg-success">Active</span>
                            </c:when>
                            <c:otherwise>
                              <span class="badge bg-danger">Inactive</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <div class="btn-group btn-group-sm">
                            <a
                              href="${pageContext.request.contextPath}/admin/users/view/${user.userId}"
                              class="btn btn-outline-info"
                              title="View Details"
                            >
                              <i class="bi bi-eye"></i>
                            </a>
                            <a
                              href="${pageContext.request.contextPath}/admin/users/edit/${user.userId}"
                              class="btn btn-outline-primary"
                              title="Edit User"
                            >
                              <i class="bi bi-pencil"></i>
                            </a>
                              <c:if test="${user.userName != currentUser}">
                                  <form action="${pageContext.request.contextPath}/admin/users/toggle/${user.userId}" method="POST" style="display: inline-block;">
                                      <c:choose>
                                          <c:when test="${user.activeStatus}">
                                              <button type="submit" class="btn btn-outline-danger btn-sm" title="Disable User" onclick="return confirm('Are you sure you want to disable this user?')">
                                                  <i class="bi bi-lock"></i>
                                              </button>
                                          </c:when>
                                          <c:otherwise>
                                              <button type="submit" class="btn btn-outline-success btn-sm" title="Enable User" onclick="return confirm('Are you sure you want to enable this user?')">
                                                  <i class="bi bi-unlock"></i>
                                              </button>
                                          </c:otherwise>
                                      </c:choose>
                                  </form>
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

      <!-- Pagination -->
        <div class="card mt-3">
          <div class="card-body">
            <div class="row align-items-center">
              <div class="col-md-6">
                <nav aria-label="User pagination">
                  <ul class="pagination mb-0">
                    <!-- Previous Button -->
                    <c:choose>
                      <c:when test="${hasPrevPage}">
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="?page=${currentPage - 1}&size=${itemsPerPage}&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >
                            Previous
                          </a>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li class="page-item disabled">
                          <span class="page-link">Previous</span>
                        </li>
                      </c:otherwise>
                    </c:choose>

                    <!-- Page Numbers -->
                    <c:set
                      var="startPage"
                      value="${currentPage - 2 > 0 ? currentPage - 2 : 1}"
                    />
                    <c:set
                      var="endPage"
                      value="${startPage + 4 <= totalPages ? startPage + 4 : totalPages}"
                    />

                    <c:if test="${startPage > 1}">
                      <li class="page-item">
                        <a
                          class="page-link"
                          href="?page=1&size=${itemsPerPage}&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >1</a
                        >
                      </li>
                      <c:if test="${startPage > 2}">
                        <li class="page-item disabled">
                          <span class="page-link">...</span>
                        </li>
                      </c:if>
                    </c:if>

                    <c:forEach var="page" begin="${startPage}" end="${endPage}">
                      <c:choose>
                        <c:when test="${page == currentPage}">
                          <li class="page-item active">
                            <span class="page-link">${page}</span>
                          </li>
                        </c:when>
                        <c:otherwise>
                          <li class="page-item">
                            <a
                              class="page-link"
                              href="?page=${page}&size=${itemsPerPage}&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                              >${page}</a
                            >
                          </li>
                        </c:otherwise>
                      </c:choose>
                    </c:forEach>

                    <c:if test="${endPage < totalPages}">
                      <c:if test="${endPage < totalPages - 1}">
                        <li class="page-item disabled">
                          <span class="page-link">...</span>
                        </li>
                      </c:if>
                      <li class="page-item">
                        <a
                          class="page-link"
                          href="?page=${totalPages}&size=${itemsPerPage}&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >${totalPages}</a
                        >
                      </li>
                    </c:if>

                    <!-- Next Button -->
                    <c:choose>
                      <c:when test="${hasNextPage}">
                        <li class="page-item">
                          <a
                            class="page-link"
                            href="?page=${currentPage + 1}&size=${itemsPerPage}&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >
                            Next
                          </a>
                        </li>
                      </c:when>
                      <c:otherwise>
                        <li class="page-item disabled">
                          <span class="page-link">Next</span>
                        </li>
                      </c:otherwise>
                    </c:choose>
                  </ul>
                </nav>
              </div>

              <div class="col-md-6 text-end">
                <div class="d-flex justify-content-end align-items-center">
                  <span class="me-3 text-muted">
                    Page ${currentPage} of ${totalPages} (${totalResults} total
                    users)
                  </span>

                  <!-- Items per page selector -->
                  <div class="btn-group">
                    <button
                      class="btn btn-outline-secondary btn-sm dropdown-toggle"
                      data-bs-toggle="dropdown"
                    >
                      ${itemsPerPage} per page
                    </button>
                    <ul class="dropdown-menu">
                      <li>
                        <a
                          class="dropdown-item"
                          href="?page=1&size=5&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >5 per page</a
                        >
                      </li>
                      <li>
                        <a
                          class="dropdown-item"
                          href="?page=1&size=10&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >10 per page</a
                        >
                      </li>
                      <li>
                        <a
                          class="dropdown-item"
                          href="?page=1&size=20&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >20 per page</a
                        >
                      </li>
                      <li>
                        <a
                          class="dropdown-item"
                          href="?page=1&size=50&keyword=${searchKeyword}&role=${selectedRole}&status=${selectedStatus}&sort=${currentSort}"
                          >50 per page</a
                        >
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

    <!-- Footer -->
    <%@ include file="footer.jsp" %>
  </body>
</html>
