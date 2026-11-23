package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.dto.CambioContrasenaRequest;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.servicio.UsuarioService;
import jakarta.validation.Valid;

// M1 Mi Perfil y listado de usuarios. El login y el registro estan en AuthController (/api/auth).
// La contrasena nunca sale en el JSON (WRITE_ONLY en la entidad).
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
