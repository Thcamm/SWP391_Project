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

<jsp:include page="header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/tasks.css"/>

<div class="task-page">
    <div class="page-header">
        <h2>🐋All tasks</h2>
        <a href="${pageContext.request.contextPath}/technician/home" class="btn-secondary">
            &laquo; Back to Home
        </a>

    </div>

    <jsp:include page="message-display.jsp"/>

    <div class="task-stats">
        <div class="stat-box all">
            <div class="stat-number">${taskStats.totalTasks}</div>
            <div class="stat-label">Total tasks</div>

        </div>

        <div class="stat-box assigned">
            <div class="stat-number">${taskStats.assignedCount}</div>
            <div class="stat-label">Assigned</div>

        </div>

        <div class="stat-box in-progress">
            <div class="stat-number">${taskStats.inProgressCount}</div>
            <div class="stat-label">In Progress</div>

        </div>

        <div class="stat-box completed">
            <div class="stat-number">${taskStats.completedCount}</div>
            <div class="stat-label">Completed</div>

        </div>

    </div>

    <!-- filter section -->
    <div class="filter-section">
        <form action="${pageContext.request.contextPath}/technician/tasks" method="get" class="filter-form">
            <div class="filter-group">
                <label for="status">Status</label>
                <select name="status" id="status" class="filter-control">
                    <option value="">All Status</option>
                    <option value="ASSIGNED" ${param.status == 'ASSIGNED' ? 'selected' : ''}>Assigned</option>
                    <option value="IN_PROGRESS" ${param.status == 'IN_PROGRESS' ? 'selected' : ''}>IN PROGRESS</option>
                    <option value="COMPLETE" ${param.status == 'COMPLETE' ? 'selected' : ''}>Completed</option>
                </select>
            </div>

            <div class="filter-group">
                <label for="priority">Priority</label>
                <select name="priority" id="priority" class="filter-control">
                    <option value="">All Priority</option>
                    <option value="LOW" ${param.priority == 'LOW' ? 'selected' : ''}>Low</option>
                    <option value="MEDIUM" ${param.priority == 'MEDIUM' ? 'selected' : ''}>Medium</option>
                    <option value="HIGH" ${param.priority == 'HIGH' ? 'selected' : ''}>High</option>
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
        </c:choose>

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
                    <th>Progress</th>
                    <th>Assigned Date</th>
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
                        <td class="vehicle-info">
                           ${task.vehicleInfo}
                        </td>
                        <td>${task.customerName}</td>
                        <td>${task.serviceInfo}</td>
                        <td>
                            <c:if test="${task.status == 'IN_PROGRESS'}">
                                ${task.progressPercentage}%
                            </c:if>
                            <c:if test="${task.status != 'IN_PROGRESS'}">
                                -
                            </c:if>
                        </td>

                        <td>
                            <fmt:formatDate value="${task.assignedDate}" pattern="dd/MM/yyyy"/>
                        </td>

                        <td class="action-buttons">
                            <c:choose>
                                <c:when test="${task.status == 'ASSIGNED'}">
                                    <form action="${pageContext.request.contextPath}/technician/tasks-action"
                                        method="post" style="display: inline;">
                                        <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                        <input type="hidden" name="action" value="accept">
                                        <button type="submit" class="btn btn-accept btn-sm">
                                            Accept🐣
                                        </button>
                                    </form>
                                </c:when>

                                <c:when test="${task.status == 'IN_PROGRESS'}">
                                    <a href="${pageContext.request.contextPath}/technician/update-progress-form?assignmentId
                                    =${task.assignmentID}" class="btn btn-update btn-sm">
                                        Update Progress
                                    </a>

                                    <form action="${pageContext.request.contextPath}/technician/update-progress" method="post" style="display: inline">
                                        <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                        <input type="hidden" name="action" value="complete">
                                        <button type="submit" class="btn btn-complete btn-sm"
                                        onclick="return confirm('Mark this task as complete?');">
                                            Mark as Complete 🐙
                                        </button>


                                    </form>

                                </c:when>
                                <c:when test="${task.status == 'COMPLETE'}">
                                    <span class="btn btn-sm" style="background: #e0e0e0; color: #666; cursor: default;">
                                        DONE 🤞
                                    </span>

                                </c:when>
                            </c:choose>

                        </td>

                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <!-- Pagination -->
            <c:if test="${tasks.totalPages > 1}">
                <div class="pagination">
                    <c:forEach begin="1" end="${tasks.totalPages}" var="page">
                        <a href="?page=${page}&status=${param.status}&priority=${param.priority}&search=${param.search}"
                           class="page-link ${page == tasks.currentPage ? 'active' : ''}">
                            ${page}
                    </c:forEach>

                </div>

            </c:if>
        </c:otherwise>

    </div>



</div>

<jsp:include page="footer.jsp"/>