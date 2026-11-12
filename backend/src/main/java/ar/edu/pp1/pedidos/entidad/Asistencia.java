package ar.edu.pp1.pedidos.entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

// Tabla asistencia: una fila por empleado y por fecha (UNIQUE id_usuario + fecha).
// Es una tabla propia y no un campo del usuario: asi se puede decir
// "el 27/10 si, el 28/10 no" y preguntar quien viene un dia dado.
@Entity
@Table(name = "asistencia")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long id;

    @NotNull(message = "Falta el usuario.")
    @Column(nullable = false)
    private Long idUsuario;

    @NotNull(message = "Falta la fecha.")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "Falta indicar si asiste.")
    @Column(nullable = false)
    private Boolean asiste;

    public Asistencia() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Boolean getAsiste() { return asiste; }
    public void setAsiste(Boolean asiste) { this.asiste = asiste; }
}
