// acceso a los datos: todas las pantallas llaman a estas funciones.
// Antes leian los json de data/ y guardaban en sessionStorage ("el backend de mentira").
// Ahora le piden los datos al backend Spring Boot, que los lee de MySQL.
// Las funciones mantienen el mismo nombre, asi las pantallas casi no cambiaron.

// el frontend corre en Live Server (5500) y Spring en el 8080: por eso la URL completa
const API = 'http://localhost:8080/api';

// hace el fetch al backend y devuelve el JSON.
// Si el backend responde con error (400, 404, 409...), lanza un Error con su mensaje,
// asi la pantalla puede mostrar el motivo (ej: "El plazo para pedir esa semana ya cerró").
async function pedirApi(ruta, metodo = 'GET', cuerpo = undefined) {
    const opciones = { method: metodo, headers: {} };
    if (cuerpo !== undefined) {
        opciones.headers['Content-Type'] = 'application/json';
        opciones.body = JSON.stringify(cuerpo); // convierte los objetos en texto
    }

    const respuesta = await fetch(`${API}${ruta}`, opciones);

    if (!respuesta.ok) {
        let mensaje = 'HTTP ' + respuesta.status;
        try {
            const error = await respuesta.json(); // el backend manda { mensaje: '...' }
            if (error.mensaje) {
                mensaje = error.mensaje;
            }
        } catch (e) {
            // la respuesta no traia JSON: queda el codigo HTTP
        }
        const falla = new Error(mensaje);
        falla.status = respuesta.status;
        throw falla;
    }

    if (respuesta.status === 204) {
        return null; // sin contenido
    }
    return respuesta.json();
}

// ---------- sesion (lo unico que queda en sessionStorage: el id de quien entro) ----------

function leer(clave) {
    const texto = sessionStorage.getItem(clave);
    if (texto === null) {
        return null;
    }
    return JSON.parse(texto);
}

function escribir(clave, valor) {
    sessionStorage.setItem(clave, JSON.stringify(valor));
}

// ---------- fechas (las usan todas las pantallas) ----------

// nombres de los dias en el orden de getDay() (0 = domingo)
const nombresDias = ['domingo', 'lunes', 'martes', 'miercoles', 'jueves', 'viernes', 'sabado'];

// nombres con tilde para mostrar
const nombresLindos = {
    lunes: 'Lunes', martes: 'Martes', miercoles: 'Miércoles',
    jueves: 'Jueves', viernes: 'Viernes', sabado: 'Sábado'
};

// textos de los estados del pedido (los del script: BORRADOR, CONFIRMADO, CANCELADO).
// Al empleado el BORRADOR se le muestra como "Pendiente" (todavia lo puede cambiar).
const textosEstado = {
    BORRADOR: 'Pendiente', CONFIRMADO: 'Confirmado', CANCELADO: 'Cancelado'
};

// fecha y hora actual
function ahora() {
    return new Date();
    //return new Date('2026-10-15T13:00:00'); // para probar: jueves despues de las 12 (plazo vencido)
}

// devuelve el nombre del dia de una fecha, ej: '2026-10-19' -> 'lunes'
function nombreDia(fecha) {
    const dia = new Date(fecha + 'T00:00:00'); // T00 para que no tome la fecha en UTC y de el dia anterior
    return nombresDias[dia.getDay()];
}

// pasa '2026-10-19' a '19/10'
function formatearFecha(fecha) {
    const partes = fecha.split('-'); // ['2026', '10', '19']
    return partes[2] + '/' + partes[1];
}

// semana (lunes a sabado) de una fecha, ej: '2026-10-20' -> '19/10 al 24/10'
function textoSemana(fecha) {
    const dia = new Date(fecha + 'T00:00:00');
    const lunes = new Date(dia);
    lunes.setDate(dia.getDate() - ((dia.getDay() + 6) % 7)); // vuelve al lunes de esa semana
    const sabado = new Date(lunes);
    sabado.setDate(lunes.getDate() + 5);
    return `${lunes.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit' })} al ${sabado.toLocaleDateString('es-AR', { day: '2-digit', month: '2-digit' })}`;
}

// pasa un Date a texto 'AAAA-MM-DD' (hora local)
function fechaTexto(fecha) {
    return fecha.toLocaleDateString('en-CA'); // el formato de Canada es año-mes-dia
}

// suma (o resta) dias a una fecha, ej: sumarDias('2026-10-19', 7) -> '2026-10-26'
function sumarDias(fecha, dias) {
    const dia = new Date(fecha + 'T00:00:00');
    dia.setDate(dia.getDate() + dias);
    return fechaTexto(dia);
}

