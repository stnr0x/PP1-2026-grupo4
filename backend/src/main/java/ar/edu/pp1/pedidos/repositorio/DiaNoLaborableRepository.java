package ar.edu.pp1.pedidos.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.DiaNoLaborable;

public interface DiaNoLaborableRepository extends JpaRepository<DiaNoLaborable, Long> {

    List<DiaNoLaborable> findAllByOrderByFecha();

    boolean existsByFecha(LocalDate fecha);

    Optional<DiaNoLaborable> findByFecha(LocalDate fecha);
}
