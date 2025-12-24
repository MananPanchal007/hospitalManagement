package com.hospitalManagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
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
                new Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.hospitalmanagement.com")
                    .description("Production Server")
            ));
    }
}

