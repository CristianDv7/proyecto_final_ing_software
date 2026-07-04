-- Esquema de la base de datos (H2). Se recrea en cada arranque para garantizar
-- un estado limpio (reintento seguro de la inicialización). El orden de DROP
-- respeta las claves foráneas (hijos antes que padres).

DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS horario_atencion;
DROP TABLE IF EXISTS local;
DROP TABLE IF EXISTS comerciante;
DROP TABLE IF EXISTS tipo_negocio;

CREATE TABLE tipo_negocio (
    id     BIGINT       PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE comerciante (
    id             UUID        PRIMARY KEY,
    whatsapp       VARCHAR(20) NOT NULL UNIQUE,
    fecha_registro TIMESTAMP   NOT NULL
);

CREATE TABLE local (
    id                 UUID         PRIMARY KEY,
    nombre             VARCHAR(120) NOT NULL,
    tipo_negocio_id    BIGINT       NOT NULL,
    whatsapp           VARCHAR(20)  NOT NULL,
    latitud            DOUBLE PRECISION NOT NULL,
    longitud           DOUBLE PRECISION NOT NULL,
    origen             VARCHAR(10)  NOT NULL,
    direccion_texto    VARCHAR(255),
    estado_publicacion VARCHAR(15)  NOT NULL,
    comerciante_id     UUID         NOT NULL,
    fecha_creacion     TIMESTAMP    NOT NULL,
    CONSTRAINT fk_local_tipo        FOREIGN KEY (tipo_negocio_id) REFERENCES tipo_negocio(id),
    CONSTRAINT fk_local_comerciante FOREIGN KEY (comerciante_id)  REFERENCES comerciante(id),
    -- FR-012: la unicidad (whatsapp + ubicación) se garantiza a nivel de base de datos.
    -- Bajo solicitudes concurrentes sobre la misma identidad y ubicación, la restricción
    -- hace que solo una prospere (sin doble reserva por condición de carrera) y hace
    -- seguro el reintento (reenviar los mismos datos no crea un duplicado).
    CONSTRAINT uq_local_whatsapp_ubicacion UNIQUE (whatsapp, latitud, longitud)
);

CREATE TABLE horario_atencion (
    local_id         UUID        NOT NULL,
    dia_semana       VARCHAR(3)  NOT NULL,
    hora_apertura    TIME        NOT NULL,
    hora_cierre      TIME        NOT NULL,
    cruza_medianoche BOOLEAN     NOT NULL,
    CONSTRAINT fk_horario_local FOREIGN KEY (local_id) REFERENCES local(id)
);

CREATE TABLE servicio (
    local_id UUID         NOT NULL,
    nombre   VARCHAR(100) NOT NULL,
    CONSTRAINT fk_servicio_local FOREIGN KEY (local_id) REFERENCES local(id)
);
