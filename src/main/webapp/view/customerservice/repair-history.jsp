<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="container py-5">
    <h2 class="mb-4 text-center">Lịch Sử Sửa Chữa Của Bạn</h2>

    <c:choose>
        <%-- Lấy danh sách từ Servlet --%>
        <c:when test="${not empty journeyList}">
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle">
                    <thead class="table-dark text-center">
                    <tr>
                        <th>ID Yêu Cầu</th>
                        <th>Ngày Bắt Đầu</th>
                        <th>Loại Hình</th>
                        <th>Giai Đoạn</th>
                        <th>Trạng Thái Mới Nhất</th>
                        <th>Hành Động</th>
                    </tr>
                    </thead>
                    <tbody>
                        <%-- Dùng c:forEach để lặp qua danh sách --%>
                    <c:forEach var="journey" items="${journeyList}">
                        <tr>
                            <td class="text-center">#${journey.requestID}</td>
                            <td class="text-center">
                                <fmt:formatDate value="${journey.entryDate}" pattern="dd/MM/yyyy" />
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${journey.entryType == 'Appointment'}">
                                        <span class="badge bg-primary">Đặt lịch</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-secondary">Walk-in</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <span class="badge bg-dark">${journey.latestStage}</span>
                            </td>
                            <td class="text-center">
                                    <%-- (Tùy chọn: Thêm màu mè cho status) --%>
                                <span class="badge bg-info">${journey.latestStatus}</span>
                            </td>
                            <td class="text-center">
                                    <%--
                                     ĐÂY LÀ LINK KẾT NỐI
                                     Nó trỏ đến Servlet "repair-tracker" và đính kèm ID
                                    --%>
                                <a href="${pageContext.request.contextPath}/customerservice/repair-detail?id=${journey.requestID}"
                                   class="btn btn-primary btn-sm">
                                    <i class="bi bi-eye"></i> Theo dõi
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <%-- Khi không có lịch sử nào --%>
        <c:otherwise>
            <div class="alert alert-info text-center">
                Bạn chưa có lịch sử sửa chữa nào.
            </div>
        </c:otherwise>
    </c:choose>
</div>