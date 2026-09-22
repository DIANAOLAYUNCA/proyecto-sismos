<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Editar evento | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Editar evento</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/eventos/editar"
              class="formulario formulario-columna">

            <input type="hidden"
                   name="id"
                   value="${evento.id}">

            <label>
                Estación ID

                <input name="estacionId"
                       type="number"
                       value="${evento.estacionId}"
                       required>
            </label>

            <label>
                Código

                <input name="codigo"
                       value="<c:out value='${evento.codigo}'/>"
                       required>
            </label>

            <label>
                Fecha y hora

                <input name="fechaHora"
                       value="<c:out value='${evento.fechaHora}'/>"
                       required>
            </label>

            <label>
                Magnitud

                <input name="magnitud"
                       type="number"
                       step="0.01"
                       value="${evento.magnitud}"
                       required>
            </label>

            <label>
                Profundidad

                <input name="profundidad"
                       type="number"
                       step="0.01"
                       value="${evento.profundidad}"
                       required>
            </label>

            <label>
                Latitud

                <input name="latitud"
                       type="number"
                       step="0.000001"
                       value="${evento.latitud}"
                       required>
            </label>

            <label>
                Longitud

                <input name="longitud"
                       type="number"
                       step="0.000001"
                       value="${evento.longitud}"
                       required>
            </label>

            <label>
                Ubicación ID

                <input name="ubicacionId"
                       type="number"
                       value="${evento.ubicacionId}"
                       required>
            </label>

            <label>
                Nivel de afectación ID

                <input name="afectacionNivelId"
                       type="number"
                       value="${evento.afectacionNivelId}"
                       required>
            </label>

            <label>
                Estado

                <select name="estado" required>

                    <option value="REGISTRADO"
                            ${evento.estado eq 'REGISTRADO' ? 'selected' : ''}>
                        Registrado
                    </option>

                    <option value="EN_EVALUACION"
                            ${evento.estado eq 'EN_EVALUACION' ? 'selected' : ''}>
                        En evaluación
                    </option>

                    <option value="EN_SEGUIMIENTO"
                            ${evento.estado eq 'EN_SEGUIMIENTO' ? 'selected' : ''}>
                        En seguimiento
                    </option>

                    <option value="CERRADO"
                            ${evento.estado eq 'CERRADO' ? 'selected' : ''}>
                        Cerrado
                    </option>

                </select>

            </label>

            <div class="acciones">

                <button type="submit">
                    Guardar cambios
                </button>

                <a href="${pageContext.request.contextPath}/eventos/detalle?id=${evento.id}">
                    Cancelar
                </a>
            </div>
        </form>
    </section>
</main>
</body>
</html>