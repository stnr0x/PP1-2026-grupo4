// Selecciona los elementos que ya existen en login.html
const form = document.querySelector('form');
const emailInput = document.querySelector('#email');
const passwordInput = document.querySelector('#password');
const errorEmail = document.querySelector('#error-email'); // p del error del email
const errorPassword = document.querySelector('#error-password'); // p del error de la contraseña

// Desactiva la validacion nativa del navegador
form.noValidate = true;

// eschucha el evento submit del formulario
form.addEventListener('submit', function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina

    const email = emailInput.value.trim();
    const password = passwordInput.value.trim();

    // limpia los errores anteriores antes de validar de nuevo
    limpiarErrorCampo(emailInput, errorEmail);
    limpiarErrorCampo(passwordInput, errorPassword);

    let hayError = false; // es let porque cambia si algun campo falla

    // valida el email
    if (email === '') {
        mostrarErrorCampo(emailInput, errorEmail, 'Ingresá tu email.');
        hayError = true;
    } else {
        const partes = email.split('@'); // separa lo que va antes y despues del @
        if (partes.length !== 2 || partes[0] === '' || !partes[1].includes('.')) {
            mostrarErrorCampo(emailInput, errorEmail, 'Ingresá un email válido.');
            hayError = true;
        }
    }

    // valida la contraseña
    if (password === '') {
        mostrarErrorCampo(passwordInput, errorPassword, 'Ingresá tu contraseña.');
        hayError = true;
    } else if (password.length < 6) {
        mostrarErrorCampo(passwordInput, errorPassword, 'La contraseña debe tener al menos 6 caracteres.');
        hayError = true;
    }

    if (hayError) {
        return; // corta aca si algun campo fallo
    }

    window.location.href = 'menu.html'; // redirige a la pantalla de menu
});

// saca el error del campo en cuanto el usuario escribe
emailInput.addEventListener('input', function () {
    limpiarErrorCampo(emailInput, errorEmail);
});
passwordInput.addEventListener('input', function () {
    limpiarErrorCampo(passwordInput, errorPassword);
});

// funcion para marcar un campo con error
function mostrarErrorCampo(input, caja, mensaje) {
    input.classList.add('input-error'); // pone el campo en rojo
    caja.textContent = mensaje; // escribe el error debajo del campo
    caja.classList.add('visible'); // muestra el error
}

// funcion para sacar el error de un campo
function limpiarErrorCampo(input, caja) {
    input.classList.remove('input-error');
    caja.textContent = '';
    caja.classList.remove('visible');
}