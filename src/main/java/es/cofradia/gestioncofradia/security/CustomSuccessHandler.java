package es.cofradia.gestioncofradia.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        // Si tiene cualquier rol de gestión (Junta), va al Dashboard
        if (roles.contains("ROLE_ADMIN") || roles.contains("ROLE_HM") || 
            roles.contains("ROLE_TES") || roles.contains("ROLE_SEC") || 
            roles.contains("ROLE_RRSS")) {
            
            response.sendRedirect("/gestion/dashboard");
            
        } else if (roles.contains("ROLE_HER")) { // Tu rol de "Hermano" es HER
            response.sendRedirect("/portal/inicio");
        } else {
            response.sendRedirect("/");
        }
    }
}