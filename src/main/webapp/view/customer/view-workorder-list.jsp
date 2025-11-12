<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Work Order List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="/common/header.jsp" />

<div class="container py-5">
    <h2 class="mb-4 text-center">List of Work Orders Eligible for Feedback</h2>

    <table class="table table-bordered table-hover align-middle">
        <thead class="table-dark text-center">
        <tr>
            <th style="width: 60px;">#</th>
            <th>Work Order ID</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${feedbackViewList.paginatedData}" varStatus="loop">
            <tr>
                <td class="text-center">${loop.index + 1}</td>
                <td class="text-center">${item.workOrder.workOrderId}</td>
                <td class="text-center">
                    <c:choose>

                        <c:when test="${item.feedbackAction == 'ALLOW_FEEDBACK'}">
                            <a href="${pageContext.request.contextPath}/customer/send-feedback?workOrderID=${item.workOrder.workOrderId}"
                               class="btn btn-success btn-sm">
                                Send Feedback
                            </a>
                        </c:when>

                        <c:when test="${item.feedbackAction == 'HAS_FEEDBACK'}">
                            <a href="${pageContext.request.contextPath}/customer/view-feedback?feedbackId=${item.feedback.feedbackID}"
                               class="btn btn-primary btn-sm">
                                <i class="fa fa-eye"></i> View Feedback
                            </a>
                        </c:when>

                        <c:when test="${item.feedbackAction == 'EXPIRED'}">
                            <span class="badge bg-secondary">Expired</span>
                        </c:when>


                        <c:otherwise>
                            <span class="text-muted">Unavailable</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <jsp:include page="/view/customerservice/pagination.jsp">
        <jsp:param name="currentPage" value="${feedbackViewList.currentPage}" />
        <jsp:param name="totalPages" value="${feedbackViewList.totalPages}" />
        <jsp:param name="baseUrl" value="/customer/workorder-list" />
        <jsp:param name="queryString" value="" />
    </jsp:include>
</div>

<jsp:include page="/common/footer.jsp" />
</body>
</html>
