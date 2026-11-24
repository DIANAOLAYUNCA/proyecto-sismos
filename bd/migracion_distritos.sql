-- ============================================================
-- Migración: distritos (PA4)
-- Para quien YA tiene la base cargada y no quiere volver a crearla.
-- Quien cargue cssp_schema_mysql.sql desde cero NO necesita este script.
--
-- Ejecutar UNA sola vez, con la base de datos seleccionada (USE cssp_sismos_pa3;).
-- Si una sentencia dice "Duplicate column" o "Duplicate key name", esa parte ya estaba aplicada.
-- ============================================================

-- 1) Distrito: estado (baja lógica) y código único dentro de cada provincia.
ALTER TABLE Distrito
    ADD COLUMN estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO' AFTER codigo;

ALTER TABLE Distrito
    ADD CONSTRAINT chk_distrito_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'));

ALTER TABLE Distrito
    ADD UNIQUE KEY uq_distrito_provincia_codigo (provincia_id, codigo);

-- 2) Estaciones y sismos: distrito opcional (los registros existentes quedan sin distrito).
ALTER TABLE Estacion
    ADD COLUMN distrito_id INT UNSIGNED NULL AFTER ubicacion_id,
    ADD CONSTRAINT fk_estacion_distrito FOREIGN KEY (distrito_id) REFERENCES Distrito(id);

ALTER TABLE Evento
    ADD COLUMN distrito_id INT UNSIGNED NULL AFTER ubicacion_id,
    ADD CONSTRAINT fk_evento_distrito FOREIGN KEY (distrito_id) REFERENCES Distrito(id);

-- 3) Los 17 distritos que faltan en el seed (1874 -> 1891) se cargan desde la aplicación:
--    menú "Importar CSV" -> "Importar archivos incluidos" (o subir ubigeo_distritos.csv en "Distritos").
