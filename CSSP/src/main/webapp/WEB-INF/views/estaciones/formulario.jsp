<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Nueva estación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Nueva estación</h1>


    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/estaciones/nuevo"
              class="formulario formulario-columna">

            <label>
                Código

                <input name="codigo"
                       value="<c:out value='${codigoIngresado}'/>"
                       required>
            </label>

            <label>
                Nombre

                <input name="nombre"
                       value="<c:out value='${nombreIngresado}'/>"
                       required>
            </label>

            <label>
                Ubicación

                <input name="ubicacionId"
                       type="number"
                       value="<c:out value='${ubicacionIdIngresada}'/>"
                       required>
            </label>

            <label>
                Latitud

                <input name="latitud"
                       type="number"
                       step="0.000001"
                       value="<c:out value='${latitudIngresada}'/>"
                       required>
            </label>

            <label>
                Longitud

                <input name="longitud"
                       type="number"
                       step="0.000001"
                       value="<c:out value='${longitudIngresada}'/>"
                       required>
            </label>

            <label>
                Fecha de instalación

                <input name="instalacionFecha"
                       type="date"
                       value="<c:out value='${instalacionFechaIngresada}'/>"
                       required>
            </label>

            <label>
                Estado

                <select name="estado" required>

                    <option value="ACTIVA"
                            ${estadoIngresado ne 'INACTIVA' ? 'selected' : ''}>
                        Activa
                    </option>

                    <option value="INACTIVA"
                            ${estadoIngresado eq 'INACTIVA' ? 'selected' : ''}>
                        Inactiva
                    </option>

                </select>

            </label>

            <div class="acciones">

                <button type="submit">
                    Registrar estación
                </button>

                <a href="${pageContext.request.contextPath}/estaciones">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

</body>
</html>