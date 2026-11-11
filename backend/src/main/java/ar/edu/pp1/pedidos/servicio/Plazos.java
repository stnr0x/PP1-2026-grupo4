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
}
