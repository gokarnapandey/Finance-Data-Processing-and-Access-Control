package com.zorvyn.assignment.FinancialRecordManagement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI financialRecordOpenAPI() {

        return new OpenAPI()
                .info(apiInfo())

                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))

//                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))

                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme()))

                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://github.com/gokarnapandey/Finance-Data-Processing-and-Access-Control"));
    }

    private Info apiInfo() {
        return new Info()
                .title("Finance Data Processing and Access Control API")
                .version("1.0.0")
                .description(apiDescription())
                .contact(new Contact()
                        .name("Gokarna Pandey")
                        .email("gokarnapandey43@gmail.com"))
                .license(new License()
                        .name("Internal Use / Educational"));
    }

    private String apiDescription() {
        return "<h3>Financial Record Management System</h3>" +

                "<p>This API provides secure and scalable tracking for <b>income</b> and <b>expenses</b> using <b>JWT-based authentication</b>.</p>" +

                "<hr/>" +

                "<h4>📌 System Information</h4>" +
                "<ul>" +
                "<li><b>Database:</b> H2 In-Memory (Resets on restart)</li>" +
                "<li><b>Architecture:</b> Layered (Controller → Service → Repository)</li>" +
                "</ul>" +

                "<h4>🔐 Default Admin Credentials</h4>" +
                "<table border='1' cellpadding='5'>" +
                "<tr><th>Field</th><th>Value</th></tr>" +
                "<tr><td>Username</td><td><code>system@admin.com</code></td></tr>" +
                "<tr><td>Password</td><td><code>Admin@12345</code></td></tr>" +
                "</table>" +

                "<p><b>Usage:</b> Call <code>/login</code> → get JWT → use in header:</p>" +

                "<pre>Authorization: Bearer &lt;your_token&gt;</pre>" +

                "<hr/>" +

                "<h4>⚠️ Notes</h4>" +
                "<ul>" +
                "<li>All secured endpoints require JWT token</li>" +
                "<li>Stateless authentication (no session storage)</li>" +
                "<li>H2 DB resets on restart</li>" +
                "</ul>";
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }
}