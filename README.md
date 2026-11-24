# CSSP — Sistema de Gestión de Sismos del Perú (Spring Boot)

Aplicación web (Spring Boot 3 · JPA · Thymeleaf · MySQL) para registrar sismos y sus reportes de daño, con catálogos que se pueden importar desde CSV.

## Requisitos
- JDK 21 o superior
- MySQL 8
- Maven (se incluye `mvnw`, no hace falta instalarlo)

## Base de datos
1. Ejecute `bd/cssp_schema_mysql.sql` y luego `bd/cssp_seed_data.sql` en su MySQL. Crean la base `cssp_sismos` (el esquema **borra y recrea** las tablas).
2. `bd/verificar_bd.sql` es una revisión de solo lectura: dice `OK` o `FALTA` por cada requisito del esquema.
3. Si ya tenía la base cargada con la versión anterior (sin distritos), no hace falta recrearla: ejecute **una vez** `bd/migracion_distritos.sql` sobre su base. Agrega `Distrito.estado` y la columna opcional `distrito_id` en `Estacion` y `Evento`.

> Los scripts empiezan con `USE cssp_sismos;`. Para cargarlos en otra base (por ejemplo `cssp_sismos_pa3`) cambie ese nombre en los dos archivos antes de ejecutarlos; si no, se cargan en `cssp_sismos`.

## Ejecutar
La conexión se configura con variables de entorno; no hay contraseñas en el repositorio.

```powershell
.\probar.ps1        # pide puerto, base, usuario y clave, y arranca la aplicación
```

o manualmente:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/cssp_sismos?useSSL=false&serverTimezone=UTC"
$env:DB_USER="root"
$env:DB_PASSWORD="su-clave"
.\mvnw.cmd spring-boot:run
```

Abra http://localhost:8081/ (puerto configurable con `SERVER_PORT`).

## Inicio de sesión
Todo requiere sesión. El seed trae usuarios de prueba, todos con la contraseña `123456` (guardada con BCrypt): `aramirez` y `cmendoza` (rol ADMIN, pueden crear, editar, eliminar e importar), `jperez` (ANALISTA), `lmendoza` (OPERADOR) e `invitado` (CONSULTA). Sin sesión se redirige a `/login`; los roles distintos de ADMIN pueden consultar pero reciben 403 al intentar crear, editar, eliminar o importar.

## Pruebas
```powershell
.\mvnw.cmd test
```
Usan una base H2 en memoria: no necesitan MySQL ni tocan sus datos.

## Módulos
| Ruta | Módulo |
|---|---|
| `/eventos` | Sismos |
| `/reportes-dano` | Reportes de daño (asociados a un sismo) |
| `/estaciones` | Estaciones de monitoreo |
| `/ubicaciones` | Ubigeo / ubicaciones (departamentos) |
| `/distritos` | Distritos del ubigeo INEI (CRUD, búsqueda y filtros) |
| `/afectacionniveles` | Niveles de afectación |
| `/tipos-alerta` | Tipos de alerta |
| `/tipos-dano` | Tipos de daño |
| `/importacion` | Importación de catálogos desde CSV |

## Importación de CSV
`/importacion` carga `ubigeo.csv` (25), `tipo_alerta.csv` (4), `tipo_dano.csv` (7) y `ubigeo_distritos.csv` (1891 distritos del INEI), incluidos en `src/main/resources/datos`, o un archivo propio por catálogo. Valida código único, campos obligatorios y valores permitidos; las filas válidas se guardan y las inválidas se reportan con su motivo. Repetir la importación no duplica datos.

- Ubigeo, tipos de alerta y tipos de daño: UTF-8 separado por comas.
- Distritos: formato del INEI (separado por `;`, Excel/ISO-8859-1 o UTF-8). Lee las columnas `IDDIST`, `NOMBDEP`, `NOMBPROV` y `NOMBDIST`; ignora las filas vacías y las notas al pie. El código de 6 dígitos se separa en departamento (2), provincia (2) y distrito (2); si falta el departamento o la provincia se crean. Con el seed incluido (1874 distritos) agrega los 17 que faltan.

## Distrito en sismos y estaciones
Los formularios de sismos y estaciones tienen un campo **Distrito (opcional)**. Al elegir la ubicación (departamento), el desplegable solo ofrece los distritos activos de ese departamento (`GET /distritos/por-ubicacion/{id}`). El servicio también lo valida: el distrito debe existir, estar activo y pertenecer al departamento de la ubicación.

## Paginación
Los listados de distritos, reportes de daño y niveles de afectación están paginados. Parámetros de la URL: `pagina` (desde 1), `tamano` (10, 20, 50 o 100), `orden` y `dir` (`asc`/`desc`); los listados con filtros (distritos: `q`, `departamentoId`, `provinciaId`, `estado`; reportes: `eventoId`) los conservan al cambiar de página. Solo se puede ordenar por las columnas que cada módulo declara. Para paginar otro listado: `Paginacion.pedido(...)` en el controlador, `PaginaVista` en el modelo y `fragments/paginacion :: barra(${pagina})` en la plantilla (ver `DistritoController` y `distritos/lista.html`).

## Estructura
`controller` → `service` → `repository` (Spring Data JPA) · `web/form` (Bean Validation) · `web/paginacion` (paginación y orden) · `validation` (validadores propios) · `templates` (Thymeleaf) · `static` (CSS/JS).
