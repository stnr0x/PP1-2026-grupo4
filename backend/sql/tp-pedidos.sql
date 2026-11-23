-- =====================================================================
-- PP1 2026 · Bloque 4 · TP Pedidos de Almuerzo
-- Script de la base de datos: crea la base pp1_pedidos, sus 9 tablas
-- y una carga de datos de prueba que cuenta UNA sola historia.
--
-- Fuente: tp-integradores/tp-pedidos/solucion-base/modelo-de-datos.md
-- Los nombres de tablas y columnas son EXACTAMENTE los del modelo.
--
-- CAMBIOS DEL GRUPO 4 respecto del script del docente
-- (las tablas y columnas NO cambian):
--   1. COLLATE utf8mb4_0900_ai_ci  →  COLLATE utf8mb4_unicode_ci
--      Motivo: usamos XAMPP, que trae MariaDB 10.11, y esa collation solo
--      existe en MySQL 8.
--   2. Contraseñas hasheadas con BCrypt (ver más abajo).
--   3. Datos para la Exposición Final, AGREGADOS después de la historia de
--      octubre (que queda igual): ver "DATOS PARA LA EXPOSICIÓN FINAL".
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
-- CAMBIO DEL GRUPO 4 (devolución E4): las contraseñas van HASHEADAS con
--   BCrypt, nunca en texto plano. Todos los usuarios de prueba tienen la
--   contraseña 'cambiar123'; en la columna se guarda su hash ($2a$10$...).
--   El backend la compara con BCryptPasswordEncoder.matches() en el login.
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
  (1, 'Laura',    'Giménez',   'laura.gimenez@empresa.com.ar',    '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 4567-8901', 'ADMINISTRADOR', TRUE),
  (2, 'Martín',   'Sosa',      'martin.sosa@empresa.com.ar',      '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 5123-4455', 'EMPLEADO',      TRUE),
  (3, 'Lucía',    'Fernández', 'lucia.fernandez@empresa.com.ar',  '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', NULL,           'EMPLEADO',      TRUE),
  (4, 'Diego',    'Romero',    'diego.romero@empresa.com.ar',     '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 6234-7788', 'EMPLEADO',      TRUE),
  (5, 'Carolina', 'Benítez',   'carolina.benitez@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 3345-9012', 'EMPLEADO',      TRUE);

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

-- ---------------------------------------------------------------------
-- DATOS PARA LA EXPOSICIÓN FINAL (agregado por el Grupo 4)
--   Lo de arriba (octubre) queda igual: los cruces del material de clase
--   dependen de esos datos. Esto se suma para que la demo del 01/12 tenga
--   un sistema "con vida":
--   · 6 platos más (12 en total) y 8 empleados más (13 usuarios).
--     Todos entran con la contraseña cambiar123 (guardada como hash BCrypt).
--   · Menús PUBLICADOS de lunes a viernes, con 4 opciones, de las semanas
--     del 23/11, 30/11, 07/12 y 14/12. El 08/12 NO tiene menú: es feriado
--     (dia_no_laborable). La semana del 14/12 es de respaldo: el jueves
--     03/12 a las 12 hs la pantalla del empleado pasa a esa semana.
--   · Asistencia de los 12 empleados para esas 4 semanas (cada uno con
--     sus días habituales).
--   · Pedidos: semanas 23/11 y 30/11 casi todos CONFIRMADOS (+1 CANCELADO
--     cada una, que el consolidado NO cuenta). Semana 07/12: algunos
--     CONFIRMADOS y algunos BORRADOR (todavía se pueden modificar hasta el
--     jueves 03/12 a las 12 hs). La semana del 14/12 queda sin pedidos.
-- ---------------------------------------------------------------------

INSERT INTO plato (id_plato, nombre, descripcion, imagen, categoria) VALUES
  (7, 'Matambre a la pizza', 'Matambre tierno con salsa y muzzarella', 'assets/img/matambre.jpg', 'clásico'),
  (8, 'Pastel de papa', 'Carne picada con puré gratinado', 'assets/img/pasteldepapa.jpg', 'clásico'),
  (9, 'Tortilla de acelga', 'Al horno, con acelga y queso', 'assets/img/tortilladeacelga.jpg', 'vegetariano'),
  (10, 'Ravioles con estofado', 'Ravioles de ricota con salsa de carne', 'assets/img/ravioles.jpg', 'pastas'),
  (11, 'Merluza con puré de calabaza', 'Filet de merluza al horno', 'assets/img/merluza.jpg', 'liviano'),
  (12, 'Guiso de lentejas', 'Lentejas con chorizo y verduras', 'assets/img/guiso.jpg', 'clásico');

