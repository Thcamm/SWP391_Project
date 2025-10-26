<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Chi tiết User - ${user.fullName}</title>
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
            href="${pageContext.request.contextPath}/admin/users"
            class="btn btn-outline-secondary"
          >
            <i class="bi bi-arrow-left"></i> Back to User List
          </a>
        </div>
      </div>

      <!-- User Details Card -->
      <div class="row">
        <div class="col-lg-8">
          <div class="card">
            <div class="card-header">
              <h4 class="mb-0">
                <i class="bi bi-person"></i> User Information
              </h4>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="fw-bold text-muted">User ID:</label>
                    <div>
                      <span class="badge bg-light text-dark fs-6"
                        >${user.userId}</span
                      >
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="fw-bold text-muted">Full Name:</label>
                    <div class="h5">${user.fullName}</div>
                  </div>
                  <div class="mb-3">
                    <label class="fw-bold text-muted">Username:</label>
                    <div class="h6">
                      <strong>${user.userName}</strong>
                      <c:if test="${user.userName == currentUser}">
                        <span class="badge bg-warning text-dark ms-2"
                          >Current User</span
                        >
                      </c:if>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="mb-3">
                    <label class="fw-bold text-muted">Email:</label>
                    <div>
                      <c:choose>
                        <c:when test="${not empty user.email}">
                          <a
                            href="mailto:${user.email}"
                            class="text-decoration-none"
                          >
                            <i class="bi bi-envelope"></i> ${user.email}
                          </a>
                        </c:when>
                        <c:otherwise>
                          <span class="text-muted fst-italic"
                            >No email provided</span
                          >
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="fw-bold text-muted">Phone Number:</label>
                    <div>
                      <c:choose>
                        <c:when test="${not empty user.phoneNumber}">
                          <i class="bi bi-telephone"></i> ${user.phoneNumber}
                        </c:when>
                        <c:otherwise>
                          <span class="text-muted fst-italic"
                            >No phone number provided</span
                          >
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="fw-bold text-muted">Role:</label>
                    <div>
                      <span class="badge ${user.roleBadgeClass} fs-6"
                        >${user.roleName}</span
                      >
                    </div>
                  </div>
                  <div class="mb-3">
                    <label class="fw-bold text-muted">Status:</label>
                    <div>
                      <c:choose>
                        <c:when test="${user.activeStatus}">
                          <span class="badge bg-success fs-6">
                            <i class="bi bi-check-circle"></i> Active
                          </span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge bg-danger fs-6">
                            <i class="bi bi-x-circle"></i> Inactive
                          </span>
                        </c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Panel -->
        <div class="col-lg-4">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0"><i class="bi bi-gear"></i> Actions</h5>
            </div>
            <div class="card-body">
              <div class="d-grid gap-2">
                <a
                  href="${pageContext.request.contextPath}/admin/users/edit/${user.userId}"
                  class="btn btn-primary"
                >
                  <i class="bi bi-pencil"></i> Edit User
                </a>

                  <c:if test="${user.userName != currentUser}">
                      <%-- Form này POST đến UserToggleStatusServlet và giữ nguyên class "d-grid" --%>
                      <form action="${pageContext.request.contextPath}/admin/users/toggle/${user.userId}" method="POST" class="d-grid">
                          <c:choose>
                              <c:when test="${user.activeStatus}">
                                  <button type="submit" class="btn btn-outline-danger" onclick="return confirm('Are you sure you want to disable this user?')">
                                      <i class="bi bi-lock"></i> Disable User
                                  </button>
                              </c:when>
                              <c:otherwise>
                                  <button type="submit" class="btn btn-outline-success" onclick="return confirm('Are you sure you want to enable this user?')">
                                      <i class="bi bi-unlock"></i> Enable User
                                  </button>
                              </c:otherwise>
                          </c:choose>
                      </form>
                  </c:if>

                <hr />

                <a
                  href="${pageContext.request.contextPath}/admin/users"
                  class="btn btn-outline-secondary"
                >
                  <i class="bi bi-list"></i> All Users
                </a>

                <a
                  href="${pageContext.request.contextPath}/admin/users/choose-type"
                  class="btn btn-success"
                >
                  <i class="bi bi-plus-circle"></i> Create New User Type
                  <Type></Type>
                </a>
              </div>
            </div>
          </div>

          <!-- Additional Info -->
          <div class="card mt-3">
            <div class="card-header">
              <h6 class="mb-0">
                <i class="bi bi-info-circle"></i> Additional Information
              </h6>
            </div>
            <div class="card-body">
              <small class="text-muted">
                <div class="mb-2"><strong>User ID:</strong> ${user.userId}</div>
                <div class="mb-2"><strong>Role ID:</strong> ${user.roleId}</div>
                <c:if test="${not empty user.createdAt}">
                  <div class="mb-2">
                    <strong>Created:</strong> ${user.createdAt}
                  </div>
                </c:if>
                <c:if test="${not empty user.updatedAt}">
                  <div class="mb-2">
                    <strong>Updated:</strong> ${user.updatedAt}
                  </div>
                </c:if>
              </small>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <%@ include file="footer.jsp" %>
  </body>
</html>
