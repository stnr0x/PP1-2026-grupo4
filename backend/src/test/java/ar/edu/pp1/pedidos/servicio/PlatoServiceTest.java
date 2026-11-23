package ar.edu.pp1.pedidos.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import ar.edu.pp1.pedidos.dto.PlatoRequest;
import ar.edu.pp1.pedidos.entidad.Plato;
import ar.edu.pp1.pedidos.excepcion.ConflictoException;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;
import ar.edu.pp1.pedidos.repositorio.PlatoRepository;

// Alta y baja de platos: nombre unico, categoria valida, la imagen tiene que ser
// de verdad un JPG/PNG, y solo se borra un plato que nunca se uso.
@ExtendWith(MockitoExtension.class)
class PlatoServiceTest {

    private static final String URL = "http://localhost:8080/imagenes/";

    @Mock PlatoRepository platoRepository;
    @Mock MenuPlatoRepository menuPlatoRepository;
    @Mock PedidoDiaRepository pedidoDiaRepository;
    @TempDir Path carpeta;

    private PlatoService service;

    @BeforeEach
    void armar() {
        AlmacenImagenes almacen = new AlmacenImagenes(carpeta.toString(), URL); // el real, en una carpeta temporal
        service = new PlatoService(platoRepository, menuPlatoRepository, pedidoDiaRepository, almacen);
    }

    private static byte[] pngDePrueba() throws Exception {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "png", salida);
        return salida.toByteArray();
    }

    @Test
    void creaElPlatoYGuardaLaImagen() throws Exception {
        when(platoRepository.existsByNombreIgnoreCase("Lasagna")).thenReturn(false);
        when(platoRepository.save(any(Plato.class))).thenAnswer(i -> i.getArgument(0));
        MockMultipartFile imagen = new MockMultipartFile("imagen", "foto.png", "image/png", pngDePrueba());

        Plato plato = service.crear(new PlatoRequest("  Lasagna ", "Con bolognesa", "pastas"), imagen);

        assertEquals("Lasagna", plato.getNombre());
        assertTrue(plato.getImagen().startsWith(URL));
        assertTrue(plato.getImagen().endsWith(".png"));
        String archivo = plato.getImagen().substring(URL.length());
        assertTrue(Files.exists(carpeta.resolve(archivo))); // quedo en disco con un nombre inventado
        assertFalse(archivo.contains("foto")); // no se usa el nombre que mando el navegador
    }

    @Test
    void sinImagenElPlatoSeCreaIgual() {
        when(platoRepository.existsByNombreIgnoreCase("Sopa")).thenReturn(false);
        when(platoRepository.save(any(Plato.class))).thenAnswer(i -> i.getArgument(0));

        Plato plato = service.crear(new PlatoRequest("Sopa", "", "liviano"), null);

        assertNull(plato.getImagen()); // la pantalla muestra sin-imagen.svg
        assertNull(plato.getDescripcion());
    }

    @Test
    void nombreRepetidoDa409() {
        when(platoRepository.existsByNombreIgnoreCase("Milanesa con puré")).thenReturn(true);

        assertThrows(ConflictoException.class,
                () -> service.crear(new PlatoRequest("Milanesa con puré", null, "clásico"), null));
        verify(platoRepository, never()).save(any());
    }

    @Test
    void categoriaInventadaDa400() {
        when(platoRepository.existsByNombreIgnoreCase("Flan")).thenReturn(false);

        assertThrows(ReglaNegocioException.class,
                () -> service.crear(new PlatoRequest("Flan", null, "postres"), null));
    }

    @Test
    void unArchivoQueNoEsImagenDa400AunqueDigaJpg() {
        when(platoRepository.existsByNombreIgnoreCase("Truco")).thenReturn(false);
        MockMultipartFile falso = new MockMultipartFile("imagen", "foto.jpg", "image/jpeg",
                "<script>alert(1)</script>".getBytes());

        assertThrows(ReglaNegocioException.class,
                () -> service.crear(new PlatoRequest("Truco", null, "clásico"), falso));
        verify(platoRepository, never()).save(any());
    }

    @Test
    void noSeBorraUnPlatoQueYaEstaEnUnMenu() {
        Plato plato = new Plato();
        plato.setId(1L);
        plato.setNombre("Milanesa con puré");
        when(platoRepository.findById(1L)).thenReturn(Optional.of(plato));
        when(menuPlatoRepository.existsByIdPlato(1L)).thenReturn(true);

        assertThrows(ReglaNegocioException.class, () -> service.borrar(1L));
        verify(platoRepository, never()).delete(any());
    }

    @Test
    void seBorraUnPlatoSinUsarYSuImagen() throws Exception {
        Path archivo = carpeta.resolve("abc.png");
        Files.write(archivo, pngDePrueba());
        Plato plato = new Plato();
        plato.setId(13L);
        plato.setNombre("Lasagna");
        plato.setImagen(URL + "abc.png");
        when(platoRepository.findById(13L)).thenReturn(Optional.of(plato));
        when(menuPlatoRepository.existsByIdPlato(13L)).thenReturn(false);
        when(pedidoDiaRepository.existsByIdPlato(13L)).thenReturn(false);

        service.borrar(13L);

        verify(platoRepository).delete(plato);
        assertFalse(Files.exists(archivo));
    }
}
