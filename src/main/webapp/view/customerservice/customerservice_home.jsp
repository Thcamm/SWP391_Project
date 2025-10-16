<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Customer Service Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 0;
        }
        .container {
            max-width: 1000px;
            margin: 60px auto;
            background: #fff;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            padding: 40px;
            text-align: center;
        }
        h1 {
            margin-bottom: 30px;
            color: #333;
        }
        .card-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
        }
        .card {
            background-color: #007bff;
            color: white;
            border-radius: 10px;
            padding: 25px;
            text-decoration: none;
            font-size: 18px;
            font-weight: bold;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            transition: all 0.25s ease-in-out;
        }
        .card:hover {
            background-color: #0056b3;
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }
        .card i {
            font-size: 30px;
            margin-bottom: 10px;
        }
    </style>
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
