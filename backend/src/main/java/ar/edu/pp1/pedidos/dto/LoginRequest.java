package ar.edu.pp1.pedidos.dto;

import jakarta.validation.constraints.NotBlank;

// Lo que manda la pantalla de login: { "correo": "...", "contrasena": "..." }
public record LoginRequest(
        @NotBlank(message = "Ingresá tu email.") String correo,
        @NotBlank(message = "Ingresá tu contraseña.") String contrasena) {
}
