const attachmentInput = document.getElementById('attachment');
const fileError = document.getElementById('fileError');
const previewImage = document.getElementById('previewImage');
const form = document.querySelector('form');

attachmentInput.addEventListener('change', function() {
    fileError.textContent = '';
    previewImage.style.display = 'none';
    const file = this.files[0];
    if (!file) return;

    // Kiểm tra kích thước (5MB)
    const maxSize = 5 * 1024 * 1024;
    if (file.size > maxSize) {
        fileError.textContent = "File quá lớn, tối đa 5MB!";
        this.value = ''; // reset input
        return;
    }

    // Kiểm tra loại file
    const allowedTypes = ['image/png', 'image/jpeg', 'application/pdf'];
    if (!allowedTypes.includes(file.type)) {
        fileError.textContent = "Chỉ cho phép file PNG, JPG hoặc PDF!";
        this.value = ''; // reset input
        return;
    }

    // Nếu là hình ảnh thì preview
    if (file.type.startsWith('image/')) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImage.src = e.target.result;
            previewImage.style.display = 'block';
        }
        reader.readAsDataURL(file);
    }
});

// Kiểm tra trước submit
form.addEventListener('submit', function(e) {
    if (attachmentInput.files.length > 0 && fileError.textContent !== '') {
        e.preventDefault();
    }
});

    document.getElementById("addAttachmentBtn").addEventListener("click", function() {
    const container = document.getElementById("attachmentContainer");
    const newInput = document.createElement("input");
    newInput.type = "file";
    newInput.name = "attachments"; // cùng name để server nhận mảng files
    newInput.accept = "image/*";
    newInput.classList.add("attachmentInput");
    container.appendChild(newInput);
});

    const attachmentContainer = document.getElementById("attachmentContainer");
    const previewContainer = document.getElementById("previewContainer");

    attachmentContainer.addEventListener("change", function(e) {
    if (e.target && e.target.classList.contains("attachmentInput")) {
    const file = e.target.files[0];
    if (!file) return;

    // Giới hạn dung lượng file
    if (file.size > 5 * 1024 * 1024) { // 5MB
    document.getElementById("fileError").innerText = "File quá lớn, tối đa 5MB";
    e.target.value = "";
    return;
} else {
    document.getElementById("fileError").innerText = "";
}

    // Hiển thị preview
    const reader = new FileReader();
    reader.onload = function(event) {
    const img = document.createElement("img");
    img.src = event.target.result;
    img.style.maxWidth = "200px";
    img.style.marginRight = "10px";
    previewContainer.appendChild(img);
};
    reader.readAsDataURL(file);
}
});

