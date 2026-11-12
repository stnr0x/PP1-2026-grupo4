package ar.edu.pp1.pedidos.entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Tabla dia_no_laborable: guarda SOLO los dias sin servicio.
// No tiene FK con menu_dia: "en un dia no laborable no hay menu ni pedidos"
// es una regla de negocio y la hace cumplir la capa de servicio.
@Entity
@Table(name = "dia_no_laborable")
public class DiaNoLaborable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dia_no_laborable")
    private Long id;

    @NotNull(message = "Falta la fecha del día no laborable.")
    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Size(max = 120, message = "El motivo puede tener hasta 120 caracteres.")
    @Column(length = 120)
    private String motivo;

    public DiaNoLaborable() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
