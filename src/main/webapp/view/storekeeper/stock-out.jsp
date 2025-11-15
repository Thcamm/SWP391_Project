<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Part Requests Management - Garage Management System</title>

    <!-- Bootstrap 5 & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        body {
            background-color: #f8fafc;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .request-card {
            transition: transform 0.2s, box-shadow 0.2s;
        }

        .request-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .stat-badge {
            padding: 0.5rem 1rem;
            border-radius: 8px;
            font-weight: 600;
        }

        .action-buttons .btn {
            min-width: 90px;
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp"/>

<div class="container-fluid px-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-auto" style="flex:0 0 280px; width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content -->
        <div class="col" style="min-width:0;">
            <main class="p-3 pb-0">

                <!-- Page Header -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex align-items-center justify-content-between">
                        <div>
                            <h2 class="h4 mb-1">
                                <i class="fas fa-box-open me-2"></i>Part Requests Management
                            </h2>
                            <p class="text-muted mb-0">
                                Manage and approve pending part requests from technicians
                            </p>
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <c:if test="${not empty pendingRequests}">
                                <span class="stat-badge bg-warning bg-opacity-10 text-warning">
                                    <i class="fas fa-clock me-1"></i>
                                    Pending: ${pendingRequests.size()}
                                </span>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/inventory?action=list"
                               class="btn btn-outline-primary">
                                <i class="fas fa-warehouse me-2"></i>View Inventory
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Alert Messages -->
                <c:if test="${param.message == 'approved'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>Request approved successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${param.message == 'rejected'}">
                    <div class="alert alert-info alert-dismissible fade show" role="alert">
                        <i class="fas fa-info-circle me-2"></i>Request has been rejected.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${param.error == 'insufficient_stock'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>Insufficient stock! Request automatically rejected.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Part Requests Table -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-bottom">
                        <h5 class="mb-0">
                            <i class="fas fa-list-check me-2"></i>Pending Part Requests
                        </h5>
                    </div>

                    <c:choose>
                        <c:when test="${empty pendingRequests}">
                            <div class="card-body">
                                <div class="text-center py-5">
                                    <i class="fas fa-inbox fa-4x text-muted mb-3"></i>
                                    <h5 class="text-muted">No Pending Requests</h5>
                                    <p class="text-muted mb-0">There are no pending part requests at the moment.</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                        <tr>
                                            <th class="ps-3">#</th>
                                            <th>Request ID</th>
                                            <th>Part Name</th>
                                            <th>Quantity</th>
                                            <th>Unit Price</th>
                                            <th>Total Amount</th>
                                            <th>Status</th>
                                            <th class="text-end pe-3">Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="request" items="${pendingRequests}" varStatus="st">
                                            <tr>
                                                <td class="ps-3">${st.count}</td>
                                                <td>
                                                    <span class="badge bg-secondary bg-opacity-10 text-secondary">
                                                        #${request.workOrderPartId}
                                                    </span>
                                                </td>
                                                <td>
                                                    <strong>${request.partName}</strong>
                                                </td>
                                                <td>
                                                    <span class="badge bg-info bg-opacity-10 text-info">
                                                        ${request.quantityUsed}
                                                    </span>
                                                </td>
                                                <td>
                                                    <fmt:formatNumber value="${request.unitPrice}"
                                                                      type="number" maxFractionDigits="2" minFractionDigits="2"/> VND
                                                </td>
                                                <td>
                                                    <strong>
                                                        <fmt:formatNumber value="${request.unitPrice * request.quantityUsed}"
                                                                          type="number" maxFractionDigits="2" minFractionDigits="2"/> VND
                                                    </strong>
                                                </td>
                                                <td>
                                                    <span class="badge bg-warning">
                                                        <i class="fas fa-clock me-1"></i>PENDING
                                                    </span>
                                                </td>
                                                <td class="text-end pe-3">
                                                    <div class="btn-group" role="group">
                                                        <!-- Approve Button -->
                                                        <form action="${pageContext.request.contextPath}/stock-out"
                                                              method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="approve">
                                                            <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                                            <button type="submit" class="btn btn-sm btn-success"
                                                                    onclick="return confirm('Approve this request?\n\nPart: ${request.partName}\nQuantity: ${request.quantityUsed}')">
                                                                <i class="fas fa-check me-1"></i>Approve
                                                            </button>
                                                        </form>

                                                        <!-- Reject Button -->
                                                        <form action="${pageContext.request.contextPath}/stock-out"
                                                              method="post" class="d-inline ms-1">
                                                            <input type="hidden" name="action" value="reject">
                                                            <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                                            <input type="hidden" name="reason" value="Rejected by storekeeper">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger"
                                                                    onclick="return confirm('Reject this request?\n\nPart: ${request.partName}\nQuantity: ${request.quantityUsed}')">
                                                                <i class="fas fa-times me-1"></i>Reject
                                                            </button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>


            </main>
        </div>
    </div>
</div>


<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
