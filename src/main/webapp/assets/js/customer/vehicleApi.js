
document.addEventListener("DOMContentLoaded", function () {
    const VEHICLE_JSON_URL = `${contextPath}/assets/car-models.json`;

    const brandSelect = document.getElementById('brand');
    const modelSelect = document.getElementById('model');
    const yearSelect = document.getElementById('year');
    const loadingText = document.getElementById('loadingText');
    const modelCount = document.getElementById('modelCount');
    const popularBrandsContainer = document.getElementById('popularBrands');

    if (!brandSelect || !modelSelect || !yearSelect) {
        return;
    }

    // Danh sách hãng xe phổ biến ở Việt Nam
    const POPULAR_BRANDS = [
        'Toyota', 'Honda', 'Hyundai', 'Mazda', 'Ford',
        'Kia', 'Mitsubishi', 'Nissan', 'Suzuki', 'Chevrolet',
        'Mercedes-Benz', 'BMW', 'Audi', 'VinFast',
    ];

    const currentBrand = brandSelect.dataset.currentBrand;
    const currentModel = modelSelect.dataset.currentModel;
    const currentYear = yearSelect.dataset.currentYear;

    let vehicleData = [];

    /**
     * Khởi tạo Select2 cho dropdown có search
     */
    function initSelect2() {
        // Brand Select2
        $(brandSelect).select2({
            theme: 'bootstrap-5',
            placeholder: 'Type to search brand...',
            allowClear: true,
            width: '100%',
            matcher: customMatcher
        });

        // Model Select2
        $(modelSelect).select2({
            theme: 'bootstrap-5',
            placeholder: 'Type to search model...',
            allowClear: true,
            width: '100%',
            matcher: customMatcher
        });

        // Event listener cho brand change
        $(brandSelect).on('select2:select', function (e) {
            const selectedBrand = e.params.data.id;
            loadModels(selectedBrand);
            updatePopularBrandsBadges(selectedBrand);
        });

        $(brandSelect).on('select2:clear', function () {
            modelSelect.innerHTML = '<option value="">Select brand first</option>';
            modelSelect.disabled = true;
            $(modelSelect).trigger('change');
            updatePopularBrandsBadges(null);
        });
    }

    /**
     * Custom matcher cho Select2 - tìm kiếm không phân biệt hoa thường
     */
    function customMatcher(params, data) {
        if ($.trim(params.term) === '') {
            return data;
        }

        if (typeof data.text === 'undefined') {
            return null;
        }

        const term = params.term.toLowerCase();
        const text = data.text.toLowerCase();

        if (text.indexOf(term) > -1) {
            return data;
        }

        return null;
    }

    /**
     * Tạo badges cho các hãng xe phổ biến
     */
    function createPopularBrandsBadges() {
        popularBrandsContainer.innerHTML = '';

        POPULAR_BRANDS.forEach(brand => {
            const badge = document.createElement('span');
            badge.className = 'badge bg-light text-dark brand-badge';
            badge.textContent = brand;
            badge.dataset.brand = brand;

            badge.addEventListener('click', () => {
                // Set value và trigger Select2
                $(brandSelect).val(brand).trigger('change');
                loadModels(brand);
                updatePopularBrandsBadges(brand);
            });

            popularBrandsContainer.appendChild(badge);
        });
    }

    /**
     * Update trạng thái selected cho badges
     */
    function updatePopularBrandsBadges(selectedBrand) {
        const badges = popularBrandsContainer.querySelectorAll('.brand-badge');
        badges.forEach(badge => {
            if (badge.dataset.brand === selectedBrand) {
                badge.classList.add('selected');
            } else {
                badge.classList.remove('selected');
            }
        });
    }

    /**
     * Load danh sách brands
     */
    function loadBrands() {
        loadingText.style.display = 'block';
        loadingText.textContent = 'Loading vehicle data...';

        fetch(VEHICLE_JSON_URL)
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                vehicleData = data;

                // Lấy danh sách brand và sort
                const brandNames = data.map(item => item.brand).sort();

                // Đổ vào select
                brandNames.forEach(brandName => {
                    const opt = document.createElement('option');
                    opt.value = brandName;
                    opt.textContent = brandName;

                    if (brandName === currentBrand) {
                        opt.selected = true;
                    }
                    brandSelect.appendChild(opt);
                });

                // Khởi tạo Select2 sau khi có data
                initSelect2();

                // Tạo popular brands badges
                createPopularBrandsBadges();

                // Nếu là trang Edit, tự động load model
                if (currentBrand) {
                    $(brandSelect).val(currentBrand).trigger('change');
                    loadModels(currentBrand);
                    updatePopularBrandsBadges(currentBrand);
                }

                loadingText.style.display = 'none';
            })
            .catch(err => {
                console.error('Error loading vehicle JSON:', err);
                loadingText.textContent = `Failed to load data: ${err.message}`;
                loadingText.style.color = 'red';
            });
    }

    /**
     * Load danh sách models
     */
    function loadModels(selectedBrand) {
        modelSelect.innerHTML = '<option value="">Select model</option>';
        modelSelect.disabled = false;
        modelCount.style.display = 'none';

        if (!selectedBrand) {
            modelSelect.disabled = true;
            return;
        }

        const brandData = vehicleData.find(item => item.brand === selectedBrand);

        if (brandData && Array.isArray(brandData.models)) {
            const models = brandData.models;

            models.forEach(modelName => {
                const opt = document.createElement('option');
                opt.value = modelName;
                opt.textContent = modelName;
                modelSelect.appendChild(opt);
            });

            if (currentModel) {
                $(modelSelect).val(currentModel).trigger('change');
            }

            modelCount.textContent = `Found ${models.length} models`;
            modelCount.style.display = 'block';
        }
    }


    /**
     * Load danh sách years
     */
    function loadYears() {
        const latestYear = new Date().getFullYear() + 1;
        for (let y = latestYear; y >= 1990; y--) {
            const opt = document.createElement('option');
            opt.value = y;
            opt.textContent = y;

            if (String(y) === currentYear) {
                opt.selected = true;
            }
            yearSelect.appendChild(opt);
        }
    }

    // Khởi chạy
    loadBrands();
    loadYears();
});