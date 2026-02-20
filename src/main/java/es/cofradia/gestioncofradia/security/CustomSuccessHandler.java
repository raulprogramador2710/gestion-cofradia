package es.cofradia.gestioncofradia.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

        // 1. CASO: No tiene ninguna asociación (No debería pasar si el login fue exitoso, pero por seguridad)
        if (asociaciones.isEmpty()) {
            session.invalidate();
            response.sendRedirect("/login?error=no_cofradia");
            return;
        }

        // Buscamos si tiene algún rol de gestión entre sus asociaciones
        UsuarioCofradia asociacionGestion = asociaciones.stream().filter(uc -> !uc.getRol().getCodigo().equals("HER")).findFirst().orElse(null);

        // 2. CASO: Es un usuario de GESTIÓN (ADMIN, HM, TES, etc.)
        if (asociacionGestion != null) {
            // Guardamos en sesión la cofradía activa para filtrar los datos en el panel
        	session.setAttribute("cofradiaId", asociacionGestion.getCofradia().getId());
            session.setAttribute("rolCodigo", asociacionGestion.getRol().getCodigo());
            session.setAttribute("nombreCofradia", asociacionGestion.getCofradia().getNombre());
            response.sendRedirect("/gestion/dashboard");
            return;
        }

        // 3. CASO: Es un usuario con rol HERMANO (HER)
        // Filtramos solo las asociaciones de tipo hermano (por si acaso)
        List<UsuarioCofradia> asociacionesHermano = asociaciones.stream().filter(uc -> uc.getRol().getCodigo().equals("HER")).toList();

        if (asociacionesHermano.size() == 1) {
            // Solo es hermano de una: Entra directo
            UsuarioCofradia uc = asociacionesHermano.get(0);
            session.setAttribute("cofradiaId", uc.getCofradia().getId());
            session.setAttribute("rolCodigo", "HER");
            session.setAttribute("nombreCofradia", uc.getCofradia().getNombre());
            response.sendRedirect("/portal/inicio");
        } else {
            // Es hermano de varias: Al selector
            session.setAttribute("asociacionesPendientes", asociacionesHermano);
            response.sendRedirect("/portal/seleccionar-cofradia");
        }
    }
}