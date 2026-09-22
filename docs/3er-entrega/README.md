# Entrega 3 — Frontend Codificado

**Grupo**: Grupo 4  
**Proyecto**: Sistema de pedidos de almuerzo  
**Fecha de entrega**: 24/09/2026  

---

## 1. Tabla de capacidades JS

| Capacidad | Pantalla | Archivo JS | CU de E1 | Qué hace |
|-----------|----------|-----------|----------|----------|
| Cap. 1 — Login validado | login.html | assets/js/login.js | CU-01 | Valida email y campos vacíos, muestra error en el DOM, redirige al menú. *Entregado por otro integrante del grupo.* |
| Cap. 2 — Listado desde datos | Menu+Registrar-pedido.html | assets/js/menu.js | CU-05, CU-06 | Trae platos desde `data/platos.json` con fetch, los renderiza en cards |
| Cap. 3 — Acción del usuario | Menu+Registrar-pedido.html | assets/js/menu.js | CU-06 | Formulario de registro de pedido, valida campos, agrega a lista de pedidos |
| Cap. 4 — Estados de interfaz | Menu+Registrar-pedido.html | assets/js/menu.js | CU-05 | Muestra estados de cargando, vacío y error para el listado de platos |
| Cap. 5 — Admin: Administrar Menús | administrar-menus.html | — | CU-12 | Pantalla estática con datos administrativos |

---

## 2. Inventario de pantallas

| Pantalla | Archivo | JS | Estado |
|----------|---------|----|--------|
| Inicio | index.html | — | Estática, links a todas las pantallas |
| Login | login.html | assets/js/login.js | Entregado por otro integrante |
| Registro | registro.html | — | Estática |
| Mi Perfil | miperfil.html | — | Estática |
| Asistencia | asistencia.html | — | Estática |
| Menú + Registrar pedido | Menu+Registrar-pedido.html | assets/js/menu.js | **Capacidades 2, 3, 4** |
| Administrar Menús | administrar-menus.html | — | Estática, Capacidad 5 |
| Generar Consolidado | Generar-consolidado-(5).html | — | Estática |
| Feriados | feriados.html | — | Estática |
| Mis pedidos | pedidos.html | — | Estática |

---

## 3. Decisiones del grupo

### Organización de archivos JS
Se usa un solo archivo JS por pantalla (`menu.js` para la pantalla 03). Cada pantalla tiene su propio archivo porque es más fácil de mantener y cada uno tiene su propia entidad (`platos`, `pedidos`). La organización es consistente: cada archivo contiene el array de datos, las funciones de render, y el manejo de eventos de esa pantalla.

### Capacidad 5 — Elección
Se eligió **Administrar Menús** (`administrar-menus.html`) como pantalla admin para la Capacidad 5. Se justifica contra el **CU-12 (Administrar Menús)** de la Entrega 1, que corresponde al rol de Administrador. Esta pantalla ya está maquetada y permite al admin crear/modificar/publicar menús, lo cual tiene sentido de cara a futuras capacidades (listar menús desde JSON, publicar desde formulario).

### Por qué no se tocó HTML/CSS de E2
La consigna indica explícitamente no rehacer el HTML/CSS de E2. Se mantuvieron las pantallas existentes y se les agregó comportamiento. No se crearon pantallas nuevas.

### Estado cargando/vacío/error
Se implementó la función `mostrarMensaje(texto, tipo, selector)` que reemplaza el contenido del contenedor con un mensaje CSS estilizado. Los estados son: `cargando`, `vacio` y `error`.

---

## 4. Declaración de uso de IA

| Herramienta | Para qué |
|-------------|----------|
| ChatGPT/OpenAI | Revisión de estructura de código, identificación de errores de sintaxis, orientación sobre uso de `fetch` y manejo de estados |
| Documentación de la consigna | Entendimiento de requisitos de la Entrega 3 |

---

## 5. Estructura del repo

```
frontend/
├── assets/
│   ├── css/      ← de E2
│   ├── img/      ← de E2
│   └── js/       ← nuevo
│       └── menu.js
├── data/         ← nuevo
│   └── platos.json
├── Menu+Registrar-pedido.html
├── administrar-menus.html
├── Generar-consolidado-(5).html
├── index.html
├── login.html
├── registro.html
├── miperfil.html
├── asistencia.html
├── feriados.html
├── pedidos.html
└── *.html        ← resto de pantallas E2
docs/
└── 3er-entrega/
    └── README.md  ← este archivo
```