INSERT INTO usuario (id_usuario, nombre, apellido, correo, contrasena, telefono, rol, activo) VALUES
  (6, 'Sofía', 'Paz', 'sofia.paz@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 4012-3344', 'EMPLEADO', TRUE),
  (7, 'Javier', 'Molina', 'javier.molina@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 5567-1290', 'EMPLEADO', TRUE),
  (8, 'Valentina', 'Ríos', 'valentina.rios@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', NULL, 'EMPLEADO', TRUE),
  (9, 'Tomás', 'Acosta', 'tomas.acosta@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 6678-2201', 'EMPLEADO', TRUE),
  (10, 'Camila', 'Herrera', 'camila.herrera@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 3390-4567', 'EMPLEADO', TRUE),
  (11, 'Nicolás', 'Domínguez', 'nicolas.dominguez@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 4455-6612', 'EMPLEADO', TRUE),
  (12, 'Agustina', 'Castro', 'agustina.castro@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', NULL, 'EMPLEADO', TRUE),
  (13, 'Federico', 'Luna', 'federico.luna@empresa.com.ar', '$2a$10$nO3nd9Xef7QCysLp7v6qtOhAS.1ZVIwxheX2kQ2u66u5awVXvqt9u', '11 5901-7788', 'EMPLEADO', TRUE);

INSERT INTO menu_dia (id_menu_dia, fecha, descripcion, publicado, id_usuario_creador) VALUES
  (6, '2026-11-23', 'Menú del lunes', TRUE, 1),
  (7, '2026-11-24', 'Menú del martes', TRUE, 1),
  (8, '2026-11-25', 'Menú del miércoles', TRUE, 1),
  (9, '2026-11-26', 'Menú del jueves', TRUE, 1),
  (10, '2026-11-27', 'Menú del viernes', TRUE, 1),
  (11, '2026-11-30', 'Menú del lunes', TRUE, 1),
  (12, '2026-12-01', 'Menú del martes', TRUE, 1),
  (13, '2026-12-02', 'Menú del miércoles', TRUE, 1),
  (14, '2026-12-03', 'Menú del jueves', TRUE, 1),
  (15, '2026-12-04', 'Menú del viernes', TRUE, 1),
  (16, '2026-12-07', 'Menú del lunes', TRUE, 1),
  (17, '2026-12-09', 'Menú del miércoles', TRUE, 1),
  (18, '2026-12-10', 'Menú del jueves', TRUE, 1),
  (19, '2026-12-11', 'Menú del viernes', TRUE, 1),
  (20, '2026-12-14', 'Menú del lunes', TRUE, 1),
  (21, '2026-12-15', 'Menú del martes', TRUE, 1),
  (22, '2026-12-16', 'Menú del miércoles', TRUE, 1),
  (23, '2026-12-17', 'Menú del jueves', TRUE, 1),
  (24, '2026-12-18', 'Menú del viernes', TRUE, 1);

