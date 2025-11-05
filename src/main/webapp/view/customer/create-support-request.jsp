<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Support Request - CarSpa VN</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
          referrerpolicy="no-referrer" />

    <style>
        :root {
            --primary-color: #3a3a3c;
            --primary-hover: #757577;
            --secondary-color: #000000;
            --light-bg: #f8f9fa;
        }

        body {
            background: linear-gradient(135deg, #333333 0%, #ffffff 100%);
            min-height: 100vh;
        }

        .support-container {
            margin-top: 50px;
            margin-bottom: 50px;
        }

        .support-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .card-header-custom {
            background: linear-gradient(0deg,  #333333 0%, #ededed 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }

        .card-header-custom h2 {
            margin: 0;
            font-weight: 700;
            font-size: 28px;
        }

        .card-header-custom p {
            margin: 10px 0 0 0;
            opacity: 0.9;
            font-size: 14px;
        }

        .card-body-custom {
            padding: 40px;
        }

        .form-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        /* Tối ưu icon trong label */
        .form-label i {
            font-size: 18px;              /* Kích thước vừa phải, dễ nhìn */
            min-width: 18px;              /* Giữ khoảng cách ổn định */
            color: #0a0a0a;  /* Màu đồng bộ với giao diện */
            vertical-align: middle;       /* Căn giữa theo chiều dọc */
            line-height: 1.2;             /* Tránh bị cắt trên/dưới */
        }


        .form-control, .form-select {
            border: 2px solid #e0e0e0;
            border-radius: 10px;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }

        .form-control:focus, .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 0.2rem rgb(217, 217, 217);
        }

        .form-control:read-only {
            background-color: #f5f5f5;
            cursor: not-allowed;
        }

        .upload-area {
            border: 2px dashed #d0d0d0;
            border-radius: 15px;
            padding: 30px;
            text-align: center;
            background: #fafafa;
            transition: all 0.3s ease;
            cursor: pointer;
            position: relative;
            min-height: 150px;
        }

        .upload-content {
            pointer-events: none;
        }

        .upload-area:hover {
            border-color: var(--primary-color);
            background: #f5f0fa;
        }

        .upload-area.dragover {
            border-color: var(--secondary-color);
            background: #ededed;
        }

        .upload-icon {
            font-size: 48px;
            color: var(--primary-color);
            margin-bottom: 15px;
        }

        .upload-text {
            color: #666;
            font-size: 14px;
        }

        .upload-text strong {
            color: var(--primary-color);
            cursor: pointer;
        }

        #attachment {
            display: none;
        }

        .image-preview-container {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
            gap: 15px;
            margin-top: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 10px;
            min-height: 0;
        }

        .image-preview-container:empty {
            display: none;
        }

        .image-preview-item {
            position: relative;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            aspect-ratio: 1;
        }

        .image-preview-item img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .remove-image {
            position: absolute;
            top: 5px;
            right: 5px;
            background: rgb(117, 117, 119);
            color: white;
            border: none;
            border-radius: 50%;
            width: 28px;
            height: 28px;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: all 0.2s ease;
            font-size: 14px;
        }

        .remove-image:hover {
            background: #d9d9d9;
            transform: scale(1.1);
        }

        .file-counter {
            display: inline-block;
            background: var(--primary-color);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            margin-top: 10px;
        }

        .btn-submit {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
            border: none;
            padding: 14px 40px;
            border-radius: 10px;
            font-weight: 600;
            font-size: 16px;
            transition: all 0.3s ease;
            box-shadow: 0 4px 15px rgb(217, 217, 217);
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgb(237, 237, 237);
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--primary-color);
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .back-link:hover {
            color: var(--primary-hover);
            gap: 12px;
        }

        .alert-custom {
            border-radius: 10px;
            border: none;
            padding: 15px 20px;
            margin-bottom: 20px;
        }

        @media (max-width: 768px) {
            .card-body-custom {
                padding: 25px;
            }

            .card-header-custom {
                padding: 20px;
            }

            .card-header-custom h2 {
                font-size: 22px;
            }
        }
    </style>
</head>
<body>
<jsp:include page="/view/customerservice/result.jsp" />
<jsp:include page="/common/header.jsp" />

<main class="container support-container">
    <div class="row justify-content-center">
        <div class="col-lg-9 col-xl-8">
            <div class="support-card">
                <div class="card-header-custom">
                    <h1 style="color: #0a0a0a"><i class="fas fa-headset"></i> Support Request</h1>
                    <p>We're here to help! Submit your request and we'll get back to you soon.</p>
                </div>

                <div class="card-body-custom">
                    <c:if test="${not empty message}">
                        <div class="alert alert-custom ${messageType == 'success' ? 'alert-success' : 'alert-danger'} alert-dismissible fade show" role="alert">
                            <i class="fas ${messageType == 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
                                ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/customer/create-support-request"
                          method="post"
                          enctype="multipart/form-data"
                          id="supportForm">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label" >
                                    <i class="fas fa-envelope" style="color: #0a0a0a"></i> Email
                                </label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${sessionScope.user.email}" required readonly>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label for="phone" class="form-label">
                                    <i class="fas fa-phone" style="color: #0a0a0a"></i> Phone Number
                                </label>
                                <input type="text" class="form-control" id="phone" name="phone"
                                       value="${sessionScope.user.phoneNumber}" required readonly>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="categoryId" class="form-label">
                                <i class="fas fa-tags" style="color: #0a0a0a"></i> Category <span class="text-danger">*</span>
                            </label>
                            <select id="categoryId" name="categoryId" class="form-select" required>
                                <option value="">-- Select a category --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.categoryId}">${cat.categoryName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="workOrderId" class="form-label">
                                    <i class="fas fa-clipboard-list"></i> Order Code
                                </label>
                                <input type="text" class="form-control" id="workOrderId" name="workOrderId"
                                       placeholder="Enter order code (optional)">
                            </div>

                            <div class="col-md-6 mb-3">
                                <label for="appointmentId" class="form-label">
                                    <i class="fas fa-calendar-check"></i> Appointment Code
                                </label>
                                <input type="text" class="form-control" id="appointmentId" name="appointmentId"
                                       placeholder="Enter appointment code (optional)">
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label">
                                <i class="fas fa-align-left"></i> Description <span class="text-danger">*</span>
                            </label>
                            <textarea id="description" name="description" class="form-control" rows="5"
                                      placeholder="Please describe your issue or request in detail..." required></textarea>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">
                                <i class="fas fa-images"></i> Attachments (Optional - Max 4 images)
                            </label>
                            <div class="upload-area" id="uploadArea">
                                <input type="file"
                                       class="form-control"
                                       id="attachment"
                                       name="attachments"
                                       accept="image/png,image/jpeg,application/pdf"
                                       multiple
                                       style="display: none;">
                                <div class="upload-content">
                                    <div class="upload-icon">
                                        <i class="fas fa-cloud-upload-alt"></i>
                                    </div>
                                    <div class="upload-text">
                                        <strong style="color: #5d6b5f">Click to upload</strong> or drag and drop<br>
                                        <small>PNG, JPG or PDF (max. 5MB per file)</small>
                                    </div>
                                </div>
                            </div>
                            <div id="fileError" class="text-danger mt-2" style="display: none;"></div>
                            <div id="fileCounter"></div>
                            <div id="imagePreviewContainer" class="image-preview-container"></div>
                        </div>

                        <div class="text-center">
                            <button type="submit" class="btn btn-submit">
                                <i class="fas fa-paper-plane"></i> Submit Request
                            </button>
                        </div>
                    </form>

                    <div class="mt-4 text-center">
                        <a href="${pageContext.request.contextPath}/support-faq" class="back-link" style="color: #5a6268">
                            <i class="fas fa-arrow-left"></i> Back to FAQ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/common/footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customer/create-support-request.js"></script>
</body>
</html>