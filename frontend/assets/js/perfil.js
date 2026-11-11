let usuario = null; // usuario que inicio sesion (se llena en cargarDatos)

// selecciona los elementos que ya existen en perfil.html
const formDatos = document.querySelector('#form-datos');
const formClave = document.querySelector('#form-clave');
const accionesBaja = document.querySelector('#acciones-baja');

// desactiva la validacion nativa del navegador (los errores los muestra el JS)
formDatos.noValidate = true;
formClave.noValidate = true;

// funcion que trae el usuario y completa el formulario con sus datos (CU003)
async function cargarDatos() {
    try {
        usuario = await exigirSesion(false); // solo empleados (datos.js)
    } catch (error) {
        console.error('No se pudo cargar el perfil:', error);
        mostrarExito('#exito-datos', 'No pudimos cargar tus datos. Probá recargar la página.');
        return;
    }
    if (usuario === null) {
        return; // no hay sesion: ya se fue al login
    }

    document.querySelector('#nombre').value = usuario.nombre;
    document.querySelector('#apellido').value = usuario.apellido;
    document.querySelector('#correo').value = usuario.correo;
    document.querySelector('#telefono').value = usuario.telefono ?? ''; // puede venir null de la base
}

// ---------- datos personales ----------
formDatos.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina
    limpiarErrores(formDatos);

    const nombre = document.querySelector('#nombre').value.trim();
    const apellido = document.querySelector('#apellido').value.trim();
    const correo = document.querySelector('#correo').value.trim();
    let hayError = false;

    if (nombre === '') {
        mostrarError('nombre', 'Ingresá tu nombre.');
        hayError = true;
    }
    if (apellido === '') {
        mostrarError('apellido', 'Ingresá tu apellido.');
        hayError = true;
    }

    const partes = correo.split('@'); // mismo criterio que el login
    if (partes.length !== 2 || partes[0] === '' || !partes[1].includes('.')) {
        mostrarError('correo', 'Ingresá un email válido.');
        hayError = true;
    }

    if (hayError) {
        return;
    }

    const cambios = {
        id: usuario.id,
        nombre: nombre,
        apellido: apellido,
        correo: correo,
        telefono: document.querySelector('#telefono').value.trim()
    };
    try {
        usuario = await actualizarUsuario(cambios); // PUT /api/usuarios/{id} (datos.js)
    } catch (error) {
        // 409: el correo ya lo usa otra cuenta (lo controla el backend)
        mostrarError('correo', error.message);
        return;
    }

    document.querySelector('.user-name').textContent = `${usuario.nombre} ${usuario.apellido}`; // actualiza la navbar
    mostrarExito('#exito-datos', 'Datos actualizados.');
});

// ---------- cambiar contraseña ----------
formClave.addEventListener('submit', async function (evento) {
    evento.preventDefault(); // evita que se recargue la pagina
    limpiarErrores(formClave);

    const actual = document.querySelector('#clave-actual').value;
    const nueva = document.querySelector('#clave-nueva').value;
    const confirmar = document.querySelector('#clave-confirmar').value;
    let hayError = false;

    if (actual === '') {
        mostrarError('clave-actual', 'Ingresá tu contraseña actual.');
        hayError = true;
    }
    if (nueva.length < 6) {
        mostrarError('clave-nueva', 'La nueva contraseña debe tener al menos 6 caracteres.');
        hayError = true;
    }
    if (confirmar !== nueva) {
        mostrarError('clave-confirmar', 'Las contraseñas no coinciden.');
        hayError = true;
    }

    if (hayError) {
        return;
    }

    // la contrasena no viaja al frontend: el backend compara la actual
    try {
        await cambiarContrasena(usuario.id, actual, nueva); // PUT /api/usuarios/{id}/contrasena
    } catch (error) {
        mostrarError('clave-actual', error.message); // 'La contraseña actual no es correcta.'
        return;
    }
    formClave.reset(); // vacia los campos
    mostrarExito('#exito-clave', 'Contraseña actualizada.');
});

// ---------- dar de baja la cuenta (CU004) ----------
accionesBaja.addEventListener('click', async function (evento) {
    const boton = evento.target.closest('button');
    if (!boton) {
        return;
    }

    if (boton.id === 'btn-baja') {
        // primer paso: pide confirmacion (CU004 paso 3)
        accionesBaja.innerHTML = `
            <p class="pregunta-baja">¿Seguro que querés dar de baja tu cuenta? No vas a poder volver a entrar.</p>
            <button type="button" id="btn-baja-si" class="btn-baja lleno">Sí, dar de baja</button>
            <button type="button" id="btn-baja-no" class="btn-baja">No, volver</button>`;
    } else if (boton.id === 'btn-baja-no') {
        // cancela la accion (CU004 flujo 3A)
        accionesBaja.innerHTML = '<button type="button" id="btn-baja" class="btn-baja">Dar de baja mi cuenta</button>';
    } else if (boton.id === 'btn-baja-si') {
        usuario.activo = false; // la cuenta queda desactivada (activo = FALSE en la tabla usuario)
        await actualizarUsuario(usuario);
        cerrarSesion();
        window.location.href = 'login.html';
    }
});

// ---------- mensajes ----------

// marca un campo en rojo con su mensaje (mismo estilo que el login)
function mostrarError(id, mensaje) {
    document.querySelector('#' + id).classList.add('input-error');
    const caja = document.querySelector('#error-' + id);
    caja.textContent = mensaje;
    caja.classList.add('visible');
}

// saca los errores y el mensaje de exito de un formulario
function limpiarErrores(formulario) {
    for (const input of formulario.querySelectorAll('.input-error')) {
        input.classList.remove('input-error');
    }
    for (const caja of formulario.querySelectorAll('.error-campo, .exito-perfil')) {
        caja.textContent = '';
        caja.classList.remove('visible');
    }
}

function mostrarExito(selector, mensaje) {
    const caja = document.querySelector(selector);
    caja.textContent = mensaje;
    caja.classList.add('visible');
}

cargarDatos(); // arranca la carga al abrir la pagina
