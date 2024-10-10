package com.yellowcat.backend.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {

    @Value("${app.keycloak.admin.clientId}")
    String clientId;
    @Value("${app.keycloak.admin.clientSecret}")
    String clientSecret;
    @Value("${app.keycloak.realm}")
    String realm;
    @Value("${app.keycloak.serverUrl}")
    String serverUrl;

    @Bean
    public Keycloak keycloak(){

        return KeycloakBuilder.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .serverUrl(serverUrl)
                .grantType("client_credentials")
                .realm(realm)
                .build();
    }
}
