-- Sistema de Recursos Humanos - IU Digital de Antioquia
-- Esquema normalizado de cinco tablas. Seguro de volver a ejecutar: no elimina datos existentes.
CREATE DATABASE IF NOT EXISTS rrhh_iudigital
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE rrhh_iudigital;

CREATE TABLE IF NOT EXISTS estado_civil (
    id_estado_civil BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_estado_civil),
    UNIQUE KEY uq_estado_civil_nombre (nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tipo_documento (
    id_tipo_documento BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    sigla VARCHAR(12) NOT NULL,
    PRIMARY KEY (id_tipo_documento),
    UNIQUE KEY uq_tipo_documento_nombre (nombre),
    UNIQUE KEY uq_tipo_documento_sigla (sigla)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS funcionarios (
    id_funcionario BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_tipo_documento BIGINT UNSIGNED NOT NULL,
    numero_identificacion VARCHAR(30) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    id_estado_civil BIGINT UNSIGNED NOT NULL,
    sexo VARCHAR(30) NOT NULL,
    direccion VARCHAR(180) NULL,
    telefono VARCHAR(30) NULL,
    fecha_nacimiento DATE NOT NULL,
    PRIMARY KEY (id_funcionario),
    UNIQUE KEY uq_funcionario_documento (id_tipo_documento, numero_identificacion),
    KEY idx_funcionarios_estado_civil (id_estado_civil),
    CONSTRAINT fk_funcionarios_tipo_documento FOREIGN KEY (id_tipo_documento)
      REFERENCES tipo_documento (id_tipo_documento) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_funcionarios_estado_civil FOREIGN KEY (id_estado_civil)
      REFERENCES estado_civil (id_estado_civil) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS grupo_familiar (
    id_familiar BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_funcionario BIGINT UNSIGNED NOT NULL,
    id_tipo_documento BIGINT UNSIGNED NULL,
    numero_identificacion VARCHAR(30) NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    fecha_nacimiento DATE NULL,
    telefono VARCHAR(30) NULL,
    PRIMARY KEY (id_familiar),
    UNIQUE KEY uq_familiar_documento (id_funcionario, id_tipo_documento, numero_identificacion),
    KEY idx_grupo_familiar_funcionario (id_funcionario),
    CONSTRAINT fk_grupo_familiar_funcionario FOREIGN KEY (id_funcionario)
      REFERENCES funcionarios (id_funcionario) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_grupo_familiar_tipo_documento FOREIGN KEY (id_tipo_documento)
      REFERENCES tipo_documento (id_tipo_documento) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_familiar_documento CHECK (
      (id_tipo_documento IS NULL AND numero_identificacion IS NULL) OR
      (id_tipo_documento IS NOT NULL AND numero_identificacion IS NOT NULL)
    )
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS formacion_academica (
    id_formacion BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    id_funcionario BIGINT UNSIGNED NOT NULL,
    universidad VARCHAR(150) NOT NULL,
    nivel_estudio VARCHAR(60) NOT NULL,
    titulo_estudio VARCHAR(180) NOT NULL,
    PRIMARY KEY (id_formacion),
    UNIQUE KEY uq_formacion_funcionario (id_funcionario, universidad, nivel_estudio, titulo_estudio),
    KEY idx_formacion_funcionario (id_funcionario),
    CONSTRAINT fk_formacion_funcionario FOREIGN KEY (id_funcionario)
      REFERENCES funcionarios (id_funcionario) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

-- El script de datos iniciales en 02_insertar_datos_ejemplo.sql agrega los valores de catálogo y la demostración.
