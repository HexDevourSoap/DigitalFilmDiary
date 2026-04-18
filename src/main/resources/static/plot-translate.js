(function () {
    function apiPrefix() {
        if (typeof window.__APP_ROOT__ !== "string") {
            return "";
        }
        return window.__APP_ROOT__.replace(/\/*$/, "");
    }

    /** Match server / URL language (i18next may be source of truth after init). */
    function getUiLang() {
        if (typeof i18next !== "undefined" && i18next.isInitialized && i18next.language) {
            return String(i18next.language).toLowerCase().split("-")[0];
        }
        var a = document.documentElement.getAttribute("data-app-lang");
        return a ? String(a).toLowerCase().split("-")[0] : "lv";
    }

    function translateShortLabels() {
        if (getUiLang() !== "lv") {
            return Promise.resolve();
        }

        var titleEl = document.getElementById("movie-title-text");
        var items = [];
        if (titleEl) {
            var t0 = titleEl.getAttribute("data-en");
            if (t0) {
                items.push({ el: titleEl, en: t0 });
            }
        }

        document.querySelectorAll(".movie-title-translate").forEach(function (el) {
            var t = el.getAttribute("data-en");
            if (t) {
                items.push({ el: el, en: t });
            }
        });

        document.querySelectorAll("li.genre-translate").forEach(function (li) {
            var g = li.getAttribute("data-en");
            if (g) {
                items.push({ el: li, en: g });
            }
        });

        if (items.length === 0) {
            return Promise.resolve();
        }

        var unique = [];
        var seen = Object.create(null);
        items.forEach(function (it) {
            if (!seen[it.en]) {
                seen[it.en] = true;
                unique.push(it.en);
            }
        });

        return fetch(apiPrefix() + "/api/translate/batch", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ texts: unique }),
        })
            .then(function (r) {
                return r.ok ? r.json() : Promise.reject(new Error("batch"));
            })
            .then(function (data) {
                if (!data || !data.texts || !Array.isArray(data.texts)) {
                    return;
                }
                var texts = data.texts;
                var map = {};
                unique.forEach(function (en, i) {
                    if (i < texts.length && texts[i] != null && String(texts[i]).length > 0) {
                        map[en] = String(texts[i]);
                    }
                });
                items.forEach(function (it) {
                    var tr = map[it.en];
                    if (tr != null && tr.length > 0) {
                        it.el.textContent = tr;
                    }
                });
                if (titleEl) {
                    var te = titleEl.getAttribute("data-en");
                    if (te && map[te]) {
                        document.title = map[te];
                    }
                }
                var poster = document.querySelector(".movie-details-poster .poster--large");
                if (poster && titleEl) {
                    var te2 = titleEl.getAttribute("data-en");
                    if (te2 && map[te2]) {
                        poster.setAttribute("alt", map[te2]);
                    }
                }
            })
            .catch(function () {});
    }

    function translatePlot() {
        var en = window.__MOVIE_PLOT_EN__;
        if (getUiLang() !== "lv" || !en) {
            return Promise.resolve();
        }

        var el = document.getElementById("movie-plot-text");
        if (!el) {
            return Promise.resolve();
        }

        el.classList.add("movie-plot--translating");

        return fetch(apiPrefix() + "/api/translate/plot", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ text: en }),
        })
            .then(function (r) {
                return r.ok ? r.json() : Promise.reject(new Error("plot"));
            })
            .then(function (data) {
                if (data && data.text && data.text.length > 0) {
                    el.textContent = data.text;
                }
            })
            .catch(function () {})
            .finally(function () {
                el.classList.remove("movie-plot--translating");
            });
    }

    var translationsRan = false;

    function runTranslationsOnce() {
        if (translationsRan) {
            return;
        }
        translationsRan = true;
        translateShortLabels();
        translatePlot();
    }

    document.addEventListener("DOMContentLoaded", function () {
        document.addEventListener("i18n:ready", runTranslationsOnce);
        setTimeout(function () {
            if (!translationsRan && getUiLang() === "lv") {
                runTranslationsOnce();
            }
        }, 900);
    });
})();
