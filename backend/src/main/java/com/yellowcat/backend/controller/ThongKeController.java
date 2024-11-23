package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.ThongKeDTO;
import com.yellowcat.backend.DTO.ThongKeResponDTO;
import com.yellowcat.backend.DTO.ThongKeTimeDTO;
import com.yellowcat.backend.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("hasRole('admin')")
@RequestMapping("/api/thong-ke")
public class ThongKeController {
    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/ngay")
    public ResponseEntity<Map<String, Object>> thongKeTheoNgay(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<Object[]> data = hoaDonService.thongKeTheoNgay(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê theo ngày thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/thang")
    public ResponseEntity<Map<String, Object>> thongKeTheoThang(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<Object[]> data = hoaDonService.thongKeTheoThang(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê theo tháng thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/nam")
    public ResponseEntity<Map<String, Object>> thongKeTheoNam(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<Object[]> data = hoaDonService.thongKeTheoNam(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê theo năm thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top10User")
    public ResponseEntity<Map<String, Object>> thongKeNguoiDung(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<ThongKeResponDTO> data = hoaDonService.Top10KhachHang(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê top 10 người dùng thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }


}
