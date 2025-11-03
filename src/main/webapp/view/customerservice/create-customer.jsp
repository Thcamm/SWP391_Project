<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Tạo mới khách hàng</title>

    <link
            rel="stylesheet"
            href="${pageContext.request.contextPath}/assets/css/customerservice/create-customer.css"
    />
</head>
<body>
<%--<jsp:include page="/view/customerservice/sidebar.jsp" />--%>
<jsp:include page="/view/customerservice/result.jsp" />

<jsp:include page="header.jsp"/>

<div class="container-fluid p-0">
    <div class="row g-0">
        <div class="col-auto" style="width: 280px;">
            <jsp:include page="sidebar.jsp"/>
        </div>

        <!-- Main Content Column -->
        <div class="col">
            <main class="main" style="padding: 1.25rem; padding-bottom: 0;">
                <div class="content-card"
                     style="background: white;
                      border: 1px solid #e5e7eb;
                       border-radius: 12px;
                        padding: 2.5rem;
                         min-height: calc(100vh - 64px - 1.25rem);
                          display: flex; flex-direction: column;
                           align-items: center; justify-content: center;
                           padding-top: 0;
                            ">
                    <!-- Nội dung trang home của bạn ở đây -->
                    <div class="container">
                        <div class="header">
                            <h3>
                                <svg class="icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                                     viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                Create New Customer
                            </h3>
                            <p class="subtitle">Enter customer Information</p>
                        </div>

                        <!-- Form -->
                        <form id="createCustomerForm" method="post" action="${pageContext.request.contextPath}/customerservice/create-customer">
                            <!-- Thông tin khách hàng -->
                            <div class="card">
                                <div class="card-header">
                                    <h2 class="card-title">
                                        <svg class="icon" xmlns="http://www.w3.org/2000/svg" width="20" height="20"
                                             viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                                            <circle cx="12" cy="7" r="4"></circle>
                                        </svg>
                                        Customer Information
                                    </h2>
                                </div>

                                <div class="card-content">
                                    <!-- Họ và tên -->
                                    <div class="form-group">
                                        <label for="name">Full Name <span class="required">*</span></label>
                                        <input type="text" id="fullName" name="fullName" class="input"
                                               placeholder="Enter customer's full name" required>
                                    </div>

                                    <!-- Email và Số điện thoại -->
                                    <div class="grid-2">
                                        <div class="form-group">
                                            <label for="email">
                                                <svg class="icon-small" xmlns="http://www.w3.org/2000/svg"
                                                     width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                     stroke-linejoin="round">
                                                    <rect width="20" height="16" x="2" y="4" rx="2"></rect>
                                                    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"></path>
                                                </svg>
                                                Email<span class="required">*</span>
                                            </label>
                                            <input type="email" id="email" name="email" class="input"
                                                   placeholder="example@email.com" required>
                                        </div>

                                        <div class="form-group">
                                            <label for="phone">
                                                <svg class="icon-small" xmlns="http://www.w3.org/2000/svg"
                                                     width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                     stroke-linejoin="round">
                                                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07
                                             19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67
                                             A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72
                                             12.84 12.84 0 0 0 .7 2.81
                                             2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6
                                             l1.27-1.27a2 2 0 0 1 2.11-.45
                                             12.84 12.84 0 0 0 2.81.7
                                             A2 2 0 0 1 22 16.92z"></path>
                                                </svg>
                                                Phone Number<span class="required">*</span>
                                            </label>
                                            <input type="tel" id="phone" name="phone" class="input" placeholder="0123456789" required>
                                        </div>
                                    </div>


                                    <!-- Giới tính và Ngày sinh -->
                                    <div class="grid-2">
                                        <div class="form-group">
                                            <label for="gender">Gender</label>
                                            <select id="gender" name="gender" class="input select">
                                                <option value="">---Select Gender---</option>
                                                <option value="male">Male</option>
                                                <option value="female">Female</option>
                                                <option value="other">Other</option>
                                            </select>
                                        </div>

                                        <div class="form-group">
                                            <label for="birthDate">
                                                <svg class="icon-small" xmlns="http://www.w3.org/2000/svg"
                                                     width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                     stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                                     stroke-linejoin="round">
                                                    <rect width="18" height="18" x="3" y="4" rx="2" ry="2"></rect>
                                                    <line x1="16" x2="16" y1="2" y2="6"></line>
                                                    <line x1="8" x2="8" y1="2" y2="6"></line>
                                                    <line x1="3" x2="21" y1="10" y2="10"></line>
                                                </svg>
                                                Date Of Birth
                                            </label>
                                            <input type="date" id="dateOfBirth" name="dateOfBirth" class="input">
                                        </div>
                                    </div>
                                    <div class="grid-2">
                                        <div class="form-group">
                                            <label for="province">Provice / City<span class="required">*</span></label>
                                            <select id="province" name="province" class="input select" required>
                                                <option selected disabled value="">Select province / city</option>
                                            </select>
                                            <div id="provinceValidation" class="validation-text"></div>
                                        </div>

                                        <div class="form-group">
                                            <label for="district">District<span class="required">*</span></label>
                                            <select id="district" name="district" class="input select" required>
                                                <option selected disabled value="">Select District</option>
                                            </select>
                                            <div id="districtValidation" class="validation-text"></div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="addressDetail">Detail Address <span class="required">*</span></label>
                                        <textarea id="addressDetail" name="addressDetail" class="input textarea" rows="2"
                                                  placeholder="House number, street name..." required></textarea>
                                        <div id="addressDetailValidation" class="validation-text"></div>
                                    </div>

                                    <input type="hidden" id="address" name="address" />

                                </div>
                            </div>
                            <div class="form-actions">
                                <button type="button" class="btn btn-outline" id="cancelBtn">Cancel</button>
                                <button type="submit" class="btn btn-primary">Create</button>
                            </div>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-customer.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/user/address.js"></script>
</body>
</html>