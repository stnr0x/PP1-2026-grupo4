package ar.edu.pp1.pedidos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Lo que manda la pantalla de Menu al registrar o modificar un pedido:
// la cabecera (quien) y una linea por cada dia elegido.
public record PedidoRequest(
        @NotNull(message = "Falta el usuario del pedido.") Long idUsuario,
        @NotEmpty(message = "Elegí un plato para al menos un día.") @Valid List<PedidoDiaRequest> pedidoDias) {
}
