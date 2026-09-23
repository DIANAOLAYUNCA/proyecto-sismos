<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Nuevo nivel de afectación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Nuevo nivel de afectación</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/afectacionniveles/nuevo"
              class="formulario formulario-columna"
              id="formulario-nivel"
              novalidate>

            <label>
                Nombre

                <input id="campo-nombre"
                       name="nombre"
                       value="<c:out value='${nombreIngresado}'/>"
                       maxlength="100"
                       required>
            </label>

            <label>
                Código

                <input id="campo-codigo"
                       name="codigo"
                       value="<c:out value='${codigoIngresado}'/>"
                       maxlength="20"
                       required>
            </label>

            <div class="acciones">

                <button type="submit">
                    Registrar nivel
                </button>

                <a href="${pageContext.request.contextPath}/afectacionniveles">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

<script src="${pageContext.request.contextPath}/js/validaciones.js"></script>
<script>
    (function () {
        const campoNombre = document.getElementById('campo-nombre');
        const campoCodigo = document.getElementById('campo-codigo');
        const formulario = document.getElementById('formulario-nivel');

        campoNombre.addEventListener('input', function () {
            ValidacionesCSSP.validarTexto(campoNombre, 100, 'El nombre es obligatorio.');
        });

        campoCodigo.addEventListener('input', function () {
            ValidacionesCSSP.validarTexto(campoCodigo, 20, 'El código es obligatorio.');
        });

        formulario.addEventListener('submit', function (evento) {
            const nombreValido = ValidacionesCSSP.validarTexto(campoNombre, 100, 'El nombre es obligatorio.');
            const codigoValido = ValidacionesCSSP.validarTexto(campoCodigo, 20, 'El código es obligatorio.');

            if (!nombreValido || !codigoValido) {
                evento.preventDefault();
            }
        });
    })();
</script>

</body>
</html>
