<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Registrar nivel de afectación | CSSP</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"></head>
<body><main class="contenedor"><header><span class="version">Sistema de sismos</span><h1>Registrar nivel de afectación</h1></header>
<section class="tarjeta"><c:if test="${not empty error}"><p class="alerta alerta-error"><c:out value="${error}"/></p></c:if>
<form method="post" action="${pageContext.request.contextPath}/afectacionniveles/crear">
<label>Nombre<input type="text" name="nombre" value="${nombre}" maxlength="100" required></label>
<label>Código<input type="text" name="codigo" value="${codigo}" maxlength="20" required></label>
<div class="acciones"><button type="submit">Guardar nivel</button><a href="${pageContext.request.contextPath}/afectacionniveles">Cancelar</a></div>
</form>
</section></main></body></html>
