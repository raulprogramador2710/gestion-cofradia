package es.cofradia.gestioncofradia.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cofradia.gestioncofradia.model.Usuario;
import es.cofradia.gestioncofradia.model.UsuarioCofradia;
import es.cofradia.gestioncofradia.repository.UsuarioCofradiaRepository;
import es.cofradia.gestioncofradia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioCofradiaRepository usuarioCofradiaRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepo.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<UsuarioCofradia> asociaciones = usuarioCofradiaRepo.findByUsuario(usuario);

        // <-- Aquí NO añadimos "ROLE_", devolvemos el código tal cual (HM, TES, HER...)
        List<SimpleGrantedAuthority> authorities = asociaciones.stream()
                .map(uc -> new SimpleGrantedAuthority(uc.getRol().getCodigo()))
                .distinct()
                .collect(Collectors.toList());

        // Opcional: si quieres un rol por defecto (mejor evitarlo en prod)
        if (authorities.isEmpty()) {
            authorities = List.of(new SimpleGrantedAuthority("HER"));
        }

        // DEBUG rápido: ver roles cargados en consola
        System.out.println("UserDetailsService: usuario=" + username + " roles=" + authorities);

        return new User(
                usuario.getUsuario(),
                usuario.getClave(),
                authorities
        );
    }
}