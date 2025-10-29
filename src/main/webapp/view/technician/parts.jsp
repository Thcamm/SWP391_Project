<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/26/2025
  Time: 3:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ftm" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/parts.css">

<div class="parts-page">
    <div class="page-header">
        <h2>Parts Request</h2>
        <a href="${pageContext.request.contextPath}/technician/home" class="btn btn-secondary">
            &laquo; Back to Home
        </a>
    </div>
    <jsp:include page="message-display.jsp"/>

    <div class="parts-stats">
        <div class="stat-box pending">
            <div class="stat-number">${partsStats.pendingCount}</div>
            <div class="stat-label">Pending</div>
        </div>
        <div class="stat-box approved">
            <div class="stat-number">${partsStats.approvedCount}</div>
            <div class="stat-label">Approved</div>
        </div>
        <div class="stat-box rejected">
            <div class="stat-number">${partsStats.rejectedCount}</div>
            <div class="stat-label">Rejected</div>
        </div>
        <div class="stat-box delivery">
            <div class="stat-number">${partsStats.deliveredCount}</div>
            <div class="stat-label">Delivered</div>
        </div>
    </div>

    <div class="parts-container">
        <!-- left column : request form -->
        <div class="request-form-card">
            <h3>Request new part</h3>

            <form action="${pageContext.request.contextPath}/technician/parts" method="post" class="parts-request-form">
                <div class="form-group task-select-group">
                    <label for="taskId">Select Task <span class="required">*</span></label>
                    <select name="taskId" id="taskId" class="form-control" required>
                        <option value="">--SELECT a task you are working on --</option>
                        <c:forEach items="${inProgressTasks}" var="task">
                            <option value="${task.assignmentID}">
                                [${task.assignmentID}] - ${task.vehicleInfo} - ${task.serviceInfo}
                            </option>
                        </c:forEach>
                    </select>
                    <small class="form-hint">Only tasks you're currently working on are shown</small>
                </div>

                <!-- Part search -->
                <div class="form-group">
                    <label>Search Part <span class="required">*</span></label>
                    <div style="display: flex; gap: 10px;">
                        <input type="text"
                        id="partSearch"
                        class="form-control"
                        placeholder="Search by part name, code, category ...">
                        <button type="button"
                                onclick="this.form.submit();"
                                class="btn btn-filter"
                                style="white-space: nowrap;">
                            Search 😶‍🌫️
                        </button>
                    </div>
                    <small class="form-hint">Search for parts in inventory</small>
                </div>

               <!--Part details selection -->
                <div class="form-group">
                    <label for="partDetailId">Select Part Detail <span class="required">*</span></label>
                    <select name="partDetailId" id="partDetailId" class="form-control" required>
                        <option value="">--First search for part above--</option>
                        <c:forEach items="${availableParts}" var="partDetail">
                            <option value="${partDetail.partDetailID}" data-price="${partDetail.unitPrice}" data-available="${partDetail.quantity}">

                                ${partDetail.partName} - ${partDetail.typeName}
                                (Code: ${partDetail.partCode})
                                - Available: ${partDetail.quantity}
                                - ${partDetail.unitPrice} VND
                            </option>
                        </c:forEach>
                    </select>
                    <small class="form-hint">Select specific part type and specification</small>
                </div>

                <div class="form-group">
                    <label for="quantity">Quantity needed <span class="required">*</span></label>
                    <input type="number"
                           name="quantity"
                           id="quantity"
                           class="form-control"
                           min="1"
                            value="1"
                           placeholder="Enter quantity needed"
                           required>
                    <span class="form-hint" id="availableQtyHint">Select a part to see available quantity</span>

                </div>

                <!--priority-->
                <div class="form-group">
                    <label for="priority">Priority <span class="required">*</span></label>
                    <select name="priority" id="priority" class="form-control" required>
                        <option value="NORMAL">Normal</option>
                        <option value="URGENT">Urgent - Task is block</option>
                    </select>

                    <small class="form-hint">Select "Urgent" only if the task cannot proceed without this part</small>

                </div>

                <!-- reason -->
                <div class="form-group">
                    <label for="reason">Reason/ Usage description <span class="required">*</span></label>
                    <textarea name="reason"
                              id="reason"
                              class="form-control"
                              rows="4"
                              placeholder="Explain why this part is needed"
                              required></textarea>

                    <small class="form-hint">Be sepecific to help managers approve request faster</small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        Submit Part Request🐧
                    </button>

                </div>
            </form>

        </div>

        <!-- right column : existing requests -->
        <div class="my-requests-card">
            <h3>My recent requests</h3>
            <c:choose>
                <c:when test="${empty myRequests}">
                    <div class="empty-state">
                        <p>No parts request yet💣</p>
                        <p>Submit your first request using the form on the left</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="request-list">
                        <c:forEach items="${myRequests}" var="request">
                            <div class="request-item">
                                <div class="request-header">
                                    <span class="request_id">Request #${request.workOrderPartID}</span>
                                    <span class="request-date">
                                        <ftm:formatDate value="${request.requestDate}" pattern="dd/MM/yyyy HH:mm"/>
                                    </span>
                                </div>

                                <div class="request-body">
                                    <div class="request-row">
                                        <span class="request-label">Part:</span>
                                        <span class="request-value">${request.partName}</span>
                                    </div>

                                    <div class="request-row">
                                        <span class="request-label">Type:</span>
                                        <span class="request-value">${request.typeName}</span>
                                    </div>

                                    <div class="request-row">
                                        <span class="request-label">Code:</span>
                                        <span class="request-value">${request.partCode}</span>
                                    </div>

                                    <div class="request-row">
                                        <span class="request-label">Quantity:</span>
                                        <span class="request-value">${request.quantityRequested}</span>
                                    </div>

                                    <div class="request-row">
                                        <span class="request-label">Unit price:</span>
                                        <span class="request-value">
                                                <fmt:formatNumber value="${request.unitPrice}" type="currency" currencySymbol="VND " />
                                        </span>
                                    </div>

                                    <div class="request-row">
                                        <span class="request-label">Task:</span>
                                        <span class="request-value">${request.taskAssignmentID}</span>
                                    </div>

                                    <c:if test="${not empty request.reason}">
                                        <div class="request-row">
                                            <span class="request-label">Reason:</span>
                                            <span class="request-value">
                                                ${fn:substring(request.reason, 0, 80)}
                                                <c:if test="${fn:length(request.reason) > 80}">...</c:if>
                                            </span>
                                        </div>
                                    </c:if>

                                </div>

                                <div class="request-footer">
                                    <span class="status-badge ${fn:toLowerCase(request.requestStatus)}">
                                        ${request.requestStatus}
                                    </span>
                                    <c:if test="${not empty request.priority && request == 'URGENT'}">
                                        <span class="priority-badge urgent">URGENT😫</span>
                                    </c:if>

                                </div>

                            </div>
                        </c:forEach>

                    </div>
                </c:otherwise>
            </c:choose>

        </div>

    </div>

</div>

<jsp:include page="footer.jsp"/>