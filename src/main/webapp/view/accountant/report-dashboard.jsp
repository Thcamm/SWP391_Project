<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
                         min-height: calc(100vh - 64px - 1.25rem);">

                    <!-- Page Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                                <i class="bi bi-graph-up me-2" style="color: #667eea;"></i>
                                Financial Reports
                            </h2>
                            <p class="text-muted mb-0">Financial reports and revenue analysis</p>
                        </div>

                        <!-- Date Range Filter -->
                        <div class="d-flex gap-2">
                            <form method="get" action="${pageContext.request.contextPath}/accountant/report" class="d-flex gap-2">
                                <input type="date" name="startDate" class="form-control" value="${startDate}" style="border-radius: 8px;">
                                <input type="date" name="endDate" class="form-control" value="${endDate}" style="border-radius: 8px;">
                                <button type="submit" class="btn btn-primary" style="border-radius: 8px;">
                                    <i class="bi bi-funnel"></i> Filter
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Summary Cards -->
                    <div class="row g-3 mb-4">
                        <!-- Total Invoiced -->
                        <div class="col-md-3">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px; border-left: 4px solid #667eea;">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <p class="text-muted mb-1" style="font-size: 0.875rem;">Total Invoiced</p>
                                            <h3 class="mb-0" style="color: #111827; font-weight: 700;">
                                                <fmt:formatNumber value="${revenueSummary.totalInvoiced}" pattern="#,###"/> ₫
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: #eef2ff; border-radius: 8px;">
                                            <i class="bi bi-receipt" style="font-size: 1.5rem; color: #667eea;"></i>
                                        </div>
                                    </div>
                                    <small class="text-muted">
                                        <i class="bi bi-file-text"></i> ${revenueSummary.count} invoices
                                    </small>
                                </div>
                            </div>
                        </div>

                        <!-- Total Paid -->
                        <div class="col-md-3">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px; border-left: 4px solid #059669;">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <p class="text-muted mb-1" style="font-size: 0.875rem;">Collected</p>
                                            <h3 class="mb-0" style="color: #059669; font-weight: 700;">
                                                <fmt:formatNumber value="${revenueSummary.totalPaid}" pattern="#,###"/> ₫
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: #d1fae5; border-radius: 8px;">
                                            <i class="bi bi-cash-coin" style="font-size: 1.5rem; color: #059669;"></i>
                                        </div>
                                    </div>
                                    <small class="text-success">
                                        <i class="bi bi-arrow-up"></i>
                                        <c:set var="paidPercentage" value="${revenueSummary.totalInvoiced > 0 ? (revenueSummary.totalPaid / revenueSummary.totalInvoiced * 100) : 0}" />
                                        <fmt:formatNumber value="${paidPercentage}" pattern="##0.0"/>% collected
                                    </small>
                                </div>
                            </div>
                        </div>

                        <!-- Outstanding -->
                        <div class="col-md-3">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px; border-left: 4px solid #f59e0b;">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <p class="text-muted mb-1" style="font-size: 0.875rem;">Outstanding</p>
                                            <h3 class="mb-0" style="color: #f59e0b; font-weight: 700;">
                                                <fmt:formatNumber value="${revenueSummary.totalOutstanding}" pattern="#,###"/> ₫
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: #fef3c7; border-radius: 8px;">
                                            <i class="bi bi-exclamation-triangle" style="font-size: 1.5rem; color: #f59e0b;"></i>
                                        </div>
                                    </div>
                                    <small class="text-warning">
                                        <i class="bi bi-clock-history"></i> Uncollected
                                    </small>
                                </div>
                            </div>
                        </div>

                        <!-- Overdue -->
                        <div class="col-md-3">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px; border-left: 4px solid #dc2626;">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <p class="text-muted mb-1" style="font-size: 0.875rem;">Overdue</p>
                                            <h3 class="mb-0" style="color: #dc2626; font-weight: 700;">
                                                <c:choose>
                                                    <c:when test="${not empty overdueReport}">
                                                        ${overdueReport[0].count}
                                                    </c:when>
                                                    <c:otherwise>0</c:otherwise>
                                                </c:choose>
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: #fee2e2; border-radius: 8px;">
                                            <i class="bi bi-clock" style="font-size: 1.5rem; color: #dc2626;"></i>
                                        </div>
                                    </div>
                                    <small class="text-danger">
                                        <i class="bi bi-exclamation-circle"></i> Needs attention
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Charts Row -->
                    <div class="row g-3 mb-4">
                        <!-- Revenue By Month Chart -->
                        <div class="col-md-8">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                            <i class="bi bi-bar-chart me-2" style="color: #667eea;"></i>
                                            Revenue - Last 6 Months
                                        </h5>
                                        <a href="${pageContext.request.contextPath}/accountant/report?action=revenue"
                                           class="btn btn-sm btn-outline-primary"
                                           style="border-radius: 6px;">
                                            Details <i class="bi bi-arrow-right"></i>
                                        </a>
                                    </div>
                                </div>
                                <div class="card-body" style="padding: 1.5rem;">
                                    <canvas id="revenueChart" height="80"></canvas>
                                </div>
                            </div>
                        </div>

                        <!-- Payment Method Chart -->
                        <div class="col-md-4">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                    <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                        <i class="bi bi-pie-chart me-2" style="color: #059669;"></i>
                                        Payment Methods
                                    </h5>
                                </div>
                                <div class="card-body" style="padding: 1.5rem;">
                                    <canvas id="paymentMethodChart"></canvas>

                                    <!-- Payment Method Summary -->
                                    <div class="mt-3">
                                        <c:forEach var="method" items="${paymentByMethod}">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <div class="d-flex align-items-center gap-2">
                                                    <c:choose>
                                                        <c:when test="${method.paymentMethod == 'ONLINE'}">
                                                            <div style="width: 12px; height: 12px; background-color: #3b82f6; border-radius: 50%;"></div>
                                                            <span>Online/Transfer</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div style="width: 12px; height: 12px; background-color: #6b7280; border-radius: 50%;"></div>
                                                            <span>Cash</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <strong>
                                                    <fmt:formatNumber value="${method.paymentAmount}" pattern="#,###"/> ₫
                                                </strong>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Tables Row -->
                    <div class="row g-3">
                        <!-- Invoice Status -->
                        <div class="col-md-6">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                    <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                        <i class="bi bi-card-checklist me-2" style="color: #667eea;"></i>
                                        Invoice Status
                                    </h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-hover mb-0">
                                            <thead style="background-color: #f9fafb;">
                                            <tr>
                                                <th style="padding: 1rem; border-top: none;">Status</th>
                                                <th style="padding: 1rem; border-top: none; text-align: right;">Count</th>
                                                <th style="padding: 1rem; border-top: none; text-align: right;">Value</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="status" items="${invoiceByStatus}">
                                                <tr>
                                                    <td style="padding: 1rem;">
                                                        <c:choose>
                                                            <c:when test="${status.status == 'UNPAID'}">
                                                                <span class="badge" style="background-color: #dc2626; padding: 0.5rem 1rem; border-radius: 20px;">
                                                                    Unpaid
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${status.status == 'PARTIALLY_PAID'}">
                                                                <span class="badge" style="background-color: #f59e0b; padding: 0.5rem 1rem; border-radius: 20px;">
                                                                    Partially Paid
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${status.status == 'PAID'}">
                                                                <span class="badge" style="background-color: #059669; padding: 0.5rem 1rem; border-radius: 20px;">
                                                                    Paid
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${status.status == 'VOID'}">
                                                                <span class="badge" style="background-color: #6b7280; padding: 0.5rem 1rem; border-radius: 20px;">
                                                                    Voided
                                                                </span>
                                                            </c:when>
                                                        </c:choose>
                                                    </td>
                                                    <td style="padding: 1rem; text-align: right;">
                                                        <strong>${status.invoiceCount}</strong>
                                                    </td>
                                                    <td style="padding: 1rem; text-align: right;">
                                                        <strong><fmt:formatNumber value="${status.totalInvoiced}" pattern="#,###"/> ₫</strong>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Top Customers -->
                        <div class="col-md-6">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                            <i class="bi bi-trophy me-2" style="color: #f59e0b;"></i>
                                            Top 5 Customers
                                        </h5>
                                        <a href="${pageContext.request.contextPath}/accountant/report?action=customer"
                                           class="btn btn-sm btn-outline-primary"
                                           style="border-radius: 6px;">
                                            View all <i class="bi bi-arrow-right"></i>
                                        </a>
                                    </div>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-hover mb-0">
                                            <thead style="background-color: #f9fafb;">
                                            <tr>
                                                <th style="padding: 1rem; border-top: none;">Customer</th>
                                                <th style="padding: 1rem; border-top: none; text-align: right;">Paid</th>
                                                <th style="padding: 1rem; border-top: none; text-align: right;">Outstanding</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:choose>
                                                <c:when test="${not empty topCustomers}">
                                                    <c:forEach var="customer" items="${topCustomers}" varStatus="status">
                                                        <tr>
                                                            <td style="padding: 1rem;">
                                                                <div class="d-flex align-items-center gap-2">
                                                                    <span class="badge bg-primary">${status.index + 1}</span>
                                                                    <div>
                                                                        <div class="fw-semibold">${customer.customerName}</div>
                                                                        <small class="text-muted">${customer.customerEmail}</small>
                                                                    </div>
                                                                </div>
                                                            </td>
                                                            <td style="padding: 1rem; text-align: right;">
                                                                <span class="text-success fw-semibold">
                                                                    <fmt:formatNumber value="${customer.totalPaid}" pattern="#,###"/> ₫
                                                                </span>
                                                            </td>
                                                            <td style="padding: 1rem; text-align: right;">
                                                                <span class="${customer.outstandingBalance > 0 ? 'text-danger' : 'text-muted'}">
                                                                    <fmt:formatNumber value="${customer.outstandingBalance}" pattern="#,###"/> ₫
                                                                </span>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="3" class="text-center py-4 text-muted">
                                                            <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                                                            <p class="mb-0 mt-2">No customer data available</p>
                                                        </td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    </div>
