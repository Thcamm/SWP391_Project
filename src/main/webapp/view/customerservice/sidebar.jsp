<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%@ taglib
uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
  .sidebar-wrapper {
    background: transparent;
    padding: 1.25rem;
    padding-bottom: 0;
    min-height: calc(100vh - 64px);
  }
  .sidebar {
    background: white;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    padding: 1.25rem;
    min-height: calc(100vh - 64px - 1.25rem);
    display: flex;
    flex-direction: column;
    margin-bottom: 30px;
  }
  .sidebar-brand {
    font-weight: 700;
    font-size: 18px;
    margin-bottom: 1.25rem;
    color: #111827;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  .menu-title {
    font-size: 12px;
    color: #6b7280;
    text-transform: uppercase;
    margin: 0.75rem 0 0.375rem;
    font-weight: 600;
    letter-spacing: 0.5px;
  }
  .sidebar .nav-link {
    padding: 0.625rem 0.75rem;
    border-radius: 10px;
    margin-bottom: 0.375rem;
    color: #12161c;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    text-decoration: none;
  }
  .sidebar .nav-link:hover {
    background-color: #f2f3f8;
  }
  .sidebar .nav-link.active {
    background-color: #111827;
    color: white;
  }
  .sidebar-footer {
    margin-top: auto;
    padding-top: 1rem;
    border-top: 1px solid #e5e7eb;
  }
  .btn-support {
    width: 100%;
    padding: 0.625rem 0.75rem;
    border: 1px solid #e5e7eb;
    background: #f9fafb;
    border-radius: 10px;
    transition: all 0.2s;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    color: #111827;
    text-decoration: none;
  }
  .btn-support:hover {
    background-color: #f3f4f6;
    color: #111827;
  }
</style>

<%-- Lấy URI hiện tại để xác định active menu --%>
<c:set var="currentURI" value="${pageContext.request.requestURI}" />

<div class="sidebar-wrapper">
  <aside class="sidebar">
    <div class="sidebar-brand">
      <i class="bi bi-calculator"></i>
      <span>Accountant Panel</span>
    </div>

    <nav class="flex-grow-1">
      <div class="menu-title">Main Menu</div>
      <ul class="nav flex-column">
        <li class="nav-item">
          <a
            class="nav-link ${fn:contains(currentURI, '/home') ? 'active' : ''}"
            href="${pageContext.request.contextPath}/accountant/home"
          >
            <i class="bi bi-house-door"></i>
            <span>Dashboard</span>
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link ${fn:contains(currentURI, '/invoice') ? 'active' : ''}"
            href="${pageContext.request.contextPath}/accountant/invoice"
          >
            <i class="bi bi-receipt"></i>
            <span>Invoices</span>
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link ${fn:contains(currentURI, '/payment') ? 'active' : ''}"
            href="${pageContext.request.contextPath}/accountant/payment"
          >
            <i class="bi bi-cash-coin"></i>
            <span>Payments</span>
          </a>
        </li>
      </ul>

      <div class="menu-title">Analytics</div>
      <ul class="nav flex-column">
        <li class="nav-item">
          <a
            class="nav-link ${fn:contains(currentURI, '/report') ? 'active' : ''}"
            href="${pageContext.request.contextPath}/accountant/report"
          >
            <i class="bi bi-graph-up"></i>
            <span>Financial Reports</span>
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link ${fn:contains(currentURI, '/statistics') ? 'active' : ''}"
            href="${pageContext.request.contextPath}/accountant/statistics"
          >
            <i class="bi bi-bar-chart"></i>
            <span>Statistics</span>
          </a>
        </li>
      </ul>

      <div class="menu-title">Quick Links</div>
      <ul class="nav flex-column">
        <li class="nav-item">
          <a
            class="nav-link"
            href="${pageContext.request.contextPath}/accountant/invoice?action=overdue"
          >
            <i class="bi bi-clock-history text-danger"></i>
            <span>Overdue Invoices</span>
          </a>
        </li>
        <li class="nav-item">
          <a
            class="nav-link"
            href="${pageContext.request.contextPath}/accountant/invoice?action=create"
          >
            <i class="bi bi-plus-circle text-success"></i>
            <span>Create Invoice</span>
          </a>
        </li>
      </ul>
    </nav>

    <div class="sidebar-footer">
      <a href="${pageContext.request.contextPath}/support" class="btn-support">
        <i class="bi bi-question-circle"></i>
        <span>Help & Support</span>
      </a>
    </div>
  </aside>
</div>
