<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Support Request Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<jsp:include page="/view/customerservice/result.jsp" />
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                             border: 1px solid #e5e7eb;
                             border-radius: 12px;
                             padding: 1.5rem 2.5rem 2.5rem 2.5rem; <%-- Sửa padding --%>
                             min-height: calc(100vh - 64px - 1.25rem);
                             display: flex; flex-direction: column;
                     <%-- XÓA: align-items và justify-content --%>
                             ">

                    <div class="container-fluid" style="padding: 0;">

                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 style="margin: 0; font-size: 24px; font-weight: 700; color: #111827;">
                                    <i class="bi bi-ticket-detailed me-2"></i>Support Request Detail
                                </h2>
                                <p style="margin: 0.5rem 0 0 0; color: #6b7280; font-size: 14px;">Review and respond to customer request #${supportRequest.requestId}</p>
                            </div>
                        </div>

                        <div class="row g-4">

                            <div class="col-lg-7">
                                <div class="card" style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                                    <div class="card-header bg-light" style="border-bottom: 1px solid #e5e7eb; border-radius: 12px 12px 0 0;">
                                        <h5 class="mb-0">Core Information</h5>
                                    </div>
                                    <div class="card-body p-4">
                                        <%-- Dùng Definition List (dl) cho đẹp --%>
                                        <dl class="row mb-0">
                                            <dt class="col-sm-4">Request ID:</dt>
                                            <dd class="col-sm-8">#${supportRequest.requestId}</dd>

                                            <dt class="col-sm-4">Customer:</dt>
                                            <dd class="col-sm-8">${customer.fullName})</dd>
                                            <dt class="col-sm-4">Customer:</dt>
                                            <dd class="col-sm-8">${customer.email}</dd>
                                            <dt class="col-sm-4">Customer:</dt>
                                            <dd class="col-sm-8">${customer.phoneNumber}</dd>
                                            <dt class="col-sm-4">Category:</dt>
                                            <dd class="col-sm-8">
                                                <span class="badge bg-secondary bg-opacity-10 text-dark-emphasis">${categoryMap[supportRequest.categoryId]}</span>
                                            </dd>

                                            <dt class="col-sm-4">Status:</dt>
                                            <dd class="col-sm-8">
                                                <c:choose>
                                                    <c:when test="${supportRequest.status == 'RESOLVED'}">
                                                        <span class="badge status-resolved">RESOLVED</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form action="${pageContext.request.contextPath}/customerservice/view-support-request"
                                                              method="post" class="d-inline">
                                                            <input type="hidden" name="requestId" value="${supportRequest.requestId}">
                                                            <input type="hidden" name="redirectUrl"
                                                                   value="${pageContext.request.contextPath}/customerservice/view-support-request?id=${supportRequest.requestId}">

                                                            <select name="status" class="form-select form-select-sm d-inline-block w-auto
                                                                ${supportRequest.status == 'PENDING' ? 'status-pending' : ''}
                                                                ${supportRequest.status == 'INPROGRESS' ? 'status-inprogress' : ''}"
                                                                    onchange="this.form.submit()">
                                                                <option value="PENDING" <c:if test="${supportRequest.status == 'PENDING'}">selected</c:if>>PENDING</option>
                                                                <option value="INPROGRESS" <c:if test="${supportRequest.status == 'INPROGRESS'}">selected</c:if>>INPROGRESS</option>
                                                                <option value="RESOLVED">RESOLVED</option>
                                                            </select>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </dd>

                                            <dt class="col-sm-4 mt-2">Created At:</dt>
                                            <dd class="col-sm-8 mt-2">${supportRequest.createdAt}</dd>

                                            <dt class="col-sm-4">Updated At:</dt>
                                            <dd class="col-sm-8">${supportRequest.updatedAt}</dd>

                                            <c:if test="${supportRequest.appointmentId != null}">
                                                <dt class="col-sm-4 mt-2">Related Appointment:</dt>
                                                <dd class="col-sm-8 mt-2">
                                                    <a href="#">#${supportRequest.appointmentId}</a>
                                                </dd>
                                            </c:if>

                                            <c:if test="${supportRequest.workOrderId != null}">
                                                <dt class="col-sm-4 mt-2">Related Work Order:</dt>
                                                <dd class="col-sm-8 mt-2">
                                                    <a href="#">#${supportRequest.workOrderId}</a>
                                                </dd>
                                            </c:if>
                                        </dl>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-5">
                                <div class="card" style="border: none; box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-radius: 12px;">
                                    <div class="card-header bg-light" style="border-bottom: 1px solid #e5e7eb; border-radius: 12px 12px 0 0;">
                                        <h5 class="mb-0">Description & Attachments</h5>
                                    </div>
                                    <div class="card-body p-4">
                                        <h6 class="fw-bold">Description:</h6>
                                        <p class="bg-light p-3 rounded-3" style="border: 1px dashed #e5e7eb;">
                                            ${supportRequest.description}
                                        </p>

                                        <hr class="my-3">

                                        <h6 class="fw-bold">Attachments:</h6>
                                        <div>
                                            <c:forEach var="file" items="${fn:split(supportRequest.attachmentPath, ';')}">
                                                <c:if test="${not empty file}">
                                                    <c:url var="fileUrl" value="/customerservice/view-attachment">
                                                        <c:param name="file" value="${file}"/>
                                                    </c:url>

                                                    <c:set var="lowerFile" value="${fn:toLowerCase(file)}" />
                                                    <c:set var="isImage" value="${fn:endsWith(lowerFile, '.png') || fn:endsWith(lowerFile, '.jpg') || fn:endsWith(lowerFile, '.jpeg') || fn:endsWith(lowerFile, '.gif') || fn:endsWith(lowerFile, '.bmp') || fn:endsWith(lowerFile, '.webp')}" />

                                                    <c:choose>
                                                        <c:when test="${isImage}">
                                                            <a href="#"
                                                               data-bs-toggle="modal"
                                                               data-bs-target="#imageModal"
                                                               data-img-src="${fileUrl}"
                                                               data-img-title="${file}"
                                                               class="d-inline-block m-1"
                                                               title="Click to view: ${file}">
                                                                <img src="${fileUrl}" alt="${file}"
                                                                     style="width: 80px; height: 80px; object-fit: cover; border: 1px solid #ddd; border-radius: 8px; cursor: pointer;">
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <%-- Sửa: Style cho file không phải ảnh --%>
                                                            <div class="mb-1">
                                                                <a href="${fileUrl}" target="_blank" class="text-decoration-none">
                                                                    <i class="bi bi-file-earmark-arrow-down me-1"></i> ${file}
                                                                </a>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="mt-4 pt-3 border-top d-flex justify-content-end gap-2">
                            <c:choose>
                                <c:when test="${supportRequest.status == 'INPROGRESS'}">
                                    <a href="${pageContext.request.contextPath}/customerservice/reply-request?id=${supportRequest.requestId}&email=${customer.email}"
                                       class="btn btn-success">
                                        <i class="bi bi-reply me-1"></i> Reply
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-success" disabled style="opacity:0.5;">
                                        <i class="bi bi-reply me-1"></i> Reply
                                    </button>
                                </c:otherwise>
                            </c:choose>

                            <%-- Sửa: Dùng javascript:history.back() để quay lại trang trước (giữ filter) --%>
                            <a href="javascript:history.back()" class="btn btn-secondary ms-2">
                                <i class="bi bi-arrow-left me-1"></i> Back
                            </a>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>

