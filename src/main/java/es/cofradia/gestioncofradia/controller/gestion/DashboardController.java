package es.cofradia.gestioncofradia.controller.gestion;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.cofradia.gestioncofradia.model.Usuario;
import es.cofradia.gestioncofradia.model.UsuarioCofradia;
import es.cofradia.gestioncofradia.repository.UsuarioRepository;
import es.cofradia.gestioncofradia.service.HermanoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final HermanoService hermanoService;
    private final UsuarioRepository usuarioRepo;

    @GetMapping("/gestion/dashboard")
    public String dashboard(Model model, Principal principal) {
        
        if (principal != null) {
            usuarioRepo.findByUsuario(principal.getName()).ifPresent((Usuario u) -> {
                // Como el usuario puede tener varias cofradías, cogemos la primera de la lista
                // para mostrar su nombre/colores en el dashboard por ahora.
                if (!u.getUsuarioCofradias().isEmpty()) {
                    // Obtenemos la primera asociación
                    UsuarioCofradia primeraAsociacion = u.getUsuarioCofradias().iterator().next();
                    model.addAttribute("cofradia", primeraAsociacion.getCofradia());
                    model.addAttribute("rolUsuario", primeraAsociacion.getRol().getDescripcion());
                }
                model.addAttribute("usuarioLogueado", u.getUsuario());
            });
        }

        // Obtenemos el total de hermanos (esto habría que filtrarlo por cofradía más adelante)
        long totalHermanos = hermanoService.listarTodos().size();
        
        model.addAttribute("totalHermanos", totalHermanos);
        model.addAttribute("totalNotificaciones", 0);
        model.addAttribute("tareasProximas", List.of());
        
        return "gestion/dashboard";
    }
}