<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Edit WorkOrder Detail</title>
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
          <i class="bi bi-tools"></i> Edit WorkOrder Detail
        </span>
        <div>
          <a
            href="${pageContext.request.contextPath}/techmanager/workorders/details?workOrderId=${workOrder.workOrderId}"
            class="btn btn-outline-light btn-sm"
          >
            <i class="bi bi-arrow-left"></i> Back to Details
          </a>
          <a
            href="${pageContext.request.contextPath}/techmanager/dashboard"
            class="btn btn-outline-light btn-sm"
          >
            <i class="bi bi-house"></i> Dashboard
          </a>
        </div>
      </div>
    </nav>

    <div class="container-fluid mt-4">
      <div class="row justify-content-center">
        <div class="col-md-8">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">
                Edit Detail #${workOrderDetail.detailId} for WorkOrder
                #${workOrder.workOrderId}
              </h5>
            </div>
            <div class="card-body">
              <!-- WorkOrder Info -->
              <div class="alert alert-info">
                <h6>WorkOrder Information:</h6>
                <p class="mb-1">
                  <strong>Request ID:</strong> ${workOrder.requestId}
                </p>
                <p class="mb-1">
                  <strong>Status:</strong>
                  <c:choose>
                    <c:when test="${workOrder.status == 'PENDING'}">
                      <span class="badge bg-warning">Pending</span>
                    </c:when>
                    <c:when test="${workOrder.status == 'IN_PROCESS'}">
                      <span class="badge bg-primary">In Process</span>
                    </c:when>
                    <c:when test="${workOrder.status == 'COMPLETE'}">
                      <span class="badge bg-success">Completed</span>
                    </c:when>
                  </c:choose>
                </p>
                <p class="mb-0">
                  <strong>Estimated Cost:</strong>
                  <fmt:formatNumber
                    value="${workOrder.estimateAmount}"
                    type="currency"
                    currencySymbol="₫"
                  />
                </p>
              </div>

              <!-- Alert Messages -->
              <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show">
                  <i class="bi bi-exclamation-triangle"></i> Error:
                  ${param.error}
                  <button
                    type="button"
                    class="btn-close"
                    data-bs-dismiss="alert"
                  ></button>
                </div>
              </c:if>

              <form
                method="post"
                action="${pageContext.request.contextPath}/techmanager/workorders/edit-detail"
              >
                <input
                  type="hidden"
                  name="workOrderId"
                  value="${workOrder.workOrderId}"
                />
                <input
                  type="hidden"
                  name="detailId"
                  value="${workOrderDetail.detailId}"
                />

                <!-- Set selected values using JSTL -->
                <c:set
                  var="customerSelected"
                  value="${workOrderDetail.source == 'CUSTOMER' ? 'selected' : ''}"
                />
                <c:set
                  var="technicianSelected"
                  value="${workOrderDetail.source == 'TECHNICIAN' ? 'selected' : ''}"
                />
                <c:set
                  var="systemSelected"
                  value="${workOrderDetail.source == 'SYSTEM' ? 'selected' : ''}"
                />

                <div class="mb-3">
                  <label for="source" class="form-label"
                    >Source <span class="text-danger">*</span></label
                  >
                  <select
                    class="form-select"
                    id="source"
                    name="source"
                    required
                  >
                    <option value="">Select source...</option>
                    <option value="CUSTOMER" ${customerSelected}>
                      Customer
                    </option>
                    <option value="TECHNICIAN" ${technicianSelected}>
                      Technician
                    </option>
                    <option value="SYSTEM" ${systemSelected}>System</option>
                  </select>
                  <div class="form-text">
                    Select the source of this order detail.
                  </div>
                </div>

                <div class="mb-3">
                  <label for="taskDescription" class="form-label"
                    >Task Description <span class="text-danger">*</span></label
                  >
                  <textarea
                    class="form-control"
                    id="taskDescription"
                    name="taskDescription"
                    rows="4"
                    placeholder="Describe the task to be performed in detail"
                    required
                  >
                        ${workOrderDetail.taskDescription}</textarea
                  >
                  <div class="form-text">
                    Provide a detailed description of the work.
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="estimateHours" class="form-label"
                        >Estimated Time
                        <span class="text-danger">*</span></label
                      >
                      <input
                        type="number"
                        class="form-control"
                        id="estimateHours"
                        name="estimateHours"
                        placeholder="0.00"
                        step="0.25"
                        min="0"
                        value="${workOrderDetail.estimateHours}"
                        required
                      />
                      <div class="form-text">
                        Estimated time to complete this task.
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="estimateAmount" class="form-label"
                        >Estimated Amount
                        <span class="text-danger">*</span></label
                      >
                      <div class="input-group">
                        <span class="input-group-text">$</span>
                        <input
                          type="number"
                          class="form-control"
                          id="estimateAmount"
                          name="estimateAmount"
                          placeholder="0.00"
                          step="0.01"
                          min="0"
                          value="${workOrderDetail.estimateAmount}"
                          required
                        />
                      </div>
                      <div class="form-text">
                        Estimated amount for this task.
                      </div>
                    </div>
                  </div>
                </div>

                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                  <a
                    href="${pageContext.request.contextPath}/techmanager/workorders/details?workOrderId=${workOrder.workOrderId}"
                    class="btn btn-secondary"
                  >
                    <i class="bi bi-x-circle"></i> Cancel
                  </a>
                  <button type="submit" class="btn btn-primary">
                    <i class="bi bi-pencil-square"></i> Update Detail
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
