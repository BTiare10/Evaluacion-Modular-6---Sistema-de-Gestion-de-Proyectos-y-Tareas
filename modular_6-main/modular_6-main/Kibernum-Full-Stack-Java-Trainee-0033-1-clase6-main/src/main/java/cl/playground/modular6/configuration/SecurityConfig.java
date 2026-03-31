package cl.playground.modular6.configuration;

import cl.playground.modular6.service.impl.UserDetailServiceImpl;
import cl.playground.modular6.utils.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.
            csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/proyectos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/tareas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/proyectos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/tareas/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            ).sessionManagement(session -> session
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
