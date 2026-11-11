package ar.edu.pp1.pedidos.excepcion;

// Se lanza cuando se pide algo que no existe (un id que no esta). Se responde 404.
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
