<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Estaciones | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<main class="contenedor">
    <header>
        <span class="version">Sistema de sismos</span>
        <h1>Lista de estaciones</h1>
    </header>
    <nav class="acciones">
        <a class="boton"
           href="${pageContext.request.contextPath}/estaciones/nuevo">
            Nueva estación
        </a>
    </nav>
    <c:if test="${param.eliminado eq 'true'}">
        <div class="alerta alerta-exito">
            La estación fue dada de baja correctamente.
        </div>
    </c:if>
    <c:if test="${param.eliminado eq 'false'}">
        <div class="alerta alerta-error">
            No se pudo dar de baja la estación: el registro ya no existe o el identificador no es válido.
        </div>
    </c:if>
    <section class="tarjeta">
        <form method="get"
              action="${pageContext.request.contextPath}/estaciones"
              class="filtro">
            <label>
                Estado
                <input name="estado"
                       value="<c:out value='${estadoSeleccionado}'/>"
                       placeholder="Ej. ACTIVA">
            </label>
            <button type="submit">
                Filtrar
            </button>
            <a href="${pageContext.request.contextPath}/estaciones">
                Limpiar
            </a>
        </form>
    </section>
    <section class="tarjeta">
        <h2>
            Estaciones encontradas:
            <c:out value="${totalEstaciones}"/>
        </h2>
        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Código</th>
                        <th>Nombre</th>
                        <th>Ubicación</th>
                        <th>Latitud</th>
                        <th>Longitud</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>

                <c:choose>
                    <c:when test="${empty estaciones}">
                        <tr>
                            <td colspan="8" class="vacio">
                                No se encontraron estaciones.
                            </td>
                        </tr>

                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${estaciones}" var="estacion">
                            <tr>
                                <td>
                                    <c:out value="${estacion.id}"/>
                                </td>

                                <td>
                                    <c:out value="${estacion.codigo}"/>
                                </td>

                                <td>
                                    <c:out value="${estacion.nombre}"/>
                                </td>

                                <td>
                                    <c:out value="${estacion.ubicacionId}"/>
                                </td>

                                <td>
                                    <c:out value="${estacion.latitud}"/>
                                </td>

                                <td>
                                    <c:out value="${estacion.longitud}"/>
                                </td>

                                <td>
                                    <c:out value="${estacion.estado}"/>
                                </td>

                                <td class="acciones-tabla">

                                    <a href="${pageContext.request.contextPath}/estaciones/detalle?id=${estacion.id}">
                                        Ver
                                    </a>

                                    <a href="${pageContext.request.contextPath}/estaciones/editar?id=${estacion.id}">
                                        Editar
                                    </a>

                                    <a class="enlace-peligro"
                                       href="${pageContext.request.contextPath}/estaciones/eliminar?id=${estacion.id}">
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