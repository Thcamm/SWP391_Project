<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">

    <style>
        body {
            background-color: #f8f9fa;
        }
        .card {
            border-radius: 10px;
        }
        .card-header {
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
        }
    </style>
</head>

<body>
<jsp:include page="/common/header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-5 col-md-7 col-sm-10">
            <div class="card shadow">
                <div class="card-header bg-primary text-white text-center py-3">
                    <h4 class="mb-0 fw-semibold">Change Your Password</h4>
                </div>
                <div class="card-body p-4">

                    <!-- Success / Error Messages -->
                    <c:if test="${not empty success}">
                        <div class="alert alert-success text-center" role="alert">
                                ${success}
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger text-center" role="alert">
                                ${error}
                        </div>
                    </c:if>

                    <!-- Change Password Form -->
                    <form action="${pageContext.request.contextPath}/user/changePassword" method="post" id="changePasswordForm">

                        <div class="mb-3">
                            <label for="oldPassword" class="form-label fw-semibold">Current Password</label>
                            <input type="password" id="oldPassword" name="oldPassword" class="form-control" placeholder="Enter current password" required>
                        </div>

                        <div class="mb-3">
                            <label for="newPassword" class="form-label fw-semibold">New Password</label>
                            <input type="password" id="newPassword" name="newPassword" class="form-control" placeholder="Enter new password" required>
                        </div>

                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label fw-semibold">Confirm New Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" placeholder="Re-enter new password" required>
                            <small id="passwordMismatch" class="text-danger mt-1 d-none">Passwords do not match.</small>
                        </div>

                        <div class="d-flex justify-content-between mt-4">
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary">
                                Back to Profile
                            </a>
                            <button type="submit" class="btn btn-primary px-4">
                                Change Password
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp" />

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>

<!-- JS kiểm tra confirm password -->
<script>
    const form = document.getElementById("changePasswordForm");
    const newPass = document.getElementById("newPassword");
    const confirmPass = document.getElementById("confirmPassword");
    const mismatchText = document.getElementById("passwordMismatch");

    form.addEventListener("submit", (e) => {
        if (newPass.value !== confirmPass.value) {
            e.preventDefault();
            mismatchText.classList.remove("d-none");
        } else {
            mismatchText.classList.add("d-none");
        }
    });
</script>
</body>
</html>
