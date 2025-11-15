<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Diagnostic Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <!-- Custom CSS -->
    <style>
        body {
            background: #f4f6f9;
            font-size: 0.95rem;
            color: #111827;
        }

        .page-wrapper {
            max-width: 1100px;
        }

        h3.page-title {
            font-weight: 600;
            letter-spacing: 0.02em;
        }

        .flash-alert {
            border-radius: 0.75rem;
            box-shadow: 0 2px 6px rgba(15, 23, 42, 0.12);
        }

        .card-main {
            border-radius: 0.9rem;
            box-shadow: 0 4px 10px rgba(15, 23, 42, 0.08);
            border: none;
        }

        .card-main .card-header {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            color: #ffffff;
            font-weight: 600;
        }

        .card-main .card-body {
            background: #ffffff;
        }

        .diag-title {
            font-weight: 600;
            font-size: 1.05rem;
        }

        .diag-label {
            font-weight: 600;
        }

        .diag-summary p {
            margin-bottom: 0.25rem;
        }

        .diag-summary p:last-child {
            margin-bottom: 0;
        }

        .status-text {
            font-weight: 500;
        }

        .badge-part-condition {
            font-size: 0.8rem;
            padding: 0.4em 0.6em;
            border-radius: 999px;
        }

        .table-parts {
            margin-bottom: 0;
        }

        .table-parts thead {
            background: #f3f4f6;
            font-size: 0.85rem;
        }

        .table-parts thead th {
            text-transform: uppercase;
            letter-spacing: 0.03em;
            border-bottom-width: 1px;
        }

        .table-parts tbody tr td {
            vertical-align: middle;
        }

        .table-parts tbody tr:hover {
            background: #f9fafb;
        }

        .part-name {
            font-weight: 600;
        }

        .part-meta {
            font-size: 0.8rem;
        }

        .btn-toggle-part {
            min-width: 90px;
            font-size: 0.8rem;
        }

        .decision-card .card-header {
            background: #f9fafb;
            font-weight: 600;
        }

        .decision-text {
            font-size: 0.9rem;
        }

        .btn-approve-main {
            min-width: 190px;
        }

        .btn-back {
            border-radius: 999px;
            padding-inline: 1.25rem;
        }

        @media (max-width: 768px) {
            .btn-toggle-part {
                width: 100%;
            }

            .btn-approve-main {
                width: 100%;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/common/header.jsp"/>

<c:set var="result" value="${requestScope.result}" />
<c:set var="detail" value="${result.data}" />
<c:set var="diag" value="${detail.diag}" />

<div class="container page-wrapper py-4"  style="background:#ffffff; border-radius:8px; padding:12px 16px;
            color:#111 !important">

    <h3 class="mb-3 page-title">Diagnostic Details</h3>

    <!-- Flash message -->
    <c:if test="${not empty sessionScope.flash}">
        <c:set var="flash" value="${sessionScope.flash}" />
        <c:remove var="flash" scope="session"/>

        <div class="alert ${flash.success ? 'alert-success' : 'alert-danger'} flash-alert">
                ${flash.message}
        </div>
    </c:if>

    <!-- Diagnostic info -->
    <div class="card card-main mb-4">
        <div class="card-body">
            <h5 class="card-title diag-title">
                Issue: ${diag.issueFound}
            </h5>
            <div class="diag-summary">
                <p>
                    <span class="diag-label">Status:</span>
                    <span class="status-text"> ${diag.statusString}</span>
                    <c:if test="${diag.rejected}">
                        <br/>
                        <span class="diag-label">Rejection reason:</span>
                        ${diag.rejectReason}
                    </c:if>
                </p>
                <p>
                    <span class="diag-label">Diagnostic labor:</span>
                    ${diag.estimateCostFormatted}
                </p>
                <p>
                    <span class="diag-label">Total cost of approved parts:</span>
                    ${diag.approvedPartsCost}
                </p>
                <p>
                    <span class="diag-label">Total estimate:</span>
                    ${diag.totalEstimateFormatted}
                </p>
            </div>
        </div>
    </div>

    <!-- Suggested parts -->
    <div class="card mb-4 card-main">
        <div class="card-header">
            <b>Suggested parts</b>
        </div>
        <div class="card-body">

            <c:if test="${empty detail.parts}">
                <p class="text-muted mb-0">There are no parts in this diagnostic.</p>
            </c:if>

            <c:if test="${not empty detail.parts}">
                <table class="table table-sm align-middle table-parts">
                    <thead>
                    <tr>
                        <th>Part</th>
                        <th>Type</th>
                            <%-- <th>Stock</th> --%>
                        <th class="text-end">Qty</th>
                        <th class="text-end">Unit price</th>
                        <th class="text-end">Amount</th>
                        <th class="text-center">Customer approved?</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="p" items="${detail.parts}">
                        <tr>
                            <td>
                                <div class="part-name">${p.partName}</div>
                                <div class="text-muted part-meta">
                                    Code: ${p.partCode}
                                    <c:if test="${not empty p.sku}">
                                        - SKU: ${p.sku}
                                    </c:if>
                                </div>
                                <c:if test="${not empty p.reasonForReplacement}">
                                    <div class="part-meta">
                                        Reason: ${p.reasonForReplacement}
                                    </div>
                                </c:if>
                            </td>
                            <td>
                                <span class="badge badge-part-condition ${p.partCondition.badgeClass}">
                                        ${p.partCondition.displayText}
                                </span>
                            </td>
                                <%--
                                <td>
                                    <span class="${p.stockStatusClass}">
                                        ${p.stockStatusText}
                                    </span>
                                    <div class="small text-muted">
                                        Stock: ${p.currentStock}
                                    </div>
                                </td>
                                --%>
                            <td class="text-end">${p.quantityNeeded}</td>
                            <td class="text-end">${p.unitPrice}</td>
                            <td class="text-end">${p.totalPrice}</td>
                            <td class="text-center">
                                <!-- Form to toggle approve/unapprove for each part -->
                                <form method="post"
                                      action="${pageContext.request.contextPath}/customer/diagnostic/part"
                                      class="d-inline">
                                    <input type="hidden" name="diagnosticPartId"
                                           value="${p.diagnosticPartID}"/>
                                    <input type="hidden" name="diagnosticId"
                                           value="${diag.vehicleDiagnosticID}"/>
                                    <input type="hidden" name="requestId"
                                           value="${param.requestId}"/>
                                    <!-- If currently approved, send approved=false, otherwise send true -->
                                    <input type="hidden" name="approved"
                                           value="${p.approved ? 'false' : 'true'}"/>

                                    <button type="submit"
                                            class="btn btn-sm ${p.approved ? 'btn-outline-danger' : 'btn-outline-success'} btn-toggle-part"
                                        ${diag.submitted ? '' : 'disabled'}>
                                        <c:choose>
                                            <c:when test="${p.approved}">
                                                Unselect
                                            </c:when>
                                            <c:otherwise>
                                                Select
                                            </c:otherwise>
                                        </c:choose>
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>
    </div>

    <!-- Final decision section -->
    <div class="card mb-4 decision-card card-main">
        <div class="card-header">
            Final decision
        </div>
        <div class="card-body">

            <c:if test="${!diag.submitted}">
                <p class="text-muted mb-0 decision-text">
                    This diagnostic has already been finalized and can no longer be changed.
                </p>
            </c:if>

            <c:if test="${diag.submitted}">
                <p class="text-muted decision-text">
                    After you click <b>Accept diagnostic</b>, the system will create corresponding repair
                    jobs based on the parts you have approved.
                </p>

                <div class="d-flex flex-column flex-md-row gap-3">
                    <!-- APPROVE form -->
                    <c:if test="${detail.canApprove}">
                        <form method="post"
                              action="${pageContext.request.contextPath}/customer/diagnostic/finalize">
                            <input type="hidden" name="diagnosticId"
                                   value="${diag.vehicleDiagnosticID}"/>
                            <input type="hidden" name="requestId"
                                   value="${param.requestId}"/>
                            <input type="hidden" name="approve" value="true"/>

                            <button type="submit" class="btn btn-success btn-approve-main">
                                Accept diagnostic
                            </button>
                        </form>
                    </c:if>

                    <!-- REJECT form -->
                    <c:if test="${detail.canReject}">
                        <form method="post"
                              action="${pageContext.request.contextPath}/customer/diagnostic/finalize"
                              class="flex-fill">
                            <input type="hidden" name="diagnosticId"
                                   value="${diag.vehicleDiagnosticID}"/>
                            <input type="hidden" name="requestId"
                                   value="${param.requestId}"/>
                            <input type="hidden" name="approve" value="false"/>

                            <div class="mb-2">
                                <label for="rejectReason" class="form-label">
                                    Rejection reason (optional)
                                </label>
                                <textarea id="rejectReason" name="rejectReason"
                                          class="form-control" rows="2"
                                          placeholder="Example: Cost is too high, want to postpone..."></textarea>
                            </div>

                            <button type="submit" class="btn btn-outline-danger">
                                Reject diagnostic
                            </button>
                        </form>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>

    <a href="${pageContext.request.contextPath}/customer/diagnostic/tree?requestId=${param.requestId}"
       class="btn btn-outline-secondary btn-back">
        &laquo; Back to diagnostics list
    </a>
</div>

<jsp:include page="/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
