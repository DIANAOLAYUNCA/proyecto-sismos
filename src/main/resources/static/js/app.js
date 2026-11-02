(function () {
    var CLASES_ESTADO = {
        ACTIVO: 'badge-ok', ACTIVA: 'badge-ok', VERIFICADO: 'badge-ok', IMPORTADA: 'badge-ok',
        REGISTRADO: 'badge-info', EN_EVALUACION: 'badge-info', EN_SEGUIMIENTO: 'badge-info', OMITIDA: 'badge-info',
        INACTIVO: 'badge-off', INACTIVA: 'badge-off', DESCARTADO: 'badge-off', CERRADO: 'badge-off',
        RECHAZADA: 'badge-rojo',
        VERDE: 'badge-verde', AMARILLO: 'badge-amarillo', NARANJA: 'badge-naranja', ROJO: 'badge-rojo'
    };

    function marcarPaginaActiva() {
        var ruta = window.location.pathname;
        var enlaces = document.querySelectorAll('.navbar-enlaces a[data-ruta]');
        for (var i = 0; i < enlaces.length; i++) {
            var base = enlaces[i].getAttribute('data-ruta');
            if (ruta === base || ruta.indexOf(base + '/') === 0) {
                enlaces[i].classList.add('activo');
                enlaces[i].setAttribute('aria-current', 'page');
            }
        }
    }

    function activarMenuMovil() {
        var boton = document.querySelector('.navbar-toggle');
        var menu = document.getElementById('menu-principal');
        if (!boton || !menu) return;
        boton.addEventListener('click', function () {
            var abierto = menu.classList.toggle('abierto');
            boton.setAttribute('aria-expanded', abierto ? 'true' : 'false');
        });
    }

    function colocarInsignias() {
        var celdas = document.querySelectorAll('td, .detalle dd');
        for (var i = 0; i < celdas.length; i++) {
            var celda = celdas[i];
            if (celda.children.length > 0) continue;
            var texto = celda.textContent.trim();
            var clase = CLASES_ESTADO[texto];
            if (!clase) continue;
            var insignia = document.createElement('span');
            insignia.className = 'badge ' + clase;
            insignia.textContent = texto;
            celda.textContent = '';
            celda.appendChild(insignia);
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        marcarPaginaActiva();
        activarMenuMovil();
        colocarInsignias();
    });
})();
