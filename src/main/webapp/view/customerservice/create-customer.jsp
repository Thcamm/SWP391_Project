<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Customer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customer/create-customer.css">
</head>
<body>
<jsp:include page="/view/customerservice/sidebar.jsp" />
<jsp:include page="/view/customerservice/result.jsp" />
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

            <div class="card-body">
                <!-- Full Name -->
                <div class="mb-3">
                    <label for="fullName" class="form-label"><b>Full Name</b> <span class="text-danger">*</span></label>
                    <input type="text" id="fullName" name="fullName" class="form-control" placeholder="Enter full name" required>
                </div>

                <!-- Email & Phone -->
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="email" class="form-label d-flex align-items-center gap-2">
                            <i class="bi bi-envelope"></i> <b>Email</b>
                        </label>
                        <input type="email" id="email" name="email" class="form-control" placeholder="example@email.com" required>
                    </div>

                    <div class="col-md-6">
                        <label for="phone" class="form-label d-flex align-items-center gap-2">
                            <i class="bi bi-telephone"></i> <b>Phone Number</b>
                        </label>
                        <input type="text" id="phone" name="phone" class="form-control" placeholder="0123456789" pattern="[0-9]{10,11}" required>
                    </div>
                </div>

                <!-- Gender & Date of Birth -->
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="gender" class="form-label">
                            <i class="fa-regular fa-venus-mars"></i> <b>Gender</b> </label>
                        <select id="gender" name="gender" class="form-select">
                            <option value="">Select gender</option>
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                            <option value="other">Other</option>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label for="birthDate" class="form-label d-flex align-items-center gap-2">
                            <i class="bi bi-calendar3"></i><b>Date of Birth</b>
                        </label>
                        <input type="date" id="birthDate" name="birthDate" class="form-control">
                    </div>
                </div>

                <!-- Address -->
                <div class="mt-3">
                    <label for="address" class="form-label"><i class="fa-regular fa-map-location-dot"></i><b>Address</b></label>
                    <textarea id="address" name="address" rows="2" class="form-control" placeholder="Enter full address"></textarea>
                </div>

            </div>
        </div>

        <!-- Action Buttons -->
        <div class="d-flex justify-content-end gap-3">
            <button type="reset" class="btn btn-outline-secondary">Cancel</button>
            <button type="submit" class="btn btn-primary">Create</button>
        </div>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-customer.js"></script>
</body>
</html>



