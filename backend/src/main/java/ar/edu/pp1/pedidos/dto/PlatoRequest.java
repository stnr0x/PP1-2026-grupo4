package ar.edu.pp1.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Los datos de texto del formulario "Nuevo plato" (la imagen viene aparte, como archivo).
// Los largos son los de la tabla plato: nombre VARCHAR(120), descripcion VARCHAR(300),
// categoria VARCHAR(60).
public record PlatoRequest(
        @NotBlank(message = "El nombre del plato es obligatorio.")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres.") String nombre,

        @Size(max = 300, message = "La descripción no puede superar los 300 caracteres.") String descripcion,

        @NotBlank(message = "Elegí una categoría.") String categoria) {
}
