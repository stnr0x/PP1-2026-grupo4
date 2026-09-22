
//console.log('probando 123');
const platos =[
{id: 1, nombre:'Milanesa con pure', descripcion:'clasica, con guarnicion',categoria:'clasico',precio:800, imagen: 'assets/img/milanesa.jpg'},
    {id: 2, nombre:'Pastel de papa', descripcion:'clasico sin aceitunas',categoria:'clasico',precio:850, imagen: 'assets/img/pasteldepapa.jpg'},
    {id: 3, nombre:'Tortilla de acelga', descripcion:'clasica',categoria:'vegetarianos',precio:900, imagen: 'assets/img/tortilladeacelga.jpg'},
    {id: 4, nombre:'Matambre de cerdo', descripcion:'con ensalda o papas fritas',categoria:'clasico',precio:1000, imagen: 'assets/img/matambre.jpg'},
    ];

console.log('cantidad platos',platos.length)
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

function renderPlatos(lista){
    const contener = document.querySelector('#lista-platos');
    let html = '';
    for (const plato of lista){
        html = html + crearTarjeta(plato);
    }
    contener.innerHTML = html;
}
renderPlatos(platos);

const selectPlato = document.querySelector('#select-plato');
for (const plato of platos){
    const option = document.createElement('option');
    option.value = plato.nombre;
    option.textContent = plato.nombre;
    selectPlato.appendChild(option);
}

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
const cajaError = document.querySelector('#error-pedido');
const form = document.querySelector('#form-pedido');
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
