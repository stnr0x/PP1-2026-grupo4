# Entrega 4 — Backend integrado

**Grupo**: Grupo 4  
**Proyecto**: Sistema de pedidos de almuerzo (TP Pedidos)  
**Integrante**: Giancarlo Nuñez  
**Fecha de entrega**: 12/11/2026 · **Correcciones de la devolución**: 23/11/2026 (ver [sección 11](#11-correcciones-de-la-devolución-e4))  

El flujo principal funciona **desde el frontend, contra el backend Spring Boot y la base MySQL** `pp1_pedidos`. Ninguna pantalla lee ya los `data/*.json`: todos los datos salen de la base y siguen ahí después de recargar.

---

## 1. Módulos entregados

| Módulo | Pantallas | Endpoints | Tag |
|---|---|---|---|
| M1 · Acceso | Login · Registro · Mi Perfil | `POST /api/auth/login` · `POST /api/auth/registro` · `GET/PUT /api/usuarios/{id}` · `PUT /api/usuarios/{id}/contrasena` | `m1-ok` |
| M2 · Mis días | Menú (panel "¿Qué días venís?") | `GET /api/asistencias?idUsuario=&desde=&hasta=` · `PUT /api/asistencias` | `m2-ok` |
| M3 · Mis menús | Menú · Administrar menús (menús y platos) · Feriados | `GET/POST /api/platos` · `DELETE /api/platos/{id}` · `GET /api/menus?idUsuario=` (empleado) · `GET /api/menus` (admin) · `GET /api/menu-platos` · `PUT /api/menus/semana` · `GET/PUT /api/dias-no-laborables` | `m3-ok` |
| M4 · Mi pedido | Menú (registrar/modificar, con nota por día) · Mis pedidos | `POST /api/pedidos` · `PUT /api/pedidos/{id}` · `PATCH /api/pedidos/{id}/estado` · `GET /api/pedidos?idUsuario=` | `m4-ok` |
| M5 · Admin: consolidado | Consolidado | `GET /api/consolidado?fecha=` · `GET /api/pedidos` · `GET /api/usuarios` | `m5-ok` |
| Destacado · Regla de negocio con JUnit | — | `PedidoServiceTest` (7) · `AsistenciaServiceTest` (3) · `MenuServiceTest` (3) · `UsuarioServiceTest` (3) · `PlatoServiceTest` (7) | `regla-ok` |

**Nivel alcanzado: DESTACADO** — M1 + M2 + M3 + M4 + M5 + una regla de negocio con test JUnit (M6 Seguridad completa no se hizo). Tag final: `v4-entrega`. Correcciones de la devolución: `v4-correcciones`.

---

## 2. Cómo correrlo

1. **Base**: prender MySQL en el panel de XAMPP y correr el script entero:  
   `cmd /c "C:\xampp\mysql\bin\mysql.exe -u root < backend\sql\tp-pedidos.sql"`  (en PowerShell el `<` no existe y un pipe `|` rompe los acentos: por eso `cmd /c`)  
   (o phpMyAdmin → Importar). Se puede correr las veces que haga falta: siempre deja los mismos datos.
2. **Backend**: desde `backend/`, pasándole la clave de la base en `DB_PASSWORD` (variable de entorno o propiedad de Java). En XAMPP root no tiene clave, y como Windows no guarda variables de entorno vacías, se pasa como propiedad:  
   - IntelliJ: *Run → Edit Configurations → Modify options → Add VM options* → `-DDB_PASSWORD=`  
   - Terminal: `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-DDB_PASSWORD="`  
   La consola termina en `Started PedidosApplication`.
3. **Frontend**: abrir `frontend/login.html` con Live Server (puerto 5500).
4. **Tests**: `./mvnw test` desde `backend/` (no necesitan la base).
5. **Postman**: importar `docs/4ta-entrega/pp1-pedidos.postman_collection.json` (64 requests con chequeos automáticos, módulo por módulo). El plan completo está en [plan-de-pruebas.md](plan-de-pruebas.md).

Cuentas del script (contraseña `cambiar123`, guardada en la base como hash BCrypt): `laura.gimenez@empresa.com.ar` (administradora) y 12 empleados `nombre.apellido@empresa.com.ar`: `martin.sosa`, `lucia.fernandez`, `diego.romero`, `carolina.benitez`, `sofia.paz`, `javier.molina`, `valentina.rios`, `tomas.acosta`, `camila.herrera`, `nicolas.dominguez`, `agustina.castro`, `federico.luna`.

**Cambios al script del docente** (tablas y columnas no cambian):
1. `COLLATE utf8mb4_0900_ai_ci` → `utf8mb4_unicode_ci`, porque XAMPP trae MariaDB 10.11 y esa collation es solo de MySQL 8.
2. Contraseñas hasheadas con BCrypt.
3. Datos para la Exposición Final, **agregados** después de la historia de octubre (que queda igual, así los cruces 1-4 del material siguen dando lo mismo): 12 platos, 13 usuarios, menús publicados de lunes a viernes de las semanas del 23/11, 30/11, 07/12 y 14/12 (el 08/12 es feriado y no tiene menú), asistencia de los 12 empleados y pedidos confirmados, en borrador y cancelados.

**Imágenes**: los platos usan `assets/img/<nombre>.jpg`. Si falta el archivo, la pantalla muestra `sin-imagen.svg`.

---

## 3. Arquitectura

```
pantalla (menu.js)  →  datos.js: fetch(`${API}/pedidos`)  →  PedidoController
   →  PedidoService (reglas)  →  PedidoRepository / PedidoDiaRepository  →  tablas pedido y pedido_dia
```

- **`frontend/assets/js/datos.js`** es la única puerta a los datos. Antes leía los JSON; ahora cada función hace `fetch` al backend con el mismo nombre (`obtenerPlatos`, `guardarPedido`…), por eso el render de las pantallas casi no cambió.
- **Paquetes del backend** (`ar.edu.pp1.pedidos`): `entidad/` (una clase por tabla), `repositorio/`, `servicio/` (reglas de negocio), `controlador/` (URLs), `dto/` (lo que entra y sale cuando no es una tabla tal cual), `excepcion/` (errores → 400/401/404/409 con `{ "mensaje": … }`), `config/` (CORS, reloj, `PasswordConfig` con BCrypt e `ImagenesConfig`, que publica las imágenes subidas).
- **Imágenes de platos nuevos**: el admin las sube desde Administrar menús. `AlmacenImagenes` las guarda en `backend/imagenes/` (fuera del repo, configurable con `IMAGENES_DIR`) con un nombre generado (UUID) y solo si el contenido es de verdad JPG o PNG (hasta 2 MB). Spring las publica en `http://localhost:8080/imagenes/…` y esa URL es la que queda en `plato.imagen`. Las imágenes de los platos del script siguen en `frontend/assets/img/`.
- `ddl-auto=validate`: el script manda; Spring solo controla que las 8 entidades coincidan con las tablas.
- La contraseña de la base se lee de `DB_PASSWORD`. La de los usuarios se guarda **hasheada con BCrypt** (`spring-security-crypto`, sin el starter de Spring Security) y nunca sale en el JSON (`@JsonProperty(access = WRITE_ONLY)`).

---

## 4. Tabla de endpoints

Base: `http://localhost:8080/api`. Los errores vuelven como `{ "mensaje": "…" }`.

| Módulo | Método | URL | Qué hace | Respuestas |
|---|---|---|---|---|
| M1 | POST | `/auth/login` | Login con `{ correo, contrasena }` (compara con BCrypt `matches`) | 200 usuario · 401 |
| M1 | POST | `/auth/registro` | Registro (nace `EMPLEADO`, activo, contraseña hasheada) | 201 · 400 · 409 correo repetido |
| M1 | GET | `/usuarios` · `/usuarios/{id}` | Lista de usuarios / un usuario (sin contraseña) | 200 · 404 |
| M1 | PUT | `/usuarios/{id}` | Mi Perfil: nombre, apellido, correo, teléfono, `activo` (baja) | 200 · 400 · 409 |
| M1 | PUT | `/usuarios/{id}/contrasena` | Cambiar contraseña con `{ actual, nueva }` | 204 · 400 |
| M2 | GET | `/asistencias?idUsuario=&desde=&hasta=` | Días que viene en esa semana (si no la configuró, repite la última) | 200 |
| M2 | PUT | `/asistencias` | Guarda `[{ idUsuario, fecha, asiste }]` (crea o actualiza) | 200 · 400 |
| M3 | GET | `/platos` · `/platos/{id}` | Platos | 200 · 404 |
| M3 | POST | `/platos` (multipart: `nombre`, `descripcion`, `categoria`, `imagen`) | Admin: alta de un plato con su imagen (opcional) | 201 · 400 · 409 nombre repetido |
| M3 | DELETE | `/platos/{id}` | Admin: borra un plato que nunca se usó (y su imagen) | 204 · 400 si ya se usó · 404 |
| M3 | GET | `/menus?idUsuario=` | **Empleado**: solo lo que puede pedir (semana que se pide, publicados, sin feriados, días que asiste). Lo filtra `MenuService` | 200 |
| M3 | GET | `/menus` · `/menus?fecha=` · `/menus?desde=&hasta=` | **Admin**: todos, también los borradores | 200 |
| M3 | GET | `/menu-platos?idMenuDia=` | Opciones de un menú con su stock | 200 |
| M3 | PUT | `/menus/semana` | Admin: arma y publica la semana | 200 · 400 |
| M3 | GET | `/dias-no-laborables` | Feriados | 200 |
| M3 | PUT | `/dias-no-laborables` | Admin: reemplaza la lista de feriados | 200 · 400 |
| M4 | GET | `/pedidos?idUsuario=` · `/pedidos` · `/pedidos/{id}` | Mis pedidos / todos (admin) / uno | 200 · 404 |
| M4 | POST | `/pedidos` | Nuevo pedido, una opción por día, nace `BORRADOR` | 201 · 400 |
| M4 | PUT | `/pedidos/{id}` | Modificar el pedido antes del plazo | 200 · 400 · 404 |
| M4 | PATCH | `/pedidos/{id}/estado` | Confirmar o cancelar `{ estado }` | 200 · 400 · 404 |
| M5 | GET | `/consolidado?fecha=` | Porciones por plato de un día (cruce 2, solo `CONFIRMADO`) | 200 |

**Bean Validation** en todo lo que se guarda: `Usuario` (`@NotBlank`, `@Email`, `@Size`), `Asistencia` y `DiaNoLaborable` (`@NotNull`, `@Size`, validadas elemento por elemento con `List<@Valid …>`) y los DTO de pedido, login, contraseña y semana de menú. Si algo no cumple → 400 con el mensaje de la anotación.

---

## 5. Tabla de traducción (A2 completa)

| Tabla | Entidad Java | Atributos (camelCase) | Relaciones | Endpoint | Pantalla de NUESTRO frontend |
|---|---|---|---|---|---|
| `usuario` | `Usuario` | `id`, `nombre`, `apellido`, `correo`, `contrasena`, `telefono`, `rol`, `activo` | la referencian `asistencia`, `menu_dia`, `pedido`, `notificacion` | `/api/usuarios` | Login · Registro · Mi Perfil · Consolidado (nombre del empleado) · navbar de todas |
| `asistencia` | `Asistencia` | `id`, `idUsuario`, `fecha`, `asiste` | muchas → un `Usuario` | `/api/asistencias` | Menú (panel de días) |
| `dia_no_laborable` | `DiaNoLaborable` | `id`, `fecha`, `motivo` | — | `/api/dias-no-laborables` | Feriados · Menú (no ofrece el día) · Administrar menús (día sin servicio) |
| `menu_dia` | `MenuDia` | `id`, `fecha`, `descripcion`, `publicado`, `idUsuarioCreador` | muchos → un `Usuario` · uno → muchos `MenuPlato` | `/api/menus` | Menú · Administrar menús · Consolidado |
| `plato` | `Plato` | `id`, `nombre`, `descripcion`, `imagen`, `categoria` | la referencian `menu_plato`, `pedido_dia` | `/api/platos` | Menú · Mis pedidos · Administrar menús (alta y baja de platos) · Consolidado |
| `menu_plato` | `MenuPlato` | `id`, `idMenuDia`, `idPlato`, `stockDisponible` | muchos → un `MenuDia` · muchos → un `Plato` | `/api/menu-platos` (escritura en `/api/menus/semana`) | Menú · Administrar menús · Consolidado |
| `pedido` | `Pedido` | `id`, `idUsuario`, `fechaPedido`, `estado` | muchos → un `Usuario` · uno → muchos `PedidoDia` | `/api/pedidos` | Menú (registrar/modificar) · Mis pedidos · Consolidado · Feriados · Administrar menús |
| `pedido_dia` | `PedidoDia` | `id`, `idPedido`, `idMenuDia`, `idPlato`, `fechaEntrega`, `comentario` | muchos → un `Pedido`, un `MenuDia`, un `Plato` | dentro de `/api/pedidos` (`pedidoDias`) | Menú (nota por día → `comentario`) · Mis pedidos (con su comentario) · Consolidado (listado por empleado, con la nota) |
| `notificacion` | — (no se mapea) | — | — | — (fuera de alcance) | sin pantalla |
| *(consulta)* | `ConsolidadoItem` (DTO) | `plato`, `porciones` | cruce 2 | `/api/consolidado?fecha=…` | Consolidado (totales por día) |

### Divergencias con la consigna

Lo que quedó distinto de los endpoints del modelo (documento "El modelo de datos explicado") y por qué:

| Consigna / modelo | Nuestro backend | Por qué |
|---|---|---|
| `/api/pedidos/{id}/dias` para `pedido_dia` | Las líneas van **dentro** del pedido (`pedidoDias`) en `POST/PUT /api/pedidos` | Un pedido se registra entero o no se registra: cabecera y días se validan y se guardan en una sola transacción (`PedidoService`). Con un endpoint aparte podría quedar un pedido sin días |
| Escritura de `menu_plato` en `/api/menu-platos` | `PUT /api/menus/semana` (`/api/menu-platos` es solo lectura) | La pantalla Administrar menús arma y publica la semana completa. El servicio controla que no se toque una semana con pedidos ni se cargue menú en un feriado |
| — | `PATCH /api/pedidos/{id}/estado` | Confirmar y cancelar son cambios de estado con reglas propias (solo desde `BORRADOR` y antes del plazo), no una edición del pedido |
| — | `PUT /api/usuarios/{id}/contrasena` | El cambio de contraseña verifica la actual con BCrypt y no se mezcla con los datos del perfil |
| — | `GET /api/menus?idUsuario=` | Pedido de la devolución: el filtro de menús del empleado se hace en el backend |
| — | `PUT /api/dias-no-laborables` reemplaza la lista | La pantalla Feriados guarda el calendario entero de una vez |

El login y el registro ya están en `/api/auth/login` y `/api/auth/registro`, como pide la consigna (antes estaban en `/api/usuarios/login` y `POST /api/usuarios`).

### Divergencias que había en el frontend y cómo se corrigieron

| Divergencia (JSON de E3) | Modelo | Corrección |
|---|---|---|
| `usuario.esUsuarioRestaurante` | `rol` (`EMPLEADO` / `ADMINISTRADOR`) | `esAdministrador(usuario)` en `datos.js`; login, navbar y `exigirSesion` usan `rol` |
| `usuario.direccion` | no existe | se sacó de Registro y Mi Perfil |
| estado `PENDIENTE` | `BORRADOR` | todos los `.js` usan `BORRADOR` (al empleado se le sigue mostrando "Pendiente") |
| `pedido.cantidadPersonas` | no existe | se sacó (un pedido = una persona) |
| `menuDia.stockTotal` | no es columna | se sacó; es `SUM(stock_disponible)` de `menu_plato` |
| `plato` sin `categoria` | `categoria NOT NULL` | la pantalla Menú muestra la categoría |
| `feriados.json` = lista de strings | `{ id, fecha, motivo }` | `obtenerFeriados()` hace `.map(d => d.fecha)` |
| asistencia como nombres de día (`['lunes', …]`) en sessionStorage | una fila por **fecha** con `asiste` | `datos.js` traduce: lee/escribe las 6 fechas de la semana que se pide |
| la contraseña se comparaba en el navegador | — | la compara el backend con BCrypt (login y cambio de contraseña) |
| `menu.js` filtraba los menús publicados | — | los filtra `MenuService.listarParaEmpleado` (`GET /api/menus?idUsuario=`) |

---

## 6. Reglas de negocio (capa de servicio)

| Regla | Dónde | Respuesta |
|---|---|---|
| No se pide en un día no laborable | `PedidoService.validarDias` | 400 |
| Solo menús publicados, el plato tiene que estar en ese menú y con stock | `PedidoService.validarDias` | 400 |
| Plazo: hasta el jueves anterior a las 12 hs | `Plazos.plazoDe` + `PedidoService` | 400 |
| Un solo pedido activo por empleado y semana | `PedidoService.crear` | 400 |
| El pedido nace `BORRADOR`; al vencer el plazo pasa solo a `CONFIRMADO` | `PedidoService.confirmarVencidos` | — |
| Pedir descuenta stock; cancelar o modificar lo devuelve | `PedidoService.guardarLineas/liberarStock` | — |
| No hay menú en un día no laborable; no se marca no laborable un día con pedidos | `MenuService`, `DiaNoLaborableService` | 400 |
| Correo único | `UsuarioService` | 409 |
| Nombre de plato único y categoría de la lista (clásico, vegetariano, liviano, pastas) | `PlatoService.crear` | 409 · 400 |
| La imagen del plato tiene que ser de verdad JPG o PNG (se lee el archivo, no se confía en la extensión) y pesar hasta 2 MB | `AlmacenImagenes` | 400 |
| Solo se borra un plato que nunca se usó en un menú ni en un pedido (si no, se rompen el historial y el consolidado) | `PlatoService.borrar` | 400 |
| La nota de cada día del pedido tiene como máximo 200 caracteres | `PedidoDiaRequest` (`@Size`) | 400 |
| La asistencia queda guardada hasta que el empleado la cambie: una semana sin configurar repite la última que guardó | `AsistenciaService.listar` | — |
| El empleado solo ve menús publicados, de días hábiles y de los días que viene | `MenuService.listarParaEmpleado` | — |
| La contraseña se guarda hasheada y se compara con `matches` | `UsuarioService` + `PasswordConfig` | 401 |

El consolidado **no se guarda**: `PedidoDiaRepository.consolidadoDeFecha` es el cruce 2 (`COUNT(*) … GROUP BY` sobre `pedido_dia`, solo `CONFIRMADO`).

---

## 7. Test JUnit (destacado)

`backend/src/test/java/ar/edu/pp1/pedidos/servicio/PedidoServiceTest.java` prueba la regla "¿se puede pedir ese día?" con Mockito (sin base) y un reloj fijo:

- pedido válido → queda `BORRADOR` y descuenta stock
- día no laborable, menú sin publicar, plato fuera del menú, sin stock → `ReglaNegocioException`
- jueves 11:00 todavía se puede; jueves 12:00 el plazo está cerrado

`AsistenciaServiceTest` (3 tests) prueba que la asistencia queda guardada: una semana sin configurar repite la última, una semana configurada se respeta tal cual, y sin nada guardado devuelve vacío.

`MenuServiceTest` (3 tests): al empleado solo le llegan los menús publicados, no feriados y de los días que viene; sin asistencia no ve ninguno; y el jueves a las 12 hs pasa a la semana siguiente.

`UsuarioServiceTest` (3 tests): el registro guarda el hash y no el texto; el login entra con la contraseña correcta y da 401 con una incorrecta.

`PlatoServiceTest` (7 tests): alta con imagen (queda en disco con un nombre generado), alta sin imagen, nombre repetido → 409, categoría inventada → 400, un archivo que dice `.jpg` pero no es una imagen → 400, no se borra un plato que está en un menú, y se borra uno sin usar junto con su imagen.

---

## 8. Cómo se probó cada módulo

Para cada módulo: el endpoint en Postman/curl con los datos del script (y sus errores 400/401/404/409), la pantalla con Live Server, **recargar y que siga ahí**, y con Spring apagado aparece el estado de error de la pantalla.

Flujo completo probado: Laura publica la semana del 16/11 (el 20/11 aparece como feriado) → Lucía marca lunes y miércoles → elige platos y guarda → recarga y el pedido sigue → en Mis pedidos lo confirma → Laura genera el consolidado (26/10 da los mismos números que el cruce 2).

---

## 9. Guion de la demo (Exposición Final, martes 01/12)

Ese día la pantalla del empleado muestra la **semana del 07/12** (el plazo cierra el jueves 03/12 a las 12 hs). El 08/12 es feriado y no aparece.

1. Correr el script y levantar el backend (con `DB_PASSWORD`).
2. **Registro en vivo** de un empleado nuevo → entra solo → la pantalla Menú le pide la asistencia (todavía no ve menús).
3. Marca sus días → aparecen solo los menús de esos días (filtro del backend: `GET /api/menus?idUsuario=`).
4. Elige un plato por día → guarda (`BORRADOR`) → Mis pedidos → **confirma**.
5. Entra Laura (admin) → Consolidado de la semana del 07/12 → el pedido nuevo suma a los confirmados (los borradores y cancelados no cuentan). La semana del 30/11 muestra la semana en curso completa.
6. phpMyAdmin → `SELECT correo, contrasena FROM usuario` → la contraseña del usuario nuevo está hasheada (`$2a$10$…`).
7. Si se ensaya después del jueves 03/12 a las 12 hs, la pantalla pasa a la semana del 14/12, que también está publicada en el script.

---

## 10. Declaración de uso de IA

| Herramienta | Para qué |
|---|---|
| Claude (Claude Code, en VS Code) | Correcciones de la devolución E4 (BCrypt, filtro de menús en el servicio, `/api/auth`, pantallas que prometían de más, datos para la exposición generados con un script y revisados en la base). Plan de trabajo por módulos a partir de la consigna; generación del backend Spring Boot (entidades, repositorios, servicios, controladores, DTO, manejo de errores, CORS) siguiendo el script del curso; migración de `datos.js` de los JSON a `fetch` contra la API; detección de divergencias entre el frontend y el modelo (`rol`, `BORRADOR`, `direccion`, `stockTotal`…); tests JUnit con Mockito; colección de Postman; plan de pruebas y este README; diagnóstico de errores del setup (collation de MariaDB, `DB_PASSWORD` vacía en Windows, orden de flush de Hibernate). |
| Documentación de la consigna y del curso | Requisitos de la Entrega 4, modelo de datos explicado y script `tp-pedidos.sql`. |

Todo el código generado se revisó, se corrió y se probó contra la base (pantalla, Postman y tests) antes de cada commit. El commit, el push, los PR y los tags los hizo el integrante. La defensa de cada módulo (pantalla → endpoint → tabla) está preparada en [plan-de-pruebas.md](plan-de-pruebas.md).

---

## 11. Correcciones de la devolución E4

| Devolución | Qué se hizo |
|---|---|
| 1. Contraseñas con BCrypt | `spring-security-crypto` (no el starter) + `config/PasswordConfig` (`BCryptPasswordEncoder`). `UsuarioService`: `encode()` al registrar y al cambiar la contraseña, `matches()` en el login. En el script, `cambiar123` va como su hash. `UsuarioServiceTest` |
| 2. Menús filtrados en el backend | `GET /api/menus?idUsuario=` → `MenuService.listarParaEmpleado`: semana que se pide (`Plazos.lunesSemanaQueViene`), publicado, no feriado y días que asiste (reusa `AsistenciaService.listar`). `menu.js` ya no filtra y pide solo las opciones de esos menús. `MenuServiceTest` |
| 3. Endpoints distintos de la consigna | Login y registro en `/api/auth/login` y `/api/auth/registro` (`AuthController`). El resto está en "Divergencias con la consigna" (sección 5) |
| 4. Lo que dice que hace y no hace | Se sacaron "Enviar al proveedor" (Consolidado), "¿Olvidaste tu contraseña?" y "Recordarme" (Login) |
| Datos precargados | Seed de 4 semanas (23/11 a 14/12) con 12 empleados, menús de 4 opciones y pedidos confirmados (ver sección 2) |
| Imágenes | Cada uno de los 12 platos apunta a su propia imagen en `frontend/assets/img/` (ya no comparten la genérica) |
| Extra: nota del pedido | La columna `pedido_dia.comentario` ya existía y el backend la guardaba, pero la pantalla no tenía dónde escribirla. Ahora, al elegir el plato de un día, aparece "Nota para la cocina" (máx. 200). Se ve en Mis pedidos y en el Consolidado |
| Extra: alta y baja de platos | Sección "Platos" en Administrar menús: nombre, categoría, descripción e imagen (`POST /api/platos`). Solo se pueden borrar los que nunca se usaron (`DELETE /api/platos/{id}`) |
| Extra: texto seguro en pantalla | `escaparHtml()` en `datos.js`: los nombres de platos y usuarios y las notas se muestran escapados (lo escriben los usuarios) |
