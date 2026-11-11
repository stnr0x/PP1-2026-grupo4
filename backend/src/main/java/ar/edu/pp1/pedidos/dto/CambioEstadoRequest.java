package ar.edu.pp1.pedidos.dto;

import jakarta.validation.constraints.NotBlank;

// { "estado": "CONFIRMADO" } o { "estado": "CANCELADO" }
public record CambioEstadoRequest(@NotBlank(message = "Falta el estado.") String estado) {
}
