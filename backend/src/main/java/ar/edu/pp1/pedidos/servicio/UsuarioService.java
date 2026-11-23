package ar.edu.pp1.pedidos.servicio;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.pp1.pedidos.dto.LoginRequest;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.excepcion.AccesoDenegadoException;
import ar.edu.pp1.pedidos.excepcion.ConflictoException;
import ar.edu.pp1.pedidos.excepcion.NoEncontradoException;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.UsuarioRepository;

// M1 Acceso: login, registro y Mi Perfil.
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt (config/PasswordConfig)

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el usuario " + id));
    }

    // CU002: entra si el correo existe, la contrasena coincide y la cuenta esta activa.
    // En la base esta el hash: matches() hashea lo que escribio el usuario y lo compara.
    public Usuario login(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreo(datos.correo().trim())
                .orElseThrow(() -> new AccesoDenegadoException("Email o contraseña incorrectos."));
        if (!passwordEncoder.matches(datos.contrasena(), usuario.getContrasena())) {
            throw new AccesoDenegadoException("Email o contraseña incorrectos.");
        }
        if (!usuario.getActivo()) {
            throw new AccesoDenegadoException("Esta cuenta está dada de baja.");
        }
        return usuario;
    }

    // CU001: el que se registra siempre es EMPLEADO y arranca activo
    @Transactional
    public Usuario registrar(Usuario nuevo) {
        if (usuarioRepository.existsByCorreo(nuevo.getCorreo().trim())) {
            throw new ConflictoException("Ya hay una cuenta con ese email.");
        }
        nuevo.setId(null); // el id lo pone la base (AUTO_INCREMENT)
        nuevo.setCorreo(nuevo.getCorreo().trim());
        nuevo.setRol(Usuario.EMPLEADO);
        nuevo.setActivo(true);
        nuevo.setContrasena(passwordEncoder.encode(nuevo.getContrasena())); // se guarda el hash, no el texto
        return usuarioRepository.save(nuevo);
    }

    // CU003 / CU004: actualiza los datos personales o da de baja la cuenta.
    // No toca ni la contrasena ni el rol: eso no se cambia desde aca.
    @Transactional
    public Usuario actualizar(Long id, Usuario datos) {
        Usuario usuario = buscar(id);

        if (datos.getNombre() == null || datos.getNombre().isBlank()
                || datos.getApellido() == null || datos.getApellido().isBlank()) {
            throw new ReglaNegocioException("El nombre y el apellido son obligatorios.");
        }
        if (datos.getCorreo() == null || datos.getCorreo().isBlank()) {
            throw new ReglaNegocioException("El correo es obligatorio.");
        }
        String correo = datos.getCorreo().trim();
        usuarioRepository.findByCorreo(correo).ifPresent(otro -> {
            if (!otro.getId().equals(id)) {
                throw new ConflictoException("Ese email ya lo usa otra cuenta.");
            }
        });

        usuario.setNombre(datos.getNombre().trim());
        usuario.setApellido(datos.getApellido().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono(datos.getTelefono());
        if (datos.getActivo() != null) {
            usuario.setActivo(datos.getActivo());
        }
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarContrasena(Long id, String actual, String nueva) {
        Usuario usuario = buscar(id);
        if (!passwordEncoder.matches(actual, usuario.getContrasena())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta.");
        }
        usuario.setContrasena(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }
}
