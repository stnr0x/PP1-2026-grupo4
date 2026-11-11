package ar.edu.pp1.pedidos.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.dto.ConsolidadoItem;
import ar.edu.pp1.pedidos.servicio.PedidoService;

// M5 Admin: consolidado. No hay tabla "consolidado": se cuenta sobre pedido_dia.
@RestController
@RequestMapping("/api/consolidado")
public class ConsolidadoController {

    private final PedidoService pedidoService;

    public ConsolidadoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // GET /api/consolidado?fecha=2026-10-28  -> [{ "plato": "Milanesa con puré", "porciones": 2 }, ...]
    @GetMapping
    public List<ConsolidadoItem> deFecha(@RequestParam LocalDate fecha) {
        return pedidoService.consolidado(fecha);
    }
}
