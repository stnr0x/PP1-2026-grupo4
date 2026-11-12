 # Plan de pruebas · Entrega 4 (TP Pedidos)

Objetivo: probar **todo** de punta a punta y poder mostrar la **trazabilidad** de cualquier módulo:

```
pantalla (.html/.js) → función de datos.js → endpoint → Controller → Service → Repository → tabla (MySQL)
```

Herramientas: XAMPP (MySQL/MariaDB + phpMyAdmin), IntelliJ (backend), VS Code + Live Server (frontend), Postman (API), DevTools del navegador (F12).

Marcá cada casilla a medida que la probás. Si algo no da lo esperado, anotá qué pasó y seguí.

---

## Fase 0 · Setup desde cero (clon limpio)

La idea es probar que **el repo solo alcanza** para levantar todo, como lo haría el docente.

- [ ] **0.1 Clonar** en una carpeta nueva (no pisa la tuya):
  ```
  cd C:\Users\tfkgn\Desktop
  git clone https://github.com/stnr0x/PP1-2026-grupo4.git PP-prueba
  ```
- [ ] **0.2 XAMPP**: abrir el panel → **Start** en **MySQL** y en **Apache** (Apache es para phpMyAdmin). Los dos en verde.
- [ ] **0.3 Crear la base**: abrir `http://localhost/phpmyadmin` → pestaña **Importar** → elegir `PP-prueba\backend\sql\tp-pedidos.sql` → **Importar**.
  - Alternativa por terminal (desde la raíz del repo): `cmd /c "C:\xampp\mysql\bin\mysql.exe -u root < backend\sql\tp-pedidos.sql"`. En PowerShell el `<` no existe, y pasarlo con `Get-Content | mysql` rompe los acentos (`purÃ©`): usá `cmd /c` o phpMyAdmin.
- [ ] **0.4 Verificar la base**: en phpMyAdmin → `pp1_pedidos` → pestaña **SQL**, pegar y ejecutar:
  ```sql
  SELECT 'usuario' t, COUNT(*) n FROM usuario UNION ALL
  SELECT 'asistencia', COUNT(*) FROM asistencia UNION ALL
  SELECT 'dia_no_laborable', COUNT(*) FROM dia_no_laborable UNION ALL
  SELECT 'menu_dia', COUNT(*) FROM menu_dia UNION ALL
  SELECT 'plato', COUNT(*) FROM plato UNION ALL
  SELECT 'menu_plato', COUNT(*) FROM menu_plato UNION ALL
  SELECT 'pedido', COUNT(*) FROM pedido UNION ALL
  SELECT 'pedido_dia', COUNT(*) FROM pedido_dia UNION ALL
  SELECT 'notificacion', COUNT(*) FROM notificacion;
  ```
  Esperado: 5 · 16 · 3 · 5 · 6 · 14 · 4 · 12 · 1.
- [ ] **0.5 Abrir el backend en IntelliJ**: *File → Open* → carpeta `PP-prueba\backend` → *Trust project*. Esperar a que Maven termine de bajar dependencias (barra de abajo).
  - Si pide SDK: *File → Project Structure → SDK* → elegir **ms-17**.
- [ ] **0.6 La contraseña de la base, sin escribirla en el repo**: *Run → Edit Configurations… → PedidosApplication → Modify options → Add VM options* → escribir:
  ```
  -DDB_PASSWORD=
  ```
  (vacía, porque root de XAMPP no tiene clave). En Windows una *variable de entorno* vacía se borra sola; como *propiedad de Java* (`-D`) sí funciona, y Spring la lee igual en `${DB_PASSWORD:cambiar}`.
  - Desde terminal sería: `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=-DDB_PASSWORD="`
- [ ] **0.7 Arrancar**: ▶ en `PedidosApplication`. La consola termina en:
  ```
  Tomcat started on port 8080 (http)
  Started PedidosApplication in X seconds
  ```
  Eso ya prueba que **las 8 entidades coinciden con las tablas** (`ddl-auto=validate`).
