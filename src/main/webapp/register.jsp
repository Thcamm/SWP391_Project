<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - Garage System</title>
    <link rel="stylesheet" href="https://unpkg.com/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="css/register.css" rel="stylesheet">
</head>
<body>
<div class="registration-container">
    <!-- Left Panel - Welcome Image -->
    <div class="left-panel">
        <img src="./assets/img/bsb-logo.svg" alt="Welcome to Garage System">
    </div>

    <!-- Right Panel - Registration Form -->
    <div class="right-panel">
        <div class="form-wrapper">
            <!-- Logo -->
            <div class="logo-container">
                <img src="./assets/img/bsb-logo.svg" alt="BootstrapBrain Logo">
            </div>

            <div class="form-header">
                <h2>Create Your Account</h2>
                <p>Join us today and get started</p>
            </div>

            <!-- Progress Bar -->
            <div class="progress-container">
                <div class="progress">
                    <div class="progress-bar" id="progressBar" role="progressbar" style="width: 33%"></div>
                </div>
                <div class="progress-info">
                    <span>Step <span id="currentStep">1</span> of 3</span>
                    <span><span id="progressPercent">33</span>% Complete</span>
                </div>
            </div>

            <!-- Alert Messages -->
            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <%= request.getAttribute("error") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <% if (request.getParameter("success") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                Registration successful! Please login.
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <form id="registrationForm" action="Register" method="POST" novalidate>

                <!-- Step 1: Personal Information -->
                <div class="step active" id="step1">
                    <h3 class="step-title">Personal Information</h3>
                    <p class="step-description">Let's start with your basic information</p>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="firstName" class="form-label">First Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="firstName" name="firstName"
                                   placeholder="Enter first name" required>
                            <div id="firstNameValidation" class="validation-text"></div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="lastName" class="form-label">Last Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="lastName" name="lastName"
                                   placeholder="Enter last name" required>
                            <div id="lastNameValidation" class="validation-text"></div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="phoneNumber" class="form-label">Phone Number <span class="text-danger">*</span></label>
                        <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber"
                               placeholder="Enter your phone number" required>
                        <div id="phoneNumberValidation" class="validation-text"></div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" id="email" name="email"
                               placeholder="name@example.com" required>
                        <div id="emailValidation" class="validation-text"></div>
                    </div>

                    <div class="navigation-buttons">
                        <div></div>
                        <button type="button" class="btn btn-primary btn-nav" id="nextStep1">
                            Next <i class="bi bi-arrow-right"></i>
                        </button>
                    </div>
                </div>

                <!-- Step 2: Address Information -->
                <div class="step" id="step2">
                    <h3 class="step-title">Personal Details</h3>
                    <p class="step-description">Tell us more about yourself</p>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="dateOfBirth" class="form-label">Birth Date <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" id="birthDate" name="birthDate" required>
                            <div id="birthDateValidation" class="validation-text"></div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="gender" class="form-label">Gender <span class="text-danger">*</span></label>
                            <select class="form-select" id="gender" name="gender" required>
                                <option value="">Select gender</option>
                                <option value="Male">Male</option>
                                <option value="Female">Female</option>
                            </select>
                            <div id="genderValidation" class="validation-text"></div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="address" class="form-label">Address <span class="text-danger">*</span></label>
                        <textarea class="form-control" id="address" name="address" rows="3"
                                  placeholder="Enter your detailed address" required></textarea>
                        <div id="addressValidation" class="validation-text"></div>
                    </div>

                    <div class="navigation-buttons">
                        <button type="button" class="btn btn-outline-secondary btn-nav" id="prevStep2">
                            <i class="bi bi-arrow-left"></i> Previous
                        </button>
                        <button type="button" class="btn btn-primary btn-nav" id="nextStep2">
                            Next <i class="bi bi-arrow-right"></i>
                        </button>
                    </div>
                </div>


                <!-- Step 3: Account Information -->
                <div class="step" id="step3">
                    <h3 class="step-title">Account Information</h3>
                    <p class="step-description">Create your login credentials</p>

                    <div class="mb-3">
                        <label for="userName" class="form-label">Username <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="userName" name="userName"
                               placeholder="Choose a username" required>
                        <div id="userNameValidation" class="validation-text"></div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="password" name="password"
                               placeholder="Create a strong password" required>
                        <div id="passwordValidation" class="validation-text"></div>
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirm Password <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" id="confirmPassword"
                               placeholder="Re-enter your password" required>
                        <div id="confirmPasswordValidation" class="validation-text"></div>
                    </div>

                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" id="iAgree" name="iAgree" required>
                        <label class="form-check-label" for="iAgree">
                            I agree to the <a href="#!">terms and conditions</a>
                        </label>
                    </div>

                    <div class="navigation-buttons">
                        <button type="button" class="btn btn-outline-secondary btn-nav" id="prevStep3">
                            <i class="bi bi-arrow-left"></i> Previous
                        </button>
                        <button type="submit" class="btn btn-success btn-nav" id="submitForm">
                            <i class="bi bi-check-circle"></i> Sign up
                        </button>
                    </div>
                </div>

                <div class="footer-link">
                    <p class="text-muted mb-0">Already have an account? <a href="login.jsp">Sign in</a></p>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://unpkg.com/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="js/register.js"></script>
</body>
</html>