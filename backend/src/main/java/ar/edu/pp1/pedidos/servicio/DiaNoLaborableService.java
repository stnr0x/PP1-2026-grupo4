package ar.edu.pp1.pedidos.servicio;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.pp1.pedidos.entidad.DiaNoLaborable;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.DiaNoLaborableRepository;
import ar.edu.pp1.pedidos.repositorio.MenuDiaRepository;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;

// Pantalla Feriados (admin). La base no impide que haya menu o pedidos en un
// dia no laborable (no hay FK entre dia_no_laborable y menu_dia): lo impide este servicio.
@Service
public class DiaNoLaborableService {

    private final DiaNoLaborableRepository diaNoLaborableRepository;
    private final PedidoDiaRepository pedidoDiaRepository;
    private final MenuDiaRepository menuDiaRepository;
    private final MenuPlatoRepository menuPlatoRepository;

    public DiaNoLaborableService(DiaNoLaborableRepository diaNoLaborableRepository,
            PedidoDiaRepository pedidoDiaRepository, MenuDiaRepository menuDiaRepository,
            MenuPlatoRepository menuPlatoRepository) {
        this.diaNoLaborableRepository = diaNoLaborableRepository;
        this.pedidoDiaRepository = pedidoDiaRepository;
        this.menuDiaRepository = menuDiaRepository;
        this.menuPlatoRepository = menuPlatoRepository;
    }

    public List<DiaNoLaborable> listar() {
        return diaNoLaborableRepository.findAllByOrderByFecha();
    }

    // Recibe la lista completa de dias no laborables que quedo en el calendario:
    // borra los que se destildaron y agrega los nuevos.
    @Transactional
    public List<DiaNoLaborable> reemplazar(List<DiaNoLaborable> nuevos) {
        Set<LocalDate> fechasNuevas = new HashSet<>();
        for (DiaNoLaborable d : nuevos) {
            fechasNuevas.add(d.getFecha());
        }

        // los que ya no estan, se borran
        for (DiaNoLaborable existente : diaNoLaborableRepository.findAll()) {
            if (!fechasNuevas.contains(existente.getFecha())) {
                diaNoLaborableRepository.delete(existente);
            }
        }

        // los nuevos, se agregan (si no tienen pedidos)
        for (DiaNoLaborable d : nuevos) {
            if (diaNoLaborableRepository.existsByFecha(d.getFecha())) {
                continue; // ya estaba
            }
            if (pedidoDiaRepository.hayPedidosActivosEnFecha(d.getFecha())) {
                throw new ReglaNegocioException("No se puede marcar el " + d.getFecha()
                        + " como no laborable: ya hay pedidos para ese día.");
            }
            // si ese dia tenia un menu sin pedidos, se borra: en un dia no laborable no hay menu
            menuDiaRepository.findByFecha(d.getFecha()).ifPresent(menu -> {
                if (!pedidoDiaRepository.existsByIdMenuDia(menu.getId())) {
                    menuPlatoRepository.deleteByIdMenuDia(menu.getId());
                    menuDiaRepository.delete(menu);
                }
            });
            DiaNoLaborable nuevo = new DiaNoLaborable();
            nuevo.setFecha(d.getFecha());
            nuevo.setMotivo(d.getMotivo() == null || d.getMotivo().isBlank() ? "Feriado" : d.getMotivo());
            diaNoLaborableRepository.save(nuevo);
        }
        return listar();
    }
}
