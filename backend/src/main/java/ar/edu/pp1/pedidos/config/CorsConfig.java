package ar.edu.pp1.pedidos.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// El frontend corre en Live Server (puerto 5500) y Spring en el 8080: son origenes
// distintos y el navegador bloquea el fetch salvo que el backend lo autorice (CORS).
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
    }
}
