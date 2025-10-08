<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error </title>
</head>
<body>
<h1>Error !!!</h1>
<div style="color: red; border: 1px solid red; padding: 15px; margin: 20px;">
    <c:choose>
        <c:when test="${not empty error}">
            <p>Detail error: <strong>${error}</strong></p>
        </c:when>
        <c:otherwise>
            <p>Try again.</p>
        </c:otherwise>
    </c:choose>
    <p><a href="${pageContext.request.contextPath}/roles?action=list">Back list roles</a></p>
</div>
</body>
</html>