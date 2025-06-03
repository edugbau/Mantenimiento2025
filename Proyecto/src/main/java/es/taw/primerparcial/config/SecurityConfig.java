package es.taw.primerparcial.config;

import es.taw.primerparcial.dao.UsuarioRepository;
import es.taw.primerparcial.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // El login es con 'usuarioname' y la contraseña es 'usuarioid' (como string)
            Usuario usuario = usuarioRepository.findByUsuarioName(username);
            if (usuario != null) {
                // Spring Security espera una contraseña encriptada.
                // Como la contraseña es el ID del usuario, la encriptaremos aquí.
                // ¡¡¡IMPORTANTE!!! Esto NO es seguro para producción. Solo para este ejercicio.
                // En un caso real, la contraseña ya debería estar encriptada en la BD.
                String encodedPassword = passwordEncoder().encode(String.valueOf(usuario.getUsuarioId()));
                return org.springframework.security.core.userdetails.User.builder()
                        .username(usuario.getUsuarioName())
                        .password(encodedPassword)
                        .authorities("USER") // Roles/autoridades
                        .build();
            }
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // ¡¡¡IMPORTANTE!!! BCrypt es una buena elección, pero almacenar el ID como contraseña no lo es.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll() // Permitir acceso a la página de login y recursos estáticos
                        .anyRequest().authenticated() // Todas las demás peticiones requieren autenticación
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login") // Especifica la página de login personalizada
                        .defaultSuccessUrl("/seleccion", true) // Redirige a /seleccion después del login exitoso
                        .failureUrl("/login?error=true") // Redirige a /login?error=true en caso de fallo
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true") // Redirige a /login?logout=true después del logout
                        .permitAll()
                );
        return http.build();
    }
} 