-- stock inicial 20; más abajo se le descuentan las porciones ya pedidas
INSERT INTO menu_plato (id_menu_plato, id_menu_dia, id_plato, stock_disponible) VALUES
  (15, 6, 1, 20),
  (16, 6, 9, 20),
  (17, 6, 5, 20),
  (18, 6, 11, 20),
  (19, 7, 4, 20),
  (20, 7, 2, 20),
  (21, 7, 10, 20),
  (22, 7, 3, 20),
  (23, 8, 7, 20),
  (24, 8, 6, 20),
  (25, 8, 12, 20),
  (26, 8, 11, 20),
  (27, 9, 8, 20),
  (28, 9, 9, 20),
  (29, 9, 5, 20),
  (30, 9, 3, 20),
  (31, 10, 1, 20),
  (32, 10, 2, 20),
  (33, 10, 10, 20),
  (34, 10, 6, 20),
  (35, 11, 4, 20),
  (36, 11, 9, 20),
  (37, 11, 12, 20),
  (38, 11, 11, 20),
  (39, 12, 7, 20),
  (40, 12, 2, 20),
  (41, 12, 5, 20),
  (42, 12, 3, 20),
  (43, 13, 8, 20),
  (44, 13, 6, 20),
  (45, 13, 10, 20),
  (46, 13, 11, 20),
  (47, 14, 1, 20),
  (48, 14, 9, 20),
  (49, 14, 12, 20),
  (50, 14, 3, 20),
  (51, 15, 4, 20),
  (52, 15, 2, 20),
  (53, 15, 5, 20),
  (54, 15, 6, 20),
  (55, 16, 1, 20),
  (56, 16, 9, 20),
  (57, 16, 5, 20),
  (58, 16, 11, 20),
  (59, 17, 7, 20),
  (60, 17, 6, 20),
  (61, 17, 12, 20),
  (62, 17, 11, 20),
  (63, 18, 8, 20),
  (64, 18, 9, 20),
  (65, 18, 5, 20),
  (66, 18, 3, 20),
  (67, 19, 1, 20),
  (68, 19, 2, 20),
  (69, 19, 10, 20),
  (70, 19, 6, 20),
  (71, 20, 4, 20),
  (72, 20, 9, 20),
  (73, 20, 12, 20),
  (74, 20, 11, 20),
  (75, 21, 7, 20),
  (76, 21, 2, 20),
  (77, 21, 5, 20),
  (78, 21, 3, 20),
  (79, 22, 8, 20),
  (80, 22, 6, 20),
  (81, 22, 10, 20),
  (82, 22, 11, 20),
  (83, 23, 1, 20),
  (84, 23, 9, 20),
  (85, 23, 12, 20),
  (86, 23, 3, 20),
  (87, 24, 4, 20),
  (88, 24, 2, 20),
  (89, 24, 5, 20),
  (90, 24, 6, 20);

