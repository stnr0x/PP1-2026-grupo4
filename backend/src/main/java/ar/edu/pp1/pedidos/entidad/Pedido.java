package ar.edu.pp1.pedidos.entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Tabla pedido: la cabecera (quien, cuando, en que estado).
// Estado: 'BORRADOR', 'CONFIRMADO' o 'CANCELADO'. Lo que se eligio cada dia esta en pedido_dia.
@Entity
@Table(name = "pedido")
public class Pedido {

    public static final String BORRADOR = "BORRADOR";
    public static final String CONFIRMADO = "CONFIRMADO";
    public static final String CANCELADO = "CANCELADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long id;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private LocalDate fechaPedido;

    @Column(nullable = false, length = 20)
    private String estado;

    public Pedido() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public LocalDate getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDate fechaPedido) { this.fechaPedido = fechaPedido; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
