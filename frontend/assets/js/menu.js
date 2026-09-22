const contenedorPlatos = document.querySelector('#lista-platos');
const selectPlato = document.querySelector('#select-plato');
const cajaError = document.querySelector('#error-pedido');
const form = document.querySelector('#form-pedido');

let platos = [];

function crearTarjeta(plato){
return `
<article class="card-dia">
<img src="${plato.imagen}" alt="${plato.nombre}" style="width: 100%; border-radius: 8px;margin-bottom: 8px;">
<h3>${plato.nombre}</h3>
<p class="opcion"><em>${plato.descripcion}</em></p>
<p class="opcion">${plato.categoria}</p>
<p class="plazo">$${plato.precio}</p>
</article>
`}

function crearOpcionSelect(plato){
const option = document.createElement('option');
option.value = plato.nombre;
option.textContent = plato.nombre;
return option;}

function renderPlatos(lista){
    if (lista.length === 0) {
        contenedorPlatos.innerHTML = '<p style="color:#cccccc;text-align:center;">No hay platos disponibles.</p>';
        return;
    }
    let html = '';
    for (const plato of lista){
        html = html + crearTarjeta(plato);
    }
    contenedorPlatos.innerHTML = html;
}

function mostrarMensaje(texto, tipo, selector) {
    const contenedor = document.querySelector(selector);
    contenedor.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`;
}

async function cargarPlatos() {
    mostrarMensaje('Cargando el menú...', 'cargando', '#lista-platos');
    try {
        const respuesta = await fetch('data/platos.json');
        if (!respuesta.ok) throw new Error('HTTP error');
        const platos = await respuesta.json();
        if (platos.length === 0) {
            mostrarMensaje('Todavía no hay platos cargados para esta semana.', 'vacio', '#lista-platos');
            return;
        }
        renderPlatos(platos);
        for (const plato of platos) {
            selectPlato.appendChild(crearOpcionSelect(plato));
        }
    } catch (error) {
        console.error('No se pudieron cargar los platos:', error);
        mostrarMensaje('No pudimos cargar el menú. Probá recargar la página.', 'error', '#lista-platos');
    }
}
cargarPlatos();

const pedidos =[];

function crearItemPedido(pedido){
    return `<li>${pedido.empleado} - ${pedido.plato} - (${pedido.cantidad}) - ${pedido.estado}</li>`;}
function renderPedidos(lista){
    const contenedor = document.querySelector('#lista-pedidos');
    let html = '';
    for (const pedido of lista){
        html = html + crearItemPedido(pedido);
    }
    contenedor.innerHTML = html;
}
async function guardarPedido(pedido) {
    pedidos.push(pedido);
}
form.addEventListener('submit',function (evento){
    evento.preventDefault();
    const plato = document.querySelector('#select-plato').value;
    const cantidad = Number(document.querySelector('#input-cantidad').value);

    if (plato === ''){
        cajaError.textContent = 'Elegi un plato';
        cajaError.classList.add('visible');
        return;
    }
    if (isNaN(cantidad) || cantidad < 1){
        cajaError.textContent = 'Ingresá una cantidad válida';
        cajaError.classList.add('visible');
        return;
    }
    cajaError.textContent = '';
    cajaError.classList.remove('visible');

    const pedido = {
        id: pedidos.length+1, plato, cantidad, estado: 'BORRADOR'
    };

    guardarPedido(pedido);
    renderPedidos(pedidos);
    form.reset();
});