<div class="modal fade" id="imageModal" tabindex="-1" aria-labelledby="imageModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="imageModalLabel">Xem ảnh</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body text-center">
                <img src="" id="modalImage" class="img-fluid" alt="Preview">
            </div>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>

<style>
    /* CSS cho Status Badges & Dropdowns (Giống trang list) */
    .badge, .form-select-sm {
        font-size: 0.8rem;
        padding: 0.4em 0.7em;
        font-weight: 600;
    }
    .status-resolved {
        background-color: #d1fae5;
        color: #065f46;
    }
    .form-select.status-pending {
        border-color: #fcd34d;
        background-color: #fef9c3;
        color: #92400e;
        font-weight: 600;
    }
    .form-select.status-inprogress {
        border-color: #93c5fd;
        background-color: #dbeafe;
        color: #1e40af;
        font-weight: 600;
    }

    /* CSS cho Definition List (dl) */
    dt {
        font-weight: 600;
        color: #374151;
    }
    dd {
        color: #111827;
    }
    dt, dd {
        padding-bottom: 0.5rem;
    }
</style>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        var imageModal = document.getElementById('imageModal');
        if (imageModal) {
            imageModal.addEventListener('show.bs.modal', function (event) {
                var triggerElement = event.relatedTarget;
                var imageSrc = triggerElement.getAttribute('data-img-src');
                var imageTitle = triggerElement.getAttribute('data-img-title');
                var modalTitle = imageModal.querySelector('.modal-title');
                var modalImage = imageModal.querySelector('#modalImage');
                modalTitle.textContent = imageTitle;
                modalImage.src = imageSrc;
            });
            imageModal.addEventListener('hidden.bs.modal', function () {
                var modalImage = imageModal.querySelector('#modalImage');
                modalImage.src = '';
            });
        }
    });
</script>
</body>
</html>