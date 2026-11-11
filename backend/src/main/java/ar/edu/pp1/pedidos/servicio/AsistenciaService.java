package ar.edu.pp1.pedidos.servicio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<Asistencia> listar(Long idUsuario, LocalDate desde, LocalDate hasta) {
        return asistenciaRepository.findByIdUsuarioAndFechaBetweenOrderByFecha(idUsuario, desde, hasta);
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
