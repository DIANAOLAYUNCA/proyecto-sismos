<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Dar de baja estación | CSSP</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"></head>
<body><main class="contenedor"><header><span class="version">Sistema de sismos</span><h1>Confirmar baja</h1></header>
<section class="tarjeta"><p>¿Desea dar de baja la estación <strong><c:out value="${estacion.nombre}"/></strong> (<c:out value="${estacion.codigo}"/>)?</p>
<p class="nota">La estación pasará a estado INACTIVA y dejará de estar disponible para registrar nuevos sismos.</p>
<form method="post" action="${pageContext.request.contextPath}/estaciones/eliminar" class="acciones"><input type="hidden" name="id" value="${estacion.id}"><button class="boton-peligro" type="submit">Confirmar baja</button><a href="${pageContext.request.contextPath}/estaciones">Cancelar</a></form>
</section></main></body></html>