- [ ] **0.8 Prueba rápida**: en el navegador `http://localhost:8080/api/platos` → JSON con 6 platos.
- [ ] **0.9 Tests**: en IntelliJ, clic derecho en `src/test/java` → *Run 'All Tests'* (o `.\mvnw.cmd test`). Esperado: **10 tests en verde** (7 de `PedidoServiceTest` + 3 de `AsistenciaServiceTest`).
- [ ] **0.10 Frontend**: abrir `PP-prueba` en VS Code → clic derecho en `frontend/login.html` → **Open with Live Server** (puerto 5500).

### Errores típicos del setup
| Ves esto | Es esto | Arreglo |
|---|---|---|
| `Communications link failure` | MySQL apagado | Start en XAMPP |
| `Access denied for user 'root'` | Spring usó `cambiar` como clave | Falta `-DDB_PASSWORD=` en VM options |
| `Unknown database 'pp1_pedidos'` | No corriste el script | Paso 0.3 |
| `Schema-validation: missing column…` | Entidad ≠ tabla | No debería pasar: avisar |
| `Port 8080 was already in use` | Otro Spring abierto | Cerrar el otro (cuadradito rojo en IntelliJ) |
| Pantalla "No pudimos cargar…" | Spring apagado o CORS | Ver Fase 4 |

---

## Fase 1 · La base sola (phpMyAdmin)

Antes del código: entender que **lo que se ve en pantalla sale de cruzar tablas**. En phpMyAdmin → `pp1_pedidos` → SQL:

- [ ] **Cruce 1 · menú del martes 27/10**
  ```sql
  SELECT md.fecha, p.id_plato, p.nombre, p.categoria, mp.stock_disponible
  FROM menu_dia md
  JOIN menu_plato mp ON mp.id_menu_dia = md.id_menu_dia
  JOIN plato p       ON p.id_plato     = mp.id_plato
  WHERE md.fecha = '2026-10-27' AND md.publicado = TRUE
  ORDER BY mp.id_menu_plato;
  ```
  Esperado: Pollo al horno (20), Ñoquis (15), Wok (10). Con `'2026-11-02'`: vacío (no publicado).
- [ ] **Cruce 2 · consolidado del 28/10**
  ```sql
  SELECT p.nombre AS plato, COUNT(*) AS porciones
  FROM pedido_dia pd
  JOIN pedido pe ON pe.id_pedido = pd.id_pedido
  JOIN plato p   ON p.id_plato   = pd.id_plato
  WHERE pd.fecha_entrega = '2026-10-28' AND pe.estado = 'CONFIRMADO'
  GROUP BY p.id_plato, p.nombre
  ORDER BY porciones DESC, p.nombre;
  ```
  Esperado: Milanesa 2, Wok 1. Probá con `'2026-10-26'` **ahora** (antes de abrir la app): Milanesa 1, Tarta 1. Y sacando el filtro de estado: Milanesa 2 (cuenta el BORRADOR de Diego).
- [ ] **Cruce 3 · mis pedidos (Lucía)**
  ```sql
  SELECT pe.id_pedido, pe.estado, md.fecha AS dia, p.nombre AS plato, pd.comentario
  FROM pedido pe
  JOIN pedido_dia pd ON pd.id_pedido   = pe.id_pedido
  JOIN menu_dia md   ON md.id_menu_dia = pd.id_menu_dia
  JOIN plato p       ON p.id_plato     = pd.id_plato
  WHERE pe.id_usuario = 3 ORDER BY md.fecha;
  ```
  Esperado: pedido 2 CONFIRMADO · 26/10 Tarta · 28/10 Wok "Sin picante" · 29/10 Tarta.
- [ ] **Cruce 4 · ¿se puede pedir el 30/10?** (copiar del modelo explicado) → `NO: día no laborable`. Con 29/10 → `SÍ`; con 02/11 → `NO: todavía no hay menú publicado`.
- [ ] **La trampa de Asistencia**: `SELECT * FROM asistencia WHERE id_usuario = 3;` → el 27/10 tiene `asiste = 0`. Esa fila es la prueba de por qué es una tabla y no un campo del usuario.

> ⚠️ Cuando la app lista pedidos o consolidado, **confirma sola los BORRADORES con plazo vencido** (Diego). Después de eso, el cruce 2 del 26/10 da Milanesa 2. Es la regla "después se confirma automáticamente". Para volver a los números del modelo: correr el script de nuevo.

---

## Fase 2 · La API con Postman

