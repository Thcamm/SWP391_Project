<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- Chỉ hiển thị nếu có nhiều hơn 1 trang --%>
<c:if test="${param.totalPages > 1}">

    <%--
      ==================================================================
      PHẦN LOGIC TÍNH TOÁN
      ==================================================================
      Chúng ta sẽ tính toán để chỉ hiển thị 5 số trang (pageWindow = 2)
      xung quanh trang hiện tại.
    --%>
    <c:set var="currentPage" value="${param.currentPage}" />
    <c:set var="totalPages" value="${param.totalPages}" />
    <c:set var="baseUrl" value="${pageContext.request.contextPath}${param.baseUrl}" />
    <c:set var="query" value="${param.queryString}" />

    <c:set var="pageWindow" value="2" /> <%-- Hiển thị 2 trang trước và 2 trang sau trang hiện tại --%>

    <%-- Tính toán trang bắt đầu (begin) và trang kết thúc (end) --%>
    <c:set var="begin" value="${currentPage - pageWindow}" />
    <c:set var="end" value="${currentPage + pageWindow}" />

    <%-- Điều chỉnh nếu 'begin' quá nhỏ (ví dụ: trang 1, 2) --%>
    <c:if test="${begin < 1}">
        <c:set var="end" value="${end + (1 - begin)}" />
        <c:set var="begin" value="1" />
    </c:if>

    <%-- Điều chỉnh nếu 'end' quá lớn (ví dụ: trang cuối, áp cuối) --%>
    <c:if test="${end > totalPages}">
        <c:set var="begin" value="${begin - (end - totalPages)}" />
        <c:set var="end" value="${totalPages}" />
    </c:if>

    <%-- Đảm bảo 'begin' không bao giờ nhỏ hơn 1 (cho trường hợp có ít hơn 5 trang) --%>
    <c:if test="${begin < 1}">
        <c:set var="begin" value="1" />
    </c:if>

    <%--
      ==================================================================
      PHẦN HIỂN THỊ (HTML)
      ==================================================================
    --%>
    <div class="d-flex justify-content-between align-items-center mt-4 flex-wrap gap-2">

            <%-- Thông tin (ví dụ: Page 1 of 10) --%>
        <div class="text-muted" style="font-size: 0.9rem;">
            Page <strong>${currentPage}</strong> of <strong>${totalPages}</strong>
        </div>

            <%-- Các nút phân trang --%>
        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center mb-0">

                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="${baseUrl}?page=1${query}" title="First Page">
                        <i class="bi bi-chevron-double-left"></i>
                    </a>
                </li>

                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                    <a class="page-link" href="${baseUrl}?page=${currentPage - 1}${query}" title="Previous">
                        <i class="bi bi-chevron-left"></i>
                    </a>
                </li>

                <c:if test="${begin > 1}">
                    <li class="page-item disabled"><span class="page-link">...</span></li>
                </c:if>

                <c:forEach var="i" begin="${begin}" end="${end}">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <c:set var="pageParam" value="${param.paramName != null ? param.paramName : 'page'}" />

                        <a class="page-link" href="${baseUrl}?${pageParam}=${i}${query}">${i}</a>

                    </li>
                </c:forEach>

                <c:if test="${end < totalPages}">
                    <li class="page-item disabled"><span class="page-link">...</span></li>
                </c:if>

                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="${baseUrl}?page=${currentPage + 1}${query}" title="Next">
                        <i class="bi bi-chevron-right"></i>
                    </a>
                </li>

                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                    <a class="page-link" href="${baseUrl}?page=${totalPages}${query}" title="Last Page">
                        <i class="bi bi-chevron-double-right"></i>
                    </a>
                </li>
            </ul>
        </nav>
    </div>

</c:if>