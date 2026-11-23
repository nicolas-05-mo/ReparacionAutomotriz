package com.usta.reparacionautomotriz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()
                        // Solo el administrador gestiona usuarios y roles
                        .requestMatchers("/usuarios/**", "/roles/**").hasRole("ADMINISTRADOR")
                        // Restricciones de creación/edición para administradores
                        .requestMatchers("/modelos/new", "/modelos/*/edit",
                                "/vehiculos/new", "/vehiculos/*/edit",
                                "/repuestos/new", "/repuestos/*/edit").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/modelos/**", "/vehiculos/**", "/repuestos/**")
                        .hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}