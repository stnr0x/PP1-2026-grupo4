package ar.edu.pp1.pedidos.dto;

import java.time.LocalDate;
import java.util.List;

import ar.edu.pp1.pedidos.entidad.PedidoDia;

// Lo que devuelve el backend: la cabecera del pedido con sus lineas adentro.
// Es la misma forma que tenia data/pedidos.json, por eso las pantallas no cambiaron.
// En la base son dos tablas (pedido y pedido_dia); en el JSON se anidan.
public record PedidoResponse(
        Long id,
        Long idUsuario,
        LocalDate fechaPedido,
        String estado,
        List<PedidoDia> pedidoDias) {
}
