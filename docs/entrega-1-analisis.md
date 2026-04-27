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
| RF-01 |  El sistema debe permitir que un usuario pueda registrarse  | Empleado |
| RF-02 |  El sistema debe permitir que un usuario pueda iniciar sesion | Empleado/Administrador | 
| RF-03 | El sistema debe permitir que un usuario pueda editar su perfil| Empleado |                |
| RF-04 | El sistema debe permitir que el administrador asigne roles a los usuarios | Administrador |          |
| RF-05 | El sistema debe permitir que los empleados puedan configurar su asistencia laboral semanal | Empleado| 
| RF-06 | El sistema debe permitir mostrar las opciones de menú semanal disponibles, filtrando únicamente los días configurados como días de asistencia del empleado | Empleado |
| RF-07 | El sistema debe permitir al usuario seleccionar, modificar o cancelar su pedido | Empleado |
| RF-08 | El sistema debe permitir que el usuario pueda confirmar su pedido de almuerzo | Empleado |          |
| RF-09 | El sistema debe permitir que al confirmar un pedido se envie una notificación al administrador | Sistema correo externo |          |
| RF-10 | El sistema debe permitir que el administrador cree, modifique o elimine los menús semanales | Administrador |         |
| RF-11 | El sistema debe permitir enviar recordatorios automáticos por correo a los empleados que no hayan completado su selección antes del límite | Sistema correo externo |
| RF-12 | El sistema debe permitir al administrador definir una fecha y hora límite para confirmar pedidos | Administrador |
| RF-13 | El sistema debe permitir al administrador marcar días feriados para bloquear pedidos en esas fechas | Administrador |
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
| HU-01 | [Empleado]   | [Editar mi perfil]   | [Mantener mi informacion actualizada]   
| HU-02 |  [Administrador] |  [Crear y publicar el menú semanal con las opciones disponibles] | [Asi los clientes pueden ver que almuerzo pueden pedir cada dia de la semana] 
| HU-03 |[Administrador]|[ Eliminar o deshabilitar un ítem del menu semanal ] | [Retirar opciones que ya no estan disponibles sin afectar los pedidos existentes]
| HU-04 | [Empleado] | [Recibir confirmacion por correo] | [Para asegurar que mi pedido ha sido realizado con exito]
| HU-05 | [Empleado] | [Quiero consultar mi historial de pedidos realizados]     | [Para facilitar la repetición de pedidos ]
| HU-06 | [Empleado] | [Recibir un aviso cuando mi vianda esta en camino o lista para retirar ] | [Estar preparado y no perder tiempo esperando]
| HU-07 | [Administrador] | [Registrar clientes activos o inactivos] | [Actualizar quienes reciben viandas]



## 5. Diagrama de Casos de Uso

> Insertar imagen del diagrama exportado desde Draw.io, Lucidchart, StarUML o similar.  
> Guardar la imagen en esta misma carpeta (`docs/`) y referenciarla abajo.

![Diagrama de Casos de Uso](./diagrama-casos-de-uso.png)

## 6. Especificación de Casos de Uso

### CU-01 — [Nombre del caso de uso]

| Campo | Detalle |
|---|---|
| **Actor principal** | |
| **Descripción** | |
| **Precondiciones** | |
| **Postcondiciones (criterios de aceptación)** | |

| Secuencia Normal (Camino feliz) | Excepciones / Alternativas |
|---|---|
| 1.  |  |
| 2.  |  |
| 3.  |  |

---

> Repetir la ficha completa para cada caso de uso del diagrama.
> Las excepciones se numeran ligadas al paso del que se desvían (ej: 4.1 en la misma fila que el paso 4).
