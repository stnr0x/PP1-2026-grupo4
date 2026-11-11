package ar.edu.pp1.pedidos.excepcion;

// Se lanza cuando el dato choca con otro que ya existe (ej: correo ya registrado). Se responde 409.
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