INSERT INTO asistencia (id_asistencia, id_usuario, fecha, asiste) VALUES
  (17, 2, '2026-11-23', TRUE),
  (18, 2, '2026-11-24', TRUE),
  (19, 2, '2026-11-25', TRUE),
  (20, 2, '2026-11-26', TRUE),
  (21, 2, '2026-11-27', TRUE),
  (22, 3, '2026-11-23', TRUE),
  (23, 3, '2026-11-24', FALSE),
  (24, 3, '2026-11-25', TRUE),
  (25, 3, '2026-11-26', TRUE),
  (26, 3, '2026-11-27', TRUE),
  (27, 4, '2026-11-23', TRUE),
  (28, 4, '2026-11-24', TRUE),
  (29, 4, '2026-11-25', FALSE),
  (30, 4, '2026-11-26', FALSE),
  (31, 4, '2026-11-27', TRUE),
  (32, 5, '2026-11-23', FALSE),
  (33, 5, '2026-11-24', TRUE),
  (34, 5, '2026-11-25', TRUE),
  (35, 5, '2026-11-26', TRUE),
  (36, 5, '2026-11-27', FALSE),
  (37, 6, '2026-11-23', TRUE),
  (38, 6, '2026-11-24', TRUE),
  (39, 6, '2026-11-25', TRUE),
  (40, 6, '2026-11-26', TRUE),
  (41, 6, '2026-11-27', TRUE),
  (42, 7, '2026-11-23', TRUE),
  (43, 7, '2026-11-24', FALSE),
  (44, 7, '2026-11-25', TRUE),
  (45, 7, '2026-11-26', FALSE),
  (46, 7, '2026-11-27', TRUE),
  (47, 8, '2026-11-23', FALSE),
  (48, 8, '2026-11-24', TRUE),
  (49, 8, '2026-11-25', TRUE),
  (50, 8, '2026-11-26', TRUE),
  (51, 8, '2026-11-27', TRUE),
  (52, 9, '2026-11-23', TRUE),
  (53, 9, '2026-11-24', TRUE),
  (54, 9, '2026-11-25', TRUE),
  (55, 9, '2026-11-26', TRUE),
  (56, 9, '2026-11-27', FALSE),
  (57, 10, '2026-11-23', TRUE),
  (58, 10, '2026-11-24', TRUE),
  (59, 10, '2026-11-25', FALSE),
  (60, 10, '2026-11-26', TRUE),
  (61, 10, '2026-11-27', TRUE),
  (62, 11, '2026-11-23', TRUE),
  (63, 11, '2026-11-24', FALSE),
  (64, 11, '2026-11-25', TRUE),
  (65, 11, '2026-11-26', TRUE),
  (66, 11, '2026-11-27', FALSE),
  (67, 12, '2026-11-23', TRUE),
  (68, 12, '2026-11-24', TRUE),
  (69, 12, '2026-11-25', TRUE),
  (70, 12, '2026-11-26', TRUE),
  (71, 12, '2026-11-27', TRUE),
  (72, 13, '2026-11-23', FALSE),
  (73, 13, '2026-11-24', TRUE),
  (74, 13, '2026-11-25', FALSE),
  (75, 13, '2026-11-26', TRUE),
  (76, 13, '2026-11-27', TRUE),
  (77, 2, '2026-11-30', TRUE),
  (78, 2, '2026-12-01', TRUE),
  (79, 2, '2026-12-02', TRUE),
  (80, 2, '2026-12-03', TRUE),
  (81, 2, '2026-12-04', TRUE),
  (82, 3, '2026-11-30', TRUE),
  (83, 3, '2026-12-01', FALSE),
  (84, 3, '2026-12-02', TRUE),
  (85, 3, '2026-12-03', TRUE),
  (86, 3, '2026-12-04', TRUE),
  (87, 4, '2026-11-30', TRUE),
  (88, 4, '2026-12-01', TRUE),
  (89, 4, '2026-12-02', FALSE),
  (90, 4, '2026-12-03', FALSE),
  (91, 4, '2026-12-04', TRUE),
  (92, 5, '2026-11-30', FALSE),
  (93, 5, '2026-12-01', TRUE),
  (94, 5, '2026-12-02', TRUE),
  (95, 5, '2026-12-03', TRUE),
  (96, 5, '2026-12-04', FALSE),
  (97, 6, '2026-11-30', TRUE),
  (98, 6, '2026-12-01', TRUE),
  (99, 6, '2026-12-02', TRUE),
  (100, 6, '2026-12-03', TRUE),
  (101, 6, '2026-12-04', TRUE),
  (102, 7, '2026-11-30', TRUE),
  (103, 7, '2026-12-01', FALSE),
  (104, 7, '2026-12-02', TRUE),
  (105, 7, '2026-12-03', FALSE),
  (106, 7, '2026-12-04', TRUE),
  (107, 8, '2026-11-30', FALSE),
  (108, 8, '2026-12-01', TRUE),
  (109, 8, '2026-12-02', TRUE),
  (110, 8, '2026-12-03', TRUE),
  (111, 8, '2026-12-04', TRUE),
  (112, 9, '2026-11-30', TRUE),
  (113, 9, '2026-12-01', TRUE),
  (114, 9, '2026-12-02', TRUE),
  (115, 9, '2026-12-03', TRUE),
  (116, 9, '2026-12-04', FALSE),
  (117, 10, '2026-11-30', TRUE),
  (118, 10, '2026-12-01', TRUE),
  (119, 10, '2026-12-02', FALSE),
  (120, 10, '2026-12-03', TRUE),
  (121, 10, '2026-12-04', TRUE),
  (122, 11, '2026-11-30', TRUE),
  (123, 11, '2026-12-01', FALSE),
  (124, 11, '2026-12-02', TRUE),
  (125, 11, '2026-12-03', TRUE),
  (126, 11, '2026-12-04', FALSE),
  (127, 12, '2026-11-30', TRUE),
  (128, 12, '2026-12-01', TRUE),
  (129, 12, '2026-12-02', TRUE),
  (130, 12, '2026-12-03', TRUE),
  (131, 12, '2026-12-04', TRUE),
  (132, 13, '2026-11-30', FALSE),
  (133, 13, '2026-12-01', TRUE),
  (134, 13, '2026-12-02', FALSE),
  (135, 13, '2026-12-03', TRUE),
  (136, 13, '2026-12-04', TRUE),
  (137, 2, '2026-12-07', TRUE),
  (138, 2, '2026-12-08', TRUE),
  (139, 2, '2026-12-09', TRUE),
  (140, 2, '2026-12-10', TRUE),
  (141, 2, '2026-12-11', TRUE),
  (142, 3, '2026-12-07', TRUE),
  (143, 3, '2026-12-08', FALSE),
  (144, 3, '2026-12-09', TRUE),
  (145, 3, '2026-12-10', TRUE),
  (146, 3, '2026-12-11', TRUE),
  (147, 4, '2026-12-07', TRUE),
  (148, 4, '2026-12-08', TRUE),
  (149, 4, '2026-12-09', FALSE),
  (150, 4, '2026-12-10', FALSE),
  (151, 4, '2026-12-11', TRUE),
  (152, 5, '2026-12-07', FALSE),
  (153, 5, '2026-12-08', TRUE),
  (154, 5, '2026-12-09', TRUE),
  (155, 5, '2026-12-10', TRUE),
  (156, 5, '2026-12-11', FALSE),
  (157, 6, '2026-12-07', TRUE),
  (158, 6, '2026-12-08', TRUE),
  (159, 6, '2026-12-09', TRUE),
  (160, 6, '2026-12-10', TRUE),
  (161, 6, '2026-12-11', TRUE),
  (162, 7, '2026-12-07', TRUE),
  (163, 7, '2026-12-08', FALSE),
  (164, 7, '2026-12-09', TRUE),
  (165, 7, '2026-12-10', FALSE),
  (166, 7, '2026-12-11', TRUE),
  (167, 8, '2026-12-07', FALSE),
  (168, 8, '2026-12-08', TRUE),
  (169, 8, '2026-12-09', TRUE),
  (170, 8, '2026-12-10', TRUE),
  (171, 8, '2026-12-11', TRUE),
  (172, 9, '2026-12-07', TRUE),
  (173, 9, '2026-12-08', TRUE),
  (174, 9, '2026-12-09', TRUE),
  (175, 9, '2026-12-10', TRUE),
  (176, 9, '2026-12-11', FALSE),
  (177, 10, '2026-12-07', TRUE),
  (178, 10, '2026-12-08', TRUE),
  (179, 10, '2026-12-09', FALSE),
  (180, 10, '2026-12-10', TRUE),
  (181, 10, '2026-12-11', TRUE),
  (182, 11, '2026-12-07', TRUE),
  (183, 11, '2026-12-08', FALSE),
  (184, 11, '2026-12-09', TRUE),
  (185, 11, '2026-12-10', TRUE),
  (186, 11, '2026-12-11', FALSE),
  (187, 12, '2026-12-07', TRUE),
  (188, 12, '2026-12-08', TRUE),
  (189, 12, '2026-12-09', TRUE),
  (190, 12, '2026-12-10', TRUE),
  (191, 12, '2026-12-11', TRUE),
  (192, 13, '2026-12-07', FALSE),
  (193, 13, '2026-12-08', TRUE),
  (194, 13, '2026-12-09', FALSE),
  (195, 13, '2026-12-10', TRUE),
  (196, 13, '2026-12-11', TRUE),
  (197, 2, '2026-12-14', TRUE),
  (198, 2, '2026-12-15', TRUE),
  (199, 2, '2026-12-16', TRUE),
  (200, 2, '2026-12-17', TRUE),
  (201, 2, '2026-12-18', TRUE),
  (202, 3, '2026-12-14', TRUE),
  (203, 3, '2026-12-15', FALSE),
  (204, 3, '2026-12-16', TRUE),
  (205, 3, '2026-12-17', TRUE),
  (206, 3, '2026-12-18', TRUE),
  (207, 4, '2026-12-14', TRUE),
  (208, 4, '2026-12-15', TRUE),
  (209, 4, '2026-12-16', FALSE),
  (210, 4, '2026-12-17', FALSE),
  (211, 4, '2026-12-18', TRUE),
  (212, 5, '2026-12-14', FALSE),
  (213, 5, '2026-12-15', TRUE),
  (214, 5, '2026-12-16', TRUE),
  (215, 5, '2026-12-17', TRUE),
  (216, 5, '2026-12-18', FALSE),
  (217, 6, '2026-12-14', TRUE),
  (218, 6, '2026-12-15', TRUE),
  (219, 6, '2026-12-16', TRUE),
  (220, 6, '2026-12-17', TRUE),
  (221, 6, '2026-12-18', TRUE),
  (222, 7, '2026-12-14', TRUE),
  (223, 7, '2026-12-15', FALSE),
  (224, 7, '2026-12-16', TRUE),
  (225, 7, '2026-12-17', FALSE),
  (226, 7, '2026-12-18', TRUE),
  (227, 8, '2026-12-14', FALSE),
  (228, 8, '2026-12-15', TRUE),
  (229, 8, '2026-12-16', TRUE),
  (230, 8, '2026-12-17', TRUE),
  (231, 8, '2026-12-18', TRUE),
  (232, 9, '2026-12-14', TRUE),
  (233, 9, '2026-12-15', TRUE),
  (234, 9, '2026-12-16', TRUE),
  (235, 9, '2026-12-17', TRUE),
  (236, 9, '2026-12-18', FALSE),
  (237, 10, '2026-12-14', TRUE),
  (238, 10, '2026-12-15', TRUE),
  (239, 10, '2026-12-16', FALSE),
  (240, 10, '2026-12-17', TRUE),
  (241, 10, '2026-12-18', TRUE),
  (242, 11, '2026-12-14', TRUE),
  (243, 11, '2026-12-15', FALSE),
  (244, 11, '2026-12-16', TRUE),
  (245, 11, '2026-12-17', TRUE),
  (246, 11, '2026-12-18', FALSE),
  (247, 12, '2026-12-14', TRUE),
  (248, 12, '2026-12-15', TRUE),
  (249, 12, '2026-12-16', TRUE),
  (250, 12, '2026-12-17', TRUE),
  (251, 12, '2026-12-18', TRUE),
  (252, 13, '2026-12-14', FALSE),
  (253, 13, '2026-12-15', TRUE),
  (254, 13, '2026-12-16', FALSE),
  (255, 13, '2026-12-17', TRUE),
  (256, 13, '2026-12-18', TRUE);

