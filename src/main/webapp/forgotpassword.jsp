<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - Garage System</title>
    <%-- Sử dụng lại file CSS của trang login để giao diện đồng bộ --%>
    <link rel="stylesheet" href="https://unpkg.com/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="assets/css/user/login.css" rel="stylesheet">
</head>
<body>
<div class="login-container">
    <div class="left-panel">
        <img src="https://carspa.vn/wp-content/uploads/2023/11/Xuong-862x1536.jpg" alt="Reset your password">
    </div>

    <div class="right-panel">
        <div class="form-wrapper">

            <div class="form-header">
                <h2>Reset Your Password</h2>
                <p>Please enter your details to reset your password.</p>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    <i class="bi bi-exclamation-circle me-2"></i>${errorMessage}
                </div>
            </c:if>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success" role="alert">
                    <i class="bi bi-check-circle me-2"></i>${successMessage}
                </div>
            </c:if>

            <form action="forgotpassword" method="post">
                <div class="mb-3">
                    <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="Enter your email to receive OTP" required>
                        <button class="btn btn-outline-secondary" type="button" id="sendOtpBtn" name="action" value="send">Send OTP</button>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="otp" class="form-label">OTP Code <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="otp" name="otp"
                           placeholder="Enter the code sent to your email" required>
                </div>

                <div class="mb-3">
                    <label for="newPassword" class="form-label">New Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword"
                           placeholder="Enter your new password" required>
                </div>

                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirm New Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword"
                           placeholder="Confirm your new password" required>
                </div>

                <button type="submit" class="btn-login">
                    Reset Password
                </button>
            </form>

            <div class="footer-links">
                <a href="login">
                    Back to Sign In
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://unpkg.com/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>