// ===== SUPPORT REQUEST PAGE - ENHANCED MULTI-UPLOAD =====
// Wrap trong IIFE để tránh conflict với JS khác
(function() {
    'use strict';

    // ==============================
    // CONSTANTS & CONFIGURATION
    // ==============================
    const MAX_FILES = 4;
    const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    const ALLOWED_TYPES = ['image/png', 'image/jpeg', 'image/jpg', 'application/pdf'];
    const ALLOWED_MIME = {
        'image/png': 'PNG',
        'image/jpeg': 'JPG',
        'image/jpg': 'JPG',
        'application/pdf': 'PDF'
    };

    // ==============================
    // STATE MANAGEMENT
    // ==============================
    let selectedFiles = [];

    // ==============================
    // DOM ELEMENTS
    // ==============================
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('attachment');
    const previewContainer = document.getElementById('imagePreviewContainer');
    const fileError = document.getElementById('fileError');
    const fileCounter = document.getElementById('fileCounter');
    const form = document.getElementById('supportForm');
    const textarea = document.querySelector('textarea[name="description"]');

    // ==============================
    // PHẦN 1: MULTIPLE FILE UPLOAD HANDLERS
    // ==============================

    if (uploadArea && fileInput && previewContainer) {

        // Click to upload - Fixed
        uploadArea.addEventListener('click', function(e) {
            // Ngăn click vào nút remove hoặc preview item
            if (e.target.closest('.remove-image') || e.target.closest('.image-preview-item')) {
                return;
            }
            fileInput.click();
        });

        // File selection via input
        fileInput.addEventListener('change', function(e) {
            handleFiles(e.target.files);
        });

        // Drag and drop events
        uploadArea.addEventListener('dragover', function(e) {
            e.preventDefault();
            e.stopPropagation();
            uploadArea.classList.add('dragover');
        });

        uploadArea.addEventListener('dragleave', function(e) {
            e.preventDefault();
            e.stopPropagation();
            uploadArea.classList.remove('dragover');
        });

        uploadArea.addEventListener('drop', function(e) {
            e.preventDefault();
            e.stopPropagation();
            uploadArea.classList.remove('dragover');
            handleFiles(e.dataTransfer.files);
        });

        // Handle files function
        function handleFiles(files) {
            clearError();

            if (!files || files.length === 0) return;

            // Convert FileList to Array
            const filesArray = Array.from(files);

            // Check total number of files
            const remainingSlots = MAX_FILES - selectedFiles.length;
            if (filesArray.length > remainingSlots) {
                showError(`Bạn chỉ có thể upload thêm ${remainingSlots} file nữa! (Tối đa ${MAX_FILES} file)`);
                return;
            }

            // Validate and add files
            let hasError = false;
            for (let file of filesArray) {
                // Check file size
                if (file.size > MAX_FILE_SIZE) {
                    showError(` File "${file.name}" quá lớn. Tối đa 5MB mỗi file.`);
                    hasError = true;
                    continue;
                }

                // Check file type
                if (!ALLOWED_TYPES.includes(file.type)) {
                    showError(` File "${file.name}" không được hỗ trợ. Chỉ chấp nhận PNG, JPG, PDF.`);
                    hasError = true;
                    continue;
                }

                // Check for duplicates
                const isDuplicate = selectedFiles.some(f =>
                    f.name === file.name && f.size === file.size
                );
                if (isDuplicate) {
                    showError(`⚠️ File "${file.name}" đã được chọn rồi.`);
                    hasError = true;
                    continue;
                }

                // Add to selected files
                selectedFiles.push(file);
            }

            // Update preview and counter
            if (selectedFiles.length > 0) {
                updatePreview();
                updateCounter();
            }

            // Reset input to allow selecting the same file again
            fileInput.value = '';
        }

        // Update preview display
        function updatePreview() {
            previewContainer.innerHTML = '';

            selectedFiles.forEach((file, index) => {
                const previewItem = document.createElement('div');
                previewItem.className = 'image-preview-item';
                previewItem.setAttribute('data-index', index);

                // Create preview based on file type
                if (file.type.startsWith('image/')) {
                    const img = document.createElement('img');
                    const objectUrl = URL.createObjectURL(file);
                    img.src = objectUrl;
                    img.alt = file.name;

                    // Clean up object URL after image loads
                    img.onload = function() {
                        URL.revokeObjectURL(objectUrl);
                    };

                    previewItem.appendChild(img);
                } else if (file.type === 'application/pdf') {
                    const pdfIcon = document.createElement('div');
                    pdfIcon.className = 'pdf-preview';
                    pdfIcon.innerHTML = `
                        <i class="fas fa-file-pdf"></i>
                        <div class="pdf-name">${truncateFileName(file.name, 15)}</div>
                    `;
                    previewItem.appendChild(pdfIcon);
                }

                // Add file info overlay
                const fileInfo = document.createElement('div');
                fileInfo.className = 'file-info-overlay';
                fileInfo.innerHTML = `
                    <small>${formatFileSize(file.size)}</small>
                `;
                previewItem.appendChild(fileInfo);

                // Remove button
                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.className = 'remove-image';
                removeBtn.innerHTML = '<i class="fas fa-times"></i>';
                removeBtn.title = 'Remove file';
                removeBtn.onclick = function(e) {
                    e.stopPropagation();
                    removeFile(index);
                };

                previewItem.appendChild(removeBtn);
                previewContainer.appendChild(previewItem);
            });
        }

        // Remove file
        function removeFile(index) {
            selectedFiles.splice(index, 1);
            updatePreview();
            updateCounter();
            clearError();
        }

        // Update counter
        function updateCounter() {
            if (selectedFiles.length > 0) {
                const counterClass = selectedFiles.length >= MAX_FILES ? 'file-counter file-counter-full' : 'file-counter';
                fileCounter.innerHTML = `<span class="${counterClass}">
                    <i class="fas fa-paperclip"></i> ${selectedFiles.length} / ${MAX_FILES} file đã chọn
                </span>`;
            } else {
                fileCounter.innerHTML = '';
            }
        }

        // Error handling
        function showError(message) {
            if (fileError) {
                fileError.textContent = message;
                fileError.style.display = 'block';
            }
        }

        function clearError() {
            if (fileError) {
                fileError.textContent = '';
                fileError.style.display = 'none';
            }
        }

        // Helper functions
        function formatFileSize(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
        }

        function truncateFileName(name, maxLength) {
            if (name.length <= maxLength) return name;
            const ext = name.substring(name.lastIndexOf('.'));
            const nameWithoutExt = name.substring(0, name.lastIndexOf('.'));
            const truncated = nameWithoutExt.substring(0, maxLength - ext.length - 3) + '...';
            return truncated + ext;
        }
    }

    // ==============================
    // PHẦN 2: FORM VALIDATION & SUBMISSION
    // ==============================

    if (form) {
        form.addEventListener('submit', function(e) {
            // Validate description
            const description = form.querySelector('textarea[name="description"]');
            if (description && description.value.trim().length < 10) {
                e.preventDefault();
                alert(' Vui lòng mô tả chi tiết hơn (tối thiểu 10 ký tự)!');
                description.focus();
                return false;
            }

            // Validate category
            const category = form.querySelector('select[name="categoryId"]');
            if (category && !category.value) {
                e.preventDefault();
                alert(' Vui lòng chọn danh mục!');
                category.focus();
                return false;
            }

            // Check if there are file errors
            if (fileError && fileError.textContent.includes('❌')) {
                e.preventDefault();
                alert('⚠️ Vui lòng xử lý lỗi file trước khi gửi!');
                return false;
            }

            // Disable original file input
            if (fileInput) {
                fileInput.disabled = true;
            }

            // Create hidden inputs for selected files
            selectedFiles.forEach((file, index) => {
                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);

                const hiddenInput = document.createElement('input');
                hiddenInput.type = 'file';
                hiddenInput.name = 'attachments';
                hiddenInput.style.display = 'none';
                hiddenInput.files = dataTransfer.files;

                form.appendChild(hiddenInput);
            });

            // Show loading state
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang gửi yêu cầu...';
            }

            return true;
        });
    }

    // ==============================
    // PHẦN 3: TEXTAREA AUTO-RESIZE
    // ==============================

    if (textarea) {
        // Set initial height
        textarea.style.minHeight = '120px';

        // Auto-resize on input
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = Math.max(120, this.scrollHeight) + 'px';
        });

        // Character counter (optional)
        const maxChars = 500;
        const charCounter = document.createElement('small');
        charCounter.className = 'char-counter text-muted';
        charCounter.style.float = 'right';
        charCounter.style.marginTop = '5px';

        textarea.addEventListener('input', function() {
            const remaining = maxChars - this.value.length;
            charCounter.textContent = `${this.value.length} / ${maxChars} ký tự`;

            if (remaining < 100) {
                charCounter.style.color = '#dc3545';
            } else {
                charCounter.style.color = '#6c757d';
            }
        });

        if (textarea.parentElement) {
            textarea.parentElement.appendChild(charCounter);
        }
    }

    // ==============================
    // PHẦN 4: ADDITIONAL ENHANCEMENTS
    // ==============================

    // Add smooth scroll to error messages
    const messageAlert = document.querySelector('.alert-custom');
    if (messageAlert) {
        messageAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });

        // Auto-dismiss success messages after 5 seconds
        if (messageAlert.classList.contains('alert-success')) {
            setTimeout(() => {
                messageAlert.style.transition = 'opacity 0.5s ease';
                messageAlert.style.opacity = '0';
                setTimeout(() => messageAlert.remove(), 500);
            }, 5000);
        }
    }

    // Add loading overlay style
    const style = document.createElement('style');
    style.textContent = `
        .pdf-preview {
            width: 100%;
            height: 100%;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #fff5f5 0%, #ffe0e0 100%);
            color: #d32f2f;
            font-size: 48px;
        }
        .pdf-name {
            font-size: 10px;
            margin-top: 8px;
            text-align: center;
            padding: 0 5px;
            word-break: break-word;
        }
        .file-info-overlay {
            position: absolute;
            bottom: 0;
            left: 0;
            right: 0;
            background: rgba(0,0,0,0.7);
            color: white;
            padding: 4px;
            text-align: center;
            font-size: 10px;
        }
        .file-counter-full {
            background: #dc3545 !important;
        }
        .char-counter {
            display: block;
            text-align: right;
            font-size: 12px;
        }
    `;
    document.head.appendChild(style);

    // Console log for debugging
    console.log('✅ Support Request Form - Enhanced Multi-Upload initialized');
    console.log(`📋 Configuration: Max ${MAX_FILES} files, ${MAX_FILE_SIZE / 1024 / 1024}MB each`);

})();