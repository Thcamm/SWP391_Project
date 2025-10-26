<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/19/2025
  Time: 4:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>



<jsp:include page="header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/technician_home.css">

<section class="welcome-section">
    <div class="welcome-left">
        <h2>Welcome back, ${technician.fullName}!</h2>
        <p class="welcome-date">
            <jsp:useBean id="now" class="java.util.Date" />
            <fmt:formatDate value="${now}" pattern="EEEE, dd MMMM yyy"/>
        </p>

    </div>
    <div class="welcome-right">
        <span class="employee-code">ID: ${technician.employeeCode}</span>
    </div>

</section>

<jsp:include page="message-display.jsp" />

<section class="dashboard-stats">
    <div class="stat-card stat-new">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
            <h3>New Tasks</h3>
            <p class="count">${statistics.newTasksCount}</p>
        </div>
    </div>

    <div class="stat-card stat-progress">
        <div class="stat-icon">⚙️</div>
        <div class="stat-info">
            <h3>In Progress</h3>
            <p class="count">${statistics.inProgressCount}</p>
        </div>
    </div>

    <div class="stat-card stat-completed">
        <div class="stat-icon">✅</div>
        <div class="stat-info">
            <h3>Completed Today</h3>
            <p class="count">${statistics.completedTodayCount}</p>
        </div>
    </div>

    <div class="stat-card stat-parts">
        <div class="stat-icon">🔧</div>
        <div class="stat-info">
            <h3>Pending Parts</h3>
            <p class="count">${statistics.pendingPartsCount}</p>
        </div>
    </div>
</section>

<section class="task-section">
    <h2>🆕 New Tasks Assigned to You</h2>

    <c:choose>
    <c:when test="${empty newTasks.data}">
    <div class="empty-state">
        <p>✨ No new tasks at the moment. Great job staying on top of your work!</p>
    </div>
    </c:when>
    <c:otherwise>
    <div class="task-table-wrapper">
        <table class="task-table">
            <thead>
            <tr>
                <th>Priority</th>
                <th>Vehicle</th>
                <th>Customer</th>
                <th>Service</th>
                <th>Type</th>
                <th>Description</th>
                <th>Est. Hours</th>
                <th>Assigned</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${newTasks.data}" var="task">
                <tr class="task-row">
                    <td>
                                    <span class="priority-badge ${task.priority.cssClass}">
                                            ${task.priority}
                                    </span>
                    </td>
                    <td class="vehicle-info">${task.vehicleInfo}</td>
                    <td>${task.customerName}</td>
                    <td>${task.serviceInfo}</td>
                    <td>
                        <span class="task-type-badge">${task.taskType}🏠</span>
                    </td>
                    <td class="task-description">
                            ${fn:substring(task.taskDescription, 0, 50)}
                        <c:if test="${fn:length(task.taskDescription) > 50}">...</c:if>
                    </td>
                    <td>${task.estimateHours}h</td>
                    <td>
                        <fmt:formatDate value="${task.assignedDate}" pattern="dd/MM HH:mm" />
                    </td>
                    <td class="action-buttons">
                        <form action="${pageContext.request.contextPath}/technician/task-action"
                              method="post" style="display:inline;">
                            <input type="hidden" name="assignmentId" value="${task.assignmentID}">
                            <input type="hidden" name="action" value="accept">
                            <button type="submit" class="btn btn-accept"
                                    onclick="return confirm('Accept this task?')">
                                Accept
                            </button>
                        </form>
                        <form action="${pageContext.request.contextPath}/technician/task-action"
                              method="post" style="display:inline;">
                            <input type="hidden" name="assignmentId" value="${task.assignmentID}">
                            <input type="hidden" name="action" value="reject">
                            <button type="submit" class="btn btn-reject"
                                    onclick="return confirm('Reject this task?')">
                                Reject
                            </button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <c:if test="${newTasks.totalPages > 1}">
            <div class="pagination">
                <c:forEach begin="1" end="${newTasks.totalPages}" var="page">
                    <a href="?newTasksPage=${page}"
                       class="page-link ${page == newTasks.currentPage ? 'active' : ''}">
                            ${page}
                    </a>
                </c:forEach>
            </div>
        </c:if>
    </div>
    </c:otherwise>
    </c:choose>
</section>

