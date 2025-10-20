<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Thêm chi tiết WorkOrder</title>
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
          <i class="bi bi-tools"></i> Thêm chi tiết WorkOrder
        </span>
        <div>
          <a
            href="${pageContext.request.contextPath}/techmanager/workorders/details?workOrderId=${workOrder.workOrderId}"
            class="btn btn-outline-light btn-sm"
          >
            <i class="bi bi-arrow-left"></i> Quay lại chi tiết
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
                Thêm chi tiết cho WorkOrder #${workOrder.workOrderId}
              </h5>
            </div>
            <div class="card-body">
              <!-- WorkOrder Info -->
              <div class="alert alert-info">
                <h6>Thông tin WorkOrder:</h6>
                <p class="mb-1">
                  <strong>Request ID:</strong> ${workOrder.requestId}
                </p>
                <p class="mb-1">
                  <strong>Status:</strong>
                  <c:choose>
                    <c:when test="${workOrder.status == 'PENDING'}">
                      <span class="badge bg-warning">Đang chờ</span>
                    </c:when>
                    <c:when test="${workOrder.status == 'IN_PROCESS'}">
                      <span class="badge bg-primary">Đang xử lý</span>
                    </c:when>
                    <c:when test="${workOrder.status == 'COMPLETE'}">
                      <span class="badge bg-success">Hoàn thành</span>
                    </c:when>
                  </c:choose>
                </p>
                <p class="mb-0">
                  <strong>Chi phí ước tính:</strong>
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
                action="${pageContext.request.contextPath}/techmanager/workorders/add-detail"
              >
                <input
                  type="hidden"
                  name="workOrderId"
                  value="${workOrder.workOrderId}"
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
                    <option value="">Chọn Source...</option>
                    <option value="CUSTOMER">Khách hàng</option>
                    <option value="TECHNICIAN">Kỹ thuật viên</option>
                    <option value="SYSTEM">Hệ thống</option>
                  </select>
                  <div class="form-text">
                    Chọn Source của chi tiết work order này.
                  </div>
                </div>

                <div class="mb-3">
                  <label for="taskDescription" class="form-label"
                    >Mô tả công việc <span class="text-danger">*</span></label
                  >
                  <textarea
                    class="form-control"
                    id="taskDescription"
                    name="taskDescription"
                    rows="4"
                    placeholder="Describe the task to be performed in detail"
                    required
                  ></textarea>
                  <div class="form-text">
                    Cung cấp mô tả chi tiết về công việc cần thực hiện.
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="estimateHours" class="form-label"
                        >Thời gian ước tính
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
                        required
                      />
                      <div class="form-text">
                        Thời gian ước tính để hoàn thành công việc này.
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="mb-3">
                      <label for="estimateAmount" class="form-label"
                        >Chi phí ước tính
                        <span class="text-danger">*</span></label
                      >
                      <div class="input-group">
                        <span class="input-group-text">₫</span>
                        <input
                          type="number"
                          class="form-control"
                          id="estimateAmount"
                          name="estimateAmount"
                          placeholder="0.00"
                          step="0.01"
                          min="0"
                          required
                        />
                      </div>
                      <div class="form-text">
                        Chi phí ước tính cho công việc này.
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
                  <button type="submit" class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> Thêm chi tiết
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
