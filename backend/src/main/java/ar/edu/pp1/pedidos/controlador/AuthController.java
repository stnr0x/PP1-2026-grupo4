package ar.edu.pp1.pedidos.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.dto.LoginRequest;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.servicio.UsuarioService;
import jakarta.validation.Valid;

// M1 Acceso: login y registro, en las rutas que pide la consigna (/api/auth).
// La contrasena nunca sale en el JSON (WRITE_ONLY en la entidad).
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/auth/login  -> 200 con el usuario, o 401 con el motivo
    @PostMapping("/login")
    public Usuario login(@Valid @RequestBody LoginRequest datos) {
        return usuarioService.login(datos);
    }

    // POST /api/auth/registro  -> 201, o 409 si el correo ya existe
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(usuario));
    }
}
