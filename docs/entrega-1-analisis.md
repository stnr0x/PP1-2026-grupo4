# Entrega 1 — Análisis del Sistema

**Grupo**: [Grupo4]  
**Proyecto**: [Sistema de pedidos de almuerzo]  
**Fecha de entrega**: 30/04/2026

---

## 1. Identificación de Actores

| Actor | Rol / Función en el sistema | Tipo (usuario final, sistema externo, etc.) |
|-------|-----------------------------|---------------------------------------------|
|Empleado|Realizar y editar pedidos   |     -Usuario final| 
|Administrador|Publicar menu, enviar recordatorios y resolver problemas|  - Administrador   |           
|Servicio correo externo|Recibe y envia mails de estado|   -Sistema externo |
## 2. Requisitos Funcionales

| ID    | Descripción | Actor | HU relacionada |
|-------|-------------|-------|----------------|
| RF-01 |  El sistema debe permitir que un usuario pueda registrarse  | Empleado | HU-08|
| RF-02 |  El sistema debe permitir que un usuario pueda iniciar sesion | Empleado/Administrador | HU-09|
| RF-03 | El sistema debe permitir que un usuario pueda editar su perfil| Empleado |    HU-01            |
| RF-04 | El sistema debe permitir que el administrador asigne roles a los usuarios | Administrador |  HU-15        |
| RF-05 | El sistema debe permitir que los empleados puedan configurar su asistencia laboral semanal | Empleado| HU-10 |
| RF-06 | El sistema debe permitir mostrar las opciones de menú semanal disponibles, filtrando únicamente los días configurados como días de asistencia del empleado | Empleado |HU-10 |
| RF-07 | El sistema debe permitir al usuario seleccionar, modificar o cancelar su pedido | Empleado |HU-11 |
| RF-08 | El sistema debe permitir que el usuario pueda confirmar su pedido de almuerzo | Empleado | HU-11 + HU-04         |
| RF-09 | El sistema debe permitir que al confirmar un pedido se envie una notificación al administrador | Sistema correo externo | HU-04         |
| RF-10 | El sistema debe permitir que el administrador cree, modifique o elimine los menús semanales | Administrador |  HU-02 + HU-03       |
| RF-11 | El sistema debe permitir enviar recordatorios automáticos por correo a los empleados que no hayan completado su selección antes del límite | Sistema correo externo |HU-12 |
| RF-12 | El sistema debe permitir al administrador definir una fecha y hora límite para confirmar pedidos | Administrador |HU-13 |
| RF-13 | El sistema debe permitir al administrador marcar dias feriados para bloquear pedidos en esas fechas | Administrador |HU-14 |
| RF-14 | El sistema debe permitir que el empleado consulte su historial de pedidos | Empleado | HU-05 |
| RF-15 | El sistema debe permitir notificar al empleado cuando su pedido esté en camino o listo para retirar | Sistema correo externo |HU-06 |
> Cada requisito debe describir una acción concreta: "El sistema debe permitir que [actor] [acción]..."

## 3. Requisitos No Funcionales

| ID     | Categoría (rendimiento, seguridad, usabilidad, etc.) | Descripción                                                                                                              |
|--------|-----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| RNF-01 | Usabilidad                                          | El sistema debe tener una interfaz clara, intuitiva y facil de usar tanto como para empleados como para administradores. |
| RNF-02 | Usabilidad                                          | La interfaz  debe adaptarse a diferentes tamaños de pantalla, pc de escritorio, celulares, tablets, etc.                 |
| RNF-03 | Rendimiento                                         | El tiempo de respuesta de las principales funciones no debe superar los 3 segundos en condiciones normales.              |
| RNF-04 | Seguridad                                           | El sistema debe tener autenticacion y autorizacion segun rol del usuario (administrador / empleado).                     |
| RNF-05 | Seguridad                                           | Credenciales cifradas  HTTPS.                                                                                            |
| RNF-06 | Compatibilidad                                      | La pagina debe funcionar correctamente en los navegadores modernos mas utilizados.                                       |
| RNF-07 | Integracion Externa                                 | El sistema debe  integrarse correctamente con un servicio de correos externo.                                            |
| RNF-08 | fiabilidad integracion externa                      | Los envios de correo deben ser confiables, en caso de fallo temporal el sistema debe reintentar el envio.                |

