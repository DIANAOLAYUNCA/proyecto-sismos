<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Detalle de nivel de afectación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Detalle del nivel de afectación</h1>

    </header>

    <c:if test="${param.actualizado eq '1'}">

        <div class="alerta alerta-exito">
            Nivel de afectación actualizado correctamente.
        </div>

    </c:if>

    <section class="tarjeta detalle">

        <dl>

            <div>
                <dt>ID</dt>
                <dd>
                    <c:out value="${nivel.id}"/>
                </dd>
            </div>

            <div>
                <dt>Nombre</dt>
                <dd>
                    <c:out value="${nivel.nombre}"/>
                </dd>
            </div>

            <div>
                <dt>Código</dt>
                <dd>
                    <c:out value="${nivel.codigo}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de creación</dt>
                <dd>
                    <c:out value="${nivel.creacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de creación</dt>
                <dd>
                    <c:out value="${nivel.creacionUsuario}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de actualización</dt>
                <dd>
                    <c:out value="${nivel.actualizacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de actualización</dt>
                <dd>
                    <c:out value="${nivel.actualizacionUsuario}"/>
                </dd>
            </div>

        </dl>

    </section>

    <nav class="acciones">

        <a class="boton"
           href="${pageContext.request.contextPath}/afectacionniveles/editar?id=${nivel.id}">
            Editar
        </a>

        <a class="enlace-peligro"
           href="${pageContext.request.contextPath}/afectacionniveles/eliminar?id=${nivel.id}">
            Eliminar
        </a>

        <a href="${pageContext.request.contextPath}/afectacionniveles">
            Volver a la lista
        </a>

    </nav>

</main>

</body>
</html>
