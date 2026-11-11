package ar.edu.pp1.pedidos.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// Lo que manda Administrar menus al guardar o publicar una semana:
// para cada dia (lunes a sabado), los platos que se ofrecen.
public record SemanaMenuRequest(
        @NotNull(message = "Falta el lunes de la semana.") LocalDate lunes,
        @NotNull Boolean publicar,
        @NotNull(message = "Falta el usuario que crea el menú.") Long idUsuarioCreador,
        @NotEmpty List<DiaMenu> dias) {

    public record DiaMenu(@NotNull LocalDate fecha, List<Long> idPlatos) {
    }
}
