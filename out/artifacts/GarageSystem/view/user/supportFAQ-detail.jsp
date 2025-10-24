<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Question Detail</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/supportFAQ-detail.css">
</head>
<body>
<<div class="container">
    <h2>${faqDetail.question}</h2>
    <p>${faqDetail.answer}</p>
    <a href="support-faq" class="back-link">← Back to FAQ List</a>
</div>

<div class="help-widget">
    <button id="helpToggle" class="help-btn">💡</button>
    <div id="helpOptions" class="help-options">
        <a href="#" title="Chatbot">💬 Chatbot</a>
        <a href="tel:19001234" title="Hotline">📞 Hotline</a>
        <a href="mailto:support@garage.vn" title="Send Email">📧 Send Email</a>
        <a href="${pageContext.request.contextPath}/customer/create-support-request" title="Submit Request">❓ Submit Request</a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/user/supportFAQ-detail.js"></script>
</body>

</html>