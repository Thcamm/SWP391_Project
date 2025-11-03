<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Rejected Tasks - Tech Manager</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css"
    />
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/techmanager/reject-task.css"
    />
  </head>
  <body>
    <div class="main-container">
      <!-- Sidebar -->
      <c:set var="activeMenu" value="rejected-tasks" scope="request" />
      <jsp:include page="sidebar-techmanager.jsp" />

      <!-- Main Content -->
      <div class="content-wrapper">
        <!-- Header -->
        <jsp:include page="header-techmanager.jsp" />

        <!-- Page Header -->
        <div class="page-header">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-1">
                <i class="bi bi-x-circle text-danger"></i>
                Rejected Tasks
              </h2>
              <p class="text-muted mb-0">
                Tasks rejected by technicians that need reassignment
              </p>
            </div>
            <button
              type="button"
              class="btn btn-outline-secondary"
              onclick="window.location.reload();"
            >
              <i class="bi bi-arrow-clockwise"></i> Refresh
            </button>
          </div>
        </div>

        <!-- Rejected Tasks Panel -->
        <section class="panel">
          <div class="panel-head">
            <h3>
              <i class="bi bi-exclamation-triangle text-danger"></i>
              Tasks Awaiting Reassignment
              <span class="badge badge-count">${rejectedTasks.size()}</span>
            </h3>
          </div>

          <c:choose>
            <c:when test="${empty rejectedTasks}">
              <div class="empty">
                <i class="bi bi-check-circle icon-success"></i>
                <p>✨ No rejected tasks! All assignments are accepted.</p>
              </div>
            </c:when>
            <c:otherwise>
              <div class="table-wrap">
                <table>
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Type</th>
                      <th>Priority</th>
                      <th>Vehicle</th>
                      <th>Customer</th>
                      <th>Task Description</th>
                      <th>Estimate</th>
                      <th>Rejected By</th>
                      <th>Rejected At</th>
                      <th>Reason</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach
                      items="${rejectedTasks}"
                      var="task"
                      varStatus="st"
                    >
                      <tr>
                        <td>${st.count}</td>
                        <td>
                          <span class="pill ${task.taskType}"
                            >${task.taskType}</span
                          >
                        </td>
                        <td>
                          <span class="pill ${task.priority}"
                            >${task.priority}</span
                          >
                        </td>
                        <td class="mono">${task.vehicleInfo}</td>
                        <td>
                          <div>${task.customerName}</div>
                          <small class="text-muted">${task.customerPhone}</small>
                        </td>
                        <td>
                          <c:choose>
                            <c:when
                              test="${task.taskDescription.length() > 60}"
                            >
                              ${task.taskDescription.substring(0, 60)}...
                            </c:when>
                            <c:otherwise> ${task.taskDescription} </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <div>
                            <fmt:formatNumber
                              value="${task.estimateHours}"
                              pattern="#.#"
                            />h
                          </div>
                          <div class="text-muted">
                            $<fmt:formatNumber value="${task.estimateAmount}" pattern="#,##0.00" />
                          </div>
                        </td>
                        <td>
                          <div>${task.technicianName}</div>
                          <small class="text-muted">${task.technicianPhone}</small>
                        </td>
                        <td>
                          <fmt:formatDate
                            value="${task.rejectedAt}"
                            pattern="dd/MM HH:mm"
                          />
                        </td>
                        <td>
                          <c:choose>
                            <c:when test="${not empty task.rejectionReason}">
                              ${task.rejectionReason}
                            </c:when>
                            <c:otherwise>
                              <span class="no-reason">No reason provided</span>
                            </c:otherwise>
                          </c:choose>
                        </td>
                        <td>
                          <button
                            class="btn btn-primary"
                            onclick="reassignTask(${task.assignmentId})"
                          >
                            <i class="bi bi-arrow-repeat"></i> Reassign
                          </button>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:otherwise>
          </c:choose>
        </section>

        <!-- Footer -->
        <jsp:include page="footer-techmanager.jsp" />
      </div>
    </div>

    <script>
      function reassignTask(assignmentId) {
        if (confirm("Reassign this task to another technician?")) {
          // Redirect to assign-diagnosis or assign-repair page with this assignmentId
          window.location.href =
            "${pageContext.request.contextPath}/techmanager/assign-diagnosis?reassign=" +
            assignmentId;
        }
      }
    </script>
  </body>
</html>
