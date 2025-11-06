<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Choose User Type - Admin</title>
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

    <div class="layout">
      <jsp:include page="sidebar.jsp" />
      <main class="main">
        <div class="container mt-4">
          <!-- Breadcrumb -->
          <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
              <li class="breadcrumb-item">
                <a
                  href="${pageContext.request.contextPath}/admin/users"
                  class="text-decoration-none"
                >
                  User Management
                </a>
              </li>
              <li class="breadcrumb-item active">Choose User Type</li>
            </ol>
          </nav>

          <!-- Alert Messages -->
          <c:if test="${not empty message}">
            <c:choose>
              <c:when test="${messageType == 'success'}">
                <div class="alert alert-success alert-dismissible fade show">
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

          <div class="row justify-content-center">
            <div class="col-lg-10">
              <div class="text-center mb-4">
                <h2 class="text-primary">Choose User Type</h2>
                <p class="text-muted">
                  Please select the appropriate user type to create a new
                  account in the system.
                </p>
              </div>

              <div class="row g-4">
                <!-- Customer User Card -->
                <div class="col-md-6">
                  <div class="card h-100 shadow-sm border-primary">
                    <div class="card-body text-center p-4">
                      <div class="mb-4">
                        <i
                          class="bi bi-person-circle text-primary"
                          style="font-size: 4rem"
                        ></i>
                      </div>
                      <h3 class="card-title text-primary">Customer User</h3>
                      <p class="card-text text-muted mb-4">
                        Create an account for customers using garage services
                      </p>

                      <div class="mb-4">
                        <h6 class="text-success">Includes:</h6>
                        <ul class="list-unstyled text-start">
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Personal Information
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Login Account
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Customer profile
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Loyalty points
                          </li>
                        </ul>
                      </div>

                      <a
                        href="${pageContext.request.contextPath}/admin/users/create-customer"
                        class="btn btn-primary btn-lg w-100"
                      >
                        <i class="bi bi-plus-circle"></i> Create Customer
                      </a>
                    </div>
                  </div>
                </div>

                <!-- Employee User Card -->
                <div class="col-md-6">
                  <div class="card h-100 shadow-sm border-success">
                    <div class="card-body text-center p-4">
                      <div class="mb-4">
                        <i
                          class="bi bi-briefcase-fill text-success"
                          style="font-size: 4rem"
                        ></i>
                      </div>
                      <h3 class="card-title text-success">Employee User</h3>
                      <p class="card-text text-muted mb-4">
                        Create an account for employees working in the garage
                      </p>

                      <div class="mb-4">
                        <h6 class="text-success">Includes:</h6>
                        <ul class="list-unstyled text-start">
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Personal Information
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Login Account
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Employee profile
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Employee ID
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Salary Information
                          </li>
                          <li>
                            <i class="bi bi-check-circle text-success"></i>
                            Management Level
                          </li>
                        </ul>
                      </div>

                      <a
                        href="${pageContext.request.contextPath}/admin/users/create-employee"
                        class="btn btn-success btn-lg w-100"
                      >
                        <i class="bi bi-plus-circle"></i> Create Employee
                      </a>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Info Section -->
              <div class="row mt-5">
                <div class="col-12">
                  <div class="card bg-light">
                    <div class="card-body">
                      <h5 class="card-title text-info">
                        <i class="bi bi-info-circle"></i> User Type Details
                      </h5>
                      <div class="row">
                        <div class="col-md-6">
                          <h6 class="text-primary">Customer User:</h6>
                          <ul class="text-muted">
                            <li>Only access to service</li>
                            <li>Cannot access management system</li>
                            <li>Automatically has Customer profile</li>
                          </ul>
                        </div>
                        <div class="col-md-6">
                          <h6 class="text-success">Employee User:</h6>
                          <ul class="text-muted">
                            <li>Has access to management system</li>
                            <li>Automatically has Employee profile</li>
                            <li>Can be assigned roles</li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Back Button -->
              <div class="text-center mt-4">
                <a
                  href="${pageContext.request.contextPath}/admin/users"
                  class="btn btn-secondary"
                >
                  <i class="bi bi-arrow-left"></i> Back to User Management
                </a>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Footer -->
    <%@ include file="footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
