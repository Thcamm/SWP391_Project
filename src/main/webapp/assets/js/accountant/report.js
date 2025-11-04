// Revenue Chart
const revenueCtx = document.getElementById('revenueChart').getContext('2d');
const revenueChart = new Chart(revenueCtx, {
    type: 'bar',
    data: {
        labels: [
            <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                '${revenue.month}/${revenue.year}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        datasets: [{
            label: 'Total Invoiced',
            data: [
                <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                    ${revenue.totalInvoiced}${!status.last ? ',' : ''}
                </c:forEach>
            ],
            backgroundColor: 'rgba(102, 126, 234, 0.2)',
            borderColor: 'rgba(102, 126, 234, 1)',
            borderWidth: 2
        }, {
            label: 'Collected',
            data: [
                <c:forEach var="revenue" items="${revenueByMonth}" varStatus="status">
                    ${revenue.totalPaid}${!status.last ? ',' : ''}
                </c:forEach>
            ],
            backgroundColor: 'rgba(5, 150, 105, 0.2)',
            borderColor: 'rgba(5, 150, 105, 1)',
            borderWidth: 2
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: true,
                position: 'top'
            }
        },
        scales: {
            y: {
                beginAtZero: true,
                ticks: {
                    callback: function(value) {
                        return value.toLocaleString('en-US') + ' VND';
                    }
                }
            }
        }
    }
});

// Payment Method Chart
const paymentCtx = document.getElementById('paymentMethodChart').getContext('2d');
const paymentChart = new Chart(paymentCtx, {
    type: 'doughnut',
    data: {
        labels: [
            <c:forEach var="method" items="${paymentByMethod}" varStatus="status">
                '${method.paymentMethod}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        datasets: [{
            data: [
                <c:forEach var="method" items="${paymentByMethod}" varStatus="status">
                    ${method.paymentAmount}${!status.last ? ',' : ''}
                </c:forEach>
            ],
            backgroundColor: [
                'rgba(59, 130, 246, 0.8)',
                'rgba(107, 114, 128, 0.8)'
            ],
            borderWidth: 2
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: true,
                position: 'bottom'
            }
        }
    }
});

// Revenue Detail Chart
const ctx = document.getElementById('revenueDetailChart').getContext('2d');
const chart = new Chart(ctx, {
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
                tension: 0.4
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
                tension: 0.4
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
                    usePointStyle: true,
                    padding: 15
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
                        return (value / 1000000).toFixed(1) + 'M đ';
                    }
                }
            }
        }
    }
});

function sendReminders() {
    if (confirm('Confirm send reminder emails to all customers with overdue invoices?')) {
        alert('Feature is under development!');
        // TODO: Implement send reminder emails
    }
}

function exportOverdueReport() {
    alert('Report export feature is under development!');
    // TODO: Implement export functionality
}

function viewCustomerDetail(customerID) {
    // TODO: Implement customer detail view
    alert('View customer details #' + customerID);
// window.location.href = '${pageContext.request.contextPath}/accountant/customer?id=' + customerID;
}

// Export to Excel function
function exportToExcel() {
    alert('Excel export feature is under development!');
    // TODO: Implement Excel export functionality
}