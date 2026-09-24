//seleccionamos los elementos que ya existe en el html
const inputFecha = document.querySelector('#fecha');
const btnGenerar = document.querySelector('#btn-generar');
const cajaError = document.querySelector('#error-consolidado');
const mensajeExito = document.querySelector('#mensaje-exito');

//array de pedidos confirmados de la semana (simulación de datos)
const pedidosConfirmados = [{id: 1, empleado: 'Carla Gómez', plato: 'Milanesa con puré (Lun, Vie)', comentarios: '--' }, 
      { id: 2, empleado: 'Juan Rodríguez', plato: 'Tarta de verdura (Lun, Mié)', comentarios: 'Extra queso' },
      { id: 3, empleado: 'María López', plato: 'Milanesa con puré (Mié, Vie)', comentarios: '--' },
      { id: 4, empleado: 'Pedro Sánchez', plato: 'Ensalada completa (Lun)<br>Milanesa con puré (Vie)', comentarios: 'Sin sal' }];

      //funcion que arma el html de una fila de la tabla a partir de un pedido
      //solo devuelve el texto, todavia no lo muestra en pantalla
      function crearFila(pedido) {
        return `<tr>
                  <td>${pedido.empleado}</td>
                  <td>${pedido.plato}</td>
                  <td>${pedido.comentarios}</td>
                </tr>`;
      }

      //funcion que dibuja todas las filas de la lista en la tabla
       function renderConsolidado(lista) {
        const contenedor = document.querySelector('#lista-consolidado');
        let html = '';
        for (const pedido of lista) {
          html = html + crearFila(pedido);
        }
        contenedor.innerHTML = html; //recien aca se dibuja en pantalla. mete todas las dila en el tbody
        }

      //escucha el click del boton generar
btnGenerar.addEventListener('click', (evento) => {
    evento.preventDefault(); //esto de aca evita que el boton haga su accion por defecto
    const semanaVal = inputFecha.value.trim();// aca lee la semana que el usuario selecciono

    //validacion de que el usuario haya seleccionado una semana
    if (semanaVal === '') {
        mostrarError('Por favor seleccione una semana del consolidado valida.');
        ocultarExito();
        return; //aca corta, asi no se genera una tabla si hay un error
    }
// si la semana es valida, limpiamos el error, mostramos el mensaje de exito y dibujamos la tabla
    limpiarError();
    mostrarExito(`Consolidado generado con éxito para la semana ${semanaVal}.`);
    renderConsolidado(pedidosConfirmados);
});

//funcion para mostrar el mensaje de error
function mostrarError(mensaje) {
    cajaError.textContent = mensaje;//pone el texto de error
    cajaError.classList.add('visible');//agrega la clase para que se vea con el estilo del css
}
//funcion para limiar el error
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