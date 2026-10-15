// datos que vienen de datos.js (se llenan en cargarDatos)
let platos = [];
let pedidos = [];

let cancelando = null; // id del pedido al que se le pregunta "¿seguro?" antes de cancelar

let idUsuario = null; // id del usuario que inicio sesion

// selecciona el contenedor que ya existe en el html
const contenedor = document.querySelector('#lista-mis-pedidos');

// nombresLindos, nombreDia, formatearFecha, textoSemana, calcularPlazo, textoPlazo y textosEstado estan en datos.js

// funcion que muestra un mensaje en lugar de la lista (cargando, vacio o error)
function mostrarMensaje(texto, tipo) {
    contenedor.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`;
}

// funcion que trae los pedidos y los dibuja
async function cargarDatos() {
    mostrarMensaje('Cargando tus pedidos...', 'cargando'); // estado cargando, antes del await

    try {
        const usuario = await exigirSesion(false); // solo empleados (datos.js)
        if (usuario === null) {
            return; // no hay sesion: ya se fue al login
        }
        idUsuario = usuario.id;

        platos = await obtenerPlatos();
        pedidos = await obtenerPedidos(); // ya vienen confirmados si se vencio el plazo
        renderPedidos();
    } catch (error) {
        console.error('No se pudieron cargar los pedidos:', error); // para el programador
        mostrarMensaje('No pudimos cargar tus pedidos. Probá recargar la página.', 'error'); // estado error
    }
}

// funcion que arma el html de la tarjeta de un pedido
function crearTarjetaPedido(pedido) {
    const primera = pedido.pedidoDias[0].fechaEntrega;
    const plazo = calcularPlazo(primera);
    const sePuedeModificar = pedido.estado === 'PENDIENTE' && ahora() < plazo;

    // un renglon por cada dia del pedido
    let detalle = '';
    for (const pd of pedido.pedidoDias) {
        const plato = platos.find(p => p.id === pd.idPlato);
        detalle = detalle + `<li><strong>${nombresLindos[nombreDia(pd.fechaEntrega)]} ${formatearFecha(pd.fechaEntrega)}:</strong> ${plato.nombre}</li>`;
    }

    // botones segun el estado
    let acciones = '';
    if (sePuedeModificar && cancelando === pedido.id) {
        // segundo paso de cancelar: pide confirmacion (CU010)
        acciones = `
            <span class="pregunta">¿Seguro que querés cancelar este pedido?</span>
            <button type="button" class="btn-peligro lleno" data-accion="cancelar-si" data-id="${pedido.id}">Sí, cancelar</button>
            <button type="button" data-accion="cancelar-no" data-id="${pedido.id}">No</button>`;
    } else if (sePuedeModificar) {
        acciones = `
            <a href="menu.html?editar=${pedido.id}">Modificar</a>
            <button type="button" class="btn-principal" data-accion="confirmar" data-id="${pedido.id}">Confirmar</button>
            <button type="button" class="btn-peligro" data-accion="cancelar" data-id="${pedido.id}">Cancelar</button>`;
    }

    let aviso = '';
    if (sePuedeModificar) {
        aviso = `<p class="aviso-plazo">Podés modificarlo o cancelarlo hasta el ${textoPlazo(plazo)}. Después se confirma automáticamente.</p>`;
    }

    return `<article class="tarjeta-pedido ${pedido.estado.toLowerCase()}">
        <div class="tarjeta-cabecera">
            <h2>Semana del ${textoSemana(primera)}</h2>
            <span class="badge badge-${pedido.estado.toLowerCase()}">${textosEstado[pedido.estado]}</span>
        </div>
        <ul class="detalle-pedido">${detalle}</ul>
        ${aviso}
        <div class="acciones-pedido">${acciones}</div>
    </article>`;
}

// funcion que dibuja todos los pedidos del usuario, del mas nuevo al mas viejo
function renderPedidos() {
    const misPedidos = pedidos.filter(p => p.idUsuario === idUsuario);

    if (misPedidos.length === 0) {
        mostrarMensaje('Todavía no hiciste ningún pedido. <a href="menu.html">Ir al menú</a>', 'vacio'); // estado vacio
        return;
    }

    let html = '';
    for (let i = misPedidos.length - 1; i >= 0; i--) { // recorre al reves: el ultimo pedido primero
        html = html + crearTarjetaPedido(misPedidos[i]);
    }
    contenedor.innerHTML = html;
}

// escucha los clicks en los botones de los pedidos
contenedor.addEventListener('click', async function (evento) {
    const boton = evento.target.closest('button[data-accion]'); // el boton que se toco
    if (!boton) {
        return; // se toco afuera de un boton (o el link de Modificar, que navega solo)
    }

    const id = Number(boton.dataset.id); // Number porque dataset devuelve texto
    const pedido = pedidos.find(p => p.id === id);

    if (boton.dataset.accion === 'confirmar') {
        pedido.estado = 'CONFIRMADO'; // CU008
        await actualizarPedido(pedido);
    } else if (boton.dataset.accion === 'cancelar') {
        cancelando = id; // muestra la pregunta
    } else if (boton.dataset.accion === 'cancelar-si') {
        pedido.estado = 'CANCELADO'; // CU010
        cancelando = null;
        await actualizarPedido(pedido);
    } else if (boton.dataset.accion === 'cancelar-no') {
        cancelando = null; // vuelve a los botones normales
    }

    renderPedidos();
});

cargarDatos(); // arranca la carga al abrir la pagina
