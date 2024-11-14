package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/hoa-don")
public class HoaDonController {
    @Autowired
    HoaDonService hoaDonService;

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/all-chuaTT")
    public List<Hoadon> getAllHoaDonChuaThanhToan(){
        List<Hoadon> listHdChuaTT = hoaDonService.getAllHoaDonChuaThanhToan(1);
        return listHdChuaTT;
    }

    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/all")
    public List<Hoadon> getAllHoaDon(){
        List<Hoadon> listHdChuaTT = hoaDonService.getAllHoaDonChuaThanhToan(1);
        return listHdChuaTT;
    }

    @PreAuthorize("hasAnyRole('manager')")
    @GetMapping("/Lich-su-thanh-toan-nhan-vien")
    public List<Hoadon> getAllHoaDonTheoTK(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String Email = jwt.getClaimAsString("email");
        List<Hoadon> lichSuThanhToanHoaDon = hoaDonService.LichSuThanhToanHoaDonTheoTaiKhoan(Email);
        return lichSuThanhToanHoaDon;
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/thanh-toan/{id}")
    public ResponseEntity<?> ThanhToanHoaDon(@PathVariable Integer id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String Email = jwt.getClaimAsString("email");

        Optional<Hoadon> hoadonOptional = hoaDonService.findById(id);
        if (hoadonOptional.isPresent()) {
            Hoadon hoadon = hoadonOptional.get();
            hoadon.setNgaythanhtoan(LocalDateTime.now());
            hoadon.setTrangthai(2);
            hoadon.setNguoithanhtoan(Email);
            hoaDonService.addOrUpdate(hoadon);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


}
