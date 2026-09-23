<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Detalle del evento | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">
    <header>
        <span class="version">Sistema de sismos</span>
        <h1>Detalle del evento</h1>
    </header>

    <c:if test="${eventoCreado}">
        <div class="alerta alerta-exito">
            Evento registrado correctamente.
        </div>
    </c:if>

    <c:if test="${param.actualizado eq '1'}">
        <div class="alerta alerta-exito">
            Evento actualizado correctamente.
        </div>
    </c:if>

    <section class="tarjeta detalle">
        <dl>
            <div>
                <dt>ID</dt>
                <dd>
                    <c:out value="${evento.id}"/>
                </dd>
            </div>

            <div>
                <dt>Estación ID</dt>
                <dd>
                    <c:out value="${evento.estacionId}"/>
                </dd>
            </div>

            <div>
                <dt>Código</dt>
                <dd>
                    <c:out value="${evento.codigo}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha y hora</dt>
                <dd>
                    <c:out value="${evento.fechaHora}"/>
                </dd>
            </div>

            <div>
                <dt>Magnitud</dt>
                <dd>
                    <c:out value="${evento.magnitud}"/>
                </dd>
            </div>

            <div>
                <dt>Profundidad</dt>
                <dd>
                    <c:out value="${evento.profundidad}"/> km
                </dd>
            </div>

            <div>
                <dt>Latitud</dt>
                <dd>
                    <c:out value="${evento.latitud}"/>
                </dd>
            </div>

            <div>
                <dt>Longitud</dt>
                <dd>
                    <c:out value="${evento.longitud}"/>
                </dd>
            </div>

            <div>
                <dt>Ubicación ID</dt>
                <dd>
                    <c:out value="${evento.ubicacionId}"/>
                </dd>
            </div>

            <div>
                <dt>Nivel de afectación</dt>
                <dd>
                    <c:out value="${evento.afectacionNivelId}"/>
                </dd>
            </div>

            <div>
                <dt>Estado</dt>
                <dd>
                    <c:out value="${evento.estado}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de creación</dt>
                <dd>
                    <c:out value="${evento.creacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de creación</dt>
                <dd>
                    <c:out value="${evento.creacionUsuario}"/>
                </dd>
            </div>

            <div>
                <dt>Fecha de actualización</dt>
                <dd>
                    <c:out value="${evento.actualizacionFecha}"/>
                </dd>
            </div>

            <div>
                <dt>Usuario de actualización</dt>
                <dd>
                    <c:out value="${evento.actualizacionUsuario}"/>
                </dd>
            </div>

        </dl>

    </section>

    <nav class="acciones">

        <a class="boton"
           href="${pageContext.request.contextPath}/eventos/editar?id=${evento.id}">
            Editar
        </a>

        <a class="enlace-peligro"
           href="${pageContext.request.contextPath}/eventos/eliminar?id=${evento.id}">
            Eliminar
        </a>

        <a href="${pageContext.request.contextPath}/eventos">
            Volver a la lista
        </a>

    </nav>

</main>

</body>
</html>