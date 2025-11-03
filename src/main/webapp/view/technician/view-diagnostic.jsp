<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/task-detail.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/view-diagnostic.css"/>


<div class="layout">
    <jsp:include page="sidebar.jsp"/>

    <main class="main">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="${pageContext.request.contextPath}/technician/home">Home</a>
            <span>/</span>
            <a href="${pageContext.request.contextPath}/technician/diagnostics">Diagnostics</a>
            <span>/</span>
            <span>View #<c:out value="${diagnostic.vehicleDiagnosticID}"/></span>
        </div>

        <!-- Header -->
        <div class="page-header d-flex align-items-center justify-content-between">
            <h1 class="page-title">🩺 Diagnostic #<c:out value="${diagnostic.vehicleDiagnosticID}"/></h1>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-secondary"
                   href="${pageContext.request.contextPath}/technician/task-detail?assignmentId=${diagnostic.assignmentID}">
                    ← Back to Task
                </a>
            </div>
        </div>

        <!-- Summary -->
        <div class="card mb-3">
            <div class="card-header bg-info">📋 Diagnostic Info</div>
            <div class="card-body">
                <div class="row">
                    <div class="col">
                        <p class="mb-2"><strong>ID:</strong> #<c:out value="${diagnostic.vehicleDiagnosticID}"/></p>
                        <p class="mb-2"><strong>Assignment:</strong> #<c:out value="${diagnostic.assignmentID}"/></p>
                        <p class="mb-2"><strong>Vehicle:</strong>
                            <span class="badge badge-secondary"><c:out value="${diagnostic.vehicleInfo}"/></span>
                        </p>
                    </div>
                    <div class="col">
                        <p class="mb-2"><strong>Technician:</strong> <c:out value="${diagnostic.technicianName}"/></p>
                        <p class="mb-2"><strong>Created:</strong>
                            <c:choose>
                                <c:when test="${not empty diagnostic.createdAt}">
                                    <c:out value="${diagnostic.createdAtFormatted}" />
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </p>
                        <p class="mb-2"><strong>Status:</strong>
                            <span class="badge ${diagnostic.status ? 'badge-primary' : 'badge-secondary'}">
                <c:out value="${diagnostic.status ? 'ACTIVE' : 'INACTIVE'}"/>
              </span>
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Issue Found -->
        <div class="card mb-3">
            <div class="card-header bg-primary">🤖 Issue Found</div>
            <div class="card-body">
                <pre style="white-space:pre-wrap;margin:0"><c:out value="${diagnostic.issueFound}"/></pre>
            </div>
        </div>

        <!-- Parts -->
        <div class="card">
            <div class="card-header bg-success d-flex align-items-center justify-content-between">
                <span>🐓 Parts</span>
                <small class="text-white-50">
                    <c:out value="${empty parts ? 0 : fn:length(parts)}"/> item(s)
                </small>
            </div>

            <div class="card-body p-0">
                <c:set var="partsTotal" value="0"/>
                <c:choose>
                    <c:when test="${empty parts}">
                        <div class="p-4 text-muted">No parts linked to this diagnostic.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table table-striped align-middle mb-0">
                                <thead>
                                <tr>
                                    <th style="width:80px;">#</th>
                                    <th>Part</th>
                                    <th style="width:140px;">SKU</th>
                                    <th class="text-end" style="width:90px;">Qty</th>
                                    <th class="text-end" style="width:140px;">Unit Price</th>
                                    <th class="text-end" style="width:160px;">Line</th>
                                    <th style="width:140px;">Condition</th>
                                    <th style="width:130px;">Approval</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${parts}" var="p" varStatus="st">
                                    <c:set var="price" value="${empty p.unitPrice ? 0 : p.unitPrice}"/>
                                    <c:set var="line"  value="${price * p.quantityNeeded}"/>
                                    <c:set var="partsTotal" value="${partsTotal + line}"/>

                                    <tr>
                                        <td>#<c:out value="${st.index+1}"/></td>
                                        <td>
                                            <div><c:out value="${p.partName}"/></div>
                                            <small class="text-muted"><c:out value="${p.partCode}"/></small>
                                        </td>
                                        <td><c:out value="${p.sku}"/></td>
                                        <td class="text-end"><c:out value="${p.quantityNeeded}"/></td>
                                        <td class="text-end">
                                            <fmt:formatNumber value="${price}" pattern="#,##0.00"/>
                                        </td>
                                        <td class="text-end">
                                            <fmt:formatNumber value="${line}" pattern="#,##0.00"/>
                                        </td>
                                        <td>
                                            <c:out value="${p.partCondition}"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${p.approved}">
                                                    <span class="badge bg-success">APPROVED</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-warning text-dark">PENDING</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    <c:if test="${not empty p.reasonForReplacement}">
                                        <tr class="bg-light">
                                            <td></td>
                                            <td colspan="7">
                                                <small class="text-muted">Reason: </small>
                                                <small><c:out value="${p.reasonForReplacement}"/></small>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Totals -->
            <!-- Totals -->
            <div class="card-footer">
                <div class="row">
                    <div class="col-md-6"></div>
                    <div class="col-md-6">
                        <table class="table table-sm mb-0">
                            <tr>
                                <th class="text-end">Parts Subtotal:</th>
                                <td class="text-end">
                                    <fmt:formatNumber value="${partsTotal}" pattern="#,##0.00"/>
                                </td>
                            </tr>
                            <tr>
                                <th class="text-end">Labor Cost:</th>
                                <td class="text-end">
                                    <fmt:formatNumber value="${diagnostic.laborCostCalculated}" pattern="#,##0.00"/>
                                </td>
                            </tr>
                            <tr>
                                <th class="text-end">Total Estimate:</th>
                                <td class="text-end fw-bold">
                                    <fmt:formatNumber value="${partsTotal + (empty diagnostic.laborCostCalculated ? 0 : diagnostic.laborCostCalculated)}"
                                                      pattern="#,##0.00"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Footer actions -->
        <div class="mt-3 d-flex justify-content-end gap-2">
            <a class="btn btn-outline-secondary"
               href="${pageContext.request.contextPath}/technician/task-detail?assignmentId=${diagnostic.assignmentID}">
                ← Back to Task
            </a>
        </div>
    </main>
</div>

<jsp:include page="footer.jsp"/>
