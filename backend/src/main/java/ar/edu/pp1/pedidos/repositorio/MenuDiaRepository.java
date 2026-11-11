package ar.edu.pp1.pedidos.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.MenuDia;

public interface MenuDiaRepository extends JpaRepository<MenuDia, Long> {

    List<MenuDia> findAllByOrderByFecha();

    List<MenuDia> findByFechaBetweenOrderByFecha(LocalDate desde, LocalDate hasta);

    Optional<MenuDia> findByFecha(LocalDate fecha);
}
