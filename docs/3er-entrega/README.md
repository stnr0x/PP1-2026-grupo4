# Entrega 3 — Frontend Codificado

**Grupo**: Grupo 4  
**Proyecto**: Sistema de pedidos de almuerzo  
**Fecha de entrega**: 24/09/2026  

---

## 1. Tabla de capacidades JS

| Capacidad | Pantalla | Archivo JS | CU de E1 | Qué hace |
|-----------|----------|-----------|----------|----------|
| Cap. 1 — Login validado | login.html | assets/js/login.js | CU-01 | Impide el envío con `preventDefault()`, valida campos vacíos y formato de email, muestra el error en el DOM y, si los datos son válidos, redirige al menú |
| Cap. 2 — Listado desde datos | Menu+Registrar-pedido.html | assets/js/menu.js | CU-05 | Trae los platos desde `data/platos.json` con `fetch` y los dibuja como tarjetas con `crearTarjeta` + `renderPlatos` |
| Cap. 3 — Acción del usuario | Menu+Registrar-pedido.html | assets/js/menu.js | CU-06 | Lee el formulario, valida plato y cantidad, arma el objeto pedido con estado `BORRADOR`, lo guarda con `guardarPedido()` (función propia, async) y lo agrega a la lista sin recargar |
| Cap. 4 — Estados de interfaz | Menu+Registrar-pedido.html | assets/js/menu.js | CU-05 | Muestra los estados cargando, vacío y error en el listado de platos con `mostrarMensaje(texto, tipo)` |
| Cap. 5 — Admin: Generar consolidado | Generar-consolidado-(5).html | assets/js/consolidado.js | CU-14 | Valida que se elija una semana, muestra error o éxito en el DOM y dibuja la tabla de pedidos confirmados desde un array con `crearFila` + `renderConsolidado` |

---

## 2. Inventario de pantallas

| Pantalla | Archivo | JS | Estado |
|----------|---------|----|--------|
| Inicio | index.html | — | Estática, links a todas las pantallas |
| Login | login.html | assets/js/login.js | Capacidad 1 |
| Registro | registro.html | — | Estática |
| Mi Perfil | miperfil.html | — | Estática |
| Asistencia | asistencia.html | — | Estática |
| Menú + Registrar pedido | Menu+Registrar-pedido.html | assets/js/menu.js | Capacidades 2, 3 y 4 |
| Mis pedidos | pedidos.html | — | Estática |
| Administrar Menús | administrar-menus.html | — | Estática |
| Generar Consolidado | Generar-consolidado-(5).html | assets/js/consolidado.js | Capacidad 5 |
| Feriados | feriados.html | — | Estática |

---

## 3. Decisiones del grupo

### Organización de archivos JS
Se usa un archivo JS por pantalla: `login.js`, `menu.js` y `consolidado.js`. Cada archivo contiene solo lo de su pantalla: los datos (o la carga de datos), las funciones de render y el manejo de eventos. Así cada pantalla se puede entender y modificar sin tocar las otras.

### Capacidad 5 — Elección
Se eligió **Generar consolidado** (`Generar-consolidado-(5).html`), justificada contra el **CU-14 (Generar Consolidado)** de la Entrega 1. Es la tarea central del Administrador: juntar los pedidos confirmados para enviarlos a cocina. Aplica dos de las capacidades anteriores: valida el formulario (la semana elegida) y actualiza la pantalla dibujando la tabla desde datos, sin HTML escrito a mano.

### Datos del consolidado
Los pedidos confirmados están en un array dentro de `consolidado.js` y no en un JSON, porque la consigna pide un solo archivo JSON (el del listado de la Capacidad 2). En noviembre esos datos van a venir del backend.

### Por qué no se tocó el HTML/CSS de E2
La consigna indica no rehacer el HTML/CSS de E2. Solo se agregaron los contenedores vacíos con `id` donde el JS dibuja (`#lista-platos`, `#lista-pedidos`, `#lista-consolidado`) y los párrafos de error.

### Estados cargando / vacío / error
La función `mostrarMensaje(texto, tipo)` reemplaza el contenido de `#lista-platos` por un mensaje con la clase CSS del estado (`cargando`, `vacio` o `error`). El mensaje de cargando se muestra antes del primer `await`; el vacío se detecta con `platos.length === 0` y corta con `return`; el error se muestra desde el `catch`.

Cómo provocar cada estado en vivo:

| Estado | Cómo |
|--------|------|
| Cargando | DevTools → Network → Slow 3G → recargar |
| Vacío | cambiar el `fetch` a `data/platos-vacio.json` |
| Error | cambiar el `fetch` a una ruta que no existe |

---

## 4. Declaración de uso de IA

| Herramienta | Para qué |
|-------------|----------|
| ChatGPT/OpenAI | Revisión de estructura de código, identificación de errores de sintaxis, orientación sobre uso de `fetch` y manejo de estados |
| Claude | Separación de `style.css` y `style9.css`, explicación paso a paso de la tabla dinámica del consolidado (`crearFila` + `renderConsolidado`), detección de errores (llave sin cerrar, clases CSS que no coincidían, falta de `classList` en el error del pedido), revisión del repositorio contra la consigna y actualización de este README |
| Documentación de la consigna | Entendimiento de requisitos de la Entrega 3 |

---

## 5. Estructura del repo

```
frontend/
├── assets/
│   ├── img/                      ← imágenes de los platos
│   └── js/
│       ├── login.js              ← Capacidad 1
│       ├── menu.js               ← Capacidades 2, 3 y 4
│       └── consolidado.js        ← Capacidad 5
├── data/
│   ├── platos.json               ← datos del menú (Capacidad 2)
│   └── platos-vacio.json         ← para provocar el estado vacío
├── index.html
├── login.html
├── registro.html
├── miperfil.html
├── asistencia.html
├── Menu+Registrar-pedido.html
├── pedidos.html
├── administrar-menus.html
├── Generar-consolidado-(5).html
├── feriados.html
└── style*.css                    ← un CSS por pantalla
docs/
└── 3er-entrega/
    └── README.md                 ← este archivo
```
