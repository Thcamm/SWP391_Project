const menuIcon = document.querySelector('.fa-bars');
const sidebarMenu = document.getElementById('sidebarMenu');
const menuOverlay = document.getElementById('menuOverlay');
const closeMenuBtn = document.getElementById('closeMenu');

// Mở menu
menuIcon.addEventListener('click', function () {
    sidebarMenu.classList.add('active');
    menuOverlay.classList.add('active');
});

// Đóng menu khi click nút X
closeMenuBtn.addEventListener('click', function () {
    sidebarMenu.classList.remove('active');
    menuOverlay.classList.remove('active');
});

// Đóng menu khi click overlay
menuOverlay.addEventListener('click', function () {
    sidebarMenu.classList.remove('active');
    menuOverlay.classList.remove('active');
});