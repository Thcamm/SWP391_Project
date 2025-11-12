<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Service Requests - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet" />
    <style>
      .service-card {
        border-left: 4px solid #6c757d;
        transition: all 0.3s ease;
      }
      .service-card.classified-request {
        border-left-color: #28a745;
        background-color: #f0f9f4;
      }
      .service-card.classified-diagnostic {
        border-left-color: #17a2b8;
        background-color: #f0f8ff;
      }
      .classify-section {
        display: none;
        background: #f8f9fa;
        border-radius: 8px;
        padding: 20px;
        margin-top: 15px;
      }
      .classify-section.show {
        display: block;
      }
      .source-btn {
        min-width: 120px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
      }
      .source-btn input[type='radio'] {
        display: none;
      }
      .source-btn:hover {
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }
      .source-btn.active-request {
        background: #28a745 !important;
        border-color: #28a745 !important;
        color: white !important;
        font-weight: 700;
        box-shadow: 0 4px 12px rgba(40, 167, 69, 0.4);
      }
      .source-btn.active-diagnostic {
        background: #17a2b8 !important;
        border-color: #17a2b8 !important;
        color: white !important;
        font-weight: 700;
        box-shadow: 0 4px 12px rgba(23, 162, 184, 0.4);
      }
    </style>
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
          <div class="d-flex justify-content-between align-items-center">
            <div>
              <h2 class="mb-1">
                <i class="bi bi-clipboard-check text-primary"></i>
                GĐ1 + GĐ2: Service Requests & Triage
              </h2>
              <p class="text-muted mb-0">
                <strong>LUỒNG MỚI:</strong>
                Approve & Classify services in one step
              </p>
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
                            onclick="toggleClassifySection('${request.requestId}')">
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

                      <!-- CLASSIFY SECTION (Hidden by default) -->
                      <div id="classify-section-${request.requestId}" class="classify-section">
                        <h6 class="mb-3">
                          <i class="bi bi-funnel text-purple"></i>
                          <strong>GĐ2: Classify Services</strong>
                        </h6>
                        <p class="text-muted small mb-3">
                          Choose
                          <span class="badge bg-success">REQUEST</span>
                          for direct repair (skip diagnosis) or
                          <span class="badge bg-info">DIAGNOSTIC</span>
                          if diagnosis is needed first.
                        </p>

                        <form
                          id="form-${request.requestId}"
                          method="POST"
                          action="${pageContext.request.contextPath}/techmanager/service-requests">
                          <input type="hidden" name="action" value="approve-classify" />
                          <input type="hidden" name="requestId" value="${request.requestId}" />

                          <!-- Services loaded from servlet (NO AJAX) -->
                          <div id="services-container-${request.requestId}">
                            <c:choose>
                              <c:when test="${empty request.services}">
                                <div class="alert alert-warning">No services found for this request</div>
                              </c:when>
                              <c:otherwise>
                                <c:forEach var="service" items="${request.services}">
                                  <div
                                    class="service-card card mb-2 p-3"
                                    id="service-card-${request.requestId}-${service.serviceId}">
                                    <div class="row align-items-center">
                                      <div class="col-md-6">
                                        <h6 class="mb-1">${service.serviceName}</h6>
                                        <small class="text-muted">${service.serviceDescription}</small>
                                        <div class="mt-1">
                                          <strong class="text-primary">
                                            $
                                            <fmt:formatNumber
                                              value="${service.serviceUnitPrice}"
                                              type="number"
                                              groupingUsed="true" />
                                          </strong>
                                        </div>
                                      </div>
                                      <div class="col-md-6 text-end">
                                        <div class="btn-group" role="group">
                                          <label class="btn btn-outline-success source-btn">
                                            <input
                                              type="radio"
                                              name="source_${service.serviceId}"
                                              value="REQUEST"
                                              onchange="markClassified('${request.requestId}', '${service.serviceId}', 'REQUEST')" />
                                            <i class="bi bi-tools"></i>
                                            REQUEST
                                          </label>
                                          <label class="btn btn-outline-info source-btn">
                                            <input
                                              type="radio"
                                              name="source_${service.serviceId}"
                                              value="DIAGNOSTIC"
                                              onchange="markClassified('${request.requestId}', '${service.serviceId}', 'DIAGNOSTIC')" />
                                            <i class="bi bi-clipboard-pulse"></i>
                                            DIAGNOSTIC
                                          </label>
                                        </div>
                                      </div>
                                    </div>
                                  </div>
                                </c:forEach>
                              </c:otherwise>
                            </c:choose>
                          </div>

                          <div class="mt-3 text-end">
                            <button
                              type="button"
                              class="btn btn-secondary"
                              onclick="toggleClassifySection('${request.requestId}')">
                              <i class="bi bi-x-circle"></i>
                              Cancel
                            </button>
                            <button type="submit" class="btn btn-secondary" disabled>
                              <i class="bi bi-check-circle"></i>
                              Submit Classification (0/?)
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
      console.log('🔥 Script loaded - Version 3.0 NO AJAX - ' + new Date().toISOString());
      const contextPath = '${pageContext.request.contextPath}';

      // Toggle classify section (NO AJAX - services already loaded)
      function toggleClassifySection(requestId) {
        const section = document.getElementById('classify-section-' + requestId);

        if (section.classList.contains('show')) {
          section.classList.remove('show');
        } else {
          section.classList.add('show');
          // Initialize counter on first open
          updateClassificationCounter(requestId);
          // Setup form submit handler
          setupFormSubmitHandler(requestId);
        }
      }

      // Setup form submit validation
      function setupFormSubmitHandler(requestId) {
        const form = document.getElementById('form-' + requestId);

        // Remove old listener to avoid duplicates
        form.onsubmit = null;

        form.onsubmit = function (e) {
          const radios = form.querySelectorAll('input[type="radio"]:checked');
          const totalServices = form.querySelectorAll('.service-card').length;

          if (radios.length !== totalServices) {
            e.preventDefault();
            alert('Please classify ALL services (' + radios.length + '/' + totalServices + ' classified)');
            return false;
          }

          console.log('✅ Submitting ' + radios.length + ' classifications for Request #' + requestId);
          return true;
        };
      }

      // Mark service as classified
      function markClassified(requestId, serviceId, source) {
        const card = document.getElementById('service-card-' + requestId + '-' + serviceId);
        const buttons = card.querySelectorAll('.source-btn');

        // Remove all active classes
        buttons.forEach((btn) => {
          btn.classList.remove('active-request', 'active-diagnostic');
        });

        // Add active class to selected button
        const activeBtn = card.querySelector('input[value="' + source + '"]').parentElement;
        activeBtn.classList.add(source === 'REQUEST' ? 'active-request' : 'active-diagnostic');

        // Update card style
        card.classList.remove('classified-request', 'classified-diagnostic');
        card.classList.add('classified-' + source.toLowerCase());

        // Update classification counter
        updateClassificationCounter(requestId);
      }

      // Update classification counter
      function updateClassificationCounter(requestId) {
        const form = document.getElementById('form-' + requestId);
        const totalServices = form.querySelectorAll('.service-card').length;
        const classifiedServices = form.querySelectorAll('input[type="radio"]:checked').length;

        const submitBtn = form.querySelector('button[type="submit"]');
        submitBtn.innerHTML =
          '<i class="bi bi-check-circle"></i> Submit Classification (' + classifiedServices + '/' + totalServices + ')';

        if (classifiedServices === totalServices) {
          submitBtn.classList.remove('btn-secondary');
          submitBtn.classList.add('btn-primary');
          submitBtn.disabled = false;
        } else {
          submitBtn.classList.remove('btn-primary');
          submitBtn.classList.add('btn-secondary');
          submitBtn.disabled = true;
        }
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
