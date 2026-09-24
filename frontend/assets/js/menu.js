
// funcion que arma el html de una tarjeta a partir de un plato
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

// funcion que dibuja todas las tarjetas en pantalla
function renderPlatos(lista) {
    const contenedor = document.querySelector('#lista-platos'); // busca el div vacio del html
    let html = ''; // es let porque cambia en cada vuelta
    for (const plato of lista) {
        html = html + crearTarjeta(plato); // suma la tarjeta de cada plato
    }
    contenedor.innerHTML = html; // aca se muestra en pantalla
}


// funcion que muestra un mensaje en lugar de la lista (cargando, vacio o error)
function mostrarMensaje(texto, tipo) {
    const contenedor = document.querySelector('#lista-platos');
    contenedor.innerHTML = `<p class="mensaje ${tipo}">${texto}</p>`; // el tipo es la clase css del estado
}


// funcion que trae los platos del json y los dibuja
async function cargarPlatos() {
    mostrarMensaje('Cargando el menú...', 'cargando'); // estado cargando, antes del await

    try {
        const respuesta = await fetch('data/platos.json'); // espera a que llegue el archivo
        //const respuesta = await fetch('data/platos-vacio.json'); // para probar el estado vacio
        //const respuesta = await fetch('data/noexiste.json'); // para probar el estado error
        const platos = await respuesta.json(); // convierte la respuesta en un array de objetos

        if (platos.length === 0) {
            mostrarMensaje('Todavía no hay platos cargados para esta semana.', 'vacio'); // estado vacio
            return; // corta aca para no dibujar una lista vacia
        }

        renderPlatos(platos);
    } catch (error) {
        console.error('No se pudieron cargar los platos:', error); // mensaje de error en consola
        mostrarMensaje('No pudimos cargar el menú. Probá recargar la página.', 'error'); // estado error, para el usuario
    }
}

cargarPlatos(); // arranca la carga al abrir la pagina


const pedidos = []; // lista de pedidos en memoria, arranca vacia

// selecciona los elementos que ya existen en el html
const form = document.querySelector('#form-pedido');
const cajaError = document.querySelector('#error-pedido');

// funcion que arma el html de un pedido
function crearItemPedido(pedido) {
    return `<li class="card-pedido">
        <h4>${pedido.plato}</h4>
        <p>${pedido.empleado} · Cantidad: ${pedido.cantidad} · Estado: ${pedido.estado}</p>
    </li>`;
}

// funcion que dibuja todos los pedidos en la lista
function renderPedidos(lista) {
    const contenedor = document.querySelector('#lista-pedidos'); // busca el ul vacio del html
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

// escucha el envio del formulario
form.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina

    const plato = document.querySelector('#select-plato').value; // lee el plato elegido
    const cantidad = Number(document.querySelector('#input-cantidad').value); // Number porque value devuelve texto

    if (plato === '' || cantidad <= 0) {
        cajaError.textContent = 'Elegí un plato y una cantidad válida.';
        cajaError.classList.add('visible'); //este es para que el error se vea en rojo y en negrita
        return; // corta aca si hay error
    }

    cajaError.textContent = '';
    cajaError.classList.remove('visible');//esto saca la clase, asi el error no queda pegado

    // arma el objeto pedido
    const pedido = {
        id: pedidos.length + 1,
        empleado: 'Juan',
        plato: plato,
        cantidad: cantidad,
        estado: 'BORRADOR' // el pedido arranca como borrador
    };

    await guardarPedido(pedido); // guarda el pedido
    renderPedidos(pedidos); // vuelve a dibujar la lista sin recargar
    form.reset(); // limpia el formulario
});
