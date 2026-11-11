-- =====================================================================
-- PP1 2026 · Bloque 4 · TP Pedidos de Almuerzo
-- Script de la base de datos: crea la base pp1_pedidos, sus 9 tablas
-- y una carga de datos de prueba que cuenta UNA sola historia.
--
-- Fuente: tp-integradores/tp-pedidos/solucion-base/modelo-de-datos.md
-- Los nombres de tablas y columnas son EXACTAMENTE los del modelo.
--
-- ÚNICO CAMBIO DEL GRUPO 4 respecto del script del docente:
--   COLLATE utf8mb4_0900_ai_ci  →  COLLATE utf8mb4_unicode_ci
--   Motivo: usamos XAMPP, que trae MariaDB 10.11, y esa collation solo
--   existe en MySQL 8. Las tablas, columnas y datos NO cambian.
--
-- CÓMO CORRERLO
--   phpMyAdmin → Importar este archivo, o desde una terminal:
--   C:\xampp\mysql\bin\mysql.exe -u root < backend/sql/tp-pedidos.sql
--
-- OJO: el script BORRA la base pp1_pedidos y la vuelve a crear.
--   Se puede correr las veces que quieras: siempre deja la base igual,
--   con los mismos datos. Si algo salió mal, correlo de nuevo entero.
--
-- POR QUÉ BIGINT Y VARCHAR (y no INT y ENUM, como dice el diagrama)
--   El backend va a usar spring.jpa.hibernate.ddl-auto=validate:
--   el script crea las tablas y Spring solo CONTROLA que las entidades
--   coincidan. Para que ese control pase sin sorpresas:
--   · Toda PK y toda FK es BIGINT, porque en Java el id es Long.
--   · rol y estado son VARCHAR(20) + CHECK, porque en Java son String.
--   · DECIMAL → BigDecimal · DATE → LocalDate · DATETIME → LocalDateTime
--     · BOOLEAN → Boolean · INT → Integer.
--
-- Contraseñas: van en texto plano ('cambiar123') A PROPÓSITO. En esta
-- etapa no hay login real ni hashing; no uses estas cuentas en nada real.
-- =====================================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS pp1_pedidos;
CREATE DATABASE pp1_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pp1_pedidos;

-- ---------------------------------------------------------------------
-- TABLAS (en orden: primero las que no dependen de nadie)
-- ---------------------------------------------------------------------

-- Usuario: Empleados y Administrador. El restaurante NO es usuario.
CREATE TABLE usuario (
  id_usuario  BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(60)  NOT NULL,
  apellido    VARCHAR(60)  NOT NULL,
  correo      VARCHAR(120) NOT NULL,
  contrasena  VARCHAR(255) NOT NULL,
  telefono    VARCHAR(30),
  rol         VARCHAR(20)  NOT NULL,
  activo      BOOLEAN      NOT NULL,
  PRIMARY KEY (id_usuario),
  CONSTRAINT uk_usuario_correo UNIQUE (correo),
  CONSTRAINT ck_usuario_rol CHECK (rol IN ('EMPLEADO', 'ADMINISTRADOR'))
) ENGINE = InnoDB;

-- Plato: el catálogo de platos. No depende de ninguna tabla.
CREATE TABLE plato (
  id_plato     BIGINT       NOT NULL AUTO_INCREMENT,
  nombre       VARCHAR(120) NOT NULL,
  descripcion  VARCHAR(300),
  imagen       VARCHAR(255),
  categoria    VARCHAR(60)  NOT NULL,
  PRIMARY KEY (id_plato)
) ENGINE = InnoDB;

-- DiaNoLaborable: guarda SOLO los días sin servicio.
-- No tiene FK con menu_dia: "en un día no laborable no hay menú" es una
-- regla de negocio y va en la capa de servicio, no en la base.
CREATE TABLE dia_no_laborable (
  id_dia_no_laborable  BIGINT       NOT NULL AUTO_INCREMENT,
  fecha                DATE         NOT NULL,
  motivo               VARCHAR(120),
  PRIMARY KEY (id_dia_no_laborable),
  CONSTRAINT uk_dia_no_laborable_fecha UNIQUE (fecha)
) ENGINE = InnoDB;

