<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Bootstrap 5 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>


<div class="container-fluid px-0">
    <div class="row g-0">

        <!-- Main -->
        <div class="col" style="min-width:0;">
            <main class="p-3 pb-0">
                <!-- Topbar -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex align-items-center justify-content-between">
                        <div>
                            <h2 class="h4 mb-1">📦 Part Requests Management</h2>
                            <p class="text-muted mb-0">
                                Manage and approve pending part requests
                            </p>
                        </div>
                        <div class="d-flex align-items-center gap-3">
                            <div class="text-end">
                                <span class="badge text-bg-warning">Pending: ${pendingRequests.size()}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Message Display -->
                <c:if test="${param.message == 'approved'}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="bi bi-check-circle me-2"></i>✅ Request approved successfully!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${param.message == 'rejected'}">
                    <div class="alert alert-info alert-dismissible fade show">
                        <i class="bi bi-info-circle me-2"></i>ℹ️ Request rejected.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${param.error == 'insufficient_stock'}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="bi bi-exclamation-triangle me-2"></i>⚠️ Insufficient stock! Request automatically rejected.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Part Requests Table -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-header bg-white border-0 d-flex align-items-center justify-content-between">
                        <h3 class="h5 mb-0">📋 Pending Part Requests</h3>
                    </div>

                    <c:choose>
                        <c:when test="${empty pendingRequests}">
                            <div class="card-body">
                                <div class="alert alert-light mb-0 text-center py-5">
                                    <div class="display-1 mb-3">📭</div>
                                    <p class="mb-0">No pending requests at the moment.</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="card-body pt-0">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-light">
                                        <tr>
                                            <th>#</th>
                                            <th>Request ID</th>
                                            <th>Part Name</th>
                                            <th>Quantity</th>
                                            <th>Unit Price</th>
                                            <th>Total Amount</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="request" items="${pendingRequests}" varStatus="st">
                                            <tr>
                                                <td>${st.count}</td>
                                                <td><strong class="font-monospace">#${request.workOrderPartId}</strong></td>
                                                <td>${request.partName}</td>
                                                <td>
                                                    <span class="badge text-bg-info">${request.quantityUsed}</span>
                                                </td>
                                                <td class="font-monospace">
                                                    <fmt:formatNumber value="${request.unitPrice}" type="currency"/>
                                                </td>
                                                <td class="font-monospace fw-semibold">
                                                    <fmt:formatNumber value="${request.unitPrice * request.quantityUsed}" type="currency"/>
                                                </td>
                                                <td>
                                                    <span class="badge text-bg-warning">
                                                        ⏳ PENDING
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="d-flex justify-content-end gap-2">
                                                        <!-- Approve Button -->
                                                        <form action="${pageContext.request.contextPath}/stock-out"
                                                              method="post" class="m-0">
                                                            <input type="hidden" name="action" value="approve">
                                                            <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                                            <button type="submit" class="btn btn-success btn-sm"
                                                                    onclick="return confirm('Approve this request?\n\nPart: ${request.partName}\nQuantity: ${request.quantityUsed}')">
                                                                ✓ Approve
                                                            </button>
                                                        </form>

                                                        <!-- Reject Button -->
                                                        <form action="${pageContext.request.contextPath}/stock-out"
                                                              method="post" class="m-0">
                                                            <input type="hidden" name="action" value="reject">
                                                            <input type="hidden" name="workOrderPartId" value="${request.workOrderPartId}">
                                                            <input type="hidden" name="reason" value="Rejected by storekeeper">
                                                            <button type="submit" class="btn btn-outline-danger btn-sm"
                                                                    onclick="return confirm('Reject this request?\n\nPart: ${request.partName}\nQuantity: ${request.quantityUsed}')">
                                                                ✕ Reject
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

                <!-- Quick Actions -->
                <div class="row g-3 mb-4">
                    <div class="col-12">
                        <div class="card border-0 shadow-sm">
                            <div class="card-header bg-white border-0">
                                <h3 class="h6 mb-0">⚡ Quick Actions</h3>
                            </div>
                            <div class="card-body">
                                <div class="row g-3">
                                    <div class="col-12 col-sm-6 col-md-4">
                                        <a class="btn btn-outline-primary w-100 py-3"
                                           href="${pageContext.request.contextPath}/inventory">
                                            📊 View Inventory
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    </div>
</div>

