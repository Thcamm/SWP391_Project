<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css">


<style>
    /* footer base – match technician */
    .footer.admin-footer{
        background:#fff;
        border-top:1px solid var(--border);
        margin-top:24px;
    }
    .footer.admin-footer .footer-content{
        max-width:1200px;
        margin:0 auto;
        padding:16px 20px;
        display:flex;
        justify-content:center;   /* chỉ có 1 cột Support -> căn giữa */
        gap:16px;
        flex-wrap:wrap;
    }
    .footer.admin-footer .footer-title-sm{
        margin:0 0 8px;
        color:var(--muted);
        font-size:12px;
        font-weight:600;
        letter-spacing:.2px;
        text-align:center;
    }
    .footer.admin-footer .footer-links{
        display:flex;
        align-items:center;
        gap:10px;
        flex-wrap:wrap;
    }
    .footer.admin-footer .footer-links a{
        color:var(--text);
        text-decoration:none;
        padding:6px 10px;
        border-radius:8px;
    }
    .footer.admin-footer .footer-links a:hover{
        background:#f3f4f6;
    }
    .footer.admin-footer .footer-bottom{
        border-top:1px solid var(--border);
        text-align:center;
        color:var(--muted);
        padding:10px 16px;
    }
    .footer.admin-footer .ver{ margin-left:8px; }



</style>

<footer class="footer admin-footer">
    <div class="footer-content">
        <nav class="footer-col footer-links">
            <h6 class="footer-title-sm">Support</h6>
            <a href="${pageContext.request.contextPath}/admin/users">Home</a>
            <a href="${pageContext.request.contextPath}/support-faq">FAQ</a>
            <a href="mailto:support@garage.com">Contact Support</a>
        </nav>
    </div>

    <div class="footer-bottom">
        <small>© 2025 Garage Management System. All rights reserved.
            <span class="ver">Version 1.0.0</span>
        </small>
    </div>
</footer>
