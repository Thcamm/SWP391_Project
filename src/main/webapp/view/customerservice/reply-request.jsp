<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Reply to Support Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            background-color: #f5f5f5;
            color: #212529;
            font-family: "Inter", "Segoe UI", sans-serif;
        }

        .content-card {
            background: #fff;
            border-radius: 14px;
            border: 1px solid #e0e0e0;
            padding: 2rem 2.5rem;
            margin: 2rem auto;
            /*max-width: ;*/
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }

        .card-header {
            background-color: #fff;
            border-bottom: 1px solid #dee2e6;
            font-weight: 600;
            font-size: 1.25rem;
            color: #212529;
            display: flex;
            align-items: center;
            padding: 1rem 1.5rem;
        }

        .card-header i {
            color: #6c757d;
            margin-right: 0.5rem;
        }

        .form-label {
            font-weight: 500;
            color: #343a40;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #ced4da;
            padding: 0.7rem;
            background-color: #fdfdfd;
            transition: all 0.2s ease;
        }

        .form-control:focus {
            border-color: #6c757d;
            box-shadow: 0 0 0 0.1rem rgba(108,117,125,0.2);
        }

        textarea.form-control {
            resize: none;
        }

        .btn {
            border-radius: 8px;
            font-weight: 500;
            padding: 0.6rem 1.4rem;
            transition: all 0.2s ease;
        }

        .btn-dark {
            background-color: #212529;
            border: none;
        }

        .btn-dark:hover {
            background-color: #343a40;
        }

        .btn-outline-dark {
            color: #212529;
            border-color: #212529;
        }

        .btn-outline-dark:hover {
            background-color: #212529;
            color: #fff;
        }

        .divider {
            height: 1px;
            background-color: #e9ecef;
            margin: 1.5rem 0;
        }
    </style>
</head>

<body>

<jsp:include page="/view/customerservice/result.jsp" />
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <!-- Sidebar -->
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content -->
        <div class="col">
            <main class="main p-4">
                <div class="content-card">
                    <div class="card border-0">
                        <div class="card-header">
                            <i class="bi bi-envelope"></i>
                            Reply to Customer
                        </div>

                        <div class="card-body px-2 pt-4 pb-2">
                            <form action="${pageContext.request.contextPath}/customerservice/reply-request" method="post">
                                <input type="hidden" name="requestId" value="${requestId}" />

                                <div class="mb-3">
                                    <label class="form-label">Customer Email</label>
                                    <input type="email" name="toEmail" class="form-control" value="${toEmail}" readonly />
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Subject</label>
                                    <input type="text" name="subject" class="form-control" placeholder="Enter subject..." required />
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Message</label>
                                    <textarea name="message" class="form-control" rows="6" placeholder="Write your message here..." required></textarea>
                                </div>

                                <div class="divider"></div>

                                <div class="d-flex justify-content-end gap-2 mt-3">
                                    <a href="${pageContext.request.contextPath}/customerservice/view-support-request" class="btn btn-outline-dark">
                                        <i class="bi bi-arrow-left"></i> Back
                                    </a>
                                    <button type="submit" class="btn btn-dark">
                                        <i class="bi bi-send-fill"></i> Send
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <jsp:include page="/view/customerservice/footer.jsp"/>
            </main>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
