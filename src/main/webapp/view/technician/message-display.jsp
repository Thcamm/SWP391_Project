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
        <span class="alert-icon">✅</span>
        <span class="alert-text">${sessionScope.successMessage}</span>
        <button class="alert-close" onclick="this.parentElement.remove()">×</button>
    </div>
    <c:remove var="successMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.errorMessage}">
    <div class="alert alert-error">
        <span class="alert-icon">❌</span>
        <span class="alert-text">${sessionScope.errorMessage}</span>
        <button class="alert-close" onclick="this.parentElement.remove()">×</button>
    </div>
    <c:remove var="errorMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.infoMessage}">
    <div class="alert alert-info">
        <span class="alert-icon">ℹ️</span>
        <span class="alert-text">${sessionScope.infoMessage}</span>
        <button class="alert-close" onclick="this.parentElement.remove()">×</button>
    </div>
    <c:remove var="infoMessage" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.warningMessage}">
    <div class="alert alert-warning">
        <span class="alert-icon">⚠️</span>
        <span class="alert-text">${sessionScope.warningMessage}</span>
        <button class="alert-close" onclick="this.parentElement.remove()">×</button>
    </div>
    <c:remove var="warningMessage" scope="session"/>
</c:if>

<style>
    .alert {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 14px 18px;
        border-radius: 10px;
        margin-bottom: 20px;
        font-size: 14px;
        font-weight: 500;
        animation: slideDown 0.3s ease;
    }
    @keyframes slideDown {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    .alert-success {
        background: #d1fae5;
        color: #065f46;
        border: 1px solid #a7f3d0;
    }
    .alert-error {
        background: #fee2e2;
        color: #991b1b;
        border: 1px solid #fecaca;
    }
    .alert-info {
        background: #dbeafe;
        color: #1e40af;
        border: 1px solid #bfdbfe;
    }
    .alert-warning {
        background: #fef3c7;
        color: #92400e;
        border: 1px solid #fde68a;
    }
    .alert-icon {
        font-size: 18px;
        flex-shrink: 0;
    }
    .alert-text {
        flex: 1;
    }
    .alert-close {
        background: none;
        border: none;
        font-size: 24px;
        line-height: 1;
        cursor: pointer;
        color: inherit;
        opacity: 0.6;
        padding: 0;
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        transition: all 0.2s;
    }
    .alert-close:hover {
        opacity: 1;
        background: rgba(0,0,0,0.1);
    }
</style>
