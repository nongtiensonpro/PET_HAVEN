package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Thongtincanhan;
import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.service.ThongTinCaNhanService;
import com.yellowcat.backend.service.ThuCungService;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final ThuCungService thuCungService;
    private final ThongTinCaNhanService thongTinCaNhanService;

    public UserController(ThuCungService thuCungService, ThongTinCaNhanService thongTinCaNhanService) {
        this.thuCungService = thuCungService;
        this.thongTinCaNhanService = thongTinCaNhanService;
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debug() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());
        return ResponseEntity.ok(authentication.getAuthorities());
    }


    @GetMapping("/api/user")
    public Map<String, Object> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idUser = auth.getName();

        Jwt jwt = (Jwt) auth.getPrincipal();
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
        String name = jwt.getClaimAsString("name");

        Thongtincanhan thongtincanhan = new Thongtincanhan();
        thongtincanhan.setIdtaikhoan(idUser);
        thongtincanhan.setHoten(name);
        thongtincanhan.setEmail(username);
        thongtincanhan.setRole(String.join(", ", petHavenRoles));
        Optional<Thongtincanhan> thongtincanhanOptional = thongTinCaNhanService.getThongtincanhanByIdTaiKhoan(idUser);
        if (!thongtincanhanOptional.isPresent()) {
            thongTinCaNhanService.addOrUpdate(thongtincanhan);
        }

        List<Thucung> thucungList = thuCungService.findListThuCungByidChu(idUser);

        Map<String, Object> response = new HashMap<>();
        response.put("roles", petHavenRoles);
        response.put("idUser", idUser);
        response.put("username", username);
        response.put("listThuCung", thucungList);

        return response;
    }

    @Value("${app.keycloak.serverUrl}")
    private String keycloakServerUrl;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin.clientId}")
    private String clientId;

    @Value("${app.keycloak.admin.clientSecret}")
    private String clientSecret;

    @Value("${app.keycloak.admin.username}")
    private String adminUsername;

    @Value("${app.keycloak.admin.password}")
    private String adminPassword;

    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm("master")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(adminUsername)
                .password(adminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/api/all-users")
    public List<Thongtincanhan> getAllUsersWithClientRoles() {
        return thongTinCaNhanService.getAllThongtincanhan();
    }

}
