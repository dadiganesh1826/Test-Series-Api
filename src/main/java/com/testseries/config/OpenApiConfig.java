package com.testseries.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI testSeriesOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("Test Series Team");
        contact.setEmail("support@testseries.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Test Series Exam Dashboard API")
                .version("1.0.0")
                .description("REST API for managing test series, exams, questions, and user exam attempts. " +
                        "This API provides endpoints for creating and managing test series, taking exams, " +
                        "submitting answers, and viewing results.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
