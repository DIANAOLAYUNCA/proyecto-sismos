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
              class="formulario formulario-columna">

            <label>
                Provincia (ID)

                <input name="provinciaId"
                       type="number"
                       value="<c:out value='${provinciaIdIngresada}'/>"
                       min="1"
                       required>
            </label>

            <label>
                Distrito (ID, opcional)

                <input name="distritoId"
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

</body>
</html>
