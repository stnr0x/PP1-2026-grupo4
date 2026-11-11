package ar.edu.pp1.pedidos.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.edu.pp1.pedidos.entidad.PedidoDia;

public interface PedidoDiaRepository extends JpaRepository<PedidoDia, Long> {

    List<PedidoDia> findByIdPedidoOrderByFechaEntrega(Long idPedido);

    void deleteByIdPedido(Long idPedido);

    // ¿hay lineas de pedido que usan este menu? (si hay, el admin no puede sacarle platos)
    boolean existsByIdMenuDia(Long idMenuDia);

    // ¿algun pedido no cancelado tiene una linea para esa fecha?
    @Query("""
            SELECT COUNT(pd) > 0 FROM PedidoDia pd, Pedido pe
            WHERE pe.id = pd.idPedido
              AND pd.fechaEntrega = :fecha
              AND pe.estado <> 'CANCELADO'
            """)
    boolean hayPedidosActivosEnFecha(@Param("fecha") LocalDate fecha);

    // Cruce 2 del modelo explicado: el consolidado de una fecha.
    // No es una tabla: se CUENTA sobre pedido_dia, solo de pedidos CONFIRMADOS
    // (si no, contaria tambien los borradores).
    @Query(value = """
            SELECT p.nombre AS plato, COUNT(*) AS porciones
            FROM pedido_dia pd
            JOIN pedido pe ON pe.id_pedido = pd.id_pedido
            JOIN plato p   ON p.id_plato   = pd.id_plato
            WHERE pd.fecha_entrega = :fecha
              AND pe.estado = 'CONFIRMADO'
            GROUP BY p.id_plato, p.nombre
            ORDER BY porciones DESC, p.nombre
            """, nativeQuery = true)
    List<Object[]> consolidadoDeFecha(@Param("fecha") LocalDate fecha);
}
