package ar.edu.pp1.pedidos.excepcion;

// Login fallido (correo o contrasena incorrectos, o cuenta dada de baja). Se responde 401.
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
