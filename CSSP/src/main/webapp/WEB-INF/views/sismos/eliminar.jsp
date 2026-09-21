<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Eliminar reporte | CSSP</title><link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css"></head>
<body><main class="contenedor"><header><div class="miga">Reportes de sismos / <strong>Eliminar</strong></div><h1>Eliminar reporte</h1><p class="subtitulo">Esta eliminación no se puede deshacer</p></header>
<section class="tarjeta-eliminar"><div class="aviso-eliminar"><div><h2>¿Estás seguro de eliminar este reporte?</h2><p>Se eliminará el registro y todo su historial de seguimiento de la aplicación. Esta operación no se puede revertir.</p></div></div>
<div class="detalle-eliminar"><div class="detalle-cabecera"><strong><c:out value="${evento.codigo}"/></strong><span class="estado"><c:out value="${evento.estado}"/></span></div>
<div class="datos-evento"><div><span>Fecha</span><strong><c:out value="${evento.fechaHora}"/></strong></div><div><span>Magnitud</span><strong><c:out value="${evento.magnitud}"/></strong></div><div><span>Profundidad</span><strong><c:out value="${evento.profundidad}"/> km</strong></div><div><span>Ubicación ID</span><strong><c:out value="${evento.ubicacionId}"/></strong></div><div><span>Afectación nivel</span><strong><c:out value="${evento.afectacionNivelId}"/></strong></div><div><span>Estación ID</span><strong><c:out value="${evento.estacionId}"/></strong></div></div>
</div></section>
<form method="post" action="${pageContext.request.contextPath}/eventos/eliminar" class="acciones acciones-eliminar"><a class="boton-cancelar" href="${pageContext.request.contextPath}/eventos/detalle?id=${evento.id}">Cancelar</a><input type="hidden" name="id" value="${evento.id}"><button class="boton-peligro" type="submit">Eliminar reporte</button></form>
</main></body></html>