// ===== SUPPORT REQUEST PAGE - SCOPED JS =====
// Wrap trong IIFE để tránh conflict với JS khác
(function() {
    'use strict';

    // ==============================
    // PHẦN 1: XỬ LÝ FILE UPLOAD ĐƠN (attachment chính)
    // ==============================
    const attachmentInput = document.getElementById('attachment');
    const fileError = document.getElementById('fileError');
    const previewImage = document.getElementById('previewImage');
    const form = document.querySelector('main.support-request-main form');

    if (attachmentInput && fileError && previewImage) {
        attachmentInput.addEventListener('change', function() {
            // Reset error và preview
            fileError.textContent = '';
            previewImage.style.display = 'none';

            const file = this.files[0];
            if (!file) return;

            // Kiểm tra kích thước (5MB)
            const maxSize = 5 * 1024 * 1024;
            if (file.size > maxSize) {
                fileError.textContent = "❌ File quá lớn, tối đa 5MB!";
                this.value = ''; // reset input
                return;
            }

            // Kiểm tra loại file
            const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif', 'application/pdf'];
            if (!allowedTypes.includes(file.type)) {
                fileError.textContent = "❌ Chỉ cho phép file PNG, JPG, GIF hoặc PDF!";
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
                reader.onerror = function() {
                    fileError.textContent = "❌ Không thể đọc file!";
                };
                reader.readAsDataURL(file);
            } else if (file.type === 'application/pdf') {
                // Hiển thị thông báo cho PDF
                fileError.textContent = "✅ File PDF đã được chọn: " + file.name;
                fileError.style.color = '#28a745';
            }
        });
    }

    // ==============================
    // PHẦN 2: VALIDATE TRƯỚC KHI SUBMIT
    // ==============================
    if (form) {
        form.addEventListener('submit', function(e) {
            // Kiểm tra nếu có lỗi file
            if (attachmentInput && attachmentInput.files.length > 0 &&
                fileError && fileError.textContent.includes('❌')) {
                e.preventDefault();
                alert('Vui lòng chọn file hợp lệ trước khi gửi!');
                return false;
            }

            // Validate description
            const description = form.querySelector('textarea[name="description"]');
            if (description && description.value.trim().length < 10) {
                e.preventDefault();
                alert('Vui lòng mô tả chi tiết hơn (tối thiểu 10 ký tự)!');
                description.focus();
                return false;
            }

            // Validate category
            const category = form.querySelector('select[name="categoryId"]');
            if (category && !category.value) {
                e.preventDefault();
                alert('Vui lòng chọn danh mục!');
                category.focus();
                return false;
            }

            // Hiển thị loading khi submit
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang gửi...';
            }
        });
    }

    // ==============================
    // PHẦN 3: XỬ LÝ MULTI-UPLOAD (Nếu có button "Add more attachment")
    // ==============================
    const addAttachmentBtn = document.getElementById('addAttachmentBtn');
    const attachmentContainer = document.getElementById('attachmentContainer');
    const previewContainer = document.getElementById('previewContainer');

    if (addAttachmentBtn && attachmentContainer && previewContainer) {
        let fileCount = 0;
        const maxFiles = 5; // Giới hạn tối đa 5 files

        addAttachmentBtn.addEventListener('click', function() {
            if (fileCount >= maxFiles) {
                alert('Chỉ được upload tối đa ' + maxFiles + ' file!');
                return;
            }

            const inputWrapper = document.createElement('div');
            inputWrapper.classList.add('attachment-input-wrapper');
            inputWrapper.style.marginBottom = '10px';

            const newInput = document.createElement('input');
            newInput.type = 'file';
            newInput.name = 'attachments'; // Server sẽ nhận array
            newInput.accept = 'image/*,application/pdf';
            newInput.classList.add('attachmentInput');
            newInput.setAttribute('data-index', fileCount);

            const removeBtn = document.createElement('button');
            removeBtn.type = 'button';
            removeBtn.innerHTML = '✖';
            removeBtn.classList.add('remove-attachment-btn');
            removeBtn.style.marginLeft = '10px';
            removeBtn.style.color = 'red';
            removeBtn.style.cursor = 'pointer';

            inputWrapper.appendChild(newInput);
            inputWrapper.appendChild(removeBtn);
            attachmentContainer.appendChild(inputWrapper);

            fileCount++;

            // Xử lý remove button
            removeBtn.addEventListener('click', function() {
                inputWrapper.remove();
                fileCount--;
                // Xóa preview tương ứng nếu có
                const relatedPreview = previewContainer.querySelector(`[data-index="${newInput.getAttribute('data-index')}"]`);
                if (relatedPreview) {
                    relatedPreview.remove();
                }
            });
        });

        // Xử lý preview cho multi-upload
        attachmentContainer.addEventListener('change', function(e) {
            if (e.target && e.target.classList.contains('attachmentInput')) {
                const file = e.target.files[0];
                if (!file) return;

                const index = e.target.getAttribute('data-index');

                // Giới hạn dung lượng
                if (file.size > 5 * 1024 * 1024) {
                    alert('File quá lớn, tối đa 5MB!');
                    e.target.value = '';
                    return;
                }

                // Kiểm tra loại file
                const allowedTypes = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif', 'application/pdf'];
                if (!allowedTypes.includes(file.type)) {
                    alert('Chỉ cho phép file ảnh hoặc PDF!');
                    e.target.value = '';
                    return;
                }

                // Hiển thị preview nếu là ảnh
                if (file.type.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function(event) {
                        // Xóa preview cũ nếu có
                        const oldPreview = previewContainer.querySelector(`[data-index="${index}"]`);
                        if (oldPreview) {
                            oldPreview.remove();
                        }

                        const previewWrapper = document.createElement('div');
                        previewWrapper.classList.add('preview-item');
                        previewWrapper.setAttribute('data-index', index);
                        previewWrapper.style.display = 'inline-block';
                        previewWrapper.style.marginRight = '10px';
                        previewWrapper.style.marginBottom = '10px';
                        previewWrapper.style.position = 'relative';

                        const img = document.createElement('img');
                        img.src = event.target.result;
                        img.style.maxWidth = '150px';
                        img.style.maxHeight = '150px';
                        img.style.border = '2px solid #ddd';
                        img.style.borderRadius = '6px';
                        img.style.objectFit = 'cover';

                        previewWrapper.appendChild(img);
                        previewContainer.appendChild(previewWrapper);
                    };
                    reader.readAsDataURL(file);
                } else if (file.type === 'application/pdf') {
                    // Hiển thị icon PDF
                    const oldPreview = previewContainer.querySelector(`[data-index="${index}"]`);
                    if (oldPreview) {
                        oldPreview.remove();
                    }

                    const pdfIndicator = document.createElement('div');
                    pdfIndicator.classList.add('preview-item');
                    pdfIndicator.setAttribute('data-index', index);
                    pdfIndicator.style.display = 'inline-block';
                    pdfIndicator.style.marginRight = '10px';
                    pdfIndicator.innerHTML = `
                        <i class="fas fa-file-pdf" style="font-size: 48px; color: #dc3545;"></i>
                        <p style="font-size: 0.8rem; margin: 5px 0;">${file.name}</p>
                    `;
                    previewContainer.appendChild(pdfIndicator);
                }
            }
        });
    }

    // ==============================
    // PHẦN 4: AUTO-RESIZE TEXTAREA
    // ==============================
    const textarea = document.querySelector('main.support-request-main textarea[name="description"]');
    if (textarea) {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';
        });
    }

})();