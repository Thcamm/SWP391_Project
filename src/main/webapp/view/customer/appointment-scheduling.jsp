<%@ page import="model.user.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đặt lịch hẹn</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/appointment-scheduling.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />

<main class="appointment-section">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="appointment-form">
                <p class="text-uppercase text-muted mb-2" style="font-size: 14px; letter-spacing: 2px;">
                    THÔNG TIN VỀ CHÚNG TÔI
                </p>
                <h2 class="mb-4" style="font-size: 32px; font-weight: 700; color: #000;">
                    ĐẶT LỊCH SỬA XE VÀ BẢO DƯỠNG
                </h2>
                <%-- Hiển thị success message ---%>
                <% if (request.getAttribute("successMessage") != null) { %>
                <div class="alert alert-success">
                    <%= request.getAttribute("successMessage") %>
                </div>
                <% request.removeAttribute("successMessage"); %>
                <% } %>

                <%-- Hiển thị error message ---%>
                <% if (request.getAttribute("errorMessage") != null) { %>
                <div class="alert alert-danger">
                    <%= request.getAttribute("errorMessage") %>
                </div>
                <% } %>

                <%-- Debug info ---%>
                <% if (request.getAttribute("debugInfo") != null) { %>
                <div class="alert alert-info">
                    <strong>Debug:</strong> <%= request.getAttribute("debugInfo") %>
                </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/customer/AppointmentService" method="post">
                    <!-- Row 1: Họ và tên + Số điện thoại -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="fullName" class="form-label">
                                Họ và tên <span class="required">*</span>
                            </label>
                            <input type="text" class="form-control" id="fullName" name="fullName" value="<%= ((User)session.getAttribute("user")).getUserName() %>"
                                   placeholder="Nhập họ và tên" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="phoneNumber" class="form-label">
                                Số điện thoại <span class="required">*</span>
                            </label>
                            <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber" value="<%= ((User)session.getAttribute("user")).getPhoneNumber() %>"
                                   placeholder="Nhập số điện thoại" pattern="[0-9]{10}" required>
                        </div>
                    </div>

                    <!-- Row 2: Hãng xe + Biển số xe -->
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="carBrand" class="form-label">
                                Hãng xe <span class="required">*</span>
                            </label>
                            <input type="text" class="form-control" id="carBrand" name="carBrand"
                                   placeholder="Ví dụ: Toyota, Honda, BMW..." required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="licensePlate" class="form-label">
                                Biển số xe <span class="required">*</span>
                            </label>
                            <input type="text" class="form-control" id="licensePlate" name="licensePlate"
                                   placeholder="Ví dụ: 51A-12345" required>
                        </div>
                    </div>

                    <!-- Ngày hẹn - Full width -->
                    <div class="mb-3">
                        <label for="appointmentDate" class="form-label">
                            Ngày hẹn <span class="required">*</span>
                        </label>
                        <input type="datetime-local" class="form-control" id="appointmentDate"
                               name="appointmentDate" required>
                    </div>

                    <!-- Mô tả - Full width -->
                    <div class="mb-4">
                        <label for="description" class="form-label">Mô tả thêm</label>
                        <textarea class="form-control" id="description" name="description"
                                  rows="4" placeholder="Nhập mô tả chi tiết về vấn đề của xe (nếu có)"></textarea>
                    </div>

                    <!-- Button submit -->
                    <div class="text-center">
                        <button type="submit" class="btn btn-submit">
                            <i class="fas fa-paper-plane"></i> Đặt lịch hẹn
                        </button>
                    </div>
                </form>
            </div>

            </div>
        </div>
    </div>
</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Set min date to today
    const dateInput = document.getElementById('appointmentDate');
    const now = new Date();
    now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
    dateInput.min = now.toISOString().slice(0, 16);
</script>
</body>
</html>
