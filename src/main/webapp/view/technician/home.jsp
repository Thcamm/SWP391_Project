<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="header.jsp"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/technician_home.css"/>

<div class="layout">
    <!-- Sidebar -->
    <aside class="sidebar">
        <div class="brand">SealsCRM</div>

        <div class="menu-section">
            <div class="menu-title">Main menu</div>
            <a class="menu-item active" href="#">Home</a>
            <a class="menu-item" href="${pageContext.request.contextPath}/technician/tasks">Tasks</a>
            <a class="menu-item" href="${pageContext.request.contextPath}/technician/parts">Parts</a>
            <a class="menu-item" href="${pageContext.request.contextPath}/technician/contacts">Contacts</a>
            <a class="menu-item" href="${pageContext.request.contextPath}/technician/settings">Settings</a>
        </div>

        <div class="sidebar-footer">
            <form action="${pageContext.request.contextPath}/logout" method="post">
                <button class="btn-logout" type="submit">⇦ Logout</button>
            </form>
        </div>
    </aside>

    <!-- Main content -->
    <main class="main">
        <!-- Topbar -->
        <div class="topbar card">
            <div class="welcome">
                <h2>My Dashboard</h2>
                <p class="muted">
                    Welcome back,
                    <strong>${technician.fullName}</strong>
                    • ID: ${technician.employeeCode}
                </p>
            </div>
            <div class="avatar">
                <img src="${pageContext.request.contextPath}/assets/img/avatar-default.png" alt="avatar"/>
                <div class="role">Technician</div>
            </div>
        </div>

        <!-- Stats -->
        <section class="stats grid-4">
            <div class="stat card">
                <div class="stat-title">New</div>
                <div class="stat-value">${statistics.newTasksCount}</div>
                <div class="stat-sub">Assigned last 24h</div>
            </div>
            <div class="stat card">
                <div class="stat-title">In Progress</div>
                <div class="stat-value">${statistics.inProgressCount}</div>
                <div class="stat-sub">Currently working</div>
            </div>
            <div class="stat card">
                <div class="stat-title">Completed Today</div>
                <div class="stat-value">${statistics.completedTodayCount}</div>
                <div class="stat-sub">Great job!</div>
            </div>
            <div class="stat card">
                <div class="stat-title">Pending Parts</div>
                <div class="stat-value">${statistics.pendingPartsCount}</div>
                <div class="stat-sub">Waiting for parts</div>
            </div>
        </section>

        <!-- New tasks -->
        <section class="panel card">
            <div class="panel-head">
                <h3>🆕 New Tasks</h3>
                <div class="panel-actions">
