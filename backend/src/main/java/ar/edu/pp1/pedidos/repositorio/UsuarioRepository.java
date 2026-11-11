package ar.edu.pp1.pedidos.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // SELECT * FROM usuario WHERE correo = ?   (Spring arma la consulta por el nombre del metodo)
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);
}
