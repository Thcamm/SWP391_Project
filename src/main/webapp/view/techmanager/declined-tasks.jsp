<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Declined Tasks - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
  </head>
  <body>
    <%@ include file="header-techmanager.jsp" %>

    <div class="main-container">
      <jsp:include page="sidebar-techmanager.jsp">
        <jsp:param name="activeMenu" value="declined-tasks" />
      </jsp:include>

      <div class="content-wrapper">
        <!-- Page Header -->
        <div class="page-header">
          <h1 class="h2">
            <i class="bi bi-hand-thumbs-down-fill text-warning"></i>
            Declined Tasks
          </h1>
          <p class="text-muted">Tasks declined by technicians with reasons</p>
          <div class="btn-toolbar">
            <a href="${pageContext.request.contextPath}/techmanager/reassign-tasks" class="btn btn-primary">
              <i class="bi bi-arrow-repeat"></i>
              Go to Reassignment
            </a>
          </div>
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
            <div class="card text-white bg-warning">
              <div class="card-body">
                <h5 class="card-title">
                  <i class="bi bi-hand-thumbs-down"></i>
                  Total Declined
                </h5>
                <h2>${totalDeclined}</h2>
                <small>Tasks declined by technicians</small>
              </div>
            </div>
          </div>
        </div>

        <!-- Declined Tasks List -->
        <div class="card shadow-sm">
          <div class="card-header bg-warning text-dark">
            <h5 class="mb-0">
              <i class="bi bi-list-ul"></i>
              Declined Tasks List
            </h5>
          </div>
          <div class="card-body">
            <c:choose>
              <c:when test="${empty declinedTasks}">
                <div class="alert alert-success">
                  <i class="bi bi-check-circle"></i>
                  No declined tasks. All technicians accepted their assignments!
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
                        <th>Declined By</th>
                        <th>Declined At</th>
                        <th>Reason</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach items="${declinedTasks}" var="task">
                        <tr>
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
                            <i class="bi bi-calendar-x text-warning"></i>
                            ${task.declinedAt}
                          </td>
                          <td>
                            <button
                              type="button"
                              class="btn btn-sm btn-outline-warning"
                              data-bs-toggle="modal"
                              data-bs-target="#reasonModal${task.assignmentId}">
                              <i class="bi bi-chat-square-text"></i>
                              View Reason
                            </button>
                          </td>
                        </tr>

                        <!-- Reason Modal for each task -->
                        <div class="modal fade" id="reasonModal${task.assignmentId}" tabindex="-1">
                          <div class="modal-dialog">
                            <div class="modal-content">
                              <div class="modal-header bg-warning text-dark">
                                <h5 class="modal-title">
                                  <i class="bi bi-chat-square-text"></i>
                                  Decline Reason
                                </h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                              </div>
                              <div class="modal-body">
                                <div class="mb-3">
                                  <label class="form-label"><strong>Task ID:</strong></label>
                                  <p>#${task.assignmentId}</p>
                                </div>

                                <div class="mb-3">
                                  <label class="form-label"><strong>Vehicle:</strong></label>
                                  <p>${task.vehicleInfo}</p>
                                </div>

                                <div class="mb-3">
                                  <label class="form-label"><strong>Declined By:</strong></label>
                                  <p>${task.technicianName}</p>
                                </div>

                                <div class="mb-3">
                                  <label class="form-label"><strong>Declined At:</strong></label>
                                  <p>${task.declinedAt}</p>
                                </div>

                                <div class="mb-3">
                                  <label class="form-label"><strong>Planned Schedule:</strong></label>
                                  <p>
                                    <i class="bi bi-clock"></i>
                                    ${task.plannedStart} → ${task.plannedEnd}
                                  </p>
                                </div>

                                <div class="alert alert-warning">
                                  <strong>
                                    <i class="bi bi-exclamation-triangle"></i>
                                    Decline Reason:
                                  </strong>
                                  <p class="mt-2 mb-0">
                                    <c:choose>
                                      <c:when test="${empty task.declineReason}">No reason provided</c:when>
                                      <c:otherwise>${task.declineReason}</c:otherwise>
                                    </c:choose>
                                  </p>
                                </div>
                              </div>
                              <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                                <a
                                  href="${pageContext.request.contextPath}/techmanager/reassign-tasks"
                                  class="btn btn-primary">
                                  <i class="bi bi-arrow-repeat"></i>
                                  Reassign Task
                                </a>
                              </div>
                            </div>
                          </div>
                        </div>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>

                <!-- Info Box -->
                <div class="alert alert-info mt-3">
                  <strong>
                    <i class="bi bi-info-circle"></i>
                    Next Steps:
                  </strong>
                  <ul class="mb-0 mt-2">
                    <li>Review decline reasons to understand technician concerns</li>
                    <li>
                      Go to
                      <a href="${pageContext.request.contextPath}/techmanager/reassign-tasks" class="alert-link">
                        Reassignment List
                      </a>
                      to assign to another technician
                    </li>
                    <li>Consider technician feedback for future assignments</li>
                  </ul>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/techmanager/declined-tasks.js"></script>

    <%@ include file="footer-techmanager.jsp" %>
  </body>
</html>
