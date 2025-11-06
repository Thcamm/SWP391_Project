<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- Bootstrap 5 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="header.jsp"/>

<div class="container-fluid px-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main -->
        <div class="col">
            <main class="p-3 pb-0">
                <!-- Topbar -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex align-items-center justify-content-between">
                        <div>
                            <h2 class="h4 mb-1">My Dashboard</h2>
                            <p class="text-muted mb-0">
                                Welcome back,
                                <strong>${technician.fullName}</strong>
                                • ID: ${technician.employeeCode}
                            </p>
                        </div>
                        <div class="d-flex align-items-center gap-3">
                            <div class="text-end">
                                <span class="badge text-bg-primary">Technician</span>
                            </div>
                            <img src="${pageContext.request.contextPath}/assets/img/avatar-default.png"
                                 alt="avatar" class="rounded-circle border" style="width:48px;height:48px;object-fit:cover;">
                        </div>
                    </div>
                </div>

                <!-- Stats -->
                <div class="row g-3 mb-3">
                    <div class="col-12 col-sm-6 col-xl-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">New</div>
                                <div class="display-6 fw-semibold">${statistics.newTasksCount}</div>
                                <div class="text-muted small">Assigned last 24h</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">In Progress</div>
                                <div class="display-6 fw-semibold">${statistics.inProgressCount}</div>
                                <div class="text-muted small">Currently working</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">Completed Today</div>
                                <div class="display-6 fw-semibold">${statistics.completedTodayCount}</div>
                                <div class="text-muted small">Great job!</div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-sm-6 col-xl-3">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-body">
                                <div class="text-muted small">Pending Parts</div>
                                <div class="display-6 fw-semibold">${statistics.pendingPartsCount}</div>
                                <div class="text-muted small">Waiting for parts</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- New Tasks -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-0 d-flex align-items-center justify-content-between">
                        <h3 class="h5 mb-0">🆕 New Tasks</h3>
                    </div>

                    <c:choose>
                        <c:when test="${empty newTasks.data}">
                            <div class="card-body">
                                <div class="alert alert-light mb-0">✨ No new tasks right now.</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card-body pt-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>Priority</th>
                                            <th>Vehicle</th>
                                            <th>Customer</th>
                                            <th>Service</th>
                                            <th>Type</th>
                                            <th>Description</th>
                                            <th>Est.</th>
                                            <th>Assigned</th>
                                            <th class="text-end">Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${newTasks.data}" var="task" varStatus="st">
                                            <tr>
                                                <td>${st.count}</td>
                                                <!-- Map cssClass to Bootstrap badge if you already store 'bg-*' values -->
                                                <td>
                            <span class="badge ${task.priority.cssClass != null ? task.priority.cssClass : 'text-bg-secondary'}">
                                    ${task.priority}
                            </span>
                                                </td>
                                                <td class="font-monospace">${task.vehicleInfo}</td>
                                                <td>${task.customerName}</td>
                                                <td>${task.serviceInfo}</td>
                                                <td><span class="badge text-bg-light text-dark">${task.taskType}</span></td>
                                                <td class="text-truncate" style="max-width: 280px;">
                                                        ${fn:substring(task.taskDescription, 0, 50)}<c:if test="${fn:length(task.taskDescription) > 50}">…</c:if>
                                                </td>
                                                <td>${task.estimateHours}h</td>
                                                <td>${task.assignedDateFormatted}</td>
                                                <td>
                                                    <div class="d-flex justify-content-end gap-2">
                                                        <form action="${pageContext.request.contextPath}/technician/tasks-action" method="post" class="m-0">
                                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                                            <input type="hidden" name="action" value="accept"/>
                                                            <button class="btn btn-success btn-sm"
                                                                    onclick="return confirm('Accept this task?')">Accept</button>
                                                        </form>
                                                        <form action="${pageContext.request.contextPath}/technician/tasks-action" method="post" class="m-0">
                                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                                            <input type="hidden" name="action" value="reject"/>
                                                            <button class="btn btn-outline-danger btn-sm"
                                                                    onclick="return confirm('Reject this task?')">Reject</button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <c:if test="${newTasks.totalPages > 1}">
                                    <nav class="mt-3">
                                        <ul class="pagination pagination-sm mb-0">
                                            <c:forEach begin="1" end="${newTasks.totalPages}" var="p">
                                                <li class="page-item ${p == newTasks.currentPage ? 'active' : ''}">
                                                    <a class="page-link" href="?newTasksPage=${p}">${p}</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </nav>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- In Progress -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-0">
                        <h3 class="h5 mb-0">⚙️ In Progress</h3>
                    </div>

                    <c:choose>
                        <c:when test="${empty inProgressTasks.data}">
                            <div class="card-body">
                                <div class="alert alert-light mb-0">📭 No tasks in progress.</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card-body pt-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>Vehicle</th>
                                            <th>Service</th>
                                            <th>Started</th>
                                            <th style="min-width: 200px;">Progress</th>
                                            <th>Notes</th>
                                            <th class="text-end">Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${inProgressTasks.data}" var="task" varStatus="st">
                                            <tr>
                                                <td>${st.count}</td>
                                                <td class="font-monospace">${task.vehicleInfo}</td>
                                                <td>${task.serviceInfo}</td>
                                                <td>${empty task.startAtFormatted ? '-' : task.startAtFormatted}</td>
                                                <td>
                                                    <div class="progress" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="${task.progressPercentage}">
                                                        <div class="progress-bar" style="width:${task.progressPercentage}%;">
                                                                ${task.progressPercentage}%
                                                        </div>
                                                    </div>
                                                </td>
                                                <td class="text-truncate" style="max-width: 220px;">
                                                        ${fn:substring(task.notes, 0, 30)}<c:if test="${fn:length(task.notes) > 30}">…</c:if>
                                                </td>
                                                <td>
                                                    <div class="d-flex justify-content-end gap-2">
                                                        <a class="btn btn-primary btn-sm"
                                                           href="${pageContext.request.contextPath}/technician/update-progress-form?assignmentId=${task.assignmentID}">
                                                            Update
                                                        </a>
                                                        <form action="${pageContext.request.contextPath}/technician/update-progress" method="post" class="m-0">
                                                            <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>
                                                            <input type="hidden" name="action" value="complete"/>
                                                            <button class="btn btn-success btn-sm">Complete</button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <c:if test="${inProgressTasks.totalPages > 1}">
                                    <nav class="mt-3">
                                        <ul class="pagination pagination-sm mb-0">
                                            <c:forEach begin="1" end="${inProgressTasks.totalPages}" var="p">
                                                <li class="page-item ${p == inProgressTasks.currentPage ? 'active' : ''}">
                                                    <a class="page-link" href="?inProgressPage=${p}">${p}</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </nav>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Activities + Quick Actions -->
                <div class="row g-3 mb-4">
                    <!-- Recent Activities -->
                    <div class="col-12 col-lg-7">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h3 class="h6 mb-0">📜 Recent Activities</h3>
                            </div>
                            <c:choose>
                                <c:when test="${empty recentActivities.data}">
                                    <div class="card-body">
                                        <div class="alert alert-light mb-0">No recent activities.</div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="card-body">
                                        <ul class="list-group list-group-flush">
                                            <c:forEach items="${recentActivities.data}" var="a">
                                                <li class="list-group-item d-flex justify-content-between align-items-start">
                                                    <div class="me-3">
                                                        <strong>${a.activityType.displayText}</strong>
                                                        <c:if test="${not empty a.vehicleInfo}"> - ${a.vehicleInfo}</c:if>
                                                        <c:if test="${not empty a.taskInfo}"> (${a.taskInfo})</c:if>
                                                    </div>
                                                    <span class="text-muted small">${a.activityTimeFormatted}</span>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Quick Actions -->
                    <div class="col-12 col-lg-5">
                        <div class="card border-0 shadow-sm h-100">
                            <div class="card-header bg-white border-0">
                                <h3 class="h6 mb-0">⚡ Quick Actions</h3>
                            </div>
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-12 col-sm-6">
                                        <a class="btn btn-outline-secondary w-100 py-3"
                                           href="${pageContext.request.contextPath}/technician/tasks">
                                            📊 All Tasks
                                        </a>
                                    </div>
                                    <!-- Thêm các action khác tại đây nếu cần -->
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
