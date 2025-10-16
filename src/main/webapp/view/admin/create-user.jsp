<%-- Created by IntelliJ IDEA. User: Admin Date: 12/10/2025 Time: 12:11 AM To
change this template use File | Settings | File Templates. --%> <%@ page
contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Tạo User Mới - Admin</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />
    <link
      href="${pageContext.request.contextPath}/assets/css/admin/create-user.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <!-- Header -->
    <nav class="navbar navbar-dark bg-dark">
      <div class="container-fluid">
        <span class="navbar-brand"> Create New User </span>
        <span class="navbar-text">
          ${currentUser} |
          <fmt:formatDate
            value="<%= new java.util.Date() %>"
            pattern="dd/MM/yyyy HH:mm"
          />
        </span>
      </div>
    </nav>

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
          <li class="breadcrumb-item active">Create User</li>
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
              <h4 class="mb-0">Tạo User Mới</h4>
              <small>Điền thông tin để tạo user mới trong hệ thống</small>
            </div>

            <div class="card-body">
              <form
                method="POST"
                action="${pageContext.request.contextPath}/admin/users/create"
              >
                <div class="row">
                  <!-- Left Column -->
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="fullName" class="form-label">
                        <span class="text-danger">*</span> Họ và Tên
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="fullName"
                        name="fullName"
                        placeholder="Nhập họ và tên đầy đủ"
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
                        placeholder="Nhập username"
                        required
                      />
                      <div class="form-text">
                        Username sẽ được sử dụng để đăng nhập
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
                        placeholder="Nhập địa chỉ email"
                        required
                      />
                    </div>
                  </div>

                  <!-- Right Column -->
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="role" class="form-label">
                        <span class="text-danger">*</span> Role
                      </label>
                      <select
                        class="form-select"
                        id="role"
                        name="role"
                        required
                      >
                        <option value="">-- Chọn Role --</option>
                        <c:forEach var="roleOption" items="${availableRoles}">
                          <option value="${roleOption.roleId}">
                            ${roleOption.roleName}
                          </option>
                        </c:forEach>
                      </select>
                    </div>

                    <!-- Info Box -->
                    <div class="alert alert-info">
                      <h6 class="alert-heading">Thông tin mặc định:</h6>
                      <ul class="mb-0">
                        <li><strong>Mật khẩu mặc định:</strong> 123456</li>
                        <li><strong>Trạng thái:</strong> Active</li>
                        <li>User có thể đổi mật khẩu sau khi đăng nhập</li>
                      </ul>
                    </div>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="row">
                  <div class="col-12">
                    <hr />
                    <div class="d-flex justify-content-between">
                      <a
                        href="${pageContext.request.contextPath}/admin/users"
                        class="btn btn-secondary"
                      >
                        Back to Users
                      </a>

                      <div class="btn-group">
                        <button type="reset" class="btn btn-outline-warning">
                          Reset Form
                        </button>
                        <button type="submit" class="btn btn-success">
                          Create User
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>

          <!-- Guidelines Card -->
          <div class="card mt-4">
            <div class="card-header">
              <h6 class="mb-0">Hướng dẫn tạo User</h6>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <h6 class="text-success">Nên làm:</h6>
                  <ul class="text-success">
                    <li>Sử dụng username dễ nhớ và duy nhất</li>
                    <li>Điền email chính xác để liên lạc</li>
                    <li>Chọn role phù hợp với công việc</li>
                    <li>Kiểm tra kỹ thông tin trước khi tạo</li>
                  </ul>
                </div>
                <div class="col-md-6">
                  <h6 class="text-danger">Tránh:</h6>
                  <ul class="text-danger">
                    <li>Username có ký tự đặc biệt hoặc khoảng trắng</li>
                    <li>Email không hợp lệ hoặc đã sử dụng</li>
                    <li>Để trống các trường bắt buộc (*)</li>
                    <li>Chọn role không phù hợp</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
