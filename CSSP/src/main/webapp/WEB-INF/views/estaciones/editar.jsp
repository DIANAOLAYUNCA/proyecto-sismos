<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Editar estación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Editar estación</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/estaciones/editar"
              class="formulario formulario-columna">

            <input type="hidden"
                   name="id"
                   value="${estacion.id}">

            <label>
                Código

                <input name="codigo"
                       value="<c:out value='${estacion.codigo}'/>"
                       required>
            </label>

            <label>
                Nombre

                <input name="nombre"
                       value="<c:out value='${estacion.nombre}'/>"
                       required>
            </label>

            <label>
                Ubicación

                <input name="ubicacionId"
                       type="number"
                       value="${estacion.ubicacionId}"
                       required>
            </label>

            <label>
                Latitud

                <input name="latitud"
                       type="number"
                       step="0.000001"
                       value="${estacion.latitud}"
                       required>
            </label>

            <label>
                Longitud

                <input name="longitud"
                       type="number"
                       step="0.000001"
                       value="${estacion.longitud}"
                       required>
            </label>

            <label>
                Fecha de instalación

                <input name="instalacionFecha"
                       type="date"
                       value="<c:out value='${estacion.instalacionFecha}'/>"
                       required>
            </label>

            <label>
                Estado

                <select name="estado" required>

                    <option value="ACTIVA"
                            ${estacion.estado eq 'ACTIVA' ? 'selected' : ''}>
                        Activa
                    </option>

                    <option value="INACTIVA"
                            ${estacion.estado eq 'INACTIVA' ? 'selected' : ''}>
                        Inactiva
                    </option>

                </select>

            </label>

            <div class="acciones">

                <button type="submit">
                    Guardar cambios
                </button>

                <a href="${pageContext.request.contextPath}/estaciones/detalle?id=${estacion.id}">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

</body>
</html>