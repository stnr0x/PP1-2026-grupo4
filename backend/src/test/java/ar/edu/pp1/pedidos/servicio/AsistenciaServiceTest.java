package ar.edu.pp1.pedidos.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ar.edu.pp1.pedidos.entidad.Asistencia;
import ar.edu.pp1.pedidos.repositorio.AsistenciaRepository;
import ar.edu.pp1.pedidos.repositorio.UsuarioRepository;

// Regla de M2: la asistencia queda guardada hasta que el empleado la cambie.
// Una semana sin configurar repite la ultima semana que guardo.
@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    private static final Long LUCIA = 3L;

    @Mock AsistenciaRepository asistenciaRepository;
    @Mock UsuarioRepository usuarioRepository;
    @InjectMocks AsistenciaService service;

    private Asistencia fila(Long id, LocalDate fecha, boolean asiste) {
        Asistencia a = new Asistencia();
        a.setId(id);
        a.setIdUsuario(LUCIA);
        a.setFecha(fecha);
        a.setAsiste(asiste);
        return a;
    }

    @Test
    void semanaSinConfigurarRepiteLaUltimaQueGuardo() {
        LocalDate lunesNuevo = LocalDate.of(2026, 11, 30);
        LocalDate sabadoNuevo = lunesNuevo.plusDays(5);
        LocalDate lunesViejo = LocalDate.of(2026, 11, 23);

        when(asistenciaRepository.findByIdUsuarioAndFechaBetweenOrderByFecha(LUCIA, lunesNuevo, sabadoNuevo))
                .thenReturn(List.of());
        when(asistenciaRepository.findFirstByIdUsuarioAndFechaBeforeOrderByFechaDesc(LUCIA, lunesNuevo))
                .thenReturn(Optional.of(fila(30L, lunesViejo.plusDays(5), false)));
        when(asistenciaRepository.findByIdUsuarioAndFechaBetweenOrderByFecha(LUCIA, lunesViejo, lunesViejo.plusDays(6)))
                .thenReturn(List.of(
                        fila(25L, lunesViejo, true),               // lunes: viene
                        fila(26L, lunesViejo.plusDays(1), false),  // martes: no
                        fila(27L, lunesViejo.plusDays(2), true))); // miercoles: viene

        List<Asistencia> semana = service.listar(LUCIA, lunesNuevo, sabadoNuevo);

        assertEquals(3, semana.size());
        assertEquals(lunesNuevo, semana.get(0).getFecha());           // misma regla, fechas nuevas
        assertTrue(semana.get(0).getAsiste());
        assertEquals(false, semana.get(1).getAsiste());
        assertTrue(semana.get(2).getAsiste());
        assertNull(semana.get(0).getId());                            // todavia no esta grabada
    }

    @Test
    void semanaYaConfiguradaSeRespetaTalCual() {
        LocalDate lunes = LocalDate.of(2026, 11, 30);
        List<Asistencia> guardada = List.of(fila(40L, lunes, false)); // ej. esa semana tiene una reunion
        when(asistenciaRepository.findByIdUsuarioAndFechaBetweenOrderByFecha(LUCIA, lunes, lunes.plusDays(5)))
                .thenReturn(guardada);

        assertEquals(guardada, service.listar(LUCIA, lunes, lunes.plusDays(5)));
    }

    @Test
    void sinNadaGuardadoDevuelveVacio() {
        LocalDate lunes = LocalDate.of(2026, 11, 30);
        when(asistenciaRepository.findByIdUsuarioAndFechaBetweenOrderByFecha(LUCIA, lunes, lunes.plusDays(5)))
                .thenReturn(List.of());
        when(asistenciaRepository.findFirstByIdUsuarioAndFechaBeforeOrderByFechaDesc(LUCIA, lunes))
                .thenReturn(Optional.empty());

        assertTrue(service.listar(LUCIA, lunes, lunes.plusDays(5)).isEmpty());
    }
}
