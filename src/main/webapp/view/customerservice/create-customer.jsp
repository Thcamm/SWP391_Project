<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html
        lang="vi"
        class="light-style layout-menu-fixed"
        dir="ltr"
        data-theme="theme-default"
>
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Tạo mới khách hàng</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <%-- XÓA BỎ TOÀN BỘ THẺ <style> TÙY CHỈNH CŨ TỪ ĐÂY --%>

    <style>
        .form-control:focus,
        .form-select:focus {
            border-color: #4f46e5;
            box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
        }
        .form-label {
            font-weight: 600;
        }
    </style>
</head>
<body>
<jsp:include page="/view/customerservice/result.jsp" />
<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);">

                    <div class="container" style="max-width: 1200px; margin: 0 auto;">

                        <div class="mb-4">
                            <h2 style="font-weight: 700; color: #111827;">
                                <i class="bi bi-person-plus-fill me-2"></i>Create New Customer
                            </h2>
                            <p class="text-muted">Enter customer information</p>
                        </div>

                        <form id="createCustomerForm" method="post" action="${pageContext.request.contextPath}/customerservice/create-customer">

                            <div class="card shadow-sm border-0" style="border-radius: 12px;">
                                <div class="card-header bg-light" style="border-radius: 12px 12px 0 0; border-bottom: 1px solid #e5e7eb;">
                                    <h5 class="card-title mb-0">
                                        <i class="bi bi-person-lines-fill me-2"></i>Customer Information
                                    </h5>
                                </div>

                                <div class="card-body p-4">

                                    <div class="mb-3">
                                        <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                                        <input type="text" id="fullName" name="fullName" class="form-control"
                                               placeholder="Enter customer's full name" required>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label for="email" class="form-label">
                                                <i class="bi bi-envelope-at me-1"></i>Email <span class="text-danger">*</span>
                                            </label>
                                            <input type="email" id="email" name="email" class="form-control"
                                                   placeholder="example@email.com" required>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label for="phone" class="form-label">
                                                <i class="bi bi-telephone me-1"></i>Phone Number <span class="text-danger">*</span>
                                            </label>
                                            <input type="tel" id="phone" name="phone" class="form-control" placeholder="0123456789" required>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label for="gender" class="form-label">Gender</label>
                                            <select id="gender" name="gender" class="form-select">
                                                <option value="">--- Select Gender ---</option>
                                                <option value="male">Male</option>
                                                <option value="female">Female</option>
                                                <option value="other">Other</option>
                                            </select>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label for="birthDate" class="form-label">
                                                <i class="bi bi-calendar-event me-1"></i>Date Of Birth
                                            </label>
                                            <input type="date" id="birthDate" name="birthDate" class="form-control">
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label for="province" class="form-label">Province / City <span class="text-danger">*</span></label>
                                            <select id="province" name="province" class="form-select" required>
                                                <option selected disabled value="">Select province / city</option>
                                            </select>
                                            <div id="provinceValidation" class="invalid-feedback"></div>
                                        </div>

                                        <div class="col-md-6 mb-3">
                                            <label for="district" class="form-label">District <span class="text-danger">*</span></label>
                                            <select id="district" name="district" class="form-select" required>
                                                <option selected disabled value="">Select District</option>
                                            </select>
                                            <div id="districtValidation" class="invalid-feedback"></div>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label for="addressDetail" class="form-label">Detail Address <span class="text-danger">*</span></label>
                                        <textarea id="addressDetail" name="addressDetail" class="form-control" rows="2"
                                                  placeholder="House number, street name..." required></textarea>
                                        <div id="addressDetailValidation" class="invalid-feedback"></div>
                                    </div>

                                    <input type="hidden" id="address" name="address" />
                                </div>
                            </div>

                            <div class="d-flex justify-content-end gap-2 mt-4">
                                <a href="${pageContext.request.contextPath}/customerservice/search-customer" class="btn btn-secondary">
                                    <i class="bi bi-x-circle me-1"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <a><i class="bi bi-check-circle me-2"></i> Create Customer
                                    </a>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-customer.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/user/address.js"></script>

</body>
</html>