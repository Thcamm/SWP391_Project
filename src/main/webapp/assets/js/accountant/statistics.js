// Weekly Revenue Chart
const weeklyCtx = document.getElementById('weeklyRevenueChart').getContext('2d');
new Chart(weeklyCtx, {
    type: 'bar',
    data: {
        labels: [
            <c:forEach var="week" items="${revenueByWeek}" varStatus="status">
                '${week.label}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        datasets: [{
            label: 'Revenue',
            data: [
                <c:forEach var="week" items="${revenueByWeek}" varStatus="status">
                    ${week.totalPaid}${!status.last ? ',' : ''}
                </c:forEach>
            ],
            backgroundColor: 'rgba(102, 126, 234, 0.8)',
            borderColor: 'rgba(102, 126, 234, 1)',
            borderWidth: 2,
            borderRadius: 8
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: { display: false }
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

// New Customers Chart
const customersCtx = document.getElementById('newCustomersChart').getContext('2d');
new Chart(customersCtx, {
    type: 'doughnut',
    data: {
        labels: [
            <c:forEach var="nc" items="${newCustomers}" varStatus="status">
                'M${nc.month}/${nc.year}'${!status.last ? ',' : ''}
            </c:forEach>
        ],
        datasets: [{
            data: [
                <c:forEach var="nc" items="${newCustomers}" varStatus="status">
                    ${nc.count}${!status.last ? ',' : ''}
                </c:forEach>
            ],
            backgroundColor: [
                'rgba(79, 172, 254, 0.8)',
                'rgba(102, 126, 234, 0.8)',
                'rgba(240, 147, 251, 0.8)',
                'rgba(67, 233, 123, 0.8)',
                'rgba(245, 87, 108, 0.8)',
                'rgba(56, 249, 215, 0.8)'
            ],
            borderWidth: 0
        }]
    },
    options: {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                position: 'bottom'
            }
        }
    }
});

// Year Comparison Chart
const yearCtx = document.getElementById('YearComparisonChart').getContext('2d');
const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

new Chart(yearCtx, {
    type: 'line',
    data: {
        labels: months,
        datasets: [
            {
                label: 'This Year (' + new Date().getFullYear() + ')',
                data: thisYear, // Sử dụng trực tiếp mảng đã tạo
                borderColor: 'rgba(75, 192, 192, 1)',
                fill: false,
                tension: 0.1
            },
            {
                label: 'Last Year (' + (new Date().getFullYear() - 1) + ')',
                data: lastYear, // Sử dụng trực tiếp mảng đã tạo
                borderColor: 'rgba(255, 99, 132, 1)',
                fill: false,
                tension: 0.1
            }
        ]
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
                        return (value / 1000000).toFixed(1) + 'M';
                    }
                }
            }
        }
    }
});