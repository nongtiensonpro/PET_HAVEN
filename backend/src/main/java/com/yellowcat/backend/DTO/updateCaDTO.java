package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;

public class updateCaDTO {
    private int id;
    @NotNull
    private String time;

    public int getId() {
        return id;
    }

    public @NotNull String getTime() {
        return time;
    }
}
