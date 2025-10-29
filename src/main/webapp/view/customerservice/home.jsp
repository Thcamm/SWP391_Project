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

    <title>Customer Service - Home</title>
    <jsp:include page="/common/employee/header.jsp" />

</head>

<body>
<div class="layout-wrapper layout-content-navbar">
    <div class="layout-container">
        <jsp:include page="/common/employee/sidebar.jsp" />

        <div class="layout-page">
            <jsp:include page="/common/employee/navbar.jsp" />

            <div class="content-wrapper">
                <div class="container-fluid flex-grow-1 container-p-y">



                </div>
                <jsp:include page="/common/employee/footer.jsp" />

                <div class="content-backdrop fade"></div>
            </div>
        </div>
    </div>
    <div class="layout-overlay layout-menu-toggle"></div>
</div>
<jsp:include page="/common/employee/script.jsp" />

</body>
</html>
