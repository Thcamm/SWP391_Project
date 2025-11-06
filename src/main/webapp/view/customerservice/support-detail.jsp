<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           align-items: center; justify-content: center;
                          ">
                    <!-- Nội dung trang home của bạn ở đây -->
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

                            <c:forEach var="file" items="${fn:split(supportRequest.attachmentPath, ';')}">
                                <p>
                                    <a href="${pageContext.request.contextPath}/customerservice/view-attachment?file=${file}" target="_blank">
                                            ${file}
                                    </a>
                                </p>
                            </c:forEach>



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
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>