// lunes de la semana de una fecha, ej: '2026-10-21' -> '2026-10-19'
function lunesDe(fecha) {
    const dia = new Date(fecha + 'T00:00:00');
    return sumarDias(fecha, -((dia.getDay() + 6) % 7));
}

// lunes de la semana para la que se pide: la que viene, o la siguiente
// si el plazo de la que viene ya cerro (jueves 12 hs)
function lunesSemanaQueViene() {
    const lunes = sumarDias(lunesDe(fechaTexto(ahora())), 7);
    if (ahora() >= calcularPlazo(lunes)) {
        return sumarDias(lunes, 7);
    }
    return lunes;
}

// plazo para pedir o modificar: el jueves anterior a esa semana, a las 12 hs
// (la misma regla la controla el backend en Plazos.java)
function calcularPlazo(fechaDeLaSemana) {
    const dia = new Date(fechaDeLaSemana + 'T00:00:00');
    const lunes = new Date(dia);
    lunes.setDate(dia.getDate() - ((dia.getDay() + 6) % 7)); // vuelve al lunes de esa semana
    const plazo = new Date(lunes);
    plazo.setDate(lunes.getDate() - 4); // 4 dias antes del lunes es el jueves anterior
    plazo.setHours(12, 0, 0, 0); // a las 12 hs
    return plazo;
}

// texto del plazo, ej: 'jueves 15/10 a las 12 hs'
function textoPlazo(plazo) {
    const dia = String(plazo.getDate()).padStart(2, '0');
    const mes = String(plazo.getMonth() + 1).padStart(2, '0');
    return `jueves ${dia}/${mes} a las 12 hs`;
}

// ---------- platos y menus (M3) ----------

// GET /api/platos -> tabla plato
async function obtenerPlatos() {
    return pedirApi('/platos');
}

// GET /api/menus -> tabla menu_dia
async function obtenerMenusDia() {
    return pedirApi('/menus');
}

// GET /api/menu-platos -> tabla menu_plato (que platos tiene cada menu y su stock)
async function obtenerMenuPlatos() {
    return pedirApi('/menu-platos');
}

// el admin guarda o publica los menus de una semana (menu_dia + menu_plato)
// semana = { lunes, publicar, idUsuarioCreador, dias: [{ fecha, idPlatos: [..] }] }
async function guardarSemanaMenus(semana) {
    return pedirApi('/menus/semana', 'PUT', semana);
}

// ---------- dias no laborables (feriados) ----------

// GET /api/dias-no-laborables -> las pantallas usan solo la fecha: ['2026-10-30', ...]
async function obtenerFeriados() {
    const dias = await pedirApi('/dias-no-laborables');
    return dias.map(d => d.fecha);
}

// PUT con la lista completa de fechas marcadas
async function guardarFeriados(lista) {
    const dias = lista.map(fecha => ({ fecha: fecha }));
    return pedirApi('/dias-no-laborables', 'PUT', dias);
}

// ---------- usuarios (M1) ----------

// GET /api/usuarios (el consolidado muestra el nombre de cada empleado)
async function obtenerUsuarios() {
    return pedirApi('/usuarios');
}

// POST /api/usuarios/login -> el usuario, o Error 'Email o contraseña incorrectos.'
async function login(correo, contrasena) {
    return pedirApi('/usuarios/login', 'POST', { correo: correo, contrasena: contrasena });
}

// POST /api/usuarios -> da de alta un empleado (el id lo pone la base)
async function crearUsuario(usuario) {
    return pedirApi('/usuarios', 'POST', usuario);
}

// PUT /api/usuarios/{id} -> datos personales o dar de baja
async function actualizarUsuario(usuario) {
    return pedirApi(`/usuarios/${usuario.id}`, 'PUT', usuario);
}

// PUT /api/usuarios/{id}/contrasena -> el backend verifica la actual
async function cambiarContrasena(idUsuario, actual, nueva) {
    return pedirApi(`/usuarios/${idUsuario}/contrasena`, 'PUT', { actual: actual, nueva: nueva });
}

// ---------- sesion ----------

// guarda quien inicio sesion (solo el id)
function iniciarSesion(usuario) {
    escribir('usuarioActual', usuario.id);
}

function cerrarSesion() {
    sessionStorage.removeItem('usuarioActual');
}

