-- ============================================================
-- Sistema de Seguimiento y Gestión de Sismos del Perú (CSSP)
-- ============================================================

CREATE DATABASE IF NOT EXISTS cssp_sismos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE cssp_sismos;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- Seguridad y usuarios
-- ============================================================

DROP TABLE IF EXISTS Rol;
CREATE TABLE Rol (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(50)  NOT NULL,
    codigo                VARCHAR(20)  NOT NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_rol_codigo (codigo)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS Modulo;
CREATE TABLE Modulo (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(50)  NOT NULL,
    codigo                VARCHAR(20)  NOT NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_modulo_codigo (codigo)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS Rol_Modulo_Permiso;
CREATE TABLE Rol_Modulo_Permiso (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    rol_id                INT UNSIGNED NOT NULL,
    modulo_id             INT UNSIGNED NOT NULL,
    puede_crear           BOOLEAN      NOT NULL DEFAULT FALSE,
    puede_leer            BOOLEAN      NOT NULL DEFAULT FALSE,
    puede_actualizar      BOOLEAN      NOT NULL DEFAULT FALSE,
    puede_eliminar        BOOLEAN      NOT NULL DEFAULT FALSE,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    CONSTRAINT fk_rmp_rol    FOREIGN KEY (rol_id)    REFERENCES Rol(id),
    CONSTRAINT fk_rmp_modulo FOREIGN KEY (modulo_id) REFERENCES Modulo(id),
    UNIQUE KEY uq_rol_modulo (rol_id, modulo_id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS Usuario;
CREATE TABLE Usuario (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombres               VARCHAR(100) NOT NULL,
    apellidos             VARCHAR(100) NOT NULL,
    email                 VARCHAR(150) NOT NULL,
    username              VARCHAR(50)  NOT NULL,
    password_hash         VARCHAR(255) NOT NULL,
    rol_id                INT UNSIGNED NOT NULL,
    estado                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_usuario_email (email),
    UNIQUE KEY uq_usuario_username (username),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES Rol(id),
    CONSTRAINT chk_usuario_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
) ENGINE=InnoDB;

-- ============================================================
-- Jerarquía geográfica (Departamento -> Provincia -> Distrito)
-- ============================================================

DROP TABLE IF EXISTS Departamento;
CREATE TABLE Departamento (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(100) NOT NULL,
    codigo                VARCHAR(10)  NOT NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_departamento_codigo (codigo)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS Provincia;
CREATE TABLE Provincia (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    departamento_id       INT UNSIGNED NOT NULL,
    nombre                VARCHAR(100) NOT NULL,
    codigo                VARCHAR(10)  NOT NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    CONSTRAINT fk_provincia_departamento FOREIGN KEY (departamento_id) REFERENCES Departamento(id)
) ENGINE=InnoDB;

DROP TABLE IF EXISTS Distrito;
CREATE TABLE Distrito (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    provincia_id          INT UNSIGNED NOT NULL,
    nombre                VARCHAR(100) NOT NULL,
    codigo                VARCHAR(10)  NOT NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    CONSTRAINT fk_distrito_provincia FOREIGN KEY (provincia_id) REFERENCES Provincia(id)
) ENGINE=InnoDB;

-- Ubigeo (en este proyecto se llama Ubicacion)
-- provincia_id y distrito_id quedan opcionales (sin usar)
DROP TABLE IF EXISTS Ubicacion;
CREATE TABLE Ubicacion (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo                VARCHAR(10)  NOT NULL,
    nombre                VARCHAR(100) NOT NULL,
    tipo                  VARCHAR(30)  NOT NULL,
    estado                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    provincia_id          INT UNSIGNED NULL,
    distrito_id           INT UNSIGNED NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_ubicacion_codigo (codigo),
    CONSTRAINT fk_ubicacion_provincia FOREIGN KEY (provincia_id) REFERENCES Provincia(id),
    CONSTRAINT fk_ubicacion_distrito  FOREIGN KEY (distrito_id)  REFERENCES Distrito(id),
    CONSTRAINT chk_ubicacion_tipo   CHECK (tipo IN ('DEPARTAMENTO', 'PROVINCIA_CONSTITUCIONAL')),
    CONSTRAINT chk_ubicacion_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
) ENGINE=InnoDB;

-- ============================================================
-- Estaciones de monitoreo
-- ============================================================

DROP TABLE IF EXISTS Estacion;
CREATE TABLE Estacion (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo                VARCHAR(20)   NOT NULL,
    nombre                VARCHAR(150)  NOT NULL,
    ubicacion_id          INT UNSIGNED  NOT NULL,
    latitud               DECIMAL(9,6)  NOT NULL,
    longitud              DECIMAL(9,6)  NOT NULL,
    instalacion_fecha     DATE          NOT NULL,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'ACTIVA',
    creacion_fecha        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100)  NOT NULL,
    actualizacion_fecha   DATETIME      NULL      ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100)  NULL,
    UNIQUE KEY uq_estacion_codigo (codigo),
    CONSTRAINT fk_estacion_ubicacion FOREIGN KEY (ubicacion_id) REFERENCES Ubicacion(id),
    CONSTRAINT chk_estacion_estado  CHECK (estado IN ('ACTIVA', 'INACTIVA')),
    CONSTRAINT chk_estacion_lat     CHECK (latitud  BETWEEN -90  AND 90),
    CONSTRAINT chk_estacion_long    CHECK (longitud BETWEEN -180 AND 180)
) ENGINE=InnoDB;

-- ============================================================
-- Catálogos de afectación
-- ============================================================

DROP TABLE IF EXISTS Afectacion_Nivel;
CREATE TABLE Afectacion_Nivel (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre                VARCHAR(100) NOT NULL,
    codigo                VARCHAR(20)  NOT NULL,
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_afectacion_nivel_codigo (codigo)
) ENGINE=InnoDB;

-- ============================================================
-- Tipo de alerta (nueva)
-- ============================================================
DROP TABLE IF EXISTS Tipo_Alerta;
CREATE TABLE Tipo_Alerta (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo                VARCHAR(20)  NOT NULL,
    nombre                VARCHAR(100) NOT NULL,
    color                 VARCHAR(20)  NOT NULL,
    prioridad             INT UNSIGNED NOT NULL,
    descripcion           VARCHAR(255) NULL,
    estado                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_tipo_alerta_codigo (codigo),
    CONSTRAINT chk_tipo_alerta_prioridad CHECK (prioridad > 0),
    CONSTRAINT chk_tipo_alerta_color     CHECK (color IN ('VERDE', 'AMARILLO', 'NARANJA', 'ROJO')),
    CONSTRAINT chk_tipo_alerta_estado    CHECK (estado IN ('ACTIVO', 'INACTIVO'))
) ENGINE=InnoDB;

-- ============================================================
-- Tipo de daño (antes Afectacion_Tipo)
-- ============================================================
DROP TABLE IF EXISTS Afectacion_Tipo;
DROP TABLE IF EXISTS Tipo_Dano;
CREATE TABLE Tipo_Dano (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    codigo                VARCHAR(20)  NOT NULL,
    nombre                VARCHAR(100) NOT NULL,
    sector                VARCHAR(30)  NOT NULL,
    descripcion           VARCHAR(255) NULL,
    estado                VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    UNIQUE KEY uq_tipo_dano_codigo (codigo),
    CONSTRAINT chk_tipo_dano_sector CHECK (sector IN ('SALUD', 'VIVIENDA', 'EDUCACION', 'INFRAESTRUCTURA', 'SERVICIOS_BASICOS', 'AGROPECUARIO', 'POBLACION')),
    CONSTRAINT chk_tipo_dano_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
) ENGINE=InnoDB;

-- ============================================================
-- Evento sísmico
-- ============================================================

DROP TABLE IF EXISTS Evento;
CREATE TABLE Evento (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    estacion_id           INT UNSIGNED  NOT NULL,
    codigo                VARCHAR(30)   NOT NULL,
    fecha_hora            DATETIME      NOT NULL,
    magnitud              DECIMAL(4,2)  NOT NULL,
    profundidad           DECIMAL(6,2)  NOT NULL,
    latitud               DECIMAL(9,6)  NOT NULL,
    longitud              DECIMAL(9,6)  NOT NULL,
    referencia            VARCHAR(255)  NOT NULL,
    ubicacion_id          INT UNSIGNED  NOT NULL,
    tipo_alerta_id        INT UNSIGNED  NOT NULL,
    afectacion_nivel_id   INT UNSIGNED  NULL,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'REGISTRADO',
    creacion_fecha        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100)  NOT NULL,
    actualizacion_fecha   DATETIME      NULL      ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100)  NULL,
    UNIQUE KEY uq_evento_codigo (codigo),
    CONSTRAINT fk_evento_estacion         FOREIGN KEY (estacion_id)         REFERENCES Estacion(id),
    CONSTRAINT fk_evento_ubicacion        FOREIGN KEY (ubicacion_id)        REFERENCES Ubicacion(id),
    CONSTRAINT fk_evento_tipo_alerta      FOREIGN KEY (tipo_alerta_id)      REFERENCES Tipo_Alerta(id),
    CONSTRAINT fk_evento_afectacion_nivel FOREIGN KEY (afectacion_nivel_id) REFERENCES Afectacion_Nivel(id),
	CONSTRAINT chk_evento_estado    CHECK (estado IN ('REGISTRADO', 'EN_EVALUACION', 'EN_SEGUIMIENTO', 'CERRADO', 'INACTIVO')),
    CONSTRAINT chk_evento_magnitud  CHECK (magnitud    > 0),
    CONSTRAINT chk_evento_profund   CHECK (profundidad >= 0),
    CONSTRAINT chk_evento_lat       CHECK (latitud  BETWEEN -90  AND 90),
    CONSTRAINT chk_evento_long      CHECK (longitud BETWEEN -180 AND 180)
) ENGINE=InnoDB;

ALTER TABLE Evento DROP CHECK chk_evento_estado;
ALTER TABLE Evento ADD CONSTRAINT chk_evento_estado
    CHECK (estado IN ('REGISTRADO', 'EN_EVALUACION', 'EN_SEGUIMIENTO', 'CERRADO', 'INACTIVO'));

-- ============================================================
-- Seguimiento del evento
-- ============================================================

DROP TABLE IF EXISTS Seguimiento;
CREATE TABLE Seguimiento (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    evento_id             INT UNSIGNED NOT NULL,
    observaciones         TEXT         NULL,
    responsable_id        INT UNSIGNED NOT NULL,
    acciones              TEXT         NULL,
    estado                VARCHAR(20)  NOT NULL DEFAULT 'REGISTRADO',
    creacion_fecha        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100) NOT NULL,
    actualizacion_fecha   DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100) NULL,
    CONSTRAINT fk_seguimiento_evento      FOREIGN KEY (evento_id)      REFERENCES Evento(id),
    CONSTRAINT fk_seguimiento_responsable FOREIGN KEY (responsable_id) REFERENCES Usuario(id),
    CONSTRAINT chk_seguimiento_estado CHECK (estado IN ('REGISTRADO', 'EN_EVALUACION', 'EN_SEGUIMIENTO', 'CERRADO'))
) ENGINE=InnoDB;

