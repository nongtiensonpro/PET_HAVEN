package com.yellowcat.backend.controller;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @GetMapping("/api/user")
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Map<String, Object> resourceAccess = (Map<String, Object>) jwt.getClaim("resource_access");
        List<String> petHavenRoles = null;
        if (resourceAccess != null) {
            Map<String, Object> petHavenAccess = (Map<String, Object>) resourceAccess.get("PetHaven");
            if (petHavenAccess != null) {
                petHavenRoles = (List<String>) petHavenAccess.get("roles");
            }
        }
        String username = jwt.getClaimAsString("preferred_username");
        if (username == null) {
            username = jwt.getClaimAsString("email");
        }

        return "Roles: " + petHavenRoles + " | idUser: " + idUser + " | username: " + username;
    }


}
