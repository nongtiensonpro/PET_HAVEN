package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Ngaynghi;
import com.yellowcat.backend.service.NgayNghiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ngay-nghi")
public class NgayNghiController {
    @Autowired
    private NgayNghiService ngayNghiService;

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/all")
    public List<Ngaynghi> all() {
        return ngayNghiService.getAllNgaynghi();
    }
}