## 4. Historias de Usuario

| ID    | Como...       | Quiero...                  | Para...                            |
|-------|---------------|----------------------------|------------------------------------|
| HU-01 | [Empleado]   | [Editar mi perfil]   | [Mantener mi informacion actualizada]  | 
| HU-02 |  [Administrador] |  [Crear y publicar el menú semanal con las opciones disponibles] | [Asi los clientes pueden ver que almuerzo pueden pedir cada dia de la semana] |
| HU-03 |[Administrador]|[ Eliminar o deshabilitar un ítem del menu semanal ] | [Retirar opciones que ya no estan disponibles sin afectar los pedidos existentes] |
| HU-04 | [Empleado] | [Recibir confirmacion por correo] | [Para asegurar que mi pedido ha sido realizado con exito] |
| HU-05 | [Empleado] | [Quiero consultar mi historial de pedidos realizados]     | [Para facilitar la repetición de pedidos ] |
| HU-06 | [Empleado] | [Recibir un aviso cuando mi vianda esta en camino o lista para retirar ] | [Estar preparado y no perder tiempo esperando] |
| HU-07 | [Administrador] | [Registrar clientes activos o inactivos] | [Actualizar quienes reciben viandas] |
| HU-08 |  [Empleado] |  [Registrarme en el sistema] |  [Acceder a las funcionalidades de la plataforma] |
| HU-09 | [Empleado/Administrador] | [Iniciar sesion con mis credenciales] | [Acceder a mi cuenta de forma segura]|
| HU-10 | [Empleado] | [Configurar mis días de asistencia semanal] | [Que el sistema me muestre solo los menús de los dias que voy a la oficina]|
| HU-11 | [Empleado] | [Seleccionar, modificar o cancelar mi pedido de almuerzo] | [Gestionar mi eleccion antes del cierre de pedidos]|
| HU-12 | [Administrador] |[Recibir un recordatorio automatico cuando un empleado no confirmo su pedido] | [Hacer seguimiento y evitar pedidos incompletos] |
| HU-13 | [Administrador] |[Definir una fecha y hora limite para la confirmacion de pedidos] | [Cerrar la recepcion a tiempo y organizar la produccion]|
| HU-14 | [Administrador] | [Marcar dias feriados en el sistema] | [Bloquear pedidos automaticamente en esas fechas]|
| HU-15 | [Administrador] | [Asignar o modificar el rol de un usuario] | [Controlar los permisos y accesos según la funcion de cada persona]
## 5. Diagrama de Casos de Uso

> Insertar imagen del diagrama exportado desde Draw.io, Lucidchart, StarUML o similar.  
> Guardar la imagen en esta misma carpeta (`docs/`) y referenciarla abajo.

![Diagrama de Casos de Uso](docs/diagrama-casos-de-uso.png)

## 6. Especificación de Casos de Uso

### CU-01 — Iniciar Sesión

| Campo | Detalle                                                              |
|---|----------------------------------------------------------------------|
| **Actor principal** | Empleado/Administrador                                               |
| **Descripción** | Permite al usuario acceder al sistema mediante sus credenciales.     |
| **Precondiciones** | El usuario debe estar registrado en el sistema.                      |
| **Postcondiciones (criterios de aceptación)** | El usuario ingresa al sistema y se dirige al dashboard según su rol. |

| Secuencia Normal (Camino feliz)                                            | Excepciones / Alternativas                                                               |
|----------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| 1. El usuario ingresa su email y contraseña en la pantalla de login.       |                                                                                          |
| 2. El sistema valida las credenciales.                                     | 2.1 Si las credenciales son incorrectas, muestra mensaje "Email o contraseña inválidos". |
| 3. El sistema autentica al usuario y redirige al dashboard correspondiente. |                                                                                          |

