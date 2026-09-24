<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Sismos | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Lista de sismos</h1>

    </header>

    <nav class="acciones">

        <a class="boton"
           href="${pageContext.request.contextPath}/eventos/nuevo">
            Nuevo sismo
        </a>

    </nav>

    <c:if test="${param.eliminado eq 'true'}">

        <div class="alerta alerta-exito">
            Sismo eliminado correctamente.
        </div>

    </c:if>

    <c:if test="${param.eliminado eq 'false'}">

        <div class="alerta alerta-error">
            No se pudo eliminar el sismo: el registro ya no existe o el identificador no es válido.
        </div>

    </c:if>

    <section class="tarjeta">

        <form method="get"
              action="${pageContext.request.contextPath}/eventos"
              class="filtro">

            <label>
                Código

                <input name="codigo"
                       value="<c:out value='${codigoBuscado}'/>"
                       placeholder="Ej. SIS-2026-0001">
            </label>

            <label>
                Fecha

                <input name="fecha"
                       type="date"
                       value="<c:out value='${fechaSeleccionada}'/>">
            </label>

            <label>
                Magnitud mínima

                <input name="magnitudMinima"
                       value="<c:out value='${magnitudMinima}'/>"
                       placeholder="Ej. 4.0">
            </label>

            <label>
                Estado

                <input name="estado"
                       value="<c:out value='${estadoSeleccionado}'/>"
                       placeholder="Ej. REGISTRADO">
            </label>

            <label>
                Ubicación

                <input name="ubicacion"
                       value="<c:out value='${ubicacionSeleccionada}'/>"
                       placeholder="ID de ubicación">
            </label>

            <button type="submit">
                Filtrar
            </button>

            <a href="${pageContext.request.contextPath}/eventos">
                Limpiar
            </a>

        </form>

    </section>

    <section class="tarjeta">

        <h2>
            Sismos encontrados:
            <c:out value="${totalEventos}"/>
        </h2>

        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Código</th>
                        <th>Fecha</th>
                        <th>Magnitud</th>
                        <th>Profundidad</th>
                        <th>Ubicación</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty eventos}">
                        <tr>
                            <td colspan="8"
                                class="vacio">
                                No se encontraron sismos.
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>

                        <c:forEach items="${eventos}" var="evento">

                            <tr>

                                <td>
                                    <c:out value="${evento.id}"/>
                                </td>

                                <td>
                                    <c:out value="${evento.codigo}"/>
                                </td>

                                <td>
                                    <c:out value="${evento.fechaHora}"/>
                                </td>

                                <td>
                                    <c:out value="${evento.magnitud}"/>
                                </td>

                                <td>
                                    <c:out value="${evento.profundidad}"/>
                                </td>

                                <td>
                                    <c:out value="${evento.ubicacionId}"/>
                                </td>

                                <td>
                                    <c:out value="${evento.estado}"/>
                                </td>

                                <td class="acciones-tabla">

                                    <a href="${pageContext.request.contextPath}/eventos/detalle?id=${evento.id}">
                                        Ver
                                    </a>

                                    <a href="${pageContext.request.contextPath}/eventos/editar?id=${evento.id}">
                                        Editar
                                    </a>

                                    <a class="enlace-peligro"
                                       href="${pageContext.request.contextPath}/eventos/eliminar?id=${evento.id}">
                                        Eliminar
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </section>
</main>
</body>
</html>