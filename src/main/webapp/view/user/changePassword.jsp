<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">

    <style>
        body {
            background-color: #f8f9fa;
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-lg-6 col-md-8">

            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h2 class="h4 mb-0">Change Your Password</h2>
                </div>
                <div class="card-body p-4">

                    <c:if test="${not empty success}">
                        <div class="alert alert-success" role="alert">
                                ${success}
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                                ${error}
                        </div>
                    </c:if>

                    <form id="changePasswordForm" action="${pageContext.request.contextPath}/user/changePassword" method="post">

                        <div class="mb-3">
                            <label for="oldPassword" class="form-label">Current Password:</label>
                            <input type="password" id="oldPassword" name="oldPassword" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label for="newPassword" class="form-label">New Password:</label>
                            <input type="password" id="newPassword" name="newPassword" class="form-control" required>
                        </div>

                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Confirm New Password:</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
                            <small id="passwordMismatch" class="text-danger mt-1" style="display: none;">Passwords do not match.</small>
                        </div>

                        <div class="mt-4 d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-secondary">Back to Profile</a>
                            <button type="submit" class="btn btn-primary">Change Password</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>

<script src="${pageContext.request.contextPath}/assets/js/user/changePassword.js"></script>
<jsp:include page="/common/footer.jsp" />

</body>
</html>