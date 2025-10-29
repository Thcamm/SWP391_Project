<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Tạo Customer - Admin</title>
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
                <i class="bi bi-person-circle"></i> Tạo Customer User
              </h4>
              <small>Tạo tài khoản khách hàng sử dụng dịch vụ garage</small>
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
                        Username để khách hàng đăng nhập
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
                      <label for="phoneNumber" class="form-label"
                        >Số điện thoại</label
                      >
                      <input
                        type="tel"
                        class="form-control"
                        id="phoneNumber"
                        name="phoneNumber"
                        placeholder="Nhập số điện thoại"
                      />
                    </div>
                  </div>

                  <!-- Right Column -->
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="gender" class="form-label">Giới tính</label>
                      <select class="form-select" id="gender" name="gender">
                        <option value="">-- Chọn giới tính --</option>
                        <option value="Male">Nam</option>
                        <option value="Female">Nữ</option>
                        <option value="Other">Khác</option>
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

                    <div class="mb-3">
                      <label for="address" class="form-label">Địa chỉ</label>
                      <textarea
                        class="form-control"
                        id="address"
                        name="address"
                        rows="3"
                        placeholder="Nhập địa chỉ"
                      ></textarea>
                    </div>

                    <!-- Customer Info Box -->
                    <div class="alert alert-info">
                      <h6 class="alert-heading">
                        <i class="bi bi-info-circle"></i> Thông tin Customer:
                      </h6>
                      <ul class="mb-0">
                        <li><strong>Role:</strong> Customer (tự động)</li>
                        <li><strong>Mật khẩu:</strong> 123456 (mặc định)</li>
                        <li><strong>Loyalty Points:</strong> 0 (khởi tạo)</li>
                        <li><strong>Trạng thái:</strong> Active</li>
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
                          <i class="bi bi-star"></i> Tính năng dành cho Customer
                        </h5>
                        <div class="row">
                          <div class="col-md-6">
                            <ul class="list-unstyled">
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Đặt lịch hẹn dịch vụ
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Xem lịch sử dịch vụ
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Theo dõi trạng thái xe
                              </li>
                            </ul>
                          </div>
                          <div class="col-md-6">
                            <ul class="list-unstyled">
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Quản lý thông tin xe
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Tích lũy điểm thưởng
                              </li>
                              <li>
                                <i class="bi bi-check-circle text-success"></i>
                                Nhận thông báo dịch vụ
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
                        <i class="bi bi-arrow-left"></i> Quay lại
                      </a>

                      <div class="btn-group">
                        <button type="reset" class="btn btn-outline-warning">
                          <i class="bi bi-arrow-clockwise"></i> Reset Form
                        </button>
                        <button type="submit" class="btn btn-primary">
                          <i class="bi bi-person-plus"></i> Tạo Customer
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

    <!-- Footer -->
    <%@ include file="footer.jsp" %>
  </body>
</html>