<section class="task-section">
    <h2>⚙️ Tasks In Progress</h2>

    <c:choose>
        <c:when test="${empty inProgressTasks.data}">
            <div class="empty-state">
                <p>📭 No tasks in progress. Accept a new task to get started!</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="task-table-wrapper">
                <table class="task-table">
                    <thead>
                    <tr>
                        <th>Vehicle</th>
                        <th>Service</th>
                        <th>Started</th>
                        <th>Progress</th>
                        <th>Notes</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${inProgressTasks.data}" var="task">
                        <tr class="task-row">
                            <td class="vehicle-info">${task.vehicleInfo}</td>
                            <td>${task.serviceInfo}</td>
                            <td>
                                <fmt:formatDate value="${task.startAt}" pattern="dd/MM HH:mm" />
                            </td>
                            <td>
                                <div class="progress-container">
                                    <div class="progress-bar">
                                        <div class="progress-fill"
                                             style="width: ${task.progressPercentage}%"></div>
                                    </div>
                                    <span class="progress-text">${task.progressPercentage}%</span>
                                </div>
                            </td>
                            <td class="task-notes">
                                    ${fn:substring(task.notes, 0, 30)}
                                <c:if test="${fn:length(task.notes) > 30}">...</c:if>
                            </td>
                            <td class="action-buttons">
                                <button type="button" class="btn btn-update"
                                        onclick="openProgressModal(${task.assignmentID}, ${task.progressPercentage}, '${fn:escapeXml(task.notes)}')">
                                    Update
                                </button>
                                <button type="button" class="btn btn-complete"
                                        onclick="completeTask(${task.assignmentID})">
                                    Complete
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <!-- Pagination cho In Progress Tasks -->
                <c:if test="${inProgressTasks.totalPages > 1}">
                    <div class="pagination">
                        <c:forEach begin="1" end="${inProgressTasks.totalPages}" var="page">
                            <a href="?inProgressPage=${page}"
                               class="page-link ${page == inProgressTasks.currentPage ? 'active' : ''}">
                                    ${page}
                            </a>
                        </c:forEach>
                    </div>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<section class="activity-section">
    <h2>📜 Recent Activities</h2>

    <c:choose>
        <c:when test="${empty recentActivities.data}">
            <div class="empty-state">
                <p>No recent activities.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="activity-list">
                <c:forEach items="${recentActivities.data}" var="activity">
                    <div class="activity-item">
                        <div class="activity-content">
                            <p class="activity-text">
                                <strong>${activity.activityType.displayText}</strong>
                                <c:if test="${not empty activity.vehicleInfo}">
                                    - ${activity.vehicleInfo}
                                </c:if>
                                <c:if test="${not empty activity.taskInfo}">
                                    (${activity.taskInfo})
                                </c:if>
                            </p>
                            <p class="activity-time">
                                <fmt:formatDate value="${activity.activityTime}" pattern="dd/MM/yyyy HH:mm" />
                            </p>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Pagination cho Activities -->
            <c:if test="${recentActivities.totalPages > 1}">
                <div class="pagination">
                    <c:forEach begin="1" end="${recentActivities.totalPages}" var="page">
                        <a href="?activitiesPage=${page}"
                           class="page-link ${page == recentActivities.currentPage ? 'active' : ''}">
                                ${page}
                        </a>
                    </c:forEach>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>
</section>

<section class="quick-actions">
    <h2>⚡ Quick Actions</h2>
    <div class="action-grid">
        <a href="${pageContext.request.contextPath}/technician/diagnostics" class="action-card">
            <div class="action-icon">🔍</div>
            <h3>Diagnose Vehicle</h3>
            <p>Perform vehicle diagnostics</p>
        </a>

        <a href="${pageContext.request.contextPath}/technician/parts" class="action-card">
            <div class="action-icon">🔧</div>
            <h3>Request Parts</h3>
            <p>Request parts for repairs</p>
        </a>

        <a href="${pageContext.request.contextPath}/technician/specifications" class="action-card">
            <div class="action-icon">📋</div>
            <h3>Technical Specs</h3>
            <p>View technical manuals</p>
        </a>

        <a href="${pageContext.request.contextPath}/technician/tasks" class="action-card">
            <div class="action-icon">📊</div>
            <h3>All Tasks</h3>
            <p>View complete task list</p>
        </a>
    </div>
</section>

<div id="progressModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="closeProgressModal()">&times;</span>
        <h2>Update Task Progress</h2>
        <form action="${pageContext.request.contextPath}/technician/update-progress" method="post">
            <input type="hidden" id="modalAssignmentId" name="assignmentId">
            <input type="hidden" name="action" value="update">

            <div class="form-group">
                <label for="progressPercentage">Progress (%)</label>
                <input type="range" id="progressPercentage" name="progressPercentage"
                       min="0" max="100" value="0" oninput="updateProgressValue(this.value)">
                <span id="progressValue">0%</span>
            </div>

            <div class="form-group">
                <label for="notes">Notes</label>
                <textarea id="notes" name="notes" rows="4"
                          placeholder="Enter any notes about the progress..."></textarea>
            </div>

            <div class="modal-actions">
                <button type="submit" class="btn btn-primary">Update Progress</button>
                <button type="button" class="btn btn-secondary" onclick="closeProgressModal()">Cancel</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="footer.jsp" />