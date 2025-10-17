<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Service Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/create-customer.css">

    <script src="https://kit.fontawesome.com/a2d5b5f5e6.js" crossorigin="anonymous"></script>
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
    </div>
</div>
</body>
</html>
