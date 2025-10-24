document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.getElementById("sidebar");
    const toggleBtn = document.getElementById("toggleBtn");


    toggleBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        sidebar.classList.toggle("expanded");
    });


    document.addEventListener("click", (e) => {
        if (!sidebar.contains(e.target)) {
            sidebar.classList.remove("expanded");
        }
    });
});
