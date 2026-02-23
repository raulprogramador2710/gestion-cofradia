package es.cofradia.gestioncofradia.modulo.salidas.infraestructura.controller;

import java.security.Principal;
import java.time.Year;
import java.util.List;

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

import es.cofradia.gestioncofradia.modulo.hermanos.dominio.Hermano;
import es.cofradia.gestioncofradia.modulo.hermanos.infraestructura.repository.HermanoRepository;
import es.cofradia.gestioncofradia.modulo.salidas.aplicacion.SalidaService;
import es.cofradia.gestioncofradia.modulo.salidas.dominio.PapeletaSitio;
import es.cofradia.gestioncofradia.modulo.salidas.dominio.TipoParticipacion;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestion/salidas")
@RequiredArgsConstructor
public class SalidaController {

    private final SalidaService salidaService;
    private final HermanoRepository hermanoRepo;
    private final UsuarioRepository usuarioRepo;

    private static final List<String> ROLES_GESTION = List.of("ADMIN", "HM", "TES", "SEC", "RRSS");

    // --- Papeletas ---

    @GetMapping
    @Transactional(readOnly = true)
    public String listarPapeletas(
            Model model,
            Principal principal,
            @RequestParam(defaultValue = "#{T(java.time.Year).now().value}") Integer anio,
            @RequestParam(required = false) Long tipoId) {

        UsuarioCofradia uc = resolverUsuarioCofradia(principal);
        Long cofradiaId = uc.getCofradia().getId();

        List<PapeletaSitio> papeletas = (tipoId != null)
                ? salidaService.listarPorCofradiaAnioYTipo(cofradiaId, anio, tipoId)
                : salidaService.listarPorCofradiaYAnio(cofradiaId, anio);

        model.addAttribute("papeletas", papeletas);
        model.addAttribute("tipos", salidaService.listarTiposParticipacion());
        model.addAttribute("anio", anio);
        model.addAttribute("tipoIdSeleccionado", tipoId);
        model.addAttribute("cofradia", uc.getCofradia());
        return "gestion/salidas/lista_papeletas";
    }

    @GetMapping("/nueva")
    public String nuevaPapeletaForm(Model model, Principal principal) {
        UsuarioCofradia uc = resolverUsuarioCofradia(principal);

        model.addAttribute("papeleta", new PapeletaSitio());
        model.addAttribute("hermanos", hermanoRepo.findByCofradiaIdOrderByNumHermanoAsc(uc.getCofradia().getId()));
        model.addAttribute("tipos", salidaService.listarTiposParticipacion());
        model.addAttribute("anioActual", Year.now().getValue());
        return "gestion/salidas/form_papeleta";
    }

    @PostMapping("/guardar")
    @Transactional
    public String guardarPapeleta(@ModelAttribute PapeletaSitio papeleta,
                                   RedirectAttributes redirectAttributes) {
        // Resolver hermano
        if (papeleta.getHermano() != null && papeleta.getHermano().getId() != null) {
            Hermano hermano = hermanoRepo.findById(papeleta.getHermano().getId()).orElseThrow();
            papeleta.setHermano(hermano);
        }
        salidaService.emitirPapeleta(papeleta);
        redirectAttributes.addFlashAttribute("mensaje", "Papeleta guardada correctamente.");
        return "redirect:/gestion/salidas";
    }

    @PostMapping("/{id}/registrarPago")
    public String registrarPago(@PathVariable Long id,
                                 @RequestParam Double importe,
                                 RedirectAttributes redirectAttributes) {
        salidaService.registrarPago(id, importe);
        redirectAttributes.addFlashAttribute("mensaje", "Pago registrado correctamente.");
        return "redirect:/gestion/salidas";
    }

    @DeleteMapping("/{id}/eliminar")
    public String eliminarPapeleta(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        salidaService.eliminarPapeleta(id);
        redirectAttributes.addFlashAttribute("mensaje", "Papeleta eliminada.");
        return "redirect:/gestion/salidas";
    }

    // --- Tipos de Participacion ---

    @GetMapping("/tipos")
    public String listarTipos(Model model) {
        model.addAttribute("tipos", salidaService.listarTiposParticipacion());
        model.addAttribute("tipo", new TipoParticipacion());
        return "gestion/salidas/lista_tipos";
    }

    @PostMapping("/tipos/guardar")
    public String guardarTipo(@ModelAttribute TipoParticipacion tipo, RedirectAttributes redirectAttributes) {
        salidaService.guardarTipoParticipacion(tipo);
        redirectAttributes.addFlashAttribute("mensaje", "Tipo guardado correctamente.");
        return "redirect:/gestion/salidas/tipos";
    }

    @DeleteMapping("/tipos/{id}/eliminar")
    public String eliminarTipo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        salidaService.eliminarTipoParticipacion(id);
        redirectAttributes.addFlashAttribute("mensaje", "Tipo eliminado.");
        return "redirect:/gestion/salidas/tipos";
    }

    // --- Privado ---

    private UsuarioCofradia resolverUsuarioCofradia(Principal principal) {
        Usuario usuario = usuarioRepo.findByUsuario(principal.getName()).orElseThrow();
        return usuario.getUsuarioCofradias().stream()
                .filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Sin permisos de gestión"));
    }
}