package com.se.hub.modules.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_AUTH = "bearerAuth";
    private static final String BEARER = "bearer";
    private static final String JWT = "JWT";

    @Value("${swagger.server.urls:}")
    private String serverUrls;


    @Bean
    public OpenAPI customizeOpenAPI() {
        final String securitySchemeName = BEARER_AUTH;

        List<Server> servers = new ArrayList<>();

        if (serverUrls != null && !serverUrls.trim().isEmpty()) {
            String[] urls = serverUrls.split(";");
            for (String item : urls) {
                if (item != null && !item.trim().isEmpty()) {
                    servers.add(new Server().url(item.trim()));
                }
            }
        }

        servers.add(new Server().url("http://localhost:8080").description("Local Development Server"));
        servers.add(new Server().url("https://apisehub.ftes.vn").description("Production Server"));
        

        List<Server> uniqueServers = new ArrayList<>();
        for (Server server : servers) {
            boolean exists = uniqueServers.stream()
                    .anyMatch(s -> s.getUrl().equals(server.getUrl()));
            if (!exists) {
                uniqueServers.add(server);
            }
        }

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(AUTHORIZATION)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(BEARER)
                                .bearerFormat(JWT)))
                .servers(uniqueServers);
    }
}

