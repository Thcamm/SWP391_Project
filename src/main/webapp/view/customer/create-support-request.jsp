<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/create-support-request.css">

</head>
<body>

<div class="container">
    <%@ include file="/common/header.jsp" %>
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
    <%@ include file="/common/footer.jsp" %>
</div>

</body>
</html>
