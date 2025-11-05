<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Feedback vừa gửi</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-dark text-light">
<div class="container mt-5">
    <h3 class="text-center mb-4">Feedback vừa gửi</h3>

    <c:if test="${not empty feedbacks}">
        <table class="table table-dark table-striped table-bordered shadow-sm">
            <thead class="table-secondary text-dark">
            <tr>
                <th>#</th>
                <th>Work Order ID</th>
                <th>Đánh giá</th>
                <th>Bình luận</th>
                <th>Ngày gửi</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="fb" items="${feedbacks}" varStatus="i">
                <tr class="table-success">
                    <td>${i.index + 1}</td>
                    <td>${fb.workOrderID}</td>
                    <td>
                        <c:forEach begin="1" end="${fb.rating}">
                            <i class="fa fa-star text-warning"></i>
                        </c:forEach>
                    </td>
                    <td>${fb.feedbackText}</td>
                    <td>${fb.feedbackDate}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="mt-4 d-flex justify-content-center gap-3">
        <!-- Back to Work Order -->
        <a href="${pageContext.request.contextPath}/customer/workorder-list" class="btn btn-outline-light">
            <i class="fa fa-arrow-left"></i> Quay lại Work Order
        </a>

        <!-- View all feedback -->
        <a href="${pageContext.request.contextPath}/customer/view-feedback-list" class="btn btn-outline-warning">
            <i class="fa fa-eye"></i> Xem tất cả Feedback
        </a>
    </div>
</div>
</body>
</html>
