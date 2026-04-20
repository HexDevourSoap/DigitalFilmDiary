document.addEventListener("DOMContentLoaded", function () {
    const body = document.body;
    const toggleButton = document.getElementById("theme-toggle");

    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
        body.classList.add("dark-mode");
    }

    if (toggleButton) {
        setThemeButtonLabel(toggleButton, body.classList.contains("dark-mode"));

        toggleButton.addEventListener("click", function () {
            body.classList.toggle("dark-mode");

            const isDark = body.classList.contains("dark-mode");
            localStorage.setItem("theme", isDark ? "dark" : "light");
            setThemeButtonLabel(toggleButton, isDark);
        });
    }

    function setThemeButtonLabel(button, isDark) {
        if (typeof i18next !== "undefined" && i18next.isInitialized && typeof window.refreshI18nThemeButton === "function") {
            window.refreshI18nThemeButton();
        } else {
            updateButtonTextPlain(button, isDark);
        }
    }

    document.addEventListener("i18n:ready", function () {
        if (toggleButton && typeof window.refreshI18nThemeButton === "function") {
            window.refreshI18nThemeButton();
        }
    });

    function updateButtonTextPlain(button, isDark) {
        button.setAttribute("aria-pressed", isDark ? "true" : "false");
        button.setAttribute("aria-label", isDark ? "Light mode" : "Dark mode");
        button.setAttribute("title", isDark ? "Light mode" : "Dark mode");
    }
});