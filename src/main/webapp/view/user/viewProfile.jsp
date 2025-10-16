<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/viewProfile.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />
<div class="container profile-content">
    <h2>User Profile</h2>

    <c:if test="${not empty sessionScope.success}">
        <div class="message success">${sessionScope.success}</div>
        <c:remove var="success" scope="session" />
    </c:if>
    <c:if test="${not empty error}">
        <div class="message error">${error}</div>
    </c:if>

    <table class="profile-table">
        <tr><th>Full Name</th><td><c:out value="${user.fullName}"/></td></tr>
        <tr><th>Email</th><td><c:out value="${user.email}"/></td></tr>
        <tr><th>Phone Number</th><td><c:out value="${user.phoneNumber}"/></td></tr>
        <tr><th>Address</th><td><c:out value="${user.address}"/></td></tr>
        <tr><th>Gender</th><td><c:out value="${user.gender}"/></td></tr>
        <tr><th>Birthdate</th><td><c:out value="${user.birthDate}"/></td></tr>
    </table>

    <div class="action-buttons">
        <form action="${pageContext.request.contextPath}/user/profile" method="get" style="display:inline;">
            <input type="hidden" name="action" value="edit"/>
            <button type="submit" class="btn btn-primary">Edit Profile</button>
        </form>
        <a href="${pageContext.request.contextPath}/user/changePassword" class="btn btn-secondary">Change Password</a>
    </div>

    <h2>Service History</h2>
    <table class="service-table">
        <thead>
        <tr><th>Service ID</th><th>Service Name</th><th>Date</th><th>Status</th><th>Price</th></tr>
        </thead>
        <tbody id="serviceBody"></tbody>
    </table>
    <div class="pagination">
        <div class="pagination-info">
            Showing <span id="start">0</span> to <span id="end">0</span> of <span id="total">0</span> records
        </div>
        <div class="pagination-controls">
            <button onclick="prevPage()" id="prevBtn" disabled>Previous</button>
            <div id="pageNumbers"></div>
            <button onclick="nextPage()" id="nextBtn" disabled>Next</button>
        </div>
    </div>
</div>
<jsp:include page="/common/footer.jsp" />
<script src="${pageContext.request.contextPath}/assets/js/user/viewProfile.js"></script>
</body>
</html>