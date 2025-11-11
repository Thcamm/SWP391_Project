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
                    <div class="mb-4">
                        <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                            <i class="bi bi-bar-chart-line me-2" style="color: #667eea;"></i>
                            Business Statistics
                        </h2>
                        <p class="text-muted mb-0">Overview statistics and business performance analysis</p>
                    </div>

                    <!-- KPI Cards Row -->
                    <div class="row g-3 mb-4">
                        <!-- Total Revenue -->
                        <div class="col-md-3">
                            <div class="card"
                                 style="border: none;
                                        border-radius: 12px;
                                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                        color: white;">
                                <div class="card-body" style="padding: 1.5rem;">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <div>
                                            <p class="mb-1" style="font-size: 0.875rem; opacity: 0.9;">Total Revenue</p>
                                            <h3 class="mb-0" style="font-weight: 700;">
                                                <fmt:formatNumber value="${kpiSummary.totalInvoiced}" pattern="#,###"/> VND
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: rgba(255,255,255,0.2); border-radius: 10px;">
                                            <i class="bi bi-currency-dollar" style="font-size: 1.5rem;"></i>
                                        </div>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <c:choose>
                                            <c:when test="${growthRate >= 0}">
                                                <i class="bi bi-arrow-up-right me-1"></i>
                                                <span style="font-size: 0.875rem;">
                                                    +<fmt:formatNumber value="${growthRate}" pattern="##0.0"/>% vs last month
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-arrow-down-right me-1"></i>
                                                <span style="font-size: 0.875rem;">
                                                    <fmt:formatNumber value="${growthRate}" pattern="##0.0"/>% vs last month
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Invoices -->
                        <div class="col-md-3">
                            <div class="card"
                                 style="border: none;
                                        border-radius: 12px;
                                        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
                                        color: white;">
                                <div class="card-body" style="padding: 1.5rem;">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <div>
                                            <p class="mb-1" style="font-size: 0.875rem; opacity: 0.9;">Total Invoices</p>
                                            <h3 class="mb-0" style="font-weight: 700;">
                                                ${kpiSummary.count}
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: rgba(255,255,255,0.2); border-radius: 10px;">
                                            <i class="bi bi-receipt" style="font-size: 1.5rem;"></i>
                                        </div>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-calculator me-1"></i>
                                        <span style="font-size: 0.875rem;">
                                            Avg: <fmt:formatNumber value="${kpiSummary.amount}" pattern="#,###"/> VND
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Customers -->
                        <div class="col-md-3">
                            <div class="card"
                                 style="border: none;
                                        border-radius: 12px;
                                        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
                                        color: white;">
                                <div class="card-body" style="padding: 1.5rem;">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <div>
                                            <p class="mb-1" style="font-size: 0.875rem; opacity: 0.9;">Total Customers</p>
                                            <h3 class="mb-0" style="font-weight: 700;">
                                                ${totalCustomers}
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: rgba(255,255,255,0.2); border-radius: 10px;">
                                            <i class="bi bi-people" style="font-size: 1.5rem;"></i>
                                        </div>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <i class="bi bi-person-check me-1"></i>
                                        <span style="font-size: 0.875rem;">Active customers</span>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Collection Rate -->
                        <div class="col-md-3">
                            <div class="card"
                                 style="border: none;
                                        border-radius: 12px;
                                        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
                                        color: white;">
                                <div class="card-body" style="padding: 1.5rem;">
                                    <div class="d-flex justify-content-between align-items-start mb-3">
                                        <div>
                                            <p class="mb-1" style="font-size: 0.875rem; opacity: 0.9;">Collection Rate</p>
                                            <h3 class="mb-0" style="font-weight: 700;">
                                                <fmt:formatNumber value="${collectionRate}" pattern="##0.0"/>%
                                            </h3>
                                        </div>
                                        <div class="p-2" style="background-color: rgba(255,255,255,0.2); border-radius: 10px;">
                                            <i class="bi bi-pie-chart" style="font-size: 1.5rem;"></i>
                                        </div>
                                    </div>
                                    <div class="progress" style="height: 6px; background-color: rgba(255,255,255,0.3);">
                                        <c:set var="collectionWidth" value="${collectionRate > 100 ? 100 : collectionRate}" />
                                        <div class="progress-bar"
                                             style="width: ${collectionWidth}%; background-color: white;"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Performance Metrics - Only 3 cards now -->
                    <div class="row g-3 mb-4">
                        <div class="col-md-4">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-body text-center" style="padding: 2rem;">
                                    <div class="mb-3">
                                        <div class="d-inline-flex align-items-center justify-content-center"
                                             style="width: 80px;
                                                    height: 80px;
                                                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                                    border-radius: 50%;">
                                            <i class="bi bi-graph-up-arrow" style="font-size: 2rem; color: white;"></i>
                                        </div>
                                    </div>
                                    <h4 class="mb-2" style="color: #111827; font-weight: 700;">
                                        <c:if test="${growthRate >= 0}">+</c:if>
                                        <fmt:formatNumber value="${growthRate}" pattern="##0.00"/>%
                                    </h4>
                                    <p class="text-muted mb-0">Revenue Growth</p>
                                    <small class="text-muted">(vs Previous Month)</small>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-body text-center" style="padding: 2rem;">
                                    <div class="mb-3">
                                        <div class="d-inline-flex align-items-center justify-content-center"
                                             style="width: 80px;
                                                    height: 80px;
                                                    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
                                                    border-radius: 50%;">
                                            <i class="bi bi-cash-coin" style="font-size: 2rem; color: white;"></i>
                                        </div>
                                    </div>
                                    <h4 class="mb-2" style="color: #111827; font-weight: 700;">
                                        <fmt:formatNumber value="${collectionRate}" pattern="##0.0"/>%
                                    </h4>
                                    <p class="text-muted mb-0">Collection Rate</p>
                                    <small class="text-muted">(Payment Collection)</small>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-body text-center" style="padding: 2rem;">
                                    <div class="mb-3">
                                        <div class="d-inline-flex align-items-center justify-content-center"
                                             style="width: 80px;
                                                    height: 80px;
                                                    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
                                                    border-radius: 50%;">
                                            <i class="bi bi-wallet2" style="font-size: 2rem; color: white;"></i>
                                        </div>
                                    </div>
                                    <h4 class="mb-2" style="color: #111827; font-weight: 700;">
                                        <fmt:formatNumber value="${kpiSummary.amount}" pattern="#,###"/> VND
                                    </h4>
                                    <p class="text-muted mb-0">Avg Invoice Value</p>
                                    <small class="text-muted">(Average per Invoice)</small>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Top Revenue Months Chart -->
                    <div class="row g-3 mb-4">
                        <div class="col-12">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                    <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                        <i class="bi bi-trophy me-2" style="color: #f59e0b;"></i>
                                        Top 5 Revenue Months (All Time)
                                    </h5>
                                </div>
                                <div class="card-body" style="padding: 2rem;">
                                    <canvas id="topMonthsChart" height="80"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Top Months Table -->
                    <div class="row g-3">
                        <div class="col-12">
                            <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                                <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                                    <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                        <i class="bi bi-list-ol me-2" style="color: #667eea;"></i>
                                        Best Performing Months
                                    </h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-hover mb-0">
                                            <thead style="background-color: #f9fafb;">
                                            <tr>
                                                <th style="padding: 1rem; border-top: none;">Rank</th>
                                                <th style="padding: 1rem; border-top: none;">Month/Year</th>
                                                <th style="padding: 1rem; text-align: right; border-top: none;">Revenue</th>
                                                <th style="padding: 1rem; text-align: center; border-top: none;">Invoices</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <c:forEach var="month" items="${topMonths}" varStatus="status">
                                                <tr>
                                                    <td style="padding: 1rem;">
                                                        <c:choose>
                                                            <c:when test="${status.index == 0}">
                                                                <span class="badge" style="background-color: #f59e0b; padding: 0.5rem 0.75rem; border-radius: 50%;">
                                                                    🥇
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${status.index == 1}">
                                                                <span class="badge" style="background-color: #94a3b8; padding: 0.5rem 0.75rem; border-radius: 50%;">
                                                                    🥈
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${status.index == 2}">
                                                                <span class="badge" style="background-color: #d97706; padding: 0.5rem 0.75rem; border-radius: 50%;">
                                                                    🥉
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">${status.index + 1}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td style="padding: 1rem;">
                                                        <strong>${month.yearMonth}</strong>
                                                    </td>
                                                    <td style="padding: 1rem; text-align: right;">
                                                        <strong style="color: #059669; font-size: 1.1rem;">
                                                            <fmt:formatNumber value="${month.amount}" pattern="#,###"/> VND
                                                        </strong>
                                                    </td>
                                                    <td style="padding: 1rem; text-align: center;">
                                                        <span class="badge bg-primary">${month.count}</span>
                                                    </td>
                                                </tr>
                                            </c:forEach>
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
    const topMonthsData = {
        labels: [
            <c:forEach var="month" items="${topMonths}" varStatus="status">
            '${month.yearMonth}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        values: [
            <c:forEach var="month" items="${topMonths}" varStatus="status">
            ${month.amount}${!status.last ? ',' : ''}
            </c:forEach>
        ],
        counts: [
            <c:forEach var="month" items="${topMonths}" varStatus="status">
            ${month.count}${!status.last ? ',' : ''}
            </c:forEach>
        ]
    };

    console.log(' Top Months Data:', topMonthsData);
