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
|Sistema |Recibe y envia mails de estado|   -Sistema externo |
## 2. Requisitos Funcionales

| ID    | Descripción | Actor | HU relacionada |
|-------|-------------|-------|----------------|
| RF-01 |  El sistema debe permitir que un usuario pueda registrarse  | Empleado | HU-06|
| RF-02 |  El sistema debe permitir que un usuario pueda iniciar sesion | Empleado/Administrador | HU-07|
| RF-03 | El sistema debe permitir que un usuario pueda editar su perfil| Empleado |    HU-01            |
| RF-04 | El sistema debe permitir que el administrador asigne roles a los usuarios | Administrador |  HU-15        |
| RF-05 | El sistema debe permitir que los empleados puedan configurar su asistencia laboral semanal | Empleado| HU-08 |
| RF-06 | El sistema debe permitir mostrar las opciones de menú semanal disponibles, filtrando únicamente los días configurados como días de asistencia del empleado | Empleado |HU-08 |
| RF-07 | El sistema debe permitir al administrador administrar (crear, modificar, eliminar) los menus semanales en estado borrador|Administrador |HU-02|
| RF-08 | El sistema debe permitir al administrador publicar un menu semanal para que sea visible a los empleados |Administrador |HU-03|
| RF-09 | El sistema debe permitir al empleado registrar su pedido de almuerzo seleccionando opciones del menu publicado| Empleado| HU-09|
| RF-10 | El sistema debe permitir al empleado modificar o cancelar su pedido antes del limite establecido | Empleado| HU-11
| RF-11 | El sistema debe permitir al empleado confirmar su pedido de almuerzo definitivamente| Empleado| HU-10
| RF-12 | El sistema debe enviar una notificacion de confirmaciom al empleado cuando su pedido sea confirmado existosamente | Empleado| HU-04
| RF-13 | El sistema debe enviar recordatorios automaticos por correo a los empleados que no hayan confirmado su pedido antes del limite| Empleado| HU-12
| RF-14 |El sistema debe permitir al administrador definir una fecha y hora limite para la confirmacion de pedidos|Administrador|HU-13
| RF-15 | El sistema debe permitir al administrador marcar días feriados para bloquear pedidos en esas fechas|Administrador|HU-14|
| RF-16 |El sistema debe permitir que el empleado consulte su historial de pedidos realizados | Empleado | HU-13
| RF-17 | El sistema debe generar automaticamente un consolidado diario con todos los pedidos confirmados, agrupados por menu y cantidad|Administrador|HU-16|
| RF-18 |El sistema debe permitir al administrador consultar y descargar el consolidado diario para enviarlo al proveedor de viandas|Administrador|HU-16|
> Cada requisito debe describir una acción concreta: "El sistema debe permitir que [actor] [acción]..."

## 3. Requisitos No Funcionales

| ID     | Categoría              | Descripción                                                                                                              |
|--------|------------------------|--------------------------------------------------------------------------------------------------------------------------|
| RNF-01 | Usabilidad             | El sistema debe tener una interfaz clara, intuitiva y fácil de usar tanto para empleados como para administradores, permitiendo que un usuario sin experiencia pueda completar un pedido en menos de 2 minutos. |
| RNF-02 | Usabilidad             | La interfaz debe ser responsive y adaptarse correctamente a diferentes tamaños de pantalla (escritorio, tablet y dispositivos móviles), utilizando Bootstrap 5 como se indica en la consigna. |
| RNF-03 | Rendimiento            | El tiempo de respuesta de las operaciones principales (login, visualización del menú, registro y confirmación de pedido) no debe superar los 2 segundos en condiciones normales de uso (95% de las peticiones). |
| RNF-04 | Seguridad              | El sistema debe implementar autenticación y control de acceso basado en roles (Empleado y Administrador). Los usuarios solo podrán acceder a las funcionalidades correspondientes a su rol. |
| RNF-05 | Seguridad              | Las contraseñas deben almacenarse utilizando hash bcrypt. Todas las comunicaciones entre el cliente y el servidor deben realizarse mediante HTTPS. Las sesiones de usuario deben cerrarse automáticamente después de 30 minutos de inactividad. |
| RNF-06 | Compatibilidad         | El sistema debe funcionar correctamente en las versiones actuales y las inmediatamente anteriores de los navegadores más utilizados: Google Chrome, Mozilla Firefox, Microsoft Edge y Safari. |
| RNF-07 | Integración Externa    | El sistema debe integrarse con un servicio de correo electrónico externo (mediante SMTP o API) para el envío confiable de notificaciones y recordatorios automáticos. |
| RNF-08 | Fiabilidad             | En caso de fallo temporal en el servicio de correo, el sistema debe reintentar el envío de forma automática hasta 3 veces utilizando un algoritmo de backoff exponencial. Los errores deben ser registrados para su posterior análisis. |
| RNF-09 | Mantenibilidad         | El código fuente debe estar bien estructurado, con separación clara de responsabilidades (siguiendo el stack propuesto: Spring Boot + JPA/Hibernate en backend), para facilitar su mantenimiento y futuras extensiones. |