INSERT INTO pedido (id_pedido, id_usuario, fecha_pedido, estado) VALUES
  (5, 2, '2026-11-15', 'CONFIRMADO'),
  (6, 3, '2026-11-16', 'CONFIRMADO'),
  (7, 4, '2026-11-13', 'CANCELADO'),
  (8, 5, '2026-11-14', 'CONFIRMADO'),
  (9, 6, '2026-11-15', 'CONFIRMADO'),
  (10, 7, '2026-11-16', 'CONFIRMADO'),
  (11, 8, '2026-11-13', 'CONFIRMADO'),
  (12, 9, '2026-11-14', 'CONFIRMADO'),
  (13, 10, '2026-11-15', 'CONFIRMADO'),
  (14, 11, '2026-11-16', 'CONFIRMADO'),
  (15, 12, '2026-11-13', 'CONFIRMADO'),
  (16, 13, '2026-11-14', 'CONFIRMADO'),
  (17, 2, '2026-11-22', 'CONFIRMADO'),
  (18, 3, '2026-11-23', 'CONFIRMADO'),
  (19, 4, '2026-11-20', 'CONFIRMADO'),
  (20, 5, '2026-11-21', 'CONFIRMADO'),
  (21, 6, '2026-11-22', 'CONFIRMADO'),
  (22, 7, '2026-11-23', 'CANCELADO'),
  (23, 8, '2026-11-20', 'CONFIRMADO'),
  (24, 9, '2026-11-21', 'CONFIRMADO'),
  (25, 10, '2026-11-22', 'CONFIRMADO'),
  (26, 12, '2026-11-20', 'CONFIRMADO'),
  (27, 13, '2026-11-21', 'CONFIRMADO'),
  (28, 2, '2026-11-29', 'CONFIRMADO'),
  (29, 3, '2026-11-30', 'BORRADOR'),
  (30, 5, '2026-11-28', 'CONFIRMADO'),
  (31, 6, '2026-11-29', 'CONFIRMADO'),
  (32, 8, '2026-11-27', 'BORRADOR'),
  (33, 9, '2026-11-28', 'CONFIRMADO'),
  (34, 10, '2026-11-29', 'BORRADOR'),
  (35, 12, '2026-11-27', 'CONFIRMADO');

