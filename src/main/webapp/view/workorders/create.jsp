<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Create WorkOrder</title>
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
          <i class="bi bi-tools"></i> Create WorkOrder
        </span>
        <div>
          <a
            href="${pageContext.request.contextPath}/techmanager/workorders/list"
            class="btn btn-outline-light btn-sm"
          >
            <i class="bi bi-arrow-left"></i> Back to List
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
              <h5 class="mb-0">Create New WorkOrder</h5>
            </div>
            <div class="card-body">
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
                action="${pageContext.request.contextPath}/techmanager/workorders/create"
              >
                <div class="mb-3">
                  <label for="requestId" class="form-label"
                    >Service Request ID
                    <span class="text-danger">*</span></label
                  >
                  <input
                    type="number"
                    class="form-control"
                    id="requestId"
                    name="requestId"
                    placeholder="Enter approved service request ID"
                    required
                  />
                  <div class="form-text">
                    Nhập ID của service request đã được phê duyệt để tạo work
                    order.
                  </div>
                </div>

                <div class="mb-3">
                  <label for="estimateAmount" class="form-label"
                    >Chi phí ước tính <span class="text-danger">*</span></label
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
                      required
                    />
                  </div>
                  <div class="form-text">
                    Nhập tổng chi phí ước tính cho work order này.
                  </div>
                </div>

                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                  <a
                    href="${pageContext.request.contextPath}/techmanager/workorders/list"
                    class="btn btn-secondary"
                  >
                    <i class="bi bi-x-circle"></i> Hủy
                  </a>
                  <button type="submit" class="btn btn-success">
                    <i class="bi bi-check-circle"></i> Tạo WorkOrder
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
