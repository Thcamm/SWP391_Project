<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Create Employee - Admin</title>
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
      <jsp:include page="/view/admin/sidebar.jsp" />
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
              <li class="breadcrumb-item">
                <a
                  href="${pageContext.request.contextPath}/admin/users/choose-type"
                  class="text-decoration-none"
                >
                  Choose Type
                </a>
              </li>
              <li class="breadcrumb-item active">Create Employee</li>
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

          <!-- Main Form -->
          <div class="row justify-content-center">
            <div class="col-lg-10">
              <div class="card shadow">
                <div class="card-header bg-primary text-white">
                  <h4 class="mb-0">
                    <i class="bi bi-person-badge"></i> Create Employee User
                  </h4>
                  <small
                    >Create an employee account with detailed information</small
                  >
                </div>

                <div class="card-body">
                  <form
                    method="POST"
                    action="${pageContext.request.contextPath}/admin/users/create-employee"
                    id="employeeForm"
                  >
                    <!-- Employee Role Selection -->
                    <input type="hidden" name="userType" value="employee" />

                    <div class="row">
                      <!-- Left Column - Basic Info -->
                      <div class="col-md-6">
                        <h5 class="text-primary mb-3">
                          <i class="bi bi-person"></i> Information
                        </h5>

                        <div class="mb-3">
                          <label for="fullName" class="form-label">
                            <span class="text-danger">*</span> Full Name
                          </label>
                          <input
                            type="text"
                            class="form-control"
                            id="fullName"
                            name="fullName"
                            placeholder="Enter full name"
                            required
                          />
                        </div>

                        <div class="mb-3">
                          <label for="userName" class="form-label">
                            <span class="text-danger">*</span> Username
                          </label>
                          <input
                            type="text"
                            class="form-control"
                            id="userName"
                            name="userName"
                            placeholder="Enter username"
                            required
                          />
                          <div class="form-text">
                            Username for employee login
                          </div>
                        </div>

                        <div class="mb-3">
                          <label for="email" class="form-label">
                            <span class="text-danger">*</span> Email
                          </label>
                          <input
                            type="email"
                            class="form-control"
                            id="email"
                            name="email"
                            placeholder="Enter email address"
                            required
                          />
                        </div>

                        <div class="mb-3">
                          <label for="phoneNumber" class="form-label">
                            <span class="text-danger">*</span> Phone Number
                          </label>
                          <input
                            type="tel"
                            class="form-control"
                            id="phoneNumber"
                            name="phoneNumber"
                            placeholder="Enter phone number"
                            required
                          />
                        </div>

                        <div class="mb-3">
                          <label for="gender" class="form-label">Gender</label>
                          <select class="form-select" id="gender" name="gender">
                            <option value="">-- Select Gender --</option>
                            <option value="Male">Male</option>
                            <option value="Female">Female</option>
                            <option value="Other">Other</option>
                          </select>
                        </div>

                        <div class="mb-3">
                          <label for="birthDate" class="form-label"
                            >Birth Date</label
                          >
                          <input
                            type="date"
                            class="form-control"
                            id="birthDate"
                            name="birthDate"
                            max=""
                          />
                          <div
                            id="birthDateError"
                            class="invalid-feedback"
                          ></div>
                        </div>
                      </div>

                      <!-- Right Column - Employee Info -->
                      <div class="col-md-6">
                        <h5 class="text-primary mb-3">
                          <i class="bi bi-briefcase"></i> Employee Information
                        </h5>

                        <div class="mb-3">
                          <label for="roleId" class="form-label">
                            <span class="text-danger">*</span> Role
                          </label>
                          <select
                            class="form-select"
                            id="roleId"
                            name="roleId"
                            required
                          >
                            <option value="">-- Select Role --</option>
                            <c:forEach var="role" items="${availableRoles}">
                              <c:if test="${role.roleName != 'Customer'}">
                                <option value="${role.roleId}">
                                  ${role.roleName}
                                </option>
                              </c:if>
                            </c:forEach>
                          </select>
                        </div>

                        <div class="mb-3">
                          <label for="employeeCode" class="form-label">
                            Employee Code
                          </label>
                          <input
                            type="text"
                            class="form-control"
                            id="employeeCode"
                            name="employeeCode"
                            placeholder="Automatically generated"
                            readonly
                          />
                          <div class="form-text"></div>
                        </div>

                        <div class="mb-3">
                          <label for="salary" class="form-label"
                            >Base Salary</label
                          >
                          <div class="input-group">
                            <input
                              type="number"
                              class="form-control"
                              id="salary"
                              name="salary"
                              placeholder="Basic salary"
                              min="0"
                              step="100000"
                            />
                            <span class="input-group-text">VNĐ</span>
                          </div>
                          <div class="form-text">Blank if not determined</div>
                        </div>

                        <div class="mb-3">
                          <label for="hireDate" class="form-label"
                            >Hire Date</label
                          >
                          <input type="date" class="form-control" id="hireDate"
                          name="hireDate" value="<%= new
                          java.text.SimpleDateFormat("yyyy-MM-dd").format(new
                          java.util.Date()) %>">
                        </div>
                      </div>
                    </div>

                    <!-- Full Width - Address -->
                    <div class="row">
                      <div class="col-12">
                        <div class="mb-3">
                          <label for="address" class="form-label"
                            >Address</label
                          >
                          <textarea
                            class="form-control"
                            id="address"
                            name="address"
                            rows="2"
                            placeholder="Enter full address"
                          ></textarea>
                        </div>
                      </div>
                    </div>

                    <!-- Employee Benefits Section -->
                    <div class="row mt-4">
                      <div class="col-12">
                        <div class="card bg-light">
                          <div class="card-body">
                            <h5 class="text-primary">
                              <i class="bi bi-award"></i> Employee Benefits
                            </h5>
                            <div class="row">
                              <div class="col-md-6">
                                <ul class="list-unstyled">
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Access to management system
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Daily task management
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Project progress tracking
                                  </li>
                                </ul>
                              </div>
                              <div class="col-md-6">
                                <ul class="list-unstyled">
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Reporting and statistics
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Customer management
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Interaction with colleagues
                                  </li>
                                </ul>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <!-- Default Settings Info -->
                    <div class="row mt-3">
                      <div class="col-12">
                        <div class="alert alert-info">
                          <h6 class="alert-heading">
                            <i class="bi bi-gear"></i> Default Settings:
                          </h6>
                          <ul class="mb-0">
                            <li><strong>Status:</strong> Active</li>
                            <li>
                              <strong>Employee Code:</strong> Automatically
                              generated in order (EMP001, EMP002...)
                            </li>
                          </ul>
                        </div>
                      </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="row mt-4">
                      <div class="col-12">
                        <hr />
                        <div class="d-flex justify-content-between">
                          <a
                            href="${pageContext.request.contextPath}/admin/users/choose-type"
                            class="btn btn-secondary"
                          >
                            <i class="bi bi-arrow-left"></i> Back
                          </a>

                          <div class="btn-group">
                            <button
                              type="reset"
                              class="btn btn-outline-warning"
                            >
                              <i class="bi bi-arrow-clockwise"></i> Reset Form
                            </button>
                            <button type="submit" class="btn btn-primary">
                              <i class="bi bi-person-badge"></i> Create Employee
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Footer -->
    <%@ include file="footer.jsp" %>

    <!-- Custom Scripts for Employee Form -->
    <script src="${pageContext.request.contextPath}/assets/js/admin/create-employee.js"></script>

    <!-- Birth Date Validation Script -->
    <script src="${pageContext.request.contextPath}/assets/js/admin/birthdate-validation.js"></script>
  </body>
</html>
