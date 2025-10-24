<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Garage System</title>
    <link rel="stylesheet" href="https://unpkg.com/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="assets/css/user/login.css" rel="stylesheet">
</head>
<body>
<div class="login-container">
    <!-- Left Panel - Welcome Image -->
    <div class="left-panel">
        <img src="https://carspa.vn/wp-content/uploads/2023/11/Xuong-862x1536.jpg" alt="Welcome back!">
    </div>

    <!-- Right Panel - Login Form -->
    <div class="right-panel">
        <div class="form-wrapper">
            <!-- Logo (Optional) -->

            <div class="form-header">
                <h2>Welcome Back!</h2>
                <p>Sign in to continue to your account</p>
            </div>

            <!-- Google Login -->
            <div class="social-login">
                <a href="${pageContext.request.contextPath}/auth/google" class="btn-google">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M15.545 6.558a9.42 9.42 0 0 1 .139 1.626c0 2.434-.87 4.492-2.384 5.885h.002C11.978 15.292 10.158 16 8 16A8 8 0 1 1 8 0a7.689 7.689 0 0 1 5.352 2.082l-2.284 2.284A4.347 4.347 0 0 0 8 3.166c-2.087 0-3.86 1.408-4.492 3.304a4.792 4.792 0 0 0 0 3.063h.003c.635 1.893 2.405 3.301 4.492 3.301 1.078 0 2.004-.276 2.722-.764h-.003a3.702 3.702 0 0 0 1.599-2.431H8v-3.08h7.545z" />
                    </svg>
                    <span>Continue with Google</span>
                </a>
            </div>

            <!-- Divider -->
            <div class="divider">
                <span>Or sign in with email</span>
            </div>

            <!-- Error Message -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    <i class="bi bi-exclamation-circle me-2"></i>${errorMessage}
                </div>
            </c:if>

            <!-- Login Form -->
            <form action="login" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">Username <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="username" name="username"
                           placeholder="Enter your username" required>
                </div>

                <div class="mb-3">
                    <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="Enter your password" required>
                </div>

                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="true"
                           name="remember_me" id="remember_me">
                    <label class="form-check-label" for="remember_me">
                        Keep me logged in
                    </label>
                </div>

                <button type="submit" class="btn-login">
                    <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
                </button>
            </form>

            <!-- Footer Links -->
            <div class="footer-links">
                <a href="Register">
                    Create new account
                </a>
                <a href="${pageContext.request.contextPath}/forgotpassword">
                    Forgot password?
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://unpkg.com/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
