<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>WorkOrders List</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <!-- Header -->
    <nav class="navbar navbar-dark bg-dark">
      <div class="container-fluid">
        <span class="navbar-brand">
          <i class="bi bi-tools"></i> WorkOrders Management
        </span>
        <div>
          <a
            href="${pageContext.request.contextPath}/techmanager/dashboard"
            class="btn btn-outline-light btn-sm"
          >
            <i class="bi bi-house"></i> Dashboard
          </a>
          <a
            href="${pageContext.request.contextPath}/techmanager/workorders/create"
            class="btn btn-success btn-sm"
          >
            <i class="bi bi-plus-circle"></i> Create WorkOrder
          </a>
        </div>
      </div>
    </nav>

    <div class="container-fluid mt-4">
      <!-- Alert Messages -->
      <c:if test="${not empty param.success}">
        <div class="alert alert-success alert-dismissible fade show">
          <i class="bi bi-check-circle"></i>
          <c:choose>
            <c:when test="${param.success == 'created'}"
              >WorkOrder created successfully!</c:when
            >
            <c:when test="${param.success == 'updated'}"
              >WorkOrder updated successfully!</c:when
            >
            <c:when test="${param.success == 'deleted'}"
              >WorkOrder deleted successfully!</c:when
            >
            <c:otherwise>Operation completed successfully!</c:otherwise>
          </c:choose>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="alert"
          ></button>
        </div>
      </c:if>

      <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show">
          <i class="bi bi-exclamation-triangle"></i> Error: ${param.error}
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="alert"
          ></button>
        </div>
      </c:if>

      <!-- WorkOrders Table -->
      <div class="card">
        <div class="card-header">
          <h5 class="mb-0">All WorkOrders</h5>
        </div>
        <div class="card-body">
          <c:choose>
            <c:when test="${not empty workOrders}">
              <div class="table-responsive">
                <table class="table table-hover">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Request ID</th>
                      <th>Status</th>
                      <th>Estimate Amount</th>
                      <th>Created Date</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="workOrder" items="${workOrders}">
                      <tr>
                        <td>${workOrder.workOrderId}</td>
                        <td>${workOrder.requestId}</td>
                        <td>
                          <c:choose>
                            <c:when test="${workOrder.status == 'PENDING'}">
                              <span class="badge bg-warning">Pending</span>
                            </c:when>
                            <c:when test="${workOrder.status == 'IN_PROCESS'}">
                              <span class="badge bg-primary">In Process</span>
                            </c:when>
                            <c:when test="${workOrder.status == 'COMPLETE'}">
                              <span class="badge bg-success">Complete</span>
                            </c:when>
                          </c:choose>
                        </td>
                        <td>
                          <fmt:formatNumber
                            value="${workOrder.estimateAmount}"
                            type="currency"
                            currencySymbol="$"
                          />
                        </td>
                        <td>
                          <fmt:formatDate
                            value="${workOrder.createdAt}"
                            pattern="dd/MM/yyyy HH:mm"
                          />
                        </td>
                        <td>
                          <a
                            href="${pageContext.request.contextPath}/techmanager/workorders/details?workOrderId=${workOrder.workOrderId}"
                            class="btn btn-sm btn-outline-info"
                          >
                            <i class="bi bi-eye"></i> View
                          </a>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>
            </c:when>
            <c:otherwise>
              <div class="text-center py-4">
                <i
                  class="bi bi-info-circle"
                  style="font-size: 3rem; color: #6c757d"
                ></i>
                <h5 class="mt-3 text-muted">No WorkOrders Found</h5>
                <p class="text-muted">
                  You haven't created any work orders yet.
                </p>
                <a
                  href="${pageContext.request.contextPath}/techmanager/workorders/create"
                  class="btn btn-primary"
                >
                  Create Your First WorkOrder
                </a>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
