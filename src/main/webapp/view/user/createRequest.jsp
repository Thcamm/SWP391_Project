<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html lang="vi">--%>
<%--<head>--%>
<%--    <meta charset="UTF-8">--%>
<%--    <meta name="viewport" content="width=device-width, initial-scale=1.0">--%>
<%--    <title>Create Service Request</title>--%>
<%--    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/vehicle/addVehicle.css">--%>
<%--</head>--%>
<%--<body>--%>
<%--<div class="form-container">--%>
<%--    <h2>Create a New Service Request</h2>--%>

<%--    <c:if test="${not empty sessionScope.success}">--%>
<%--        <div class="message success">${sessionScope.success}</div>--%>
<%--        <c:remove var="success" scope="session" />--%>
<%--    </c:if>--%>
<%--    <c:if test="${not empty sessionScope.error}">--%>
<%--        <div class="message error">${sessionScope.error}</div>--%>
<%--        <c:remove var="error" scope="session" />--%>
<%--    </c:if>--%>
<%--    <c:if test="${not empty requestScope.error}">--%>
<%--        <div class="message error">${requestScope.error}</div>--%>
<%--    </c:if>--%>

<%--    <form action="${pageContext.request.contextPath}/customer/createRequest" method="post">--%>

<%--        <div class="form-group">--%>
<%--            <label for="vehicleId">Select Your Vehicle <span class="required">*</span></label>--%>
<%--            <select id="vehicleId" name="vehicleId" required class="form-input">--%>
<%--                <option value="">-- Please select a vehicle --</option>--%>
<%--                <c:forEach var="vehicle" items="${vehicles}">--%>
<%--                    <option value="${vehicle.vehicleId}">--%>
<%--                        <c:out value="${vehicle.brand} ${vehicle.model} - ${vehicle.licensePlate}"/>--%>
<%--                    </option>--%>
<%--                </c:forEach>--%>
<%--            </select>--%>

<%--            <c:if test="${empty vehicles}">--%>
<%--                <small class="error-text" style="display: block; margin-top: 5px;">--%>
<%--                    You have no vehicles. Please <a href="${pageContext.request.contextPath}/customer/addVehicle">add a vehicle</a> first.--%>
<%--                </small>--%>
<%--            </c:if>--%>
<%--        </div>--%>

<%--        <div class="form-group">--%>
<%--            <label for="serviceId">Select Service <span class="required">*</span></label>--%>
<%--            <select id="serviceId" name="serviceId" required class="form-input">--%>
<%--                <option value="">-- Please select a service --</option>--%>
<%--                <c:forEach var="service" items="${services}">--%>
<%--                    <option value="${service.serviceTypeID}">--%>
<%--                        <c:out value="${service.serviceName}"/> - ${service.price}đ--%>
<%--                    </option>--%>
<%--                </c:forEach>--%>
<%--            </select>--%>
<%--        </div>--%>

<%--        <div class="button-group">--%>
<%--            <button type="submit" class="btn btn-primary" ${empty vehicles ? 'disabled' : ''}>Send Request</button>--%>
<%--            <a href="${pageContext.request.contextPath}/Home" class="btn btn-secondary">Back to Home</a>--%>
<%--        </div>--%>
<%--    </form>--%>
<%--</div>--%>
<%--</body>--%>
<%--</html>--%>