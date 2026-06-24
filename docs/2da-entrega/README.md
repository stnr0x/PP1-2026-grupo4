# Entrega 2 — Diseño de Interfaces
**Grupo**:  Grupo 4
**Proyecto**: Sistema de pedidos de almuerzo
**Fecha de entrega**: 25/06/2026

---

## 1. Inventario de pantallas troncales

| N° | Nombre | Actor principal | CU(s) cubierto(s) | Función (1 frase) |
|----|--------|-----------------|-------------------|-------------------|
| 01 | Login | Empleado / Administrador | CU-01, CU-02 | Permite al usuario autenticarse con email y contraseña para acceder al sistema. |
| 02 | Menu Semanal y Pedido | Empleado | CU-05, CU-06, CU-07, CU-09 | Permite al empleado visualizar el menú semanal filtrado por sus días de asistencia, registrar su pedido como borrador, modificarlo y confirmarlo definitivamente. |
| 03 | Historial de Pedidos | Empleado | CU-11 | Permite al empleado consultar sus pedidos realizados en semanas anteriores, ver su estado y detalle de opciones seleccionadas. |
| 04 | Mis Pedidos | Empleado | CU-06, CU-07, CU-12 | Permite al empleado gestionar sus pedidos activos: confirmar borradores, modificarlos o cancelarlos. |
| 05 | | | | |

---

## 2. Trazabilidad pantalla ↔ E1

| Pantalla | CU(s) | HU(s) | Actor |
|----------|-------|-------|-------|
| 01 — Login | CU-01, CU-02 | HU-05, HU-06 | Empleado / Administrador |
| 02 — Menu semanal y pedido | CU-05, CU-06, CU-07, CU-08 | HU-07, HU-08, HU-09, HU-10 | Empleado |
| 03 — Historial de Pedidos | CU-11 | HU-04 | Empleado |
| 04 — Mis Pedidos | CU-06, CU-07, CU-12 | HU-09, HU-10 | Empleado |
| 05 — [Nombre] | | | |

---

## 3. Decisiones técnicas y observaciones

> Documentar acá las decisiones de diseño y desarrollo del grupo, organizadas por pantalla.
> Esta sección es clave para la defensa oral del 25/06.

### Pantalla 01 — Login
- Se eligió una paleta oscura para una apariencia moderna y reducir fatiga visual.
- El formulario cuenta con los respectivos campos de email y contraseña para colocar credenciales.
- Tiene tambien un checkbox de "Recordarme" para mantener la sesión.
- Cuenta con un enlace "¿Olvidaste tu contraseña?", para recuperar el acceso a la plataforma.
- Un botón para el inicio de sesión.
- Un texto de "¿No tenés cuenta?", con un botón para registrarse.

### Pantalla 02 — Menu Semanal y Pedido
- Se muestra el menu filtrado unicamente por los dias de asistencia configurados por el empleado (RF-06).
- Los dias feriados aparecen bloqueados visualmente, sin posibilidad de seleccion (CU-10).
- El pedido se guarda primero como borrador (CU-06) y puede modificarse hasta el limite (CU-07).
- Un boton de "Confirmar pedido" cierra el flujo y dispara la notificacion por correo (CU-09).
- Se usa una estructura de cards por dia para facilitar la lectura.
- Layout con barra de navegación de semanas (flechas ‹ ›) para seleccionar la semana a configurar.
- Se eligió cards con checkbox por día porque permite ver de un vistazo qué días están marcados.
- Las cards marcadas tienen borde sólido y las no marcadas borde punteado para diferenciación visual.
- Se agregó indicación del estado actual de configuración para que el empleado vea su selección antes de modificar.
- Se incluyó nota explicativa que vincula esta pantalla con el flujo.

### Pantalla 03 — Historial de Pedidos
- Se muestra una lista de pedidos anteriores agrupados por semana.
- Cada fila indica la semana, el estado del pedido (borrador / confirmado / cancelado) y un boton para ver el detalle.
- El detalle de cada pedido muestra las opciones seleccionadas por dia.
- Si no hay pedidos previos, se muestra un mensaje informativo.

### Pantalla 04 — Mis Pedidos
- Se eligió una lista vertical de pedidos (un article por pedido) en lugar de grilla, porque cada pedido tiene contenido variable (acciones según estado) y la lista facilita la lectura secuencial.
- Los botones de acción se ubicaron alineados a la derecha de cada pedido con Flexbox.
- Se muestran dos estados: "Borrador" (badge con borde punteado) y "Confirmado" (badge sólido relleno), diferenciados visualmente por color y estilo de borde.
- Los pedidos en borrador muestran botones de Confirmar, Modificar y Cancelar; los confirmados solo Modificar y Cancelar.
- El pedido de la semana del 02/06 al 06/06 reaparece como Borrador, coherente con lo registrado en la pantalla 02 (Menú semanal y pedido).
- Cubre las funcionalidades de ver pedidos, confirmar pedido y modificar/cancelar pedido (CU-06, CU-07, CU-12 — verificar IDs exactos en E1).
- No se incluyó el detalle de platos por pedido en esta pantalla para mantenerla simple; ese detalle se concentra en la pantalla 03 (Historial de Pedidos).

### Pantalla 05 — [Nombre]
- (Pendiente)