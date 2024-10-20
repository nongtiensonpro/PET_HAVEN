package com.yellowcat.backend.controller;


import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.service.DichVuService;
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
@RequestMapping("/api/dich-vu")
public class DichVuController {
    private final DichVuService dichVuService;

    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }

    @RequestMapping("/all")
    public Page<Dichvu> getAllDichVu(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 items per page
        return dichVuService.getAllDichVu(pageable);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PostMapping("/add")
    public Dichvu addone(@RequestBody Dichvu dichvu){
        Dichvu createdDichVu = dichVuService.addOrUpdateDichVu(dichvu);
        return new ResponseEntity<>(createdDichVu, HttpStatus.CREATED).getBody();
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Dichvu> updateone(@RequestBody Dichvu dichvu, @PathVariable int id) {
        Optional<Dichvu> dichvu1 = dichVuService.findById(id);

        // Kiểm tra xem dichvu1 có tồn tại không
        if (!dichvu1.isPresent()) {
            return ResponseEntity.notFound().build(); // Hoặc bạn có thể ném ra một exception
        }

        Dichvu dichvu2 = dichvu1.get();
        dichvu2.setTendichvu(dichvu.getTendichvu()); // Kiểm tra phương thức này
        dichvu2.setMota(dichvu.getMota());
        dichvu2.setAnh(dichvu.getAnh());
        dichvu2.setGiatien(dichvu.getGiatien());

        dichVuService.addOrUpdateDichVu(dichvu2);
        return ResponseEntity.ok(dichvu2);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        dichVuService.deleteDichVu(id);
        return ResponseEntity.status(HttpStatus.OK).body("Dịch vụ đã được xóa thành công");

    }

    @GetMapping("/find")
    public ResponseEntity<List<Dichvu>> findDichVu(@RequestParam String namedv) {
        List<Dichvu> dichvus = dichVuService.FindByNameDV(namedv);
        return ResponseEntity.ok(dichvus);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<Dichvu> updateTrangThai(@PathVariable Integer id) {
        Optional<Dichvu> dichvu1 = dichVuService.findById(id);
        Dichvu dichvu = dichvu1.get();
        if (dichvu.getTrangthai()){
            dichvu.setTrangthai(false);
        }else {
            dichvu.setTrangthai(true);
        }
        dichVuService.addOrUpdateDichVu(dichvu);
        return ResponseEntity.ok(dichvu);
    }
}
