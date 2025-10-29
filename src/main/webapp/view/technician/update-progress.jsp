<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/26/2025
  Time: 10:48 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/update-progress.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css">

<section class="update-progress-section">
    <div class="page-header">
        <h2>
            🤖Update task progress
        </h2>

        <a href="${pageContext.request.contextPath}/technician/home" class="btn btn-secondary">
            &laquo; Back to Home
        </a>
    </div>

    <jsp:include page="message-display.jsp"/>

    <!-- task imformation card -->

    <div class="task-info-card">
        <h3>Task Information</h3>
        <div class="info-grid">
            <div class="info-item">
                <span class="info-label">Vehicle:</span>
                <span class="info-value">${task.vehicleInfo}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Customer:</span>
                <span class="info-value">${task.customerName}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Vehicle:</span>
                <span class="info-value">${task.vehicleInfo}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Customer:</span>
                <span class="info-value">${task.customerName}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Task type:</span>
                <span class="info-value">${task.taskType}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Priority:</span>
                <span class="priority-badge ${task.priority.cssClass}">${task.priority}</span>
            </div>
            <div class="info-item">
                <span class="info-label">Estimated Hours:</span>
                <span class="info-value">${task.estimateHours}h</span>
            </div>
            <div class="info-item full-width">
                <span class="info-label">Description:</span>
                <p class="info-value">${task.taskDescription}</p>
            </div>
        </div>
    </div>

    <!-- update progress form -->
    <div class="form-card">
        <h3>
            Update progress
        </h3>
        <form action="${pageContext.request.contextPath}/technician/update-progress"
        method="post" class="progress-form">
            <input type="hidden" name="assignmentId" value="${task.assignmentID}">
            <input type="hidden" name="action" value="update">

            <div class="form-group">
                <label for="progressPercentage">Progress Percentage *</label>
                <select name="progressPercentage" id="progressPercentage" class="form-control" required>
                    <option value="">--Select Progress --</option>
                    <c:forEach begin="0" end="100" step="5" var="percent">
                        <option value="${percent}" ${percent == task.progressPercentage ? 'selected' : ''}>
                                ${percent}%
                        </option>
                    </c:forEach>
                </select>
                <small class="form-hint">Current progress: ${task.progressPercentage}%</small>

            </div>

            <div class="form-group">
                <label for="notes">Progress Notes</label>
                <textarea id="notes"
                          name="notes"
                          rows="6"
                          class="form-control"
                          placeholder="Enter details progress notes, issues encountered, ...">${task.notes}</textarea>
                <small class="form-hint">Optional: Add notes about your progress, any issues  </small>

            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    😇Update Progress
                </button>
                <a href="${pageContext.request.contextPath}/technician/home" class="btn btn-secondary">
                    Cancel
                </a>
            </div>

            <c:if test="${not empty task.notes}">
                <div class="progress-history-card">
                    <h3>Previous Notes</h3>
                    <div class="notes-display">
                        <p>${task.notes}</p>
                    </div>
                </div>
            </c:if>

        </form>

    </div>
</section>

<jsp:include page="footer.jsp"/>