package com.yellowcat.backend.controller;

import com.yellowcat.backend.service.DanhGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/danh-gia")
public class DanhGiaController {
    @Autowired
    DanhGiaService danhGiaService;

//    @PostMapping("/add")
//    public ResponseEntity<?> vietDanhGia(){
//        danhGiaService.addOrUpdateDanhGia();
//    }
}
