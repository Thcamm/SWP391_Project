<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
                            text-align: center;">
                    <!-- Nội dung trang home của bạn ở đây -->
                    <div style="max-width: 800px; width: 100%;">
                        <h2 style="margin-bottom: 1rem; font-size: 32px; font-weight: 700; color: #111827;">Dashboard</h2>
                        <p style="color: #6b7280; font-size: 16px;">Welcome to Customer Service Dashboard</p>
                    </div>
                </div>
            </main>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>