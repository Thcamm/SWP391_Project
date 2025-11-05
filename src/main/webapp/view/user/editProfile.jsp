<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Edit Profile</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <style>
        body {
            background-color: #f8f9fa;
        }
        .validation-text {
            font-size: 0.875em;
            margin-top: 0.25rem;
        }
        .validation-text.valid {
            color: #198754;
        }
        .validation-text.invalid {
            color: #dc3545;
        }

        .form-control.is-valid, .form-select.is-valid {
            border-color: #198754;
        }
        .form-control.is-invalid, .form-select.is-invalid {
            border-color: #dc3545;
        }
    </style>
</head>
<body>
<jsp:include page="/common/customer/header.jsp" />
<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-lg-8 col-md-10">

            <div class="card shadow-sm">
                <div class="card-header bg-primary text-white">
                    <h2 class="h4 mb-0">Edit Profile</h2>
                </div>
                <div class="card-body p-4">

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                                ${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/user/profile" method="post" id="editProfileForm" novalidate>

                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name <span class="text-danger">*</span></label>
                            <input type="text"
                                   name="fullName"
                                   id="fullName"
                                   value="<c:out value='${user.fullName}'/>"
                                   class="form-control" required />
                            <div id="fullNameValidation" class="validation-text"></div>
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                            <input type="email"
                                   name="email"
                                   id="email"
                                   value="<c:out value='${user.email}'/>"
                                   class="form-control" required />
                            <div id="emailValidation" class="validation-text"></div>
                        </div>

                        <div class="mb-3">
                            <label for="phoneNumber" class="form-label">Phone Number</label>
                            <input type="tel"
                                   name="phoneNumber"
                                   id="phoneNumber"
                                   value="<c:out value='${user.phoneNumber}'/>"
                                   class="form-control" />
                            <div id="phoneNumberValidation" class="validation-text"></div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="province" class="form-label">Province <span class="text-danger">*</span></label>
                                <select class="form-select" id="province" name="province" required>
                                    <option selected disabled value="">Province</option>
                                </select>
                                <div id="provinceValidation" class="validation-text"></div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="district" class="form-label">District <span class="text-danger">*</span></label>
                                <select class="form-select" id="district" name="district" required>
                                    <option selected disabled value="">District</option>
                                </select>
                                <div id="districtValidation" class="validation-text"></div>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="addressDetail" class="form-label">Detailed address <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="addressDetail" name="addressDetail" rows="2" placeholder="House's number, Road name..." required></textarea>
                            <div id="addressDetailValidation" class="validation-text"></div>
                        </div>
                        <input type="hidden" id="address" name="address" />

                        <div class="mb-3">
                            <label for="gender" class="form-label">Gender <span class="text-danger">*</span></label>
                            <select class="form-select" id="gender" name="gender" required>
                                <option value="">Select gender</option>
                                <option value="Male" ${user.gender == 'Male' ? 'selected' : ''}>Male</option>
                                <option value="Female" ${user.gender == 'Female' ? 'selected' : ''}>Female</option>
                            </select>
                            <div id="genderValidation" class="validation-text"></div>
                        </div>

                        <div class="mb-3">
                            <label for="birthDate" class="form-label">Birth Date</label>
                            <input type="date"
                                   name="birthDate"
                                   id="birthDate"
                                   value="${user.birthDate}"
                                   class="form-control" />
                            <div id="birthDateValidation" class="validation-text"></div>
                        </div>

                        <div class="mt-4 d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-secondary">Back</a>
                            <button type="submit" class="btn btn-primary">Save Changes</button>
                        </div>

                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
<script src="${pageContext.request.contextPath}/assets/js/user/address.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/user/editProfile.js"></script>
<jsp:include page="/common/customer/footer.jsp" />
</body>
</html>