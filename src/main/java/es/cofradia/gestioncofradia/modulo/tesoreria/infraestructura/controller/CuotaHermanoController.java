package es.cofradia.gestioncofradia.modulo.tesoreria.infraestructura.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @PostMapping("/marcar-pagada/{id}")
    public ResponseEntity<Void> marcarCuotaPagada(Principal principal, @PathVariable Long id) {
    	
        try {
            cuotaHermanoService.marcarComoPagada(id);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =========================================================
    // MÉTODO PRIVADO
    // =========================================================
    private UsuarioCofradia resolverUsuarioCofradia(Principal principal) {
    	
        Usuario usuario = usuarioRepo.findByUsuario(principal.getName()).orElseThrow();
        
        return usuario.getUsuarioCofradias().stream().filter(a -> a.getRol() != null && ROLES_GESTION.contains(a.getRol().getCodigo())).findFirst().
        		orElseThrow(() -> new AccessDeniedException("Sin permisos de gestión"));
    }
    
}