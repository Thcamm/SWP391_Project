<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Diagnostic Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<main class="container mt-5 mb-5">
    <div class="card shadow-lg p-4">
        <h2 class="mb-4">🔧 Vehicle Diagnostic #${diagnostic.vehicleDiagnosticID}</h2>

        <div class="mb-3">
            <p><strong>Issue Found:</strong> ${diagnostic.issueFound}</p>
            <p><strong>Status:</strong>
                <span class="badge
                    <c:choose>
                        <c:when test="${diagnostic.statusString eq 'APPROVED'}">bg-success</c:when>
                        <c:when test="${diagnostic.statusString eq 'REJECTED'}">bg-danger</c:when>
                        <c:otherwise>bg-secondary</c:otherwise>
                    </c:choose>">
                    ${diagnostic.statusString}
                </span>
            </p>
            <p><strong>Created at:</strong> ${diagnostic.createdAtFormatted}</p>
            <p><strong>Total Estimated Cost:</strong>
                <fmt:formatNumber value="${diagnostic.totalEstimate}" type="currency" currencySymbol="$"/>
            </p>
        </div>

        <hr>

        <h4 class="mb-3">🧩 Diagnostic Parts</h4>

        <table class="table table-bordered align-middle">
            <thead class="table-light">
            <tr>
                <th>#</th>
                <th>Part</th>
                <th>Condition</th>
                <th>Qty</th>
                <th>Unit Price</th>
                <th>Total</th>
                <th>Approved</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="p" items="${diagnostic.parts}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td>${p.partDetailName}</td>
                    <td>${p.partCondition}</td>
                    <td>${p.quantityNeeded}</td>
                    <td>$${p.unitPrice}</td>
                    <td>$<fmt:formatNumber value="${p.unitPrice * p.quantityNeeded}" type="number" minFractionDigits="2"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.approved}">V</c:when>
                            <c:otherwise>X</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <form method="post" action="${pageContext.request.contextPath}/customer/diagnostic/part-approval">
                            <input type="hidden" name="partId" value="${p.diagnosticPartID}">
                            <input type="hidden" name="diagnosticId" value="${diagnostic.vehicleDiagnosticID}">
                            <button name="approved" value="true" class="btn btn-success btn-sm"
                                ${diagnostic.statusString ne 'SUBMITTED' ? 'disabled' : ''}>Approve</button>
                            <button name="approved" value="false" class="btn btn-danger btn-sm"
                                ${diagnostic.statusString ne 'SUBMITTED' ? 'disabled' : ''}>Reject</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>

        <c:if test="${diagnostic.statusString eq 'SUBMITTED'}">
            <hr>
            <h4>Finalize Decision</h4>

            <c:set var="approvedCount" value="${diagnostic.approvedPartsCount}"/>

            <form method="post" action="${pageContext.request.contextPath}/customer/diagnostic/view">
                <input type="hidden" name="diagnosticId" value="${diagnostic.vehicleDiagnosticID}">
                <c:choose>
                    <c:when test="${approvedCount > 0}">
                        <button type="submit" name="action" value="approve" class="btn btn-success btn-lg mt-3">
                            Approve Diagnostic
                        </button>
                    </c:when>
                    <c:otherwise>
                        <div class="mb-3">
                            <label for="reason" class="form-label">Reason for rejection:</label>
                            <textarea name="reason" id="reason" class="form-control" rows="3"
                                      placeholder="Enter reason for rejecting this diagnostic"></textarea>
                        </div>
                        <button type="submit" name="action" value="reject" class="btn btn-danger btn-lg">
                            Reject Diagnostic
                        </button>
                    </c:otherwise>
                </c:choose>
            </form>
        </c:if>

        <c:if test="${diagnostic.statusString ne 'SUBMITTED'}">
            <div class="alert alert-info mt-3">
                This diagnostic has been <strong>${diagnostic.statusString}</strong>.
            </div>
        </c:if>

        <a href="${pageContext.request.contextPath}/customer/requests" class="btn btn-outline-secondary mt-4">
            ← Back to My Requests
        </a>
    </div>
</main>

<jsp:include page="/common/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
