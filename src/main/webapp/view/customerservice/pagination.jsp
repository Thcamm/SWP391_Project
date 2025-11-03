<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${customerList.totalPages > 1}">
    <nav class="mt-4">
        <ul class="pagination justify-content-center">

            <c:if test="${customerList.currentPage > 1}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/customerservice/search-customer?page=${customerList.currentPage - 1}${param.queryString}">
                        &laquo; Prev
                    </a>
                </li>
            </c:if>

            <c:forEach begin="1" end="${customerList.totalPages}" var="i">
                <li class="page-item ${i == customerList.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/customerservice/search-customer?page=${i}${param.queryString}">
                            ${i}
                    </a>
                </li>
            </c:forEach>

            <c:if test="${customerList.currentPage < customerList.totalPages}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/customerservice/search-customer?page=${customerList.currentPage + 1}${param.queryString}">
                        Next &raquo;
                    </a>
                </li>
            </c:if>
        </ul>
    </nav>
</c:if>
