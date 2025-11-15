
<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/26/2025
  Time: 12:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/tasks.css"/>

<c:url var="returnTo" value="/technician/tasks">
    <c:if test="${not empty param.status}">
        <c:param name="status" value="${param.status}" />
    </c:if>
    <c:if test="${not empty param.priority}">
        <c:param name="priority" value="${param.priority}" />
    </c:if>
    <c:if test="${not empty param.search}">
        <c:param name="search" value="${param.search}" />
    </c:if>
    <c:if test="${not empty param.page}">
        <c:param name="page" value="${param.page}" />
    </c:if>
</c:url>


<jsp:include page="header.jsp"/>
<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main">
                <div class="task-page">
                    <div class="page-header">
                        <h2>All tasks</h2>
                        <a href="${pageContext.request.contextPath}/technician/home" class="btn-secondary">
                            &laquo; Back to Home
                        </a>

                    </div>

                    <jsp:include page="message-display.jsp"/>

                    <div class="task-stats">
                        <div class="stat-box all">
                            <div class="stat-number">${taskStats.totalTasksCount}</div>
                            <div class="stat-label">Total tasks</div>

                        </div>

                        <div class="stat-box assigned">
                            <div class="stat-number">${taskStats.newTasksCount}</div>
                            <div class="stat-label">Assigned</div>

                        </div>

                        <div class="stat-box in-progress">
                            <div class="stat-number">${taskStats.inProgressCount}</div>
                            <div class="stat-label">In Progress</div>

                        </div>

                        <div class="stat-box completed">
                            <div class="stat-number">${taskStats.completedTodayCount}</div>
                            <div class="stat-label">Completed</div>

                        </div>

                    </div>

                    <!-- filter section -->
                    <div class="filter-section">
                        <form action="${pageContext.request.contextPath}/technician/tasks" method="get" class="filter-form">
                            <div class="filter-group">
                                <label for="status">Status</label>
                                <select name="status" id="status" class="filter-control">
                                    <option value="" ${empty param.status ? 'selected' : ''}>All Status</option>
                                    <option value="ASSIGNED"    ${param.status eq 'ASSIGNED'    ? 'selected' : ''}>Assigned</option>
                                    <option value="IN_PROGRESS" ${param.status eq 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                    <option value="COMPLETE"    ${param.status eq 'COMPLETE'    ? 'selected' : ''}>Completed</option>
                                    <option value="DECLINED"    ${param.status eq 'DECLINED'    ? 'selected' : ''}>Declined</option>
                                    <option value="CANCELLED"   ${param.status eq 'CANCELLED'   ? 'selected' : ''}>Cancelled</option>
                                </select>
                            </div>

                            <div class="filter-group">
                                <label for="priority">Priority</label>
                                <select name="priority" id="priority" class="filter-control">
                                    <option value="">All Priority</option>
                                    <option value="LOW" ${param.priority == 'LOW' ? 'selected' : ''}>Low</option>
                                    <option value="MEDIUM" ${param.priority == 'MEDIUM' ? 'selected' : ''}>Medium</option>
                                    <option value="HIGH" ${param.priority == 'HIGH' ? 'selected' : ''}>High</option>
                                    <option value="URGENT" ${param.priority == 'URGENT' ? 'selected' : ''}>Urgent</option>
                                </select>

                            </div>

                            <div class="filter-group">
                                <label for="search">Search</label>
                                <input type="text"
                                       name="search"
                                       id="search"
                                       class="filter-control"
                                       placeholder="Search by vehicle or customer, service..."
                                       value="${param.search != null ? param.search : ''}"/>
                            </div>

                            <div class="filter-group">
                                <div class="filter-actions">
                                    <button type="submit" class="btn btn-filter">Filter</button>
                                    <a href="${pageContext.request.contextPath}/technician/tasks" class="btn btn-reset">Reset</a>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div class="task-table-section">
                        <h3>Tasks List</h3>
                        <c:choose>
                            <c:when test="${empty tasks.data}">
                                <div class="empty-state">
                                    <p>No tasks found matching your criteria.</p>
                                    <p>Try adjusting your filters or check back letter.</p>

                                </div>
                            </c:when>


                            <c:otherwise>
                                <table class="tasks-table">
                                    <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Priority</th>
                                        <th>Status</th>
                                        <th>Vehicle</th>
                                        <th>Customer</th>
                                        <th>Service</th>
                                        <th>Source_Detail</th>
                                        <th>Progress</th>
                                        <th>Pl_Start</th>
                                        <th>Pl_End</th>
                                        <th>Actions</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    <c:forEach items="${tasks.data}" var="task">
                                        <tr>
                                            <td>#${task.assignmentID}</td>
                                            <td>
                                     <span class="priority-badge ${fn:toLowerCase(task.priority)}">
                                             ${task.priority}
                                     </span>
                                            </td>
                                            <td>${task.status}</td>


                                            <td class="vehicle-info">
                                                    ${task.vehicleInfo}
                                            </td>
                                            <td>${task.customerName}</td>
                                            <td>${task.taskDesDetail}</td>
                                            <td>${task.workSource}</td>
                                            <td>
                                                    ${task.progressPercentage}%
                                            </td>

                                            <td>
                                                    ${task.plannedStartFormatted}

                                            </td>

                                            <td>
                                                    ${task.plannedEndFormatted}

                                            </td>

                                            <td class="action-buttons text-end">
                                                <c:set var="ctx" value="${pageContext.request.contextPath}" />
                                                <c:set var="qs"  value="${pageContext.request.queryString}" />
                                                <c:set var="back" value="${pageContext.request.requestURI}${not empty qs ? '?' : ''}${qs}" />


                                                <c:url var="detailUrl" value="/technician/task-detail">
                                                    <c:param name="assignmentId" value="${task.assignmentID}" />
                                                    <c:param name="returnTo" value="${returnTo}" />
                                                </c:url>
                                                <a href="${detailUrl}" class="btn btn-outline-primary btn-sm">View Details</a>


                                                <c:choose>
                                                    <%-- ASSIGNED --%>
                                                    <c:when test="${task.status eq 'ASSIGNED'}">
                                                        <form action="${pageContext.request.contextPath}/technician/tasks-action" method="post" style="display:inline;">
                                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}" />
                                                            <input type="hidden" name="action" value="accept" />
                                                            <input type="hidden" name="returnTo" value="${returnTo}" />
                                                            <button type="submit" class="btn btn-accept btn-sm">Accept</button>
                                                        </form>


                                                        <button type="button" class="btn btn-sm btn-secondary" disabled
                                                                title="Start the task first to create diagnostic">
                                                            Create Diagnostic
                                                        </button>
                                                    </c:when>

                                                    <%-- IN_PROGRESS --%>
                                                    <c:when test="${task.status eq 'IN_PROGRESS'}">
                                                        <c:url var="updUrl" value="/technician/update-progress-form">
                                                            <c:param name="assignmentId" value="${task.assignmentID}" />
                                                            <c:param name="returnTo" value="${returnTo}" />
                                                        </c:url>
                                                        <a href="${updUrl}" class="btn btn-update btn-sm">Update Progress</a>


                                                        <form action="${ctx}/technician/update-progress" method="post" style="display:inline;">
                                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}" />
                                                            <input type="hidden" name="action" value="complete" />
                                                            <input type="hidden" name="returnTo" value="${returnTo}" />
                                                            <button type="submit" class="btn btn-complete btn-sm"
                                                                    onclick="return confirm('Mark this task as complete?');">
                                                                Mark as Complete
                                                            </button>
                                                        </form>


                                                        <form method="get" action="${ctx}/technician/create-diagnostic" style="display:inline;">
                                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}" />
                                                            <input type="hidden" name="returnTo" value="${returnTo}" />
                                                            <button type="submit" class="btn btn-primary btn-sm">Create Diagnostic</button>
                                                        </form>

                                                    </c:when>

                                                    <%-- COMPLETE --%>
                                                    <c:when test="${task.status eq 'COMPLETE'}">
                                                        <span class="btn btn-sm" style="background:#e0e0e0;color:#666;cursor:default;">DONE</span>
                                                    </c:when>

                                                    <%-- CANCELLED / DECLINED --%>
                                                    <c:when test="${task.status eq 'DECLINED'}">
                                                    <span class="badge text-bg-warning text-dark">DECLINED</span>
                                                    <button type="button"
                                                            class="btn btn-outline-secondary btn-sm ms-1"
                                                            data-bs-toggle="modal"
                                                            data-bs-target="#declineReason-${task.assignmentID}">
                                                        View reason
                                                    </button>


                                                        <div class="modal fade" id="declineReason-${task.assignmentID}" tabindex="-1" aria-hidden="true">
                                                            <div class="modal-dialog modal-dialog-centered">
                                                                <div class="modal-content">
                                                                    <div class="modal-header">
                                                                        <h5 class="modal-title">Decline reason — #${task.assignmentID}</h5>
                                                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                                    </div>
                                                                    <div class="modal-body">
                                                                        <p class="mb-1"><strong>Reason:</strong></p>
                                                                        <p class="mb-2">${empty task.declineReason ? '—' : task.declineReason}</p>
                                                                        <p class="text-muted small mb-0">
                                                                            Declined at: ${task.declinedAtFormatted}
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:when>

                                                    <c:when test="${task.status eq 'CANCELLED'}">
                                                        <span class="badge text-bg-danger">CANCELLED</span>
                                                        <c:choose>
                                                            <c:when test="${not empty task.declineReason}">
                                                            <button type="button"
                                                                    class="btn btn-outline-secondary btn-sm ms-1"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#cancelReason-${task.assignmentID}">
                                                                View decline
                                                            </button>


                                                                <div class="modal fade" id="cancelReason-${task.assignmentID}" tabindex="-1" aria-hidden="true">
                                                                    <div class="modal-dialog modal-dialog-centered">
                                                                        <div class="modal-content">
                                                                            <div class="modal-header">
                                                                                <h5 class="modal-title">Cancelled (TM approved) — #${task.assignmentID}</h5>
                                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                                            </div>
                                                                            <div class="modal-body">
                                                                                <p class="mb-1"><strong>Decline reason:</strong></p>
                                                                                <p class="mb-2">${task.declineReason}</p>
                                                                                <p class="text-muted small mb-0">
                                                                                    Declined at: ${task.declinedAtFormatted}
                                                                                </p>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </c:when>



                                                            <c:otherwise>
                                                                <button type="button"
                                                                        class="btn btn-outline-secondary btn-sm ms-1"
                                                                        data-bs-toggle="modal"
                                                                        data-bs-target="#cancelWhy-${task.assignmentID}">
                                                                    Reason
                                                                </button>
                                                                    <div class="modal fade" id="cancelWhy-${task.assignmentID}" tabindex="-1" aria-hidden="true">
                                                                        <div class="modal-dialog modal-dialog-centered">
                                                                            <div class="modal-content">
                                                                                <div class="modal-header">
                                                                                    <h5 class="modal-title">Cancelled (auto-timeout) — #${task.assignmentID}</h5>
                                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                                                </div>
                                                                                <div class="modal-body">
                                                                                    <p class="mb-2">This task was auto-cancelled because it wasn’t accepted within 10 minutes of being assigned.</p>
                                                                                    <p class="text-muted small mb-0">
                                                                                        Assigned at: ${task.assignedDateFormatted}
                                                                                    </p>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>


                                                </c:choose>
                                            </td>


                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>

                                <!-- Pagination -->
                                <c:if test="${tasks.totalPages > 0}">
                                    <div class="pagination">
                                        <c:forEach begin="1" end="${tasks.totalPages}" var="p">
                                            <c:url var="pageUrl" value="/technician/tasks">
                                                <c:param name="page" value="${p}" />
                                                <c:if test="${not empty param.status}">
                                                    <c:param name="status" value="${param.status}" />
                                                </c:if>
                                                <c:if test="${not empty param.priority}">
                                                    <c:param name="priority" value="${param.priority}" />
                                                </c:if>
                                                <c:if test="${not empty param.search}">
                                                    <c:param name="search" value="${param.search}" />
                                                </c:if>
                                            </c:url>
                                            <a href="${pageUrl}" class="page-link ${p == tasks.currentPage ? 'active' : ''}">${p}</a>
                                        </c:forEach>
                                    </div>
                                </c:if>


                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
