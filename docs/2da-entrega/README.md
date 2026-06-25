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
| 05 |Generar Consolidado |Administrador |CU-14 |Permite al Administrador generar el consolidado de los pedidos confirmados de la semana para enviarlo al proveedor. |

---

## 2. Trazabilidad pantalla ↔ E1

| Pantalla | CU(s) | HU(s) | Actor |
|----------|-------|-------|-------|
| 01 — Login | CU-01, CU-02 | HU-05, HU-06 | Empleado / Administrador |
| 02 — Menu semanal y pedido | CU-05, CU-06, CU-07, CU-08 | HU-07, HU-08, HU-09, HU-10 | Empleado |
| 03 — Historial de Pedidos | CU-11 | HU-04 | Empleado |
| 04 — Mis Pedidos | CU-06, CU-07, CU-12 | HU-09, HU-10 | Empleado |
| 05 — Generar Consolidado | CU-14|HU-15 |Administrador |

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

### Pantalla 03 — Menu + Registrar pedido
-Se incorporo la indicacion visible del plazo limite para realizar pedidos de cada dia, con el objetivo de evitar solicitudes fuera del horario permitido y brindar claridad al usuario sobre la disponibilidad del servicio.
-Se implemento una validacion previa de asistencia del empleado. En caso de que no haya configurado su asistencia, se muestra el mensaje: "Configura tu asistencia para ver el menu", evitando que el usuario avance en el proceso sin cumplir este requisito.
-Cada opción del menu incluye un indicador de stock o disponibilidad, permitiendo al usuario conocer en tiempo real que productos pueden ser solicitados y reduciendo errores durante el registro del pedido.
-Se decidio mostrar el precio individual de cada opción junto con un total estimado del pedido, proporcionando transparencia en los costos y facilitando la toma de decisiones antes de confirmar la compra.
-La informacion de disponibilidad y precios se actualiza dinamicamente segun los datos registrados en el sistema, garantizando consistencia entre el menu mostrado y el pedido generado.
-Se priorizo una interfaz clara y sencilla, concentrando en una misma pantalla toda la informacion necesaria para que el usuario pueda seleccionar productos y registrar su pedido de forma rápida y eficiente.

### Pantalla 04 — Mis Pedidos
- Se eligió una lista vertical de pedidos (un article por pedido) en lugar de grilla, porque cada pedido tiene contenido variable (acciones según estado) y la lista facilita la lectura secuencial.
- Los botones de acción se ubicaron alineados a la derecha de cada pedido con Flexbox.
- Se muestran dos estados: "Borrador" (badge con borde punteado) y "Confirmado" (badge sólido relleno), diferenciados visualmente por color y estilo de borde.
- Los pedidos en borrador muestran botones de Confirmar, Modificar y Cancelar; los confirmados solo Modificar y Cancelar.
- El pedido de la semana del 02/06 al 06/06 reaparece como Borrador, coherente con lo registrado en la pantalla 02 (Menú semanal y pedido).
- Cubre las funcionalidades de ver pedidos, confirmar pedido y modificar/cancelar pedido (CU-06, CU-07, CU-12 — verificar IDs exactos en E1).
- No se incluyó el detalle de platos por pedido en esta pantalla para mantenerla simple; ese detalle se concentra en la pantalla 03 (Historial de Pedidos).

### Pantalla 05 — Generar Consolidado
- Esta es la única pantalla del rol Administrador, por lo que se diferenció claramente del resto mediante un navbar específico (“Panel del Administrador”) y un diseño más orientado a la visualización de datos.
- Se optó por un layout simple con una tabla de pedidos consolidados y una sección de totales, priorizando la claridad y facilidad de lectura.
- Se mantuvo coherencia con los pedidos confirmados que podrían venir de la Pantalla 04, y se utilizó el botón “Enviar al proveedor” para reflejar correctamente el flujo del sistema.
- Se dejaron afuera componentes opcionales (como filtros avanzados) para mantener la pantalla simple y cumplir con lo requerido.