## 4. Historias de Usuario

| ID    | Como...       | Quiero...                  | Para...                            |
|-------|---------------|----------------------------|------------------------------------|
| HU-01 | [Empleado]   | [Editar mi perfil]   | [Mantener mi informacion actualizada]  | 
| HU-02 |  [Administrador] |  [Crear y administrar el menu semanal ] | [Gestionar las opciones disponibles antes de publicarlas] |
| HU-03 |[Administrador]|[ Publicar el menu semanal  ] | [Que los empleados puedan verlo y realizar sus pedidos] |
| HU-04 | [Empleado] | [Recibir confirmacion por correo al confirmar mi pedido] | [Para asegurar que mi pedido fue registrado con exito] |
| HU-05 | [Empleado] | [Quiero consultar mi historial de pedidos realizados]     | [Para facilitar la repetición de pedidos ] |
| HU-06 | [Empleado] | [Registrarme en el sistema ] | [Acceder a las funcionalidades de la plataforma] |
| HU-07 | [Administrador/Empleado] | [Iniciar sesion con mis credenciales] | [Acceder a mi cuenta de forma segura] |
| HU-08 | [Empleado] |  [Configurar mis dias de asistencia semanal] |  [Que el sistema me muestre solo los menus de los dias que voy a la oficina] |
| HU-09 | [Empleado] | [Registrar mi pedido de almuerzo seleccionando opciones del menu] | [Elegir mis preferencias antes del cierre de pedidos]|
| HU-10 | [Empleado] | [Confirmar mi pedido de almuerzo] | [Enviar definitivamente mi seleccion y que quede registrada en el sistema]|
| HU-11 | [Empleado] | [Modificar o cancelar mi pedido antes del limite] | [Corregir mi seleccion si cambio de planes]|
| HU-12 | [Empleado] | 	[Recibir un recordatorio automático si aún no confirmé mi pedido] |	[No olvidarme de confirmar a tiempo]
| HU-13 | [Administrador] |[Definir una fecha y hora limite para la confirmacion de pedidos] | [Cerrar la recepcion a tiempo y organizar la produccion]|
| HU-14 | [Administrador] | [Marcar dias feriados en el sistema] | [Bloquear pedidos automaticamente en esas fechas]|
| HU-15 | [Administrador] | [Asignar o modificar el rol de un usuario] | [Controlar los permisos y accesos según la funcion de cada persona]
| HU-16 | [Administrador] | [Generar y consultar el consolidado diario de pedidos confirmados] | [Enviarlo al proveedor de viandas para que prepare las cantidades correctas]

## 5. Diagrama de Casos de Uso

> Insertar imagen del diagrama exportado desde Draw.io, Lucidchart, StarUML o similar.  
> Guardar la imagen en esta misma carpeta (`docs/`) y referenciarla abajo.

![Diagrama de Casos de Uso](docs/diagrama-casos-de-uso.png)

## 6. Especificación de Casos de Uso

