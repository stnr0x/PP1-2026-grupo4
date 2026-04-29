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

### CU-01 — [Iniciar Sesion]

| Campo | Detalle                                                              |
|---|----------------------------------------------------------------------|
| **Actor principal** | Empleado/Administrador                                               |
| **Descripción** | Permite al usuario acceder al sistema mediandte sus credenciales.    |
| **Precondiciones** | El usuario debe estar registrado en el sistema.                      |
| **Postcondiciones (criterios de aceptación)** | El usuario ingresa al sistema y se dirige al dashboard segun su rol. |

| Secuencia Normal (Camino feliz)                                            | Excepciones / Alternativas                                                               |
|----------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| 1.El usuario ingresa su mail y contraseña en la pantalla de login.         |                                                                                          |
| 2.El sistema valida las credenciales.                                      | 2.1 si las credenciales son incorrectas, muestra mensaje "Email o contraseña invalidos". |
| 3.El sistema autentica al usuario y redirige al dashboard correspondiente. |                                                                                          |

### CU-02 — [Registrarse]
| Campo | Detalle                                                    |
|---|------------------------------------------------------------|
| **Actor principal** | Empleado                                                   |
| **Descripción** | permite a un nuevo empleado crear una cuenta en el sistema. |
| **Precondiciones** | Ninguna.                                                   |
| **Postcondiciones (criterios de aceptación)** | Se crea una nueva cuenta con rol de Empleado por defecto.  |

| Secuencia Normal (Camino feliz)                                            | Excepciones / Alternativas                                   |
|----------------------------------------------------------------------------|--------------------------------------------------------------|
| 1.El usuario accede a la opcion de "Registrarse".                          |                                                              |
| 2.Completa los datos requeridos:nombre, apellido, email, contraseña y DNI. | 2.1 Si el mail ya esta registrado, muestra mensaje de error. |
| 3.El sistema valida la informacion y crea la cuenta.                       |                                                              |
| 4.Muestra mensaje de exito y redirige a la pantalla de inicio de sesión.   |                                                              |

### CU-03 — [Configurar Dias de Asistencia]
| Campo | Detalle                                                                                             |
|---|-----------------------------------------------------------------------------------------------------|
| **Actor principal** | Empleado                                                                                            |
| **Descripción** | Permite al empleado definir los dias de la semana en los que asistira presencialmente a la oficina. |
| **Precondiciones** | El usuario ha iniciado sesion.                                                                      |
| **Postcondiciones (criterios de aceptación)** | El sistema filtra los menus segun los dias configurados por el empleado.                            |

| Secuencia Normal (Camino feliz)                                   | Excepciones / Alternativas                               |
|-------------------------------------------------------------------|----------------------------------------------------------|
| 1.El empleado accede a la seccion de configuracion de asistencia. |                                                          |
| 2.Selecciona los dias de la semana que asistirá.                  |                                                          |
| 3.Guarda la configuración.                                        | 3.1 Si no se selecciona ningún dia, muestra advertencia. |

### CU-04 — [Visualizar Menu Semanal]
| Campo | Detalle                                                                         |
|---|---------------------------------------------------------------------------------|
| **Actor principal** | Empleado/Administrador                                                          |
| **Descripción** | Muestra las opciones de menu disponibles para cada dia de la semana.            |
| **Precondiciones** | El usuario ha iniciado sesion                                                   |
| **Postcondiciones (criterios de aceptación)** | Se visualiza el menú semanal (filtrado por dias de asistencia para el empleado) |

| Secuencia Normal (Camino feliz)                    | Excepciones / Alternativas |
|----------------------------------------------------|---|
| 1.El usuario selecciona la semana deseada.         |  |
| 2.El sistema muestra las opciones de menú por dia. |  |

### CU-05 — [Realizar Pedido]
| Campo | Detalle                                                                                  |
|---|------------------------------------------------------------------------------------------|
| **Actor principal** | Empleado                                                                                 |
| **Descripción** | Permite al empleado seleccionar, modificar, cancelar y confirmar su pedido de almuerzo.  |
| **Precondiciones** | El usuario ha iniciado sesión y esta dentro del plazo establacido para realizar pedidos. |
| **Postcondiciones (criterios de aceptación)** | El pedido queda registrado y se notifica al administrador.                               |

| Secuencia Normal (Camino feliz)                            | Excepciones / Alternativas                                                      |
|------------------------------------------------------------|---------------------------------------------------------------------------------|
| 1.El empleado accede a la sección "Realizar pedido".       |                                                                                 |
| 2.El sistema muestra el menú según sus dias de asistencia. | 2.1 Si el dia es feriado, no permite seleccionar menú para ese dia.             |
| 3.El empleado selecciona una opción de menú por cada dia.  |                                                                                 |
| 4.Revisa el resumen de su pedido y lo confirma.            | 4.1 El empleado puede elegir modificar o cancelar el pedido antes de confirmar. |
| 5.El sistema registra el pedido.                           |                                                                                 |