-- Asistencia: qué día viene cada empleado. Es una entidad propia,
-- no un atributo del usuario (la "trampa ASISTENCIA").
CREATE TABLE asistencia (
  id_asistencia  BIGINT  NOT NULL AUTO_INCREMENT,
  id_usuario     BIGINT  NOT NULL,
  fecha          DATE    NOT NULL,
  asiste         BOOLEAN NOT NULL,
  PRIMARY KEY (id_asistencia),
  CONSTRAINT uk_asistencia_usuario_fecha UNIQUE (id_usuario, fecha),
  CONSTRAINT fk_asistencia_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

-- MenuDia: UN menú por fecha (fecha UNIQUE). Sus opciones están en menu_plato.
CREATE TABLE menu_dia (
  id_menu_dia         BIGINT       NOT NULL AUTO_INCREMENT,
  fecha               DATE         NOT NULL,
  descripcion         VARCHAR(200),
  publicado           BOOLEAN      NOT NULL,
  id_usuario_creador  BIGINT       NOT NULL,
  PRIMARY KEY (id_menu_dia),
  CONSTRAINT uk_menu_dia_fecha UNIQUE (fecha),
  CONSTRAINT fk_menu_dia_usuario FOREIGN KEY (id_usuario_creador)
    REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

-- MenuPlato: tabla puente CON atributo propio (stock_disponible) → entidad.
CREATE TABLE menu_plato (
  id_menu_plato     BIGINT NOT NULL AUTO_INCREMENT,
  id_menu_dia       BIGINT NOT NULL,
  id_plato          BIGINT NOT NULL,
  stock_disponible  INT    NOT NULL,
  PRIMARY KEY (id_menu_plato),
  CONSTRAINT fk_menu_plato_menu_dia FOREIGN KEY (id_menu_dia)
    REFERENCES menu_dia (id_menu_dia),
  CONSTRAINT fk_menu_plato_plato FOREIGN KEY (id_plato)
    REFERENCES plato (id_plato)
) ENGINE = InnoDB;

-- Pedido: la cabecera. Nace en BORRADOR y se confirma en un paso aparte.
CREATE TABLE pedido (
  id_pedido     BIGINT      NOT NULL AUTO_INCREMENT,
  id_usuario    BIGINT      NOT NULL,
  fecha_pedido  DATE        NOT NULL,
  estado        VARCHAR(20) NOT NULL,
  PRIMARY KEY (id_pedido),
  CONSTRAINT ck_pedido_estado CHECK (estado IN ('BORRADOR', 'CONFIRMADO', 'CANCELADO')),
  CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

-- PedidoDia: la línea del pedido, una por día. Tabla puente CON atributos
-- (fecha_entrega, comentario) → entidad.
CREATE TABLE pedido_dia (
  id_pedido_dia  BIGINT       NOT NULL AUTO_INCREMENT,
  id_pedido      BIGINT       NOT NULL,
  id_menu_dia    BIGINT       NOT NULL,
  id_plato       BIGINT       NOT NULL,
  fecha_entrega  DATE         NOT NULL,
  comentario     VARCHAR(200),
  PRIMARY KEY (id_pedido_dia),
  CONSTRAINT uk_pedido_dia_pedido_menu_dia UNIQUE (id_pedido, id_menu_dia),
  CONSTRAINT fk_pedido_dia_pedido FOREIGN KEY (id_pedido)
    REFERENCES pedido (id_pedido),
  CONSTRAINT fk_pedido_dia_menu_dia FOREIGN KEY (id_menu_dia)
    REFERENCES menu_dia (id_menu_dia),
  CONSTRAINT fk_pedido_dia_plato FOREIGN KEY (id_plato)
    REFERENCES plato (id_plato)
) ENGINE = InnoDB;

-- Notificacion: FUERA DEL ALCANCE MÍNIMO (no tiene pantalla).
CREATE TABLE notificacion (
  id_notificacion  BIGINT       NOT NULL AUTO_INCREMENT,
  id_usuario       BIGINT       NOT NULL,
  fecha_envio      DATETIME     NOT NULL,
  asunto           VARCHAR(150),
  mensaje          TEXT,
  PRIMARY KEY (id_notificacion),
  CONSTRAINT fk_notificacion_usuario FOREIGN KEY (id_usuario)
    REFERENCES usuario (id_usuario)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- DATOS DE PRUEBA — la historia
--   Laura (Administradora) publicó el menú de lunes a jueves de la semana
--   del 26/10/2026. El viernes 30/10 no hay servicio: cierre por
--   inventario. El menú del lunes 02/11 está cargado pero SIN publicar.
--   Cuatro empleados configuraron su asistencia y armaron su pedido de la
--   semana; Diego todavía no confirmó el suyo (BORRADOR).
-- ---------------------------------------------------------------------

INSERT INTO usuario (id_usuario, nombre, apellido, correo, contrasena, telefono, rol, activo) VALUES
  (1, 'Laura',    'Giménez',   'laura.gimenez@empresa.com.ar',    'cambiar123', '11 4567-8901', 'ADMINISTRADOR', TRUE),
  (2, 'Martín',   'Sosa',      'martin.sosa@empresa.com.ar',      'cambiar123', '11 5123-4455', 'EMPLEADO',      TRUE),
  (3, 'Lucía',    'Fernández', 'lucia.fernandez@empresa.com.ar',  'cambiar123', NULL,           'EMPLEADO',      TRUE),
  (4, 'Diego',    'Romero',    'diego.romero@empresa.com.ar',     'cambiar123', '11 6234-7788', 'EMPLEADO',      TRUE),
  (5, 'Carolina', 'Benítez',   'carolina.benitez@empresa.com.ar', 'cambiar123', '11 3345-9012', 'EMPLEADO',      TRUE);

INSERT INTO plato (id_plato, nombre, descripcion, imagen, categoria) VALUES
  (1, 'Milanesa con puré',        'Clásica, con guarnición',           'assets/img/milanesa.jpg', 'clásico'),
  (2, 'Tarta de verdura',         'Masa casera, acelga y queso',       'assets/img/tarta.jpg',    'vegetariano'),
  (3, 'Ensalada César',           'Con pollo grillado y croutons',     'assets/img/ensalada.jpg', 'liviano'),
  (4, 'Pollo al horno con papas', 'Cuarto de pollo con papas doradas', 'assets/img/pollo.jpg',    'clásico'),
  (5, 'Ñoquis con salsa fileto',  'Ñoquis de papa caseros',            'assets/img/noquis.jpg',   'pastas'),
  (6, 'Wok de vegetales',         'Vegetales salteados con arroz',     'assets/img/wok.jpg',      'vegetariano');

INSERT INTO dia_no_laborable (id_dia_no_laborable, fecha, motivo) VALUES
  (1, '2026-10-30', 'Cierre por inventario'),
  (2, '2026-11-20', 'Día de la Soberanía Nacional'),
  (3, '2026-12-08', 'Inmaculada Concepción de María');

INSERT INTO asistencia (id_asistencia, id_usuario, fecha, asiste) VALUES
  ( 1, 2, '2026-10-26', TRUE),  ( 2, 2, '2026-10-27', TRUE),  ( 3, 2, '2026-10-28', TRUE),  ( 4, 2, '2026-10-29', TRUE),
  ( 5, 3, '2026-10-26', TRUE),  ( 6, 3, '2026-10-27', FALSE), ( 7, 3, '2026-10-28', TRUE),  ( 8, 3, '2026-10-29', TRUE),
  ( 9, 4, '2026-10-26', TRUE),  (10, 4, '2026-10-27', TRUE),  (11, 4, '2026-10-28', FALSE), (12, 4, '2026-10-29', FALSE),
  (13, 5, '2026-10-26', FALSE), (14, 5, '2026-10-27', TRUE),  (15, 5, '2026-10-28', TRUE),  (16, 5, '2026-10-29', TRUE);

INSERT INTO menu_dia (id_menu_dia, fecha, descripcion, publicado, id_usuario_creador) VALUES
  (1, '2026-10-26', 'Menú del lunes',            TRUE,  1),
  (2, '2026-10-27', 'Menú del martes',           TRUE,  1),
  (3, '2026-10-28', 'Menú del miércoles',        TRUE,  1),
  (4, '2026-10-29', 'Menú del jueves',           TRUE,  1),
  (5, '2026-11-02', 'Menú del lunes (borrador)', FALSE, 1);

INSERT INTO menu_plato (id_menu_plato, id_menu_dia, id_plato, stock_disponible) VALUES
  ( 1, 1, 1, 20), ( 2, 1, 2, 15), ( 3, 1, 3, 10),
  ( 4, 2, 4, 20), ( 5, 2, 5, 15), ( 6, 2, 6, 10),
  ( 7, 3, 1, 20), ( 8, 3, 6, 12), ( 9, 3, 3, 10),
  (10, 4, 5, 20), (11, 4, 2, 15), (12, 4, 4, 12),
  (13, 5, 1, 20), (14, 5, 2, 15);

INSERT INTO pedido (id_pedido, id_usuario, fecha_pedido, estado) VALUES
  (1, 2, '2026-10-22', 'CONFIRMADO'),
  (2, 3, '2026-10-22', 'CONFIRMADO'),
  (3, 4, '2026-10-23', 'BORRADOR'),
  (4, 5, '2026-10-23', 'CONFIRMADO');

INSERT INTO pedido_dia (id_pedido_dia, id_pedido, id_menu_dia, id_plato, fecha_entrega, comentario) VALUES
  ( 1, 1, 1, 1, '2026-10-26', 'Sin sal en el puré'),
  ( 2, 1, 2, 4, '2026-10-27', NULL),
  ( 3, 1, 3, 1, '2026-10-28', NULL),
  ( 4, 1, 4, 5, '2026-10-29', NULL),
  ( 5, 2, 1, 2, '2026-10-26', NULL),
  ( 6, 2, 3, 6, '2026-10-28', 'Sin picante'),
  ( 7, 2, 4, 2, '2026-10-29', NULL),
  ( 8, 3, 1, 1, '2026-10-26', NULL),
  ( 9, 3, 2, 5, '2026-10-27', 'Queso rallado aparte'),
  (10, 4, 2, 4, '2026-10-27', NULL),
  (11, 4, 3, 1, '2026-10-28', NULL),
  (12, 4, 4, 5, '2026-10-29', 'Porción chica');

INSERT INTO notificacion (id_notificacion, id_usuario, fecha_envio, asunto, mensaje) VALUES
  (1, 2, '2026-10-22 10:15:00', 'Pedido confirmado', 'Tu pedido de la semana del 26/10 quedó confirmado.');

-- =====================================================================
-- Verificación — lo esperado está al lado de cada línea.
-- =====================================================================
-- SELECT COUNT(*) FROM usuario;           -- 5
-- SELECT COUNT(*) FROM asistencia;        -- 16
-- SELECT COUNT(*) FROM dia_no_laborable;  -- 3
-- SELECT COUNT(*) FROM menu_dia;          -- 5
-- SELECT COUNT(*) FROM plato;             -- 6
-- SELECT COUNT(*) FROM menu_plato;        -- 14
-- SELECT COUNT(*) FROM pedido;            -- 4
-- SELECT COUNT(*) FROM pedido_dia;        -- 12
-- SELECT COUNT(*) FROM notificacion;      -- 1

-- Cruce 2 · El consolidado de una fecha → pantalla Consolidado
-- Esperado (2026-10-28): Milanesa con puré 2, Wok de vegetales 1.
-- SELECT p.nombre AS plato, COUNT(*) AS porciones
-- FROM pedido_dia pd
-- JOIN pedido pe ON pe.id_pedido = pd.id_pedido
-- JOIN plato p   ON p.id_plato   = pd.id_plato
-- WHERE pd.fecha_entrega = '2026-10-28'
--   AND pe.estado = 'CONFIRMADO'
-- GROUP BY p.id_plato, p.nombre
-- ORDER BY porciones DESC, p.nombre;
