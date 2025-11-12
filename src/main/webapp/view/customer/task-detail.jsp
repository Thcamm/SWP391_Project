<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Task Diagnostics</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="p-4">
<div class="container">
    <h2>🩺 Diagnostics for Task #${param.assignmentId}</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty diagnostics}">
            <div class="alert alert-info mt-4">
                No diagnostics have been submitted for this task yet.
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-striped mt-4">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Issue Found</th>
                    <th>Technician</th>
                    <th>Estimate</th>
                    <th>Status</th>
                    <th>Created At</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="diag" items="${diagnostics}">
                    <tr>
                        <td>#${diag.vehicleDiagnosticID}</td>
                        <td>
                            <c:choose>
                                <c:when test="${fn:length(diag.issueFound) > 50}">
                                    ${fn:substring(diag.issueFound, 0, 50)}...
                                </c:when>
                                <c:otherwise>${diag.issueFound}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>${diag.technicianName}</td>
                        <td>
                            <fmt:formatNumber value="${diag.estimateCost}" type="currency" currencySymbol="$"/>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${diag.statusString == 'SUBMITTED'}">
                                    <span class="badge bg-warning text-dark">SUBMITTED</span>
                                </c:when>
                                <c:when test="${diag.statusString == 'APPROVED'}">
                                    <span class="badge bg-success">APPROVED</span>
                                </c:when>
                                <c:when test="${diag.statusString == 'REJECTED'}">
                                    <span class="badge bg-danger">REJECTED</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">UNKNOWN</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${diag.createdAtFormatted}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/customer/diagnostic/view?diagnosticId=${diag.vehicleDiagnosticID}"
                               class="btn btn-sm btn-outline-primary">👀 View</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
