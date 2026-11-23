package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.pp1.pedidos.dto.PlatoRequest;
import ar.edu.pp1.pedidos.entidad.Plato;
import ar.edu.pp1.pedidos.servicio.PlatoService;
import jakarta.validation.Valid;

// Catalogo de platos (tabla plato).
@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    private final PlatoService platoService;

    public PlatoController(PlatoService platoService) {
        this.platoService = platoService;
    }

    @GetMapping
    public List<Plato> listar() {
        return platoService.listar();
    }

    @GetMapping("/{id}")
    public Plato buscar(@PathVariable Long id) {
        return platoService.buscar(id);
    }

    // POST /api/platos  (multipart/form-data: nombre, descripcion, categoria, imagen)
    //   -> 201 con el plato, 400 si falta algo o la imagen no es JPG/PNG, 409 si el nombre ya existe
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Plato> crear(@Valid @ModelAttribute PlatoRequest datos,
            @RequestPart(name = "imagen", required = false) MultipartFile imagen) {
        return ResponseEntity.status(HttpStatus.CREATED).body(platoService.crear(datos, imagen));
    }

    // DELETE /api/platos/{id}  -> 204, o 400 si el plato ya se uso en un menu o pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        platoService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