-- ============================================================
-- Reporte de afectación
-- ============================================================

DROP TABLE IF EXISTS Reporte;
DROP TABLE IF EXISTS Reporte_Dano;
CREATE TABLE Reporte_Dano (
    id                    INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    evento_id             INT UNSIGNED  NOT NULL,
    ubicacion_id          INT UNSIGNED  NOT NULL,
    fecha_hora            DATETIME      NOT NULL,
    afectacion_nivel_id   INT UNSIGNED  NULL,
    tipo_dano_id          INT UNSIGNED  NOT NULL,
    descripcion           TEXT          NULL,
    cantidad_afectada     INT UNSIGNED  NOT NULL DEFAULT 0,
    estado                VARCHAR(20)   NOT NULL DEFAULT 'REGISTRADO',
    creacion_fecha        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario      VARCHAR(100)  NOT NULL,
    actualizacion_fecha   DATETIME      NULL      ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario VARCHAR(100)  NULL,
    CONSTRAINT fk_reporte_dano_evento           FOREIGN KEY (evento_id)           REFERENCES Evento(id),
    CONSTRAINT fk_reporte_dano_ubicacion        FOREIGN KEY (ubicacion_id)        REFERENCES Ubicacion(id),
    CONSTRAINT fk_reporte_dano_afectacion_nivel FOREIGN KEY (afectacion_nivel_id) REFERENCES Afectacion_Nivel(id),
    CONSTRAINT fk_reporte_dano_tipo_dano        FOREIGN KEY (tipo_dano_id)        REFERENCES Tipo_Dano(id),
    CONSTRAINT chk_reporte_dano_estado CHECK (estado IN ('REGISTRADO', 'VERIFICADO', 'DESCARTADO'))
) ENGINE=InnoDB;

