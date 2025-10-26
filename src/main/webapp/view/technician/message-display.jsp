<%--
  Created by IntelliJ IDEA.
  User: ADMIN
  Date: 10/19/2025
  Time: 8:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty sessionScope.successMessage}">
    <div class="alert alert-success">
        <span class="alert-icon">v</span>
        <span class="alert-text">${sessionScope.successMessage}</span>
    </div>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.errorMessage}">
    <div class="alert alert-error">
        <span class="alert-icon">x</span>
        <span class="alert-text">${sessionScope.errorMessage}</span>
    </div>
    <c:remove var="errorMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.warningMessage}">
    <div class="alert alert-warning">
        <span class="alert-icon">!</span>
        <span class="alert-text">${sessionScope.warningMessage}</span>
    </div>
    <c:remove var="warningMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.infoMessage}">
    <div class="alert alert-info">
        <span class="alert-icon">i</span>
        <span class="alert-text">${sessionScope.infoMessage}</span>
    </div>
    <c:remove var="infoMessage" scope="session"/>
</c:if>