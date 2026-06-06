/* Shrabon Events - front-end behaviour */
(function () {
    "use strict";

    // Sticky public navbar shadow on scroll
    var nav = document.querySelector(".public-navbar");
    if (nav) {
        window.addEventListener("scroll", function () {
            if (window.scrollY > 40) {
                nav.classList.add("shadow");
            } else {
                nav.classList.remove("shadow");
            }
        });
    }

    // Dashboard sidebar toggle (mobile)
    var toggle = document.getElementById("sidebarToggle");
    var sidebar = document.getElementById("dashSidebar");
    var backdrop = document.getElementById("sidebarBackdrop");
    if (toggle && sidebar) {
        toggle.addEventListener("click", function () {
            sidebar.classList.toggle("open");
            if (backdrop) backdrop.classList.toggle("show");
        });
    }
    if (backdrop && sidebar) {
        backdrop.addEventListener("click", function () {
            sidebar.classList.remove("open");
            backdrop.classList.remove("show");
        });
    }

    // Animated statistics counters
    var counters = document.querySelectorAll("[data-count]");
    if (counters.length) {
        var run = function (el) {
            var target = parseFloat(el.getAttribute("data-count")) || 0;
            var dur = 1400, start = 0, t0 = null;
            function step(ts) {
                if (!t0) t0 = ts;
                var p = Math.min((ts - t0) / dur, 1);
                el.textContent = Math.floor(p * target).toLocaleString();
                if (p < 1) requestAnimationFrame(step);
                else el.textContent = target.toLocaleString();
            }
            requestAnimationFrame(step);
        };
        var io = new IntersectionObserver(function (entries) {
            entries.forEach(function (e) {
                if (e.isIntersecting) { run(e.target); io.unobserve(e.target); }
            });
        }, { threshold: 0.4 });
        counters.forEach(function (c) { io.observe(c); });
    }
})();

/* Custom Package Builder live quote (used on the client customize page) */
function initBuilder(quoteUrl, csrfHeader, csrfToken) {
    var pkgSelect = document.getElementById("packageId");
    var checkboxes = document.querySelectorAll(".builder-checkbox");

    function selectedItemIds() {
        return Array.prototype.slice.call(checkboxes)
            .filter(function (c) { return c.checked; })
            .map(function (c) { return c.value; });
    }

    function fmt(n) {
        return "৳ " + Number(n).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function refresh() {
        var params = new URLSearchParams();
        if (pkgSelect && pkgSelect.value) params.append("packageId", pkgSelect.value);
        selectedItemIds().forEach(function (id) { params.append("itemIds", id); });

        var headers = { "Content-Type": "application/x-www-form-urlencoded" };
        if (csrfHeader && csrfToken) headers[csrfHeader] = csrfToken;

        fetch(quoteUrl, { method: "POST", headers: headers, body: params.toString() })
            .then(function (r) { return r.json(); })
            .then(function (q) {
                setText("qBase", fmt(q.basePrice));
                setText("qAdd", fmt(q.additionalCost));
                setText("qDiscount", "- " + fmt(q.discount));
                setText("qFinal", fmt(q.finalPrice));
            })
            .catch(function () { /* ignore */ });
    }

    function setText(id, val) { var el = document.getElementById(id); if (el) el.textContent = val; }

    if (pkgSelect) pkgSelect.addEventListener("change", refresh);
    checkboxes.forEach(function (c) { c.addEventListener("change", refresh); });
    refresh();
}
