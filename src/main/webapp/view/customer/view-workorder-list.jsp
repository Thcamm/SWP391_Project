<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Work Order</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <h3 class="mb-4 text-center text-dark">Danh sách Work Order</h3>
    <table class="table table-bordered table-striped shadow-sm">
        <thead class="table-dark">
        <tr>
            <th>#</th>
            <th>Mã Work Order</th>
            <th>Trạng thái</th>
            <th>Ước tính</th>
            <th>Ngày tạo</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="wo" items="${workOrders}" varStatus="i">
            <tr>
                <td>${i.index + 1}</td>
                <td>${wo.workOrderId}</td>
                <td>${wo.status}</td>
                <td>${wo.estimateAmount}</td>
                <td>${wo.createdAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/customer/send-feedback?workOrderID=${wo.workOrderId}"
                       class="btn btn-sm btn-primary">
                        Gửi Feedback
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
