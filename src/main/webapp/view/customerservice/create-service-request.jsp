<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo Service Order - Quản Lý Dịch Vụ Xe Ô Tô</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/customerservice/create-service-request.css">
</head>

<body>


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
                           <%--align-items: center; justify-content: center;--%>
                           ">
                    <!-- Nội dung trang home của bạn ở đây -->
                    <div class="main-container">
                        <!-- Page Header -->
                        <div class="page-header">
                            <h3 class="page-title">Create Service Order</h3>
                        </div>

                        <!-- Customer Information Card -->
                        <div class="customer-info-card">
                            <h5 class="customer-info-title">Customer Information</h5>

                            <div class="customer-info-row">
                                <div class="customer-info-item">
                                    <span class="customer-info-label">Full Name</span>
                                    <span class="customer-info-value">
                                        <c:out value="${customer.fullName}" />
                                    </span>
                                </div>

                                <div class="customer-info-item">
                                    <span class="customer-info-label">Email</span>
                                    <span class="customer-info-value">
                                        <c:out value="${customer.email}" />
                                    </span>
                                </div>

                                <div class="customer-info-item">
                                    <span class="customer-info-label">Phone Number</span>
                                    <span class="customer-info-value">
                                        <c:out value="${customer.phoneNumber}" />
                                    </span>
                                </div>
                            </div>
                        </div>

                        <!-- Service Order Form -->
                        <form id="serviceOrderForm" method="post"
                              action="${pageContext.request.contextPath}/customerservice/createRequest">

                            <input type="hidden" name="customerId" value="${customer.customerId}" />
                            <input type="hidden" name="appointmentId" value="${param.appointmentId}" />

                            <!-- Orders Container -->
                            <div id="ordersContainer">
                                <div class="service-order" data-index="0">
                                    <div class="service-order-header">
                                        <span class="service-order-badge">Order #1</span>
                                    </div>

                                    <button type="button" class="remove-order-btn" title="Xóa order này">✖</button>

                                    <!-- Vehicle Selection -->
                                    <div class="form-group position-relative">
                                        <label class="form-label">
                                            Select Vehicle <span class="required-mark">*</span>
                                        </label>
                                        <input type="hidden" name="vehicleIds[0]" class="vehicle-id" required/>
                                        <input type="text"
                                               class="form-control vehicle-input"
                                               placeholder="Find a car by license plate or model..."
                                               autocomplete="off"
                                               required/>
                                    </div>

                                    <!-- Services Selection -->
                                    <div class="form-group position-relative">
                                        <label class="form-label form-label-service">
                                            Select Service <span class="required-mark">*</span>
                                        </label>
                                        <input type="text"
                                               class="form-control service-input"
                                               placeholder="Find service..."
                                               autocomplete="off" />

                                        <!-- Selected Services Table -->
                                        <div class="service-table-wrapper">
                                            <div class="service-table-header">Selected service</div>
                                            <table class="service-table selected-services">
                                                <tbody>
                                                <tr class="no-service">
                                                    <td colspan="3">
                                                        No services have been selected yet</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>

                                    <!-- Notes -->
                                    <div class="form-group">
                                        <label class="form-label form-label-note">Note</label>
                                        <textarea class="form-control"
                                                  name="note"
                                                  rows="3"
                                                  placeholder="Enter any notes or special requests..."></textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="btn-group">
                                <button type="button" class="btn btn-add" id="addOrderBtn">
                                    Add
                                </button>
                                <button type="submit" class="btn btn-submit">
                                    Create
                                </button>
                            </div>
                        </form>
                        <jsp:include page="/view/customerservice/add-vehicle.jsp" />



                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script>
    const contextPath = "${pageContext.request.contextPath}";
    const customerId = "${customer.customerId}";
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-service-request.js"></script>

</body>
</html>
