/* Al elegir la ubicación (departamento) el desplegable de distrito solo ofrece los distritos de ese departamento. */
(function () {
    var ubicacion = document.getElementById('ubicacionId');
    var distrito = document.getElementById('distritoId');
    if (!ubicacion || !distrito) return;

    var urlBase = distrito.getAttribute('data-url');

    function opcion(valor, texto) {
        var o = document.createElement('option');
        o.value = valor;
        o.textContent = texto;
        return o;
    }

    function cargar(ubicacionId) {
        distrito.innerHTML = '';
        distrito.appendChild(opcion('', ubicacionId ? 'Cargando distritos...' : 'Seleccione primero la ubicación'));
        if (!ubicacionId) return;

        fetch(urlBase + encodeURIComponent(ubicacionId), { headers: { 'Accept': 'application/json' }, credentials: 'same-origin' })
            .then(function (respuesta) { return respuesta.ok ? respuesta.json() : []; })
            .catch(function () { return []; })
            .then(function (distritos) {
                distrito.innerHTML = '';
                distrito.appendChild(opcion('', distritos.length ? 'Sin distrito' : 'No hay distritos para esta ubicación'));
                distritos.forEach(function (d) {
                    distrito.appendChild(opcion(d.id, d.provincia + ' - ' + d.nombre));
                });
            });
    }

    ubicacion.addEventListener('change', function () { cargar(ubicacion.value); });
})();