</div>

<!-- Chart.js -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<!-- Initialize Chart Data -->
<script>
    /**
     * Financial Report Charts Data
     * Author: Thcamm
     * Date: 2025-11-04
     */

// ==========================================
// 1. REVENUE DATA
// ==========================================
    const revenueData = {
        labels: [
            <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
            'M${revenue.month}/${revenue.year}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        totalInvoiced: [
            <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
            ${revenue.totalInvoiced}${!status.last ? ',' : ''}
            </c:forEach>
        ],
        totalPaid: [
            <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
            ${revenue.totalPaid}${!status.last ? ',' : ''}
            </c:forEach>
        ]
    };

    // ==========================================
    // 2. PAYMENT METHOD DATA
    // ==========================================
    const paymentMethodData = {
        labels: [
            <c:forEach var="method" items="${paymentByMethod}" varStatus="status">
            '${method.paymentMethod == "ONLINE" ? "Online/Transfer" : "Cash"}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        values: [
            <c:forEach var="method" items="${paymentByMethod}" varStatus="status">
            ${method.paymentAmount}${!status.last ? ',' : ''}
            </c:forEach>
        ],
        counts: [
            <c:forEach var="method" items="${paymentByMethod}" varStatus="status">
            ${method.paymentCount}${!status.last ? ',' : ''}
            </c:forEach>
        ]
    };

    console.log('📊 Report Data Initialized:');
    console.log('Revenue:', revenueData);
    console.log('Payment Methods:', paymentMethodData);
