<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Registrar ubicación | CSSP</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"></head>
<body><main class="contenedor"><header><span class="version">Sistema de sismos</span><h1>Registrar ubicación</h1></header>
<section class="tarjeta"><c:if test="${not empty error}"><p class="alerta alerta-error"><c:out value="${error}"/></p></c:if>
<form method="post" action="${pageContext.request.contextPath}/ubicaciones/crear">
<label>Provincia (ID)<input type="number" name="provinciaId" value="${provinciaId}" min="1" required></label>
<label>Distrito (ID, opcional)<input type="number" name="distritoId" value="${distritoId}" min="1"></label>
<div class="acciones"><button type="submit">Guardar ubicación</button><a href="${pageContext.request.contextPath}/ubicaciones">Cancelar</a></div>
</form>
</section></main></body></html>
