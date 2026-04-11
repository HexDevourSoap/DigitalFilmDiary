(function () {
    function pathLang() {
        var m = location.pathname.match(/^\/(lv|en)(\/|$)/);
        return m ? m[1] : null;
    }

    function applyTranslations() {
        document.querySelectorAll("[data-i18n]").forEach(function (el) {
            var key = el.getAttribute("data-i18n");
            if (key) {
                el.textContent = i18next.t(key);
            }
        });
        document.querySelectorAll("[data-i18n-placeholder]").forEach(function (el) {
            var key = el.getAttribute("data-i18n-placeholder");
            if (key) {
                el.setAttribute("placeholder", i18next.t(key));
            }
        });
        document.querySelectorAll("[data-i18n-title]").forEach(function (el) {
            var key = el.getAttribute("data-i18n-title");
            if (key) {
                el.setAttribute("title", i18next.t(key));
            }
        });
        document.querySelectorAll("[data-i18n-alt]").forEach(function (el) {
            var key = el.getAttribute("data-i18n-alt");
            if (key) {
                el.setAttribute("alt", i18next.t(key));
            }
        });
        refreshThemeButton();
        document.dispatchEvent(new CustomEvent("i18n:ready"));
    }

    function refreshThemeButton() {
        var btn = document.getElementById("theme-toggle");
        if (!btn) {
            return;
        }
        var isDark = document.body.classList.contains("dark-mode");
        btn.textContent = isDark ? i18next.t("theme.light") : i18next.t("theme.dark");
    }

    window.refreshI18nThemeButton = refreshThemeButton;

    document.addEventListener("DOMContentLoaded", function () {
        var fromPath = pathLang();
        var serverLang = document.documentElement.getAttribute("data-app-lang");
        // URL prefix wins; else server-rendered lang (e.g. default lv); else last choice in localStorage
        var lng = fromPath || serverLang || localStorage.getItem("i18nextLng") || "lv";

        i18next.use(i18nextHttpBackend).init(
            {
                lng: lng,
                fallbackLng: "lv",
                supportedLngs: ["lv", "en"],
                ns: ["translation"],
                defaultNS: "translation",
                backend: {
                    loadPath: "/locales/{{lng}}/{{ns}}.json",
                },
            },
            function (err) {
                if (err) {
                    console.warn("i18n init", err);
                }
                localStorage.setItem("i18nextLng", i18next.language);
                applyTranslations();
            }
        );

        i18next.on("languageChanged", function () {
            localStorage.setItem("i18nextLng", i18next.language);
            applyTranslations();
        });
    });
})();
