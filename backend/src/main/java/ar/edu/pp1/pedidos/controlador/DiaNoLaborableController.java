package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.entidad.DiaNoLaborable;
import jakarta.validation.Valid;
import ar.edu.pp1.pedidos.servicio.DiaNoLaborableService;

// Pantalla Feriados (admin) y Menu (no ofrecer esos dias).
@RestController
@RequestMapping("/api/dias-no-laborables")
public class DiaNoLaborableController {

    private final DiaNoLaborableService diaNoLaborableService;

    public DiaNoLaborableController(DiaNoLaborableService diaNoLaborableService) {
        this.diaNoLaborableService = diaNoLaborableService;
    }

    @GetMapping
    public List<DiaNoLaborable> listar() {
        return diaNoLaborableService.listar();
    }

    // PUT /api/dias-no-laborables  con la lista completa [{ fecha, motivo }, ...]
    @PutMapping
    public List<DiaNoLaborable> reemplazar(@RequestBody List<@Valid DiaNoLaborable> dias) {
        return diaNoLaborableService.reemplazar(dias);
    }
}
