package com.yellowcat.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yellowcat.backend.DTO.DichVuDTO;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.service.CloudinaryService;
import com.yellowcat.backend.service.DichVuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dich-vu")
public class DichVuController {

    private final CloudinaryService cloudinaryService;

    private final DichVuService dichVuService;

    public DichVuController(CloudinaryService cloudinaryService, DichVuService dichVuService) {
        this.cloudinaryService = cloudinaryService;
        this.dichVuService = dichVuService;
    }

    @RequestMapping("/all")
    public Page<Dichvu> getAllDichVu(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id")); // 10 items per page
        return dichVuService.getAllDichVu(pageable);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @PostMapping(value = "/add" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> themDichVu(@RequestPart("dichVu") String dichVuJson,
                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        DichVuDTO dichVuDTO;
        try {
            dichVuDTO = objectMapper.readValue(dichVuJson, DichVuDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Dữ liệu JSON không hợp lệ"));
        }
        dichVuService.themDichVu(dichVuDTO, file);

        return ResponseEntity.ok(Map.of("message", "Thêm dịch vụ thành công"));
    }


    @PreAuthorize("hasAnyRole('admin')")
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateone(
            @RequestPart("dichVu") String dichVuJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @PathVariable int id) {

        ObjectMapper objectMapper = new ObjectMapper();
        DichVuDTO dichVuDTO;
        try {
            dichVuDTO = objectMapper.readValue(dichVuJson, DichVuDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Dữ liệu JSON không hợp lệ"));
        }

        dichVuService.capNhatDichVu( id, dichVuDTO, file);

        return ResponseEntity.ok(Map.of("message", "Cập nhật dịch vụ thành công"));
    }


    @PreAuthorize("hasAnyRole('admin')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        dichVuService.deleteDichVu(id);
        return ResponseEntity.status(HttpStatus.OK).body("Dịch vụ đã được xóa thành công");

    }

    @GetMapping("/find")
    public ResponseEntity<Page<Dichvu>> findDichVu(@RequestParam String namedv, @RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Dichvu> dichvus = dichVuService.FindByNameDV(namedv, pageable);
        return ResponseEntity.ok(dichvus);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<Dichvu> updateTrangThai(@PathVariable Integer id) {
        Optional<Dichvu> dichvu1 = dichVuService.findById(id);
        Dichvu dichvu = dichvu1.get();
        if (dichvu.getTrangthai()) {
            dichvu.setTrangthai(false);
        } else {
            dichvu.setTrangthai(true);
        }
        dichVuService.addOrUpdateDichVu(dichvu);
        return ResponseEntity.ok(dichvu);
    }

    @PreAuthorize("hasAnyRole('admin')")
    @PutMapping("/update-hien/{id}")
    public ResponseEntity<Dichvu> updateAnHienManHinh(@PathVariable Integer id) {
        Optional<Dichvu> dichvu1 = dichVuService.findById(id);
        if (!dichvu1.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Dichvu dichvu = dichvu1.get();
        if (dichvu.getHien()) {
            dichvu.setHien(false);
        } else {
            dichvu.setHien(true);
        }
        dichVuService.addOrUpdateDichVu(dichvu);
        return ResponseEntity.ok(dichvu);
    }
}
