<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assign Diagnosis - Tech Manager</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/dashboard-techmanager.css">
</head>
<body>
    <%@ include file="header-techmanager.jsp" %>
    
    <div class="main-container">
        <jsp:include page="sidebar-techmanager.jsp">
            <jsp:param name="activeMenu" value="assign-diagnosis" />
        </jsp:include>
        
        <div class="content-wrapper">
            <div class="page-header">
                <h1 class="h2">
                    <i class="bi bi-person-check"></i> Assign Diagnosis Tasks
                </h1>
                <div class="btn-toolbar">
                    <a href="${pageContext.request.contextPath}/techmanager/service-requests" class="btn btn-sm btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Back to Requests
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
                    <div class="col-md-3">
                        <div class="card text-white bg-warning">
                            <div class="card-body">
                                <h5 class="card-title"><i class="bi bi-clipboard-pulse"></i> Pending Diagnosis</h5>
                                <h2>${totalPending}</h2>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card text-white bg-info">
                            <div class="card-body">
                                <h5 class="card-title"><i class="bi bi-people"></i> Available Technicians</h5>
                                <h2>${technicians.size()}</h2>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Pending Diagnosis Table -->
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="bi bi-list-check"></i> WorkOrders Needing Diagnosis Assignment</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty pendingDetails}">
                                <div class="alert alert-info">
                                    <i class="bi bi-info-circle"></i> No diagnosis assignments pending. All approved ServiceRequests have been assigned!
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>WO #</th>
                                                <th>Vehicle</th>
                                                <th>Customer</th>
                                                <th>Task Description</th>
                                                <th>Est. Hours</th>
                                                <th>Created At</th>
                                                <th>Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${pendingDetails}" var="detail">
                                                <tr>
                                                    <td><strong>#${detail.workOrderId}</strong></td>
                                                    <td>
                                                        <i class="bi bi-car-front"></i>
                                                        ${detail.vehicleInfo}
                                                    </td>
                                                    <td>
                                                        <i class="bi bi-person"></i>
                                                        ${detail.customerName}
                                                    </td>
                                                    <td>${detail.taskDescription}</td>
                                                    <td>${detail.estimateHours} hrs</td>
                                                    <td>
                                                        <fmt:formatDate value="${detail.workOrderCreatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn btn-sm btn-primary" 
                                                                data-bs-toggle="modal" 
                                                                data-bs-target="#assignModal"
                                                                data-detail-id="${detail.detailId}"
                                                                data-wo-id="${detail.workOrderId}"
                                                                data-vehicle="${detail.vehicleInfo}"
                                                                data-task="${detail.taskDescription}"
                                                                onclick="prepareAssignment(this)">
                                                            <i class="bi bi-person-plus"></i> Assign
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Available Technicians Info -->
                <div class="card shadow-sm mt-4">
                    <div class="card-header bg-secondary text-white">
                        <h5 class="mb-0"><i class="bi bi-people-fill"></i> Available Technicians</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <c:forEach items="${technicians}" var="tech">
                                <div class="col-md-3 mb-3">
                                    <div class="card">
                                        <div class="card-body">
                                            <h6 class="card-title">
                                                <i class="bi bi-person-badge"></i> ${tech.fullName}
                                            </h6>
                                            <p class="card-text small text-muted">
                                                Code: ${tech.employeeCode}<br>
                                                Phone: ${tech.phoneNumber}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
        </div>
    </div>

    <!-- Assignment Modal -->
    <div class="modal fade" id="assignModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <form method="post" action="${pageContext.request.contextPath}/techmanager/assign-diagnosis">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title"><i class="bi bi-clipboard-check"></i> Assign Diagnosis Task</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" name="detailId" id="modalDetailId">
                        
                        <div class="mb-3">
                            <label class="form-label"><strong>WorkOrder:</strong></label>
                            <p id="modalWorkOrderInfo" class="form-text"></p>
                        </div>

                        <div class="mb-3">
                            <label class="form-label"><strong>Vehicle:</strong></label>
                            <p id="modalVehicleInfo" class="form-text"></p>
                        </div>

                        <div class="mb-3">
                            <label class="form-label"><strong>Task:</strong></label>
                            <p id="modalTaskDesc" class="form-text"></p>
                        </div>

                        <div class="mb-3">
                            <label for="technicianId" class="form-label">Select Technician <span class="text-danger">*</span></label>
                            <select class="form-select" name="technicianId" id="technicianId" required>
                                <option value="">-- Choose Technician --</option>
                                <c:forEach items="${technicians}" var="tech">
                                    <option value="${tech.employeeId}">
                                        ${tech.fullName} - ${tech.employeeCode}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="priority" class="form-label">Priority <span class="text-danger">*</span></label>
                            <select class="form-select" name="priority" id="priority" required>
                                <option value="LOW">Low</option>
                                <option value="MEDIUM" selected>Medium</option>
                                <option value="HIGH">High</option>
                                <option value="URGENT">Urgent</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="notes" class="form-label">Notes (Optional)</label>
                            <textarea class="form-control" name="notes" id="notes" rows="3" 
                                      placeholder="Any special instructions for the technician..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-check-circle"></i> Assign Task
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function prepareAssignment(button) {
            const detailId = button.getAttribute('data-detail-id');
            const workOrderId = button.getAttribute('data-wo-id');
            const vehicleInfo = button.getAttribute('data-vehicle');
            const taskDesc = button.getAttribute('data-task');
            
            document.getElementById('modalDetailId').value = detailId;
            document.getElementById('modalWorkOrderInfo').textContent = 'WorkOrder #' + workOrderId;
            document.getElementById('modalVehicleInfo').textContent = vehicleInfo;
            document.getElementById('modalTaskDesc').textContent = taskDesc;
        }

        // Auto-dismiss alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
    
    <%@ include file="footer-techmanager.jsp" %>
</body>
</html>
