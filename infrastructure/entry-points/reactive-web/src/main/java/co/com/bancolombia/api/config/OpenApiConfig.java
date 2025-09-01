package co.com.bancolombia.api.config;

import io.swagger.v3.oas.models.Components;
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
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Creditya Authentication API")
                        .description("API para registro y autenticación de usuarios")
                        .version("v1.0.0")
                        .contact(new Contact().name("Equipo Backend").email("backend@empresa.com"))
                        .license(new License().name("Apache 2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local"),
                        new Server().url("https://api.tu-dominio.com").description("Prod")
                ));

        // Si usas JWT en endpoints protegidos, habilita el esquema de seguridad "bearerAuth"
        SecurityScheme bearer = new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        openAPI.components(new Components().addSecuritySchemes("bearerAuth", bearer));
        // Requisito global opcional: se aplicará a endpoints que declares con security = { @SecurityRequirement(name = "bearerAuth") }
        openAPI.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

        return openAPI;
    }

}
