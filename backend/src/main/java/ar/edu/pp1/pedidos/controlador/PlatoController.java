package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.entidad.Plato;
import ar.edu.pp1.pedidos.repositorio.PlatoRepository;

// Catalogo de platos. Solo lectura: no hay reglas, por eso no tiene Service.
@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    private final PlatoRepository platoRepository;

    public PlatoController(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    @GetMapping
    public List<Plato> listar() {
        return platoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plato> buscar(@PathVariable Long id) {
        return platoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
