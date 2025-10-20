<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Tạo Employee - Admin</title>
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
    <nav class="navbar navbar-dark bg-primary">
      <div class="container-fluid">
        <span class="navbar-brand">
          <i class="bi bi-person-badge"></i> Create Employee User
        </span>
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
                <i class="bi bi-person-badge"></i> Tạo Employee User
              </h4>
              <small>Tạo tài khoản nhân viên với thông tin chi tiết</small>
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
                      <i class="bi bi-person"></i> Thông tin cơ bản
                    </h5>

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
                        Username để nhân viên đăng nhập
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

                    <div class="mb-3">
                      <label for="phoneNumber" class="form-label">
                        <span class="text-danger">*</span> Số điện thoại
                      </label>
                      <input
                        type="tel"
                        class="form-control"
                        id="phoneNumber"
                        name="phoneNumber"
                        placeholder="Nhập số điện thoại"
                        required
                      />
                    </div>

                    <div class="mb-3">
                      <label for="gender" class="form-label">Giới tính</label>
                      <select class="form-select" id="gender" name="gender">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="Male">Nam</option>
                        <option value="Female">Nữ</option>
                      </select>
                    </div>

                    <div class="mb-3">
                      <label for="birthDate" class="form-label"
                        >Ngày sinh</label
                      >
                      <input
                        type="date"
                        class="form-control"
                        id="birthDate"
                        name="birthDate"
                      />
                    </div>
                  </div>

                  <!-- Right Column - Employee Info -->
                  <div class="col-md-6">
                    <h5 class="text-primary mb-3">
                      <i class="bi bi-briefcase"></i> Thông tin nhân viên
                    </h5>

                    <div class="mb-3">
                      <label for="roleId" class="form-label">
                        <span class="text-danger">*</span> Chức vụ
                      </label>
                      <select
                        class="form-select"
                        id="roleId"
                        name="roleId"
                        required
                      >
                        <option value="">-- Chọn chức vụ --</option>
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
                        Mã nhân viên
                      </label>
                      <input
                        type="text"
                        class="form-control"
                        id="employeeCode"
                        name="employeeCode"
                        placeholder="Tự động tạo nếu để trống"
                        readonly
                      />
                      <div class="form-text">
                        <i class="bi bi-info-circle"></i>
                      </div>
                    </div>

                    <div class="mb-3">
                      <label for="salary" class="form-label"
                        >Lương cơ bản</label
                      >
                      <div class="input-group">
                        <input
                          type="number"
                          class="form-control"
                          id="salary"
                          name="salary"
                          placeholder="Nhập lương cơ bản"
                          min="0"
                          step="100000"
                        />
                        <span class="input-group-text">VNĐ</span>
                      </div>
                      <div class="form-text">Để trống nếu chưa xác định</div>
                    </div>

                    <div class="mb-3">
                      <label for="hireDate" class="form-label"
                        >Ngày vào làm</label
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
                      <label for="address" class="form-label">Địa chỉ</label>
                      <textarea
                        class="form-control"
                        id="address"
                        name="address"
                        rows="2"
                        placeholder="Nhập địa chỉ đầy đủ"
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
                          <i class="bi bi-award"></i> Quyền lợi Employee
                        </h5>
                        <div class="row">
                          <div class="col-md-6">
                            <ul class="list-unstyled">
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Truy cập hệ thống quản lý
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Quản lý công việc hàng ngày
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Theo dõi tiến độ dự án
                              </li>
                            </ul>
                          </div>
                          <div class="col-md-6">
                            <ul class="list-unstyled">
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Báo cáo và thống kê
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Quản lý khách hàng
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Tương tác với đồng nghiệp
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
                        <i class="bi bi-gear"></i> Cài đặt mặc định:
                      </h6>
                      <ul class="mb-0">
                        <li>
                          <strong>Mật khẩu mặc định:</strong> 123456 (nhân viên
                          nên đổi sau lần đăng nhập đầu tiên)
                        </li>
                        <li><strong>Trạng thái:</strong> Active</li>
                        <li>
                          <strong>Mã nhân viên:</strong> Tự động tạo theo thứ tự
                          (EMP001, EMP002...)
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
                        <i class="bi bi-arrow-left"></i> Quay lại
                      </a>

                      <div class="btn-group">
                        <button type="reset" class="btn btn-outline-warning">
                          <i class="bi bi-arrow-clockwise"></i> Reset Form
                        </button>
                        <button type="submit" class="btn btn-primary">
                          <i class="bi bi-person-badge"></i> Tạo Employee
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script>
      // Generate employee code suggestion based on role selection
      document.getElementById("roleId").addEventListener("change", function () {
        const selectedRole = this.options[this.selectedIndex].text;
        const employeeCodeField = document.getElementById("employeeCode");

        if (selectedRole && selectedRole !== "-- Chọn chức vụ --") {
          // Generate suggested employee code
          const rolePrefix = selectedRole.substring(0, 3).toUpperCase();
          const timestamp = new Date().getTime().toString().slice(-4);
          const suggestedCode = rolePrefix + timestamp;

          employeeCodeField.placeholder = `Gợi ý: ${suggestedCode}`;
        } else {
          employeeCodeField.placeholder = "Tự động tạo nếu để trống";
        }
      });

      // Form validation
      document
        .getElementById("employeeForm")
        .addEventListener("submit", function (e) {
          const salary = document.getElementById("salary").value;
          if (salary && salary < 0) {
            alert("Lương không thể âm");
            e.preventDefault();
            return false;
          }
        });
    </script>
  </body>
</html>
