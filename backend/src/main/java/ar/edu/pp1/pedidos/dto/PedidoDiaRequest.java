package ar.edu.pp1.pedidos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Una linea del pedido: que plato (idPlato) de que menu (idMenuDia) para que dia.
public record PedidoDiaRequest(
        @NotNull(message = "Falta el menú del día.") Long idMenuDia,
        @NotNull(message = "Falta el plato elegido.") Long idPlato,
        @NotNull(message = "Falta la fecha de entrega.") LocalDate fechaEntrega,
        @Size(max = 200, message = "El comentario no puede superar los 200 caracteres.") String comentario) {
}
