<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            padding: 30px;
        }
        .container {
            max-width: 700px;
            margin: auto;
            background: #fff;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        h2 { color: #6a1b9a; margin-bottom: 20px; text-align: center; }
        label { font-weight: bold; margin-top: 10px; display: block; }
        input, select, textarea {
            width: 100%; padding: 10px; margin-top: 5px;
            border: 1px solid #ccc; border-radius: 6px;
        }
        button {
            margin-top: 20px; width: 100%;
            padding: 12px; border: none;
            background-color: #6a1b9a; color: white;
            font-weight: bold; border-radius: 6px;
            cursor: pointer;
        }
        button:hover { background-color: #50127a; }
        .message { text-align: center; color: green; font-weight: bold; }
    </style>
</head>
<body>
<div class="container">
    <h2>Support Request / Report a Bug</h2>

    <form action="${pageContext.request.contextPath}/customer/create-support-request" method="post" enctype="multipart/form-data">
        <label>Email</label>
        <input type="email" name="email" value="${sessionScope.user.email}" required>

        <label>Phone Number</label>
        <input type="text" name="phone" value="${sessionScope.user.phoneNumber}" required>

        <label>Category</label>
        <select name="categoryId" required>
            <option value="">-- Select option --</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.categoryId}">${cat.categoryName}</option>
            </c:forEach>
        </select>

        <label>Order code (if any)</label>
        <input type="text" name="workOrderId" placeholder="Enter Work Order Code ">
        <label>Appointment code (if any)</label>
        <input type="text" name="appointmentId" placeholder="Enter Appointment Code">

        <label>Detail</label>
        <textarea name="description" rows="5" placeholder="Enter the content of your support request..." required></textarea>

        <label>
            Attached photo (if any)</label>
        <input type="file" name="attachment">

        <button type="submit">Send request</button>
    </form>
    <a href="support-faq" class="back-link">Return FAQ</a>
    <c:if test="${not empty message}">
        <p class="message">${message}</p>
    </c:if>
</div>
</body>
</html>
