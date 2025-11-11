<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/task-detail.css">


<jsp:include page="header.jsp"/>
<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main">
                <div class="task-detail-page">
                    <!-- Page Header -->
                    <div class="page-header">
                        <h2>🔍 Task Details #${task.assignmentID}</h2>
                        <a href="${returnTo}" class="btn-back">← Back to Tasks</a>
                    </div>

                    <jsp:include page="message-display.jsp"/>

                    <!-- Task Information Card -->
                    <div class="task-info-card">
                        <h3>📋 Task Information</h3>
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">Task ID</div>
                                <div class="info-value">#${task.assignmentID}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Status Working</div>
                                <div class="info-value">
                                    <span class="status-badge ${task.status}">${task.status}</span>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Priority</div>
                                <div class="info-value">
                                    <span class="priority-badge ${task.priority}">${task.priority}</span>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Vehicle</div>
                                <div class="info-value">${task.vehicleInfo}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Customer</div>
                                <div class="info-value">${task.customerName}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Service</div>
                                <div class="info-value">${task.serviceInfo}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Assigned Date</div>
                                <div class="info-value">
                                    <c:out value="${task.assignedDateFormatted}"/>
                                </div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Progress</div>
                                <div class="info-value">
                                    <%--                            <c:choose>--%>
                                    <%--                                <c:when test="${task.status == 'IN_PROGRESS'}">--%>
                                    <%--                                    --%>
                                    <%--                                </c:when>--%>
                                    <%--                                <c:otherwise>-</c:otherwise>--%>
                                    <%--                            </c:choose>--%>

                                    ${task.progressPercentage}%
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Diagnostics Section -->
                    <div class="diagnostics-section">
                        <div class="section-header">
                            <h3>🩺 Vehicle Diagnostics (${vm.page.totalItems})</h3>
                            <c:if test="${task.status == 'IN_PROGRESS'}">
                                <a href="${pageContext.request.contextPath}/technician/create-diagnostic?assignmentId=${task.assignmentID}"
                                   class="btn-create">
                                    🩺 Create New Diagnostic
                                </a>
                            </c:if>
                        </div>

                        <c:choose>
                        <c:when test="${empty vm.page.data}">
                            <div class="empty-state">
                                <p>📭 No diagnostics found for this task.</p>
                                <c:if test="${task.status == 'IN_PROGRESS'}">
                                    <p>Click "Create New Diagnostic" to add one.</p>
                                </c:if>
                            </div>
                        </c:when>
                        <c:otherwise>
                        <table class="diagnostic-table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Issue Found</th>
                                <th>Labor Cost</th>
                                <th>Parts Count</th>
                                <th>Total Cost</th>
                                <th>Status</th>
                                <th>Created Date</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${vm.page.data}" var="diag">
                                <tr>
                                    <td>#${diag.vehicleDiagnosticID}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${fn:length(diag.issueFound) > 50}">
                                                ${fn:substring(diag.issueFound, 0, 50)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${diag.issueFound}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${diag.laborCostCalculated}" type="currency"
                                                          currencySymbol="$"/>
                                    </td>
                                    <td>${fn:length(vm.partsMap[diag.vehicleDiagnosticID])}</td>
                                    <td>
                                        <fmt:formatNumber value="${vm.grandTotal[diag.vehicleDiagnosticID]}" type="currency"
                                                          currencySymbol="$"/>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${diag.statusString == 'SUBMITTED'}">
                                                <span class="status-badge SUBMITTED">Submitted</span>
                                            </c:when>
                                            <c:when test="${diag.statusString == 'APPROVED'}">
                                                <span class="status-badge APPROVED">Approved</span>
                                            </c:when>
                                            <c:when test="${diag.statusString == 'REJECTED'}">
                                                <span class="status-badge REJECTED">Rejected</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-badge DRAFT">Draft</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:out value="${diag.createdAtFormatted}"/>
                                    </td>

                                    <td class="actions">
                                        <a href="${pageContext.request.contextPath}/technician/diagnostic/view?diagnosticId=${diag.vehicleDiagnosticID}"
                                           class="btn-sm btn-view">👀 View</a>

                                        <c:set var="approvedCnt"
                                               value="${vm.approvedCount[diag.vehicleDiagnosticID] != null ? vm.approvedCount[diag.vehicleDiagnosticID] : 0}"/>

                                        <c:if test="${approvedCnt == 0}">
                                            <a class="btn btn-sm btn-outline-secondary"
                                               href="${pageContext.request.contextPath}/technician/diagnostic/edit?diagnosticId=${diag.vehicleDiagnosticID}">
                                                🐙 Edit
                                            </a>
                                        </c:if>

                                        <c:if test="${approvedCnt > 0}">
            <span class="text-muted" style="font-size:12px;"
                  title="This diagnostic has approved parts and cannot be edited.">(locked)</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>


                    <!-- Pagination -->
                    <c:if test="${vm.page.totalPages > 1}">
                        <div class="pagination">
                            <c:forEach begin="1" end="${vm.page.totalPages}" var="p">
                                <a href="?assignmentId=${task.assignmentID}&page=${p}&size=${vm.page.itemsPerPage}&returnTo=${returnTo}"
                                   class="page-link ${p == vm.page.currentPage ? 'active' : ''}">
                                        ${p}
                                </a>
                            </c:forEach>
                        </div>
                    </c:if>
                    </c:otherwise>
                    </c:choose>
                </div>

            </main>
        </div>

    </div>
</div>

<jsp:include page="footer.jsp"/>
