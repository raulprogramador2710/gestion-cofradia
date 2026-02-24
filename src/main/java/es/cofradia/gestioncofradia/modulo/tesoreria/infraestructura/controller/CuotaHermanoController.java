package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.cofradia.gestioncofradia.modulo.tesoreria.aplicacion.CuotaHermanoService;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.Usuario;
import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/gestion/cuotas")
@RequiredArgsConstructor
public class CuotaHermanoController {

    private final CuotaHermanoService cuotaHermanoService;
    private final UsuarioRepository usuarioRepo;

    private static final List<String> ROLES_GESTION = List.of("ADMIN", "HM", "TES", "SEC");

    // =========================================================
    // REGISTRAR PAGO MANUAL desde la ficha del hermano
    // El secretario/tesorero marca que el hermano ha pagado
    // =========================================================
    @PostMapping("/hermanos/{hermanoId}/pagar")
    public String registrarPago(@PathVariable Long hermanoId,
                                @RequestParam String redirectUrl,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        UsuarioCofradia uc = resolverUsuarioCofradia(principal);
        try {
            cuotaHermanoService.registrarPagoAnual(hermanoId, uc.getCofradia().getId());
            redirectAttributes.addFlashAttribute("mensaje", "Pago registrado correctamente.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + redirectUrl;
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