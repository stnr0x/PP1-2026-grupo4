
function crearTarjeta(plato) {
    return `
    <article class="card-dia">
      <img src="${plato.imagen}" alt="${plato.nombre}" style="width:100%;border-radius:8px;margin-bottom:8px;">
      <h3>${plato.nombre}</h3>
      <p class="opcion"><em>${plato.descripcion}</em></p>
      <p class="opcion">${plato.categoria}</p>
      <p class="plazo">$${plato.precio}</p>
    </article>
  `;
}

function renderPlatos(lista) {
    const contenedor = document.querySelector('#lista-platos');
    let html = '';
    for (const plato of lista) {
        html = html + crearTarjeta(plato);
    }
    contenedor.innerHTML = html;
}


function mostrarMensaje(texto, tipo) {
    const contenedor = document.querySelector('#lista-platos');
    contenedor.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`;
}


async function cargarPlatos() {
    mostrarMensaje('Cargando el menú...', 'cargando');

    try {
        const respuesta = await fetch('data/platos.json');
        const platos = await respuesta.json();

        if (platos.length === 0) {
            mostrarMensaje('Todavía no hay platos cargados para esta semana.', 'vacio');
            return;
        }

        renderPlatos(platos);
    } catch (error) {
        console.error('No se pudieron cargar los platos:', error);
        mostrarMensaje('No pudimos cargar el menú. Probá recargar la página.', 'error');
    }
}

cargarPlatos();


const pedidos = [];

const form = document.querySelector('#form-pedido');
const cajaError = document.querySelector('#error-pedido');

function crearItemPedido(pedido) {
    return `<li class="card-pedido">
        <h4>${pedido.plato}</h4>
        <p>${pedido.empleado} · Cantidad: ${pedido.cantidad} · Estado: ${pedido.estado}</p>
    </li>`;
}

function renderPedidos(lista) {
    const contenedor = document.querySelector('#lista-pedidos');
    let html = '';
    for (const pedido of lista) {
        html = html + crearItemPedido(pedido);
    }
    contenedor.innerHTML = html;
}


async function guardarPedido(pedido) {
    pedidos.push(pedido);
}

form.addEventListener('submit', async function (evento) {
    evento.preventDefault();

    const plato = document.querySelector('#select-plato').value;
    const cantidad = Number(document.querySelector('#input-cantidad').value);

    if (plato === '' || cantidad <= 0) {
        cajaError.textContent = 'Elegí un plato y una cantidad válida.';
        cajaError.classList.add('visible'); //este es para que el error se vea en rojo y en negrita
        return;
    }

    cajaError.textContent = '';
    cajaError.classList.remove('visible');//esto saca la clase, asi el error no queda pegado

    const pedido = {
        id: pedidos.length + 1,
        empleado: 'Juan',
        plato: plato,
        cantidad: cantidad,
        estado: 'BORRADOR'
    };

    await guardarPedido(pedido);
    renderPedidos(pedidos);
    form.reset();
});
