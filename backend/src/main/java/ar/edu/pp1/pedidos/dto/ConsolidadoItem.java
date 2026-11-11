package ar.edu.pp1.pedidos.dto;

// Una fila del consolidado: cuantas porciones de cada plato hay que pedir.
// No es una tabla: sale de contar pedido_dia (cruce 2).
public record ConsolidadoItem(String plato, Long porciones) {
}
