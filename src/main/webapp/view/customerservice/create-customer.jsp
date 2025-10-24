<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Customer</title>

    <!-- Bootstrap (if not already included in sidebar) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/create-customer.css">
</head>
<body>

<!-- Sidebar -->
<jsp:include page="/view/customerservice/sidebar.jsp" />

<!-- Main content -->
<div class="main-content container mt-4">
    <xh2 class="page-title mb-4">Create Customer</x`h2>

    <form id="createCustomerForm" action="${pageContext.request.contextPath}/customerservice/create-customer" method="post" class="customer-form">

        <div class="row mb-3">
            <div class="col-md-6">
                <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="fullName" name="fullName" required>
            </div>
            <div class="col-md-6">
                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                <input type="email" class="form-control" id="email" name="email" required>
            </div>
        </div>

        <div class="row mb-3">
            <div class="col-md-6">
                <label for="phone" class="form-label">Phone Number <span class="text-danger">*</span></label>
                <input type="text" class="form-control" id="phone" name="phone" pattern="\\d{10,11}" required placeholder="10–11 digits">
            </div>
            <div class="col-md-6">
                <label for="address" class="form-label">Address</label>
                <input type="text" class="form-control" id="address" name="address">
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="gender" class="form-label">Gender</label>
                <select class="form-select" id="gender" name="gender">
                    <option value="">Select gender</option>
                    <option value="male">Male</option>
                    <option value="female">Female</option>
                    <option value="other">Other</option>
                </select>
            </div>
            <div class="col-md-6">
                <label for="birthDate" class="form-label">Birth Date</label>
                <input type="date" class="form-control" id="birthDate" name="birthDate">
            </div>
        </div>
        <div class="mb-3">
            <label for="note" class="form-label">Note</label>
            <textarea class="form-control" id="note" name="note" rows="3"></textarea>
        </div>

        <div class="d-flex justify-content-end">
            <button type="reset" class="btn btn-secondary me-2">Reset</button>
            <button type="submit" class="btn btn-primary">Create</button>
        </div>

    </form>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom JS for validation -->
<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-customer.js"></script>


</body>
</html>
