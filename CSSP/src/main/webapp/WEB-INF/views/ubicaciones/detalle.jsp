<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Detalle de ubicación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Detalle de la ubicación</h1>

    </header>

    <c:if test="${param.actualizado eq '1'}">

        <div class="alerta alerta-exito">
            Ubicación actualizada correctamente.
        </div>

    </c:if>

    <section class="tarjeta detalle">

        <dl>

            <div>
                <dt>ID</dt>
                <dd>
                    <c:out value="${ubicacion.id}"/>
                </dd>
            </div>

            <div>
                <dt>Provincia (ID)</dt>
                <dd>
                    <c:out value="${ubicacion.provinciaId}"/>
                </dd>
            </div>

            <div>
                <dt>Distrito (ID)</dt>
                <dd>
                    <c:choose>
                        <c:when test="${empty ubicacion.distritoId}">—</c:when>
                        <c:otherwise>
                            <c:out value="${ubicacion.distritoId}"/>
                        </c:otherwise>
                    </c:choose>
                </dd>
            </div>

            <div>
                <dt>Fecha de creación</dt>
                <dd>
                    <c:out value="${ubicacion.creacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de creación</dt>
                <dd>
                    <c:out value="${ubicacion.creacionUsuario}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de actualización</dt>
                <dd>
                    <c:out value="${ubicacion.actualizacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de actualización</dt>
                <dd>
                    <c:out value="${ubicacion.actualizacionUsuario}"/>
                </dd>
            </div>

        </dl>

    </section>

    <nav class="acciones">

        <a class="boton"
           href="${pageContext.request.contextPath}/ubicaciones/editar?id=${ubicacion.id}">
            Editar
        </a>

        <a class="enlace-peligro"
           href="${pageContext.request.contextPath}/ubicaciones/eliminar?id=${ubicacion.id}">
            Eliminar
        </a>

        <a href="${pageContext.request.contextPath}/ubicaciones">
            Volver a la lista
        </a>

    </nav>

</main>

</body>
</html>
