<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <title>Accountant Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
      rel="stylesheet"
    />

    <style>
      body {
        background-color: #f8f9fa;
        display: flex;
        flex-direction: column;
        min-height: 100vh;
      }

      .sidebar {
        width: 140px;
        height: calc(100vh - 56px);
        position: fixed;
        top: 56px;
        left: 0;
        background-color: #fff;
        border-right: 1px solid #dee2e6;
        overflow-y: auto;
      }

      main {
        margin-left: 120px;
        margin-top: 56px;
        padding: 20px;
        flex-grow: 1;
      }

      footer {
        background-color: #fff;
        border-top: 1px solid #dee2e6;
        padding: 10px 20px;
        text-align: right;
        font-size: 0.9rem;
        color: #6c757d;
      }
    </style>
  </head>
  <body>
    <%@ include file="header.jsp" %> <%@ include file="sidebar.jsp" %>

    <main>
      <div class="container-fluid">
        <!-- Summary Cards -->
        <div class="row g-3 mb-4">
          <div class="col-md-4">
            <div class="card text-white bg-success h-100 shadow-sm">
              <div class="card-body">
                <h6>Total Revenue (This Month)</h6>
                <h2>
                  <fmt:formatNumber
                    value="${totalRevenue}"
                    type="currency"
                    currencySymbol=""
                    maxFractionDigits="0"
                  />
                  VND
                </h2>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card text-white bg-warning h-100 shadow-sm">
              <div class="card-body">
                <h6>Unpaid Invoices</h6>
                <h2>${unpaidCount}</h2>
              </div>
            </div>
          </div>
          <div class="col-md-4">
            <div class="card text-white bg-danger h-100 shadow-sm">
              <div class="card-body">
                <h6>Overdue Invoices</h6>
                <h2>${overdueCount}</h2>
              </div>
            </div>
          </div>
        </div>

        <!-- Recent Invoices -->
        <div class="card shadow-sm">
          <div class="card-header fw-semibold">Recent Invoices</div>
          <div class="card-body table-responsive">
            <table class="table table-hover align-middle">
              <thead class="table-light">
                <tr>
                  <th>Invoice #</th>
                  <th>Customer</th>
                  <th>Date</th>
                  <th>Total Amount</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach var="invoice" items="${recentInvoices}">
                  <tr>
                    <td>${invoice.invoiceNumber}</td>
                    <td>${invoice.customerName}</td>
                    <td>
                      <fmt:formatDate
                        value="${invoice.invoiceDate}"
                        pattern="dd/MM/yyyy"
                      />
                    </td>
                    <td>
                      <fmt:formatNumber
                        value="${invoice.totalAmount}"
                        type="currency"
                        currencySymbol=""
                        maxFractionDigits="0"
                      />
                      VND
                    </td>
                    <td>
                      <span
                        class="badge bg-${invoice.paymentStatus eq 'Paid' ? 'success' : 'warning'}"
                      >
                        ${invoice.paymentStatus}
                      </span>
                    </td>
                    <td>
                      <a href="#" class="btn btn-sm btn-outline-primary"
                        >View</a
                      >
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
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
                        <p style="color: #6b7280; font-size: 16px;">Welcome to Accountant Dashboard</p>
                    </div>
                </div>
            </main>
        </div>
      </div>
    </main>

    <%@ include file="footer.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>

    </div>
</div>
<jsp:include page="footer.jsp"/>