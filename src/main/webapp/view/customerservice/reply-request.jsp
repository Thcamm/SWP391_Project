<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Reply to Support Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body class="bg-light">

<jsp:include page="/view/customerservice/result.jsp" />

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           align-items: center; justify-content: center;
                            text-align: center;">
                    <!-- Nội dung trang home của bạn ở đây -->
                    <div class="container mt-5">

                        <div class="card shadow-sm">
                            <div class="card-header bg-primary text-white">
                                <h4 class="mb-0">Reply to Customer</h4>
                            </div>

                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/customerservice/reply-request" method="post">

                                    <!-- Hidden: requestId -->
                                    <input type="hidden" name="requestId" value="${requestId}" />

                                    <!-- Customer Email -->
                                    <div class="mb-3">
                                        <label class="form-label">Customer Email</label>
                                        <input type="email" name="toEmail" class="form-control"
                                               value="${toEmail}" readonly />
                                    </div>

                                    <!-- Subject -->
                                    <div class="mb-3">
                                        <label class="form-label">Subject</label>
                                        <input type="text" name="subject" class="form-control"
                                               placeholder="Enter subject..." required />
                                    </div>

                                    <!-- Message Content -->
                                    <div class="mb-3">
                                        <label class="form-label">Message</label>
                                        <textarea name="message" class="form-control" rows="6"
                                                  placeholder="Write your message here..." required></textarea>
                                    </div>

                                    <div class="d-flex justify-content-end gap-2">
                                        <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="btn btn-secondary">Back</a>
                                        <button type="submit" class="btn btn-success">Send</button>
                                    </div>
                                </form>

                            </div>
                        </div>
                        <jsp:include page="/view/customerservice/footer.jsp"/>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
