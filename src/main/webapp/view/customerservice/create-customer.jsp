<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo mới khách hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/create-customer.css">

</head>
<body>

<div class="container">
    <div class="header">
        <h1>
            <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
            </svg>
            Create A New Customer
        </h1>
        <p>Enter Customer Information</p>
    </div>
    <c:if test="${not empty message}">
        <div class="alert
                ${messageType == 'success' ? 'alert-success' :
                messageType == 'warning' ? 'alert-warning' :
                'alert-danger'}" role="alert">
                ${message}
        </div>
    </c:if>
    <form action="${pageContext.request.contextPath}/customerservice/create-customer" method="post" id="customerForm">

        <div class="section">
            <div class="section-title">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                </svg>
                Customer Details
            </div>

            <div class="form-group full-width">
                <label>
                    Full Name
                    <span class="required">*</span>
                </label>
                <input type="text" name="fullName" placeholder="Enter full name" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="16" rx="2"></rect>
                            <polyline points="3 10 12 15 21 10"></polyline>
                        </svg>
                        Email
                        <span class="required">*</span>
                    </label>
                    <input type="email" name="email" placeholder="example@email.com">
                </div>

                <div class="form-group">
                    <label>
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                        </svg>
                        Phone Number
                    </label>
                    <input type="tel" name="phone" placeholder="0123456789">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Gender</label>
                    <select name="gender">
                        <option value="">Select gender</option>
                        <option value="male">Male</option>
                        <option value="female">Female</option>
                        <option value="other">Other</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>
                        <svg class="icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                            <line x1="16" y1="2" x2="16" y2="6"></line>
                            <line x1="8" y1="2" x2="8" y2="6"></line>
                            <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                        Birth Date
                    </label>
                    <input type="date" name="birthDate" placeholder="dd/mm/yyyy">
                </div>
            </div>

            <div class="form-group full-width">
                <label>Address</label>
                <textarea name="address" placeholder="Enter full address"></textarea>
            </div>

        </div>

        <div class="form-actions">
            <button type="button" class="btn btn-cancel" onclick="window.history.back()">Cancel</button>
            <button type="submit" class="btn btn-submit">Create</button>
        </div>
    </form>
</div>

</body>
</html>