<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/1/2025
  Time: 4:58 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register - Garage System</title>
    <link rel="stylesheet" href="https://unpkg.com/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://unpkg.com/bs-brain@2.0.4/components/registrations/registration-3/assets/css/registration-3.css">
</head>
<body>
<!-- Registration 3 - Bootstrap Brain Component -->
<section class="p-3 p-md-4 p-xl-5">
    <div class="container">
        <div class="row">
            <div class="col-12 col-md-6 bsb-tpl-bg-platinum">
                <div class="d-flex flex-column justify-content-between h-100 p-3 p-md-4 p-xl-5">
                    <h3 class="m-0">Welcome!</h3>
                    <img class="img-fluid rounded mx-auto my-4" loading="lazy" src="./assets/img/bsb-logo.svg" width="245" height="80" alt="BootstrapBrain Logo">
                    <p class="mb-0">Join us today! <a href="register.jsp" class="link-secondary text-decoration-none">Register now</a></p>
                </div>
            </div>
            <div class="col-12 col-md-6 bsb-tpl-bg-lotion">
                <div class="p-3 p-md-4 p-xl-5">
                    <div class="row">
                        <div class="col-12">
                            <div class="mb-5">
                                <h2 class="h3">Registration</h2>
                                <h3 class="fs-6 fw-normal text-secondary m-0">Enter your details to register</h3>
                            </div>
                        </div>
                    </div>

                    <!-- Hiển thị thông báo lỗi nếu có -->
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <%= request.getAttribute("error") %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <!-- Hiển thị thông báo thành công nếu có -->
                    <% if (request.getParameter("success") != null) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            Đăng ký thành công! Vui lòng đăng nhập.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    <% } %>

                    <form action="Register" method="POST">
                        <div class="row gy-3 gy-md-4 overflow-hidden">
                            <div class="col-12">
                                <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="fullName" id="fullName" placeholder="Enter your full name" required>
                            </div>
                            <div class="col-12">
                                <label for="userName" class="form-label">Username <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="userName" id="userName" placeholder="Enter your username" required>
                            </div>
                            <div class="col-12">
                                <label for="phoneNumber" class="form-label">Phone Number <span class="text-danger">*</span></label>
                                <input type="tel" class="form-control" name="phoneNumber" id="phoneNumber" placeholder="Enter your phone number" required>
                            </div>
                            <div class="col-12">
                                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" name="email" id="email" placeholder="name@example.com" required>
                            </div>
                            <div class="col-12">
                                <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" name="password" id="password" placeholder="Enter your password" required>
                            </div>
                            <div class="col-12">
                                <div class="form-check">
                                    <input class="form-check-input" type="checkbox" value="" name="iAgree" id="iAgree" required>
                                    <label class="form-check-label text-secondary" for="iAgree">
                                        I agree to the <a href="#!" class="link-primary text-decoration-none">terms and conditions</a>
                                    </label>
                                </div>
                            </div>
                            <div class="col-12">
                                <div class="d-grid">
                                    <button class="btn bsb-btn-xl btn-primary" type="submit">Sign up</button>
                                </div>
                            </div>
                        </div>
                    </form>
                    <div class="row">
                        <div class="col-12">
                            <hr class="mt-5 mb-4 border-secondary-subtle">
                            <p class="m-0 text-secondary text-end">Already have an account? <a href="login.jsp" class="link-primary text-decoration-none">Sign in</a></p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-12">
                            <p class="mt-5 mb-4">Or sign in with</p>
                            <div class="d-flex gap-3 flex-column flex-xl-row">
                                <a href="#" class="btn bsb-btn-xl btn-outline-primary w-100 px-4 py-3 d-flex align-items-center justify-content-center" style="font-size: 1.25rem;height: 55px;">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-google me-2" viewBox="0 0 16 16">
                                        <path d="M15.545 6.558a9.42 9.42 0 0 1 .139 1.626c0 2.434-.87 4.492-2.384 5.885h.002C11.978 15.292 10.158 16 8 16A8 8 0 1 1 8 0a7.689 7.689 0 0 1 5.352 2.082l-2.284 2.284A4.347 4.347 0 0 0 8 3.166c-2.087 0-3.86 1.408-4.492 3.304a4.792 4.792 0 0 0 0 3.063h.003c.635 1.893 2.405 3.301 4.492 3.301 1.078 0 2.004-.276 2.722-.764h-.003a3.702 3.702 0 0 0 1.599-2.431H8v-3.08h7.545z" />
                                    </svg>
                                    <span class="ms-2 fw-semibold">Sign in with Google</span>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<script src="https://unpkg.com/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
