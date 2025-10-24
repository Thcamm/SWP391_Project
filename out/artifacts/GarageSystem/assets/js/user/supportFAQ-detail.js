const helpToggle = document.getElementById("helpToggle");
const helpOptions = document.getElementById("helpOptions");

helpToggle.addEventListener("click", () => {
    helpOptions.classList.toggle("open");
});

document.addEventListener("click", (e) => {
    if (!helpOptions.contains(e.target) && !helpToggle.contains(e.target)) {
        helpOptions.classList.remove("open");
    }
});