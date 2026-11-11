package ar.edu.pp1.pedidos.servicio;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.pp1.pedidos.dto.ConsolidadoItem;
import ar.edu.pp1.pedidos.dto.PedidoDiaRequest;
import ar.edu.pp1.pedidos.dto.PedidoRequest;
import ar.edu.pp1.pedidos.dto.PedidoResponse;
import ar.edu.pp1.pedidos.entidad.MenuDia;
import ar.edu.pp1.pedidos.entidad.MenuPlato;
import ar.edu.pp1.pedidos.entidad.Pedido;
import ar.edu.pp1.pedidos.entidad.PedidoDia;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.excepcion.NoEncontradoException;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.DiaNoLaborableRepository;
import ar.edu.pp1.pedidos.repositorio.MenuDiaRepository;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoRepository;
import ar.edu.pp1.pedidos.repositorio.UsuarioRepository;

// M4 Mi pedido y M5 Consolidado. Aca viven las reglas de negocio del pedido:
// la base guarda lo que le mandan; el que decide si se puede pedir es este servicio.
@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoDiaRepository pedidoDiaRepository;
    private final MenuDiaRepository menuDiaRepository;
    private final MenuPlatoRepository menuPlatoRepository;
    private final DiaNoLaborableRepository diaNoLaborableRepository;
    private final UsuarioRepository usuarioRepository;
    private final Clock reloj;

    public PedidoService(PedidoRepository pedidoRepository, PedidoDiaRepository pedidoDiaRepository,
            MenuDiaRepository menuDiaRepository, MenuPlatoRepository menuPlatoRepository,
            DiaNoLaborableRepository diaNoLaborableRepository, UsuarioRepository usuarioRepository,
            Clock reloj) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoDiaRepository = pedidoDiaRepository;
        this.menuDiaRepository = menuDiaRepository;
        this.menuPlatoRepository = menuPlatoRepository;
        this.diaNoLaborableRepository = diaNoLaborableRepository;
        this.usuarioRepository = usuarioRepository;
        this.reloj = reloj;
    }

    // ---------- consultas ----------

    // Todos los pedidos, o los de un usuario (cruce 3). Antes de devolverlos,
    // los BORRADORES con el plazo vencido pasan a CONFIRMADO.
    @Transactional
    public List<PedidoResponse> listar(Long idUsuario) {
        confirmarVencidos();
        List<Pedido> pedidos = idUsuario == null
                ? pedidoRepository.findAll()
                : pedidoRepository.findByIdUsuarioOrderByIdDesc(idUsuario);
        List<PedidoResponse> respuesta = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            respuesta.add(armarRespuesta(pedido));
        }
        return respuesta;
    }

    @Transactional
    public PedidoResponse buscar(Long id) {
        confirmarVencidos();
        return armarRespuesta(buscarPedido(id));
    }

    // Cruce 2: el consolidado de una fecha (cuantas porciones de cada plato).
    @Transactional
    public List<ConsolidadoItem> consolidado(LocalDate fecha) {
        confirmarVencidos(); // asi entran los borradores que ya cerraron
        List<ConsolidadoItem> items = new ArrayList<>();
        for (Object[] fila : pedidoDiaRepository.consolidadoDeFecha(fecha)) {
            items.add(new ConsolidadoItem((String) fila[0], ((Number) fila[1]).longValue()));
        }
        return items;
    }

    // ---------- escrituras ----------

    // CU007: registrar el pedido de la semana. Nace en BORRADOR.
    @Transactional
    public PedidoResponse crear(PedidoRequest datos) {
        Usuario usuario = usuarioRepository.findById(datos.idUsuario())
                .orElseThrow(() -> new ReglaNegocioException("No existe el usuario " + datos.idUsuario()));
        if (!Usuario.EMPLEADO.equals(usuario.getRol()) || !usuario.getActivo()) {
            throw new ReglaNegocioException("Solo un empleado activo puede hacer pedidos.");
        }

        LocalDate lunes = validarDias(datos.pedidoDias());

        if (tienePedidoActivoEnSemana(datos.idUsuario(), lunes, null)) {
            throw new ReglaNegocioException("Ya tenés un pedido para esa semana. Modificalo desde Mis pedidos.");
        }

        Pedido pedido = new Pedido();
        pedido.setIdUsuario(datos.idUsuario());
        pedido.setFechaPedido(LocalDate.now(reloj));
        pedido.setEstado(Pedido.BORRADOR);
        pedido = pedidoRepository.save(pedido);

        guardarLineas(pedido.getId(), datos.pedidoDias());
        return armarRespuesta(pedido);
    }

    // CU009: modificar los platos de un pedido en BORRADOR, antes del plazo.
    @Transactional
    public PedidoResponse modificar(Long id, PedidoRequest datos) {
        Pedido pedido = buscarPedido(id);
        if (!Pedido.BORRADOR.equals(pedido.getEstado())) {
            throw new ReglaNegocioException("Solo se puede modificar un pedido pendiente.");
        }
        if (!pedido.getIdUsuario().equals(datos.idUsuario())) {
            throw new ReglaNegocioException("El pedido no es de ese usuario.");
        }

        // se devuelve el stock de las lineas viejas y se borran
        liberarStock(pedido.getId());
        pedidoDiaRepository.deleteByIdPedido(pedido.getId());
        pedidoDiaRepository.flush(); // borra YA, antes de insertar las nuevas (UNIQUE id_pedido + id_menu_dia)

        LocalDate lunes = validarDias(datos.pedidoDias());
        if (tienePedidoActivoEnSemana(pedido.getIdUsuario(), lunes, pedido.getId())) {
            throw new ReglaNegocioException("Ya tenés otro pedido para esa semana.");
        }

        guardarLineas(pedido.getId(), datos.pedidoDias());
        return armarRespuesta(pedido);
    }

    // CU008 confirmar / CU010 cancelar. Solo desde BORRADOR y antes del plazo.
    @Transactional
    public PedidoResponse cambiarEstado(Long id, String nuevoEstado) {
        Pedido pedido = buscarPedido(id);

        if (!Pedido.CONFIRMADO.equals(nuevoEstado) && !Pedido.CANCELADO.equals(nuevoEstado)) {
            throw new ReglaNegocioException("Estado inválido: " + nuevoEstado);
        }
        if (!Pedido.BORRADOR.equals(pedido.getEstado())) {
            throw new ReglaNegocioException("El pedido ya está " + pedido.getEstado().toLowerCase() + ".");
        }
        List<PedidoDia> lineas = pedidoDiaRepository.findByIdPedidoOrderByFechaEntrega(id);
        if (!lineas.isEmpty() && !LocalDateTime.now(reloj).isBefore(Plazos.plazoDe(lineas.get(0).getFechaEntrega()))) {
            throw new ReglaNegocioException("El plazo para cambiar este pedido ya cerró.");
        }

        if (Pedido.CANCELADO.equals(nuevoEstado)) {
            liberarStock(id); // las porciones vuelven a estar disponibles
        }
        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
        return armarRespuesta(pedido);
    }

    // ---------- reglas ----------

    // Revisa cada dia pedido (cruce 4 "¿se puede pedir ese dia?") y devuelve el lunes de la semana.
    LocalDate validarDias(List<PedidoDiaRequest> dias) {
        if (dias == null || dias.isEmpty()) {
            throw new ReglaNegocioException("Elegí un plato para al menos un día.");
        }

        LocalDate lunes = Plazos.lunesDe(dias.get(0).fechaEntrega());
        Set<Long> menusUsados = new HashSet<>();

        for (PedidoDiaRequest dia : dias) {
            LocalDate fecha = dia.fechaEntrega();

            if (diaNoLaborableRepository.existsByFecha(fecha)) {
                throw new ReglaNegocioException("El " + fecha + " es un día no laborable: no se puede pedir.");
            }
            MenuDia menu = menuDiaRepository.findById(dia.idMenuDia())
                    .orElseThrow(() -> new ReglaNegocioException("No existe el menú " + dia.idMenuDia()));
            if (!menu.getPublicado()) {
                throw new ReglaNegocioException("El menú del " + fecha + " todavía no está publicado.");
            }
            if (!menu.getFecha().equals(fecha)) {
                throw new ReglaNegocioException("La fecha de entrega no coincide con la del menú.");
            }
            if (!Plazos.lunesDe(fecha).equals(lunes)) {
                throw new ReglaNegocioException("Todos los días del pedido tienen que ser de la misma semana.");
            }
            if (!menusUsados.add(menu.getId())) {
                throw new ReglaNegocioException("Elegí un solo plato por día.");
            }
            MenuPlato opcion = menuPlatoRepository.findByIdMenuDiaAndIdPlato(menu.getId(), dia.idPlato())
                    .orElseThrow(() -> new ReglaNegocioException("Ese plato no está en el menú del " + fecha + "."));
            if (opcion.getStockDisponible() <= 0) {
                throw new ReglaNegocioException("No queda stock de ese plato para el " + fecha + ".");
            }
        }

        if (!LocalDateTime.now(reloj).isBefore(Plazos.plazoDe(lunes))) {
            throw new ReglaNegocioException("El plazo para pedir esa semana ya cerró (jueves anterior a las 12 hs).");
        }
        return lunes;
    }

    // ¿el usuario ya tiene un pedido no cancelado para esa semana? (sin contar el pedido "excepto")
    private boolean tienePedidoActivoEnSemana(Long idUsuario, LocalDate lunes, Long excepto) {
        for (Pedido otro : pedidoRepository.findByIdUsuarioOrderByIdDesc(idUsuario)) {
            if (Pedido.CANCELADO.equals(otro.getEstado()) || otro.getId().equals(excepto)) {
                continue;
            }
            List<PedidoDia> lineas = pedidoDiaRepository.findByIdPedidoOrderByFechaEntrega(otro.getId());
            if (!lineas.isEmpty() && Plazos.lunesDe(lineas.get(0).getFechaEntrega()).equals(lunes)) {
                return true;
            }
        }
        return false;
    }

    // Graba una linea por dia y descuenta una porcion del stock de esa opcion.
    private void guardarLineas(Long idPedido, List<PedidoDiaRequest> dias) {
        for (PedidoDiaRequest dia : dias) {
            PedidoDia linea = new PedidoDia();
            linea.setIdPedido(idPedido);
            linea.setIdMenuDia(dia.idMenuDia());
            linea.setIdPlato(dia.idPlato());
            linea.setFechaEntrega(dia.fechaEntrega());
            linea.setComentario(dia.comentario());
            pedidoDiaRepository.save(linea);

            MenuPlato opcion = menuPlatoRepository.findByIdMenuDiaAndIdPlato(dia.idMenuDia(), dia.idPlato()).get();
            opcion.setStockDisponible(opcion.getStockDisponible() - 1);
            menuPlatoRepository.save(opcion);
        }
    }

    // Devuelve al stock una porcion por cada linea del pedido.
    private void liberarStock(Long idPedido) {
        for (PedidoDia linea : pedidoDiaRepository.findByIdPedidoOrderByFechaEntrega(idPedido)) {
            menuPlatoRepository.findByIdMenuDiaAndIdPlato(linea.getIdMenuDia(), linea.getIdPlato())
                    .ifPresent(opcion -> {
                        opcion.setStockDisponible(opcion.getStockDisponible() + 1);
                        menuPlatoRepository.save(opcion);
                    });
        }
    }

    // "Después se confirma automáticamente": los BORRADORES cuyo plazo ya paso pasan a CONFIRMADO.
    private void confirmarVencidos() {
        LocalDateTime ahora = LocalDateTime.now(reloj);
        for (Pedido pedido : pedidoRepository.findByEstado(Pedido.BORRADOR)) {
            List<PedidoDia> lineas = pedidoDiaRepository.findByIdPedidoOrderByFechaEntrega(pedido.getId());
            if (!lineas.isEmpty() && !ahora.isBefore(Plazos.plazoDe(lineas.get(0).getFechaEntrega()))) {
                pedido.setEstado(Pedido.CONFIRMADO);
                pedidoRepository.save(pedido);
            }
        }
    }

    private Pedido buscarPedido(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el pedido " + id));
    }

    private PedidoResponse armarRespuesta(Pedido pedido) {
        return new PedidoResponse(pedido.getId(), pedido.getIdUsuario(), pedido.getFechaPedido(),
                pedido.getEstado(), pedidoDiaRepository.findByIdPedidoOrderByFechaEntrega(pedido.getId()));
    }
}