**Dónde entra Postman**: prueba el backend **sin el frontend**. Si algo falla en la pantalla, Postman te dice si el problema es del backend o del frontend. En la defensa sirve para mostrar el endpoint "pelado": qué recibe y qué devuelve.

- [ ] **2.1 Instalar Postman de escritorio** (postman.com/downloads). La versión web no llega a `localhost` sin un agente extra.
- [ ] **2.2 Importar la colección**: *Import* → `docs/4ta-entrega/pp1-pedidos.postman_collection.json`. Aparece **"PP1 · TP Pedidos (Grupo 4)"** con carpetas 0, M1, M3, M2, M4 y M5.
- [ ] **2.3 Base limpia**: correr el script de nuevo (la colección espera los datos originales).
- [ ] **2.4 Recorrerla a mano, una por una, en orden.** En cada request mirá:
  - **Body** (lo que mandás) y la respuesta (lo que vuelve), el **código** (200, 201, 400…).
  - La pestaña **Test Results**: chequeos automáticos en verde.
  - La **descripción** de la request (ícono de documento a la derecha): dice qué Controller → Service → tabla toca y qué SQL correr para verificar.
  - La **consola de IntelliJ**: con `show-sql=true` aparece el SQL que ejecutó Hibernate (`Hibernate: insert into pedido …`). **Esto es trazabilidad en vivo.**
- [ ] **2.5 Correrla entera**: clic derecho en la colección → *Run collection* → *Run*. Esperado: **58 requests, todas verdes**. (Para correrla otra vez: script primero.)
- [ ] **2.6 Verificar en la base** después de M4:
  ```sql
  SELECT * FROM pedido ORDER BY id_pedido DESC LIMIT 3;
  SELECT * FROM pedido_dia ORDER BY id_pedido_dia DESC LIMIT 4;
  SELECT * FROM menu_plato WHERE id_menu_dia >= 6;  -- stock de la semana 23/11
  ```

Qué cubre cada carpeta:

| Carpeta | Casos OK | Casos de error |
|---|---|---|
| M1 | login Lucía/Laura, registro, ver, editar perfil, cambiar clave, dar de baja | clave mala 401, correo repetido 409, email inválido 400, usuario 999 404, baja → 401 |
| M3 | plato, menú del 27/10 + opciones (cruce 1), feriados, publicar semana 23/11 | plato 99 404, menú en día no laborable 400 |
| M2 | asistencia de Lucía (27/10 = false), guardar semana, *upsert* (misma fila), semana sin configurar repite la última | ningún día marcado 400 |
| M4 | pedir (BORRADOR), stock baja, modificar, mis pedidos (cruce 3), confirmar, cancelar devuelve stock | 2º pedido de la semana, plato fuera del menú, día no laborable, menú sin publicar, plazo vencido, sin días, admin edita semana con pedidos, feriado con pedidos, cancelar confirmado |
| M5 | consolidado 28/10 (cruce 2), 26/10, 23/11 (no cuenta el cancelado), todos los pedidos, usuarios sin contraseña | — |

---

## Fase 3 · Las pantallas, módulo por módulo, con trazabilidad

Base limpia (script). Spring corriendo. Live Server abierto. **F12 → pestaña Network → filtro "Fetch/XHR"** abierta todo el tiempo: ahí ves cada pedido al backend, su método, URL, código y respuesta.

Para cada módulo: hacé la acción, mirá la request en Network, el SQL en la consola de IntelliJ y la fila en phpMyAdmin. **Recargá (F5) y verificá que sigue ahí** (es la definición de "funcionando").

### M1 · Acceso

| Acción | Pantalla / función | Request | Backend | Tabla |
|---|---|---|---|---|
| Iniciar sesión | `login.js` → `login()` | `POST /api/usuarios/login` | `UsuarioController.login` → `UsuarioService.login` → `findByCorreo` | `usuario` (SELECT) |
| Registrarse | `registro.js` → `crearUsuario()` | `POST /api/usuarios` | `UsuarioService.registrar` → `save` | `usuario` (INSERT) |
| Abrir cualquier pantalla | `exigirSesion()` → `obtenerUsuarioActual()` | `GET /api/usuarios/{id}` | `UsuarioService.buscar` | `usuario` |
| Editar perfil / baja | `perfil.js` → `actualizarUsuario()` | `PUT /api/usuarios/{id}` | `UsuarioService.actualizar` | `usuario` (UPDATE) |
| Cambiar contraseña | `perfil.js` → `cambiarContrasena()` | `PUT /api/usuarios/{id}/contrasena` | `UsuarioService.cambiarContrasena` | `usuario` |

