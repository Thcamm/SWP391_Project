<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    .sidebar-wrapper {
        position: sticky;
        top: 64px; /* Giả định chiều cao header là 64px */
        height: calc(100vh - 64px);
        background: transparent;
        padding: 1.25rem;
        padding-bottom: 0;
        overflow-y: auto;
        scrollbar-width: none; /* Firefox */
        -ms-overflow-style: none;  /* IE/Edge */
    }
    .sidebar-wrapper::-webkit-scrollbar {
        display: none;
    }
    .sidebar {
        background: white;
        border: 1px solid #e5e7eb;
        border-radius: 12px;
        padding: 1.25rem;
        height: 100%;
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
    .menu-title-trigger {
        font-size: 12px;
        color: #6b7280;
        text-transform: uppercase;
        margin: 0.75rem 0 0.375rem;
        font-weight: 600;
        letter-spacing: 0.5px;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.625rem 0.75rem;
        border-radius: 10px;
        text-decoration: none;
        cursor: pointer;
        transition: all 0.2s;
    }
    .menu-title-trigger:hover {
        background-color: #f2f3f8;
        color: #111827;
    }
    .menu-title-trigger .bi-chevron-down {
        transition: transform 0.2s ease-in-out;
    }
    .menu-title-trigger[aria-expanded="true"] .bi-chevron-down {
        transform: rotate(180deg);
    }
    .collapse .nav-link {
        padding-left: 1.5rem;
        font-size: 0.9rem;
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

<%-- (Phần logic c:set của bạn giữ nguyên) --%>
<c:set var="currentURI" value="${pageContext.request.requestURI}" />
<c:set var="isMainActive" value="${fn:contains(currentURI, '/dashboard') || fn:contains(currentURI, '/service-types')}" />
<c:set var="isCustomerActive" value="${fn:contains(currentURI, '/search-customer') || fn:contains(currentURI, '/create-customer')}" />
<c:set var="isAppointmentsActive" value="${fn:contains(currentURI, '/appointment-list')} || fn:contains(currentURI, '/view-all-repairs')}" />
<c:set var="isFeedbackActive" value="${fn:contains(currentURI, '/reply-feedback.jsp')}" />
<c:set var="isSupportActive" value="${fn:contains(currentURI, '/view-support-request')} || fn:contains(currentURI, '/faq')" />


<div class="sidebar-wrapper">
    <aside class="sidebar">

        <nav class="flex-grow-1" id="sidebarAccordion">

            <a class="menu-title-trigger" data-bs-toggle="collapse" href="#collapseMain" role="button"
               aria-expanded="${isMainActive ? 'true' : 'false'}" aria-controls="collapseMain">
                <span>Main Menu</span>
                <i class="bi bi-chevron-down"></i>
            </a>
            <div class="collapse ${isMainActive ? 'show' : ''}" id="collapseMain" data-bs-parent="#sidebarAccordion">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/dashboard') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/dashboard">
                            <i class="bi bi-house-door"></i>
                            <span>Dashboard</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/service-types') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/service-types">
                            <i class="bi bi-receipt"></i>
                            <span>Services</span>
                        </a>
                    </li>
                </ul>
            </div>

            <a class="menu-title-trigger" data-bs-toggle="collapse" href="#collapseCustomer" role="button"
               aria-expanded="${isCustomerActive ? 'true' : 'false'}" aria-controls="collapseCustomer">
                <span>Customer</span>
                <i class="bi bi-chevron-down"></i>
            </a>
            <div class="collapse ${isCustomerActive ? 'show' : ''}" id="collapseCustomer" data-bs-parent="#sidebarAccordion">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/search-customer') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/search-customer">
                            <i class="bi bi-graph-up"></i>
                            <span>Customer List</span>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/create-customer') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/create-customer">
                            <i class="bi bi-bar-chart"></i>
                            <span>Create Customer</span>
                        </a>
                    </li>
                </ul>
            </div>

            <a class="menu-title-trigger" data-bs-toggle="collapse" href="#collapseAppt" role="button"
               aria-expanded="${isAppointmentsActive ? 'true' : 'false'}" aria-controls="collapseAppt">
                <span>Appointments</span>
                <i class="bi bi-chevron-down"></i>
            </a>
            <div class="collapse ${isAppointmentsActive ? 'show' : ''}" id="collapseAppt" data-bs-parent="#sidebarAccordion">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/appointment-list') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/appointment-list">
                            <i class="bi bi-clock-history text-danger"></i>
                            <span>Appointment List</span>
                        </a>
                    </li>
                </ul>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/view-all-repairs') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/view-all-repairs">
                            <i class="bi bi-clock-history text-danger"></i>
                            <span>Tracking List</span>
                        </a>
                    </li>
                </ul>
            </div>

            <a class="menu-title-trigger" data-bs-toggle="collapse" href="#collapseFeedback" role="button"
               aria-expanded="${isFeedbackActive ? 'true' : 'false'}" aria-controls="collapseFeedback">
                <span>Feedback</span>
                <i class="bi bi-chevron-down"></i>
            </a>
            <div class="collapse ${isFeedbackActive ? 'show' : ''}" id="collapseFeedback" data-bs-parent="#sidebarAccordion">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/reply-feedback.jsp') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/view/customerservice/reply-feedback.jsp">
                            <i class="bi bi-clock-history text-danger"></i>
                            <span>Feedback Reply</span>
                        </a>
                    </li>
                </ul>
            </div>

            <a class="menu-title-trigger" data-bs-toggle="collapse" href="#collapseSupport" role="button"
               aria-expanded="${isSupportActive ? 'true' : 'false'}" aria-controls="collapseSupport">
                <span>Support Request</span>
                <i class="bi bi-chevron-down"></i>
            </a>
            <div class="collapse ${isSupportActive ? 'show' : ''}" id="collapseSupport" data-bs-parent="#sidebarAccordion">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/view-support-request') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/view-support-request">
                            <i class="bi bi-clock-history "></i>
                            <span>Support Requests</span>
                        </a>
                    </li>
                </ul>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link ${fn:contains(currentURI, '/faq') ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/customerservice/faq">
                            <i class="bi bi-clock-history "></i>
                            <span>Support FAQ</span>
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <div class="sidebar-footer">
            <a href="${pageContext.request.contextPath}/support" class="btn-support">
                <i class="bi bi-question-circle"></i>
                <span>Help & Support</span>
            </a>
        </div>
    </aside>
</div>