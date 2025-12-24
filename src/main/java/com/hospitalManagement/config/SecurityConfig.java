package com.hospitalManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration
 * 
 * Configures security settings for the Hospital Management System API.
 * 
 * Security Features:
 * - CSRF protection disabled (stateless REST API)
 * - Public access to API endpoints and Swagger UI
 * - Stateless session management (no server-side sessions)
 * 
 * Note: In production, you should implement proper authentication
 * (JWT tokens, OAuth2, etc.) and restrict API access based on roles.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configures the security filter chain
     * 
     * Security Settings:
     * - CSRF disabled: REST APIs are stateless and don't use cookies
     * - Public API access: All /api/** endpoints are accessible without authentication
     * - Swagger UI access: Documentation endpoints are publicly accessible
     * - Stateless sessions: No server-side session storage (better for scalability)
     * 
     * @param http HttpSecurity object to configure
     * @return Configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection for stateless REST API
            // CSRF is typically needed for stateful web applications with cookies
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure request authorization
            .authorizeHttpRequests(auth -> auth
                // Allow public access to API endpoints
                // In production, add authentication/authorization here
                .requestMatchers(
                    "/api/**",                    // All API endpoints
                    "/swagger-ui/**",            // Swagger UI interface
                    "/v3/api-docs/**",           // OpenAPI documentation JSON
                    "/swagger-resources/**",     // Swagger resources
                    "/webjars/**"                // WebJars static resources
                ).permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            
            // Configure session management
            .sessionManagement(session -> session
                // STATELESS: No session is created or used by Spring Security
                // Better for REST APIs and scalability
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}

