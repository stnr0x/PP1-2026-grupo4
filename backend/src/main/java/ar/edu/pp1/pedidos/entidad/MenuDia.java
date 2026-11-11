package ar.edu.pp1.pedidos.entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Tabla menu_dia: UN menu por fecha (fecha UNIQUE). Sus opciones estan en menu_plato.
// El stock total del dia NO es columna: es SUM(stock_disponible) de menu_plato.
@Entity
@Table(name = "menu_dia")
public class MenuDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu_dia")
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(length = 200)
    private String descripcion;

    @Column(nullable = false)
    private Boolean publicado;

    @Column(nullable = false)
    private Long idUsuarioCreador;

    public MenuDia() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getPublicado() { return publicado; }
    public void setPublicado(Boolean publicado) { this.publicado = publicado; }

    public Long getIdUsuarioCreador() { return idUsuarioCreador; }
    public void setIdUsuarioCreador(Long idUsuarioCreador) { this.idUsuarioCreador = idUsuarioCreador; }
}
