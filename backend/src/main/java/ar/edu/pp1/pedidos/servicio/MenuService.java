package ar.edu.pp1.pedidos.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.pp1.pedidos.dto.SemanaMenuRequest;
import ar.edu.pp1.pedidos.entidad.MenuDia;
import ar.edu.pp1.pedidos.entidad.MenuPlato;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.DiaNoLaborableRepository;
import ar.edu.pp1.pedidos.repositorio.MenuDiaRepository;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;
import ar.edu.pp1.pedidos.repositorio.PlatoRepository;

// M3 Mis menus: los menus de cada dia (menu_dia) y sus opciones (menu_plato).
@Service
public class MenuService {

    // stock con el que arranca cada opcion cuando el admin la carga
    public static final int STOCK_INICIAL = 20;

    private final MenuDiaRepository menuDiaRepository;
    private final MenuPlatoRepository menuPlatoRepository;
    private final PlatoRepository platoRepository;
    private final DiaNoLaborableRepository diaNoLaborableRepository;
    private final PedidoDiaRepository pedidoDiaRepository;

    public MenuService(MenuDiaRepository menuDiaRepository, MenuPlatoRepository menuPlatoRepository,
            PlatoRepository platoRepository, DiaNoLaborableRepository diaNoLaborableRepository,
            PedidoDiaRepository pedidoDiaRepository) {
        this.menuDiaRepository = menuDiaRepository;
        this.menuPlatoRepository = menuPlatoRepository;
        this.platoRepository = platoRepository;
        this.diaNoLaborableRepository = diaNoLaborableRepository;
        this.pedidoDiaRepository = pedidoDiaRepository;
    }

    public List<MenuDia> listar(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null) {
            return menuDiaRepository.findByFechaBetweenOrderByFecha(desde, hasta);
        }
        return menuDiaRepository.findAllByOrderByFecha();
    }

    // Guarda (borrador) o publica los menus de una semana, de lunes a sabado.
    // Reglas: no se toca una semana que ya tiene pedidos, y en un dia no
    // laborable no puede haber menu.
    @Transactional
    public List<MenuDia> guardarSemana(SemanaMenuRequest semana) {
        LocalDate lunes = Plazos.lunesDe(semana.lunes());
        LocalDate sabado = lunes.plusDays(5);

        for (MenuDia menu : menuDiaRepository.findByFechaBetweenOrderByFecha(lunes, sabado)) {
            if (pedidoDiaRepository.existsByIdMenuDia(menu.getId())) {
                throw new ReglaNegocioException("Esta semana ya tiene pedidos: no se puede modificar.");
            }
        }

        int diasConPlatos = 0;
        for (SemanaMenuRequest.DiaMenu dia : semana.dias()) {
            LocalDate fecha = dia.fecha();
            List<Long> idPlatos = dia.idPlatos() == null ? List.of() : dia.idPlatos();

            if (fecha.isBefore(lunes) || fecha.isAfter(sabado)) {
                throw new ReglaNegocioException("El día " + fecha + " no es de esa semana.");
            }
            if (!idPlatos.isEmpty() && diaNoLaborableRepository.existsByFecha(fecha)) {
                throw new ReglaNegocioException("El " + fecha + " es no laborable: no puede tener menú.");
            }
            for (Long idPlato : idPlatos) {
                if (!platoRepository.existsById(idPlato)) {
                    throw new ReglaNegocioException("No existe el plato " + idPlato);
                }
            }

            MenuDia menu = menuDiaRepository.findByFecha(fecha).orElse(null);

            // se borran las opciones viejas de ese dia (se vuelven a cargar abajo)
            if (menu != null) {
                menuPlatoRepository.deleteByIdMenuDia(menu.getId());
            }

            // dia sin platos: si tenia menu, se borra
            if (idPlatos.isEmpty()) {
                if (menu != null) {
                    menuDiaRepository.delete(menu);
                }
                continue;
            }

            // dia con platos: si no tenia menu, se crea
            if (menu == null) {
                menu = new MenuDia();
                menu.setFecha(fecha);
                menu.setDescripcion("Menú del " + nombreDia(fecha));
                menu.setIdUsuarioCreador(semana.idUsuarioCreador());
            }
            menu.setPublicado(semana.publicar());
            menu = menuDiaRepository.save(menu);

            for (Long idPlato : idPlatos) {
                MenuPlato opcion = new MenuPlato();
                opcion.setIdMenuDia(menu.getId());
                opcion.setIdPlato(idPlato);
                opcion.setStockDisponible(STOCK_INICIAL);
                menuPlatoRepository.save(opcion);
            }
            diasConPlatos++;
        }

        if (diasConPlatos == 0) {
            throw new ReglaNegocioException("Elegí al menos un plato para algún día.");
        }
        return menuDiaRepository.findByFechaBetweenOrderByFecha(lunes, sabado);
    }

    private String nombreDia(LocalDate fecha) {
        return switch (fecha.getDayOfWeek()) {
            case MONDAY -> "lunes";
            case TUESDAY -> "martes";
            case WEDNESDAY -> "miércoles";
            case THURSDAY -> "jueves";
            case FRIDAY -> "viernes";
            case SATURDAY -> "sábado";
            case SUNDAY -> "domingo";
        };
    }
}
