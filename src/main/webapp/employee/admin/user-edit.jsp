<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Edit User - ${user.fullName}</title>
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
        <span class="navbar-brand">
          <i class="bi bi-pencil-square"></i> Edit User
        </span>
        <span class="navbar-text">
          <i class="bi bi-person-circle"></i> ${currentUser} |
          <fmt:formatDate
            value="<%= new java.util.Date() %>"
            pattern="dd/MM/yyyy HH:mm"
          />
        </span>
      </div>
    </nav>

    <div class="container-fluid mt-4">
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

      <!-- Back Button -->
      <div class="row mb-3">
        <div class="col-12">
          <a
            href="${pageContext.request.contextPath}/employee/admin/users/view/${user.userId}"
            class="btn btn-outline-info"
          >
            <i class="bi bi-arrow-left"></i> Back to User Details
          </a>
          <a
            href="${pageContext.request.contextPath}/employee/admin/users"
            class="btn btn-outline-secondary ms-2"
          >
            <i class="bi bi-list"></i> User List
          </a>
        </div>
      </div>

      <!-- Edit Form -->
      <div class="row">
        <div class="col-lg-8">
          <div class="card">
            <div class="card-header">
              <h4 class="mb-0">
                <i class="bi bi-pencil"></i> Edit User Information
              </h4>
            </div>
            <div class="card-body">
              <form
                method="POST"
                action="${pageContext.request.contextPath}/employee/admin/users/edit/${user.userId}"
              >
                <div class="row">
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="fullName" class="form-label">
                        <i class="bi bi-person"></i> Full Name *
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="fullName"
                        name="fullName"
                        value="${user.fullName}"
                        required
                      />
                      <div class="form-text">Enter the user's full name</div>
                    </div>

                    <div class="mb-3">
                      <label for="userName" class="form-label">
                        <i class="bi bi-at"></i> Username *
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="userName"
                        name="userName"
                        value="${user.userName}"
                        required
                        pattern="[a-zA-Z0-9_]{3,20}"
                        title="Username must be 3-20 characters, letters, numbers and underscore only"
                      />
                      <div class="form-text">
                        3-20 characters, letters, numbers and underscore only
                      </div>
                    </div>

                    <div class="mb-3">
                      <label for="email" class="form-label">
                        <i class="bi bi-envelope"></i> Email
                      </label>
                      <input
                        type="email"
                        class="form-control"
                        id="email"
                        name="email"
                        value="${user.email}"
                      />
                      <div class="form-text">
                        Valid email address (optional)
                      </div>
                    </div>
                  </div>

                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="phoneNumber" class="form-label">
                        <i class="bi bi-telephone"></i> Phone Number
                      </label>
                      <input
                        type="tel"
                        class="form-control"
                        id="phoneNumber"
                        name="phoneNumber"
                        value="${user.phoneNumber}"
                      />
                      <div class="form-text">Phone number (optional)</div>
                    </div>

                    <div class="mb-3">
                      <label for="roleId" class="form-label">
                        <i class="bi bi-shield"></i> Role *
                      </label>
                      <select
                        class="form-select"
                        id="roleId"
                        name="roleId"
                        required
                      >
                        <c:forEach var="role" items="${availableRoles}">
                          <c:choose>
                            <c:when test="${role.roleId == user.roleId}">
                              <option value="${role.roleId}" selected>
                                ${role.roleName}
                              </option>
                            </c:when>
                            <c:otherwise>
                              <option value="${role.roleId}">
                                ${role.roleName}
                              </option>
                            </c:otherwise>
                          </c:choose>
                        </c:forEach>
                      </select>
                      <div class="form-text">Select user role</div>
                    </div>

                    <div class="mb-3">
                      <label for="activeStatus" class="form-label">
                        <i class="bi bi-toggle-on"></i> Status
                      </label>
                      <select
                        class="form-select"
                        id="activeStatus"
                        name="activeStatus"
                      >
                        <c:choose>
                          <c:when test="${user.activeStatus}">
                            <option value="true" selected>Active</option>
                            <option value="false">Inactive</option>
                          </c:when>
                          <c:otherwise>
                            <option value="true">Active</option>
                            <option value="false" selected>Inactive</option>
                          </c:otherwise>
                        </c:choose>
                      </select>
                      <div class="form-text">User account status</div>
                    </div>
                  </div>
                </div>

                <!-- Submit Buttons -->
                <div class="row mt-4">
                  <div class="col-12">
                    <hr />
                    <div class="d-flex justify-content-between">
                      <div>
                        <button type="submit" class="btn btn-primary btn-lg">
                          <i class="bi bi-check-circle"></i> Save Changes
                        </button>
                        <button
                          type="reset"
                          class="btn btn-outline-warning ms-2"
                        >
                          <i class="bi bi-arrow-clockwise"></i> Reset Form
                        </button>
                      </div>
                      <div>
                        <a
                          href="${pageContext.request.contextPath}/employee/admin/users/view/${user.userId}"
                          class="btn btn-outline-secondary"
                        >
                          <i class="bi bi-x-circle"></i> Cancel
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>

        <!-- Info Panel -->
        <div class="col-lg-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                <i class="bi bi-info-circle"></i> Current User Info
              </h5>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <strong>User ID:</strong>
                <span class="badge bg-light text-dark">${user.userId}</span>
              </div>
              <div class="mb-3">
                <strong>Current Role:</strong>
                <span class="badge ${user.roleBadgeClass}"
                  >${user.roleName}</span
                >
              </div>
              <div class="mb-3">
                <strong>Current Status:</strong>
                <c:choose>
                  <c:when test="${user.activeStatus}">
                    <span class="badge bg-success">Active</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge bg-danger">Inactive</span>
                  </c:otherwise>
                </c:choose>
              </div>
              <c:if test="${user.userName == currentUser}">
                <div class="alert alert-warning">
                  <i class="bi bi-exclamation-triangle"></i>
                  <strong>Warning:</strong> You are editing your own account. Be
                  careful not to lock yourself out!
                </div>
              </c:if>
            </div>
          </div>

          <!-- Quick Actions -->
          <div class="card mt-3">
            <div class="card-header">
              <h6 class="mb-0">
                <i class="bi bi-lightning"></i> Quick Actions
              </h6>
            </div>
            <div class="card-body">
              <div class="d-grid gap-2">
                <a
                  href="${pageContext.request.contextPath}/employee/admin/users/view/${user.userId}"
                  class="btn btn-outline-info btn-sm"
                >
                  <i class="bi bi-eye"></i> View Details
                </a>
                <a
                  href="${pageContext.request.contextPath}/employee/admin/users"
                  class="btn btn-outline-secondary btn-sm"
                >
                  <i class="bi bi-list"></i> All Users
                </a>
                <a
                  href="${pageContext.request.contextPath}/employee/admin/users/create"
                  class="btn btn-outline-success btn-sm"
                >
                  <i class="bi bi-plus-circle"></i> Create New User
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Form Validation -->
    <script>
      // Bootstrap form validation
      (function () {
        "use strict";
        window.addEventListener(
          "load",
          function () {
            var forms = document.getElementsByClassName("needs-validation");
            var validation = Array.prototype.filter.call(
              forms,
              function (form) {
                form.addEventListener(
                  "submit",
                  function (event) {
                    if (form.checkValidity() === false) {
                      event.preventDefault();
                      event.stopPropagation();
                    }
                    form.classList.add("was-validated");
                  },
                  false
                );
              }
            );
          },
          false
        );
      })();
    </script>
  </body>
</html>
