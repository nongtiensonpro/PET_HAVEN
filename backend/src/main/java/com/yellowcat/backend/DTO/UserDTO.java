package com.yellowcat.backend.DTO;

import java.util.List;
import java.util.Map;

public class UserDTO {
    private String id;
    private String username;
    private Map<String, List<String>> clientRoles; // Key: clientId, Value: List of role names

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Map<String, List<String>> getClientRoles() {
        return clientRoles;
    }

    public void setClientRoles(Map<String, List<String>> clientRoles) {
        this.clientRoles = clientRoles;
    }
}
