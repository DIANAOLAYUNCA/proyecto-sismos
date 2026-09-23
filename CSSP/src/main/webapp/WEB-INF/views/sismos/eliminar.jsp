<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Eliminar reporte | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Eliminar reporte</h1>

        <p>
            Esta eliminación no se puede deshacer.
        </p>

    </header>

    <section class="tarjeta">

        <p>
            ¿Está seguro de eliminar el reporte
            <strong>
                <c:out value="${evento.codigo}"/>
            </strong>?
        </p>

        <div class="detalle">

            <dl>

                <div>
                    <dt>Estado</dt>
                    <dd>
                        <c:out value="${evento.estado}"/>
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
                    <dt>Estación ID</dt>
                    <dd>
                        <c:out value="${evento.estacionId}"/>
                    </dd>
                </div>

            </dl>

        </div>

        <form method="post"
              action="${pageContext.request.contextPath}/eventos/eliminar"
              class="acciones">

            <input type="hidden"
                   name="id"
                   value="${evento.id}">

            <button class="boton-peligro"
                    type="submit">
                Eliminar reporte
            </button>

            <a href="${pageContext.request.contextPath}/eventos">
                Cancelar
            </a>

        </form>

    </section>

</main>

</body>
</html>