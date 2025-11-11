<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Reply to Support Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <%-- XÓA BỎ KHỐI <style> CŨ CỦA BẠN --%>

    <style>
        .form-control:focus,
        .form-select:focus {
            border-color: #4f46e5;
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
        }
        .form-label {
            font-weight: 600;
        }
        /* CSS cho input group */
        .input-group-text {
            background-color: #f9fafb;
            border-right: none;
            color: #6b7280;
        }
        .input-group .form-control {
            border-left: none;
        }
        .input-group:focus-within .input-group-text {
            border-color: #4f46e5;
            background-color: white;
            color: #4f46e5;
        }
    </style>
</head>

<body class="bg-light"> <%-- Đổi: Dùng bg-light (nhất quán) --%>

<jsp:include page="/view/customerservice/result.jsp" />
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;"> <%-- Đổi: Dùng padding chuẩn --%>

                <%-- Sửa: Dùng lại layout chuẩn của các trang khác --%>
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);">

                    <div class="container" style="max-width: 1000px; margin: 0 auto;">

                        <div class="mb-4">
                            <h2 style="font-weight: 700; color: #111827;">
                                <i class="bi bi-reply-fill me-2"></i>Reply to Customer
                            </h2>
                            <p class="text-muted">
                                Replying to Request #${requestId} for customer ${toEmail}
                            </p>
                        </div>

                        <div class="card shadow-sm border-0" style="border-radius: 12px;">
                            <div class="card-body p-4">
                                <form action="${pageContext.request.contextPath}/customerservice/reply-request" method="post">
                                    <input type="hidden" name="requestId" value="${requestId}" />

                                    <div class="mb-3">
                                        <label for="toEmail" class="form-label">Customer Email</label>
                                        <%-- Sửa: Dùng form-control-plaintext cho đẹp hơn --%>
                                        <input type="email" id="toEmail" name="toEmail" class="form-control-plaintext" value="${toEmail}" readonly
                                               style="font-weight: 600; font-size: 1.1rem;"/>
                                    </div>

                                    <div class="mb-3">
                                        <label for="subject" class="form-label">Subject <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="bi bi-card-heading"></i></span>
                                            <input type="text" id="subject" name="subject" class="form-control" placeholder="Enter subject..." required />
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="message" class="form-label">Message <span class="text-danger">*</span></label>
                                        <textarea id="message" name="message" class="form-control" rows="10" placeholder="Write your message here..." required></textarea>
                                    </div>

                                    <div class="d-flex justify-content-end gap-2 mt-4 pt-3 border-top">
                                        <%-- Giữ nguyên backUrlForJsp của bạn (rất tốt!) --%>
                                        <a href="${backUrlForJsp}" class="btn btn-secondary">
                                            <i class="bi bi-x-circle me-1"></i> Back
                                        </a>
                                        <%-- Sửa: Dùng btn-primary cho đồng bộ --%>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-send-fill me-1"></i> Send Reply
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Xóa footer lồng nhau --%>
                <%-- <jsp:include page="/view/customerservice/footer.jsp"/> --%>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>