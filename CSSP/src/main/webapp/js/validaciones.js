(function () {

    function contenedorError(campo) {
        let contenedor = campo.parentElement.querySelector('.mensaje-campo');

        if (!contenedor) {
            contenedor = document.createElement('span');
            contenedor.className = 'mensaje-campo';
            campo.insertAdjacentElement('afterend', contenedor);
        }

        return contenedor;
    }

    function mostrarError(campo, mensaje) {
        campo.classList.add('campo-invalido');
        contenedorError(campo).textContent = mensaje;
    }

    function limpiarError(campo) {
        campo.classList.remove('campo-invalido');
        contenedorError(campo).textContent = '';
    }

    function validarRequerido(campo, mensaje) {
        if (campo.value.trim() === '') {
            mostrarError(campo, mensaje || 'Este campo es obligatorio.');
            return false;
        }

        limpiarError(campo);
        return true;
    }

    function validarEnteroPositivo(campo, opcional, mensaje) {
        const valor = campo.value.trim();

        if (valor === '') {
            if (opcional) {
                limpiarError(campo);
                return true;
            }

            mostrarError(campo, mensaje || 'Este campo es obligatorio.');
            return false;
        }

        const numero = Number(valor);

        if (!Number.isInteger(numero) || numero <= 0) {
            mostrarError(campo, mensaje || 'Debe ser un número entero positivo.');
            return false;
        }

        limpiarError(campo);
        return true;
    }

    function validarTexto(campo, maximo, mensaje) {
        const valor = campo.value.trim();

        if (valor === '') {
            mostrarError(campo, mensaje || 'Este campo es obligatorio.');
            return false;
        }

        if (maximo && valor.length > maximo) {
            mostrarError(campo, 'Máximo ' + maximo + ' caracteres.');
            return false;
        }

        limpiarError(campo);
        return true;
    }

    window.ValidacionesCSSP = {
        validarRequerido: validarRequerido,
        validarEnteroPositivo: validarEnteroPositivo,
        validarTexto: validarTexto
    };

})();
