<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="uri" value="${pageContext.request.requestURI}" />
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/technician/base.css">
<style>

    :root{
        --sb-width: 240px;
        --sb-bg: #ffffff;
        --sb-border: #e5e7eb;
        --ink-900:#0f172a;
        --ink-700:#334155;
        --ink-500:#64748b;
        --brand-600:#2563eb;
        --brand-50:#eef2ff;
        --hover:#f8fafc;
        --ring:#93c5fd;
        --radius: 16px;
    }

    /* khối sidebar */
    .sidebar{
        width: var(--sb-width);
        min-width: var(--sb-width);
        background: var(--sb-bg);
        border-right: 1px solid var(--sb-border);
        padding: 20px 16px;
        border-radius: 14px;
        margin: 12px 0 12px 12px;
        position: sticky;               /* bám theo cuộn */
        top: 76px;                      /* lệch xuống dưới header một chút */
        height: calc(100vh - 24px - 76px);
        overflow-y: auto;
        box-shadow: 0 1px 0 rgba(0,0,0,0.02);
    }


    .sidebar .brand{
        font-weight: 700;
        color: var(--ink-900);
        margin-bottom: 10px;
    }
    .sidebar .menu-section{
        margin-top: 8px;
    }
    .sidebar .menu-title{
        font-size: .82rem;
        text-transform: uppercase;
        letter-spacing: .06em;
        color: var(--ink-500);
        margin: 12px 8px;
    }


    .sidebar .menu-item{
        display: block;
        padding: 10px 12px;
        margin: 6px 6px;
        border-radius: 10px;
        color: var(--ink-700);
        text-decoration: none;
        border: 1px solid transparent;
        transition: background-color .15s ease, color .15s ease, border-color .15s ease, box-shadow .15s ease, transform .08s ease;
    }
    .sidebar .menu-item:hover{
        background: var(--hover);
        color: var(--ink-900);
        border-color: #eef2f7;
    }


    .sidebar .menu-item.active{
        background: color-mix(in srgb, var(--brand-600) 6%, #fff);
        border-color: color-mix(in srgb, var(--brand-600) 18%, #e5e7eb);
        color: var(--brand-600);
        box-shadow: 0 0 0 3px color-mix(in srgb, var(--ring) 22%, transparent);
        font-weight: 600;
    }


    .sidebar .menu-item:focus{
        outline: 0;
        box-shadow: 0 0 0 3px color-mix(in srgb, var(--ring) 45%, transparent);
    }


    @media (max-width: 992px){
        .sidebar{
            width: 200px;
            min-width: 200px;
            top: 70px;
        }
    }
    @media (max-width: 768px){
        .sidebar{
            position: relative;
            top: 0;
            height: auto;
            margin: 12px;
            width: auto;
            min-width: 0;
        }
        .sidebar .menu-item{ margin: 6px 0; }
    }
</style>

<aside class="sidebar">
    <div class="brand">Admin</div>

    <div class="menu-section">
        <div class="menu-title">Main menu</div>

        <a class="menu-item ${activeMenu == 'Home' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/users">Home</a>

        <a class="menu-item ${activeMenu == 'rbac' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/rbac/rolesList?action=list">RBAC / Roles</a>

        <a class="menu-item ${activeMenu == 'employees' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/techmanager/dashboard">Tech Manager</a>

        <a class="menu-item ${activeMenu == 'reports' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/reports">Reports</a>
    </div>
</aside>
