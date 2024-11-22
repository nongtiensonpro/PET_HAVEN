package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.ThongKeDTO;
import com.yellowcat.backend.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@PreAuthorize("hasRole('admin')")
@RequestMapping("/api/thong-ke")
public class ThongKeController {
    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/ngay")
    public List<Object[]> thongKeTheoNgay(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        return hoaDonService.thongKeTheoNgay(startDate, endDate);
    }

    @GetMapping("/thang")
    public List<Object[]> thongKeTheoThang(@RequestParam ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        return hoaDonService.thongKeTheoThang(startDate, endDate);
    }

    @GetMapping("/nam")
    public List<Object[]> thongKeTheoNam(@RequestParam ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        return hoaDonService.thongKeTheoNam(startDate, endDate);
    }



}