### CU-01 — Iniciar Sesión

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado/Administrador |
| **Descripción** | Permite al usuario acceder al sistema mediante sus credenciales. |
| **Precondiciones** | El usuario debe estar registrado en el sistema. |
| **Postcondiciones (criterios de aceptación)** | El usuario ingresa al sistema y se dirige al dashboard según su rol. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El usuario ingresa su email y contraseña en la pantalla de login. | |
| 2. El sistema valida las credenciales. | 2.1 Si las credenciales son incorrectas, muestra mensaje "Email o contraseña inválidos". |
| 3. El sistema autentica al usuario y redirige al dashboard correspondiente. | |

### CU-02 — Registrarse

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite a un nuevo empleado crear una cuenta en el sistema. |
| **Precondiciones** | Ninguna. |
| **Postcondiciones (criterios de aceptación)** | Se crea una nueva cuenta con rol de Empleado por defecto. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El usuario accede a la opción de "Registrarse". | |
| 2. Completa los datos requeridos: nombre, apellido, email, contraseña y DNI. | 2.1 Si el email ya está registrado, muestra mensaje de error. |
| 3. El sistema valida la información y crea la cuenta. | |
| 4. Muestra mensaje de éxito y redirige a la pantalla de inicio de sesión. | |

### CU-03 — Editar Perfil

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado editar su información personal y mantener sus datos actualizados. |
| **Precondiciones** | El usuario ha iniciado sesión. |
| **Postcondiciones (criterios de aceptación)** | Los datos del perfil del empleado quedan actualizados correctamente. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a la sección "Mi Perfil". | |
| 2. El sistema muestra los datos actuales. | |
| 3. El empleado modifica los campos deseados. | 3.1 Si intenta usar un email ya registrado, muestra error. |
| 4. Guarda los cambios. | |
| 5. El sistema actualiza la información y muestra mensaje de éxito. | |

### CU-04 — Configurar Días de Asistencia

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado definir los días de la semana en los que asistirá presencialmente a la oficina. |
| **Precondiciones** | El usuario ha iniciado sesión. |
| **Postcondiciones (criterios de aceptación)** | El sistema filtra los menús según los días configurados por el empleado. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a la sección de configuración de asistencia. | |
| 2. Selecciona los días de la semana que asistirá. | |
| 3. Guarda la configuración. | 3.1 Si no se selecciona ningún día, muestra advertencia. |

### CU-05 — Visualizar Menú Semanal

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado/Administrador |
| **Descripción** | Muestra las opciones de menú disponibles para cada día de la semana. |
| **Precondiciones** | El usuario ha iniciado sesión y el menú está publicado. |
| **Postcondiciones (criterios de aceptación)** | Se visualiza el menú semanal (filtrado por días de asistencia para el empleado). |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El usuario selecciona la semana deseada. | |
| 2. El sistema muestra las opciones de menú por día. | |

### CU-06 — Registrar Pedido

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado seleccionar las opciones del menú y guardar el pedido en estado borrador. |
| **Precondiciones** | El usuario ha iniciado sesión, el menú está publicado y está dentro del plazo de pedidos. |
| **Postcondiciones (criterios de aceptación)** | El pedido queda guardado en estado "borrador". |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a la sección "Mis Pedidos". | |
| 2. El sistema incluye el caso de uso **Visualizar Menú Semanal** (filtrado según sus días de asistencia). | 2.1 Si el día es feriado, no permite seleccionar opciones. |
| 3. El empleado selecciona una opción de menú por cada día que asiste. | |
| 4. El empleado guarda el pedido como borrador. | 4.1 Si no selecciona alguna opción obligatoria, muestra advertencia. |

### CU-07 — Modificar o Cancelar Pedido

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado modificar o cancelar un pedido que aún está en estado borrador. |
| **Precondiciones** | El usuario tiene un pedido en estado "borrador" y está dentro del plazo. |
| **Postcondiciones (criterios de aceptación)** | El pedido queda modificado o cancelado según la acción del empleado. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a sus pedidos en estado borrador. | |
| 2. Selecciona el pedido que desea modificar o cancelar. | |
| 3. Elige entre modificar las opciones o cancelar el pedido completo. | 3.1 Si el pedido ya está confirmado, el sistema lo impide. |
| 4. Confirma la acción. | |
| 5. El sistema guarda los cambios. | |

