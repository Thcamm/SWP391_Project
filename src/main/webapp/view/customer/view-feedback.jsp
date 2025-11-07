<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Feedback Submitted</title>

    <!-- FontAwesome for icons -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">

    <style>
        :root {
            --primary: #28a745;
            --bg: #f5f5f5;
            --text: #000;
            --muted: #555;
            --border: #ddd;
        }

        body {
            margin: 0;
            padding: 0;
            background: var(--bg);
            font-family: 'Segoe UI', sans-serif;
            color: var(--text);
        }

        main.feedback-page {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 90vh;
        }

        .thankyou-card {
            background: #fff;
            width: 90%;
            max-width: 640px;
            border-radius: 20px;
            box-shadow: 0 4px 25px rgba(0, 0, 0, 0.1);
            padding: 40px 30px;
            text-align: center;
        }

        .check-icon {
            font-size: 64px;
            color: var(--primary);
            margin-bottom: 16px;
        }

        h4 {
            color: var(--primary);
            font-size: 1.5rem;
            margin-bottom: 10px;
        }

        p {
            color: var(--text);
            margin: 0 0 8px;
            line-height: 1.5;
        }

        .feedback-info {
            background: #f1f3f5;
            border-radius: 15px;
            padding: 20px;
            margin: 25px 0;
            text-align: left;
        }

        .feedback-info p {
            margin-bottom: 10px;
        }

        .star-rating i {
            color: #f5b301;
            font-size: 18px;
        }

        .feedback-date {
            color: var(--muted);
            font-size: 0.9rem;
        }

        .btn-group {
            display: flex;
            justify-content: center;
            gap: 15px;
            margin-top: 25px;
        }

        .btn {
            padding: 10px 22px;
            border-radius: 25px;
            border: 1px solid var(--border);
            text-decoration: none;
            font-weight: 500;
            transition: 0.3s ease;
        }

        .btn-back {
            background: #333;
            color: #fff;
        }

        .btn-back:hover {
            background: #000;
        }

        .btn-view {
            background: transparent;
            border: 1px solid #f5b301;
            color: #f5b301;
        }

        .btn-view:hover {
            background: #f5b301;
            color: #fff;
        }

        @media (max-width: 600px) {
            .thankyou-card {
                padding: 30px 20px;
            }
            .btn {
                padding: 8px 16px;
            }
        }
    </style>
</head>

<body>
<jsp:include page="/common/header.jsp" />

<main class="feedback-page">
    <div class="thankyou-card">
        <div class="check-icon">
            <i class="fa-solid fa-circle-check"></i>
        </div>

        <h4 style="color: #5a6268">Thank you for your feedback!</h4>
        <p>Your feedback helps us improve our services every day.</p>

        <div class="feedback-info">
            <p><strong>Customer:</strong>
                <c:choose>
                    <c:when test="${feedback.anonymous}">
                        Anonymous
                    </c:when>
                    <c:otherwise>
                        ${customerName}
                    </c:otherwise>
                </c:choose>
            </p>
            <p><strong>Work Order ID:</strong> ${feedback.workOrderID}</p>
            <p><strong>Rating:</strong>
                <span class="star-rating">
                    <c:forEach var="i" begin="1" end="5">
                        <i class="fa fa-star${i <= feedback.rating ? '' : '-o'}"></i>
                    </c:forEach>
                </span>
            </p>
            <p><strong>Comment:</strong> ${feedback.feedbackText}</p>
            <p class="feedback-date">
                <i class="fa fa-clock"></i> Submitted on: ${feedback.feedbackDate}
            </p>
        </div>

        <div class="btn-group">
            <a href="${pageContext.request.contextPath}/customer/workorder-list" class="btn btn-back">
                <i class="fa fa-arrow-left"></i> Back to Work Orders
            </a>
            <a href="${pageContext.request.contextPath}/view/user/view-feedback-list.jsp" class="btn btn-view">
                <i class="fa fa-eye"></i> View All Feedback
            </a>
        </div>
    </div>
</main>

<jsp:include page="/common/footer.jsp" />
</body>
</html>