- [ ] Login con clave mala → "Email o contraseña incorrectos." (Network: 401)
- [ ] Login Lucía → va a `menu.html`. Login Laura → va a `administrar-menus.html` (por `rol`)
- [ ] En Network, la respuesta del login **no tiene `contrasena`**
- [ ] Registro con correo nuevo → entra; en phpMyAdmin aparece con `rol = EMPLEADO`
- [ ] Registro con `lucia.fernandez@empresa.com.ar` → "Ya hay una cuenta con ese email." (409)
- [ ] Perfil: cambiar teléfono → F5 → sigue cambiado → `SELECT telefono FROM usuario WHERE id_usuario = 3;`
- [ ] Cambiar contraseña con la actual mal → error; bien → OK
- [ ] Sesión: en DevTools → *Application → Session Storage* solo está `usuarioActual` (el id). Todo lo demás viene del backend.

### M3 · Mis menús (admin publica)

| Acción | Pantalla / función | Request | Backend | Tablas |
|---|---|---|---|---|
| Ver semanas | `administrar-menus.js` → `obtenerMenusDia()`, `obtenerMenuPlatos()`, `obtenerPlatos()` | `GET /api/menus`, `/menu-platos`, `/platos` | Controllers → repositorios | `menu_dia`, `menu_plato`, `plato` |
| Publicar | `guardarSemana(true)` → `guardarSemanaMenus()` | `PUT /api/menus/semana` | `MenuService.guardarSemana` | `menu_dia` + `menu_plato` (INSERT/DELETE) |
| Feriados | `feriados.js` → `guardarFeriados()` | `PUT /api/dias-no-laborables` | `DiaNoLaborableService.reemplazar` | `dia_no_laborable` |

- [ ] Laura → *Crear menú de una semana nueva* → semana del **16/11** (o 23/11 si ya pasó el jueves 12 hs) → el **viernes 20/11 sale como feriado**
- [ ] Tildar platos → **Publicar** → F5 → la semana dice "Publicado"
- [ ] phpMyAdmin: `SELECT * FROM menu_dia ORDER BY fecha DESC;` y `SELECT * FROM menu_plato ORDER BY id_menu_plato DESC;` → stock 20
- [ ] La semana del 26/10 dice **"Ver"** (tiene pedidos: no se puede editar)
- [ ] Feriados: tildar el 28/10 → *Guardar* → error "ya hay pedidos para ese día"
- [ ] Feriados: tildar un día sin pedidos (ej. 10/12) → guardar → F5 → sigue → `SELECT * FROM dia_no_laborable;` (motivo "Feriado"). Destildarlo y guardar para dejarlo como estaba.

### M2 · Mis días

| Acción | Pantalla / función | Request | Backend | Tabla |
|---|---|---|---|---|
| Ver mis días | `menu.js` → `obtenerAsistencia()` | `GET /api/asistencias?idUsuario=&desde=&hasta=` | `AsistenciaService.listar` (si la semana está vacía, repite la última guardada) | `asistencia` |
| Guardar días | `guardarAsistencia()` | `PUT /api/asistencias` (6 filas) | `AsistenciaService.guardar` (upsert) | `asistencia` |

**Regla: la asistencia queda guardada hasta que la cambies.** Si una semana no tiene filas, `listar` busca la última fila del empleado (`findFirstByIdUsuarioAndFechaBeforeOrderByFechaDesc`), toma esa semana como su horario habitual y la copia a las fechas pedidas, día por día. Esas filas vuelven con `id: null` porque todavía no están en la tabla: se graban recién cuando el empleado guarda. La tabla no cambia y no hace falta ninguna columna nueva.

