<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Users - Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<div class="container-fluid">
    <!-- Header -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
        <div class="container-fluid">
                <span class="navbar-brand">
                    <i class="bi bi-gear-fill"></i> Admin Panel
                </span>
            <div class="navbar-nav ms-auto">
                    <span class="navbar-text">
                        <i class="bi bi-person-circle"></i> Xin chào, <strong>${currentUser}</strong>
                    </span>
            </div>
        </div>
    </nav>

    <!-- HIỂN THỊ MESSAGE THAY VÌ JSON -->
    <c:if test="${not empty message}">
        <div class="row mb-3">
            <div class="col-12">
                <div class="alert ${messageType == 'success' ? 'alert-success' : 'alert-danger'} alert-dismissible fade show" role="alert">
                    <c:choose>
                        <c:when test="${messageType == 'success'}">
                            <i class="bi bi-check-circle-fill"></i>
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-exclamation-triangle-fill"></i>
                        </c:otherwise>
                    </c:choose>
                        ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </div>
        </div>
    </c:if>

    <!-- Page Title & Stats -->
    <div class="row mb-4">
        <div class="col-md-8">
            <h2><i class="bi bi-people-fill"></i> Quản lý Users</h2>
            <p class="text-muted">Ngày: <%= new java.util.Date() %></p>
        </div>
        <div class="col-md-4">
            <div class="card bg-primary text-white">
                <div class="card-body">
                    <h5 class="card-title">Tổng Users</h5>
                    <h3><i class="bi bi-people"></i> ${totalUsers}</h3>
                </div>
            </div>
        </div>
    </div>

    <!-- Search Form -->
    <div class="row mb-4">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5><i class="bi bi-search"></i> Tìm kiếm & Lọc</h5>
                </div>
                <div class="card-body">
                    <form method="GET" action="${pageContext.request.contextPath}/admin/users/" class="row g-3">
                        <div class="col-md-4">
                            <label for="keyword" class="form-label">Từ khóa</label>
                            <input type="text" class="form-control" id="keyword" name="keyword"
                                   value="${searchKeyword}" placeholder="Tìm username, email, tên...">
                        </div>
                        <div class="col-md-3">
                            <label for="role" class="form-label">Vai trò</label>
                            <select class="form-select" id="role" name="role">
                                <option value="">Tất cả</option>
                                <option value="1" ${selectedRole == 1 ? 'selected' : ''}>Admin</option>
                                <option value="2" ${selectedRole == 2 ? 'selected' : ''}>Manager</option>
                                <option value="3" ${selectedRole == 3 ? 'selected' : ''}>Employee</option>
                                <option value="4" ${selectedRole == 4 ? 'selected' : ''}>User</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">&nbsp;</label>
                            <div>
                                <button type="submit" class="btn btn-primary w-100">
                                    <i class="bi bi-search"></i> Tìm kiếm
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Create User Button -->
    <div class="row mb-3">
        <div class="col-12">
            <button class="btn btn-success" data-bs-toggle="collapse" data-bs-target="#createUserForm">
                <i class="bi bi-person-plus"></i> Tạo User Mới
            </button>
        </div>
    </div>

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
            </div>
        </div>
    </div>

    <!-- Users Table -->
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5><i class="bi bi-table"></i> Danh sách Users</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty users}">
                            <div class="text-center p-4">
                                <i class="bi bi-inbox display-1 text-muted"></i>
                                    <p class="text-muted">Không tìm thấy user nào</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-striped table-hover">
                                    <thead class="table-dark">
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Họ tên</th>
                                        <th>Email</th>
                                        <th>Vai trò</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>${user.userId}</td>
                                            <td>
                                                <strong>${user.userName}</strong>
                                                <c:if test="${user.userName == currentUser}">
                                                    <span class="badge bg-warning text-dark">Bạn</span>
                                                </c:if>
                                            </td>
                                            <td>${user.fullName}</td>
                                            <td>${user.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.roleId == 1}">
                                                        <span class="badge bg-danger">Admin</span>
                                                    </c:when>
                                                    <c:when test="${user.roleId == 2}">
                                                        <span class="badge bg-warning text-dark">Manager</span>
                                                    </c:when>
                                                    <c:when test="${user.roleId == 3}">
                                                        <span class="badge bg-info">Employee</span>
                                                    </c:when>
                                                    <c:when test="${user.roleId == 4}">
                                                        <span class="badge bg-primary">User</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Guest</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.activeStatus}">
                                                        <span class="badge bg-success">Hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Không hoạt động</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <!-- FORM BUTTONS THAY VÌ JAVASCRIPT -->
                                                <div class="btn-group" role="group">
                                                    <!-- Change Role -->
                                                    <button class="btn btn-sm btn-outline-primary"
                                                            data-bs-toggle="collapse"
                                                            data-bs-target="#roleForm${user.userId}"
                                                        ${user.userName == currentUser ? 'disabled' : ''}>
                                                        <i class="bi bi-shield-check"></i>
                                                    </button>

                                                    <!-- Toggle Status -->
                                                    <form method="POST" action="/admin/users" style="display:inline;">
                                                        <input type="hidden" name="action" value="toggleStatus">
                                                        <input type="hidden" name="userId" value="${user.userId}">
                                                        <button class="btn btn-sm ${user.activeStatus ? 'btn-outline-warning' : 'btn-outline-success'}"
                                                                type="submit"
                                                            ${user.userName == currentUser ? 'disabled' : ''}
                                                                onclick="return confirm('Bạn có chắc muốn thay đổi trạng thái?')">
                                                            <i class="bi ${user.activeStatus ? 'bi-lock' : 'bi-unlock'}"></i>
                                                        </button>
                                                    </form>

                                                    <!-- Reset Password -->
                                                    <form method="POST" action="/admin/users" style="display:inline;">
                                                        <input type="hidden" name="action" value="resetPassword">
                                                        <input type="hidden" name="userId" value="${user.userId}">
                                                        <button class="btn btn-sm btn-outline-secondary"
                                                                type="submit"
                                                                onclick="return confirm('Bạn có chắc muốn reset password?')">
                                                            <i class="bi bi-key"></i>
                                                        </button>
                                                    </form>

                                                    <!-- Activate (chỉ hiện khi inactive) -->
                                                    <c:if test="${!user.activeStatus}">
                                                        <form method="POST" action="/admin/users" style="display:inline;">
                                                            <input type="hidden" name="action" value="activate">
                                                            <input type="hidden" name="userId" value="${user.userId}">
                                                            <button class="btn btn-sm btn-outline-success"
                                                                    type="submit"
                                                                    onclick="return confirm('Bạn có chắc muốn kích hoạt user này?')">
                                                                <i class="bi bi-check-circle"></i>
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>

                                                <!-- Role Change Form (Collapse) -->
                                                <div class="collapse mt-2" id="roleForm${user.userId}">
                                                    <form method="POST" action="/admin/users" class="border p-2 rounded bg-light">
                                                        <input type="hidden" name="action" value="changeRole">
                                                        <input type="hidden" name="userId" value="${user.userId}">
                                                        <div class="row">
                                                            <div class="col-8">
                                                                <select name="newRole" class="form-select form-select-sm">
                                                                    <option value="1" ${user.roleId == 1 ? 'selected' : ''}>Admin</option>
                                                                    <option value="2" ${user.roleId == 2 ? 'selected' : ''}>Manager</option>
                                                                    <option value="3" ${user.roleId == 3 ? 'selected' : ''}>Employee</option>
                                                                    <option value="4" ${user.roleId == 4 ? 'selected' : ''}>User</option>
                                                                    <option value="5" ${user.roleId == 5 ? 'selected' : ''}>Guest</option>
                                                                </select>
                                                            </div>
                                                            <div class="col-4">
                                                                <button type="submit" class="btn btn-sm btn-primary w-100">
                                                                    Đổi
                                                                </button>
                                                            </div>
                                                        </div>
                                                    </form>
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
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>