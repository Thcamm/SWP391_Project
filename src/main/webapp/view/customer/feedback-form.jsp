<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Gửi đánh giá dịch vụ</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            color: #222;
            font-family: 'Segoe UI', sans-serif;
        }
        .feedback-card {
            max-width: 600px;
            margin: 60px auto;
            background: white;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
            padding: 30px;
        }
        .star-rating i {
            font-size: 26px;
            color: #ccc;
            cursor: pointer;
        }
        .star-rating i.active {
            color: #f5b301;
        }
        textarea {
            resize: none;
        }
    </style>
</head>
<body>
<jsp:include page="/view/customerservice/result.jsp" />
<div class="feedback-card">
    <h3 class="text-center mb-4">💬 Gửi đánh giá dịch vụ</h3>

    <c:if test="${not empty message}">
        <div class="alert alert-info text-center">${message}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/customer/send-feedback" method="post">
        <input type="hidden" name="workOrderID" value="${param.workOrderID}">

        <!-- Rating -->
        <div class="mb-3 text-center">
            <label class="form-label d-block">Đánh giá chất lượng:</label>
            <div class="star-rating">
                <input type="hidden" name="rating" id="rating" value="0">
                <i class="fa fa-star" data-value="1"></i>
                <i class="fa fa-star" data-value="2"></i>
                <i class="fa fa-star" data-value="3"></i>
                <i class="fa fa-star" data-value="4"></i>
                <i class="fa fa-star" data-value="5"></i>
            </div>
        </div>

        <!-- Nội dung -->
        <div class="mb-3">
            <label for="feedbackText" class="form-label">Nhận xét của bạn:</label>
            <textarea class="form-control" id="feedbackText" name="feedbackText" rows="4"
                      placeholder="Chia sẻ cảm nhận của bạn về dịch vụ..."></textarea>
        </div>

        <!-- Ẩn danh -->
        <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" id="isAnonymous" name="isAnonymous">
            <label class="form-check-label" for="isAnonymous">Gửi ẩn danh</label>
        </div>

        <button type="submit" class="btn btn-dark w-100">Gửi đánh giá</button>
    </form>
</div>

<script>
    document.querySelectorAll('.star-rating i').forEach(star => {
        star.addEventListener('click', function() {
            const value = this.getAttribute('data-value');
            document.getElementById('rating').value = value;

            document.querySelectorAll('.star-rating i').forEach(s => {
                s.classList.toggle('active', s.getAttribute('data-value') <= value);
            });
        });
    });
</script>

</body>
</html>