- [ ] Lucía → menú → **ya aparece "Venís: lunes, miércoles, jueves"** sin que configure nada: es su semana del 26/10 del script (el martes 27/10 tiene `asiste = 0`), repetida
- [ ] En Network, mirá la respuesta del GET: fechas de la semana nueva y **`id: null`**. `SELECT * FROM asistencia WHERE id_usuario = 3 AND fecha > '2026-11-01';` → 0 filas: todavía no se grabó nada
- [ ] *Modificar* → dejar **lunes y miércoles** → *Guardar*
- [ ] En Network, mirá el body del PUT: **6 objetos** `{ idUsuario, fecha, asiste }`. La pantalla piensa en "lunes, miércoles"; la base, en fechas. `datos.js` traduce.
- [ ] F5 → siguen lunes y miércoles
- [ ] `SELECT * FROM asistencia WHERE id_usuario = 3 ORDER BY fecha;` → 6 filas nuevas
- [ ] *Modificar* → agregar martes → guardar → **mismos ids** en la tabla (actualizó, no duplicó)
- [ ] En Postman, *Semana 30/11 sin configurar*: devuelve el último patrón que guardaste con las fechas del 30/11. Ese es el "queda guardado hasta que lo cambies"
- [ ] Desmarcar todo → "Marcá al menos un día de asistencia."

### M4 · Mi pedido

| Acción | Pantalla / función | Request | Backend | Tablas |
|---|---|---|---|---|
| Guardar pedido | `menu.js` submit → `guardarPedido()` | `POST /api/pedidos` | `PedidoService.crear` → `validarDias` → `guardarLineas` | `pedido`, `pedido_dia` (INSERT), `menu_plato` (stock −1) |
| Modificar | `menu.html?editar=ID` → `actualizarPedido()` | `PUT /api/pedidos/{id}` | `PedidoService.modificar` | ídem |
| Mis pedidos | `mis-pedidos.js` → `obtenerPedidos(idUsuario)` | `GET /api/pedidos?idUsuario=` | `PedidoService.listar` (cruce 3) | `pedido`, `pedido_dia` |
| Confirmar / cancelar | `cambiarEstadoPedido()` | `PATCH /api/pedidos/{id}/estado` | `PedidoService.cambiarEstado` | `pedido` (UPDATE), `menu_plato` (stock +1 al cancelar) |

- [ ] Lucía elige un plato por día → *Guardar pedido* → tarjeta "Pedido registrado · Pendiente"
- [ ] Network: `POST /api/pedidos` → **201** → respuesta con `estado: "BORRADOR"` y `pedidoDias`
- [ ] Consola IntelliJ: `insert into pedido`, `insert into pedido_dia` ×2, `update menu_plato`
- [ ] phpMyAdmin: el pedido, sus líneas y el `stock_disponible` bajó a 19
- [ ] F5 → sigue la tarjeta del pedido
- [ ] Mis pedidos → *Modificar* → cambiar un plato → guardar → en la base cambió la línea
- [ ] Mis pedidos → *Cancelar* → "¿Seguro?" → *Sí* → "Cancelado" → el stock volvió a 20
- [ ] Volver al menú → hacer otro pedido → Mis pedidos → *Confirmar* → "Confirmado" → F5 → sigue
- [ ] El pedido del 26/10 muestra "(Sin picante)" (columna `comentario`)

### M5 · Consolidado

| Acción | Pantalla / función | Request | Backend | Tablas |
|---|---|---|---|---|
| Generar | `consolidado.js` → `renderTotalesPorDia()` → `obtenerConsolidado(fecha)` (uno por día) | `GET /api/consolidado?fecha=` | `PedidoService.consolidado` → `PedidoDiaRepository.consolidadoDeFecha` (SQL nativo = cruce 2) | `pedido_dia` + `pedido` + `plato` (COUNT/GROUP BY) |
| Listado por empleado | `obtenerPedidos()`, `obtenerUsuarios()` | `GET /api/pedidos`, `GET /api/usuarios` | `PedidoService.listar`, `UsuarioService.listar` | `pedido`, `pedido_dia`, `usuario` |

- [ ] Laura → Consolidado → semana del 26/10 → *Generar* → miércoles 28/10: Milanesa × 2, Wok × 1
- [ ] Network: 4 requests `GET /api/consolidado?fecha=…` (una por día)
- [ ] Consola IntelliJ: el `SELECT p.nombre AS plato, COUNT(*) …` (el mismo SQL de la Fase 1)
- [ ] Comparar con el cruce 2 en phpMyAdmin: **mismos números**
- [ ] Semana nueva (la que pidió Lucía) → aparece su pedido confirmado; los BORRADOR se avisan arriba y no se cuentan

