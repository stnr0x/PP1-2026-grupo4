package ar.edu.pp1.pedidos.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.entidad.Asistencia;
import jakarta.validation.Valid;
import ar.edu.pp1.pedidos.servicio.AsistenciaService;

// M2 Mis dias.
@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    // GET /api/asistencias?idUsuario=3&desde=2026-10-26&hasta=2026-10-31
    @GetMapping
    public List<Asistencia> listar(@RequestParam Long idUsuario,
            @RequestParam LocalDate desde, @RequestParam LocalDate hasta) {
        return asistenciaService.listar(idUsuario, desde, hasta);
    }

    // PUT /api/asistencias  con [{ idUsuario, fecha, asiste }, ...]  (crea o actualiza cada dia)
    // List<@Valid ...>: Bean Validation revisa cada elemento de la lista
    @PutMapping
    public List<Asistencia> guardar(@RequestBody List<@Valid Asistencia> dias) {
        return asistenciaService.guardar(dias);
    }
}
