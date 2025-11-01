<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html
        lang="en"
        class="light-style layout-menu-fixed"
        dir="ltr"
        data-theme="theme-default"
        data-assets-path="../assets/"
        data-template="vertical-menu-template-free"
>
<head>
    <meta charset="utf-8" />
    <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0"
    />

    <title>
        <c:choose>
            <c:when test="${not empty sessionScope.roleName}">
                ${sessionScope.roleName} - Home
            </c:when>
            <c:otherwise>
                Home
            </c:otherwise>
        </c:choose>
    </title>



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



                </div>
                <jsp:include page="/common/employee/component/footer.jsp" />

                <div class="content-backdrop fade"></div>
            </div>
        </div>
    </div>
    <div class="layout-overlay layout-menu-toggle"></div>
</div>
<jsp:include page="/common/employee/component/script.jsp" />

</body>
</html>

