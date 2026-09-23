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
              class="formulario formulario-columna">

            <label>
                Nombre

                <input name="nombre"
                       value="<c:out value='${nombreIngresado}'/>"
                       maxlength="100"
                       required>
            </label>

            <label>
                Código

                <input name="codigo"
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

</body>
</html>
