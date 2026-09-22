
const contenedorPlatos = document.querySelector('#lista-platos');
const selectPlato = document.querySelector('#select-plato');
const cajaError = document.querySelector('#error-pedido');
const form = document.querySelector('#form-pedido');

let platos = [];
let estado = 'cargando';

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
        estado = 'vacio';
        return;
    }
    let html = '';
    for (const plato of lista){
        html = html + crearTarjeta(plato);
    }
    contenedorPlatos.innerHTML = html;
    estado = 'listo';
}

function renderEstado(estado){
    const estados = {
        cargando: '<p style="color:#cccccc;text-align:center;">Cargando menú...</p>',
        vacio: '<p style="color:#cccccc;text-align:center;">No hay platos disponibles.</p>',
        error: '<p style="color:#b00020;text-align:center;">Error al cargar el menú.</p>',
        listo: ''
    };
    contenedorPlatos.innerHTML = estados[estado] || '';
}

async function cargarPlatos(){
    renderEstado('cargando');
    try {
        const res = await fetch('data/platos.json');
        if (!res.ok) throw new Error('HTTP error');
        platos = await res.json();
        renderPlatos(platos);
        for (const plato of platos){
            selectPlato.appendChild(crearOpcionSelect(plato));
        }
    } catch (err) {
        renderEstado('error');
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
