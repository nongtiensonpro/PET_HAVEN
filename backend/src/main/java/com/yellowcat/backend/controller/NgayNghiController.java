package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Ngaynghi;
import com.yellowcat.backend.service.NgayNghiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ngay-nghi")
public class NgayNghiController {

    private final NgayNghiService ngayNghiService;

    public NgayNghiController(NgayNghiService ngayNghiService) {
        this.ngayNghiService = ngayNghiService;
    }

    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/all")
    public List<Ngaynghi> all() {
        return ngayNghiService.getAllNgaynghi();
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/doi-trang-thai-nghi")
    public ResponseEntity<Ngaynghi> huyNgayNghi(@RequestParam int idNgayNghi) {
        Optional<Ngaynghi> ngaynghiOptional = ngayNghiService.getNgaynghibyid(idNgayNghi);
        if (ngaynghiOptional.isPresent()) {
            Ngaynghi ngaynghi = ngaynghiOptional.get();
            ngaynghi.setTrangthai(!ngaynghi.getTrangthai());
            ngayNghiService.addOrUpdate(ngaynghi);
            return ResponseEntity.ok(ngaynghi);
        }
        return ResponseEntity.badRequest().build();
    }
}

