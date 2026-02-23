package es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.cofradia.gestioncofradia.modulo.hermanos.aplicacion.HermanoService;
import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.EstadoHermano;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaComunicacion;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaPago;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.EstadoHermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.FormaComunicacionRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.FormaPagoRepository;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestion/hermanos")
@RequiredArgsConstructor
public class HermanoController {
	
	private final HermanoRepository hermanoRepo;
	private final UsuarioRepository usuarioRepo;
	private final EstadoHermanoRepository estadoRepo;
	private final FormaPagoRepository pagoRepo;
	private final FormaComunicacionRepository comunicacionRepo;
	
	private final HermanoService hermanoService;
	
	private static final List<String> ROLES_GESTION = List.of("ADMIN", "HM", "TES", "SEC", "RRSS");
	
	@GetMapping
	@Transactional(readOnly = true)
	public String listar(
	        Model model,
	        Principal principal,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "numHermano") String sortField,
	        @RequestParam(defaultValue = "asc") String sortDir,
	        @RequestParam(required = false) String filtroNombre,
	        @RequestParam(required = false) String filtroDni) {
	
	    String username = principal.getName();
	
	    Usuario usuario = usuarioRepo.findByUsuario(username)
	            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + username));
	
	    UsuarioCofradia uc = usuario.getUsuarioCofradias().stream()
	            .filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo()))
	            .findFirst()
	            .orElseThrow(() -> new AccessDeniedException("No tiene permisos de gestión en ninguna cofradía"));
	
	    Long cofradiaId = uc.getCofradia().getId();
	
	    filtroNombre = (filtroNombre == null || "null".equals(filtroNombre)) ? "" : filtroNombre.trim();
	    filtroDni = (filtroDni == null || "null".equals(filtroDni)) ? "" : filtroDni.trim();
	
	    Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
	    Pageable pageable = PageRequest.of(page, size, sort);
	
	    Page<Hermano> hermanosPage = hermanoRepo.buscarPorCofradiaConFiltros(cofradiaId,
	            filtroNombre.isEmpty() ? null : filtroNombre,
	            filtroDni.isEmpty() ? null : filtroDni,
	            pageable);
	
	    model.addAttribute("hermanosPage", hermanosPage);
	    model.addAttribute("hermanos", hermanosPage.getContent());
	    model.addAttribute("cofradia", uc.getCofradia());
	
	    model.addAttribute("page", page);
	    model.addAttribute("size", size);
	    model.addAttribute("sortField", sortField);
	    model.addAttribute("sortDir", sortDir);
	    model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
	
	    model.addAttribute("filtroNombre", filtroNombre);
	    model.addAttribute("filtroDni", filtroDni);
        
        return "gestion/hermanos/lista_hermanos";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model, Principal principal) {
        Usuario usuario = usuarioRepo.findByUsuario(principal.getName()).orElseThrow();
        UsuarioCofradia uc = usuario.getUsuarioCofradias().stream()
                .filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No tiene permisos de gestión en ninguna cofradía"));

        Hermano hermano = new Hermano();
        hermano.setCofradia(uc.getCofradia()); // preasignamos cofradía

        // calcular siguiente número
        int siguiente = calcularSiguienteNum(uc.getCofradia().getId());
        hermano.setNumHermano(siguiente);

        // inicializar objetos anidados para facilitar binding (th:field="*{estado.id}" etc.)
        hermano.setEstado(new EstadoHermano());
        hermano.setFormaPago(new FormaPago());
        hermano.setFormaComunicacion(new FormaComunicacion());

        model.addAttribute("hermano", hermano);
        cargarMaestras(model);
        model.addAttribute("esNuevo", true);
        return "gestion/hermanos/form_hermano";
    }

    @PostMapping("/guardar")
    @Transactional
    public String guardar(@ModelAttribute Hermano hermano, Principal principal) {
        // Resolver y proteger campos según si es nuevo o edición
        if (hermano.getId() == null) {
            // nuevo: set cofradia y numHermano correcto basado en usuario
            Usuario usuario = usuarioRepo.findByUsuario(principal.getName()).orElseThrow();
            UsuarioCofradia uc = usuario.getUsuarioCofradias().stream()
                    .filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo()))
                    .findFirst()
                    .orElseThrow(() -> new AccessDeniedException("No tiene permisos de gestión en ninguna cofradía"));

            hermano.setCofradia(uc.getCofradia());
            int siguiente = calcularSiguienteNum(uc.getCofradia().getId());
            hermano.setNumHermano(siguiente);
        } else {
            // edición: proteger numHermano y cofradía (ignorar cualquier cambio enviado por el formulario)
            Hermano existente = hermanoRepo.findById(hermano.getId())
                    .orElseThrow(() -> new IllegalArgumentException("ID inválido"));
            hermano.setNumHermano(existente.getNumHermano());
            hermano.setCofradia(existente.getCofradia());
        }

        // Resolver entidades maestras a partir de los ids recibidos por el form
        if (hermano.getEstado() != null && hermano.getEstado().getId() != null) {
            hermano.setEstado(estadoRepo.findById(hermano.getEstado().getId()).orElse(null));
        } else {
            hermano.setEstado(null);
        }

        if (hermano.getFormaPago() != null && hermano.getFormaPago().getId() != null) {
            hermano.setFormaPago(pagoRepo.findById(hermano.getFormaPago().getId()).orElse(null));
        } else {
            hermano.setFormaPago(null);
        }

        if (hermano.getFormaComunicacion() != null && hermano.getFormaComunicacion().getId() != null) {
            hermano.setFormaComunicacion(comunicacionRepo.findById(hermano.getFormaComunicacion().getId()).orElse(null));
        } else {
            hermano.setFormaComunicacion(null);
        }

        hermanoRepo.save(hermano);
        return "redirect:/gestion/hermanos?success";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Hermano hermano = hermanoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));
        model.addAttribute("hermano", hermano);
        return "gestion/hermanos/detalle_hermano";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model) {
        Hermano hermano = hermanoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        // Inicializar sub-objetos para evitar problemas de binding en selects (estado.id, ...)
        if (hermano.getEstado() == null) hermano.setEstado(new EstadoHermano());
        if (hermano.getFormaPago() == null) hermano.setFormaPago(new FormaPago());
        if (hermano.getFormaComunicacion() == null) hermano.setFormaComunicacion(new FormaComunicacion());

        model.addAttribute("hermano", hermano);
        cargarMaestras(model);
        model.addAttribute("esNuevo", false);
        return "gestion/hermanos/form_hermano";
    }
    
    @DeleteMapping("/{id}/eliminar")
    public String eliminarHermano(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    	hermanoService.eliminarPorId(id);
        redirectAttributes.addFlashAttribute("mensaje", "Hermano eliminado.");
        return "redirect:/gestion/hermanos";
    }

    private void cargarMaestras(Model model) {
        model.addAttribute("estados", estadoRepo.findAll());
        model.addAttribute("formasPago", pagoRepo.findAll());
        model.addAttribute("formasComunicacion", comunicacionRepo.findAll());
    }

    private int calcularSiguienteNum(Long cofradiaId) {
        Integer max = hermanoRepo.findMaxNumHermanoByCofradiaId(cofradiaId);
        return (max == null ? 1 : max + 1);
    }
}