</script>

<!-- Render Charts -->
<script>
    document.addEventListener('DOMContentLoaded', function() {

        // ==========================================
        // 1. REVENUE CHART (Bar Chart)
        // ==========================================
        const revenueCtx = document.getElementById('revenueChart');
        if (revenueCtx) {
            new Chart(revenueCtx.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: revenueData.labels,
                    datasets: [
                        {
                            label: 'Total Invoiced',
                            data: revenueData.totalInvoiced,
                            backgroundColor: 'rgba(102, 126, 234, 0.6)',
                            borderColor: 'rgba(102, 126, 234, 1)',
                            borderWidth: 2,
                            borderRadius: 6
                        },
                        {
                            label: 'Collected',
                            data: revenueData.totalPaid,
                            backgroundColor: 'rgba(5, 150, 105, 0.6)',
                            borderColor: 'rgba(5, 150, 105, 1)',
                            borderWidth: 2,
                            borderRadius: 6
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'top',
                            labels: {
                                padding: 15,
                                font: {
                                    size: 12,
                                    weight: '600'
                                }
                            }
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const label = context.dataset.label || '';
                                    const value = context.parsed.y || 0;
                                    return label + ': ' + value.toLocaleString('vi-VN') + ' ₫';
                                }
                            }
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return (value / 1000000).toFixed(1) + 'M';
                                }
                            }
                        }
                    }
                }
            });
        }

        // ==========================================
        // 2. PAYMENT METHOD CHART (Doughnut Chart)
        // ==========================================
        const paymentCtx = document.getElementById('paymentMethodChart');
        if (paymentCtx) {
            new Chart(paymentCtx.getContext('2d'), {
                type: 'doughnut',
                data: {
                    labels: paymentMethodData.labels,
                    datasets: [{
                        data: paymentMethodData.values,
                        backgroundColor: [
                            'rgba(59, 130, 246, 0.8)',  // Blue for Online
                            'rgba(107, 114, 128, 0.8)'  // Gray for Cash
                        ],
                        borderColor: [
                            'rgba(59, 130, 246, 1)',
                            'rgba(107, 114, 128, 1)'
                        ],
                        borderWidth: 2,
                        hoverOffset: 10
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: true,
                            position: 'bottom',
                            labels: {
                                padding: 15,
                                font: {
                                    size: 12
                                },
                                generateLabels: function(chart) {
                                    const data = chart.data;
                                    if (data.labels.length && data.datasets.length) {
                                        return data.labels.map((label, i) => {
                                            const value = data.datasets[0].data[i];
                                            const count = paymentMethodData.counts[i];
                                            const total = data.datasets[0].data.reduce((a, b) => a + b, 0);
                                            const percentage = ((value / total) * 100).toFixed(1);

                                            return {
                                                text: label + ' (' + percentage + '%)',
                                                fillStyle: data.datasets[0].backgroundColor[i],
                                                hidden: false,
                                                index: i
                                            };
                                        });
                                    }
                                    return [];
                                }
                            }
                        },
                        tooltip: {
                            callbacks: {
                                label: function(context) {
                                    const label = context.label || '';
                                    const value = context.parsed || 0;
                                    const count = paymentMethodData.counts[context.dataIndex];
                                    return [
                                        label,
                                        'Amount: ' + value.toLocaleString('vi-VN') + ' ₫',
                                        'Transactions: ' + count
                                    ];
                                }
                            }
                        }
                    },
                    cutout: '60%'
                }
            });
        }

        console.log('All financial charts rendered successfully');
    });
</script>

<jsp:include page="footer.jsp"/>