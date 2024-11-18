package com.yellowcat.backend.DTO;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class updateCaDTO {
    private int id;
    @NotNull
    private String time;
    @NotNull
    private String name;

    public @NotNull String getName() {
        return name;
    }


    public int getId() {
        return id;
    }

    public @NotNull String getTime() {
        return time;
    }
}
