package com.zorvyn.assignment.FinancialRecordManagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI financialRecordOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Data Processing and Access Control")
                        .description("### Financial Record Management System\n\n" +
                        "This API provides robust tracking for income and expenses with integrated JWT security.\n\n" +
                        "**System Information:**\n" +
                        "* **Database:** H2 In-Memory (Resets on application restart).\n" +
                        "* **Default Admin Credentials:** \n" +
                        "  * **Username:** `system@admin.com` \n" +
                        "  * **Password:** `Admin@12345` \n\n" +
                        "Use the credentials above at the `/login` endpoint to receive a Bearer token.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gokarna Pandey")
                                .email("gokarnapandey43@gmail.com")))
                // Add the security requirement so all endpoints show the "lock" icon
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}