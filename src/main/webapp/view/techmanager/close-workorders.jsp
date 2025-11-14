<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/core"
prefix="c" %> <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Close Work Orders - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css" />
  </head>
  <body>
    <%@ include file="header-techmanager.jsp" %>

    <div class="main-container">
      <jsp:include page="sidebar-techmanager.jsp">
        <jsp:param name="activeMenu" value="close-workorders" />
      </jsp:include>

      <div class="content-wrapper">
        <div class="page-header">
          <h1 class="h2">
            <i class="bi bi-folder-check"></i>
            Close Work Orders
          </h1>
          <div class="btn-toolbar">
            <a href="${pageContext.request.contextPath}/techmanager/dashboard" class="btn btn-sm btn-outline-secondary">
              <i class="bi bi-arrow-left"></i>
              Back to Dashboard
            </a>
          </div>
        </div>

        <!-- Alert Messages -->
        <c:if test="${param.message != null}">
          <div class="alert alert-${param.type} alert-dismissible fade show" role="alert">
            <c:out value="${param.message}" />
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>

        <!-- Statistics -->
        <div class="row mb-4">
          <div class="col-md-4">
            <div class="card text-white bg-success">
              <div class="card-body">
                <h5 class="card-title">
                  <i class="bi bi-folder-check"></i>
                  Ready to Close
                </h5>
                <h2>${totalReady}</h2>
                <p class="mb-0 small">All tasks completed</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Work Orders Ready for Closure -->
        <div class="card shadow-sm">
          <div class="card-header bg-success text-white">
            <h5 class="mb-0">
              <i class="bi bi-list-check"></i>
              Work Orders Ready for Closure
            </h5>
          </div>
          <div class="card-body">
            <c:choose>
              <c:when test="${empty workOrders}">
                <div class="alert alert-info mb-0">
                  <i class="bi bi-info-circle"></i>
                  No work orders ready for closure. All work orders are either in progress or already closed.
                </div>
              </c:when>
              <c:otherwise>
                <div class="alert alert-success">
                  <strong>✓ ${totalReady} Work Order(s)</strong>
                  are ready to be closed. All tasks have been completed by technicians.
                </div>

                <div class="table-responsive">
                  <table class="table table-hover">
                    <thead>
                      <tr>
                        <th>WO #</th>
                        <th>Vehicle</th>
                        <th>Customer</th>
                        <th>Tech Manager</th>
                        <th>Tasks Status</th>
                        <th>Days Open</th>
                        <th>Created At</th>
                        <th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach items="${workOrders}" var="wo">
                        <tr>
                          <td><strong>#${wo.workOrderID}</strong></td>
                          <td>
                            <i class="bi bi-car-front"></i>
                            ${wo.vehicleInfo}
                          </td>
                          <td>
                            <i class="bi bi-person"></i>
                            ${wo.customerName}
                          </td>
                          <td>
                            <small>${wo.techManagerName}</small>
                          </td>
                          <td>
                            <span class="badge bg-success">${wo.completedTasks}/${wo.totalTasks} Complete</span>
                          </td>
                          <td>
                            <span class="badge ${wo.daysOpen > 7 ? 'bg-warning text-dark' : 'bg-secondary'}">
                              ${wo.daysOpen} days
                            </span>
                          </td>
                          <td>
                            <fmt:formatDate value="${wo.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                          </td>
                          <td>
                            <button
                              type="button"
                              class="btn btn-sm btn-success btn-close-wo"
                              data-wo-id="${wo.workOrderID}"
                              data-vehicle="${wo.vehicleInfo}">
                              <i class="bi bi-check-circle"></i>
                              Close WO
                            </button>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>

                <div class="alert alert-warning mt-3">
                  <strong>
                    <i class="bi bi-info-circle"></i>
                    Note:
                  </strong>
                  Closing a Work Order will:
                  <ul class="mb-0 mt-2">
                    <li>
                      Change WorkOrder status to
                      <strong>COMPLETE</strong>
                    </li>
                    <li>Trigger Invoice generation (if not already created)</li>
                    <li>Allow Accountant to process payment</li>
                    <li>Archive the work order from active lists</li>
                  </ul>
                </div>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </div>

    <!-- Close Confirmation Modal -->
    <div class="modal fade" id="closeModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <form method="post" action="${pageContext.request.contextPath}/techmanager/close-workorders">
            <input type="hidden" name="action" value="close" />
            <input type="hidden" name="workOrderID" id="modalWorkOrderID" />

            <div class="modal-header bg-success text-white">
              <h5 class="modal-title">
                <i class="bi bi-folder-check"></i>
                Confirm Close Work Order
              </h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <div class="alert alert-info">
                <strong>Work Order:</strong>
                <span id="modalWONumber"></span>
                <br />
                <strong>Vehicle:</strong>
                <span id="modalVehicleInfo"></span>
              </div>

              <p>Are you sure you want to close this Work Order?</p>

              <p class="text-muted small mb-0">
                <i class="bi bi-info-circle"></i>
                This action will mark the work order as complete and trigger invoice generation.
              </p>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
              <button type="submit" class="btn btn-success">
                <i class="bi bi-check-circle"></i>
                Yes, Close Work Order
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      // Use event delegation for dynamically handling close buttons
      document.addEventListener('DOMContentLoaded', function () {
        document.querySelectorAll('.btn-close-wo').forEach(function (btn) {
          btn.addEventListener('click', function () {
            const workOrderID = this.getAttribute('data-wo-id');
            const vehicleInfo = this.getAttribute('data-vehicle');

            document.getElementById('modalWorkOrderID').value = workOrderID;
            document.getElementById('modalWONumber').textContent = '#' + workOrderID;
            document.getElementById('modalVehicleInfo').textContent = vehicleInfo;

            const modal = new bootstrap.Modal(document.getElementById('closeModal'));
            modal.show();
          });
        });
      });
    </script>
    <%@ include file="footer-techmanager.jsp" %>
  </body>
</html>
