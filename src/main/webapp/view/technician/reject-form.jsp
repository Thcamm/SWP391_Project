<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css"/>

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <!-- Keep sidebar + main on one row -->
    <div class="row g-0 flex-nowrap">
        <!-- Sidebar -->
        <div class="col-auto" style="flex:0 0 280px; width:280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main -->
        <div class="col" style="min-width:0;">
            <main class="p-3">
                <!-- Breadcrumb / Title -->
                <div class="card border-0 shadow-sm mb-3">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <h2 class="h5 mb-0">Reject Task</h2>
                        <a class="btn btn-light btn-sm" href="${returnTo}">← Back</a>
                    </div>
                </div>

                <jsp:include page="message-display.jsp"/>

                <!-- Form -->
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white">
                        <strong>Provide a reason to reject this assignment</strong>
                    </div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/technician/tasks-action" method="post" class="needs-validation" novalidate>
                            <!-- Hidden params -->
                            <input type="hidden" name="action" value="reject"/>
                            <input type="hidden" name="stage" value="submit"/>
                            <input type="hidden" name="assignmentId" value="${assignmentId}"/>
                            <input type="hidden" name="returnTo" value="${returnTo}"/>

                            <div class="mb-3">
                                <label for="reason" class="form-label">Reason <span class="text-danger">*</span></label>
                                <textarea id="reason"
                                          name="reason"
                                          class="form-control"
                                          rows="6"
                                          required
                                          minlength="5"
                                          maxlength="1000"
                                          placeholder="Please describe why you cannot take this task..."></textarea>
                                <div class="form-text">Min 5 characters, Max 2000.</div>
                                <div class="invalid-feedback">Please enter a reason (at least 5 characters).</div>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-outline-danger">Submit Reject</button>
                                <a href="${returnTo}" class="btn btn-secondary">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>

            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<script>
    // client-side Bootstrap validation (optional)
    (function () {
        'use strict';
        const forms = document.querySelectorAll('.needs-validation');
        Array.prototype.slice.call(forms).forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault(); // chan de gui form
                    event.stopPropagation(); // chan de gui form
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
