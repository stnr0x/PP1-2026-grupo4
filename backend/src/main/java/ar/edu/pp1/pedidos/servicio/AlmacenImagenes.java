package ar.edu.pp1.pedidos.servicio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;

// Guarda en disco las imagenes de los platos que sube el administrador.
// - La carpeta la dice app.imagenes.dir (por defecto backend/imagenes, fuera del repo).
// - Spring la publica en app.imagenes.url (ver config/ImagenesConfig).
// - En la tabla plato, la columna imagen guarda esa URL.
// Por seguridad no se confia en lo que manda el navegador:
//   el nombre del archivo lo inventa el backend (UUID) y el tipo se averigua
//   leyendo el archivo: solo pasa si de verdad es un JPG o un PNG.
@Component
public class AlmacenImagenes {

    private final Path carpeta;
    private final String urlBase;

    public AlmacenImagenes(@Value("${app.imagenes.dir}") String dir,
            @Value("${app.imagenes.url}") String urlBase) {
        this.carpeta = Path.of(dir).toAbsolutePath().normalize();
        this.urlBase = urlBase.endsWith("/") ? urlBase : urlBase + "/";
    }

    public Path getCarpeta() {
        return carpeta;
    }

    // guarda la imagen y devuelve la URL para la columna plato.imagen
    public String guardar(MultipartFile archivo) {
        byte[] bytes;
        try {
            bytes = archivo.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        String extension = formatoReal(bytes);
        if (extension == null) {
            throw new ReglaNegocioException("La imagen tiene que ser un archivo JPG o PNG.");
        }

        String nombre = UUID.randomUUID() + "." + extension;
        try {
            Files.createDirectories(carpeta);
            Files.write(carpeta.resolve(nombre), bytes);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }
        return urlBase + nombre;
    }

    // borra la imagen si es una de las que subio el admin (las de assets/img no se tocan)
    public void borrarSiEsNuestra(String url) {
        if (url == null || !url.startsWith(urlBase)) {
            return;
        }
        Path archivo = carpeta.resolve(url.substring(urlBase.length())).normalize();
        if (!archivo.startsWith(carpeta)) {
            return; // nunca borrar algo fuera de la carpeta de imagenes
        }
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // si no se pudo borrar el archivo, el plato igual queda borrado
        }
    }

    // "jpg", "png" o null si los bytes no son una imagen de esos tipos
    private String formatoReal(byte[] bytes) {
        try (ImageInputStream entrada = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (entrada == null) {
                return null;
            }
            Iterator<ImageReader> lectores = ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext()) {
                return null;
            }
            String formato = lectores.next().getFormatName().toLowerCase();
            if (formato.equals("jpeg") || formato.equals("jpg")) {
                return "jpg";
            }
            if (formato.equals("png")) {
                return "png";
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
