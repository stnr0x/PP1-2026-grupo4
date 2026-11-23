package ar.edu.pp1.pedidos.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ar.edu.pp1.pedidos.dto.PlatoRequest;
import ar.edu.pp1.pedidos.entidad.Plato;
import ar.edu.pp1.pedidos.excepcion.ConflictoException;
import ar.edu.pp1.pedidos.excepcion.NoEncontradoException;
import ar.edu.pp1.pedidos.excepcion.ReglaNegocioException;
import ar.edu.pp1.pedidos.repositorio.MenuPlatoRepository;
import ar.edu.pp1.pedidos.repositorio.PedidoDiaRepository;
import ar.edu.pp1.pedidos.repositorio.PlatoRepository;

// Catalogo de platos: el administrador da de alta platos nuevos (con su imagen)
// y puede borrar los que todavia no se usaron.
@Service
public class PlatoService {

    // las categorias que usa el catalogo (columna plato.categoria)
    public static final List<String> CATEGORIAS = List.of("clásico", "vegetariano", "liviano", "pastas");

    private final PlatoRepository platoRepository;
    private final MenuPlatoRepository menuPlatoRepository;
    private final PedidoDiaRepository pedidoDiaRepository;
    private final AlmacenImagenes almacenImagenes;

    public PlatoService(PlatoRepository platoRepository, MenuPlatoRepository menuPlatoRepository,
            PedidoDiaRepository pedidoDiaRepository, AlmacenImagenes almacenImagenes) {
        this.platoRepository = platoRepository;
        this.menuPlatoRepository = menuPlatoRepository;
        this.pedidoDiaRepository = pedidoDiaRepository;
        this.almacenImagenes = almacenImagenes;
    }

    public List<Plato> listar() {
        return platoRepository.findAll();
    }

    public Plato buscar(Long id) {
        return platoRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("No existe el plato " + id));
    }

    // Alta de un plato. La imagen es opcional: sin imagen, la pantalla muestra sin-imagen.svg.
    @Transactional
    public Plato crear(PlatoRequest datos, MultipartFile imagen) {
        String nombre = datos.nombre().trim();
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya hay un plato que se llama \"" + nombre + "\".");
        }
        if (!CATEGORIAS.contains(datos.categoria())) {
            throw new ReglaNegocioException("La categoría tiene que ser una de: " + String.join(", ", CATEGORIAS) + ".");
        }

        Plato plato = new Plato();
        plato.setNombre(nombre);
        String descripcion = datos.descripcion() == null ? "" : datos.descripcion().trim();
        plato.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        plato.setCategoria(datos.categoria());
        if (imagen != null && !imagen.isEmpty()) {
            plato.setImagen(almacenImagenes.guardar(imagen)); // se guarda la URL, no la imagen
        }
        return platoRepository.save(plato);
    }

    // Borrar: solo si el plato NUNCA se uso en un menu ni en un pedido.
    // Si ya se uso, borrarlo romperia las FK de menu_plato y pedido_dia, el historial
    // de Mis pedidos y el consolidado. En la practica, se pueden borrar los platos
    // recien creados que todavia no se pusieron en ningun menu.
    @Transactional
    public void borrar(Long id) {
        Plato plato = buscar(id);
        if (menuPlatoRepository.existsByIdPlato(id) || pedidoDiaRepository.existsByIdPlato(id)) {
            throw new ReglaNegocioException("\"" + plato.getNombre()
                    + "\" ya se usó en un menú: no se puede borrar.");
        }
        platoRepository.delete(plato);
        almacenImagenes.borrarSiEsNuestra(plato.getImagen());
    }
}