// devuelve el usuario que inicio sesion (lo pide al backend), o undefined si no hay nadie
async function obtenerUsuarioActual() {
    const id = leer('usuarioActual');
    if (id === null) {
        return undefined;
    }
    try {
        const usuario = await pedirApi(`/usuarios/${id}`);
        if (!usuario.activo) {
            return undefined;
        }
        return usuario;
    } catch (error) {
        if (error.status === 404) {
            return undefined; // el usuario ya no existe
        }
        throw error; // otro error (ej: el backend apagado): que lo muestre la pantalla
    }
}

// true si el usuario es la administradora (rol del script: 'ADMINISTRADOR' o 'EMPLEADO')
function esAdministrador(usuario) {
    return usuario.rol === 'ADMINISTRADOR';
}

// se llama al abrir cada pantalla: si no hay sesion manda al login,
// y si el rol no corresponde manda a su pantalla principal
async function exigirSesion(soloAdmin) {
    const usuario = await obtenerUsuarioActual();

    if (usuario === undefined) {
        location.href = 'login.html'; // no inicio sesion
        return null;
    }

    if (esAdministrador(usuario) !== soloAdmin) {
        if (esAdministrador(usuario)) {
            location.href = 'administrar-menus.html'; // es admin: va a su panel
        } else {
            location.href = 'menu.html'; // es empleado: va al menu
        }
        return null;
    }

    dibujarNavbar(usuario); // arma la navbar segun el rol (navbar.js)
    return usuario;
}

// ---------- asistencia (M2) ----------
// En la base hay una fila por empleado y por FECHA (tabla asistencia).
// La pantalla trabaja con nombres de dia de la semana que se pide: ['lunes', 'miercoles'].
// Estas dos funciones traducen entre las dos formas.

// GET /api/asistencias?idUsuario=&desde=&hasta= -> ['lunes', 'miercoles', ...]
async function obtenerAsistencia(idUsuario) {
    const lunes = lunesSemanaQueViene();
    const sabado = sumarDias(lunes, 5);
    const filas = await pedirApi(`/asistencias?idUsuario=${idUsuario}&desde=${lunes}&hasta=${sabado}`);
    return filas.filter(f => f.asiste).map(f => nombreDia(f.fecha));
}

// PUT /api/asistencias -> una fila por cada dia (lunes a sabado) con asiste true/false
async function guardarAsistencia(idUsuario, dias) {
    const lunes = lunesSemanaQueViene();
    const filas = [];
    for (let i = 0; i < 6; i++) {
        const fecha = sumarDias(lunes, i);
        filas.push({ idUsuario: idUsuario, fecha: fecha, asiste: dias.includes(nombreDia(fecha)) });
    }
    return pedirApi('/asistencias', 'PUT', filas);
}

// ---------- pedidos (M4) ----------

// GET /api/pedidos (todos) o /api/pedidos?idUsuario=3 (los de un empleado).
// Los BORRADORES con el plazo vencido ya vienen CONFIRMADOS (lo hace el backend).
async function obtenerPedidos(idUsuario) {
    if (idUsuario !== undefined) {
        return pedirApi(`/pedidos?idUsuario=${idUsuario}`);
    }
    return pedirApi('/pedidos');
}

// POST /api/pedidos -> devuelve el pedido guardado (con su id y en BORRADOR)
async function guardarPedido(pedido) {
    return pedirApi('/pedidos', 'POST', pedido);
}

// PUT /api/pedidos/{id} -> reemplaza los dias de un pedido que todavia es BORRADOR
async function actualizarPedido(pedido) {
    return pedirApi(`/pedidos/${pedido.id}`, 'PUT', pedido);
}

// PATCH /api/pedidos/{id}/estado -> 'CONFIRMADO' o 'CANCELADO'
async function cambiarEstadoPedido(idPedido, estado) {
    return pedirApi(`/pedidos/${idPedido}/estado`, 'PATCH', { estado: estado });
}

// pedido (no cancelado) del usuario para la semana de esos menus, o undefined si no hay
function buscarPedidoDeLaSemana(pedidos, menusDia, idUsuario) {
    return pedidos.find(pedido =>
        pedido.idUsuario === idUsuario &&
        pedido.estado !== 'CANCELADO' &&
        menusDia.find(menu => menu.fecha === pedido.pedidoDias[0].fechaEntrega) !== undefined
    );
}

// ---------- consolidado (M5) ----------

// GET /api/consolidado?fecha= -> [{ plato: 'Milanesa con puré', porciones: 2 }, ...]
async function obtenerConsolidado(fecha) {
    return pedirApi(`/consolidado?fecha=${fecha}`);
}
