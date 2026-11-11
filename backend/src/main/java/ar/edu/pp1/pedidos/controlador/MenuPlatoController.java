package ar.edu.pp1.pedidos.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.pp1.pedidos.entidad.MenuPlato;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;

// M3: las opciones de cada menu, con su stock (tabla menu_plato).
@RestController
@RequestMapping("/api/menu-platos")
public class MenuPlatoController {

    private final MenuPlatoRepository menuPlatoRepository;

    public MenuPlatoController(MenuPlatoRepository menuPlatoRepository) {
        this.menuPlatoRepository = menuPlatoRepository;
    }

    // GET /api/menu-platos  o  /api/menu-platos?idMenuDia=2
    @GetMapping
    public List<MenuPlato> listar(@RequestParam(required = false) Long idMenuDia) {
        if (idMenuDia != null) {
            return menuPlatoRepository.findByIdMenuDia(idMenuDia);
        }
        return menuPlatoRepository.findAll();
    }
}
