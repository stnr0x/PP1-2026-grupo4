package ar.edu.pp1.pedidos.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.MenuPlato;

public interface MenuPlatoRepository extends JpaRepository<MenuPlato, Long> {

    List<MenuPlato> findByIdMenuDia(Long idMenuDia);

    // el plato idPlato, ofrecido en el menu idMenuDia (si no esta, no se puede pedir)
    Optional<MenuPlato> findByIdMenuDiaAndIdPlato(Long idMenuDia, Long idPlato);

    void deleteByIdMenuDia(Long idMenuDia);
}
