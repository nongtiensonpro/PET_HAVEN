package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.service.ThuCungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    ThuCungService thuCungService;

    @GetMapping("/api/user")
    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
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

        List<Thucung> thucungList = thuCungService.findListThuCungByidChu(idUser);

        Map<String, Object> response = new HashMap<>();
        response.put("roles", petHavenRoles);
        response.put("idUser", idUser);
        response.put("username", username);
        response.put("listThuCung", thucungList);

        return response;
    }
}
