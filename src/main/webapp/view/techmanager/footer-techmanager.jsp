<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/base-techmanager.css"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/techmanager/footer-techmanager.css"/>

<footer class="footer techmanager-footer">
    <div class="footer-content">
        <nav class="footer-col footer-links">
            <h6 class="footer-title-sm">Tech Manager</h6>
            <a href="${pageContext.request.contextPath}/techmanager/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/techmanager/service-requests">Service Requests</a>
            <a href="${pageContext.request.contextPath}/techmanager/diagnosis-review">Diagnosis Review</a>
        </nav>
        
        <nav class="footer-col footer-links">
            <h6 class="footer-title-sm">Support</h6>
            <a href="${pageContext.request.contextPath}/support-faq">FAQ</a>
            <a href="mailto:gara.tuanduong.auto2929@gmail.com">Contact Support</a>
            <a href="${pageContext.request.contextPath}/techmanager/reports">Reports</a>
        </nav>
    </div>

    <div class="footer-bottom">
        <small>© 2025 Garage Management System - Tech Manager Portal. All rights reserved.
            <span class="ver">Version 1.0.0</span>
        </small>
    </div>
</footer>
