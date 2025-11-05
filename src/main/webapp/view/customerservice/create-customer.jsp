<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %> <%@
        taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Customer</title>

    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/customerservice/create-customer.css"
    />
  </head>
  <body>
    <%--<jsp:include page="/view/customerservice/sidebar.jsp" />--%>
    <jsp:include page="/view/customerservice/result.jsp" />
    <div class="container">
      <div class="header">
        <h1>
            <svg class="icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24"
                 viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
            </svg>
            Tạo mới khách hàng
        </h1>
        <p class="subtitle">Enter customer and vehicle information (if any)</p>
    </div>

      <!-- Form -->
      <form
        id="createCustomerForm"
        method="post"
        action="${pageContext.request.contextPath}/customerservice/create-customer"
      >
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
                           placeholder="Enter full name" required>
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
                                             A2 2 0 0 1 22 16.92z"
                    ></path>
                  </svg>
                  Số điện thoại<span class="required">*</span>
                </label>
                <input
                  type="tel"
                  id="phone"
                  name="phone"
                  class="input"
                  placeholder="0123456789"
                  required
                />
              </div>
            </div>


                <!-- Giới tính và Ngày sinh -->
                <div class="grid-2">
                    <div class="form-group">
                        <label for="gender">Gender</label>
                        <select id="gender" name="gender" class="input select">
                            <option value="">Select gender</option>
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
                            Date of Birth
                        </label>
                        <input type="date" id="dateOfBirth" name="dateOfBirth" class="input">
                    </div>
                </div>
                <div class="grid-2">
                    <div class="form-group">
                        <label for="province">Province / City <span class="required">*</span></label>
                        <select id="province" name="province" class="input select" required>
                            <option selected disabled value="">Select province / city</option>
                        </select>
                        <div id="provinceValidation" class="validation-text"></div>
                    </div>

                    <div class="form-group">
                        <label for="district">District <span class="required">*</span></label>
                        <select id="district" name="district" class="input select" required>
                            <option selected disabled value="">Select district</option>
                        </select>
                        <div id="districtValidation" class="validation-text"></div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="addressDetail">Detailed Address <span class="required">*</span></label>
                    <textarea id="addressDetail" name="addressDetail" class="input textarea" rows="2"
                              placeholder="House number, street name..." required></textarea>
                    <div id="addressDetailValidation" class="validation-text"></div>
                </div>

            <input type="hidden" id="address" name="address" />
          </div>
        </div>

        <!-- Toggle thêm thông tin xe -->
        <div class="card">
          <div class="card-content">
            <div class="toggle-container">
              <div>
                <label class="toggle-label">
                  <svg
                    class="icon"
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                  >
                    <path
                      d="M19 17h2c.6 0 1-.4 1-1v-3
                                             c0-.9-.7-1.7-1.5-1.9C18.7 10.6 16 10 16 10
                                             s-1.3-1.4-2.2-2.3c-.5-.4-1.1-.7-1.8-.7H5
                                             c-.6 0-1.1.4-1.4.9l-1.4 2.9A3.7 3.7 0 0 0 2 12v4
                                             c0 .6.4 1 1 1h2"></path>
                                <circle cx="7" cy="17" r="2"></circle>
                                <path d="M9 17h6"></path>
                                <circle cx="17" cy="17" r="2"></circle>
                            </svg>
                            Add Vehicle Information (<span id="vehicleCount">0</span>)
                        </label>
                        <p class="toggle-description">Turn on to enter customer's vehicle information</p>
                    </div>
                    <label class="switch">
                        <input type="checkbox" id="includeVehicle">
                        <span class="slider"></span>
                    </label>
                </div>
            </div>
        </div>

        <!-- Danh sách xe -->
        <div
          id="vehiclesSection"
          class="vehicles-section"
          style="display: none"
        >
          <div id="vehiclesList"></div>

            <div class="add-vehicle-container">
                <button type="button" id="addVehicleBtn" class="btn btn-outline">
                    <svg class="icon-small" xmlns="http://www.w3.org/2000/svg"
                         width="16" height="16" viewBox="0 0 24 24" fill="none"
                         stroke="currentColor" stroke-width="2" stroke-linecap="round"
                         stroke-linejoin="round">
                        <path d="M5 12h14"></path>
                        <path d="M12 5v14"></path>
                    </svg>
                    Add Vehicle
                </button>
            </div>
        </div>

        <div class="form-actions">
            <button type="button" class="btn btn-outline" id="cancelBtn">Cancel</button>
            <button type="submit" class="btn btn-primary">Create Customer</button>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-customer.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/user/address.js"></script>
</body>
</html>





