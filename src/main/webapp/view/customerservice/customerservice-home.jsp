<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Service Page</title>
</head>
<body>
<div class="container">
    <h1>Customer Service Management</h1>

    <div class="card-grid">
        <a href="${pageContext.request.contextPath}/customerservice/appointment-list" class="card">
            <i class="fa-solid fa-calendar-check"></i>
            Appointment List
        </a>
        <a href="${pageContext.request.contextPath}/customerservice/search-customer" class="card">
            <i class="fa-solid fa-magnifying-glass"></i>
            Search Customer
        </a>
        <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="card">
            <i class="fa-solid fa-headset"></i>
            Support Requests
        </a>
        <a href="${pageContext.request.contextPath}/customerservice/requests" class="card">
        <i class="fa-solid fa-headset"></i>
        View Service Requests
    </a>
    </div>
</div>
<jsp:include page="/view/customerservice/sidebar.jsp" />
</body>
</html>
