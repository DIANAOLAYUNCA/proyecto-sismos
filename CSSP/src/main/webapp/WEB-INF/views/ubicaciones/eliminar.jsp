<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Eliminar ubicación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Confirmar eliminación</h1>

    </header>

    <section class="tarjeta">

        <p>
            ¿Desea eliminar la ubicación con ID
            <strong>
                <c:out value="${ubicacion.id}"/>
            </strong>
            (provincia <c:out value="${ubicacion.provinciaId}"/>)?
        </p>

        <p class="nota">
            Esta acción no se puede deshacer. Si hay estaciones o eventos que
            referencian esta ubicación, quedarán con un ID inválido.
        </p>

        <form method="post"
              action="${pageContext.request.contextPath}/ubicaciones/eliminar"
              class="acciones">

            <input type="hidden"
                   name="id"
                   value="${ubicacion.id}">

            <button class="boton-peligro"
                    type="submit">
                Confirmar eliminación
            </button>

            <a href="${pageContext.request.contextPath}/ubicaciones">
                Cancelar
            </a>

        </form>

    </section>

</main>

</body>
</html>
