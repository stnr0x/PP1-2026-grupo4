// esto es para el login de la pagina, elige el formulario y el contener del mensaje de error
const form = document.querySelector('#form-login'); 
const cajaError = document.querySelector('#error-login');

// eschucha el evento submit del formulario
form.addEventListener('submit', async (e) => {
    e.preventDefault(); // esto evita  que se recargue la pagina automaticamnte al enviar el formulario
    
    // obtiene los valores de los inputs del formulario
    const email = form.email.value;
    const password = form.password.value;  

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
    window.location.href = 'menu.html'; // redirige a la pantalla de menu

    // funcion para mostrar el mensaje de error
    function mostrarError(mensaje) {
        cajaError.textContent = mensaje; // muestra el mensaje de error en la caja de error
        cajaError.classList.add('visible'); // hace visible la caja de error
    }

    // funcion para limpiar el mensaje de error
    function limpiarError() {
        cajaError.textContent = ''; // limpia el mensaje de error
        cajaError.classList.remove('visible'); // oculta la caja de error
    }