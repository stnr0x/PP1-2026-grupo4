// datos que viene de los json (se llenan en cargarDatos)
let platos = [];
let menusDia = [];
let menuPlatos = [];

let seleccion = {}; // plato elegido en cada menu: { idMenuDia: idPlato }
const pedidos = []; // pedidos guardados en memoria

// selecciona los elementos que ya existen en el html
const form = document.querySelector('#form-pedido');
const cajaError = document.querySelector('#error-pedido');
const contenedorPlatos = document.querySelector('#lista-platos');

// nombres de los dias en el orden de getDay() (0 = domingo)
const nombresDias = ['domingo', 'lunes', 'martes', 'miercoles', 'jueves', 'viernes', 'sabado'];

// funcion que muestra un mensaje en lugar de los platos (cargando, vacio o error)
function mostrarMensaje(texto, tipo) {
    contenedorPlatos.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`;
}

// funcion que trae los tres json y dibuja la pantalla
async function cargarDatos() {
    mostrarMensaje('Cargando el menú...', 'cargando'); // estado cargando, antes del await

    try {
        const respuestaPlatos = await fetch('data/platos.json');
        platos = await respuestaPlatos.json();

        const respuestaMenus = await fetch('data/menus-dia.json');
        //const respuestaMenus = await fetch('data/menus-dia-vacio.json'); // para probar el estado vacio
        //const respuestaMenus = await fetch('data/noexiste.json'); // para probar el estado error
        menusDia = await respuestaMenus.json();

        const respuestaMenuPlatos = await fetch('data/menu-platos.json');
        menuPlatos = await respuestaMenuPlatos.json();

        menusDia = menusDia.filter(menu => menu.publicado); // solo los menus publicados

        if (menusDia.length === 0) {
            mostrarMensaje('Todavía no hay menú publicado para esta semana.', 'vacio'); // estado vacio
            return;
        }

        mostrarSemana();
        renderMenuSemana();
        renderResumen();
    } catch (error) {
        console.error('No se pudo cargar el menú:', error); // para el programador
        mostrarMensaje('No pudimos cargar el menú. Probá recargar la página.', 'error'); // estado error
    }
}// devuelve el nombre del dia de una fecha, ej: '2026-10-05' -> 'lunes'
function nombreDia(fecha) {
    const dia = new Date(fecha + 'T00:00:00'); // T00 para que no tome la fecha en UTC y de el dia anterior
    return nombresDias[dia.getDay()];
}

// pasa '2026-10-05' a '05/10'
function formatearFecha(fecha) {
    const partes = fecha.split('-'); // ['2026', '10', '05']
    return partes[2] + '/' + partes[1];
}

// escribe la semana del pedido con la primera y la ultima fecha del menu
function mostrarSemana() {
    const primera = menusDia[0].fecha;
    const ultima = menusDia[menusDia.length - 1].fecha;
    document.querySelector('#semana-pedido').textContent = `${formatearFecha(primera)} al ${formatearFecha(ultima)}`;
}

// devuelve un array con los dias tildados, ej: ['lunes', 'miercoles']
function diasMarcados() {
    const marcados = document.querySelectorAll('input[name="dia"]:checked');
    const dias = [];
    for (const check of marcados) {
        dias.push(check.value);
    }
    return dias;
}
// funcion que arma el html de la tarjeta de un plato
function crearOpcion(menu, plato) {
    let clase = 'opcion-plato';
    const elegido = seleccion[menu.id]; // plato elegido en este dia (o undefined)

    if (elegido === plato.id) {
        clase = clase + ' seleccionado'; // es el plato elegido
    } else if (elegido !== undefined) {
        clase = clase + ' bloqueado'; // ya hay otro elegido en este dia
    }

    return `<article class="${clase}" data-menu="${menu.id}" data-plato="${plato.id}">
        <img src="${plato.imagen}" alt="${plato.nombre}">
        <h4>${plato.nombre}</h4>
        <p>${plato.descripcion}</p>
    </article>`;
}

// funcion que arma el bloque de un dia con sus platos
function crearBloqueDia(menu) {
    const opciones = menuPlatos.filter(mp => mp.idMenuDia === menu.id); // los platos de este menu
    let html = '';
    for (const mp of opciones) {
        const plato = platos.find(p => p.id === mp.idPlato); // busca los datos del plato
        html = html + crearOpcion(menu, plato);
    }
    return `<div class="dia-bloque">
        <h3>${menu.descripcion} · ${formatearFecha(menu.fecha)}</h3>
        <div class="opciones">${html}</div>
    </div>`;
}

// funcion que dibuja los dias marcados con sus platos
function renderMenuSemana() {
    const dias = diasMarcados();

    if (dias.length === 0) {
        mostrarMensaje('Marcá los días que venís para ver el menú.', 'vacio');
        return;
    }

    let html = '';
    for (const menu of menusDia) {
        if (dias.includes(nombreDia(menu.fecha))) { // solo los dias tildados
            html = html + crearBloqueDia(menu);
        }
    }
    contenedorPlatos.innerHTML = html;
}
// escucha los clicks en las tarjetas de platos
contenedorPlatos.addEventListener('click', function (evento) {
    const tarjeta = evento.target.closest('.opcion-plato'); // la tarjeta que se toco
    if (!tarjeta) {
        return; // se toco afuera de una tarjeta
    }

    const idMenu = Number(tarjeta.dataset.menu); // Number porque dataset devuelve texto
    const idPlato = Number(tarjeta.dataset.plato);

    if (seleccion[idMenu] === idPlato) {
        delete seleccion[idMenu]; // se toco el elegido: se deselecciona
    } else if (seleccion[idMenu] === undefined) {
        seleccion[idMenu] = idPlato; // el dia estaba libre: se elige
    } else {
        return; // esta bloqueado: no hace nada
    }

    renderMenuSemana();
    renderResumen();
});

// escucha cuando se tilda o destilda un dia
document.querySelector('.dias').addEventListener('change', function () {
    if (menusDia.length === 0) {
        return; // si no cargo el menu, deja el mensaje de vacio o error
    }

    const dias = diasMarcados();
    for (const menu of menusDia) {
        if (!dias.includes(nombreDia(menu.fecha))) {
            delete seleccion[menu.id]; // si se destildo el dia, se borra su plato elegido
        }
    }

    renderMenuSemana();
    renderResumen();
});

// funcion que dibuja el resumen con los platos elegidos
function renderResumen() {
    const contenedor = document.querySelector('#resumen-pedido');
    let html = '';

    for (const menu of menusDia) {
        const idPlato = seleccion[menu.id];
        if (idPlato !== undefined) {
            const plato = platos.find(p => p.id === idPlato);
            html = html + `<li><strong>${menu.descripcion} (${formatearFecha(menu.fecha)}):</strong> ${plato.nombre}</li>`;
        }
    }

    if (html === '') {
        contenedor.innerHTML = '<p class="resumen-vacio">Todavía no elegiste ningún plato.</p>';
        return;
    }
    contenedor.innerHTML = `<ul>${html}</ul>`;
}
// funcion que arma el html de un pedido guardado
function crearItemPedido(pedido) {
    let detalle = '';
    for (const pd of pedido.pedidoDias) {
        const menu = menusDia.find(m => m.id === pd.idMenuDia);
        const plato = platos.find(p => p.id === pd.idPlato);
        detalle = detalle + `<p>${menu.descripcion}: ${plato.nombre}</p>`;
    }
    return `<li class="card-pedido">
        <h4>Pedido #${pedido.id} · ${pedido.estado}</h4>
        ${detalle}
    </li>`;
}

// funcion que dibuja todos los pedidos guardados
function renderPedidos(lista) {
    const contenedor = document.querySelector('#lista-pedidos');
    let html = '';
    for (const pedido of lista) {
        html = html + crearItemPedido(pedido);
    }
    contenedor.innerHTML = html;
}

// funcion que guarda el pedido, va aparte para despues conectarla al backend
async function guardarPedido(pedido) {
    pedidos.push(pedido); // hoy se guarda en memoria
}

// funciones para mostrar y limpiar el error del pedido
function mostrarError(mensaje) {
    cajaError.textContent = mensaje;
    cajaError.classList.add('visible');
}

function limpiarError() {
    cajaError.textContent = '';
    cajaError.classList.remove('visible');
}

// escucha el envio del formulario
form.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina

    const dias = diasMarcados();
    if (dias.length === 0) {
        mostrarError('Marcá al menos un día de asistencia.'); // CU005 flujo 2A
        return;
    }

    const menusElegidos = menusDia.filter(menu => dias.includes(nombreDia(menu.fecha))); // menus de los dias tildados
    for (const menu of menusElegidos) {
        if (seleccion[menu.id] === undefined) {
            mostrarError('Elegí un plato para cada día que marcaste.');
            return;
        }
    }

    limpiarError();

    // arma un PedidoDia por cada dia elegido
    const pedidoDias = [];
    for (const menu of menusElegidos) {
        pedidoDias.push({
            id: pedidoDias.length + 1,
            fechaEntrega: menu.fecha,
            idMenuDia: menu.id,
            idPlato: seleccion[menu.id]
        });
    }

    // arma el Pedido con los nombres del DER
    const pedido = {
        id: pedidos.length + 1,
        fechaPedido: new Date().toLocaleDateString('en-CA'), // fecha de hoy en formato AAAA-MM-DD
        estado: 'PENDIENTE',
        cantidadPersonas: 1,
        idUsuario: 1,
        pedidoDias: pedidoDias
    };

    await guardarPedido(pedido); // guarda el pedido
    renderPedidos(pedidos); // muestra la lista sin recargar

    form.reset(); // destilda los dias
    seleccion = {}; // borra los platos elegidos
    renderMenuSemana();
    renderResumen();
});

cargarDatos(); // arranca la carga al abrir la pagina