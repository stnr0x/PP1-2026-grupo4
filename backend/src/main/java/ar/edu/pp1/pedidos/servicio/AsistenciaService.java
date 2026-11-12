package ar.edu.pp1.pedidos.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.pp1.pedidos.entidad.Asistencia;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.AsistenciaRepository;
import ar.edu.pp1.pedidos.repositorio.UsuarioRepository;

// M2 Mis dias: que dias viene cada empleado. Una fila por (usuario, fecha).
@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaService(AsistenciaRepository asistenciaRepository, UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Devuelve la asistencia de una semana. Si el empleado todavia no la configuro,
    // se repite la ultima semana que guardo: su horario queda "guardado hasta que
    // lo vuelva a cambiar". Esas filas repetidas vienen con id = null porque todavia
    // no estan en la tabla; se graban recien cuando el empleado guarda (PUT).
    public List<Asistencia> listar(Long idUsuario, LocalDate desde, LocalDate hasta) {
        List<Asistencia> semana = asistenciaRepository.findByIdUsuarioAndFechaBetweenOrderByFecha(idUsuario, desde, hasta);
        if (!semana.isEmpty()) {
            return semana; // esa semana ya la configuro: se respeta tal cual (ej. un cambio por una reunion)
        }

        Optional<Asistencia> ultima = asistenciaRepository.findFirstByIdUsuarioAndFechaBeforeOrderByFechaDesc(idUsuario, desde);
        if (ultima.isEmpty()) {
            return semana; // nunca configuro nada: lista vacia, la pantalla le pide que la cargue
        }

        // la semana (lunes a domingo) de esa ultima fila es su patron habitual
        LocalDate lunesPatron = Plazos.lunesDe(ultima.get().getFecha());
        List<Asistencia> patron = asistenciaRepository
                .findByIdUsuarioAndFechaBetweenOrderByFecha(idUsuario, lunesPatron, lunesPatron.plusDays(6));

        // copia el patron a las fechas pedidas, dia de la semana por dia de la semana
        List<Asistencia> repetida = new ArrayList<>();
        for (LocalDate fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            for (Asistencia dia : patron) {
                if (dia.getFecha().getDayOfWeek() == fecha.getDayOfWeek()) {
                    Asistencia copia = new Asistencia();
                    copia.setIdUsuario(idUsuario);
                    copia.setFecha(fecha);
                    copia.setAsiste(dia.getAsiste());
                    repetida.add(copia);
                }
            }
        }
        return repetida;
    }

    // Guarda la asistencia de varios dias. Si ya habia una fila para ese usuario
    // y esa fecha, la actualiza (asi se respeta el UNIQUE id_usuario + fecha);
    // si no habia, la crea.
    @Transactional
    public List<Asistencia> guardar(List<Asistencia> dias) {
        if (dias == null || dias.isEmpty()) {
            throw new ReglaNegocioException("No hay días para guardar.");
        }
        boolean algunoAsiste = dias.stream().anyMatch(d -> Boolean.TRUE.equals(d.getAsiste()));
        if (!algunoAsiste) {
            throw new ReglaNegocioException("Marcá al menos un día de asistencia."); // CU005 flujo 2A
        }

        List<Asistencia> guardadas = new ArrayList<>();
        for (Asistencia dia : dias) {
            if (dia.getIdUsuario() == null || dia.getFecha() == null || dia.getAsiste() == null) {
                throw new ReglaNegocioException("Cada día necesita idUsuario, fecha y asiste.");
            }
            if (!usuarioRepository.existsById(dia.getIdUsuario())) {
                throw new ReglaNegocioException("No existe el usuario " + dia.getIdUsuario());
            }
            Asistencia fila = asistenciaRepository
                    .findByIdUsuarioAndFecha(dia.getIdUsuario(), dia.getFecha())
                    .orElseGet(Asistencia::new);
            fila.setIdUsuario(dia.getIdUsuario());
            fila.setFecha(dia.getFecha());
            fila.setAsiste(dia.getAsiste());
            guardadas.add(asistenciaRepository.save(fila));
        }
        return guardadas;
    }
}
