<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Support Request - CarSpa VN</title>
    <!-- Bootstrap & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<jsp:include page="/common/customer/header.jsp" />
<jsp:include page="/view/customerservice/result.jsp" />
<main class="container py-5" style="margin-top: 130px;">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card p-4 shadow-sm">
                <h2 class="text-center mb-4" style="color: #6a1b9a;">Support Request / Report a Bug</h2>

                <form action="${pageContext.request.contextPath}/customer/create-support-request"
                      method="post"
                      enctype="multipart/form-data">

                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email"
                               value="${sessionScope.user.email}" required>
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone Number</label>
                        <input type="text" class="form-control" id="phone" name="phone"
                               value="${sessionScope.user.phoneNumber}" required>
                    </div>

                    <div class="mb-3">
                        <label for="categoryId" class="form-label">Category</label>
                        <select id="categoryId" name="categoryId" class="form-select" required>
                            <option value="">-- Select option --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.categoryId}">${cat.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="mb-3">
                        <label for="workOrderId" class="form-label">Order code (if any)</label>
                        <input type="text" class="form-control" id="workOrderId" name="workOrderId"
                               placeholder="Enter Work Order Code">
                    </div>

                    <div class="mb-3">
                        <label for="appointmentId" class="form-label">Appointment code (if any)</label>
                        <input type="text" class="form-control" id="appointmentId" name="appointmentId"
                               placeholder="Enter Appointment Code">
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Detail</label>
                        <textarea id="description" name="description" class="form-control" rows="5"
                                  placeholder="Enter the content of your support request..." required></textarea>
                    </div>

                    <div class="mb-3">
                        <label for="attachment" class="form-label">Attached photo (if any)</label>
                        <input type="file" class="form-control" id="attachment" name="attachment" accept="image/*">
                        <div id="fileError" class="text-danger mt-1"></div>
                        <img id="previewImage" class="img-fluid mt-2 rounded shadow-sm" alt="Preview" />
                    </div>

                    <div class="text-center">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-paper-plane"></i> Send request
                        </button>
                    </div>
                </form>

                <div class="mt-3 text-center">
                    <a href="${pageContext.request.contextPath}/support-faq" class="text-decoration-none">
                        <i class="fas fa-arrow-left"></i> Return FAQ
                    </a>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/common/customer/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/create-support-request.js"></script>
