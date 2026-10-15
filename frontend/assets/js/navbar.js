// navbar compartida: cada pantalla tiene un <header id="navbar"></header> vacio
// y esta funcion lo llena segun el usuario (empleado o admin) y la pagina actual.
// la llama exigirSesion() de datos.js cuando ya se sabe quien inicio sesion.

// links de cada rol
const linksEmpleado = [
    { texto: 'Inicio', href: 'index.html' },
    { texto: 'Menú', href: 'menu.html' },
    { texto: 'Mis Pedidos', href: 'mis-pedidos.html' },
    { texto: 'Mi Perfil', href: 'perfil.html' }
];

const linksAdmin = [
    { texto: 'Inicio', href: 'index.html' },
    { texto: 'Menús', href: 'administrar-menus.html' },
    { texto: 'Consolidado', href: 'consolidado.html' },
    { texto: 'Feriados', href: 'feriados.html' }
];

function dibujarNavbar(usuario) {
    const header = document.querySelector('#navbar');
    if (!header) {
        return; // la pagina no tiene navbar (login, registro)
    }

    const esAdmin = usuario.esUsuarioRestaurante;
    const paginaActual = location.pathname.split('/').pop(); // ej: 'menu.html'

    // links del rol, marcando el de la pagina actual
    let links = '';
    for (const link of esAdmin ? linksAdmin : linksEmpleado) {
        let clase = '';
        if (link.href === paginaActual) {
            clase = 'active';
        }
        links = links + `<li><a href="${link.href}" class="${clase}">${link.texto}</a></li>`;
    }

    // el admin tiene la etiqueta "Admin" y se muestra solo su nombre
    let etiqueta = '';
    let nombre = `${usuario.nombre} ${usuario.apellido}`;
    if (esAdmin) {
        etiqueta = '<span class="navbar-admin"><span class="admin-label">Admin</span></span>';
        nombre = usuario.nombre;
    }

    header.innerHTML = `
        <div class="navbar-inner">
            <div class="navbar-brand">
                <span class="logo">Byte</span>
                <span class="brand-name">Sistema de Pedidos</span>
                ${etiqueta}
            </div>

            <nav>
                <ul class="navbar-nav">${links}</ul>
            </nav>

            <div class="navbar-user">
                <span class="user-name">${nombre}</span>
                <a href="login.html" class="btn-logout">Cerrar sesión</a>
            </div>
        </div>
    `;

    header.querySelector('.btn-logout').addEventListener('click', cerrarSesion); // borra la sesion antes de ir al login
}