-- ============================================================
-- Importación masiva de históricos (procesamiento concurrente)
-- ============================================================

DROP TABLE IF EXISTS Importacion_Historico;
CREATE TABLE Importacion_Historico (
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    usuario_id             INT UNSIGNED NOT NULL,
    nombre_archivo         VARCHAR(255) NOT NULL,
    fecha_inicio           DATETIME     NOT NULL,
    fecha_fin              DATETIME     NULL,
    total_registros        INT UNSIGNED NOT NULL DEFAULT 0,
    registros_procesados   INT UNSIGNED NOT NULL DEFAULT 0,
    registros_error        INT UNSIGNED NOT NULL DEFAULT 0,
    estado                 VARCHAR(20)  NOT NULL DEFAULT 'EN_PROCESO',
    detalle_errores        TEXT         NULL,
    creacion_fecha         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    creacion_usuario       VARCHAR(100) NOT NULL,
    actualizacion_fecha    DATETIME     NULL     ON UPDATE CURRENT_TIMESTAMP,
    actualizacion_usuario  VARCHAR(100) NULL,
    CONSTRAINT fk_importacion_usuario FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
    CONSTRAINT chk_importacion_estado CHECK (estado IN ('EN_PROCESO', 'COMPLETADO', 'CON_ERRORES'))
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;
