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

// ---------- fechas (las usan todas las pantallas) ----------

// nombres de los dias en el orden de getDay() (0 = domingo)
const nombresDias = ['domingo', 'lunes', 'martes', 'miercoles', 'jueves', 'viernes', 'sabado'];

// nombres con tilde para mostrar
const nombresLindos = {
    lunes: 'Lunes', martes: 'Martes', miercoles: 'Miércoles',
    jueves: 'Jueves', viernes: 'Viernes', sabado: 'Sábado'
};

// textos de los estados del pedido (DER: PENDIENTE, CONFIRMADO, CANCELADO)
const textosEstado = {
    PENDIENTE: 'Pendiente', CONFIRMADO: 'Confirmado', CANCELADO: 'Cancelado'
};

// fecha y hora actual
function ahora() {
    return new Date();
    //return new Date('2026-10-15T13:00:00'); // para probar: jueves despues de las 12 (plazo vencido)
}

// devuelve el nombre del dia de una fecha, ej: '2026-10-19' -> 'lunes'
function nombreDia(fecha) {
    const dia = new Date(fecha + 'T00:00:00'); // T00 para que no tome la fecha en UTC y de el dia anterior
    return nombresDias[dia.getDay()];
}

// pasa '2026-10-19' a '19/10'
function formatearFecha(fecha) {
    const partes = fecha.split('-'); // ['2026', '10', '19']
    return partes[2] + '/' + partes[1];
}

// semana (lunes a sabado) de una fecha, ej: '2026-10-20' -> '19/10 al 24/10'
function textoSemana(fecha) {
    const dia = new Date(fecha + 'T00:00:00');
    const lunes = new Date(dia);
    lunes.setDate(dia.getDate() - ((dia.getDay() + 6) % 7)); // vuelve al lunes de esa semana
    const sabado = new Date(lunes);
    sabado.setDate(lunes.getDate() + 5);
    return `${lunes.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit' })} al ${sabado.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit' })}`;
}

// plazo para pedir o modificar: el jueves anterior a esa semana, a las 12 hs
function calcularPlazo(fechaDeLaSemana) {
    const dia = new Date(fechaDeLaSemana + 'T00:00:00');
    const lunes = new Date(dia);
    lunes.setDate(dia.getDate() - ((dia.getDay() + 6) % 7)); // vuelve al lunes de esa semana
    const plazo = new Date(lunes);
    plazo.setDate(lunes.getDate() - 4); // 4 dias antes del lunes es el jueves anterior
    plazo.setHours(12, 0, 0, 0); // a las 12 hs
    return plazo;
}

// texto del plazo, ej: 'jueves 15/10 a las 12 hs'
function textoPlazo(plazo) {
    const dia = String(plazo.getDate()).padStart(2, '0');
    const mes = String(plazo.getMonth() + 1).padStart(2, '0');
    return `jueves ${dia}/${mes} a las 12 hs`;
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

// ---------- usuarios ----------
async function obtenerUsuarios() {
    return obtenerDesdeJson('usuarios', 'data/usuarios.json');
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

// devuelve los pedidos; los PENDIENTES con el plazo vencido pasan solos a CONFIRMADO
async function obtenerPedidos() {
    const pedidos = await obtenerDesdeJson('pedidos', 'data/pedidos.json'); // la primera vez trae los pedidos de ejemplo

    let huboCambios = false;
    for (const pedido of pedidos) {
        const plazo = calcularPlazo(pedido.pedidoDias[0].fechaEntrega);
        if (pedido.estado === 'PENDIENTE' && ahora() >= plazo) {
            pedido.estado = 'CONFIRMADO'; // se confirma automaticamente
            huboCambios = true;
        }
    }
    if (huboCambios) {
        escribir('pedidos', pedidos);
    }
    return pedidos;
}

async function guardarPedido(pedido) {
    const pedidos = await obtenerPedidos();
    pedido.id = pedidos.length + 1; // el id lo pone el guardado, como lo hara la base de datos
    pedidos.push(pedido);
    escribir('pedidos', pedidos);
}

// reemplaza un pedido que ya existe (mismo id) por su version nueva
async function actualizarPedido(pedidoActualizado) {
    const pedidos = await obtenerPedidos();
    for (let i = 0; i < pedidos.length; i++) {
        if (pedidos[i].id === pedidoActualizado.id) {
            pedidos[i] = pedidoActualizado;
        }
    }
    escribir('pedidos', pedidos);
}

// pedido (no cancelado) del usuario para la semana de esos menus, o undefined si no hay
function buscarPedidoDeLaSemana(pedidos, menusDia, idUsuario) {
    return pedidos.find(pedido =>
        pedido.idUsuario === idUsuario &&
        pedido.estado !== 'CANCELADO' &&
        menusDia.find(menu => menu.fecha === pedido.pedidoDias[0].fechaEntrega) !== undefined
    );
}
