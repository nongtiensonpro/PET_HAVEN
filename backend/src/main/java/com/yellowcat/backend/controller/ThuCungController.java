package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Thucung;
import com.yellowcat.backend.service.ThuCungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/thu-cung")
public class ThuCungController {
    @Autowired
    private ThuCungService thuCungService;

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/all")
    public Page<Thucung> allThuCung(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return thuCungService.pageThuCung(pageable);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addThuCung(@RequestBody Thucung thuCung) {
        Thucung thucung = thuCungService.saveOrUpdate(thuCung);
        return ResponseEntity.ok(thucung);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateThuCung(@RequestBody Integer id) {
        Optional<Thucung> thucungOptional = thuCungService.findThuCungById(id);
        if (!thucungOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("không tìm thấy thú cưng");
        }
        Thucung thucung = thucungOptional.get();
        Thucung updateThuCung = thuCungService.saveOrUpdate(thucung);
        return ResponseEntity.ok(updateThuCung);
    }

//    @PutMapping("/update-TT")
//    public ResponseEntity<?> updateThuCungTT(@RequestBody Integer id) {
//        Optional<Thucung> thucungOptional = thuCungService.findThuCungById(id);
//        if (!thucungOptional.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("không tìm thấy thú cưng")
//        }
//        Thucung thucung = thucungOptional.get();
////        if (thucung.get)
//        Thucung updateThuCung = thuCungService.saveOrUpdate(thucung);
//        return ResponseEntity.ok(updateThuCung);
//    }
}
