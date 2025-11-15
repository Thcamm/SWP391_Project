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
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-circle me-2"></i>${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle me-2"></i>${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <%-- Form: Combined Email and OTP --%>
            <form action="forgotpassword" method="post" id="resetPasswordForm">
                <input type="hidden" name="action" value="${otpSent ? 'verifyOTP' : 'sendOTP'}">

                <%-- Email Input (Always visible) --%>
                <div class="mb-3">
                    <label for="email" class="form-label">Email Address <span class="text-danger">*</span></label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        <input type="email"
                               class="form-control"
                               id="email"
                               name="email"
                               placeholder="Enter your registered email"
                               value="${email != null ? email : param.email}"
                               ${otpSent ? 'readonly' : ''}
                               required>
                    </div>
                    <small class="text-muted">
                        <i class="bi bi-info-circle"></i> We'll send a verification code to this email
                    </small>
                </div>

                <%-- OTP Input (Only visible after OTP sent) --%>
                <c:if test="${otpSent}">
                    <div class="mb-3">
                        <label for="otp" class="form-label">Verification Code <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-shield-lock"></i></span>
                            <input type="text"
                                   class="form-control"
                                   id="otp"
                                   name="otp"
                                   placeholder="Enter 6-digit code"
                                   maxlength="6"
                                   pattern="[0-9]{6}"
                                   required>
                        </div>
                        <small class="text-muted">
                            <i class="bi bi-clock"></i> Code expires in 15 minutes
                        </small>
                    </div>

                    <div class="alert alert-info" role="alert">
                        <i class="bi bi-lightbulb me-2"></i>
                        <strong>What happens next?</strong><br/>
                        After verifying your code, a new password will be automatically generated and sent to your email.
                    </div>
                </c:if>

                <%-- Submit Button --%>
                <button type="submit" class="btn-login">
                    <c:choose>
                        <c:when test="${otpSent}">
                            <i class="bi bi-check-circle me-1"></i> Verify Code & Reset Password
                        </c:when>
                        <c:otherwise>
                            <i class="bi bi-send me-1"></i> Send Verification Code
                        </c:otherwise>
                    </c:choose>
                </button>

                <%-- Resend OTP Link (Only visible after OTP sent) --%>
                <c:if test="${otpSent}">
                    <div class="text-center mt-3">
                        <a href="forgotpassword" class="text-decoration-none">
                            <i class="bi bi-arrow-clockwise"></i> Didn't receive code? Try again
                        </a>
                    </div>
                </c:if>
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