<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Support Request - CarSpa VN</title>
    <style>
        /* ===== SUPPORT REQUEST PAGE - SCOPED CSS ===== */

        /* Override body từ header.jsp */
        body {
            font-family: Arial, sans-serif !important;
            background-color: #f7f7f7 !important;
            padding-top: 100px !important;
        }

        main.support-request-main {
            padding-top: 40px !important;
            padding-bottom: 60px !important;
            min-height: 70vh !important;
            background-color: #f7f7f7 !important;
        }

        main.support-request-main .support-form-container {
            max-width: 700px;
            margin: 0 auto;
            background: #fff;
            padding: 35px;
            border-radius: 10px;
            box-shadow: 0 2px 15px rgba(0,0,0,0.1);
        }

        main.support-request-main h2 {
            color: #6a1b9a;
            margin-bottom: 25px;
            text-align: center;
            font-size: 1.8rem;
            font-weight: 600;
        }

        main.support-request-main .form-group {
            margin-bottom: 20px;
        }

        main.support-request-main .form-group label {
            font-weight: 600;
            margin-bottom: 8px;
            display: block;
            color: #333;
            font-size: 0.95rem;
        }

        main.support-request-main .form-group input[type="email"],
        main.support-request-main .form-group input[type="text"],
        main.support-request-main .form-group select,
        main.support-request-main .form-group textarea {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            box-sizing: border-box;
            font-size: 0.95rem;
            font-family: Arial, sans-serif;
            transition: border-color 0.3s ease, box-shadow 0.3s ease;
        }

        main.support-request-main .form-group input:focus,
        main.support-request-main .form-group select:focus,
        main.support-request-main .form-group textarea:focus {
            outline: none;
            border-color: #6a1b9a;
            box-shadow: 0 0 0 3px rgba(106, 27, 154, 0.1);
        }

        main.support-request-main .form-group input[type="file"] {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 6px;
            background-color: #f9f9f9;
        }

        main.support-request-main .form-group textarea {
            resize: vertical;
            min-height: 120px;
        }

        main.support-request-main .btn-submit {
            width: 100%;
            padding: 14px;
            border: none;
            background-color: #6a1b9a;
            color: white;
            font-weight: 600;
            font-size: 1rem;
            border-radius: 6px;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.1s ease;
            margin-top: 10px;
        }

        main.support-request-main .btn-submit:hover {
            background-color: #50127a;
            transform: translateY(-1px);
        }

        main.support-request-main .btn-submit:active {
            transform: translateY(0);
        }

        main.support-request-main .back-link {
            display: inline-block;
            margin-top: 20px;
            color: #6a1b9a;
            text-decoration: none;
            font-weight: 600;
            font-size: 0.95rem;
            transition: color 0.3s ease;
        }

        main.support-request-main .back-link:hover {
            color: #50127a;
            text-decoration: underline;
        }

        main.support-request-main .message {
            text-align: center;
            color: #28a745;
            font-weight: 600;
            margin-top: 20px;
            padding: 12px;
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            border-radius: 6px;
        }

        main.support-request-main #fileError {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 5px;
            font-weight: 500;
        }

        main.support-request-main #previewImage {
            display: none;
            margin-top: 15px;
            max-width: 100%;
            max-height: 300px;
            border-radius: 6px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.15);
            object-fit: contain;
        }
    </style>

</head>
<body>

<!-- Header -->
<jsp:include page="/common/customer/header.jsp" />

<!-- Main Content -->

<main class="support-request-main">
    <div class="container">
        <div class="support-form-container">
            <h2>Support Request / Report a Bug</h2>

            <form action="${pageContext.request.contextPath}/customer/create-support-request"
                  method="post"
                  enctype="multipart/form-data">

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email"
                           id="email"
                           name="email"
                           value="${sessionScope.user.email}"
                           required>
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <input type="text"
                           id="phone"
                           name="phone"
                           value="${sessionScope.user.phoneNumber}"
                           required>
                </div>

                <div class="form-group">
                    <label for="categoryId">Category</label>
                    <select id="categoryId" name="categoryId" required>
                        <option value="">-- Select option --</option>
                        <c:forEach var="cat" items="${categories}">
                            <option value="${cat.categoryId}">${cat.categoryName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="workOrderId">Order code (if any)</label>
                    <input type="text"
                           id="workOrderId"
                           name="workOrderId"
                           placeholder="Enter Work Order Code">
                </div>

                <div class="form-group">
                    <label for="appointmentId">Appointment code (if any)</label>
                    <input type="text"
                           id="appointmentId"
                           name="appointmentId"
                           placeholder="Enter Appointment Code">
                </div>

                <div class="form-group">
                    <label for="description">Detail</label>
                    <textarea id="description"
                              name="description"
                              rows="5"
                              placeholder="Enter the content of your support request..."
                              required></textarea>
                </div>

                <div class="form-group">
                    <label for="attachment">Attached photo (if any)</label>
                    <input type="file"
                           id="attachment"
                           name="attachment"
                           accept="image/*">
                    <div id="fileError"></div>
                    <img id="previewImage" alt="Preview" />
                </div>

                <button type="submit" class="btn-submit">
                    <i class="fas fa-paper-plane"></i> Send request
                </button>
            </form>

            <a href="${pageContext.request.contextPath}/support-faq" class="back-link">
                <i class="fas fa-arrow-left"></i> Return FAQ
            </a>

            <c:if test="${not empty message}">
                <p class="message">
                    <i class="fas fa-check-circle"></i> ${message}
                </p>
            </c:if>
        </div>
    </div>
</main>

<!-- Footer -->
<jsp:include page="/common/customer/footer.jsp" />

<script src="${pageContext.request.contextPath}/assets/js/customer/create-support-request.js"></script>

</body>
</html>

