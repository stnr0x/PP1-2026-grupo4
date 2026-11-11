package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.dto.CambioContrasenaRequest;
import ar.edu.pp1.pedidos.dto.LoginRequest;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.servicio.UsuarioService;
import jakarta.validation.Valid;

// M1 Acceso. La contrasena nunca sale en el JSON (WRITE_ONLY en la entidad).
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/usuarios/login  -> 200 con el usuario, o 401 con el motivo
    @PostMapping("/login")
    public Usuario login(@Valid @RequestBody LoginRequest datos) {
        return usuarioService.login(datos);
    }

    // POST /api/usuarios  (registro) -> 201, o 409 si el correo ya existe
    @PostMapping
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(usuario));
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Long id) {
        return usuarioService.buscar(id);
    }

    // PUT /api/usuarios/{id}  (Mi Perfil: datos personales o dar de baja)
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioService.actualizar(id, datos);
    }

    // PUT /api/usuarios/{id}/contrasena  -> 204, o 400 si la actual no coincide
    @PutMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(@PathVariable Long id,
            @Valid @RequestBody CambioContrasenaRequest datos) {
        usuarioService.cambiarContrasena(id, datos.actual(), datos.nueva());
        return ResponseEntity.noContent().build();
    }
}
