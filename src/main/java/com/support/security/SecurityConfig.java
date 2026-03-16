package com.support.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AppUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(AppUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // JWT — stateless, CSRF не нужен
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth

                // --- Публичные ---
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()

                // --- Покрытия: просмотр — любой авторизованный, управление — ADMIN ---
                .requestMatchers(HttpMethod.GET, "/api/coverages/**").authenticated()
                .requestMatchers("/api/coverages/**").hasRole("ADMIN")

                // --- Клиенты: просмотр — ADMIN/AGENT, управление — ADMIN ---
                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.POST, "/api/customers").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

                // --- Полисы: создание и просмотр — ADMIN/AGENT, удаление — ADMIN ---
                .requestMatchers(HttpMethod.GET, "/api/policies/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.POST, "/api/policies/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.PUT, "/api/policies/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.DELETE, "/api/policies/**").hasRole("ADMIN")

                // --- Платежи: просмотр и управление — ADMIN/AGENT ---
                .requestMatchers(HttpMethod.GET, "/api/payments/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.POST, "/api/payments/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.PUT, "/api/payments/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.DELETE, "/api/payments/**").hasRole("ADMIN")

                // --- Страховые случаи: подача — USER/ADMIN/AGENT, обработка — ADMIN/AGENT ---
                .requestMatchers(HttpMethod.POST, "/api/claims").hasAnyRole("ADMIN", "AGENT", "USER")
                .requestMatchers(HttpMethod.GET, "/api/claims/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.PUT, "/api/claims/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers(HttpMethod.DELETE, "/api/claims/**").hasRole("ADMIN")

                // --- Бизнес-операции и статистика — ADMIN/AGENT ---
                .requestMatchers("/api/insurance/**").hasAnyRole("ADMIN", "AGENT")

                .anyRequest().authenticated()
            )

            // JWT-фильтр запускается до стандартной аутентификации
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