INSERT INTO pedido_dia (id_pedido_dia, id_pedido, id_menu_dia, id_plato, fecha_entrega, comentario) VALUES
  (13, 5, 6, 1, '2026-11-23', NULL),
  (14, 5, 7, 3, '2026-11-24', NULL),
  (15, 5, 8, 12, '2026-11-25', NULL),
  (16, 5, 9, 9, '2026-11-26', NULL),
  (17, 5, 10, 1, '2026-11-27', 'Sin queso'),
  (18, 6, 6, 9, '2026-11-23', NULL),
  (19, 6, 8, 6, '2026-11-25', NULL),
  (20, 6, 9, 3, '2026-11-26', NULL),
  (21, 6, 10, 2, '2026-11-27', NULL),
  (22, 7, 6, 1, '2026-11-23', 'Aderezo aparte'),
  (23, 7, 7, 4, '2026-11-24', NULL),
  (24, 7, 10, 1, '2026-11-27', NULL),
  (25, 8, 7, 2, '2026-11-24', NULL),
  (26, 8, 8, 11, '2026-11-25', NULL),
  (27, 8, 9, 9, '2026-11-26', 'Sin cebolla'),
  (28, 9, 6, 1, '2026-11-23', NULL),
  (29, 9, 7, 2, '2026-11-24', NULL),
  (30, 9, 8, 12, '2026-11-25', NULL),
  (31, 9, 9, 3, '2026-11-26', NULL),
  (32, 9, 10, 1, '2026-11-27', 'Porción chica'),
  (33, 10, 6, 11, '2026-11-23', NULL),
  (34, 10, 8, 6, '2026-11-25', NULL),
  (35, 10, 10, 6, '2026-11-27', NULL),
  (36, 11, 7, 3, '2026-11-24', NULL),
  (37, 11, 8, 7, '2026-11-25', 'Sin picante'),
  (38, 11, 9, 9, '2026-11-26', NULL),
  (39, 11, 10, 10, '2026-11-27', NULL),
  (40, 12, 6, 11, '2026-11-23', NULL),
  (41, 12, 7, 3, '2026-11-24', NULL),
  (42, 12, 8, 11, '2026-11-25', 'Sin sal'),
  (43, 12, 9, 3, '2026-11-26', NULL),
  (44, 13, 6, 5, '2026-11-23', NULL),
  (45, 13, 7, 4, '2026-11-24', NULL),
  (46, 13, 9, 8, '2026-11-26', NULL),
  (47, 13, 10, 10, '2026-11-27', 'Sin queso'),
  (48, 14, 6, 9, '2026-11-23', NULL),
  (49, 14, 8, 6, '2026-11-25', NULL),
  (50, 14, 9, 9, '2026-11-26', NULL),
  (51, 15, 6, 5, '2026-11-23', NULL),
  (52, 15, 7, 2, '2026-11-24', 'Aderezo aparte'),
  (53, 15, 8, 7, '2026-11-25', NULL),
  (54, 15, 9, 3, '2026-11-26', NULL),
  (55, 15, 10, 10, '2026-11-27', NULL),
  (56, 16, 7, 10, '2026-11-24', NULL),
  (57, 16, 9, 8, '2026-11-26', 'Sin cebolla'),
  (58, 16, 10, 2, '2026-11-27', NULL),
  (59, 17, 11, 9, '2026-11-30', NULL),
  (60, 17, 12, 7, '2026-12-01', NULL),
  (61, 17, 13, 11, '2026-12-02', NULL),
  (62, 17, 14, 12, '2026-12-03', 'Porción chica'),
  (63, 17, 15, 2, '2026-12-04', NULL),
  (64, 18, 11, 4, '2026-11-30', NULL),
  (65, 18, 13, 8, '2026-12-02', NULL),
  (66, 18, 14, 12, '2026-12-03', NULL),
  (67, 18, 15, 4, '2026-12-04', 'Sin picante'),
  (68, 19, 11, 4, '2026-11-30', NULL),
  (69, 19, 12, 7, '2026-12-01', NULL),
  (70, 19, 15, 4, '2026-12-04', NULL),
  (71, 20, 12, 5, '2026-12-01', NULL),
  (72, 20, 13, 8, '2026-12-02', 'Sin sal'),
  (73, 20, 14, 12, '2026-12-03', NULL),
  (74, 21, 11, 11, '2026-11-30', NULL),
  (75, 21, 12, 7, '2026-12-01', NULL),
  (76, 21, 13, 6, '2026-12-02', NULL),
  (77, 21, 14, 12, '2026-12-03', 'Sin queso'),
  (78, 21, 15, 6, '2026-12-04', NULL),
  (79, 22, 11, 11, '2026-11-30', NULL),
  (80, 22, 13, 6, '2026-12-02', NULL),
  (81, 22, 15, 6, '2026-12-04', NULL),
  (82, 23, 12, 7, '2026-12-01', 'Aderezo aparte'),
  (83, 23, 13, 6, '2026-12-02', NULL),
  (84, 23, 14, 12, '2026-12-03', NULL),
  (85, 23, 15, 6, '2026-12-04', NULL),
  (86, 24, 11, 12, '2026-11-30', NULL),
  (87, 24, 12, 5, '2026-12-01', 'Sin cebolla'),
  (88, 24, 13, 10, '2026-12-02', NULL),
  (89, 24, 14, 12, '2026-12-03', NULL),
  (90, 25, 11, 12, '2026-11-30', NULL),
  (91, 25, 12, 7, '2026-12-01', NULL),
  (92, 25, 14, 1, '2026-12-03', 'Porción chica'),
  (93, 25, 15, 5, '2026-12-04', NULL),
  (94, 26, 11, 9, '2026-11-30', NULL),
  (95, 26, 12, 7, '2026-12-01', NULL),
  (96, 26, 13, 11, '2026-12-02', NULL),
  (97, 26, 14, 12, '2026-12-03', 'Sin picante'),
  (98, 26, 15, 2, '2026-12-04', NULL),
  (99, 27, 12, 5, '2026-12-01', NULL),
  (100, 27, 14, 1, '2026-12-03', NULL),
  (101, 27, 15, 2, '2026-12-04', NULL),
  (102, 28, 16, 5, '2026-12-07', 'Sin sal'),
  (103, 28, 17, 11, '2026-12-09', NULL),
  (104, 28, 18, 5, '2026-12-10', NULL),
  (105, 28, 19, 2, '2026-12-11', NULL),
  (106, 29, 16, 11, '2026-12-07', NULL),
  (107, 29, 17, 7, '2026-12-09', 'Sin queso'),
  (108, 29, 18, 5, '2026-12-10', NULL),
  (109, 29, 19, 1, '2026-12-11', NULL),
  (110, 30, 17, 7, '2026-12-09', NULL),
  (111, 30, 18, 5, '2026-12-10', NULL),
  (112, 31, 16, 5, '2026-12-07', 'Aderezo aparte'),
  (113, 31, 17, 6, '2026-12-09', NULL),
  (114, 31, 18, 5, '2026-12-10', NULL),
  (115, 31, 19, 6, '2026-12-11', NULL),
  (116, 32, 17, 6, '2026-12-09', NULL),
  (117, 32, 18, 5, '2026-12-10', 'Sin cebolla'),
  (118, 32, 19, 6, '2026-12-11', NULL),
  (119, 33, 16, 9, '2026-12-07', NULL),
  (120, 33, 17, 12, '2026-12-09', NULL),
  (121, 33, 18, 5, '2026-12-10', NULL),
  (122, 34, 16, 5, '2026-12-07', 'Porción chica'),
  (123, 34, 18, 8, '2026-12-10', NULL),
  (124, 34, 19, 10, '2026-12-11', NULL),
  (125, 35, 16, 1, '2026-12-07', NULL),
  (126, 35, 17, 11, '2026-12-09', NULL),
  (127, 35, 18, 5, '2026-12-10', 'Sin picante'),
  (128, 35, 19, 2, '2026-12-11', NULL);

