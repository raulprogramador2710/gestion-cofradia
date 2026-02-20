package es.cofradia.gestioncofradia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/public/**", "/css/**", "/js/**", "/images/**").permitAll() // Público
                .requestMatchers("/gestion/**").hasAnyAuthority("ADMIN", "HM", "TES", "SEC")// Gestión (Junta) -> permitir a quien tenga HM, TES o SEC (ajusta según quieras)
                .requestMatchers("/portal/**").hasAnyAuthority("HER", "HM", "TES", "SEC") // Portal -> permitir a hermanos y también a roles de la Junta si procede
                .anyRequest().authenticated() // Todo lo demás requiere login
            )
            .formLogin(form -> form
                .loginPage("/login") // Nuestra futura página de login personalizada
                .defaultSuccessUrl("/gestion/dashboard", true)
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