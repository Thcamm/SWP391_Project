<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${param.totalPages > 1}">
    <nav class="mt-4">
        <ul class="pagination justify-content-center">

            <!-- Nút Prev -->
            <c:if test="${param.currentPage > 1}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}${param.baseUrl}?page=${param.currentPage - 1}${param.queryString}">
                        &laquo; Prev
                    </a>
                </li>
            </c:if>

            <!-- Danh sách trang -->
            <c:forEach begin="1" end="${param.totalPages}" var="i">
                <li class="page-item ${i == param.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}${param.baseUrl}?page=${i}${param.queryString}">
                            ${i}
                    </a>
                </li>
            </c:forEach>

            <!-- Nút Next -->
            <c:if test="${param.currentPage < param.totalPages}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}${param.baseUrl}?page=${param.currentPage + 1}${param.queryString}">
                        Next &raquo;
                    </a>
                </li>
            </c:if>
        </ul>
    </nav>
</c:if>