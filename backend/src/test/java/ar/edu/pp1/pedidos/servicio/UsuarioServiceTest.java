package ar.edu.pp1.pedidos.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ar.edu.pp1.pedidos.dto.LoginRequest;
import ar.edu.pp1.pedidos.entidad.Usuario;
import ar.edu.pp1.pedidos.excepcion.AccesoDenegadoException;
import ar.edu.pp1.pedidos.repositorio.UsuarioRepository;

// M1: la contrasena se guarda hasheada con BCrypt y el login la compara con matches().
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder(); // el real, no un mock
    private UsuarioService service;

    @BeforeEach
    void armar() {
        service = new UsuarioService(usuarioRepository, encoder);
    }

    private Usuario usuario(String correo, String contrasena) {
        Usuario u = new Usuario();
        u.setNombre("Ana");
        u.setApellido("Paz");
        u.setCorreo(correo);
        u.setContrasena(contrasena);
        u.setActivo(true);
        return u;
    }

    @Test
    void elRegistroGuardaElHashYNoElTexto() {
        when(usuarioRepository.existsByCorreo("ana@empresa.com.ar")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        Usuario guardado = service.registrar(usuario("ana@empresa.com.ar", "secreta1"));

        assertNotEquals("secreta1", guardado.getContrasena());
        assertTrue(guardado.getContrasena().startsWith("$2a$")); // formato BCrypt
        assertTrue(encoder.matches("secreta1", guardado.getContrasena()));
        assertEquals(Usuario.EMPLEADO, guardado.getRol());
    }

    @Test
    void loginConLaContrasenaCorrectaEntra() {
        Usuario lucia = usuario("lucia@empresa.com.ar", encoder.encode("cambiar123"));
        when(usuarioRepository.findByCorreo("lucia@empresa.com.ar")).thenReturn(Optional.of(lucia));

        Usuario entro = service.login(new LoginRequest("lucia@empresa.com.ar", "cambiar123"));

        assertEquals("lucia@empresa.com.ar", entro.getCorreo());
    }

    @Test
    void loginConContrasenaIncorrectaDa401() {
        Usuario lucia = usuario("lucia@empresa.com.ar", encoder.encode("cambiar123"));
        when(usuarioRepository.findByCorreo("lucia@empresa.com.ar")).thenReturn(Optional.of(lucia));

        assertThrows(AccesoDenegadoException.class,
                () -> service.login(new LoginRequest("lucia@empresa.com.ar", "otra")));
    }
}