-- El stock de cada opción arranca en 20 y baja una porción por cada plato
-- pedido (lo mismo que hace PedidoService al registrar un pedido).
-- Los pedidos CANCELADOS devuelven su porción, por eso no se cuentan.
UPDATE menu_plato mp
SET mp.stock_disponible = 20 - (
  SELECT COUNT(*)
  FROM pedido_dia pd
  JOIN pedido pe ON pe.id_pedido = pd.id_pedido
  WHERE pd.id_menu_dia = mp.id_menu_dia
    AND pd.id_plato = mp.id_plato
    AND pe.estado <> 'CANCELADO'
)
WHERE mp.id_menu_dia >= 6;

-- =====================================================================
-- Verificación — lo esperado está al lado de cada línea.
-- =====================================================================
-- SELECT COUNT(*) FROM usuario;           -- 13
-- SELECT COUNT(*) FROM asistencia;        -- 256
-- SELECT COUNT(*) FROM dia_no_laborable;  -- 3
-- SELECT COUNT(*) FROM menu_dia;          -- 24
-- SELECT COUNT(*) FROM plato;             -- 12
-- SELECT COUNT(*) FROM menu_plato;        -- 90
-- SELECT COUNT(*) FROM pedido;            -- 35
-- SELECT COUNT(*) FROM pedido_dia;        -- 128
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
