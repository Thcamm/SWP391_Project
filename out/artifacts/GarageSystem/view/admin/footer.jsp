<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<footer class="admin-footer bg-dark text-light mt-5">
  <div class="container-fluid py-4">
    <div class="row">
      <div class="col-md-6">
        <h5>Garage Management System</h5>
        <p class="mb-0">Professional vehicle service management platform</p>
      </div>
      <div class="col-md-3">
        <h6>Quick Links</h6>
        <ul class="list-unstyled">
          <li>
            <a
              href="${pageContext.request.contextPath}/admin/users"
              class="text-light text-decoration-none"
              >Dashboard</a
            >
          </li>
          <li>
            <a
              href="${pageContext.request.contextPath}/admin/rbac/roles"
              class="text-light text-decoration-none"
              >RBAC</a
            >
          </li>
        </ul>
      </div>
      <div class="col-md-3">
        <h6>Support</h6>
        <ul class="list-unstyled">
          <li>
            <a href="${pageContext.request.contextPath}/admin/users"
              class="text-light text-decoration-none"
              >Home</a
            >
          </li>
          <li>
            <a
              href="${pageContext.request.contextPath}/support-faq"
              class="text-light text-decoration-none"
              >FAQ</a
            >
          </li>
          <li>
            <a
              href="mailto:support@garage.com"
              class="text-light text-decoration-none"
              >Contact Support</a
            >
          </li>
        </ul>
      </div>
    </div>
    <hr class="my-3" />
    <div class="row">
      <div class="col-12 text-center">
        <small class="text-muted">
          © 2025 Garage Management System. All rights reserved.
          <span class="ms-2">Version 1.0.0</span>
        </small>
      </div>
    </div>
  </div>
</footer>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
