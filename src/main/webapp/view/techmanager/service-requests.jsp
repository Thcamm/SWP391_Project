<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Service Request Approval - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
  </head>
  <body>
    <div class="main-container">
      <!-- Sidebar -->
      <c:set var="activeMenu" value="service-requests" scope="request" />
      <jsp:include page="sidebar-techmanager.jsp" />

      <!-- Main Content -->
      <div class="content-wrapper">
        <jsp:include page="header-techmanager.jsp" />

        <!-- Page Header -->
        <div class="page-header">
          <h1 class="h2">
            <i class="bi bi-clipboard-check text-primary"></i>
            Service Request Approval
          </h1>
          <p class="text-muted">
            Review and approve pending service requests. After approval, you will classify services in GĐ 2 (Triage).
          </p>
        </div>

        <div class="d-none">
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-1">
                <i class="bi bi-clipboard-check text-primary"></i>
                Service Requests & Triage
              </h2>
              <p class="text-muted mb-0">Approve & Classify services in one step</p>
            </div>
            <button type="button" class="btn btn-outline-secondary" onclick="window.location.reload();">
              <i class="bi bi-arrow-clockwise"></i>
              Refresh
            </button>
          </div>
        </div>

        <!-- Alert Messages -->
        <c:if test="${not empty param.message}">
          <div
            class="alert alert-${param.type == 'success' ? 'success' : param.type == 'warning' ? 'warning' : 'danger'} alert-dismissible fade show">
            <i class="bi bi-${param.type == 'success' ? 'check-circle' : 'exclamation-triangle'}"></i>
            ${param.message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
        </c:if>

        <!-- Stats -->
        <div class="row mb-4">
          <div class="col-md-4">
            <div class="card bg-warning text-white">
              <div class="card-body">
                <h5>
                  <i class="bi bi-clipboard-check"></i>
                  Pending Requests
                </h5>
                <h2 class="mb-0">${totalPending}</h2>
              </div>
            </div>
          </div>
        </div>

        <!-- Service Requests List -->
        <div class="card shadow-sm">
          <div class="card-body">
            <c:choose>
              <c:when test="${empty pendingRequests}">
                <div class="text-center py-5">
                  <i class="bi bi-inbox display-1 text-muted"></i>
                  <p class="text-muted mt-3">No pending service requests</p>
                </div>
              </c:when>
              <c:otherwise>
                <c:forEach var="request" items="${pendingRequests}">
                  <div class="card mb-3 border">
                    <div class="card-header bg-light">
                      <div class="row align-items-center">
                        <div class="col-md-8">
                          <h5 class="mb-0">
                            <i class="bi bi-file-text text-primary"></i>
                            Request #${request.requestId}
                          </h5>
                          <small class="text-muted">
                            <i class="bi bi-calendar"></i>
                            <fmt:formatDate value="${request.requestDate}" pattern="dd/MM/yyyy HH:mm" />
                          </small>
                        </div>
                        <div class="col-md-4 text-end">
                          <button
                            type="button"
                            class="btn btn-success"
                            data-bs-toggle="modal"
                            data-bs-target="#approveModal${request.requestId}">
                            <i class="bi bi-check-circle"></i>
                            Approve & Classify
                          </button>
                          <button type="button" class="btn btn-danger" onclick="rejectRequest('${request.requestId}')">
                            <i class="bi bi-x-circle"></i>
                            Reject
                          </button>
                        </div>
                      </div>
                    </div>
                    <div class="card-body">
                      <div class="row">
                        <div class="col-md-6">
                          <p>
                            <strong>Customer:</strong>
                            ${request.customerName}
                          </p>
                          <p>
                            <strong>Phone:</strong>
                            ${request.phoneNumber}
                          </p>
                        </div>
                        <div class="col-md-6">
                          <p>
                            <strong>Vehicle:</strong>
                            ${request.vehicleBrand} ${request.vehicleModel}
                          </p>
                          <p>
                            <strong>License:</strong>
                            ${request.licensePlate}
                          </p>
                        </div>
                      </div>

                      <!-- Services List with Classification -->
                      <div class="mt-3">
                        <h6 class="text-muted">
                          <i class="bi bi-gear"></i>
                          Requested Services (${request.services.size()}):
                        </h6>
                        <c:choose>
                          <c:when test="${not empty request.services}">
                            <div class="table-responsive">
                              <table class="table table-sm table-hover">
                                <thead class="table-light">
                                  <tr>
                                    <th width="40%">Service</th>
                                    <th width="30%">Description</th>
                                    <th width="15%" class="text-end">Price</th>
                                    <th width="15%" class="text-center">Type</th>
                                  </tr>
                                </thead>
                                <tbody>
                                  <c:forEach var="service" items="${request.services}" varStatus="status">
                                    <tr>
                                      <td><strong>${service.serviceName}</strong></td>
                                      <td><small class="text-muted">${service.serviceDescription}</small></td>
                                      <td class="text-end">
                                        <strong class="text-primary">
                                          <fmt:formatNumber
                                            value="${service.serviceUnitPrice}"
                                            type="number"
                                            groupingUsed="true" />
                                          ₫
                                        </strong>
                                      </td>
                                      <td class="text-center">
                                        <span class="badge bg-secondary">Pending</span>
                                      </td>
                                    </tr>
                                  </c:forEach>
                                </tbody>
                              </table>
                            </div>
                          </c:when>
                          <c:otherwise>
                            <p class="text-muted">No services found</p>
                          </c:otherwise>
                        </c:choose>
                      </div>
                    </div>
                  </div>

                  <!-- Approve & Classify Modal -->
                  <div class="modal fade" id="approveModal${request.requestId}" tabindex="-1">
                    <div class="modal-dialog modal-lg">
                      <div class="modal-content">
                        <div class="modal-header bg-success text-white">
                          <h5 class="modal-title">
                            <i class="bi bi-check-circle"></i>
                            Approve & Classify Services - Request #${request.requestId}
                          </h5>
                          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <form
                          method="POST"
                          action="${pageContext.request.contextPath}/techmanager/service-requests"
                          onsubmit="return validateClassification('${request.requestId}')">
                          <div class="modal-body">
                            <input type="hidden" name="action" value="approve" />
                            <input type="hidden" name="requestId" value="${request.requestId}" />

                            <div class="alert alert-info">
                              <i class="bi bi-info-circle"></i>
                              <strong>Instructions:</strong>
                              Classify each service as either:
                              <ul class="mb-0 mt-2">
                                <li>
                                  <strong>REQUEST:</strong>
                                  Direct repair - Skip diagnosis (Go to GĐ5)
                                </li>
                                <li>
                                  <strong>DIAGNOSTIC:</strong>
                                  Needs inspection first (Go to GĐ1)
                                </li>
                              </ul>
                            </div>

                            <div class="table-responsive">
                              <table class="table table-bordered">
                                <thead class="table-light">
                                  <tr>
                                    <th width="40%">Service</th>
                                    <th width="30%">Description</th>
                                    <th width="15%" class="text-end">Price</th>
                                    <th width="15%" class="text-center">Classification *</th>
                                  </tr>
                                </thead>
                                <tbody>
                                  <c:forEach var="service" items="${request.services}" varStatus="status">
                                    <tr>
                                      <td><strong>${service.serviceName}</strong></td>
                                      <td><small class="text-muted">${service.serviceDescription}</small></td>
                                      <td class="text-end">
                                        <strong>
                                          <fmt:formatNumber
                                            value="${service.serviceUnitPrice}"
                                            type="number"
                                            groupingUsed="true" />
                                          ₫
                                        </strong>
                                      </td>
                                      <td class="text-center">
                                        <input
                                          type="hidden"
                                          name="serviceDetailId_${status.index}"
                                          value="${service.detailId}" />
                                        <select
                                          name="source_${status.index}"
                                          class="form-select form-select-sm source-select-${request.requestId}"
                                          required>
                                          <option value="">-- Choose --</option>
                                          <option value="REQUEST" style="background-color: #d1e7dd">
                                            🔧 REQUEST (Direct Repair)
                                          </option>
                                          <option value="DIAGNOSTIC" style="background-color: #fff3cd">
                                            🔍 DIAGNOSTIC (Needs Inspection)
                                          </option>
                                        </select>
                                      </td>
                                    </tr>
                                  </c:forEach>
                                </tbody>
                              </table>
                            </div>

                            <input type="hidden" name="totalServices" value="${request.services.size()}" />
                          </div>
                          <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-success">
                              <i class="bi bi-check-circle"></i>
                              Approve & Create WorkOrder
                            </button>
                          </div>
                        </form>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      const contextPath = '${pageContext.request.contextPath}';

      // Validate classification before submit
      function validateClassification(requestId) {
        const selects = document.querySelectorAll('.source-select-' + requestId);
        let allSelected = true;

        selects.forEach((select) => {
          if (!select.value) {
            allSelected = false;
            select.classList.add('is-invalid');
          } else {
            select.classList.remove('is-invalid');
          }
        });

        if (!allSelected) {
          alert('Please classify all services before approving!');
          return false;
        }
        return true;
      }

      // Reject request
      function rejectRequest(requestId) {
        const reason = prompt('Enter rejection reason:');
        if (reason) {
          const form = document.createElement('form');
          form.method = 'POST';
          form.action = contextPath + '/techmanager/service-requests';

          const actionInput = document.createElement('input');
          actionInput.type = 'hidden';
          actionInput.name = 'action';
          actionInput.value = 'reject';

          const idInput = document.createElement('input');
          idInput.type = 'hidden';
          idInput.name = 'requestId';
          idInput.value = requestId;

          const reasonInput = document.createElement('input');
          reasonInput.type = 'hidden';
          reasonInput.name = 'rejectionReason';
          reasonInput.value = reason;

          form.appendChild(actionInput);
          form.appendChild(idInput);
          form.appendChild(reasonInput);

          document.body.appendChild(form);
          form.submit();
        }
      }
    </script>
  </body>
</html>
