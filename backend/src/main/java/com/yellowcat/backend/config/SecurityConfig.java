package com.yellowcat.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

    private final jwtAuthConverter jwtAuthConverter;

    public SecurityConfig(com.yellowcat.backend.config.jwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // URL của endpoint cung cấp public key cho JWT
        String issuerUri = "http://localhost:9082/realms/spring";

        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        // Optional: Thêm các validator nếu cần thiết
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        jwtDecoder.setJwtValidator(withIssuer);

        return jwtDecoder;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/dich-vu/all", "/login", "/oauth2/**","/","/Dang-xuat","/images/AvatarDichVu/**","/api/payPal/payment/**","/api/danh-gia/danh-gia-theo-dich-vu/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/Dang-xuat")
                        .logoutSuccessUrl("http://localhost:3000")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthConverter)
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(keycloakClientRegistration());
    }

    private ClientRegistration keycloakClientRegistration() {
        return ClientRegistration.withRegistrationId("keycloak")
                .clientId("PetHaven") // ID của client trong Keycloak
                .clientSecret("GuFIaAADNfBUpuahqxLvMPWzqt6g8fRL") // Client secret
                .scope("openid", "profile", "email") // Scope yêu cầu
                .authorizationUri("http://localhost:9082/realms/spring/protocol/openid-connect/auth")
                .tokenUri("http://localhost:9082/realms/spring/protocol/openid-connect/token")
                .userInfoUri("http://localhost:9082/realms/spring/protocol/openid-connect/userinfo")
                .userNameAttributeName("preferred_username")
                .redirectUri("http://localhost:3000") // Cần chỉnh sửa cho đường dẫn của bạn
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Địa chỉ front-end
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Các phương thức
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization","*")); // Thêm header cần thiết
        configuration.setAllowCredentials(true); // Cho phép gửi cookie nếu cần

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Cấu hình CORS cho tất cả API
        return source;
    }


}
