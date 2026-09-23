<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Editar ubicación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Editar ubicación</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/ubicaciones/editar"
              class="formulario formulario-columna">

            <input type="hidden"
                   name="id"
                   value="${ubicacion.id}">

            <label>
                Provincia (ID)

                <input name="provinciaId"
                       type="number"
                       value="${ubicacion.provinciaId}"
                       min="1"
                       required>
            </label>

            <label>
                Distrito (ID, opcional)

                <input name="distritoId"
                       type="number"
                       value="${ubicacion.distritoId}"
                       min="1">
            </label>

            <div class="acciones">

                <button type="submit">
                    Guardar cambios
                </button>

                <a href="${pageContext.request.contextPath}/ubicaciones/detalle?id=${ubicacion.id}">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

</body>
</html>
