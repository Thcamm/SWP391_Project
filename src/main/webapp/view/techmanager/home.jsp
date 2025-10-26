<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>TechManager Dashboard</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />
    <link
      href="${pageContext.request.contextPath}/assets/css/admin/create-user.css"
      rel="stylesheet"
    />
  </head>
  <body>
    <!-- Header -->
    <nav class="navbar navbar-dark bg-dark">
      <div class="container-fluid">
        <span class="navbar-brand">
          <i class="bi bi-tools"></i> TechManager Dashboard
        </span>
              <!-- ================= THÊM CHUÔNG VÀO ĐÂY ================= -->
              <div class="dropdown">
                  <a href="#" class="notification-bell me-3" id="notificationDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                      <i class="bi bi-bell-fill"></i>
                      <c:if test="${not empty userNotifications}">
                          <span class="notification-count">${fn:length(userNotifications)}</span>
                      </c:if>
                  </a>
                  <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="notificationDropdown">
                      <c:choose>
                          <c:when test="${not empty userNotifications}">
                              <c:forEach var="notif" items="${userNotifications}" begin="0" end="4">
                                  <li><a class="dropdown-item" href="#">
                                      <strong>${notif.title}</strong><br>
                                      <small>${notif.body}</small>
                                  </a></li>
                              </c:forEach>
                              <li><hr class="dropdown-divider"></li>
                              <li><a class="dropdown-item text-center" href="#">View All</a></li>
                          </c:when>
                          <c:otherwise>
                              <li><a class="dropdown-item" href="#">No new notifications.</a></li>
                          </c:otherwise>
                      </c:choose>
                  </ul>
              </div>
        <span class="navbar-text">
          <i class="bi bi-person-circle"></i> ${techManager.fullName} |
          <fmt:formatDate
            value="<%= new java.util.Date() %>"
            pattern="dd/MM/yyyy HH:mm"
          />
        </span>
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
          <i class="bi bi-exclamation-triangle"></i>
          <c:choose>
            <c:when test="${param.error == 'create_failed'}"
              >Failed to create WorkOrder!</c:when
            >
            <c:when test="${param.error == 'update_failed'}"
              >Failed to update WorkOrder!</c:when
            >
            <c:otherwise>Error: ${param.error}</c:otherwise>
          </c:choose>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="alert"
          ></button>
        </div>
      </c:if>

      <!-- Statistics Cards -->
      <div class="row mb-4">
        <div class="col-md-4">
          <div class="card text-white bg-warning">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h5 class="card-title">WorkOrders Đang Chờ</h5>
                  <h2 class="mb-0">${pendingCount}</h2>
                </div>
                <div class="align-self-center">
                  <i class="bi bi-clock-history" style="font-size: 2rem"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card text-white bg-primary">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h5 class="card-title">Đang Xử Lý</h5>
                  <h2 class="mb-0">${inProcessCount}</h2>
                </div>
                <div class="align-self-center">
                  <i class="bi bi-gear" style="font-size: 2rem"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card text-white bg-success">
            <div class="card-body">
              <div class="d-flex justify-content-between">
                <div>
                  <h5 class="card-title">Hoàn Thành</h5>
                  <h2 class="mb-0">${completedCount}</h2>
                </div>
                <div class="align-self-center">
                  <i class="bi bi-check-circle" style="font-size: 2rem"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="row mb-4">
        <div class="col-12">
          <div class="card">
            <div class="card-header">
              <h5 class="mb-0">Quick Actions</h5>
            </div>
            <div class="card-body">
              <div class="d-flex gap-2 flex-wrap">
                <a
                  href="${pageContext.request.contextPath}/techmanager/workorders/create"
                  class="btn btn-success"
                >
                  <i class="bi bi-plus-circle"></i> Tạo WorkOrder
                </a>
                <a
                  href="${pageContext.request.contextPath}/techmanager/workorders/list"
                  class="btn btn-primary"
                >
                  <i class="bi bi-list"></i> Xem Tất Cả WorkOrders
                </a>
                <a
                  href="${pageContext.request.contextPath}/Home"
                  class="btn btn-secondary"
                >
                  <i class="bi bi-house"></i> Back to Home
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
        <div class="row mb-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-bell"></i> Recent Notifications</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty userNotifications}">
                                <ul class="list-group list-group-flush">
                                    <c:forEach var="notif" items="${userNotifications}" begin="0" end="4">
                                        <li class="list-group-item d-flex justify-content-between align-items-start">
                                            <div class="ms-2 me-auto">
                                                <div class="fw-bold">${notif.title}</div>
                                                    ${notif.body}
                                            </div>
                                            <span class="badge bg-primary rounded-pill">
                                            <fmt:formatDate value="${notif.createdAt}" pattern="dd/MM HH:mm" />
                                        </span>
                                            <a href="${pageContext.request.contextPath}/servicerequest/details?id=${notif.entityId}" class="stretched-link"></a>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted text-center m-0">No new notifications.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <c:if test="${fn:length(userNotifications) > 5}">
                        <div class="card-footer text-center">
                            <a href="#">View All Notifications</a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

      <!-- Recent WorkOrders -->
      <div class="row">
        <div class="col-12">
          <div class="card">
            <div
              class="card-header d-flex justify-content-between align-items-center"
            >
              <h5 class="mb-0">Recent WorkOrders</h5>
              <a
                href="${pageContext.request.contextPath}/techmanager/workorders/list"
                class="btn btn-sm btn-outline-primary"
              >
                View All
              </a>
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
                        <c:forEach
                          var="workOrder"
                          items="${workOrders}"
                          begin="0"
                          end="9"
                        >
                          <tr>
                            <td>${workOrder.workOrderId}</td>
                            <td>${workOrder.requestId}</td>
                            <td>
                              <c:choose>
                                <c:when test="${workOrder.status == 'PENDING'}">
                                  <span class="badge bg-warning">Đang Chờ</span>
                                </c:when>
                                <c:when
                                  test="${workOrder.status == 'IN_PROCESS'}"
                                >
                                  <span class="badge bg-primary"
                                    >Đang Xử Lý</span
                                  >
                                </c:when>
                                <c:when
                                  test="${workOrder.status == 'COMPLETE'}"
                                >
                                  <span class="badge bg-success"
                                    >Hoàn Thành</span
                                  >
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
                                pattern="dd/MM/yyyy"
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
                    <h5 class="mt-3 text-muted">Không Tìm Thấy WorkOrders</h5>
                    <p class="text-muted">
                      Bạn chưa tạo bất kỳ work order nào.
                    </p>
                    <a
                      href="${pageContext.request.contextPath}/techmanager/workorders/create"
                      class="btn btn-primary"
                    >
                      Tạo WorkOrder Đầu Tiên Của Bạn
                    </a>
                  </div>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
