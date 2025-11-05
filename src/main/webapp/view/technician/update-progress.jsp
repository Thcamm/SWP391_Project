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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<jsp:include page="header.jsp"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/update-progress.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css">

<div class="layout">
    <jsp:include page="sidebar.jsp"/>
    <main class="main">
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
                        <span class="info-value">
        <c:choose>
            <c:when test="${not empty task.vehicleInfo}"><c:out value="${task.vehicleInfo}"/></c:when>
            <c:otherwise>-</c:otherwise>
        </c:choose>
      </span>
                    </div>

                    <div class="info-item">
                        <span class="info-label">Customer:</span>
                        <span class="info-value">
        <c:choose>
            <c:when test="${not empty task.customerName}"><c:out value="${task.customerName}"/></c:when>
            <c:otherwise>-</c:otherwise>
        </c:choose>
      </span>
                    </div>

                    <div class="info-item">
                        <span class="info-label">Task type:</span>
                        <span class="info-value"><c:out value="${task.taskType}"/></span>
                    </div>

                    <div class="info-item">
                        <span class="info-label">Priority:</span>
                        <span class="priority-badge ${fn:toLowerCase(task.priority)}">
        <c:out value="${task.priority}"/>
      </span>
                    </div>

                    <div class="info-item">
                        <span class="info-label">Estimated Hours:</span>
                        <span class="info-value">
        <c:choose>
            <c:when test="${not empty task.estimateHours}"><c:out value="${task.estimateHours}"/>h</c:when>
            <c:otherwise>-</c:otherwise>
        </c:choose>
      </span>
                    </div>

                    <div class="info-item full-width">
                        <span class="info-label">Description:</span>
                        <p class="info-value">
                            <c:choose>
                                <c:when test="${not empty task.taskDescription}"><c:out value="${task.taskDescription}"/></c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </p>
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
                    <c:set var="rt" value="${not empty returnTo ? returnTo : pageContext.request.requestURL}" />
                    <c:if test="${not empty pageContext.request.queryString}">
                        <c:set var="returnTo" value="${returnTo}?${pageContext.request.queryString}" />
                    </c:if>

                    <input type="hidden" name="returnTo" value="${returnTo}" />

                    <div class="form-group">
                        <label for="progressPercentage">Progress Percentage *</label>
                        <select name="progressPercentage" id="progressPercentage" class="form-control" required>
                            <option value="">-- Select Progress --</option>
                            <c:forEach begin="0" end="100" step="10" var="percent">
                                <option value="${percent}" ${percent == task.progressPercentage ? 'selected' : ''}>
                                        ${percent}%
                                </option>
                            </c:forEach>
                        </select>
                        <small class="form-hint">
                            Current progress:
                            <c:choose>
                                <c:when test="${not empty task.progressPercentage}"><c:out value="${task.progressPercentage}"/>%</c:when>
                                <c:otherwise>0%</c:otherwise>
                            </c:choose>
                        </small>
                    </div>

                    <div class="form-group">
                        <label for="notes">Progress Notes</label>
                        <textarea id="notes"
                                  name="notes"
                                  rows="6"
                                  maxlength="2000"
                                  class="form-control"
                                  placeholder="Enter detailed progress notes, issues encountered, ..."><c:out value="${task.notes}"/></textarea>
                        <small class="form-hint">Optional: Add notes about your progress, any issues.</small>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">😇 Update Progress</button>
                        <a href="${pageContext.request.contextPath}/technician/tasks" class="btn btn-secondary">Cancel</a>
                    </div>

                    <c:if test="${not empty task.notes}">
                        <div class="progress-history-card">
                            <h3>Previous Notes</h3>
                            <div class="notes-display">
                                <p><c:out value="${task.notes}"/></p>
                            </div>
                        </div>
                    </c:if>
                </form>
            </div>
        </section>

    </main>


</div>


<jsp:include page="footer.jsp"/>