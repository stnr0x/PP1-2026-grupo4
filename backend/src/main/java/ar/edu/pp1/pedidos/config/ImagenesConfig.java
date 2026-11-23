package ar.edu.pp1.pedidos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ar.edu.pp1.pedidos.servicio.AlmacenImagenes;

// Publica la carpeta de imagenes subidas: http://localhost:8080/imagenes/<archivo>.
// Asi el <img src="..."> del frontend las puede mostrar.
@Configuration
public class ImagenesConfig implements WebMvcConfigurer {

    private final AlmacenImagenes almacenImagenes;

    public ImagenesConfig(AlmacenImagenes almacenImagenes) {
        this.almacenImagenes = almacenImagenes;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String carpeta = almacenImagenes.getCarpeta().toUri().toString();
        if (!carpeta.endsWith("/")) {
            carpeta = carpeta + "/"; // Spring necesita la barra final para tratarla como carpeta
        }
        registry.addResourceHandler("/imagenes/**").addResourceLocations(carpeta);
    }
}
