<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Nueva ubicación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Nueva ubicación</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/ubicaciones/nuevo"
              class="formulario formulario-columna"
              id="formulario-ubicacion"
              novalidate>

            <label>
                Provincia (ID)

                <input id="campo-provinciaId"
                       name="provinciaId"
                       type="number"
                       value="<c:out value='${provinciaIdIngresada}'/>"
                       min="1"
                       required>
            </label>

            <label>
                Distrito (ID, opcional)

                <input id="campo-distritoId"
                       name="distritoId"
                       type="number"
                       value="<c:out value='${distritoIdIngresada}'/>"
                       min="1">
            </label>

            <div class="acciones">

                <button type="submit">
                    Registrar ubicación
                </button>

                <a href="${pageContext.request.contextPath}/ubicaciones">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

<script src="${pageContext.request.contextPath}/js/validaciones.js"></script>
<script>
    (function () {
        const campoProvincia = document.getElementById('campo-provinciaId');
        const campoDistrito = document.getElementById('campo-distritoId');
        const formulario = document.getElementById('formulario-ubicacion');

        const mensajeProvincia = 'La provincia es obligatoria y debe ser un número entero positivo.';
        const mensajeDistrito = 'El distrito, si se indica, debe ser un número entero positivo.';

        campoProvincia.addEventListener('input', function () {
            ValidacionesCSSP.validarEnteroPositivo(campoProvincia, false, mensajeProvincia);
        });

        campoDistrito.addEventListener('input', function () {
            ValidacionesCSSP.validarEnteroPositivo(campoDistrito, true, mensajeDistrito);
        });

        formulario.addEventListener('submit', function (evento) {
            const provinciaValida = ValidacionesCSSP.validarEnteroPositivo(campoProvincia, false, mensajeProvincia);
            const distritoValido = ValidacionesCSSP.validarEnteroPositivo(campoDistrito, true, mensajeDistrito);

            if (!provinciaValida || !distritoValido) {
                evento.preventDefault();
            }
        });
    })();
</script>

</body>
</html>
