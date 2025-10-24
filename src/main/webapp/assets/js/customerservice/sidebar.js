/**
 * Sidebar Toggle Functionality
 * Handles sidebar expand/collapse with click outside detection
 */

(function() {
    'use strict';

    // ==================== DOM ELEMENTS ====================
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('toggleBtn');
    const overlay = document.getElementById('sidebarOverlay');
    const menuItems = document.querySelectorAll('.menu-item');

    // ==================== STATE ====================
    let isExpanded = false;

    // ==================== INIT ====================
    function init() {
        if (!sidebar || !toggleBtn) {
            console.warn('Sidebar elements not found');
            return;
        }

        // Set active menu item based on current URL
        setActiveMenuItem();

        // Attach event listeners
        attachEventListeners();
    }

    // ==================== EVENT LISTENERS ====================
    function attachEventListeners() {
        // Toggle button click
        toggleBtn.addEventListener('click', handleToggleClick);

        // Click outside to close
        document.addEventListener('click', handleOutsideClick);

        // Overlay click to close
        if (overlay) {
            overlay.addEventListener('click', collapseSidebar);
        }

        // Menu item clicks (for active state)
        menuItems.forEach(item => {
            item.addEventListener('click', function() {
                // Remove active from all
                menuItems.forEach(i => i.classList.remove('active'));
                // Add active to clicked item
                this.classList.add('active');
            });
        });

        // Keyboard accessibility
        toggleBtn.addEventListener('keydown', handleKeyPress);

        // Window resize handler
        window.addEventListener('resize', handleResize);
    }

    // ==================== TOGGLE HANDLERS ====================
    function handleToggleClick(e) {
        e.stopPropagation();
        toggleSidebar();
    }

    function toggleSidebar() {
        if (isExpanded) {
            collapseSidebar();
        } else {
            expandSidebar();
        }
    }

    function expandSidebar() {
        sidebar.classList.add('expanded');
        isExpanded = true;
        toggleBtn.setAttribute('aria-expanded', 'true');
    }

    function collapseSidebar() {
        sidebar.classList.remove('expanded');
        isExpanded = false;
        toggleBtn.setAttribute('aria-expanded', 'false');
    }

    // ==================== CLICK OUTSIDE DETECTION ====================
    function handleOutsideClick(e) {
        // If sidebar is expanded and click is outside sidebar
        if (isExpanded && !sidebar.contains(e.target)) {
            collapseSidebar();
        }
    }

    // ==================== KEYBOARD ACCESSIBILITY ====================
    function handleKeyPress(e) {
        // Toggle on Enter or Space
        if (e.key === 'Enter' || e.key === ' ') {
            e.preventDefault();
            toggleSidebar();
        }
        // Close on Escape
        if (e.key === 'Escape' && isExpanded) {
            collapseSidebar();
        }
    }

    // ==================== RESPONSIVE HANDLER ====================
    function handleResize() {
        // Auto-collapse on mobile when resizing
        if (window.innerWidth <= 768 && isExpanded) {
            collapseSidebar();
        }
    }

    // ==================== SET ACTIVE MENU ITEM ====================
    function setActiveMenuItem() {
        const currentPath = window.location.pathname;

        menuItems.forEach(item => {
            const href = item.getAttribute('href');

            // Check if current path matches the menu item
            if (href && currentPath.includes(href.split('/').pop())) {
                item.classList.add('active');
            }
        });
    }

    // ==================== UTILITY FUNCTIONS ====================

    // Public API (optional)
    window.Sidebar = {
        expand: expandSidebar,
        collapse: collapseSidebar,
        toggle: toggleSidebar,
        isExpanded: function() { return isExpanded; }
    };

    // ==================== INITIALIZE ON DOM READY ====================
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

})();