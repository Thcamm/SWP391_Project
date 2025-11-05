<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Service Request Manager</title>
    <!-- Bootstrap 5 for styling -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet" />
</head>
<body class="bg-light">

<jsp:include page="/view/customerservice/result.jsp" />
<div class="container-fluid mt-4">
    <div class="card">
        <div class="card-header">
            <h3 class="mb-0"><i class="bi bi-card-list"></i> Service Request Management</h3>
        </div>
        <div class="card-body">

            <%-- Display success or error messages --%>
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success alert-dismissible fade show">
                        ${sessionScope.successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show">
                        ${sessionScope.errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <%-- Table to display all service requests --%>
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>Request ID</th>
                        <th>Date</th>
                        <th>Customer</th>
                        <th>Vehicle</th>
                        <th>Service</th>
                        <th>Price</th>
                        <th style="width: 250px;">Status</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty serviceRequestList}">
                            <c:forEach var="item" items="${serviceRequestList}">
                                <tr>
                                    <td>#${item.requestId}</td>
                                    <td><fmt:formatDate value="${item.requestDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td><c:out value="${item.customerName}"/></td>
                                    <td><c:out value="${item.vehicleInfo}"/></td>
                                    <td><c:out value="${item.serviceName}"/></td>
                                    <td><fmt:formatNumber value="${item.servicePrice}" type="currency" currencySymbol=""/> VND</td>
                                    <td>
                                        <form action="${pageContext.request.contextPath}/customerservice/requests" method="POST" class="d-flex align-items-center">
                                            <input type="hidden" name="requestId" value="${item.requestId}" />

                                            <select name="newStatus" class="form-select form-select-sm me-2">
                                                <option value="PENDING_REVIEW" ${item.status == 'PENDING_REVIEW' ? 'selected' : ''}>Pending Review</option>
                                                <option value="APPROVED" ${item.status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                                                <option value="CANCELLED" ${item.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                            </select>

                                            <button type="submit" class="btn btn-primary btn-sm">
                                                <i class="bi bi-check-lg"></i>
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="text-center text-muted p-4">
                                    <h5><i class="bi bi-info-circle"></i> No service requests found.</h5>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

