// Selecciona los elementos que ya existen en login.html
const form = document.querySelector('form');
const emailInput = document.querySelector('#email');
const passwordInput = document.querySelector('#password');

// Desactiva la validacion nativa del navegador
form.noValidate = true;

// Crea la caja para los mensajes de error
const cajaError = document.createElement('div');
cajaError.id = 'error-login';
form.prepend(cajaError);

// eschucha el evento submit del formulario
form.addEventListener('submit', function (evento) {
    evento.preventDefault(); // esto evita  que se recargue la pagina automaticamnte al enviar el formulario
    
    // obtiene los valores de los inputs del formulario
    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    // validamos los campos del formulario
    if (email === '' || password === '') {
         mostrarError('Por favor, completá todos los campos.'); 
         return;
    } // el return es para que no continue con el resto del codigo si hay un error

    //validamos el formato del email
    if (!email.includes('@') || !email.includes('.')) {
        mostrarError('Por favor, ingresá un email válido.'); 
        return;
    }
    // si los datos son validos, limpiamos el mensaje de error y enviamos los datos 
    limpiarError();
    window.location.href = 'Menu+Registrar-pedido.html'; // redirige a la pantalla de menu
});
    // funcion para mostrar el mensaje de error
    function mostrarError(mensaje) {
        cajaError.textContent = mensaje; // muestra el mensaje de error en la caja de error
        cajaError.classList.add('visible'); // hace visible la caja de error
        cajaError.style.display = 'block';
    }

    // funcion para limpiar el mensaje de error
    function limpiarError() {
        cajaError.textContent = ''; // limpia el mensaje de error
        cajaError.classList.remove('visible'); // oculta la caja de error
        cajaError.style.display = 'none';
    }