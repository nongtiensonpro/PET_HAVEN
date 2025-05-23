package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.service.GiamGiaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@PreAuthorize("hasRole('admin')")
@RequestMapping("/api/giam-gia")
public class GiamGiaController {

    private final GiamGiaService giamGiaService;

    public GiamGiaController(GiamGiaService giamGiaService) {
        this.giamGiaService = giamGiaService;
    }

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

    @PutMapping("/update-tt/{id}")
    public ResponseEntity<String> updateTt(@PathVariable int id) {
        boolean tt;
        Optional<Giamgia> giamgiaOptional = giamGiaService.findById(id);
        if (!giamgiaOptional.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Giamgia giamgia = giamgiaOptional.get();
        tt = giamgia.getTrangthai();
        tt = !tt;
        giamgia.setTrangthai(tt);
        giamGiaService.addOrUpdate(giamgia);
        return ResponseEntity.ok().build();

    }
}
