<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%-- Import JSTL core library --%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hồ sơ người dùng</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        .container { max-width: 600px; margin: 0 auto; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; }
        .form-group input[type="text"], .form-group input[type="email"] {
            width: 100%; padding: 8px; box-sizing: border-box;
        }
        .message { padding: 10px; margin-bottom: 15px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>

<div class="container">
    <h1>Hồ sơ người dùng</h1>

    <%-- Display success message if available (pushed by Controller) --%>
    <c:if test="${not empty successMessage}">
        <div class="message success">${successMessage}</div>
    </c:if>

    <%-- Display error message if available (pushed by Controller) --%>
    <c:if test="${not empty errorMessage}">
        <div class="message error">${errorMessage}</div>
    </c:if>

    <%-- Check if the 'user' object exists in the request scope --%>
    <c:choose>
        <c:when test="${not empty user}">
            <form action="${pageContext.request.contextPath}/me" method="POST">

                <div class="form-group">
                    <label for="userName">Username:</label>
                    <input type="text" id="userName" name="userName" value="${user.userName}" readonly style="background-color: #eee;">
                    <span>(Không thể chỉnh sửa)</span>
                </div>

                <div class="form-group">
                    <label for="fullName">Họ và Tên:</label>
                    <input type="text" id="fullName" name="fullName" value="${user.fullName}" required>
                </div>

                <div class="form-group">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" value="${user.email}" required>
                </div>

                <div class="form-group">
                    <label for="phoneNumber">Số điện thoại:</label>
                    <input type="text" id="phoneNumber" name="phoneNumber" value="${user.phoneNumber}">
                </div>

                <button type="submit" style="padding: 10px 20px; background-color: #007bff; color: white; border: none; cursor: pointer;">Lưu Hồ Sơ</button>
            </form>
        </c:when>
        <c:otherwise>
            <%-- Display fallback message if no user object is present --%>
            <p>Không thể tải thông tin hồ sơ.</p>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>