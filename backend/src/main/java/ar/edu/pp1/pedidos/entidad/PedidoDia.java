package ar.edu.pp1.pedidos.entidad;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Tabla pedido_dia: una linea por dia del pedido. Puente entre pedido, menu_dia
// y plato CON atributos propios (fecha_entrega, comentario) -> entidad.
// De esta tabla sale el consolidado (se cuenta, no se guarda).
@Entity
@Table(name = "pedido_dia")
public class PedidoDia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_dia")
    private Long id;

    @Column(nullable = false)
    private Long idPedido;

    @Column(nullable = false)
    private Long idMenuDia;

    @Column(nullable = false)
    private Long idPlato;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    @Column(length = 200)
    private String comentario;

    public PedidoDia() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPedido() { return idPedido; }
    public void setIdPedido(Long idPedido) { this.idPedido = idPedido; }

    public Long getIdMenuDia() { return idMenuDia; }
    public void setIdMenuDia(Long idMenuDia) { this.idMenuDia = idMenuDia; }

    public Long getIdPlato() { return idPlato; }
    public void setIdPlato(Long idPlato) { this.idPlato = idPlato; }

    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
