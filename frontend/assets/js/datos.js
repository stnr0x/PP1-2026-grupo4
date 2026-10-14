// guardado temporal de datos (sessionStorage): se comparte entre pantallas
// y se borra al cerrar la pestaña. cuando este el backend, solo cambia este archivo.

// lee un dato guardado (devuelve null si no hay nada)
function leer(clave) {
    const texto = sessionStorage.getItem(clave);
    if (texto === null) {
        return null;
    }
    return JSON.parse(texto); // convierte el texto guardado en objetos
}

// guarda un dato (sessionStorage solo guarda texto)
function escribir(clave, valor) {
    sessionStorage.setItem(clave, JSON.stringify(valor)); // convierte los objetos en texto
}

// la primera vez trae el json, y despues usa lo guardado
async function obtenerDesdeJson(clave, ruta) {
    const guardado = leer(clave);
    if (guardado !== null) {
        return guardado; // ya estaba en el storage
    }
    const respuesta = await fetch(ruta);
    const datos = await respuesta.json();
    escribir(clave, datos); // lo guarda para las proximas veces
    return datos;
}

// ---------- platos y menus ----------
async function obtenerPlatos() {
    return obtenerDesdeJson('platos', 'data/platos.json');
}

async function obtenerMenusDia() {
    return obtenerDesdeJson('menusDia', 'data/menus-dia.json');
    //return obtenerDesdeJson('menusDia', 'data/menus-dia-vacio.json'); // para probar el estado vacio
    //return obtenerDesdeJson('menusDia', 'data/noexiste.json'); // para probar el estado error
}

async function obtenerMenuPlatos() {
    return obtenerDesdeJson('menuPlatos', 'data/menu-platos.json');
}

// ---------- asistencia ----------
async function obtenerAsistencia() {
    const asistencia = leer('asistencia');
    if (asistencia === null) {
        return []; // todavia no se configuro
    }
    return asistencia;
}

async function guardarAsistencia(dias) {
    escribir('asistencia', dias);
}

// ---------- pedidos ----------
async function obtenerPedidos() {
    const pedidos = leer('pedidos');
    if (pedidos === null) {
        return []; // todavia no hay pedidos
    }
    return pedidos;
}

async function guardarPedido(pedido) {
    const pedidos = await obtenerPedidos();
    pedido.id = pedidos.length + 1; // el id lo pone el guardado, como lo hara la base de datos
    pedidos.push(pedido);
    escribir('pedidos', pedidos);
}
