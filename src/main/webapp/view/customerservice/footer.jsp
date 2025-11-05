<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<style>
    .footer-wrapper {
        background: transparent;
        padding: 1.25rem;
        padding-top: 0;
    }
    .footer {
        background: white;
        border: 1px solid #e5e7eb;
        /*border-radius: 12px;*/
        width: 100%;
        color: #12161c;
        padding: 1.25rem 1.25rem;
        margin-bottom: -1.25rem;
    }
    .footer-links a {
        color: #6b7280;
        text-decoration: none;
        padding: 0.375rem 0.5rem;
        border-radius: 8px;
        transition: all 0.2s;
        display: inline-flex;
        align-items: center;
        gap: 0.25rem;
    }
    .footer-links a:hover {
        background-color: #f3f4f6;
        color: #12161c;
    }
    .footer p {
        margin: 0;
        font-size: 14px;
        color: #6b7280;
    }
</style>

</main>

<div class="footer-wrapper">
    <footer class="footer">
        <div class="row align-items-center g-2">
            <div class="col-md-6 col-12 text-center text-md-start">
                <p>&copy; 2025 Garage Management System. All rights reserved.</p>
            </div>
<%--            <div class="col-md-6 col-12">--%>
<%--                <div class="footer-links d-flex justify-content-center justify-content-md-end gap-2 flex-wrap">--%>
<%--                    <a href="${pageContext.request.contextPath}/help">--%>
<%--                        <i class="bi bi-question-circle"></i>--%>
<%--                        <span>Help</span>--%>
<%--                    </a>--%>
<%--                    <a href="${pageContext.request.contextPath}/contact">--%>
<%--                        <i class="bi bi-envelope"></i>--%>
<%--                        <span>Contact</span>--%>
<%--                    </a>--%>
<%--                    <a href="${pageContext.request.contextPath}/terms">--%>
<%--                        <i class="bi bi-file-text"></i>--%>
<%--                        <span>Terms</span>--%>
<%--                    </a>--%>
<%--                </div>--%>
<%--            </div>--%>
        </div>
    </footer>
</div>

<!-- Bootstrap JS Bundle -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<%--<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>--%>
<%--<script src="${pageContext.request.contextPath}/assets/js/technician-dashboard.js"></script>--%>
</body>
</html>