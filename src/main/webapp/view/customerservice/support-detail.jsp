<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/view/customerservice/sidebar.jsp" />

<div class="container py-4">
    <h3>Support Request Detail</h3>

    <div class="card p-4 mb-4">
        <p><strong>Request ID:</strong> ${supportRequest.requestId}</p>
        <p><strong>Customer:</strong> ${customer.fullName} (${customer.email})</p>
        <p><strong>Category:</strong> ${categoryMap[supportRequest.categoryId]}</p>
        <p><strong>Status:</strong> ${supportRequest.status}</p>
        <p><strong>Created At:</strong> ${supportRequest.createdAt}</p>
        <p><strong>Updated At:</strong> ${supportRequest.updatedAt}</p>

        <c:if test="${supportRequest.appointmentId != null}">
            <p><strong>Appointment ID:</strong> ${supportRequest.appointmentId}</p>
        </c:if>

        <c:if test="${supportRequest.workOrderId != null}">
            <p><strong>Work Order ID:</strong> ${supportRequest.workOrderId}</p>
        </c:if>

        <p><strong>Description:</strong></p>
        <p class="border p-2 bg-white">${supportRequest.description}</p>

        <c:if test="${not empty supportRequest.attachmentPath}">
            <p><strong>Attachment:</strong>
                <a href="${pageContext.request.contextPath}/customerservice/view-attachment?file=${supportRequest.attachmentPath}" target="_blank">
                    View in Browser
                </a>
            </p>
        </c:if>


        <div class="mt-3">
            <c:choose>
                <c:when test="${supportRequest.status == 'INPROGRESS'}">
                    <a href="${pageContext.request.contextPath}/customerservice/reply-request?id=${supportRequest.requestId}&email=${customer.email}"
                       class="btn btn-success">Reply</a>
                </c:when>
                <c:otherwise>
                    <button class="btn btn-success" disabled style="opacity:0.5;">Reply</button>
                </c:otherwise>
            </c:choose>

            <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="btn btn-secondary ms-2">Back</a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
