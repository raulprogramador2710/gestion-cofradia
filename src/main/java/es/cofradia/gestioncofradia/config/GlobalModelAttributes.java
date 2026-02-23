package es.cofradia.gestioncofradia.config;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.cofradia.gestioncofradia.modulo.usuarios.dominio.UsuarioCofradia;
import es.cofradia.gestioncofradia.modulo.usuarios.infraestructura.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UsuarioRepository usuarioRepo;

    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal) {
        if (principal != null) {
            usuarioRepo.findByUsuario(principal.getName()).ifPresent(u -> {
                if (!u.getUsuarioCofradias().isEmpty()) {
                    UsuarioCofradia uc = u.getUsuarioCofradias().iterator().next();
                    model.addAttribute("cofradia", uc.getCofradia());
                }
                model.addAttribute("usuarioLogueado", u.getUsuario());
            });
        }
    }
}