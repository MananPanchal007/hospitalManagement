package com.hospitalManagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI Configuration
 * 
 * Configures API documentation using OpenAPI 3.0 specification.
 * Provides interactive API documentation accessible via Swagger UI.
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access OpenAPI JSON at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class SwaggerConfig {
    
    /**
     * Configures OpenAPI documentation for the Hospital Management System
     * 
     * Includes:
     * - API title, description, and version
     * - Contact information
     * - License information
     * - Server URLs for different environments
     * 
     * @return Configured OpenAPI object
     */
    @Bean
    public OpenAPI hospitalManagementOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Hospital Management System API")
                .description("RESTful API for managing patient records, appointments, and doctor schedules. " +
                           "This API supports transactional operations, validation, and pessimistic locking " +
                           "for high-concurrency scenarios.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Hospital Management Team")
                    .email("support@hospitalmanagement.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .servers(List.of(
                // Local development server
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                // Production server (example)
                new Server()
                    .url("https://api.hospitalmanagement.com")
                    .description("Production Server")
            ));
    }
}

