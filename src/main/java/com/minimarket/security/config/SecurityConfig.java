package com.minimarket.security.config;

import com.minimarket.security.filter.JwtAuthenticationFilter;
import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/api/usuarios/**").hasAuthority("ROLE_ADMINISTRADOR")

                        .requestMatchers("/api/productos/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_EMPLEADO", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/inventario/**").hasAnyAuthority("ROLE_EMPLEADO", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/ventas/**").hasAnyAuthority("ROLE_EMPLEADO", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/categorias/**").hasAnyAuthority("ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/carrito/**").hasAnyAuthority("ROLE_EMPLEADO", "ROLE_CLIENTE", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/detalle-ventas/**").hasAnyAuthority("ROLE_EMPLEADO", "ROLE_ADMINISTRADOR")
                        .requestMatchers("/api/roles/**").hasAuthority("ROLE_ADMINISTRADOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}