---

## Fase 4 · Estados de error y CORS

- [ ] **Backend caído**: parar Spring (cuadradito rojo) → F5 en Menú, Mis pedidos y Consolidado → cada una muestra su mensaje de error, no una pantalla en blanco. Network: `ERR_CONNECTION_REFUSED`.
- [ ] **Cargando**: Network → *No throttling* → **Slow 3G** → F5 → se ve "Cargando…".
- [ ] **CORS** (para entender, y volver atrás): en `CorsConfig.java` comentar la línea de `allowedOrigins` → reiniciar → F5 → consola del navegador: *blocked by CORS policy*. **Postman sigue andando**: el que bloquea es el navegador. Descomentar y reiniciar.
- [ ] **Validate** (para entender, y volver atrás): en `Plato.java` cambiar `categoria` por `tipo` → reiniciar → Spring no arranca: `Schema-validation: missing column [tipo] in table [plato]`. Volver a `categoria`.

---

## Fase 5 · Flujo completo de la demo (ensayo con reloj)

1. Script (base limpia) → Spring → Live Server.
2. Laura publica la semana (si es después del jueves 12 hs, la app ofrece la del 23/11).
3. Lucía marca sus días → elige platos → guarda → F5 (sigue).
4. Mis pedidos → Confirmar.
5. Laura → Consolidado de esa semana → aparece Lucía.
6. Mostrar en phpMyAdmin las filas nuevas.

Objetivo: **menos de 5 minutos**, sin buscar nada.

---

## Fase 6 · Preguntas de defensa (respondelas en voz alta)

1. **¿Por qué `Asistencia` es una tabla y no un campo del usuario?** Porque es "qué día viene", por fecha. Un campo no puede decir "el 27/10 no, el 28/10 sí" ni contestar "¿quién viene el martes?". Fila de prueba: Lucía, 27/10, `asiste = 0`.
2. **¿Por qué `MenuPlato` es una entidad y no solo una unión?** Porque además de las dos FK tiene `stock_disponible`. `PedidoDia` también: tiene `fecha_entrega` y `comentario`.
3. **¿Dónde se guarda el consolidado?** En ningún lado. Se cuenta sobre `pedido_dia` con `COUNT(*) … GROUP BY` (cruce 2, `PedidoDiaRepository.consolidadoDeFecha`). Se filtra `estado = 'CONFIRMADO'` para no contar borradores. Si se guardara un número, quedaría mal cuando alguien cancela.
4. **¿Quién impide pedir en un día no laborable?** El backend, en `PedidoService.validarDias`. La base no puede: `dia_no_laborable` no tiene FK con `menu_dia` ni con `pedido_dia`. Es una regla de negocio y va en el servicio.
5. **¿Qué hace `ddl-auto=validate`?** Spring no crea ni cambia tablas: compara entidades contra el script y, si no coinciden, no arranca.
6. **¿Por qué la URL completa en el fetch y qué es CORS?** Live Server (5500) y Spring (8080) son orígenes distintos; el navegador bloquea leer respuestas de otro origen salvo que el backend lo autorice (`CorsConfig`).
7. **¿Por qué la contraseña no está en el repo?** `${DB_PASSWORD:cambiar}`: se lee de una variable/propiedad de tu máquina.
8. **¿Por qué hay un Service y no todo en el Controller?** El controller atiende la URL; las reglas (plazo, stock, no laborable) están en el service, y por eso se pueden testear con JUnit sin base.
9. **¿Qué es un DTO y por qué `PedidoResponse`?** En la base son dos tablas (`pedido` + `pedido_dia`); el frontend quiere el pedido con sus días adentro. El DTO arma esa forma.
10. **¿Cómo testeás el plazo del jueves si hoy es otro día?** El servicio usa un `Clock` inyectado; en el test se usa un reloj fijo (`Clock.fixed`).
11. **¿Por qué la respuesta no trae la contraseña?** `@JsonProperty(access = WRITE_ONLY)` en `Usuario`: se puede recibir, nunca se devuelve.
12. **¿Qué pasa si dos piden el último plato?** Hoy el stock se controla en el servicio dentro de una transacción; para concurrencia real habría que bloquear la fila (lo podés mencionar como mejora).
