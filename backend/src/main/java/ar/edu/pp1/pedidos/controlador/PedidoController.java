package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.dto.CambioEstadoRequest;
import ar.edu.pp1.pedidos.dto.PedidoRequest;
import ar.edu.pp1.pedidos.dto.PedidoResponse;
import ar.edu.pp1.pedidos.servicio.PedidoService;
import jakarta.validation.Valid;

// M4 Mi pedido. El controller solo atiende la URL: las reglas estan en PedidoService.
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // GET /api/pedidos               -> todos (admin)
    // GET /api/pedidos?idUsuario=3   -> mis pedidos (cruce 3)
    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) Long idUsuario) {
        return pedidoService.listar(idUsuario);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) {
        return pedidoService.buscar(id);
    }

    // POST /api/pedidos  -> 201 con el pedido en BORRADOR, o 400 con el motivo
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(datos));
    }

    // PUT /api/pedidos/{id}  -> reemplaza los dias del pedido
    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @Valid @RequestBody PedidoRequest datos) {
        return pedidoService.modificar(id, datos);
    }

    // PATCH /api/pedidos/{id}/estado  con { "estado": "CONFIRMADO" | "CANCELADO" }
    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest datos) {
        return pedidoService.cambiarEstado(id, datos.estado());
    }
}
