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
    usuario = await exigirSesion(false); // solo empleados (datos.js)
    if (usuario === null) {
        return; // no hay sesion: ya se fue al login
    }

    document.querySelector('#nombre').value = usuario.nombre;
    document.querySelector('#apellido').value = usuario.apellido;
    document.querySelector('#correo').value = usuario.correo;
    document.querySelector('#telefono').value = usuario.telefono;
    document.querySelector('#direccion').value = usuario.direccion;
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
    } else {
        // el correo no puede ser de otra cuenta
        const usuarios = await obtenerUsuarios();
        const otro = usuarios.find(u => u.id !== usuario.id && u.correo.toLowerCase() === correo.toLowerCase());
        if (otro !== undefined) {
            mostrarError('correo', 'Ese email ya lo usa otra cuenta.');
            hayError = true;
        }
    }

    if (hayError) {
        return;
    }

    usuario.nombre = nombre;
    usuario.apellido = apellido;
    usuario.correo = correo;
    usuario.telefono = document.querySelector('#telefono').value.trim();
    usuario.direccion = document.querySelector('#direccion').value.trim();
    await actualizarUsuario(usuario); // guarda en datos.js

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

    if (actual !== usuario.contrasena) {
        mostrarError('clave-actual', 'La contraseña actual no es correcta.');
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

    usuario.contrasena = nueva;
    await actualizarUsuario(usuario); // guarda en datos.js
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
        usuario.activo = false; // la cuenta queda desactivada
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
