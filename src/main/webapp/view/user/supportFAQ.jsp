<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title> Frequently asked questions</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/user/supportFAQ.css">

</head>
<body>
<h2>Frequently asked questions</h2>

<form method="get" action="support-faq">
    <input type="text" name="q" placeholder="Search for a question..." value="${param.q}">
    <button type="submit">Search</button>
    <a href="support-faq" style="align-self:center; color:#fff; margin-left:10px;">Reset</a>
</form>

<hr>

<c:if test="${empty faqs}">
    <p>No matching questions found.</p>
</c:if>

<ul>
    <c:forEach var="faq" items="${faqs}">
        <li>
            <a href="${pageContext.request.contextPath}/support-faq?id=${faq.FAQId}">
                    ${faq.question}
            </a>
        </li>
    </c:forEach>
</ul>

<div class="help-widget">
    <button id="helpToggle" class="help-btn">💡</button>
    <div id="helpOptions" class="help-options">
        <a href="#" title="Chatbot">💬 Chatbot</a>
        <a href="tel:19001234" title="Hotline">📞 Hotline</a>
        <a href="mailto:support@garage.vn" title="Send Email">📧 Send Email</a>
        <a href="${pageContext.request.contextPath}/app/create-support-request" title="Submit Request">❓ Submit Request</a>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/user/supportFAQ.js"></script>

</body>
</html>
