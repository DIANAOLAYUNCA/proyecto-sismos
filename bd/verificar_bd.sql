-- Revision SOLO DE LECTURA de la base cssp_sismos (no modifica nada).
-- Ejecutala en MySQL Workbench o con:  mysql -u root -p < verificar_bd.sql
--
-- Si algun requisito sale FALTA, la base tiene el esquema antiguo. Para actualizarla hay que ejecutar
-- cssp_schema_mysql (1).sql y luego cssp_seed_data (1).sql. OJO: el script de esquema BORRA y recrea las
-- tablas (DROP TABLE), asi que se pierden los datos que ya tengas cargados.

USE cssp_sismos;

SELECT 'Ubicacion tiene codigo, nombre, tipo y estado' AS requisito,
       IF((SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'ubicacion'
             AND COLUMN_NAME IN ('codigo', 'nombre', 'tipo', 'estado')) = 4, 'OK', 'FALTA') AS resultado
UNION ALL
SELECT 'Existe la tabla Tipo_Alerta',
       IF((SELECT COUNT(*) FROM information_schema.TABLES
           WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'tipo_alerta') = 1, 'OK', 'FALTA')
UNION ALL
SELECT 'Existe la tabla Tipo_Dano',
       IF((SELECT COUNT(*) FROM information_schema.TABLES
           WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'tipo_dano') = 1, 'OK', 'FALTA')
UNION ALL
SELECT 'Reporte_Dano tiene cantidad_afectada y tipo_dano_id',
       IF((SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'reporte_dano'
             AND COLUMN_NAME IN ('cantidad_afectada', 'tipo_dano_id')) = 2, 'OK', 'FALTA')
UNION ALL
SELECT 'Evento tiene referencia y tipo_alerta_id',
       IF((SELECT COUNT(*) FROM information_schema.COLUMNS
           WHERE TABLE_SCHEMA = DATABASE() AND LOWER(TABLE_NAME) = 'evento'
             AND COLUMN_NAME IN ('referencia', 'tipo_alerta_id')) = 2, 'OK', 'FALTA');

-- Cantidad de filas por tabla (esperado tras el seed: Ubicacion 25, Tipo_Alerta 4, Tipo_Dano 7,
-- Afectacion_Nivel 12, Estacion 2, Evento 2, Reporte_Dano 2; si importas desde la pantalla, 25/4/7).
-- Falla si alguna tabla no existe: en ese caso mira el resultado de arriba.
SELECT 'Ubicacion' AS tabla, COUNT(*) AS filas FROM Ubicacion
UNION ALL SELECT 'Tipo_Alerta', COUNT(*) FROM Tipo_Alerta
UNION ALL SELECT 'Tipo_Dano', COUNT(*) FROM Tipo_Dano
UNION ALL SELECT 'Afectacion_Nivel', COUNT(*) FROM Afectacion_Nivel
UNION ALL SELECT 'Estacion', COUNT(*) FROM Estacion
UNION ALL SELECT 'Evento', COUNT(*) FROM Evento
UNION ALL SELECT 'Reporte_Dano', COUNT(*) FROM Reporte_Dano;
