document.addEventListener("DOMContentLoaded", function () {
    var lang = document.documentElement.getAttribute("data-app-lang");

    function translateShortLabels() {
        if (lang !== "lv") {
            return Promise.resolve();
        }

        var titleEl = document.getElementById("movie-title-text");
        var items = [];
        if (titleEl) {
            var t = titleEl.getAttribute("data-en");
            if (t) {
                items.push({ el: titleEl, en: t });
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

        return fetch("/api/translate/batch", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ texts: unique }),
        })
            .then(function (r) {
                return r.ok ? r.json() : Promise.reject(new Error("batch"));
            })
            .then(function (data) {
                if (!data || !data.texts || data.texts.length !== unique.length) {
                    return;
                }
                var map = {};
                unique.forEach(function (en, i) {
                    map[en] = data.texts[i];
                });
                items.forEach(function (it) {
                    if (map[it.en]) {
                        it.el.textContent = map[it.en];
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
        if (lang !== "lv" || !en) {
            return Promise.resolve();
        }

        var el = document.getElementById("movie-plot-text");
        if (!el) {
            return Promise.resolve();
        }

        el.classList.add("movie-plot--translating");

        return fetch("/api/translate/plot", {
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

    translateShortLabels();
    translatePlot();
});
