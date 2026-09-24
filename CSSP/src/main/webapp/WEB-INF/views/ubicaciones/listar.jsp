<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ubicaciones | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<main class="contenedor">
    <header>
        <span class="version">Sistema de sismos</span>
        <h1>Lista de ubicaciones</h1>
    </header>

    <nav class="acciones">
        <a class="boton"
           href="${pageContext.request.contextPath}/ubicaciones/nuevo">
            Nueva ubicación
        </a>
    </nav>

    <c:if test="${param.eliminado eq 'true'}">
        <div class="alerta alerta-exito">
            La ubicación fue eliminada correctamente.
        </div>
    </c:if>

    <c:if test="${param.eliminado eq 'false'}">
        <div class="alerta alerta-error">
            No se pudo eliminar la ubicación: el registro ya no existe o el identificador no es válido.
        </div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alerta alerta-error">
            <c:out value="${error}"/>
        </div>
    </c:if>

    <section class="tarjeta">
        <form method="get"
              action="${pageContext.request.contextPath}/ubicaciones"
              class="filtro">
            <label>
                Provincia (ID)
                <input name="provinciaId"
                       value="<c:out value='${provinciaSeleccionada}'/>"
                       placeholder="Ej. 1">
            </label>
            <button type="submit">
                Filtrar
            </button>
            <a href="${pageContext.request.contextPath}/ubicaciones">
                Limpiar
            </a>
        </form>
    </section>

    <section class="tarjeta">
        <h2>
            Ubicaciones encontradas:
            <c:out value="${totalUbicaciones}"/>
        </h2>
        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Provincia</th>
                        <th>Distrito</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>

                <c:choose>
                    <c:when test="${empty ubicaciones}">
                        <tr>
                            <td colspan="4" class="vacio">
                                No se encontraron ubicaciones.
                            </td>
                        </tr>

                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${ubicaciones}" var="ubicacion">
                            <tr>
                                <td>
                                    <c:out value="${ubicacion.id}"/>
                                </td>

                                <td>
                                    <c:out value="${ubicacion.provinciaId}"/>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${empty ubicacion.distritoId}">—</c:when>
                                        <c:otherwise>
                                            <c:out value="${ubicacion.distritoId}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td class="acciones-tabla">

                                    <a href="${pageContext.request.contextPath}/ubicaciones/detalle?id=${ubicacion.id}">
                                        Ver
                                    </a>

                                    <a href="${pageContext.request.contextPath}/ubicaciones/editar?id=${ubicacion.id}">
                                        Editar
                                    </a>

                                    <a class="enlace-peligro"
                                       href="${pageContext.request.contextPath}/ubicaciones/eliminar?id=${ubicacion.id}">
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
