package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.service.DichVuService;
import com.yellowcat.backend.service.GiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/giam-gia")
@PreAuthorize("hasRole('admin')")
public class GiamGiaController {
    @Autowired
    private GiamGiaService giamGiaService;

    @GetMapping("/all")
    public List<Giamgia> getAll() {
        return giamGiaService.getGiamgia();
    }

    @PostMapping("/add")
    public Giamgia create(@RequestBody Giamgia giamgia) {
         Giamgia createGiamGia = giamGiaService.addOrUpdate(giamgia);
        return new ResponseEntity<>(createGiamGia, HttpStatus.CREATED).getBody();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable int id, @RequestBody Giamgia giamgia) {
        Optional<Giamgia> giamgia1 = giamGiaService.findById(id);
        Giamgia giamgia2 = giamgia1.get();

        giamgia2.setMota(giamgia.getMota());
        giamgia2.setNgaybatdau(giamgia.getNgaybatdau());
        giamgia2.setPhantramgiam(giamgia.getPhantramgiam());
        giamgia2.setNgayketthuc(giamgia.getNgayketthuc());
        giamgia2.setTrangthai(giamgia.getTrangthai());

        giamGiaService.addOrUpdate(giamgia2);
        return ResponseEntity.status(HttpStatus.OK).body("update thanh cong");
    }
}
