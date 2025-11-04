<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Create Customer - Admin</title>
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
      <%@ include file="sidebar.jsp" %>
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
              <li class="breadcrumb-item active">Create Customer</li>
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
            <div class="col-lg-8">
              <div class="card shadow">
                <div class="card-header bg-primary text-white">
                  <h4 class="mb-0">
                    <i class="bi bi-person-circle"></i> Create Customer Account
                  </h4>
                  <small
                    >Create a customer account to use garage services</small
                  >
                </div>

                <div class="card-body">
                  <form
                    method="POST"
                    action="${pageContext.request.contextPath}/admin/users/create-customer"
                  >
                    <!-- Customer Role Hidden Field -->
                    <input type="hidden" name="userType" value="customer" />

                    <div class="row">
                      <!-- Left Column -->
                      <div class="col-md-6">
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
                            Username for customer login
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
                          <label for="phoneNumber" class="form-label"
                            >Phone Number</label
                          >
                          <input
                            type="tel"
                            class="form-control"
                            id="phoneNumber"
                            name="phoneNumber"
                            placeholder="Type phone number"
                          />
                        </div>
                      </div>

                      <!-- Right Column -->
                      <div class="col-md-6">
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
                          />
                        </div>

                        <div class="mb-3">
                          <label for="address" class="form-label"
                            >Address</label
                          >
                          <textarea
                            class="form-control"
                            id="address"
                            name="address"
                            rows="3"
                            placeholder="Your address"
                          ></textarea>
                        </div>

                        <!-- Customer Info Box -->
                        <div class="alert alert-info">
                          <h6 class="alert-heading">
                            <i class="bi bi-info-circle"></i> Information
                            Customer:
                          </h6>
                          <ul class="mb-0">
                            <li>
                              <strong>Role:</strong> Customer (automatically)
                            </li>
                            <li>
                              <strong>Password:</strong>
                            </li>
                            <li>
                              <strong>Loyalty Points:</strong> 0 (initialized)
                            </li>
                            <li><strong>Status:</strong> Active</li>
                          </ul>
                        </div>
                      </div>
                    </div>

                    <!-- Customer Features Section -->
                    <div class="row mt-4">
                      <div class="col-12">
                        <div class="card bg-light">
                          <div class="card-body">
                            <h5 class="text-primary">
                              <i class="bi bi-star"></i> Customer Features
                            </h5>
                            <div class="row">
                              <div class="col-md-6">
                                <ul class="list-unstyled">
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Schedule service appointment
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    View service history
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Track vehicle status
                                  </li>
                                </ul>
                              </div>
                              <div class="col-md-6">
                                <ul class="list-unstyled">
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Manage vehicle information
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Loyalty points accumulation
                                  </li>
                                  <li>
                                    <i
                                      class="bi bi-check-circle text-success"
                                    ></i>
                                    Receive service notifications
                                  </li>
                                </ul>
                              </div>
                            </div>
                          </div>
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
                              <i class="bi bi-person-plus"></i> Create Customer
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
  </body>
</html>
