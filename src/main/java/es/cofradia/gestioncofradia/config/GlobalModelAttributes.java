package es.cofradia.gestioncofradia.config;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.cofradia.gestioncofradia.model.UsuarioCofradia;
import es.cofradia.gestioncofradia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UsuarioRepository usuarioRepo;

    @ModelAttribute("cofradia")
    public Object addCofradiaToModel(Authentication authentication) {
        // Si no hay nadie logueado (página pública), no añadimos nada
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return null;
        }

        String username = authentication.getName();
        
        return usuarioRepo.findByUsuario(username)
                .map(usuario -> {
                    // Buscamos la primera cofradía asociada al usuario
                    return usuario.getUsuarioCofradias().stream()
                            .findFirst()
                            .map(UsuarioCofradia::getCofradia)
                            .orElse(null);
                })
                .orElse(null);
    }
}