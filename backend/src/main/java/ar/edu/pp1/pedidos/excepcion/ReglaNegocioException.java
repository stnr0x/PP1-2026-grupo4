package ar.edu.pp1.pedidos.excepcion;

// Se lanza cuando un pedido HTTP rompe una regla de negocio (dia no laborable,
// plazo vencido, plato que no esta en el menu...). Se responde 400 Bad Request.
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
