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

<jsp:include page="/common/employee/component/header.jsp" />
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <jsp:include page="/common/employee/component/sidebar.jsp" />

        <div class="layout-page">
            <jsp:include page="/common/employee/component/navbar.jsp" />

            <div class="content-wrapper">
                <div class="container-fluid flex-grow-1 container-p-y">

                    <div class="main-container">
                        <!-- Page Header -->
                        <div class="page-header">
                            <h3 class="page-title">Tạo Service Order</h3>
                        </div>

                        <!-- Customer Information Card -->
                        <div class="customer-info-card">
                            <h5 class="customer-info-title">Thông Tin Khách Hàng</h5>

                            <div class="customer-info-row">
                                <div class="customer-info-item">
                                    <span class="customer-info-label">Họ và Tên</span>
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
                                    <span class="customer-info-label">Số Điện Thoại</span>
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
                                            Chọn Xe <span class="required-mark">*</span>
                                        </label>
                                        <input type="hidden" name="vehicleIds[0]" class="vehicle-id" required/>
                                        <input type="text"
                                               class="form-control vehicle-input"
                                               placeholder="Tìm xe theo biển số hoặc model..."
                                               autocomplete="off"
                                               required/>
                                    </div>

                                    <!-- Services Selection -->
                                    <div class="form-group position-relative">
                                        <label class="form-label form-label-service">
                                            Chọn Dịch Vụ <span class="required-mark">*</span>
                                        </label>
                                        <input type="text"
                                               class="form-control service-input"
                                               placeholder="Tìm dịch vụ..."
                                               autocomplete="off" />

                                        <!-- Selected Services Table -->
                                        <div class="service-table-wrapper">
                                            <div class="service-table-header">Dịch vụ đã chọn</div>
                                            <table class="service-table selected-services">
                                                <tbody>
                                                <tr class="no-service">
                                                    <td colspan="3">Chưa có dịch vụ nào được chọn</td>
                                                </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>

                                    <!-- Notes -->
                                    <div class="form-group">
                                        <label class="form-label form-label-note">Ghi Chú</label>
                                        <textarea class="form-control"
                                                  name="note"
                                                  rows="3"
                                                  placeholder="Nhập ghi chú hoặc yêu cầu đặc biệt..."></textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="btn-group">
                                <button type="button" class="btn btn-add" id="addOrderBtn">
                                    ➕ Thêm Service Order
                                </button>
                                <button type="submit" class="btn btn-submit">
                                    ✓ Tạo Yêu Cầu
                                </button>
                            </div>
                        </form>
                        <!-- Modal Add Vehicle -->
                        <div class="modal fade" id="addVehicleModal" tabindex="-1" aria-labelledby="addVehicleModalLabel" aria-hidden="true">
                            <div class="modal-dialog modal-lg">
                                <div class="modal-content">

                                    <div class="modal-header">
                                        <h5 class="modal-title" id="addVehicleModalLabel">Thêm xe mới</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                                    </div>

                                    <form id="addVehicleForm" action="${pageContext.request.contextPath}/customerservice/addVehicle" method="post">
                                        <div class="modal-body">
                                            <jsp:include page="/view/customerservice/add-vehicle.jsp"/>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                            <button type="submit" class="btn btn-primary">Lưu xe</button>
                                        </div>
                                    </form>


                                </div>
                            </div>
                        </div>

                    </div>

                </div>
                <jsp:include page="/common/employee/component/footer.jsp" />

                <div class="content-backdrop fade"></div>
            </div>
        </div>
    </div>
    <div class="layout-overlay layout-menu-toggle"></div>
</div>
<jsp:include page="/common/employee/component/script.jsp" />


<script>
    const contextPath = "${pageContext.request.contextPath}";
    const customerId = "${customer.customerId}";
</script>
<script src="${pageContext.request.contextPath}/assets/js/customerservice/create-service-request.js"></script>
</body>
</html>
