
function validatePaymentForm() {
    const amount = parseFloat(document.getElementById('amount').value);
    const maxAmount = parseFloat(document.getElementById('balanceAmount').value);
    if (isNaN(amount) || amount <= 0) {
        alert('Please enter a valid amount!');
        return false;
    }

    if (amount > maxAmount) {
        alert('Payment amount must not exceed remaining balance: ' + maxAmount.toLocaleString('en-US') + ' VND');
        return false;
    }

    return confirm('Confirm payment of ' + amount.toLocaleString('en-US') + ' VND?');
}

function setAmount(value) {
    document.getElementById('amount').value = Math.round(value);
}

// Highlight selected payment method
document.querySelectorAll('input[name="method"]').forEach(radio => {
    radio.addEventListener('change', function() {
        document.querySelectorAll('.form-check-label').forEach(label => {
            label.style.borderColor = '#e5e7eb';
            label.style.backgroundColor = 'white';
        });

        if (this.checked) {
            this.nextElementSibling.style.borderColor = '#667eea';
            this.nextElementSibling.style.backgroundColor = '#f0f4ff';
        }
    });
});

// Trigger initial selection
document.getElementById('methodOnline').dispatchEvent(new Event('change'));