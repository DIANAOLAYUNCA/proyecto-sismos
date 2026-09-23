<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="es">

<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Nuevo sismo | CSSP</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/estilos.css">

</head>

<body>

<main class="contenedor">

    <header>

        <span class="version">Sistema de sismos</span>

        <h1>Nuevo sismo</h1>

    </header>

    <section class="tarjeta">

        <c:if test="${not empty error}">

            <div class="alerta alerta-error">
                <c:out value="${error}"/>
            </div>

        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/eventos/nuevo"
              class="formulario formulario-columna">

            <label>
                Estación ID

                <input name="estacionId"
                       type="number"
                       value="<c:out value='${estacionIdIngresada}'/>"
                       required>
            </label>

            <label>
                Código

                <input name="codigo"
                       value="<c:out value='${codigoIngresado}'/>"
                       required>
            </label>

            <label>
                Fecha y hora

                <input name="fechaHora"
                       type="datetime-local"
                       value="<c:out value='${fechaHoraIngresada}'/>"
                       required>
            </label>

            <label>
                Magnitud

                <input name="magnitud"
                       type="number"
                       step="0.01"
                       value="<c:out value='${magnitudIngresada}'/>"
                       required>
            </label>

            <label>
                Profundidad

                <input name="profundidad"
                       type="number"
                       step="0.01"
                       value="<c:out value='${profundidadIngresada}'/>"
                       required>
            </label>

            <label>
                Latitud

                <input name="latitud"
                       type="number"
                       step="0.000001"
                       value="<c:out value='${latitudIngresada}'/>"
                       required>
            </label>

            <label>
                Longitud

                <input name="longitud"
                       type="number"
                       step="0.000001"
                       value="<c:out value='${longitudIngresada}'/>"
                       required>
            </label>

            <label>
                Ubicación ID

                <input name="ubicacionId"
                       type="number"
                       value="<c:out value='${ubicacionIdIngresada}'/>"
                       required>
            </label>

            <label>
                Nivel de afectación ID

                <input name="afectacionNivelId"
                       type="number"
                       value="<c:out value='${afectacionNivelIdIngresada}'/>"
                       required>
            </label>

            <label>
                Estado

                <select name="estado" required>

                    <option value="REGISTRADO"
                            ${estadoIngresado eq 'REGISTRADO' || empty estadoIngresado ? 'selected' : ''}>
                        Registrado
                    </option>

                    <option value="EN_EVALUACION"
                            ${estadoIngresado eq 'EN_EVALUACION' ? 'selected' : ''}>
                        En evaluación
                    </option>

                    <option value="EN_SEGUIMIENTO"
                            ${estadoIngresado eq 'EN_SEGUIMIENTO' ? 'selected' : ''}>
                        En seguimiento
                    </option>

                    <option value="CERRADO"
                            ${estadoIngresado eq 'CERRADO' ? 'selected' : ''}>
                        Cerrado
                    </option>

                </select>

            </label>

            <div class="acciones">

                <button type="submit">
                    Registrar sismo
                </button>

                <a href="${pageContext.request.contextPath}/eventos">
                    Cancelar
                </a>

            </div>

        </form>

    </section>

</main>

</body>
</html>