### CU-08 — Confirmar Pedido

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado confirmar definitivamente su pedido. |
| **Precondiciones** | El usuario tiene un pedido en estado "borrador" y está dentro del plazo. |
| **Postcondiciones (criterios de aceptación)** | El pedido pasa a estado "confirmado" y se envían notificaciones. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a sus pedidos en borrador. | |
| 2. El sistema muestra el resumen completo del pedido. | |
| 3. El empleado confirma el pedido. | 3.1 Si el plazo ya venció, el sistema impide la confirmación. |
| 4. El sistema registra la confirmación y envía notificaciones al empleado y administrador. | |

### CU-09 — Consultar Historial de Pedidos

| Campo | Detalle |
|---|---------|
| **Actor principal** | Empleado |
| **Descripción** | Permite al empleado consultar sus pedidos realizados en semanas anteriores. |
| **Precondiciones** | El usuario ha iniciado sesión. |
| **Postcondiciones (criterios de aceptación)** | Se muestra el historial de pedidos con sus detalles. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El empleado accede a la sección "Historial de Pedidos". | |
| 2. El sistema muestra la lista de pedidos por semana. | |
| 3. El empleado selecciona una semana para ver los detalles. | |

### CU-10 — Administrar Menús

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador crear, modificar o eliminar opciones del menú semanal en estado borrador. |
| **Precondiciones** | Sesión iniciada con rol de Administrador. |
| **Postcondiciones** | Los cambios quedan guardados como borrador. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador selecciona la semana a gestionar. | |
| 2. Agrega, modifica o elimina platos. | |
| 3. Guarda los cambios como borrador. | |

### CU-11 — Publicar Menú

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador publicar el menú semanal para que sea visible por los empleados. |
| **Precondiciones** | Existe un menú en estado borrador. |
| **Postcondiciones** | El menú pasa a estado "publicado". |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a gestión de menús. | |
| 2. Selecciona el menú preparado. | |
| 3. Publica el menú. | 3.1 Si no tiene suficientes opciones, muestra advertencia. |

### CU-12 — Generar Consolidado Diario

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite generar un consolidado diario de todos los pedidos confirmados para enviarlo al proveedor. |
| **Precondiciones** | Sesión como Administrador. |
| **Postcondiciones** | Se genera el reporte consolidado listo para exportar. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a "Consolidados Diarios". | |
| 2. Selecciona la fecha. | 2.1 Si no hay pedidos, muestra mensaje informativo. |
| 3. El sistema muestra el resumen por plato y cantidades. | |
| 4. Genera o exporta el consolidado. | |

### CU-13 — Gestionar Usuarios

| Campo | Detalle |
|---|---------|
| **Actor principal** | Administrador |
| **Descripción** | Permite al administrador visualizar, editar información, cambiar el rol y el estado de los usuarios del sistema. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador. |
| **Postcondiciones (criterios de aceptación)** | La información de los usuarios y sus roles quedan actualizados correctamente en el sistema. |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---------------------------------|----------------------------|
| 1. El administrador accede a la sección "Gestión de Usuarios". | |
| 2. El sistema muestra la lista completa de usuarios registrados. | |
| 3. El administrador selecciona un usuario de la lista. | |
| 4. Puede realizar las siguientes acciones: ver detalles, editar información personal, cambiar rol (Empleado/Administrador) o cambiar estado (activo/inactivo). | 4.1 Si el administrador intenta cambiar su propio rol a Empleado, el sistema lo impide.<br>4.2 Si intenta desactivarse a sí mismo, el sistema lo impide. |
| 5. El administrador guarda los cambios realizados. | 5.1 Si se intenta registrar un email ya existente, se muestra mensaje de error. |
| 6. El sistema actualiza la información del usuario y muestra mensaje de confirmación. | |

### CU-14 — Definir Límite de Pedidos

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

### CU-15 — Marcar Días Feriados

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
