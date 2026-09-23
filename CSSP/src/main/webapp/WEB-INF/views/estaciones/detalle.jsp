<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Detalle de estación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Detalle de la estación</h1>

    </header>

    <c:if test="${estacionCreada}">

        <div class="alerta alerta-exito">
            Estación registrada correctamente.
        </div>

    </c:if>

    <c:if test="${param.actualizado eq '1'}">

        <div class="alerta alerta-exito">
            Estación actualizada correctamente.
        </div>

    </c:if>

    <section class="tarjeta detalle">

        <dl>

            <div>
                <dt>ID</dt>
                <dd>
                    <c:out value="${estacion.id}"/>
                </dd>
            </div>

            <div>
                <dt>Código</dt>
                <dd>
                    <c:out value="${estacion.codigo}"/>
                </dd>
            </div>

            <div>
                <dt>Nombre</dt>
                <dd>
                    <c:out value="${estacion.nombre}"/>
                </dd>
            </div>

            <div>
                <dt>Ubicación ID</dt>
                <dd>
                    <c:out value="${estacion.ubicacionId}"/>
                </dd>
            </div>

            <div>
                <dt>Latitud</dt>
                <dd>
                    <c:out value="${estacion.latitud}"/>
                </dd>
            </div>

            <div>
                <dt>Longitud</dt>
                <dd>
                    <c:out value="${estacion.longitud}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de instalación</dt>
                <dd>
                    <c:out value="${estacion.instalacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Estado</dt>
                <dd>
                    <c:out value="${estacion.estado}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de creación</dt>
                <dd>
                    <c:out value="${estacion.creacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de creación</dt>
                <dd>
                    <c:out value="${estacion.creacionUsuario}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de actualización</dt>
                <dd>
                    <c:out value="${estacion.actualizacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de actualización</dt>
                <dd>
                    <c:out value="${estacion.actualizacionUsuario}"/>
                </dd>
            </div>

        </dl>

    </section>

    <nav class="acciones">

        <a class="boton"
           href="${pageContext.request.contextPath}/estaciones/editar?id=${estacion.id}">
            Editar
        </a>

        <a class="enlace-peligro"
           href="${pageContext.request.contextPath}/estaciones/eliminar?id=${estacion.id}">
            Eliminar
        </a>

        <a href="${pageContext.request.contextPath}/estaciones">
            Volver a la lista
        </a>

    </nav>

</main>

</body>
</html>