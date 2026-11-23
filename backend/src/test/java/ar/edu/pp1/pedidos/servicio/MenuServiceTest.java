package ar.edu.pp1.pedidos.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.pp1.pedidos.entidad.Asistencia;
import ar.edu.pp1.pedidos.entidad.MenuDia;
import ar.edu.pp1.pedidos.repositorio.DiaNoLaborableRepository;
import ar.edu.pp1.pedidos.repositorio.MenuDiaRepository;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;
import ar.edu.pp1.pedidos.repositorio.PlatoRepository;

// M3: el empleado solo recibe los menus que puede pedir. El filtro lo hace el
// backend: publicado, no feriado y dia que el empleado asiste.
@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    private static final Long LUCIA = 3L;
    private static final ZoneId AR = ZoneId.of("America/Argentina/Buenos_Aires");

    // semana del 07/12/2026: el 08/12 es feriado
    private static final LocalDate LUNES = LocalDate.of(2026, 12, 7);
    private static final LocalDate MARTES = LUNES.plusDays(1);
    private static final LocalDate MIERCOLES = LUNES.plusDays(2);
    private static final LocalDate JUEVES = LUNES.plusDays(3);
    private static final LocalDate VIERNES = LUNES.plusDays(4);

    @Mock MenuDiaRepository menuDiaRepository;
    @Mock MenuPlatoRepository menuPlatoRepository;
    @Mock PlatoRepository platoRepository;
    @Mock DiaNoLaborableRepository diaNoLaborableRepository;
    @Mock PedidoDiaRepository pedidoDiaRepository;
    @Mock AsistenciaService asistenciaService;

    private MenuService serviceA(LocalDateTime ahora) {
        Clock reloj = Clock.fixed(ahora.atZone(AR).toInstant(), AR);
        return new MenuService(menuDiaRepository, menuPlatoRepository, platoRepository,
                diaNoLaborableRepository, pedidoDiaRepository, asistenciaService, reloj);
    }

    private MenuDia menu(long id, LocalDate fecha, boolean publicado) {
        MenuDia m = new MenuDia();
        m.setId(id);
        m.setFecha(fecha);
        m.setPublicado(publicado);
        return m;
    }

    private Asistencia asiste(LocalDate fecha, boolean asiste) {
        Asistencia a = new Asistencia();
        a.setIdUsuario(LUCIA);
        a.setFecha(fecha);
        a.setAsiste(asiste);
        return a;
    }

    @Test
    void soloLlegaLoPublicadoNoFeriadoYLosDiasQueAsiste() {
        // martes 01/12 a la tarde: se pide para la semana del 07/12
        MenuService service = serviceA(LocalDateTime.of(2026, 12, 1, 15, 0));

        when(menuDiaRepository.findByFechaBetweenOrderByFecha(LUNES, LUNES.plusDays(5))).thenReturn(List.of(
                menu(1, LUNES, true),        // publicado y asiste        -> SI
                menu(2, MARTES, true),       // feriado 08/12             -> no
                menu(3, MIERCOLES, true),    // no asiste                 -> no
                menu(4, JUEVES, false),      // sin publicar (borrador)   -> no
                menu(5, VIERNES, true)));    // publicado y asiste        -> SI
        lenient().when(diaNoLaborableRepository.existsByFecha(MARTES)).thenReturn(true);
        when(asistenciaService.listar(LUCIA, LUNES, LUNES.plusDays(5))).thenReturn(List.of(
                asiste(LUNES, true), asiste(MARTES, true), asiste(MIERCOLES, false),
                asiste(JUEVES, true), asiste(VIERNES, true)));

        List<MenuDia> menus = service.listarParaEmpleado(LUCIA);

        assertEquals(List.of(1L, 5L), menus.stream().map(MenuDia::getId).toList());
    }

    @Test
    void sinAsistenciaConfiguradaNoVeNingunMenu() {
        MenuService service = serviceA(LocalDateTime.of(2026, 12, 1, 15, 0));
        when(menuDiaRepository.findByFechaBetweenOrderByFecha(LUNES, LUNES.plusDays(5)))
                .thenReturn(List.of(menu(1, LUNES, true)));
        when(asistenciaService.listar(LUCIA, LUNES, LUNES.plusDays(5))).thenReturn(List.of());

        assertEquals(0, service.listarParaEmpleado(LUCIA).size());
    }

    @Test
    void despuesDelJuevesALas12PasaALaSemanaSiguiente() {
        assertEquals(LUNES, Plazos.lunesSemanaQueViene(LocalDateTime.of(2026, 12, 3, 11, 59)));
        assertEquals(LUNES.plusWeeks(1), Plazos.lunesSemanaQueViene(LocalDateTime.of(2026, 12, 3, 12, 0)));
    }
}
