<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Nuevo sismo | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Nuevo sismo</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/eventos/nuevo"
              class="formulario formulario-columna"
              id="formulario-evento"
              novalidate>

            <label>
                Estación ID

                <input id="campo-estacionId"
                       name="estacionId"
                       type="number"
                       value="<c:out value='${estacionIdIngresada}'/>"
                       required>
            </label>

            <label>
                Código

                <input id="campo-codigo"
                       name="codigo"
                       value="<c:out value='${codigoIngresado}'/>"
                       required>
            </label>

            <label>
                Fecha y hora

                <input id="campo-fechaHora"
                       name="fechaHora"
                       type="datetime-local"
                       value="<c:out value='${fechaHoraIngresada}'/>"
                       required>
            </label>

            <label>
                Magnitud

                <input id="campo-magnitud"
                       name="magnitud"
                       type="number"
                       step="0.01"
                       value="<c:out value='${magnitudIngresada}'/>"
                       required>
            </label>

            <label>
                Profundidad

                <input id="campo-profundidad"
                       name="profundidad"
                       type="number"
                       step="0.01"
                       value="<c:out value='${profundidadIngresada}'/>"
                       required>
            </label>

            <label>
                Latitud

                <input id="campo-latitud"
                       name="latitud"
                       type="number"
                       step="0.000001"
                       value="<c:out value='${latitudIngresada}'/>"
                       required>
            </label>

            <label>
                Longitud

                <input id="campo-longitud"
                       name="longitud"
                       type="number"
                       step="0.000001"
                       value="<c:out value='${longitudIngresada}'/>"
                       required>
            </label>

            <label>
                Ubicación ID

                <input id="campo-ubicacionId"
                       name="ubicacionId"
                       type="number"
                       value="<c:out value='${ubicacionIdIngresada}'/>"
                       required>
            </label>

            <label>
                Nivel de afectación ID

                <input id="campo-afectacionNivelId"
                       name="afectacionNivelId"
                       type="number"
                       value="<c:out value='${afectacionNivelIdIngresada}'/>"
                       required>
            </label>

            <label>
                Estado

                <select name="estado" required>

                    <option value="REGISTRADO"
                            ${estadoIngresado eq 'REGISTRADO' || empty estadoIngresado ? 'selected' : ''}>
                        Registrado
                    </option>

                    <option value="EN_EVALUACION"
                            ${estadoIngresado eq 'EN_EVALUACION' ? 'selected' : ''}>
                        En evaluación
                    </option>

                    <option value="EN_SEGUIMIENTO"
                            ${estadoIngresado eq 'EN_SEGUIMIENTO' ? 'selected' : ''}>
                        En seguimiento
                    </option>

                    <option value="CERRADO"
                            ${estadoIngresado eq 'CERRADO' ? 'selected' : ''}>
                        Cerrado
                    </option>

                </select>

            </label>

            <div class="acciones">

                <button type="submit">
                    Registrar sismo
                </button>

                <a href="${pageContext.request.contextPath}/eventos">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

<script src="${pageContext.request.contextPath}/js/validaciones.js"></script>
<script>
    (function () {
        const campoEstacionId = document.getElementById('campo-estacionId');
        const campoCodigo = document.getElementById('campo-codigo');
        const campoFechaHora = document.getElementById('campo-fechaHora');
        const campoMagnitud = document.getElementById('campo-magnitud');
        const campoProfundidad = document.getElementById('campo-profundidad');
        const campoLatitud = document.getElementById('campo-latitud');
        const campoLongitud = document.getElementById('campo-longitud');
        const campoUbicacionId = document.getElementById('campo-ubicacionId');
        const campoAfectacionNivelId = document.getElementById('campo-afectacionNivelId');
        const formulario = document.getElementById('formulario-evento');

        const mensajes = {
            estacionId: 'La estación es obligatoria y debe ser un número entero positivo.',
            codigo: 'El código es obligatorio.',
            fechaHora: 'La fecha y hora son obligatorias.',
            magnitud: 'La magnitud es obligatoria y debe ser mayor a 0.',
            profundidad: 'La profundidad es obligatoria y debe ser 0 o mayor.',
            latitud: 'La latitud es obligatoria y debe estar entre -90 y 90.',
            longitud: 'La longitud es obligatoria y debe estar entre -180 y 180.',
            ubicacionId: 'La ubicación es obligatoria y debe ser un número entero positivo.',
            afectacionNivelId: 'El nivel de afectación es obligatorio y debe ser un número entero positivo.'
        };

        function validarTodo() {
            const resultados = [
                ValidacionesCSSP.validarEnteroPositivo(campoEstacionId, false, mensajes.estacionId),
                ValidacionesCSSP.validarTexto(campoCodigo, null, mensajes.codigo),
                ValidacionesCSSP.validarRequerido(campoFechaHora, mensajes.fechaHora),
                ValidacionesCSSP.validarNumero(campoMagnitud, { min: 0, minExclusivo: true }, mensajes.magnitud),
                ValidacionesCSSP.validarNumero(campoProfundidad, { min: 0 }, mensajes.profundidad),
                ValidacionesCSSP.validarNumero(campoLatitud, { min: -90, max: 90 }, mensajes.latitud),
                ValidacionesCSSP.validarNumero(campoLongitud, { min: -180, max: 180 }, mensajes.longitud),
                ValidacionesCSSP.validarEnteroPositivo(campoUbicacionId, false, mensajes.ubicacionId),
                ValidacionesCSSP.validarEnteroPositivo(campoAfectacionNivelId, false, mensajes.afectacionNivelId)
            ];

            return resultados.every(function (valido) { return valido; });
        }

        campoEstacionId.addEventListener('input', function () {
            ValidacionesCSSP.validarEnteroPositivo(campoEstacionId, false, mensajes.estacionId);
        });

        campoCodigo.addEventListener('input', function () {
            ValidacionesCSSP.validarTexto(campoCodigo, null, mensajes.codigo);
        });

        campoFechaHora.addEventListener('input', function () {
            ValidacionesCSSP.validarRequerido(campoFechaHora, mensajes.fechaHora);
        });

        campoMagnitud.addEventListener('input', function () {
            ValidacionesCSSP.validarNumero(campoMagnitud, { min: 0, minExclusivo: true }, mensajes.magnitud);
        });

        campoProfundidad.addEventListener('input', function () {
            ValidacionesCSSP.validarNumero(campoProfundidad, { min: 0 }, mensajes.profundidad);
        });

        campoLatitud.addEventListener('input', function () {
            ValidacionesCSSP.validarNumero(campoLatitud, { min: -90, max: 90 }, mensajes.latitud);
        });

        campoLongitud.addEventListener('input', function () {
            ValidacionesCSSP.validarNumero(campoLongitud, { min: -180, max: 180 }, mensajes.longitud);
        });

        campoUbicacionId.addEventListener('input', function () {
            ValidacionesCSSP.validarEnteroPositivo(campoUbicacionId, false, mensajes.ubicacionId);
        });

        campoAfectacionNivelId.addEventListener('input', function () {
            ValidacionesCSSP.validarEnteroPositivo(campoAfectacionNivelId, false, mensajes.afectacionNivelId);
        });

        formulario.addEventListener('submit', function (evento) {
            if (!validarTodo()) {
                evento.preventDefault();
            }
        });
    })();
</script>

</body>
</html>