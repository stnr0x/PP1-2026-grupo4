package ar.edu.pp1.pedidos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.edu.pp1.pedidos.entidad.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByIdUsuarioOrderByIdDesc(Long idUsuario);

    List<Pedido> findByEstado(String estado);
}
