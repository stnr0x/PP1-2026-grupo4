package ar.edu.pp1.pedidos.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.Plato;

// La puerta a la tabla plato. Spring escribe findAll, findById, save, deleteById.
public interface PlatoRepository extends JpaRepository<Plato, Long> {
}
