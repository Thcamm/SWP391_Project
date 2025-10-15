<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/changePassword.css">
</head>
<body>

<div class="container">
    <h2>Change Your Password</h2>

    <c:if test="${not empty success}">
        <div class="message success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="message error">${error}</div>
    </c:if>

    <%-- Update form action to the new URL --%>
    <form id="changePasswordForm" action="${pageContext.request.contextPath}/user/changePassword" method="post">
        <div class="form-group">
            <label for="oldPassword">Current Password:</label>
            <input type="password" id="oldPassword" name="oldPassword" required>
        </div>
        <div class="form-group">
            <label for="newPassword">New Password:</label>
            <input type="password" id="newPassword" name="newPassword" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm New Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
            <small id="passwordMismatch" class="error-text" style="display: none;">Passwords do not match.</small>
        </div>
        <div class="action-buttons">
            <button type="submit" class="btn btn-primary">Change Password</button>
            <%-- Update "Back" link to the new profile URL --%>
            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-secondary">Back to Profile</a>
        </div>
    </form>
</div>

<script src="${pageContext.request.contextPath}/assets/js/user/changePassword.js"></script>

</body>
</html>