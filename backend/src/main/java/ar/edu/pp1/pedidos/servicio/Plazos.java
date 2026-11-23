package ar.edu.pp1.pedidos.servicio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

// Calculos de fechas que usan varios servicios.
public final class Plazos {

    private Plazos() {
    }

    // lunes de la semana de una fecha, ej: 2026-10-28 -> 2026-10-26
    public static LocalDate lunesDe(LocalDate fecha) {
        return fecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    // plazo para pedir o modificar: el jueves anterior a esa semana, a las 12 hs
    public static LocalDateTime plazoDe(LocalDate fecha) {
        return lunesDe(fecha).minusDays(4).atTime(LocalTime.NOON);
    }

    // lunes de la semana para la que el empleado pide: la que viene, o la
    // siguiente si el plazo de la que viene ya cerro (jueves 12 hs).
    // Ej: martes 01/12 -> 07/12 · viernes 04/12 -> 14/12.
    // (La misma regla esta en lunesSemanaQueViene() de datos.js.)
    public static LocalDate lunesSemanaQueViene(LocalDateTime ahora) {
        LocalDate lunes = lunesDe(ahora.toLocalDate()).plusWeeks(1);
        if (!ahora.isBefore(plazoDe(lunes))) {
            return lunes.plusWeeks(1);
        }
        return lunes;
    }
}