### CU-02 — Registrarse

| Campo | Detalle                                                    |
|---|------------------------------------------------------------|
| **Actor principal** | Empleado                                                   |
| **Descripción** | Permite a un nuevo empleado crear una cuenta en el sistema. |
| **Precondiciones** | Ninguna.                                                   |
| **Postcondiciones (criterios de aceptación)** | Se crea una nueva cuenta con rol de Empleado por defecto.  |

| Secuencia Normal (Camino feliz)                                            | Excepciones / Alternativas                                   |
|----------------------------------------------------------------------------|--------------------------------------------------------------|
| 1. El usuario accede a la opción de "Registrarse".                         |                                                              |
| 2. Completa los datos requeridos: nombre, apellido, email, contraseña y DNI. | 2.1 Si el email ya está registrado, muestra mensaje de error. |
| 3. El sistema valida la información y crea la cuenta.                      |                                                              |
| 4. Muestra mensaje de éxito y redirige a la pantalla de inicio de sesión.  |                                                              |

### CU-03 — Configurar Días de Asistencia

| Campo | Detalle                                                                                             |
|---|-----------------------------------------------------------------------------------------------------|
| **Actor principal** | Empleado                                                                                            |
| **Descripción** | Permite al empleado definir los días de la semana en los que asistirá presencialmente a la oficina. |
| **Precondiciones** | El usuario ha iniciado sesión.                                                                      |
| **Postcondiciones (criterios de aceptación)** | El sistema filtra los menús según los días configurados por el empleado.                            |

| Secuencia Normal (Camino feliz)                                   | Excepciones / Alternativas                               |
|-------------------------------------------------------------------|----------------------------------------------------------|
| 1. El empleado accede a la sección de configuración de asistencia.|                                                          |
| 2. Selecciona los días de la semana que asistirá.                 |                                                          |
| 3. Guarda la configuración.                                       | 3.1 Si no se selecciona ningún día, muestra advertencia.|

### CU-04 — Visualizar Menú Semanal

| Campo | Detalle                                                                         |
|---|---------------------------------------------------------------------------------|
| **Actor principal** | Empleado/Administrador                                                          |
| **Descripción** | Muestra las opciones de menú disponibles para cada día de la semana.            |
| **Precondiciones** | El usuario ha iniciado sesión y el menú está publicado.                        |
| **Postcondiciones (criterios de aceptación)** | Se visualiza el menú semanal (filtrado por días de asistencia para el empleado).|

| Secuencia Normal (Camino feliz)                    | Excepciones / Alternativas |
|----------------------------------------------------|----------------------------|
| 1. El usuario selecciona la semana deseada.        |                            |
| 2. El sistema muestra las opciones de menú por día.|                            |

### CU-05 — Registrar Pedido

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado seleccionar las opciones del menú semanal y guardar su pedido en estado borrador. |
| **Precondiciones** | El usuario ha iniciado sesión, el menú semanal está publicado y se encuentra dentro del plazo para realizar pedidos. |
| **Postcondiciones (criterios de aceptación)** | El pedido queda guardado en estado "borrador". |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a la sección "Mis Pedidos". | |
| 2. El sistema incluye el caso de uso **Visualizar Menú Semanal** (filtrado según sus días de asistencia). | 2.1 Si el día es feriado, no permite seleccionar opciones para ese día. |
| 3. El empleado selecciona una opción de menú por cada día que asiste. | |
| 4. El empleado guarda el pedido como borrador. | 4.1 Si no selecciona una opción en algún día obligatorio, el sistema muestra advertencia. |

### CU-06 — Confirmar Pedido

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado confirmar definitivamente su pedido una vez revisado. |
| **Precondiciones** | El usuario tiene un pedido en estado "borrador" y se encuentra dentro del plazo establecido. |
| **Postcondiciones (criterios de aceptación)** | El pedido pasa a estado "confirmado" y se envía notificación por correo tanto al empleado como al administrador. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a la lista de sus pedidos en borrador. | |
| 2. El sistema muestra el resumen completo del pedido. | |
| 3. El empleado revisa y confirma el pedido. | 3.1 Si el plazo para confirmar ya venció, el sistema impide la confirmación y muestra mensaje de error. |
| 4. El sistema registra la confirmación y envía las notificaciones correspondientes. | |

