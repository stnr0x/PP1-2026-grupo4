package ar.edu.pp1.pedidos.config;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// El "ahora" del sistema, en hora de Argentina. Se inyecta en los servicios
// en vez de llamar a LocalDateTime.now(): asi en los tests JUnit se puede
// usar un reloj fijo y probar el plazo del jueves a las 12.
@Configuration
public class RelojConfig {

    @Bean
    public Clock reloj() {
        return Clock.system(ZoneId.of("America/Argentina/Buenos_Aires"));
    }
}
