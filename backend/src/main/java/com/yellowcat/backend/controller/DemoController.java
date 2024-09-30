package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Pet;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "http://localhost:9200")
public class DemoController {

    @GetMapping
    public List<Pet> getPets() {
        return Arrays.asList(
                new Pet(1, "Dog", "Chó", 2, "Golden Retriever", 5),
                new Pet(2, "Cat", "Mèo", 3, "Siamese", 6),
                new Pet(3, "Hamster", "Chuột Hamster", 1, "Siamese", 8)
        );
    }
}