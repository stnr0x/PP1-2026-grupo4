package ar.edu.pp1.pedidos.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import ar.edu.pp1.pedidos.dto.PedidoDiaRequest;
import ar.edu.pp1.pedidos.dto.PedidoRequest;
import ar.edu.pp1.pedidos.dto.PedidoResponse;
import ar.edu.pp1.pedidos.entidad.MenuDia;
import ar.edu.pp1.pedidos.entidad.MenuPlato;
import ar.edu.pp1.pedidos.entidad.Pedido;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.DiaNoLaborableRepository;
import ar.edu.pp1.pedidos.repositorio.MenuDiaRepository;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoRepository;
import ar.edu.pp1.pedidos.repositorio.UsuarioRepository;

// Test de la regla de negocio principal: "¿se puede pedir ese dia?".
// No usa la base: los repositorios son mocks (Mockito) y el reloj esta fijo,
// asi el test da siempre lo mismo, sin importar que dia se corra.
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PedidoServiceTest {

    private static final ZoneId ARGENTINA = ZoneId.of("America/Argentina/Buenos_Aires");

    // semana del lunes 16/11/2026; el plazo cierra el jueves 12/11 a las 12
    private static final LocalDate LUNES = LocalDate.of(2026, 11, 16);

    @Mock PedidoRepository pedidoRepository;
    @Mock PedidoDiaRepository pedidoDiaRepository;
    @Mock MenuDiaRepository menuDiaRepository;
    @Mock MenuPlatoRepository menuPlatoRepository;
    @Mock DiaNoLaborableRepository diaNoLaborableRepository;
    @Mock UsuarioRepository usuarioRepository;

    private MenuDia menuLunes;
    private MenuPlato milanesaLunes;

    @BeforeEach
    void preparar() {
        Usuario lucia = new Usuario();
        lucia.setId(3L);
        lucia.setRol(Usuario.EMPLEADO);
        lucia.setActivo(true);
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(lucia));

        menuLunes = new MenuDia();
        menuLunes.setId(10L);
        menuLunes.setFecha(LUNES);
        menuLunes.setPublicado(true);
        when(menuDiaRepository.findById(10L)).thenReturn(Optional.of(menuLunes));

        milanesaLunes = new MenuPlato();
        milanesaLunes.setIdMenuDia(10L);
        milanesaLunes.setIdPlato(1L);
        milanesaLunes.setStockDisponible(20);
        when(menuPlatoRepository.findByIdMenuDiaAndIdPlato(10L, 1L)).thenReturn(Optional.of(milanesaLunes));

        when(pedidoRepository.findByIdUsuarioOrderByIdDesc(3L)).thenReturn(List.of());
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(99L);
            return p;
        });
    }

    // el servicio con el reloj parado en un momento dado
    private PedidoService servicioEl(int dia, int hora) {
        Clock reloj = Clock.fixed(ZonedDateTime.of(2026, 11, dia, hora, 0, 0, 0, ARGENTINA).toInstant(), ARGENTINA);
        return new PedidoService(pedidoRepository, pedidoDiaRepository, menuDiaRepository,
                menuPlatoRepository, diaNoLaborableRepository, usuarioRepository, reloj);
    }

    private PedidoRequest pedidoDeLucia(Long idMenuDia, Long idPlato, LocalDate fecha) {
        return new PedidoRequest(3L, List.of(new PedidoDiaRequest(idMenuDia, idPlato, fecha, null)));
    }

    @Test
    void pedidoValidoQuedaEnBorradorYDescuentaStock() {
        PedidoResponse pedido = servicioEl(11, 18).crear(pedidoDeLucia(10L, 1L, LUNES));

        assertEquals(Pedido.BORRADOR, pedido.estado());
        assertEquals(19, milanesaLunes.getStockDisponible()); // 20 - 1
    }

    @Test
    void noSePuedePedirEnUnDiaNoLaborable() {
        when(diaNoLaborableRepository.existsByFecha(LUNES)).thenReturn(true);

        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicioEl(11, 18).crear(pedidoDeLucia(10L, 1L, LUNES)));

        assertEquals("El 2026-11-16 es un día no laborable: no se puede pedir.", error.getMessage());
        verify(pedidoRepository, never()).save(any()); // no se guardo nada
    }

    @Test
    void noSePuedePedirDeUnMenuSinPublicar() {
        menuLunes.setPublicado(false);

        assertThrows(ReglaNegocioException.class,
                () -> servicioEl(11, 18).crear(pedidoDeLucia(10L, 1L, LUNES)));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void noSePuedePedirUnPlatoQueNoEstaEnElMenu() {
        // el plato 5 no esta en el menu del lunes (findByIdMenuDiaAndIdPlato devuelve vacio)
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicioEl(11, 18).crear(pedidoDeLucia(10L, 5L, LUNES)));

        assertEquals("Ese plato no está en el menú del 2026-11-16.", error.getMessage());
    }

    @Test
    void noSePuedePedirSinStock() {
        milanesaLunes.setStockDisponible(0);

        assertThrows(ReglaNegocioException.class,
                () -> servicioEl(11, 18).crear(pedidoDeLucia(10L, 1L, LUNES)));
    }

    @Test
    void elJuevesAntesDeLas12TodaviaSePuedePedir() {
        PedidoResponse pedido = servicioEl(12, 11).crear(pedidoDeLucia(10L, 1L, LUNES)); // jueves 12/11 11:00

        assertEquals(Pedido.BORRADOR, pedido.estado());
    }

    @Test
    void elJuevesDesdeLas12ElPlazoEstaCerrado() {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicioEl(12, 12).crear(pedidoDeLucia(10L, 1L, LUNES))); // jueves 12/11 12:00

        assertEquals("El plazo para pedir esa semana ya cerró (jueves anterior a las 12 hs).", error.getMessage());
    }
}
