package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.cofradia.gestioncofradia.modulo.maestras.infraestructura.repository.TipoCuotaRepository;
import es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion.CuotaService;
import es.cofradia.gestioncofradia.modulo.tesoreria.dominio.Cuota;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestion/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;
    private final TipoCuotaRepository tipoCuotaRepo;
    private final UsuarioRepository usuarioRepo;

    private static final List<String> ROLES_GESTION = List.of("ADMIN", "HM", "TES", "SEC");

    // =========================================================
    // LISTADO DE CUOTAS
    // =========================================================
    @GetMapping
    public String listar(Model model, Principal principal) {
        UsuarioCofradia uc = resolverUsuarioCofradia(principal);
        model.addAttribute("cuotas", cuotaService.listarPorCofradia(uc.getCofradia().getId()));
        model.addAttribute("cofradia", uc.getCofradia());
        return "gestion/cuotas/lista_cuotas";
    }

    // =========================================================
    // FORMULARIO NUEVA CUOTA
    // =========================================================
    @GetMapping("/nueva")
    public String nuevaForm(Model model, Principal principal) {
        UsuarioCofradia uc = resolverUsuarioCofradia(principal);
        Cuota cuota = new Cuota();
        cuota.setCofradia(uc.getCofradia());
        model.addAttribute("cuota", cuota);
        model.addAttribute("tiposCuota", tipoCuotaRepo.findAll());
        return "gestion/cuotas/form_cuota";
    }

    // =========================================================
    // GUARDAR CUOTA (crea la cuota y pone a todos en DEUDOR)
    // =========================================================
    @PostMapping("/guardar")
    @Transactional
    public String guardar(@ModelAttribute Cuota cuota, Principal principal,
                          RedirectAttributes redirectAttributes) {
        UsuarioCofradia uc = resolverUsuarioCofradia(principal);
        cuota.setCofradia(uc.getCofradia());
        cuota.setTipo(tipoCuotaRepo.findById(cuota.getTipo().getId()).orElseThrow());
        cuotaService.guardarYActivar(cuota);
        redirectAttributes.addFlashAttribute("mensaje", "Cuota creada. Los hermanos han sido marcados como deudores.");
        return "redirect:/gestion/cuotas";
    }

    // =========================================================
    // MÉTODO PRIVADO
    // =========================================================
    private UsuarioCofradia resolverUsuarioCofradia(Principal principal) {
        Usuario usuario = usuarioRepo.findByUsuario(principal.getName()).orElseThrow();
        return usuario.getUsuarioCofradias().stream()
                .filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo()))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("Sin permisos de gestión"));
    }
}