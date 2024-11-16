package com.yellowcat.backend.controller;

import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.HoaDonService;
import com.yellowcat.backend.service.LichHenService;
import com.yellowcat.backend.service.PdfExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/api/hoa-don")
public class HoaDonController {
    @Autowired
    HoaDonService hoaDonService;
    @Autowired
    PdfExportService pdfExportService;
    @Autowired
    LichHenService lichHenService;

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/all-chuaTT")
    public List<Hoadon> getAllHoaDonChuaThanhToan(){
        List<Hoadon> listHdChuaTT = hoaDonService.getAllHoaDonChuaThanhToan(1);
        return listHdChuaTT;
    }

    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping("/all")
    public List<Hoadon> getAllHoaDon(){
        List<Hoadon> listHdChuaTT = hoaDonService.getALl();
        return listHdChuaTT;
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/Lich-su-thanh-toan-nhan-vien")
    public List<Hoadon> getAllHoaDonTheoTK(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String Email = jwt.getClaimAsString("email");
        List<Hoadon> lichSuThanhToanHoaDon = hoaDonService.LichSuThanhToanHoaDonTheoTaiKhoan(Email);
        return lichSuThanhToanHoaDon;
    }

    @GetMapping("/chi-tiet-hd")
    public ResponseEntity<Hoadon> getHoaDonChiTietHd(
            @RequestParam Integer id
    ){
        Optional<Hoadon> hoadon = hoaDonService.findById(id);
        if (!hoadon.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Hoadon hoaDon = hoadon.get();
        return new ResponseEntity<>(hoaDon, HttpStatus.OK);
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

            Lichhen lichhen = lichHenService.findById(hoadon.getIdlichhen().getId());
            if (lichhen == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            lichhen.setTrangthai(0);
            lichHenService.addOrUpdate(lichhen);

            hoadon.setNgaythanhtoan(LocalDateTime.now());
            hoadon.setTrangthai(2);
            hoadon.setNguoithanhtoan(Email);
            hoaDonService.addOrUpdate(hoadon);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/in-hoa-don")
        public ResponseEntity<byte[]> getInvoice(@RequestParam Integer id) {
        Optional<Hoadon> hoadonOptional = hoaDonService.findById(id);
        if (!hoadonOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Hoadon hoadon = hoadonOptional.get();
        byte[] pdfBytes = pdfExportService.generateInvoice(hoadon.getNgaythanhtoan().toString(),hoadon.getMagiaodich(),hoadon.getPhuongthucthanhtoan(),hoadon.getIdlichhen().getDichvu().getTendichvu(),hoadon.getSotien());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "invoice.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }


}