</script>

<!-- Render Chart -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const ctx = document.getElementById('topMonthsChart');

        if (ctx && topMonthsData.labels.length > 0) {
            new Chart(ctx.getContext('2d'), {
                type: 'bar',
                data: {
                    labels: topMonthsData.labels,
                    datasets: [{
                        label: 'Revenue (VND)',
                        data: topMonthsData.values,
                        backgroundColor: [
                            'rgba(245, 158, 11, 0.8)',   // Gold
                            'rgba(148, 163, 184, 0.8)',  // Silver
                            'rgba(217, 119, 6, 0.8)',    // Bronze
                            'rgba(102, 126, 234, 0.8)',  // Purple
                            'rgba(79, 172, 254, 0.8)'    // Blue
                        ],
                        borderColor: [
                            'rgba(245, 158, 11, 1)',
                            'rgba(148, 163, 184, 1)',
                            'rgba(217, 119, 6, 1)',
                            'rgba(102, 126, 234, 1)',
                            'rgba(79, 172, 254, 1)'
                        ],
                        borderWidth: 2,
                        borderRadius: 8
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {
                            display: false
                        },
                        tooltip: {
                            callbacks: {
                                title: function(context) {
                                    const index = context[0].dataIndex;
                                    return topMonthsData.labels[index];
                                },
                                label: function(context) {
                                    return 'Revenue: ' + (context.parsed.y / 1000000).toFixed(2) + 'M VND';
                                },
                                afterLabel: function(context) {
                                    const index = context.dataIndex;
                                    return 'Invoices: ' + topMonthsData.counts[index];
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

            console.log(' Top Months Chart rendered successfully');
        } else {
            console.warn('️ No data available for Top Months chart');
        }
    });
</script>

<jsp:include page="footer.jsp"/>