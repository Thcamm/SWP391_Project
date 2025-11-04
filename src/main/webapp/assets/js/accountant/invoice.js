function filterByStatus(status) {
    window.location.href = '${pageContext.request.contextPath}/accountant/invoice?status=' + status;
}
function deleteInvoice(invoiceId) {
    if (confirm('This invoice has errors. Do you want to delete it?')) {
        window.location.href = '${pageContext.request.contextPath}/accountant/invoice?action=delete&id=' + invoiceId;
    }
}

function showQRCode() {
    const qrImage = document.getElementById('qrCodeImage');
    const qrLoading = document.getElementById('qrCodeLoading');
    const modal = new bootstrap.Modal(document.getElementById('qrCodeModal'));

    // Show loading spinner, hide image
    qrLoading.classList.remove('d-none');
    qrLoading.classList.add('d-block');
    qrImage.classList.add('d-none');
    qrImage.src = '';

    modal.show();

    // Call new servlet
    fetch('${pageContext.request.contextPath}/accountant/generateQR?invoiceId=${invoice.invoiceID}')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.success && data.qrImageUrl) {
                qrImage.src = data.qrImageUrl;

                // Hide loading, show image
                qrLoading.classList.add('d-none');
                qrLoading.classList.remove('d-block');
                qrImage.classList.remove('d-none');

                // Update amount display if needed
                console.log('QR Code generated for amount:', data.amount, 'VND');
                console.log('Invoice Number:', data.invoiceNumber);
            } else {
                throw new Error(data.message || 'Failed to generate QR code');
            }
        })
        .catch(error => {
            console.error('Error fetching QR code:', error);
            qrLoading.classList.remove('spinner-border', 'text-primary');
            qrLoading.innerHTML = '<span class="text-danger"><i class="bi bi-exclamation-triangle"></i> Failed to load QR code. ' + error.message + '</span>';
            qrLoading.classList.add('d-block');
            qrImage.classList.add('d-none');
        });
}