### CU-06 — [Consultar Historial de Pedidos]
| Campo | Detalle                                                                     |
|---|-----------------------------------------------------------------------------|
| **Actor principal** | Empleado                                                                    |
| **Descripción** | Permite al empkeado consultar sus pedidos realizados en semanas anteriores. |
| **Precondiciones** | El usuario ha iniciado sesión.                                              |
| **Postcondiciones (criterios de aceptación)** | Se muestra el historial de pedidos con sus detalles.                        |

| Secuencia Normal (Camino feliz)                           | Excepciones / Alternativas |
|-----------------------------------------------------------|---|
| 1.El empleado accede a la sección "Historial de Pedidos". |  |
| 2.El sistema muestra la lista de pedidos por semana.      |  |
| 3.El empleado puede ver los detalles de cada pedido.      |  |

### CU-07 — [Administrar Menú Semanal]

| Campo | Detalle                                                                                               |
|---|-------------------------------------------------------------------------------------------------------|
| **Actor principal** | Administrador                                                                                         |
| **Descripción** | Permite al administrador crear, modificar o eliminar las opciones de menú para cada dia de la semana. |
| **Precondiciones** | El usuario ha iniciado sesión con rol de Administrador.                                               |
| **Postcondiciones (criterios de aceptación)** | Los cambios en el menú quedan guardados y visibles para los empleados.                                |

| Secuencia Normal (Camino feliz)                              | Excepciones / Alternativas |
|--------------------------------------------------------------|---|
| 1.El administrador selecciona la semana a gestionar.         |  |
| 2.Agrega, modifica o elimina platos para los diferentes dias |  |
| 3.Guarda y publica los cambios en el menú.                   |  |

### CU-08 — [Gestionar Usuarios]
| Campo | Detalle                                                                                                 |
|---|---------------------------------------------------------------------------------------------------------|
| **Actor principal** | Administrador                                                                                           |
| **Descripción** | Permite al administrador gestionar los usuarios del sistema (visualizar, editar, cambiar rol y estado)/ |
| **Precondiciones** | El usuario ha iniciado sesión con el rol de Administrador.                                              |
| **Postcondiciones (criterios de aceptación)** | La informacion de los usuarios y sus roles quedan actualizados correctamente.                           |

| Secuencia Normal (Camino feliz)                                                                                                   | Excepciones / Alternativas                                                              |
|-----------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| 1.El administrador accede a "Gestion de Usuarios".                                                                                |                                                                                         |
| 2.Visualiza la lista completa de usuarios.                                                                                        |                                                                                         |
| 3.El Administrador selecciona un usuario de la lista.                                                                             |                                                                                         |
| 4.El Aministrador puede realizar las siguientes acciones: ver detalles, editar información o cambiar rol (Empleado/Administrador) | 4.1 Si el administrador intenta cambiar su propio rol a Empleado, el sistema lo impide. |
| 5.El administrador guarda los cambios realizados.                                                                                 | 5.1 Si se intenta registrar un mail ya existente, se muestra un mensaje de error.       |
| 6.El sistema actualiza la información del usuario.                                                                                |                                                                                         |

### CU-09 — [Definir Limite de Pedidos]
| Campo | Detalle                                                                                                        |
|---|----------------------------------------------------------------------------------------------------------------|
| **Actor principal** | Administrador                                                                                                  |
| **Descripción** | Permite al administrador establecer la fecha y hora limite para que los empleados puedan confirmar sus pedidos |
| **Precondiciones** | El usuario ha iniciado sesión con el rol de Administrador.                                                     |
| **Postcondiciones (criterios de aceptación)** | Se define el plazo de pedidos y el sistema bloquea nuevas confirmaciones una vez vencido.                      |

| Secuencia Normal (Camino feliz)                          | Excepciones / Alternativas                                                              |
|----------------------------------------------------------|-----------------------------------------------------------------------------------------|
| 1.El administrador accede a la seccion de configuración. |                                                                                         |
| 2.Define fecha y hora limite para realizar pedidos.      |                                                                                         |
| 3.Guarda los cambios.                                    |                                                                                         |

### CU-10 — [Marcar Dias Feriados]
| Campo | Detalle                                                                                                         |
|---|-----------------------------------------------------------------------------------------------------------------|
| **Actor principal** | Administrador                                                                                                   |
| **Descripción** | Permite al administrador marcar dias feriados para bloquear automaticamente la seccion de pedidos en esa fecha. |
| **Precondiciones** | El usuario ha iniciado sesión con el rol de Administrador.                                                      |
| **Postcondiciones (criterios de aceptación)** | No se permiten pedidos en los dias marcados como feriados.                                                      |

| Secuencia Normal (Camino feliz)                          | Excepciones / Alternativas                                                              |
|----------------------------------------------------------|-----------------------------------------------------------------------------------------|
| 1.El administrador accede a la seccion de configuración. |                                                                                         |
| 2.Selecciona la fecha y la marca como dia feriado.       |                                                                                         |
| 3.Guarda los cambios.                                    |                                                                                         |


---

> Repetir la ficha completa para cada caso de uso del diagrama.
> Las excepciones se numeran ligadas al paso del que se desvían (ej: 4.1 en la misma fila que el paso 4).
