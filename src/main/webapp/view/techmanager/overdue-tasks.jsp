<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Overdue Tasks - SLA Violations</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
  </head>
  <body>
    <%@ include file="header-techmanager.jsp" %>

    <div class="main-container">
      <jsp:include page="sidebar-techmanager.jsp">
        <jsp:param name="activeMenu" value="overdue-tasks" />
      </jsp:include>

      <div class="content-wrapper">
        <!-- Page Header -->
        <div class="page-header">
          <h1 class="h2">
            <i class="bi bi-exclamation-triangle-fill text-danger"></i>
            Overdue Tasks
          </h1>
          <p class="text-muted">Tasks that missed their planned start time (SLA violation)</p>
        </div>

        <!-- Messages -->
        <c:if test="${param.message != null}">
          <div class="alert alert-${param.type} alert-dismissible fade show" role="alert">
            <c:out value="${param.message}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>

        <!-- Statistics -->
        <div class="row mb-4">
          <div class="col-md-4">
            <div class="card text-white bg-danger">
              <div class="card-body">
                <h5 class="card-title">
                  <i class="bi bi-clock-history"></i>
                  Total Overdue
                </h5>
                <h2>${totalOverdue}</h2>
                <small>Tasks past planned start time</small>
              </div>
            </div>
          </div>
        </div>

        <!-- Overdue Tasks List -->
        <div class="card shadow-sm">
          <div class="card-header bg-danger text-white">
            <h5 class="mb-0">
              <i class="bi bi-exclamation-triangle"></i>
              Overdue Tasks List
            </h5>
          </div>
          <div class="card-body">
            <c:choose>
              <c:when test="${empty overdueTasks}">
                <div class="alert alert-success">
                  <i class="bi bi-check-circle"></i>
                  Great! No overdue tasks at this time.
                </div>
              </c:when>
              <c:otherwise>
                <div class="table-responsive">
                  <table class="table table-hover">
                    <thead>
                      <tr>
                        <th>Task ID</th>
                        <th>Type</th>
                        <th>Vehicle</th>
                        <th>Customer</th>
                        <th>Technician</th>
                        <th>Planned Start</th>
                        <th>Hours Overdue</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach items="${overdueTasks}" var="task">
                        <tr class="table-danger">
                          <td><strong>#${task.assignmentId}</strong></td>
                          <td>
                            <span class="badge bg-primary">${task.taskType}</span>
                          </td>
                          <td>
                            <i class="bi bi-car-front"></i>
                            ${task.vehicleInfo}
                          </td>
                          <td>
                            <i class="bi bi-person"></i>
                            ${task.customerName}
                          </td>
                          <td>
                            <i class="bi bi-person-badge"></i>
                            ${task.technicianName}
                          </td>
                          <td>
                            <i class="bi bi-calendar-x text-danger"></i>
                            ${task.plannedStart}
                          </td>
                          <td>
                            <span class="badge bg-danger">
                              <i class="bi bi-clock"></i>
                              ${task.hoursOverdue} hours
                            </span>
                          </td>
                          <td>
                            <button
                              type="button"
                              class="btn btn-sm btn-danger"
                              data-bs-toggle="modal"
                              data-bs-target="#cancelModal"
                              data-assignment-id="${task.assignmentId}"
                              data-vehicle="${fn:escapeXml(task.vehicleInfo)}"
                              data-technician="${fn:escapeXml(task.technicianName)}"
                              onclick="prepareCancellationFromBtn(this)">
                              <i class="bi bi-x-circle"></i>
                              Cancel Task
                            </button>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>

                <!-- Info Box -->
                <div class="alert alert-info mt-3">
                  <strong>
                    <i class="bi bi-info-circle"></i>
                    What happens when you cancel?
                  </strong>
                  <ul class="mb-0 mt-2">
                    <li>
                      Task status will be changed to
                      <strong>CANCELLED</strong>
                    </li>
                    <li>
                      Task will appear in
                      <strong>Reassignment List</strong>
                    </li>
                    <li>You can reassign it to another technician with new scheduling</li>
                  </ul>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </div>

    <!-- Cancel Confirmation Modal -->
    <div class="modal fade" id="cancelModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method="post" action="${pageContext.request.contextPath}/techmanager/overdue-tasks">
            <div class="modal-header bg-danger text-white">
              <h5 class="modal-title">
                <i class="bi bi-exclamation-triangle"></i>
                Cancel Overdue Task
              </h5>
              <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <input type="hidden" name="action" value="cancel" />
              <input type="hidden" name="assignmentId" id="modalAssignmentId" />

              <div class="alert alert-warning">
                <strong>Are you sure you want to cancel this task?</strong>
              </div>

              <div class="mb-3">
                <label class="form-label"><strong>Task ID:</strong></label>
                <p id="modalTaskId" class="form-text"></p>
              </div>

              <div class="mb-3">
                <label class="form-label"><strong>Vehicle:</strong></label>
                <p id="modalVehicle" class="form-text"></p>
              </div>

              <div class="mb-3">
                <label class="form-label"><strong>Assigned To:</strong></label>
                <p id="modalTechnician" class="form-text"></p>
              </div>

              <div class="alert alert-info">
                <i class="bi bi-info-circle"></i>
                After cancellation, you can reassign this task to another technician from the
                <strong>Reassignment List</strong>
                .
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">No, Keep It</button>
              <button type="submit" class="btn btn-danger">
                <i class="bi bi-x-circle"></i>
                Yes, Cancel Task
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      function prepareCancellationFromBtn(btn) {
        var id = btn.getAttribute('data-assignment-id');
        var vehicle = btn.getAttribute('data-vehicle') || '';
        var tech = btn.getAttribute('data-technician') || '';
        var assignmentInput = document.getElementById('modalAssignmentId');
        if (assignmentInput) assignmentInput.value = id;
        var taskIdEl = document.getElementById('modalTaskId');
        if (taskIdEl) taskIdEl.textContent = '#' + id;
        var vehEl = document.getElementById('modalVehicle');
        if (vehEl) vehEl.textContent = vehicle;
        var techEl = document.getElementById('modalTechnician');
        if (techEl) techEl.textContent = tech;
      }
    </script>

    <%@ include file="footer-techmanager.jsp" %>
  </body>
</html>
