# CSSP — Sistema de Gestión de Sismos del Perú (Spring Boot)

Aplicación web (Spring Boot 3 · JPA · Thymeleaf · MySQL) para registrar sismos y sus reportes de daño, con catálogos que se pueden importar desde CSV.

## Requisitos
- JDK 21 o superior
- MySQL 8
- Maven (se incluye `mvnw`, no hace falta instalarlo)

## Base de datos
1. Ejecute `bd/cssp_schema_mysql.sql` y luego `bd/cssp_seed_data.sql` en su MySQL. Crean la base `cssp_sismos` (el esquema **borra y recrea** las tablas).
2. `bd/verificar_bd.sql` es una revisión de solo lectura: dice `OK` o `FALTA` por cada requisito del esquema.

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
| `/ubicaciones` | Ubigeo / ubicaciones |
| `/afectacionniveles` | Niveles de afectación |
| `/tipos-alerta` | Tipos de alerta |
| `/tipos-dano` | Tipos de daño |
| `/importacion` | Importación de catálogos desde CSV |

## Importación de CSV
`/importacion` carga `ubigeo.csv` (25), `tipo_alerta.csv` (4) y `tipo_dano.csv` (7), incluidos en `src/main/resources/datos`, o un archivo propio por catálogo (UTF-8, separado por comas). Valida código único, campos obligatorios y valores permitidos; las filas válidas se guardan y las inválidas se reportan con su motivo. Repetir la importación no duplica datos.

## Estructura
`controller` → `service` → `repository` (Spring Data JPA) · `web/form` (Bean Validation) · `validation` (validadores propios) · `templates` (Thymeleaf) · `static` (CSS/JS).
