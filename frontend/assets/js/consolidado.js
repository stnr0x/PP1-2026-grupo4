// datos que vienen de datos.js (se llenan en cargarDatos)
let platos = [];
let menusDia = [];
let menuPlatos = [];
let pedidos = [];
let usuarios = [];
let semanaGenerada = ''; // semana del ultimo consolidado generado

//seleccionamos los elementos que ya existe en el html
const selectSemana = document.querySelector('#semana');
const btnGenerar = document.querySelector('#btn-generar');
const cajaError = document.querySelector('#error-consolidado');
const mensajeExito = document.querySelector('#mensaje-exito');
const cajaEstado = document.querySelector('#estado-consolidado');
const resultado = document.querySelector('#resultado-consolidado');

// nombresLindos, nombreDia, formatearFecha, textoSemana, calcularPlazo y textoPlazo estan en datos.js

// funcion que muestra un mensaje de estado (cargando, vacio o error) y esconde el resultado
function mostrarMensaje(texto, tipo) {
    cajaEstado.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`;
    resultado.classList.add('oculto');
}

// funcion que trae los datos y arma la lista de semanas
async function cargarDatos() {
    mostrarMensaje('Cargando datos...', 'cargando'); // estado cargando, antes del await

    try {
        const usuario = await exigirSesion(true); // solo el usuario del restaurante (datos.js)
        if (usuario === null) {
            return; // no hay sesion: ya se fue al login
        }

        platos = await obtenerPlatos(); // las funciones obtener... estan en datos.js
        menusDia = await obtenerMenusDia();
        menuPlatos = await obtenerMenuPlatos();
        pedidos = await obtenerPedidos(); // ya vienen confirmados si se vencio el plazo
        usuarios = await obtenerUsuarios();

        menusDia = menusDia.filter(menu => menu.publicado); // solo los menus publicados

        if (menusDia.length === 0) {
            mostrarMensaje('Todavía no hay menús publicados para consolidar.', 'vacio'); // estado vacio
            return;
        }

        cargarSemanas();
        cajaEstado.innerHTML = ''; // saca el mensaje de cargando
    } catch (error) {
        console.error('No se pudieron cargar los datos:', error); // para el programador
        mostrarMensaje('No pudimos cargar los datos. Probá recargar la página.', 'error'); // estado error
    }
}

// llena el select con las semanas que tienen menu publicado (sin repetir)
function cargarSemanas() {
    const semanas = [];
    for (const menu of menusDia) {
        if (!semanas.includes(textoSemana(menu.fecha))) {
            semanas.push(textoSemana(menu.fecha));
            selectSemana.innerHTML = selectSemana.innerHTML + `<option value="${menu.fecha}">${textoSemana(menu.fecha)}</option>`;
        }
    }
}

// ---------- calculos ----------

// devuelve los pedidos de una semana con cierto estado
function pedidosDeLaSemana(fecha, estado) {
    const semana = textoSemana(fecha);
    return pedidos.filter(pedido =>
        pedido.estado === estado &&
        textoSemana(pedido.pedidoDias[0].fechaEntrega) === semana
    );
}

// todos los dias pedidos (PedidoDia) de una lista de pedidos, en un solo array
function todosLosDias(lista) {
    const dias = [];
    for (const pedido of lista) {
        for (const pd of pedido.pedidoDias) {
            dias.push(pd);
        }
    }
    return dias;
}

// ---------- dibujar ----------

// cantidades de cada plato por dia (la lista del dia para cocina)
function renderTotalesPorDia(fecha, confirmados) {
    const semana = textoSemana(fecha);
    const menusSemana = menusDia.filter(menu => textoSemana(menu.fecha) === semana);
    const diasPedidos = todosLosDias(confirmados);
    let html = '';

    for (const menu of menusSemana) {
        const opciones = menuPlatos.filter(mp => mp.idMenuDia === menu.id); // platos de ese dia
        let filas = '';
        let totalDia = 0;

        for (const mp of opciones) {
            const plato = platos.find(p => p.id === mp.idPlato);
            const cantidad = diasPedidos.filter(pd => pd.idMenuDia === menu.id && pd.idPlato === mp.idPlato).length; // filtrar y contar
            totalDia = totalDia + cantidad;
            let clase = '';
            if (cantidad === 0) {
                clase = 'sin-pedidos'; // en gris si nadie lo pidio
            }
            filas = filas + `<li class="${clase}"><span>${plato.nombre}</span> <strong>× ${cantidad}</strong></li>`;
        }

        html = html + `<div class="tarjeta-dia">
            <h3>${nombresLindos[nombreDia(menu.fecha)]} ${formatearFecha(menu.fecha)}</h3>
            <ul>${filas}<li class="total-dia"><span>Total del día</span> <span>${totalDia}</span></li></ul>
        </div>`;
    }
    document.querySelector('#totales-dia').innerHTML = html;
}

//funcion que arma el html de una fila de la tabla a partir de un pedido
//solo devuelve el texto, todavia no lo muestra en pantalla
function crearFila(pedido) {
    const usuario = usuarios.find(u => u.id === pedido.idUsuario);
    let detalle = '';
    for (const pd of pedido.pedidoDias) {
        const plato = platos.find(p => p.id === pd.idPlato);
        detalle = detalle + `${nombresLindos[nombreDia(pd.fechaEntrega)]} ${formatearFecha(pd.fechaEntrega)}: ${plato.nombre}<br>`;
    }
    return `<tr>
              <td>${usuario.nombre} ${usuario.apellido}</td>
              <td>${detalle}</td>
            </tr>`;
}

//funcion que dibuja todas las filas de la lista en la tabla
function renderConsolidado(lista) {
    const contenedor = document.querySelector('#lista-consolidado');
    let html = '';
    for (const pedido of lista) {
        html = html + crearFila(pedido);
    }
    contenedor.innerHTML = html; //recien aca se dibuja en pantalla. mete todas las fila en el tbody
}

// totales de cada plato en toda la semana
function renderTotalesSemana(confirmados) {
    const diasPedidos = todosLosDias(confirmados);
    let html = '';

    for (const plato of platos) {
        const cantidad = diasPedidos.filter(pd => pd.idPlato === plato.id).length; // filtrar y contar
        if (cantidad > 0) {
            html = html + `<li><span>${plato.nombre}</span> <strong>× ${cantidad}</strong></li>`;
        }
    }

    document.querySelector('#totales-semana').innerHTML = html;
    document.querySelector('#total-empleados').textContent = confirmados.length; // un pedido por empleado por semana
    document.querySelector('#total-menus').textContent = diasPedidos.length; // cada dia pedido es un menu
}

// ---------- botones ----------

//escucha el click del boton generar
btnGenerar.addEventListener('click', function () {
    const semanaVal = selectSemana.value; // aca lee la semana que el usuario selecciono

    //validacion de que el usuario haya seleccionado una semana
    if (semanaVal === '') {
        mostrarError('Por favor seleccione una semana del consolidado valida.');
        ocultarExito();
        return; //aca corta, asi no se genera una tabla si hay un error
    }

    limpiarError();
    const confirmados = pedidosDeLaSemana(semanaVal, 'CONFIRMADO');
    const pendientes = pedidosDeLaSemana(semanaVal, 'PENDIENTE');

    // aviso de los pendientes que todavia pueden cambiar (no entran en el consolidado)
    const aviso = document.querySelector('#aviso-pendientes');
    if (pendientes.length > 0) {
        aviso.textContent = `Hay ${pendientes.length} pedido(s) pendiente(s) que no están incluidos: se pueden modificar hasta el ${textoPlazo(calcularPlazo(semanaVal))} y después se confirman solos.`;
        aviso.classList.remove('oculto');
    } else {
        aviso.classList.add('oculto');
    }

    if (confirmados.length === 0) {
        ocultarExito();
        mostrarMensaje('Todavía no hay pedidos confirmados para esa semana.', 'vacio'); // estado vacio
        semanaGenerada = '';
        return;
    }

    // si la semana es valida y hay pedidos, dibujamos todo
    renderTotalesPorDia(semanaVal, confirmados);
    renderConsolidado(confirmados);
    renderTotalesSemana(confirmados);
    cajaEstado.innerHTML = '';
    resultado.classList.remove('oculto');
    semanaGenerada = textoSemana(semanaVal);
    mostrarExito(`Consolidado generado con éxito para la semana del ${semanaGenerada}.`);
});

// descargar PDF: abre la ventana de impresion del navegador (ahi se elige "Guardar como PDF")
document.querySelector('#btn-pdf').addEventListener('click', function () {
    if (semanaGenerada === '') {
        mostrarError('Primero generá el consolidado.');
        return;
    }
    window.print();
});

// enviar al proveedor: por ahora solo avisa (el envio real lo hara el backend)
document.querySelector('#btn-enviar').addEventListener('click', function () {
    if (semanaGenerada === '') {
        mostrarError('Primero generá el consolidado.');
        return;
    }
    limpiarError();
    mostrarExito(`Consolidado de la semana del ${semanaGenerada} enviado a cocina.`);
});

//funcion para mostrar el mensaje de error
function mostrarError(mensaje) {
    cajaError.textContent = mensaje;//pone el texto de error
    cajaError.classList.add('visible');//agrega la clase para que se vea con el estilo del css
}
//funcion para limpiar el error
function limpiarError() {
    cajaError.textContent = '';//aca borra el texto de error
    cajaError.classList.remove('visible');//aca quita la clase para que no se vea el error
}
//funcion para mostrar el mensaje de exito
function mostrarExito(mensaje) {
    mensajeExito.textContent = mensaje;//pone el texto de exito
    mensajeExito.classList.add('visible');// aca lo hace visible
}
//funcion para ocultar el mensaje de exito
function ocultarExito() {
    if (mensajeExito){ //aca se fija que el elemento exista antes de intentar modificarlo
        mensajeExito.textContent = '';
        mensajeExito.classList.remove('visible');
    }
}

cargarDatos(); // arranca la carga al abrir la pagina