<%--                    <span class="badge" title="Total">${newTasks.}</span>--%>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty newTasks.data}">
                    <div class="empty">✨ No new tasks right now.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="table">
                            <thead>
                            <tr>
                                <th>#</th><th>Priority</th><th>Vehicle</th><th>Customer</th>
                                <th>Service</th><th>Type</th><th>Description</th>
                                <th>Est.</th><th>Assigned</th><th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${newTasks.data}" var="task" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td><span class="pill ${task.priority.cssClass}">${task.priority}</span></td>
                                    <td class="mono">${task.vehicleInfo}</td>
                                    <td>${task.customerName}</td>
                                    <td>${task.serviceInfo}</td>
                                    <td><span class="pill light">${task.taskType}</span></td>
                                    <td>
                                            ${fn:substring(task.taskDescription, 0, 50)}
                                        <c:if test="${fn:length(task.taskDescription) > 50}">…</c:if>
                                    </td>
                                    <td>${task.estimateHours}h</td>
                                    <td><fmt:formatDate value="${task.assignedDate}" pattern="dd/MM HH:mm"/></td>
                                    <td class="actions">
                                        <form action="${pageContext.request.contextPath}/technician/tasks-action" method="post">
                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                            <input type="hidden" name="action" value="accept"/>
                                            <button class="btn success" onclick="return confirm('Accept this task?')">Accept</button>
                                        </form>
                                        <form action="${pageContext.request.contextPath}/technician/tasks-action" method="post">
                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                            <input type="hidden" name="action" value="reject"/>
                                            <button class="btn danger" onclick="return confirm('Reject this task?')">Reject</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${newTasks.totalPages > 1}">
                        <div class="pagination">
                            <c:forEach begin="1" end="${newTasks.totalPages}" var="p">
                                <a class="page ${p == newTasks.currentPage ? 'active' : ''}" href="?newTasksPage=${p}">${p}</a>
                            </c:forEach>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- In progress -->
        <section class="panel card">
            <div class="panel-head">
                <h3>⚙️ In Progress</h3>
            </div>

            <c:choose>
                <c:when test="${empty inProgressTasks.data}">
                    <div class="empty">📭 No tasks in progress.</div>
                </c:when>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="table">
                            <thead>
                            <tr>
                                <th>#</th><th>Vehicle</th><th>Service</th><th>Started</th>
                                <th>Progress</th><th>Notes</th><th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${inProgressTasks.data}" var="task" varStatus="st">
                                <tr>
                                    <td>${st.count}</td>
                                    <td class="mono">${task.vehicleInfo}</td>
                                    <td>${task.serviceInfo}</td>
                                    <td><fmt:formatDate value="${task.startAt}" pattern="dd/MM HH:mm"/></td>
                                    <td>
                                        <div class="progress">
                                            <div class="progress-fill" style="width:${task.progressPercentage}%;"></div>
                                        </div>
                                        <span class="mono">${task.progressPercentage}%</span>
                                    </td>
                                    <td>
                                            ${fn:substring(task.notes, 0, 30)}
                                        <c:if test="${fn:length(task.notes) > 30}">…</c:if>
                                    </td>
                                    <td class="actions">
                                        <a class="btn" href="${pageContext.request.contextPath}/technician/update-progress-form?assignmentId=${task.assignmentID}">Update</a>
                                        <form action="${pageContext.request.contextPath}/technician/update-progress" method="post">
                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                            <input type="hidden" name="action" value="complete"/>
                                            <button class="btn success">Complete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${inProgressTasks.totalPages > 1}">
                        <div class="pagination">
                            <c:forEach begin="1" end="${inProgressTasks.totalPages}" var="p">
                                <a class="page ${p == inProgressTasks.currentPage ? 'active' : ''}" href="?inProgressPage=${p}">${p}</a>
                            </c:forEach>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- Activities + Quick actions -->
        <div class="grid-2">
            <section class="panel card">
                <div class="panel-head"><h3>📜 Recent Activities</h3></div>
                <c:choose>
                    <c:when test="${empty recentActivities.data}">
                        <div class="empty">No recent activities.</div>
                    </c:when>
                    <c:otherwise>
                        <ul class="activity">
                            <c:forEach items="${recentActivities.data}" var="a">
                                <li class="activity-item">
                                    <div>
                                        <strong>${a.activityType.displayText}</strong>
                                        <c:if test="${not empty a.vehicleInfo}"> - ${a.vehicleInfo}</c:if>
                                        <c:if test="${not empty a.taskInfo}"> (${a.taskInfo})</c:if>
                                    </div>
                                    <div class="muted"><fmt:formatDate value="${a.activityTime}" pattern="dd/MM/yyyy HH:mm"/></div>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="panel card">
                <div class="panel-head"><h3>⚡ Quick Actions</h3></div>
                <div class="actions-grid">
                    <a class="action-card" href="${pageContext.request.contextPath}/technician/diagnostics">
                        <div class="action-ic">🔍</div><div>Diagnose Vehicle</div>
                    </a>
                    <a class="action-card" href="${pageContext.request.contextPath}/technician/parts">
                        <div class="action-ic">🔧</div><div>Request Parts</div>
                    </a>
                    <a class="action-card" href="${pageContext.request.contextPath}/technician/specifications">
                        <div class="action-ic">📋</div><div>Technical Specs</div>
                    </a>
                    <a class="action-card" href="${pageContext.request.contextPath}/technician/tasks">
                        <div class="action-ic">📊</div><div>All Tasks</div>
                    </a>
                </div>
            </section>
        </div>
    </main>
</div>

<jsp:include page="footer.jsp"/>
