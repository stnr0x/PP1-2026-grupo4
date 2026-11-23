package ar.edu.pp1.pedidos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.Plato;

// La puerta a la tabla plato. Spring escribe findAll, findById, save, deleteById.
public interface PlatoRepository extends JpaRepository<Plato, Long> {

    // el catalogo en orden alfabetico (la collation de la base ordena bien tildes y la Ñ)
    List<Plato> findAllByOrderByNombreAsc();

    // ¿ya hay un plato con ese nombre? (sin importar mayusculas)
    boolean existsByNombreIgnoreCase(String nombre);
}
