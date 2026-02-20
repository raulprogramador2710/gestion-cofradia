package es.cofradia.gestioncofradia.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.cofradia.gestioncofradia.dto.CofradiaSeleccionDTO;
import es.cofradia.gestioncofradia.model.Usuario;
import es.cofradia.gestioncofradia.model.UsuarioCofradia;
import es.cofradia.gestioncofradia.repository.UsuarioCofradiaRepository;
import es.cofradia.gestioncofradia.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioCofradiaRepository usuarioCofradiaRepo;

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        Usuario usuario = usuarioRepo.findByUsuario(username).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<UsuarioCofradia> asociaciones = usuarioCofradiaRepo.findByUsuario(usuario);
        HttpSession session = request.getSession();

        // 1. CASO: No tiene ninguna asociación
        if (asociaciones == null || asociaciones.isEmpty()) {
            session.invalidate();
            response.sendRedirect("/login?error=no_cofradia");
            return;
        }

        // Buscamos si tiene algún rol de gestión entre sus asociaciones (cualquier rol distinto de HER)
        UsuarioCofradia asociacionGestion = asociaciones.stream()
                .filter(uc -> uc.getRol() != null && !"HER".equals(uc.getRol().getCodigo()))
                .findFirst()
                .orElse(null);

        // 2. CASO: Es un usuario de GESTIÓN (ADMIN, HM, TES, etc.)
        if (asociacionGestion != null) {
            session.setAttribute("cofradiaId", asociacionGestion.getCofradia() != null ? asociacionGestion.getCofradia().getId() : null);
            session.setAttribute("rolCodigo", asociacionGestion.getRol() != null ? asociacionGestion.getRol().getCodigo() : null);
            session.setAttribute("nombreCofradia", asociacionGestion.getCofradia() != null ? asociacionGestion.getCofradia().getNombre() : null);
            response.sendRedirect("/gestion/inicio");
            return;
        }

        // 3. CASO: Es un usuario con rol HERMANO (HER)
        List<UsuarioCofradia> asociacionesHermano = asociaciones.stream()
                .filter(uc -> uc.getRol() != null && "HER".equals(uc.getRol().getCodigo()))
                .collect(Collectors.toList());

        if (asociacionesHermano.size() == 1) {
            // Solo es hermano de una: Entra directo
            UsuarioCofradia uc = asociacionesHermano.get(0);
            session.setAttribute("cofradiaId", uc.getCofradia() != null ? uc.getCofradia().getId() : null);
            session.setAttribute("rolCodigo", "HER");
            session.setAttribute("nombreCofradia", uc.getCofradia() != null ? uc.getCofradia().getNombre() : null);
            response.sendRedirect("/portal/inicio");
            return;
        }

        // Es hermano de varias: Guardamos DTOs simples en sesión (evita entidades JPA en sesión)
        List<CofradiaSeleccionDTO> dtos = asociacionesHermano.stream()
                .map(uc -> CofradiaSeleccionDTO.builder()
                        .usuarioCofradiaId(uc.getId())
                        .cofradiaId(uc.getCofradia() != null ? uc.getCofradia().getId() : null)
                        .nombreCofradia(uc.getCofradia() != null ? uc.getCofradia().getNombre() : null)
                        .nombreRol(uc.getRol() != null ? uc.getRol().getDescripcion() : null)
                        .build())
                .collect(Collectors.toList());

        session.setAttribute("asociacionesPendientes", dtos);
        response.sendRedirect("/portal/seleccionar-cofradia");
    }
}