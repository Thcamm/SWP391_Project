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
                            <a href="${pageContext.request.contextPath}/accountant/report"
                               class="btn btn-outline-secondary mb-3"
                               style="border-radius: 8px;">
                                <i class="bi bi-arrow-left me-2"></i>Back to Dashboard
                            </a>
                            <h2 class="mb-1" style="font-size: 28px; font-weight: 700; color: #111827;">
                                <i class="bi bi-graph-up-arrow me-2" style="color: #667eea;"></i>
                                Detailed Revenue Report
                            </h2>
                            <p class="text-muted mb-0">Monthly revenue analysis and trends</p>
                        </div>

                        <!-- Month Filter -->
                        <div>
                            <form method="get" action="${pageContext.request.contextPath}/accountant/report" class="d-flex gap-2">
                                <input type="hidden" name="action" value="revenue">
                                <select name="months" class="form-select" style="border-radius: 8px; width: 200px;">
                                    <option value="6" ${selectedMonths == 6 ? 'selected' : ''}>Last 6 months</option>
                                    <option value="12" ${selectedMonths == 12 ? 'selected' : ''}>Last 12 months</option>
                                    <option value="24" ${selectedMonths == 24 ? 'selected' : ''}>Last 24 months</option>
                                </select>
                                <button type="submit" class="btn btn-primary" style="border-radius: 8px;">
                                    <i class="bi bi-funnel"></i> Apply
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Revenue Chart -->
                    <div class="card mb-4" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                        <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                            <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                <i class="bi bi-bar-chart-line me-2" style="color: #667eea;"></i>
                                Revenue Trend Chart
                            </h5>
                        </div>
                        <div class="card-body" style="padding: 2rem;">
                            <canvas id="revenueDetailChart" height="100"></canvas>
                        </div>
                    </div>

                    <!-- Revenue Table -->
                    <div class="card" style="border: 1px solid #e5e7eb; border-radius: 12px;">
                        <div class="card-header" style="background-color: #f9fafb; border-bottom: 1px solid #e5e7eb; padding: 1.25rem;">
                            <div class="d-flex justify-content-between align-items-center">
                                <h5 class="mb-0" style="color: #111827; font-weight: 600;">
                                    <i class="bi bi-table me-2" style="color: #667eea;"></i>
                                    Detailed Revenue by Month
                                </h5>
                                <button class="btn btn-sm btn-outline-success" onclick="exportToExcel()" style="border-radius: 6px;">
                                    <i class="bi bi-file-earmark-excel"></i> Export to Excel
                                </button>
                            </div>
                        </div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover mb-0" id="revenueTable">
                                    <thead style="background-color: #f9fafb;">
                                    <tr>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; border-top: none;">No</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; border-top: none;">Month/Year</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right; border-top: none;">Total Invoiced</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right; border-top: none;">Collected</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: right; border-top: none;">Outstanding</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center; border-top: none;">Invoices</th>
                                        <th style="padding: 1rem; font-weight: 600; color: #374151; text-align: center; border-top: none;">Collection %</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:set var="totalInvoiced" value="0" />
                                    <c:set var="totalPaid" value="0" />
                                    <c:set var="totalInvoiceCount" value="0" />

                                    <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                                        <c:set var="outstanding" value="${revenue.totalInvoiced - revenue.totalPaid}" />
                                        <c:set var="percentage" value="${revenue.totalInvoiced > 0 ? (revenue.totalPaid / revenue.totalInvoiced * 100) : 0}" />

                                        <c:set var="totalInvoiced" value="${totalInvoiced + revenue.totalInvoiced}" />
                                        <c:set var="totalPaid" value="${totalPaid + revenue.totalPaid}" />
                                        <c:set var="totalInvoiceCount" value="${totalInvoiceCount + revenue.count}" />

                                        <tr style="transition: all 0.2s;"
                                            onmouseover="this.style.backgroundColor='#f9fafb'"
                                            onmouseout="this.style.backgroundColor='white'">
                                            <td style="padding: 1rem;">
                                                <span class="badge bg-secondary">${status.index + 1}</span>
                                            </td>
                                            <td style="padding: 1rem;">
                                                <strong>${revenue.month}/${revenue.year}</strong>
                                            </td>
                                            <td style="padding: 1rem; text-align: right;">
                                                <strong style="color: #111827;">
                                                    <fmt:formatNumber value="${revenue.totalInvoiced}" pattern="#,###"/> VND
                                                </strong>
                                            </td>
                                            <td style="padding: 1rem; text-align: right;">
                                                <strong style="color: #059669;">
                                                    <fmt:formatNumber value="${revenue.totalPaid}" pattern="#,###"/> VND
                                                </strong>
                                            </td>
                                            <td style="padding: 1rem; text-align: right;">
                                                <strong style="color: ${outstanding > 0 ? '#dc2626' : '#6b7280'};">
                                                    <fmt:formatNumber value="${outstanding}" pattern="#,###"/> VND
                                                </strong>
                                            </td>
                                            <td style="padding: 1rem; text-align: center;">
                                                <span class="badge bg-primary" style="font-size: 0.9rem; padding: 0.5rem 1rem; border-radius: 20px;">
                                                        ${revenue.count}
                                                </span>
                                            </td>
                                            <td style="padding: 1rem; text-align: center;">
                                                <div class="d-flex align-items-center justify-content-center gap-2">
                                                    <div class="progress" style="width: 60px; height: 8px;">
                                                        <div class="progress-bar ${percentage >= 80 ? 'bg-success' : percentage >= 50 ? 'bg-warning' : 'bg-danger'}"
                                                             role="progressbar"
                                                             style="width: ${percentage}%"></div>
                                                    </div>
                                                    <small class="fw-semibold">
                                                        <fmt:formatNumber value="${percentage}" pattern="##0.0"/>%
                                                    </small>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                    <tfoot style="background-color: #f3f4f6; border-top: 2px solid #e5e7eb;">
                                    <tr>
                                        <td colspan="2" style="padding: 1rem; font-weight: 700; color: #111827;">
                                            <i class="bi bi-calculator me-2"></i>TOTAL
                                        </td>
                                        <td style="padding: 1rem; text-align: right; font-weight: 700; color: #111827;">
                                            <fmt:formatNumber value="${totalInvoiced}" pattern="#,###"/> VND
                                        </td>
                                        <td style="padding: 1rem; text-align: right; font-weight: 700; color: #059669;">
                                            <fmt:formatNumber value="${totalPaid}" pattern="#,###"/> VND
                                        </td>
                                        <td style="padding: 1rem; text-align: right; font-weight: 700; color: #dc2626;">
                                            <fmt:formatNumber value="${totalInvoiced - totalPaid}" pattern="#,###"/> VND
                                        </td>
                                        <td style="padding: 1rem; text-align: center; font-weight: 700;">
                                            ${totalInvoiceCount}
                                        </td>
                                        <td style="padding: 1rem; text-align: center; font-weight: 700;">
                                            <fmt:formatNumber value="${totalInvoiced > 0 ? (totalPaid / totalInvoiced * 100) : 0}" pattern="##0.0"/>%
                                        </td>
                                    </tr>
                                    </tfoot>
                                </table>
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
<script>
    const ctx = document.getElementById('revenueDetailChart').getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: [
                <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                '${revenue.month}/${revenue.year}'${!status.last ? ',' : ''}
                </c:forEach>
            ],
            datasets: [
                {
                    label: 'Total Invoiced',
                    data: [
                        <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                        ${revenue.totalInvoiced}${!status.last ? ',' : ''}
                        </c:forEach>
                    ],
                    borderColor: 'rgba(102, 126, 234, 1)',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: 'rgba(102, 126, 234, 1)',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                },
                {
                    label: 'Collected',
                    data: [
                        <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                        ${revenue.totalPaid}${!status.last ? ',' : ''}
                        </c:forEach>
                    ],
                    borderColor: 'rgba(5, 150, 105, 1)',
                    backgroundColor: 'rgba(5, 150, 105, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 5,
                    pointHoverRadius: 7,
                    pointBackgroundColor: 'rgba(5, 150, 105, 1)',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            interaction: {
                mode: 'index',
                intersect: false
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        usePointStyle: true,
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
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            label += context.parsed.y.toLocaleString('en-US') + ' VND';
                            return label;
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

    function exportToExcel() {
        alert(' Export to Excel feature will be developed in the future!\n\nStay tuned for updates.');
        console.log('Export function called - Feature under development');
    }
</script>

<jsp:include page="footer.jsp"/>