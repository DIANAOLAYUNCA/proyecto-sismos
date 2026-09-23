<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Eliminar nivel de afectación | CSSP</title>

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
            ¿Desea eliminar el nivel de afectación
            <strong>
                <c:out value="${nivel.nombre}"/>
            </strong>
            (<c:out value="${nivel.codigo}"/>)?
        </p>

        <p class="nota">
            Esta acción no se puede deshacer. Si hay eventos que referencian
            este nivel, quedarán con un ID inválido.
        </p>

        <form method="post"
              action="${pageContext.request.contextPath}/afectacionniveles/eliminar"
              class="acciones">

            <input type="hidden"
                   name="id"
                   value="${nivel.id}">

            <button class="boton-peligro"
                    type="submit">
                Confirmar eliminación
            </button>

            <a href="${pageContext.request.contextPath}/afectacionniveles">
                Cancelar
            </a>

        </form>

    </section>

</main>

</body>
</html>
