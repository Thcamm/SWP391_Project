let currentPage = 1;
const itemsPerPage = 5;

function render() {
    if (typeof services === 'undefined' || !services || services.length === 0) {
        document.getElementById('serviceBody').innerHTML = '<tr><td colspan="5" style="text-align:center;">No service history found.</td></tr>';
        return;
    }

    const totalPages = Math.ceil(services.length / itemsPerPage);
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageData = services.slice(start, end);

    document.getElementById('serviceBody').innerHTML = pageData.map(s =>
        `<tr>
            <td>${s.id}</td>
            <td>${s.name}</td>
            <td>${s.date}</td>
            <td><span class="status status-${s.status.toLowerCase()}">${s.status}</span></td>
            <td>${s.price}</td>
        </tr>`
    ).join('');

    document.getElementById('start').textContent = (services.length > 0) ? start + 1 : 0;
    document.getElementById('end').textContent = Math.min(end, services.length);
    document.getElementById('total').textContent = services.length;

    document.getElementById('prevBtn').disabled = currentPage === 1;
    document.getElementById('nextBtn').disabled = currentPage === totalPages;

    const pageNumbers = document.getElementById('pageNumbers');
    pageNumbers.innerHTML = '';
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement('button');
        btn.textContent = i;
        btn.className = i === currentPage ? 'active' : '';
        btn.onclick = () => goToPage(i);
        pageNumbers.appendChild(btn);
    }
}

function goToPage(page) {
    currentPage = page;
    render();
}

function prevPage() {
    if (currentPage > 1) goToPage(currentPage - 1);
}

function nextPage() {
    const totalPages = Math.ceil(services.length / itemsPerPage);
    if (currentPage < totalPages) goToPage(currentPage + 1);
}

document.addEventListener('DOMContentLoaded', render);