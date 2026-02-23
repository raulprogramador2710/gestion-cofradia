package es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.controller;

import java.security.Principal;
import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaComunicacion;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.FormaPago;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionHermano;
import es.cofradia.gestioncofradia.modulo.maestras.dominio.SituacionPagoHermano;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.FormaComunicacionRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.FormaPagoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionHermanoRepository;
import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.SituacionPagoHermanoRepository;
import es.cofradia.gestioncofradia.modulo.salidas.dominio.PapeletaSitio;
import es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository.PapeletaSitioRepository;
import es.cofradia.gestioncofradia.modulo.salidas.infraestructura.repository.TipoParticipacionRepository;
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
    private final SituacionHermanoRepository situacionRepo;
    private final SituacionPagoHermanoRepository situacionPagoRepo;
    private final FormaPagoRepository pagoRepo;
    private final FormaComunicacionRepository comunicacionRepo;
    private final PapeletaSitioRepository papeletaRepo;
    private final TipoParticipacionRepository tipoParticipacionRepo;
    private final HermanoService hermanoService;

    private static final List<String> ROLES_GESTION = List.of("ADMIN", "HM", "TES", "SEC", "RRSS");

    // =========================================================
    // LISTADO
    // =========================================================

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

        UsuarioCofradia uc = resolverUsuarioCofradia(principal);
        Long cofradiaId = uc.getCofradia().getId();

        filtroNombre = (filtroNombre == null || "null".equals(filtroNombre)) ? "" : filtroNombre.trim();
        filtroDni    = (filtroDni    == null || "null".equals(filtroDni))    ? "" : filtroDni.trim();

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Page<Hermano> hermanosPage = hermanoRepo.buscarPorCofradiaConFiltros(
                cofradiaId,
                filtroNombre.isEmpty() ? null : filtroNombre,
                filtroDni.isEmpty()    ? null : filtroDni,
                PageRequest.of(page, size, sort));

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
        model.addAttribute("anioActual", Year.now().getValue());

        return "gestion/hermanos/lista_hermanos";
    }

    // =========================================================
    // NUEVO HERMANO
    // =========================================================

    @GetMapping("/nuevo")
    public String nuevoForm(Model model, Principal principal) {
        UsuarioCofradia uc = resolverUsuarioCofradia(principal);

        Hermano hermano = new Hermano();
        hermano.setCofradia(uc.getCofradia());
        hermano.setNumHermano(calcularSiguienteNum(uc.getCofradia().getId()));
        hermano.setSituacion(new SituacionHermano());
        hermano.setSituacionPago(new SituacionPagoHermano());
        hermano.setFormaPago(new FormaPago());
        hermano.setFormaComunicacion(new FormaComunicacion());

        model.addAttribute("hermano", hermano);
        model.addAttribute("esNuevo", true);
        // No hay papeleta previa en un hermano nuevo
        model.addAttribute("tipoParticipacionActualId", null);
        cargarMaestras(model);

        return "gestion/hermanos/form_hermano";
    }

    // =========================================================
    // EDITAR HERMANO
    // =========================================================

    @GetMapping("/editar/{id}")
    @Transactional(readOnly = true)
    public String editarForm(@PathVariable Long id, Model model) {
        Hermano hermano = hermanoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        // Inicializar sub-objetos para evitar problemas de binding en selects
        if (hermano.getSituacion() == null)         hermano.setSituacion(new SituacionHermano());
        if (hermano.getSituacionPago() == null)     hermano.setSituacionPago(new SituacionPagoHermano());
        if (hermano.getFormaPago() == null)         hermano.setFormaPago(new FormaPago());
        if (hermano.getFormaComunicacion() == null) hermano.setFormaComunicacion(new FormaComunicacion());

        // Recuperar la papeleta del año actual para preseleccionar el tipo en el formulario
        int anioActual = Year.now().getValue();
        Long tipoParticipacionActualId = papeletaRepo
                .findByHermanoIdAndAnio(id, anioActual)
                .map(p -> p.getTipoParticipacion().getId())
                .orElse(null);

        model.addAttribute("hermano", hermano);
        model.addAttribute("esNuevo", false);
        model.addAttribute("tipoParticipacionActualId", tipoParticipacionActualId);
        model.addAttribute("anioActual", anioActual);
        cargarMaestras(model);

        return "gestion/hermanos/form_hermano";
    }

    // =========================================================
    // GUARDAR (NUEVO Y EDICIÓN)
    // =========================================================

    @PostMapping("/guardar")
    @Transactional
    public String guardar(@ModelAttribute Hermano hermano, @RequestParam(required = false) Long tipoParticipacionId, Principal principal) {

        // --- 1. Proteger campos según si es nuevo o edición ---
        if (hermano.getId() == null) {
            UsuarioCofradia uc = resolverUsuarioCofradia(principal);
            hermano.setCofradia(uc.getCofradia());
            hermano.setNumHermano(calcularSiguienteNum(uc.getCofradia().getId()));
        } else {
            Hermano existente = hermanoRepo.findById(hermano.getId())
                    .orElseThrow(() -> new IllegalArgumentException("ID inválido"));
            hermano.setNumHermano(existente.getNumHermano());
            hermano.setCofradia(existente.getCofradia());
        }

        // --- 2. Resolver entidades maestras ---
        hermano.setSituacion(
    	    (hermano.getSituacion() != null && hermano.getSituacion().getId() != null)
    	        ? situacionRepo.findById(hermano.getSituacion().getId()).orElse(null) : null
    	);

    	hermano.setSituacionPago(
    	    (hermano.getSituacionPago() != null && hermano.getSituacionPago().getId() != null)
    	        ? situacionPagoRepo.findById(hermano.getSituacionPago().getId()).orElse(null) : null
    	);
        hermano.setFormaPago(
            (hermano.getFormaPago() != null && hermano.getFormaPago().getId() != null)
                ? pagoRepo.findById(hermano.getFormaPago().getId()).orElse(null) : null
        );
        hermano.setFormaComunicacion(
            (hermano.getFormaComunicacion() != null && hermano.getFormaComunicacion().getId() != null)
                ? comunicacionRepo.findById(hermano.getFormaComunicacion().getId()).orElse(null) : null
        );

        if (tipoParticipacionId != null) {
        	hermano.setTipoParticipacion(tipoParticipacionRepo.findById(tipoParticipacionId).orElse(null));
        }else {
            hermano.setTipoParticipacion(null); // permite dejarlo vacío
        }

        // --- 3. Guardar hermano ---
        hermanoRepo.save(hermano);

        return "redirect:/gestion/hermanos?success";
    }

    // =========================================================
    // DETALLE
    // =========================================================

    @GetMapping("/detalle/{id}")
    @Transactional(readOnly = true)
    public String verDetalle(@PathVariable Long id, Model model) {
        Hermano hermano = hermanoRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID inválido"));

        // Historial completo de papeletas ordenado por año descendente
        List<PapeletaSitio> historialPapeletas = papeletaRepo.findByHermanoIdOrderByAnioDesc(id);

        model.addAttribute("hermano", hermano);
        model.addAttribute("historialPapeletas", historialPapeletas);
        model.addAttribute("anioActual", Year.now().getValue());

        return "gestion/hermanos/detalle_hermano";
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @DeleteMapping("/{id}/eliminar")
    public String eliminarHermano(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        hermanoService.eliminarPorId(id);
        redirectAttributes.addFlashAttribute("mensaje", "Hermano eliminado.");
        return "redirect:/gestion/hermanos";
    }

    // =========================================================
    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================

    private UsuarioCofradia resolverUsuarioCofradia(Principal principal) {
        Usuario usuario = usuarioRepo.findByUsuario(principal.getName()).orElseThrow();
        return usuario.getUsuarioCofradias().stream()
                .filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No tiene permisos de gestión en ninguna cofradía"));
    }

    private void cargarMaestras(Model model) {
        model.addAttribute("situaciones", situacionRepo.findAll());
        model.addAttribute("situacionesPago", situacionPagoRepo.findAll());
        model.addAttribute("formasPago", pagoRepo.findAll());
        model.addAttribute("formasComunicacion", comunicacionRepo.findAll());
        model.addAttribute("tiposParticipacion", tipoParticipacionRepo.findAll());
    }

    private int calcularSiguienteNum(Long cofradiaId) {
        Integer max = hermanoRepo.findMaxNumHermanoByCofradiaId(cofradiaId);
        return (max == null ? 1 : max + 1);
    }
}