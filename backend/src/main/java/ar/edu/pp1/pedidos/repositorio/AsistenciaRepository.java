package ar.edu.pp1.pedidos.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.Asistencia;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    // asistencia de un empleado entre dos fechas (una semana)
    List<Asistencia> findByIdUsuarioAndFechaBetweenOrderByFecha(Long idUsuario, LocalDate desde, LocalDate hasta);

    // la fila de un empleado para una fecha (UNIQUE id_usuario + fecha)
    Optional<Asistencia> findByIdUsuarioAndFecha(Long idUsuario, LocalDate fecha);

    // la ultima fila que guardo el empleado antes de una fecha (para repetir su semana habitual)
    Optional<Asistencia> findFirstByIdUsuarioAndFechaBeforeOrderByFechaDesc(Long idUsuario, LocalDate fecha);
}
