// datos que vienen de datos.js (se llenan en cargarDatos)
let platos = [];
let menusDia = [];
let menuPlatos = [];
let feriados = [];
let pedidos = [];

let semanaEditando = ''; // lunes de la semana que se esta editando, ej: '2026-10-26'
let borrador = {}; // platos tildados en cada dia: { '2026-10-26': [1, 3], ... }

const idAdmin = 6; // usuario que crea los menus (fijo hasta que este el login)
const diasSemana = ['lunes', 'martes', 'miercoles', 'jueves', 'viernes', 'sabado'];

// selecciona los elementos que ya existen en el html
const cajaEstado = document.querySelector('#estado-admin');
const listaSemanas = document.querySelector('#lista-semanas');
const editor = document.querySelector('#editor');
const diasEditor = document.querySelector('#dias-editor');
const cajaError = document.querySelector('#error-admin');
const cajaExito = document.querySelector('#exito-admin');

// nombresLindos, formatearFecha, textoSemana, sumarDias y lunesDe estan en datos.js

// funcion que muestra un mensaje de estado (cargando, vacio o error)
function mostrarMensaje(texto, tipo) {
    cajaEstado.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`;
}

// funcion que trae los datos y dibuja la lista de semanas
async function cargarDatos() {
    mostrarMensaje('Cargando menús...', 'cargando'); // estado cargando, antes del await

    try {
        platos = await obtenerPlatos(); // las funciones obtener... estan en datos.js
        menusDia = await obtenerMenusDia();
        menuPlatos = await obtenerMenuPlatos();
        feriados = await obtenerFeriados();
        pedidos = await obtenerPedidos();

        cajaEstado.innerHTML = '';
        renderSemanas();
    } catch (error) {
        console.error('No se pudieron cargar los menús:', error); // para el programador
        mostrarMensaje('No pudimos cargar los menús. Probá recargar la página.', 'error'); // estado error
    }
}

// ---------- consultas ----------

// lunes de cada semana que tiene menus (sin repetir, de la mas vieja a la mas nueva)
function semanasExistentes() {
    const semanas = [];
    for (const menu of menusDia) {
        if (!semanas.includes(lunesDe(menu.fecha))) {
            semanas.push(lunesDe(menu.fecha));
        }
    }
    return semanas.sort(); // las fechas 'AAAA-MM-DD' se ordenan bien como texto
}

// menus (MenuDia) de una semana
function menusDeLaSemana(lunes) {
    return menusDia.filter(menu => lunesDe(menu.fecha) === lunes);
}

// true si todos los menus de la semana estan publicados
function estaPublicada(lunes) {
    const menus = menusDeLaSemana(lunes);
    return menus.length > 0 && menus.filter(menu => menu.publicado).length === menus.length;
}

// true si algun empleado ya pidio para esa semana (no cancelado)
function tienePedidos(lunes) {
    return pedidos.find(pedido =>
        pedido.estado !== 'CANCELADO' &&
        lunesDe(pedido.pedidoDias[0].fechaEntrega) === lunes
    ) !== undefined;
}

// siguiente id libre de una lista (como lo haria la base de datos)
function siguienteId(lista) {
    let mayor = 0;
    for (const item of lista) {
        if (item.id > mayor) {
            mayor = item.id;
        }
    }
    return mayor + 1;
}

// ---------- dibujar ----------

// lista de semanas con su estado (la mas nueva primero)
function renderSemanas() {
    const semanas = semanasExistentes();

    if (semanas.length === 0) {
        listaSemanas.innerHTML = '<p class="mensaje vacio">Todavía no hay menús cargados. Creá el de una semana nueva.</p>'; // estado vacio
        return;
    }

    let html = '';
    for (let i = semanas.length - 1; i >= 0; i--) {
        const lunes = semanas[i];
        const publicada = estaPublicada(lunes);
        let clase = 'semana-item';
        if (lunes === semanaEditando) {
            clase = clase + ' activa'; // la que se esta editando
        }
        html = html + `<article class="${clase}">
            <span class="fecha">Semana del ${textoSemana(lunes)}</span>
            <span class="estado ${publicada ? 'estado-publicado' : 'estado-borrador'}">${publicada ? 'Publicado' : 'Borrador'}</span>
            <button type="button" class="btn-editar" data-semana="${lunes}">${tienePedidos(lunes) ? 'Ver' : 'Editar'}</button>
        </article>`;
    }
    listaSemanas.innerHTML = html;
}

// abre el editor con los platos que ya tiene esa semana
function abrirEditor(lunes) {
    semanaEditando = lunes;
    borrador = {};
    for (let i = 0; i < diasSemana.length; i++) {
        const fecha = sumarDias(lunes, i);
        const menu = menusDia.find(m => m.fecha === fecha);
        borrador[fecha] = [];
        if (menu !== undefined) {
            for (const mp of menuPlatos.filter(mp => mp.idMenuDia === menu.id)) {
                borrador[fecha].push(mp.idPlato); // platos que ya estaban
            }
        }
    }
    limpiarMensajes();
    renderSemanas();
    renderEditor();
}

// dibuja una tarjeta por dia con los platos para tildar
function renderEditor() {
    const bloqueada = tienePedidos(semanaEditando);
    const existe = menusDeLaSemana(semanaEditando).length > 0;

    document.querySelector('#titulo-editor').textContent = `Semana del ${textoSemana(semanaEditando)}`;

    let estado = 'Semana nueva: todavía no está guardada.';
    if (bloqueada) {
        estado = 'Esta semana ya tiene pedidos: el menú se puede ver pero no modificar.';
    } else if (existe && estaPublicada(semanaEditando)) {
        estado = 'Publicado: los empleados ya lo ven.';
    } else if (existe) {
        estado = 'Borrador: los empleados todavía no lo ven.';
    }
    document.querySelector('#estado-editor').textContent = estado;

    let html = '';
    for (let i = 0; i < diasSemana.length; i++) {
        const fecha = sumarDias(semanaEditando, i);
        const titulo = `${nombresLindos[diasSemana[i]]} ${formatearFecha(fecha)}`;

        if (feriados.includes(fecha)) {
            html = html + `<article class="card-dia feriado"><h3>${titulo}</h3><hr><p class="nota-dia">Feriado: sin servicio</p></article>`;
            continue; // pasa al dia siguiente
        }

        let opciones = '';
        for (const plato of platos) {
            const tildado = borrador[fecha].includes(plato.id);
            opciones = opciones + `<label class="opcion-check">
                <input type="checkbox" data-fecha="${fecha}" value="${plato.id}" ${tildado ? 'checked' : ''} ${bloqueada ? 'disabled' : ''}>
                ${plato.nombre} <small>${plato.categoria}</small>
            </label>`;
        }
        if (borrador[fecha].length === 0) {
            opciones = opciones + '<p class="nota-dia">Sin menú este día</p>';
        }
        html = html + `<article class="card-dia"><h3>${titulo}</h3><hr>${opciones}</article>`;
    }
    diasEditor.innerHTML = html;

    // si tiene pedidos, todo queda de solo lectura
    editor.classList.toggle('editor-bloqueado', bloqueada);
    document.querySelector('#btn-guardar').disabled = bloqueada;
    document.querySelector('#btn-publicar').disabled = bloqueada;
    document.querySelector('#btn-duplicar').disabled = bloqueada;
    editor.classList.remove('oculto');
}

// ---------- guardar ----------

// guarda los menus (MenuDia) y sus platos (MenuPlato) de la semana
async function guardarSemana(publicar) {
    limpiarMensajes();

    if (tienePedidos(semanaEditando)) {
        mostrarError('Esta semana ya tiene pedidos: no se puede modificar.');
        return;
    }

    // tiene que haber al menos un dia con platos
    let diasConPlatos = 0;
    for (let i = 0; i < diasSemana.length; i++) {
        const fecha = sumarDias(semanaEditando, i);
        if (!feriados.includes(fecha) && borrador[fecha].length > 0) {
            diasConPlatos = diasConPlatos + 1;
        }
    }
    if (diasConPlatos === 0) {
        mostrarError('Elegí al menos un plato para algún día.');
        return;
    }

    for (let i = 0; i < diasSemana.length; i++) {
        const fecha = sumarDias(semanaEditando, i);
        let menu = menusDia.find(m => m.fecha === fecha);
        let ids = borrador[fecha];
        if (feriados.includes(fecha)) {
            ids = []; // los feriados no tienen menu
        }

        // borra los platos viejos de ese dia (se vuelven a cargar abajo)
        if (menu !== undefined) {
            menuPlatos = menuPlatos.filter(mp => mp.idMenuDia !== menu.id);
        }

        // dia sin platos: si tenia menu, se borra
        if (ids.length === 0) {
            if (menu !== undefined) {
                menusDia = menusDia.filter(m => m.id !== menu.id);
            }
            continue;
        }

        // dia con platos: si no tenia menu, se crea (MenuDia del DER)
        if (menu === undefined) {
            menu = {
                id: siguienteId(menusDia),
                fecha: fecha,
                descripcion: `Menú del ${nombresLindos[diasSemana[i]].toLowerCase()}`,
                publicado: false,
                stockTotal: 60,
                idUsuarioCreador: idAdmin
            };
            menusDia.push(menu);
        }
        menu.publicado = publicar;

        // un MenuPlato por cada plato tildado
        for (const idPlato of ids) {
            menuPlatos.push({ id: siguienteId(menuPlatos), idMenuDia: menu.id, idPlato: idPlato, stockDisponible: 20 });
        }
    }

    menusDia.sort((a, b) => (a.fecha < b.fecha ? -1 : 1)); // ordena por fecha
    await guardarMenusDia(menusDia); // guarda en datos.js
    await guardarMenuPlatos(menuPlatos);

    if (publicar) {
        mostrarExito(`Menú de la semana del ${textoSemana(semanaEditando)} publicado: los empleados ya pueden pedir.`);
    } else {
        mostrarExito('Borrador guardado. Los empleados todavía no lo ven.');
    }
    renderSemanas();
    renderEditor();
}

// ---------- eventos ----------

// boton Editar / Ver de cada semana
listaSemanas.addEventListener('click', function (evento) {
    const boton = evento.target.closest('button[data-semana]');
    if (boton) {
        abrirEditor(boton.dataset.semana);
    }
});

// tildar o destildar un plato de un dia
diasEditor.addEventListener('change', function (evento) {
    const check = evento.target;
    const fecha = check.dataset.fecha;
    const idPlato = Number(check.value); // Number porque value devuelve texto

    if (check.checked) {
        borrador[fecha].push(idPlato);
    } else {
        borrador[fecha] = borrador[fecha].filter(id => id !== idPlato);
    }
    limpiarMensajes();
    renderEditor();
});

// crear una semana nueva: la proxima que todavia no tenga menu
document.querySelector('#btn-nueva-semana').addEventListener('click', function () {
    let lunes = lunesSemanaQueViene();
    while (semanasExistentes().includes(lunes)) {
        lunes = sumarDias(lunes, 7); // si ya existe, prueba con la siguiente
    }
    abrirEditor(lunes);
});

// copiar los platos de la semana anterior
document.querySelector('#btn-duplicar').addEventListener('click', function () {
    const anterior = sumarDias(semanaEditando, -7);
    if (menusDeLaSemana(anterior).length === 0) {
        mostrarError(`La semana del ${textoSemana(anterior)} no tiene menú para copiar.`);
        return;
    }

    for (let i = 0; i < diasSemana.length; i++) {
        const fecha = sumarDias(semanaEditando, i);
        const menuAnterior = menusDia.find(m => m.fecha === sumarDias(anterior, i)); // mismo dia, una semana antes
        borrador[fecha] = [];
        if (menuAnterior !== undefined && !feriados.includes(fecha)) {
            for (const mp of menuPlatos.filter(mp => mp.idMenuDia === menuAnterior.id)) {
                borrador[fecha].push(mp.idPlato);
            }
        }
    }
    limpiarMensajes();
    mostrarExito('Platos copiados. Revisalos y guardá o publicá.');
    renderEditor();
});

document.querySelector('#btn-guardar').addEventListener('click', function () {
    guardarSemana(false); // guarda como borrador
});

document.querySelector('#btn-publicar').addEventListener('click', function () {
    guardarSemana(true); // publica (CU013)
});

// ---------- mensajes ----------
function mostrarError(mensaje) {
    cajaError.textContent = mensaje;
    cajaError.classList.add('visible');
}

function mostrarExito(mensaje) {
    cajaExito.textContent = mensaje;
    cajaExito.classList.add('visible');
}

function limpiarMensajes() {
    cajaError.textContent = '';
    cajaError.classList.remove('visible');
    cajaExito.textContent = '';
    cajaExito.classList.remove('visible');
}

cargarDatos(); // arranca la carga al abrir la pagina
