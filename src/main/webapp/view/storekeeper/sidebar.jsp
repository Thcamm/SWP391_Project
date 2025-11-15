<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .sidebar {
        background: #fff;
        border-right: 1px solid #e5e7eb;
        height: 100vh;
        position: sticky;
        top: 0;
        overflow-y: auto;
    }

    .sidebar-header {
        padding: 1.25rem 1rem;
        border-bottom: 1px solid #e5e7eb;
    }

    .sidebar-header h5 {
        margin: 0;
        font-size: 0.875rem;
        font-weight: 600;
        color: #6b7280;
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }

    .sidebar-menu {
        list-style: none;
        padding: 0.5rem;
        margin: 0;
    }

    .sidebar-menu li {
        margin-bottom: 0.25rem;
    }

    .sidebar-menu a {
        display: flex;
        align-items: center;
        gap: 0.75rem;
        padding: 0.75rem 1rem;
        border-radius: 0.5rem;
        color: #374151;
        text-decoration: none;
        transition: all 0.2s;
        font-size: 0.9rem;
    }

    .sidebar-menu a:hover {
        background: #f3f4f6;
        color: #111827;
    }

    .sidebar-menu a.active {
        background: #111827;
        color: #fff;
        font-weight: 500;
    }

    .sidebar-menu a i {
        width: 20px;
        text-align: center;
        font-size: 1.1rem;
    }

    .menu-divider {
        height: 1px;
        background: #e5e7eb;
        margin: 0.75rem 0;
    }
</style>

<div class="sidebar">
    <div class="sidebar-header">
        <h5>Inventory Menu</h5>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="${pageContext.request.contextPath}/inventory?action=list"
               class="${param.action == 'list' || empty param.action ? 'active' : ''}">
                <i class="fas fa-th-list"></i>
                <span>All Items</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/inventory?action=lowstock"
               class="${param.action == 'lowstock' ? 'active' : ''}">
                <i class="fas fa-exclamation-triangle"></i>
                <span>Low Stock</span>
            </a>
        </li>

        <div class="menu-divider"></div>

        <li>
            <a href="${pageContext.request.contextPath}/inventory?action=add">
                <i class="fas fa-plus-circle"></i>
                <span>Add New Part</span>
            </a>
        </li>

        <li>
            <a href="${pageContext.request.contextPath}/stock-out">
                <i class="fas fa-arrow-circle-up"></i>
                <span>Stock Out Requests</span>
            </a>
        </li>

        <div class="menu-divider"></div>

        <li>
            <a href="${pageContext.request.contextPath}/transactions">
                <i class="fas fa-history"></i>
                <span>Transaction History</span>
            </a>
        </li>

<%--        <li>--%>
<%--            <a href="${pageContext.request.contextPath}/inventory?action=reports">--%>
<%--                <i class="fas fa-chart-bar"></i>--%>
<%--                <span>Reports</span>--%>
<%--            </a>--%>
<%--        </li>--%>
    </ul>
</div>