### CU-07 — Administrar Menús

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador crear, modificar o eliminar las opciones del menú semanal trabajando en estado borrador. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador. |
| **Postcondiciones (criterios de aceptación)** | Los cambios en el menú quedan guardados como borrador (aún no visibles para los empleados). |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador selecciona la semana a gestionar. | |
| 2. Agrega, modifica o elimina platos para los diferentes días. | |
| 3. Guarda los cambios como borrador. | |

### CU-08 — Publicar Menú

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador publicar el menú semanal para que sea visible por los empleados. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador y existe un menú en estado borrador. |
| **Postcondiciones (criterios de aceptación)** | El menú pasa a estado "publicado" y queda disponible para que los empleados lo visualicen y realicen pedidos. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a la sección de gestión de menús. | |
| 2. Selecciona el menú semanal que desea publicar. | |
| 3. Publica el menú. | 3.1 Si el menú no tiene opciones suficientes, el sistema muestra una advertencia antes de publicar. |
| 4. El sistema notifica a los empleados que el nuevo menú está disponible. | |

### CU-09 — Generar Consolidado Diario

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador generar un consolidado con todos los pedidos confirmados de un día específico para enviarlo al proveedor de viandas. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador. |
| **Postcondiciones (criterios de aceptación)** | Se genera un reporte consolidado listo para ser enviado al proveedor. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a la sección "Consolidados Diarios". | |
| 2. Selecciona la fecha deseada. | 2.1 Si no hay pedidos confirmados para esa fecha, el sistema muestra un mensaje informativo. |
| 3. El sistema muestra el resumen de pedidos (cantidad por plato, totales y detalle por empleado). | |
| 4. El administrador genera o exporta el consolidado (en formato PDF o vista en pantalla). | |
| 5. (Opcional) El administrador marca el consolidado como "enviado al proveedor". | |

### CU-10 — Gestionar Usuarios

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador gestionar los usuarios del sistema (visualizar, editar información, cambiar rol y estado). |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador. |
| **Postcondiciones (criterios de aceptación)** | La información de los usuarios y sus roles quedan actualizados correctamente. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a "Gestión de Usuarios". | |
| 2. Visualiza la lista completa de usuarios. | |
| 3. Selecciona un usuario de la lista. | |
| 4. Puede ver detalles, editar información o cambiar rol (Empleado/Administrador). | 4.1 Si el administrador intenta cambiar su propio rol a Empleado, el sistema lo impide.<br>4.2 Si intenta desactivarse a sí mismo, el sistema lo impide. |
| 5. Guarda los cambios realizados. | 5.1 Si se intenta usar un email ya registrado, se muestra mensaje de error. |

### CU-11 — Definir Límite de Pedidos

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador establecer la fecha y hora límite para que los empleados puedan confirmar sus pedidos. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador. |
| **Postcondiciones (criterios de aceptación)** | Se define el plazo y el sistema bloquea nuevas confirmaciones una vez vencido el límite. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a la sección de configuración. | |
| 2. Define la fecha y hora límite para la confirmación de pedidos. | |
| 3. Guarda los cambios. | |

### CU-12 — Marcar Días Feriados

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador marcar días feriados para bloquear automáticamente los pedidos en esas fechas. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador. |
| **Postcondiciones (criterios de aceptación)** | No se permiten pedidos en los días marcados como feriados. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a la sección de configuración. | |
| 2. Selecciona la fecha y la marca como día feriado. | |
| 3. Guarda los cambios. | |

---

> Repetir la ficha completa para cada caso de uso del diagrama.
> Las excepciones se numeran ligadas al paso del que se desvían (ej: 4.1 en la misma fila que el paso 4).
