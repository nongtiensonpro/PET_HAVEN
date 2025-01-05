package com.yellowcat.backend.controller;

import com.yellowcat.backend.DTO.ThongKeDTO;
import com.yellowcat.backend.DTO.ThongKeResponDTO;
import com.yellowcat.backend.DTO.ThongKeTimeDTO;
import com.yellowcat.backend.service.HoaDonService;
import com.yellowcat.backend.service.ChiTietThongKeService;
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
    
    @Autowired
    private ChiTietThongKeService chiTietThongKeService;

    @PostMapping("/ngay")
    public ResponseEntity<Map<String, Object>> thongKeTheoNgay(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<Object[]> data = hoaDonService.thongKeTheoNgay(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê theo ngày thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/thang")
    public ResponseEntity<Map<String, Object>> thongKeTheoThang(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<Object[]> data = hoaDonService.thongKeTheoThang(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê theo tháng thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/nam")
    public ResponseEntity<Map<String, Object>> thongKeTheoNam(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<Object[]> data = hoaDonService.thongKeTheoNam(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê theo năm thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/top10User")
    public ResponseEntity<Map<String, Object>> thongKeNguoiDung(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        List<ThongKeResponDTO> data = hoaDonService.Top10KhachHang(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê top 10 người dùng thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/doanh-thu-theo-dich-vu")
    public ResponseEntity<Map<String, Object>> thongKeDoanhThuTheoDichVu(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        Map<String, Object> data = chiTietThongKeService.thongKeDoanhThuTheoDichVu(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê doanh thu theo dịch vụ thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/thong-ke-thu-cung")
    public ResponseEntity<Map<String, Object>> thongKeThuCungTheoLoai(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        Map<String, Object> data = chiTietThongKeService.thongKeThuCungTheoLoai(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê thú cưng theo loại thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ty-le-huy-lich")
    public ResponseEntity<Map<String, Object>> tyLeHuyLich(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        Map<String, Object> data = chiTietThongKeService.tyLeHuyLich(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê tỷ lệ hủy lịch thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/gio-dat-lich-pho-bien")
    public ResponseEntity<Map<String, Object>> thongKeGioDatLichPhoBien(@RequestBody ThongKeDTO dto) {
        LocalDate startDate = hoaDonService.parseDate(dto.getStartDate());
        LocalDate endDate = hoaDonService.parseDate(dto.getEndDate());
        Map<String, Object> data = chiTietThongKeService.thongKeGioDatLichPhoBien(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thống kê giờ đặt lịch phổ biến thành công");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}
