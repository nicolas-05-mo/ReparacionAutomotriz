// Modo oscuro / claro. Se carga en el <head> para aplicar el tema antes de pintar la página
(function () {
    var KEY = 'taller-theme';
    var saved = null;
    try { saved = localStorage.getItem(KEY); } catch (e) { /* almacenamiento bloqueado */ }
    var prefersDark = window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches;
    document.documentElement.setAttribute('data-theme', saved || (prefersDark ? 'dark' : 'light'));

    document.addEventListener('DOMContentLoaded', function () {
        var btn = document.getElementById('theme-toggle');
        if (!btn) { return; }

        function render() {
            var dark = document.documentElement.getAttribute('data-theme') === 'dark';
            btn.querySelector('i').className = 'bi ' + (dark ? 'bi-sun' : 'bi-moon-stars');
            btn.querySelector('span').textContent = dark ? 'Modo claro' : 'Modo oscuro';
            btn.setAttribute('aria-pressed', String(dark));
        }

        btn.addEventListener('click', function () {
            var next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
            document.documentElement.setAttribute('data-theme', next);
            try { localStorage.setItem(KEY, next); } catch (e) { /* se ignora */ }
            render();
        });
        render();
    });
})();
