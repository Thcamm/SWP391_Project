<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="header.jsp"/>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/task-detail.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/task-parts.css"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main content -->
        <div class="col">
            <main class="main">

                <c:set var="task" value="${vm.task}"/>

                <!-- Page header -->
                <div class="page-header d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2>Request parts for Task #${task.assignmentID}</h2>
                        <div class="text-muted small">
                            Vehicle: ${task.vehicleInfo} |
                            Customer: ${task.customerName}
                        </div>
                    </div>
                    <div>
                        <a href="${pageContext.request.contextPath}/technician/tasks"
                           class="btn-back">Back task List</a>
                    </div>
                </div>

                <jsp:include page="message-display.jsp"/>

                <!-- Thông tin task -->
                <div class="task-info-card mb-4">
                    <h3>Task infor</h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">Task ID</div>
                            <div class="info-value">#${task.assignmentID}</div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">Status-Task</div>
                            <div class="info-value">
                                <span class="status-badge ${task.status}">
                                    ${task.status}
                                </span>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">Priority</div>
                            <div class="info-value">
                                <span class="priority-badge ${task.priority}">
                                    ${task.priority}
                                </span>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">Assigned</div>
                            <div class="info-value">
                                <c:out value="${task.assignedDateFormatted}"/>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">Service</div>
                            <div class="info-value">
                                <c:out
                                        value="${task.taskDesDetail != null ? task.taskDesDetail : task.serviceInfo}"/>
                            </div>
                        </div>

                        <div class="info-item">
                            <div class="info-label">Progress</div>
                            <div class="info-value">
                                ${task.progressPercentage}%
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Form yêu cầu phụ tùng (chỉ cho IN_PROGRESS) -->
                <c:if test="${task.status == 'IN_PROGRESS'}">
                    <div class="card mb-4">
                        <div class="card-body">
                            <h3 class="card-title mb-3">Create rquest part</h3>

                            <c:choose>
                                <c:when test="${empty vm.availableParts}">
                                    <p class="text-muted">
                                        Now no part available to request
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <!-- Form search parts (GET) -->
                                    <form method="get"
                                          action="${pageContext.request.contextPath}/technician/task-parts"
                                          class="row g-3 mb-3">

                                        <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>

                                        <div class="col-md-6">
                                            <label class="form-label">Search part</label>
                                            <input type="text"
                                                   name="partSearch"
                                                   class="form-control"
                                                   placeholder="Search by name / code / SKU..."
                                                   value="${vm.partSearch}"/>
                                        </div>

                                        <div class="col-md-3 d-flex align-items-end">
                                            <button type="submit" class="btn btn-outline-secondary w-100">
                                                Search
                                            </button>
                                        </div>

                                        <div class="col-md-3 d-flex align-items-end">
                                            <a href="${pageContext.request.contextPath}/technician/task-parts?assignmentId=${task.assignmentID}"
                                               class="btn btn-outline-light border w-100">
                                                Reset
                                            </a>
                                        </div>
                                    </form>

                                    <hr/>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/technician/task-parts"
                                          class="row g-3 align-items-end">

                                        <!-- assignmentId để servlet dùng -->
                                        <input type="hidden" name="assignmentId" value="${task.assignmentID}"/>

                                        <!-- Chọn phụ tùng -->
                                        <div class="col-md-6">
                                            <label class="form-label">Pick part</label>
                                            <select name="partDetailId" class="form-select" required>
                                                <option value="">-- Chọn phụ tùng --</option>
                                                <c:forEach var="p" items="${vm.availableParts}">
                                                    <option value="${p.partDetailId}">
                                                        [${p.sku}] ${p.partName}
                                                        (Tồn: ${p.currentStock})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>

                                        <!-- Số lượng -->
                                        <div class="col-md-3">
                                            <label class="form-label">Quantity</label>
                                            <input type="number"
                                                   name="quantity"
                                                   class="form-control"
                                                   min="1"
                                                   value="1"
                                                   required/>
                                        </div>

                                        <div class="col-md-3">
                                            <button type="submit" class="btn btn-primary w-100">
                                                Send Request
                                            </button>
                                        </div>

                                        <div class="col-12 text-muted small">
                                            * Only request part when status Task is
                                            <strong>IN_PROGRESS</strong>.
                                        </div>
                                    </form>

                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </c:if>

                <div class="diagnostics-section">
                    <div class="section-header">
                        <h3> History of Request Part
                            (<c:out value="${fn:length(vm.parts)}"/>)
                        </h3>
                    </div>

                    <c:choose>
                        <c:when test="${empty vm.parts}">
                            <div class="empty-state">
                                <p>No request part for this task.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <table class="table table-striped table-hover align-middle">
                                <thead>
                                <tr>
                                    <th>#ID</th>
                                    <th>SKU</th>
                                    <th>Name Part</th>
                                    <th>Quantity</th>
                                    <th>Unit price</th>
                                    <th>Total price</th>
                                    <th>Status request</th>
                                    <th>Requested at</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="p" items="${vm.parts}">
                                    <tr>
                                        <td>#${p.workOrderPartID}</td>
                                        <td>${p.sku}</td>
                                        <td>${p.partName}</td>
                                        <td>${p.quantityUsed}</td>

                                        <td>
                                            <fmt:formatNumber value="${p.unitPrice}"
                                                              type="currency"
                                                              currencySymbol="$"/>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${p.totalPrice}"
                                                              type="currency"
                                                              currencySymbol="$"/>
                                        </td>

                                        <td><span class="badge status-${p.requestStatus}">
                                                ${p.requestStatusLabel}
                                            </span>
                                        </td>

                                        <td>${p.requestedAtFormatted}</td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:otherwise>
                    </c:choose>
                </div>

            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
