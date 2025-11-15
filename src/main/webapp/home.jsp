<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page
        import="model.user.User" %>
<%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carspa | Best Car Care Services</title>

    <!-- Bootstrap -->
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <!-- FontAwesome -->
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
    />
    <!-- Custom CSS -->
    <link
      rel="stylesheet"
      type="text/css"
      href="${pageContext.request.contextPath}/assets/css/user/chatbot.css"
    />

    <style>
      :root {
        --primary-color: #3a3a3c;
        --secondary-color: #dfdfdf;
        --dark-bg: #111111;
        --brand-red: #dc3545;
      }

      body {
        font-family: "Inter", sans-serif;
        background-color: var(--dark-bg);
        color: var(--primary-color);
        padding-top: 120px; /* tránh bị che bởi header fixed-top */
      }

      /* Hero Section */
      .hero-section {
        height: 100vh;
        min-height: 700px;
        background-image: url("https://images.unsplash.com/photo-1552537595-b30edb7a6331?q=80&w=2070&auto=format&fit=crop");
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        position: relative;
        display: flex;
        align-items: center;
        color: var(--primary-color);
      }

      .hero-section::before {
        content: "";
        position: absolute;
        inset: 0;
        background: linear-gradient(
          to top,
          rgba(0, 0, 0, 0.8),
          rgba(0, 0, 0, 0.3)
        );
      }

      .hero-content {
        position: relative;
        z-index: 2;
        text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.7);
      }

      .hero-content .sub-heading {
        font-size: 1.5rem;
        font-weight: 300;
      }

      .hero-content h1 {
        font-size: 4rem;
        font-weight: 300;
        margin-top: 0.5rem;
      }

      .hero-content h1 strong {
        display: block;
        font-size: 5.5rem;
        font-weight: 900;
        line-height: 1.1;
      }

      .btn-view-services {
        margin-top: 2rem;
        background: transparent;
        border: 1px solid var(--primary-color);
        color: var(--primary-color);
        padding: 0.8rem 2rem;
        font-weight: 500;
        border-radius: 4px;
        text-transform: uppercase;
        font-size: 0.9rem;
        letter-spacing: 1px;
        transition: all 0.3s ease;
      }

      .btn-view-services:hover {
        background-color: var(--primary-color);
        color: var(--dark-bg);
      }
    </style>
  </head>

  <body>
    <!-- ✅ HEADER -->
    <%@ include file="/common/header.jsp" %>

    <!-- ✅ MAIN CONTENT -->
    <main class="hero-section">
      <div class="container">
        <div class="row">
            <div class="col-lg-8">
                <div class="hero-content">
                    <p class="sub-heading">Carspa. Solving all vehicle problems</p>
                    <h1>Professional auto repair <strong>center.</strong></h1>
                    <a href="#" class="btn btn-view-services">
                        View all services <i class="fas fa-arrow-right ms-2"></i>
                    </a>
                </div>
            </div>
        </div>
      </div>
    </main>

    <!-- ✅ FOOTER -->
    <%@ include file="/common/footer.jsp" %>

    <!-- JS -->
    <script src="${pageContext.request.contextPath}/assets/js/user/chatbot.js"></script>
  </body>
</html>
