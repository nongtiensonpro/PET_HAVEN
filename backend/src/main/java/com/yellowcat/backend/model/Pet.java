package com.yellowcat.backend.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


public class Pet {
    private int id;
    private String name;
    private String type;
    private int age;
    private String breed;
    private Integer ownerId;

    public Pet() {
    }

    public Pet(int id, String name, String type, int age, String breed, Integer ownerId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.age = age;
        this.breed = breed;
        this.ownerId = ownerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
