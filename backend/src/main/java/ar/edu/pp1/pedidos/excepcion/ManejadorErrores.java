package ar.edu.pp1.pedidos.excepcion;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// Traduce las excepciones a respuestas HTTP con un JSON { "mensaje": "..." }.
// Asi el frontend puede mostrar el motivo exacto del error.
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return respuesta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoEncontradoException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(NoEncontradoException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<Map<String, String>> conflicto(ConflictoException e) {
        return respuesta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, String>> accesoDenegado(AccesoDenegadoException e) {
        return respuesta(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // Bean Validation: un @NotBlank, @Email, @Size que no se cumple
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> validacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Bean Validation sobre cada elemento de una lista (List<@Valid Asistencia>)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, String>> validacionLista(HandlerMethodValidationException e) {
        String mensaje = e.getAllErrors().get(0).getDefaultMessage();
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // la imagen del plato supera spring.servlet.multipart.max-file-size
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> archivoGrande(MaxUploadSizeExceededException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "La imagen no puede pesar más de 2 MB.");
    }

    private ResponseEntity<Map<String, String>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", mensaje));
    }
}
