const inputFecha = document.querySelector('#fecha');
const btnGenerar = document.querySelector('#btn-generar');
const cajaError = document.querySelector('#error-consolidado');
const mensajeExito = document.querySelector('#mensaje-exito');

btnGenerar.addEventListener('click', (evento) => {
    evento.preventDefault();
    const semanaVal = inputFecha.value.trim();

    if (semanaVal === '') {
        mostrarError('Por favor seleccione una semana del consolidado valida.');
        ocultarExito();
        return;
    }

    limpiarError();
    mostrarExito(`Consolidado generado con éxito para la semana ${semanaVal}.`);
});

function mostrarError(mensaje) {
    cajaError.textContent = mensaje;
    cajaError.classList.add('visible');
}
function limpiarError() {
    cajaError.textContent = '';
    cajaError.classList.remove('visible');
}
function mostrarExito(mensaje) {
    mensajeExito.textContent = mensaje;
    mensajeExito.classList.add('visible');
}
function ocultarExito() {
    if (mensajeExito){
        mensajeExito.textContent = '';
        mensajeExito.classList.remove('visible');
    }
}