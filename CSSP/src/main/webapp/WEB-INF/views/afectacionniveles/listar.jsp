<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Niveles de afectación | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<main class="contenedor">
    <header>
        <span class="version">Sistema de sismos</span>
        <h1>Lista de niveles de afectación</h1>
    </header>

    <nav class="acciones">
        <a class="boton"
           href="${pageContext.request.contextPath}/afectacionniveles/nuevo">
            Nuevo nivel
        </a>
    </nav>

    <c:if test="${param.eliminado eq 'true'}">
        <div class="alerta alerta-exito">
            El nivel de afectación fue eliminado correctamente.
        </div>
    </c:if>

    <c:if test="${param.eliminado eq 'false'}">
        <div class="alerta alerta-error">
            No se pudo eliminar el nivel de afectación: el registro ya no existe o el identificador no es válido.
        </div>
    </c:if>

    <section class="tarjeta">
        <form method="get"
              action="${pageContext.request.contextPath}/afectacionniveles"
              class="filtro">
            <label>
                Código
                <input name="codigo"
                       value="<c:out value='${codigoSeleccionado}'/>"
                       placeholder="Ej. LEV">
            </label>
            <button type="submit">
                Filtrar
            </button>
            <a href="${pageContext.request.contextPath}/afectacionniveles">
                Limpiar
            </a>
        </form>
    </section>

    <section class="tarjeta">
        <h2>
            Niveles encontrados:
            <c:out value="${totalNiveles}"/>
        </h2>
        <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Código</th>
                        <th>Acciones</th>
                    </tr>
                </thead>

                <tbody>

                <c:choose>
                    <c:when test="${empty niveles}">
                        <tr>
                            <td colspan="4" class="vacio">
                                No se encontraron niveles de afectación.
                            </td>
                        </tr>

                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${niveles}" var="nivel">
                            <tr>
                                <td>
                                    <c:out value="${nivel.id}"/>
                                </td>

                                <td>
                                    <c:out value="${nivel.nombre}"/>
                                </td>

                                <td>
                                    <c:out value="${nivel.codigo}"/>
                                </td>

                                <td class="acciones-tabla">

                                    <a href="${pageContext.request.contextPath}/afectacionniveles/detalle?id=${nivel.id}">
                                        Ver
                                    </a>

                                    <a href="${pageContext.request.contextPath}/afectacionniveles/editar?id=${nivel.id}">
                                        Editar
                                    </a>

                                    <a class="enlace-peligro"
                                       href="${pageContext.request.contextPath}/afectacionniveles/eliminar?id=${nivel.id}">
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
