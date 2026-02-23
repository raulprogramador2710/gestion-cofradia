package es.cofradia.gestioncofradia.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/public/**", "/css/**", "/js/**", "/images/**").permitAll() // Público
                .requestMatchers("/gestion/**").hasAnyAuthority("ADMIN", "HM", "TES", "SEC", "RRSS")// Gestión (Junta) -> permitir a quien tenga HM, TES o SEC (ajusta según quieras)
                .requestMatchers("/portal/**").hasAnyAuthority("HER") // Portal -> permitir a hermanos y también a roles de la Junta si procede
                .anyRequest().authenticated() // Todo lo demás requiere login
            )
            .formLogin(form -> form
                .loginPage("/login") // Nuestra futura página de login personalizada
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/index")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Esto es VITAL: nunca guardaremos contraseñas en texto plano.
        // BCrypt es el estándar de la industria.
        return new BCryptPasswordEncoder();
    }
}