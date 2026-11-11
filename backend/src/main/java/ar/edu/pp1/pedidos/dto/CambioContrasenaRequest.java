package ar.edu.pp1.pedidos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Cambio de contrasena desde Mi Perfil: la actual (para verificar) y la nueva.
public record CambioContrasenaRequest(
        @NotBlank(message = "Ingresá tu contraseña actual.") String actual,
        @NotBlank @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres.") String nueva) {
}
