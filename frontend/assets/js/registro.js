// selecciona los elementos que ya existen en registro.html
const form = document.querySelector('#form-registro');
const campos = {
    nombre: { input: document.querySelector('#nombre'), error: document.querySelector('#error-nombre') },
    apellido: { input: document.querySelector('#apellido'), error: document.querySelector('#error-apellido') },
    email: { input: document.querySelector('#email'), error: document.querySelector('#error-email') },
    password: { input: document.querySelector('#password'), error: document.querySelector('#error-password') },
    confirm: { input: document.querySelector('#confirm-password'), error: document.querySelector('#error-confirm') }
};
const cajaExito = document.querySelector('#exito-registro');

// desactiva la validacion nativa del navegador (los errores los muestra el JS)
form.noValidate = true;

// escucha el envio del formulario (CU001)
form.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina

    const nombre = campos.nombre.input.value.trim();
    const apellido = campos.apellido.input.value.trim();
    const email = campos.email.input.value.trim();
    const password = campos.password.input.value.trim();
    const confirm = campos.confirm.input.value.trim();

    // limpia los errores anteriores antes de validar de nuevo
    for (const clave in campos) {
        limpiarErrorCampo(campos[clave]);
    }

    let hayError = false; // es let porque cambia si algun campo falla

    if (nombre === '') {
        mostrarErrorCampo(campos.nombre, 'Ingresá tu nombre.');
        hayError = true;
    }

    if (apellido === '') {
        mostrarErrorCampo(campos.apellido, 'Ingresá tu apellido.');
        hayError = true;
    }

    // valida el email (mismo criterio que el login)
    if (email === '') {
        mostrarErrorCampo(campos.email, 'Ingresá tu email.');
        hayError = true;
    } else {
        const partes = email.split('@'); // separa lo que va antes y despues del @
        if (partes.length !== 2 || partes[0] === '' || !partes[1].includes('.')) {
            mostrarErrorCampo(campos.email, 'Ingresá un email válido.');
            hayError = true;
        }
    }

    if (password.length < 6) {
        mostrarErrorCampo(campos.password, 'La contraseña debe tener al menos 6 caracteres.');
        hayError = true;
    }

    if (confirm !== password) {
        mostrarErrorCampo(campos.confirm, 'Las contraseñas no coinciden.');
        hayError = true;
    }

    if (hayError) {
        return; // corta aca si algun campo fallo (CU001 flujo 4A)
    }

    // el correo no puede estar registrado (CU001 flujo 4B)
    const usuarios = await obtenerUsuarios(); // esta en datos.js
    const existe = usuarios.find(u => u.correo.toLowerCase() === email.toLowerCase());
    if (existe !== undefined) {
        mostrarErrorCampo(campos.email, 'Ya hay una cuenta con ese email.');
        return;
    }

    // arma el Usuario con los nombres del DER (siempre como empleado)
    const usuario = await crearUsuario({
        nombre: nombre,
        apellido: apellido,
        correo: email,
        contrasena: password,
        telefono: '',
        direccion: '',
        esUsuarioRestaurante: false,
        activo: true
    });

    iniciarSesion(usuario); // entra directamente con la cuenta nueva
    cajaExito.textContent = `¡Cuenta creada, ${usuario.nombre}! Te llevamos al menú...`;
    cajaExito.classList.add('visible');
    setTimeout(function () {
        window.location.href = 'menu.html';
    }, 1500); // espera 1,5 segundos para que se lea el mensaje
});

// saca el error de un campo en cuanto el usuario escribe
form.addEventListener('input', function (evento) {
    for (const clave in campos) {
        if (campos[clave].input === evento.target) {
            limpiarErrorCampo(campos[clave]);
        }
    }
});

// funcion para marcar un campo con error (mismo estilo que el login)
function mostrarErrorCampo(campo, mensaje) {
    campo.input.classList.add('input-error'); // pone el campo en rojo
    campo.error.textContent = mensaje; // escribe el error debajo del campo
    campo.error.classList.add('visible');
}

// funcion para sacar el error de un campo
function limpiarErrorCampo(campo) {
    campo.input.classList.remove('input-error');
    campo.error.textContent = '';
    campo.error.classList.remove('visible');
}
