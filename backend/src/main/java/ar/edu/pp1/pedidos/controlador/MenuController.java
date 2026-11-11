package ar.edu.pp1.pedidos.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.dto.SemanaMenuRequest;
import ar.edu.pp1.pedidos.entidad.MenuDia;
import ar.edu.pp1.pedidos.servicio.MenuService;
import jakarta.validation.Valid;

// M3 Mis menus (tabla menu_dia).
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // GET /api/menus                              -> todos
    // GET /api/menus?fecha=2026-10-27             -> el de ese dia
    // GET /api/menus?desde=2026-10-26&hasta=2026-10-31
    @GetMapping
    public List<MenuDia> listar(@RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        if (fecha != null) {
            return menuService.listar(fecha, fecha);
        }
        return menuService.listar(desde, hasta);
    }

    // PUT /api/menus/semana  (Administrar menus: guardar borrador o publicar la semana)
    @PutMapping("/semana")
    public List<MenuDia> guardarSemana(@Valid @RequestBody SemanaMenuRequest semana) {
        return menuService.guardarSemana(semana);
    }
}
