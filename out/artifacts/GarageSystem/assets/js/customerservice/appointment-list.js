document.querySelector('form').addEventListener('submit', function(e) {
    const fromDate = document.querySelector('input[name="fromDate"]').value;
    const toDate = document.querySelector('input[name="toDate"]').value;

    if (fromDate && toDate && new Date(fromDate) > new Date(toDate)) {
        e.preventDefault();
        alert("The start date cannot be greater than the end date!");
    }
});
document.addEventListener("DOMContentLoaded", function() {
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'))
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl, {
            trigger: 'focus', // click sẽ hiển thị, click ra ngoài sẽ ẩn
            placement: 'right'
        })
    })
});