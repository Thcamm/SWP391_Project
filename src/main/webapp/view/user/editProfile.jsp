<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Edit Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/editProfile.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h2>Edit Profile</h2>
    </div>

    <div class="form-container">
        <c:if test="${not empty error}">
            <div class="error-message">${error}</div>
        </c:if>

        <form action="profile" method="post" id="editProfileForm">
            <table class="form-table">
                <tr>
                    <td>Full Name <span class="required">*</span>:</td>
                    <td><input type="text" name="fullName" value="<c:out value='${user.fullName}'/>" class="form-input" required /></td>
                </tr>
                <tr>
                    <td>Email <span class="required">*</span>:</td>
                    <td><input type="email" name="email" value="<c:out value='${user.email}'/>" class="form-input" required /></td>
                </tr>
                <tr>
                    <td>Phone Number:</td>
                    <td><input type="text" name="phoneNumber" value="<c:out value='${user.phoneNumber}'/>" class="form-input" /></td>
                </tr>
                <tr>
                    <td>Address:</td>
                    <td><input type="text" name="address" value="<c:out value='${user.address}'/>" class="form-input" /></td>
                </tr>
                <tr>
                    <td>Gender:</td>
                    <td>
                        <select name="gender" class="form-input">
                            <option value="">Select Gender</option>
                            <option value="male" ${user.gender == 'male' ? 'selected' : ''}>Male</option>
                            <option value="female" ${user.gender == 'female' ? 'selected' : ''}>Female</option>
                            <option value="other" ${user.gender == 'other' ? 'selected' : ''}>Other</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td>Birth Date:</td>
                    <td><input type="date" name="birthDate" value="${user.birthDate}" class="form-input" /></td>
                </tr>
            </table>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">Save Changes</button>
                <a href="${pageContext.request.contextPath}/profile" class="btn btn-secondary">Back</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>