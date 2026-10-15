// datos que vienen de datos.js (se llenan en cargarDatos)
let feriados = []; // fechas marcadas como feriado, ej: ['2026-10-12', ...]
let pedidos = [];

// selecciona los elementos que ya existen en el html
const inputMes = document.querySelector('#mes');
const calendario = document.querySelector('#calendario');
const form = document.querySelector('#form-feriados');
const cajaError = document.querySelector('#error-feriados');
const cajaExito = document.querySelector('#exito-feriados');
const cajaEstado = document.querySelector('#estado-feriados');

// fechaTexto y formatearFecha estan en datos.js

// funcion que trae los feriados y dibuja el mes actual
async function cargarDatos() {
    cajaEstado.innerHTML = '<p class="mensaje cargando">Cargando feriados...</p>'; // estado cargando

    try {
        const usuario = await exigirSesion(true); // solo el usuario del restaurante (datos.js)
        if (usuario === null) {
            return; // no hay sesion: ya se fue al login
        }

        feriados = await obtenerFeriados(); // las funciones obtener... estan en datos.js
        pedidos = await obtenerPedidos();

        const partes = fechaTexto(ahora()).split('-'); // ['2026', '10', '14']
        inputMes.value = `${partes[0]}-${partes[1]}`; // arranca en el mes actual, ej: '2026-10'

        cajaEstado.innerHTML = '';
        renderCalendario();
    } catch (error) {
        console.error('No se pudieron cargar los feriados:', error); // para el programador
        cajaEstado.innerHTML = '<p class="mensaje error">No pudimos cargar los feriados. Probá recargar la página.</p>'; // estado error
    }
}

// true si algun empleado ya pidio para ese dia (no cancelado)
function tienePedidosEseDia(fecha) {
    return pedidos.find(pedido =>
        pedido.estado !== 'CANCELADO' &&
        pedido.pedidoDias.find(pd => pd.fechaEntrega === fecha) !== undefined
    ) !== undefined;
}

// funcion que arma la celda de un dia
function crearCelda(fecha, numero) {
    const dia = new Date(fecha + 'T00:00:00');

    if (dia.getDay() === 0) {
        return `<td class="finde"><label>${numero}</label></td>`; // domingo: no hay servicio
    }

    if (feriados.includes(fecha)) {
        return `<td class="feriado"><label><input type="checkbox" data-fecha="${fecha}" checked> ${numero}</label><span>FERIADO</span></td>`;
    }

    return `<td><label><input type="checkbox" data-fecha="${fecha}"> ${numero}</label></td>`;
}

// funcion que dibuja el calendario del mes elegido (filas de lunes a domingo)
function renderCalendario() {
    const partes = inputMes.value.split('-'); // ['2026', '10']
    const anio = Number(partes[0]);
    const mes = Number(partes[1]);

    const primerDia = new Date(anio, mes - 1, 1);
    const diasDelMes = new Date(anio, mes, 0).getDate(); // el dia 0 del mes siguiente es el ultimo de este
    const espacios = (primerDia.getDay() + 6) % 7; // celdas vacias antes del dia 1 (la semana empieza el lunes)

    let html = '';
    let fila = '';
    let celdas = 0;

    // celdas vacias hasta llegar al dia 1
    for (let i = 0; i < espacios; i++) {
        fila = fila + '<td class="fuera-mes"></td>';
        celdas = celdas + 1;
    }

    for (let numero = 1; numero <= diasDelMes; numero++) {
        const fecha = `${inputMes.value}-${String(numero).padStart(2, '0')}`; // ej: '2026-10-05'
        fila = fila + crearCelda(fecha, numero);
        celdas = celdas + 1;

        if (celdas === 7) { // se completo la semana: cierra la fila
            html = html + `<tr>${fila}</tr>`;
            fila = '';
            celdas = 0;
        }
    }

    // completa la ultima fila con celdas vacias
    if (celdas > 0) {
        while (celdas < 7) {
            fila = fila + '<td class="fuera-mes"></td>';
            celdas = celdas + 1;
        }
        html = html + `<tr>${fila}</tr>`;
    }

    calendario.innerHTML = html;
}

// tildar o destildar un dia
calendario.addEventListener('change', function (evento) {
    const check = evento.target;
    const fecha = check.dataset.fecha;
    limpiarMensajes();

    if (check.checked) {
        if (tienePedidosEseDia(fecha)) {
            check.checked = false;
            mostrarError(`No se puede marcar el ${formatearFecha(fecha)} como feriado: ya hay pedidos para ese día.`);
            return;
        }
        feriados.push(fecha);
    } else {
        feriados = feriados.filter(f => f !== fecha);
    }
    renderCalendario();
});

// cambiar de mes
inputMes.addEventListener('change', function () {
    if (inputMes.value === '') {
        return; // si se borro el mes, deja el calendario como estaba
    }
    renderCalendario();
});

// guardar los feriados
form.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina
    feriados.sort(); // las fechas 'AAAA-MM-DD' se ordenan bien como texto
    await guardarFeriados(feriados); // guarda en datos.js
    limpiarMensajes();
    cajaExito.textContent = 'Feriados guardados. Esos días no tienen menú ni se reciben pedidos.';
    cajaExito.classList.add('visible');
});

// ---------- mensajes ----------
function mostrarError(mensaje) {
    cajaError.textContent = mensaje;
    cajaError.classList.add('visible');
}

function limpiarMensajes() {
    cajaError.textContent = '';
    cajaError.classList.remove('visible');
    cajaExito.textContent = '';
    cajaExito.classList.remove('visible');
}

cargarDatos(); // arranca la carga al abrir la pagina
