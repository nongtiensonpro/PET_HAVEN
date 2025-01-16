package com.yellowcat.backend.controller;

import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.DTO.DoiLichDTO;
import com.yellowcat.backend.PAY.PayPalService;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.service.HoaDonService;
import com.yellowcat.backend.service.LichHenService;
import com.yellowcat.backend.service.PdfExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/lich-hen")
public class LichHenController {

    private final LichHenService lichHenService;
    private final HoaDonService hoaDonService;
    private final PdfExportService pdfExportService;
    @Autowired
    PayPalService payPalService;

    public LichHenController(LichHenService lichHenService, HoaDonService hoaDonService, PdfExportService pdfExportService) {
        this.lichHenService = lichHenService;
        this.hoaDonService = hoaDonService;
        this.pdfExportService = pdfExportService;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/all")
    public List<Lichhen> getAllLichHen(@RequestParam(defaultValue = "0") int page) {
        return lichHenService.getAllPageLichHen();
    }

    @GetMapping("/findByIdUser")
    public Page<Lichhen> findByIdUser(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 100); // 10 items per page

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName(); // Đây là idUser

        return lichHenService.findByIdUser(pageable,idUser);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/findByUserEmail")
    public Page<Lichhen> findByUserEmail(@RequestParam(defaultValue = "0") int page,@RequestParam("email") String Email) {
        Pageable pageable = PageRequest.of(page, 10);

        return lichHenService.findByEmailNguoiDat(pageable, Email);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/getListDoiTrangThai")
    public Page<Lichhen> listDoi(@RequestParam(defaultValue = "0") int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Lichhen> ListDoi = lichHenService.findAllLichWithTrangThai(pageable,true, LocalDate.now());
        return ListDoi;
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PutMapping("/updateTrangThai/{id}")
    public ResponseEntity<?> updateMore(@PathVariable int id) throws PayPalRESTException {

        Optional<Hoadon> hoadonOptional1 = hoaDonService.finHoadonByIdLich2(id, 1);
        Optional<Hoadon> hoadonOptional2 = hoaDonService.finHoadonByIdLich2(id, 2);

        Hoadon hoadonOptional;

        hoadonOptional = hoadonOptional2.orElse(null);
        if (hoadonOptional1.isPresent()) {
            hoadonOptional = hoadonOptional1.get();
        }
        if (hoadonOptional == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy hóa đơn phù hợp.");
        }
        if (hoadonOptional1.isPresent() && hoadonOptional2.isPresent() || !hoadonOptional1.isPresent() && !hoadonOptional2.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Hóa đơn ko hợp lệ");
        }

        Lichhen lichhen = lichHenService.findById(id);
        Map<String, String> response = new HashMap<>();
        if (lichhen == null) {
            System.out.println(1);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch hẹn không tồn tại.");
        }

        if (lichhen.getTrangthai() == 4 || lichhen.getTrangthai() == 6 || lichhen.getTrangthai() == 8) {
            // Tạo bản ghi lưu lại lịch sử đặt với trạng thái hủy
            lichhen.setSolanthaydoi(lichhen.getSolanthaydoi() + 1);

            Lichhen lichhenNew = new Lichhen();
            lichhenNew.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichhenNew.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichhenNew.setIdkhachhang(lichhen.getIdkhachhang());
            lichhenNew.setTrangthai(2); // Đặt trạng thái là "Đã hủy"
            lichhenNew.setIdcalichhen(lichhen.getIdcalichhen());
            lichhenNew.setThoigianhuy(LocalDateTime.now());
            lichhenNew.setThucung(lichhen.getThucung());
            lichhenNew.setTuyChonCanNang(lichhen.getTuyChonCanNang());
            lichhenNew.setDate(lichhen.getDate());
            lichhenNew.setTrangthaica(true);
            lichhenNew.setSolannhacnho(1);
            lichhenNew.setDoidichvu(lichhen.getDoidichvu());
            lichHenService.addOrUpdate(lichhenNew);

//            Hoàn tiền
            if (lichhen.getTrangthai() == 6) { // Đã thanh toán
                System.out.println(hoadonOptional.getMagiaodich()+ "   " + hoadonOptional.getSotien() + ":    Meo Meo Hoàn tiền");
                Refund refund = payPalService.refundPayment(hoadonOptional.getMagiaodich(), hoadonOptional.getSotien(), "USD");
                System.out.println(refund);
                if ("completed".equals(refund.getState())) {
                    hoadonOptional.setTrangthai(4); // Trạng thái: Đã hoàn tiền
                    hoaDonService.addOrUpdate(hoadonOptional);
                    lichhenNew.setTrangthai(7); // Trạng thái: Đã hoàn tiền
                    lichHenService.addOrUpdate(lichhenNew);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Hoàn tiền thất bại: " + refund.getReason());
                }
            }


//            Xóa hóa đơn chờ
            if (hoadonOptional.getTrangthai() == 1) {
                System.out.println("hello");
                hoadonOptional.setTrangthai(3); //Thất bại
                hoaDonService.addOrUpdate(hoadonOptional);
            }

            // Cập nhật lịch gốc với trạng thái rỗng
            lichhen.setIdkhachhang("demo");
            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            lichhen.setThucung(null);
            lichhen.setTuyChonCanNang(null);
            lichhen.setDoidichvu(false);
            if (lichhen.getTrangthaica()) {
                lichhen.setTrangthaica(false);
            } else {
                return ResponseEntity.ok("Lỗi ca");
            }
            lichHenService.addOrUpdate(lichhen);
            lichHenService.cancelScheduleChange();
            response.put("message", "Lịch hẹn đã được hủy thành công.");
            return ResponseEntity.ok(response);
        }
        response.put("message", "Lịch hẹn hủy không thành công.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PutMapping("/update-time/{id}")
    public ResponseEntity<?> doiTimeQuyenAdmin(@PathVariable Integer id,@Valid @RequestBody DoiLichDTO doiLichDTO) {
        Lichhen lichhen = lichHenService.findById(id);
        Lichhen lichhenNew = new Lichhen();
        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(id);
        if (lichhen == null) {
            System.out.println(1);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch hẹn không tồn tại.");
        }

//          Thay đổi thời gian và ca lịch
            Optional<Lichhen> lichhenDoiOptional = lichHenService.getLichHenByDateandCa(doiLichDTO.getDate(),Integer.parseInt(doiLichDTO.getIdcalichhen()));
            if (!lichhenDoiOptional.isPresent()) {
                System.out.println(2);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch lỗi.");
            }

            Lichhen lichDoi = lichhenDoiOptional.get();
            lichDoi.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichDoi.setIdkhachhang(lichhen.getIdkhachhang());
            lichDoi.setTrangthai(lichhen.getTrangthai()); // Đặt trạng thái là "Chờ thanh toán"
            lichDoi.setThoigianthaydoi(LocalDateTime.now());
            lichDoi.setThucung(lichhen.getThucung());
            lichDoi.setTuyChonCanNang(lichhen.getTuyChonCanNang());
            lichDoi.setTrangthaica(true);
            lichDoi.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichDoi.setSolannhacnho(0);
            lichDoi.setDoidichvu(lichhen.getDoidichvu());

            Hoadon hoadon = hoadonOptional.get();
            hoadon.setIdlichhen(lichDoi);

//            Cập nhập số lần thay đổi
            lichhen.setSolanthaydoi(lichhen.getSolanthaydoi()+1);

//            Tạo bản ghi lưu trừ lịch đổi
            lichhenNew.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichhenNew.setIdkhachhang(lichhen.getIdkhachhang());
            lichhenNew.setTrangthai(1); // Đặt trạng thái là "Thất bại"
            lichhenNew.setIdcalichhen(lichhen.getIdcalichhen());
            lichhenNew.setThoigianthaydoi(LocalDateTime.now());
            lichhenNew.setThucung(lichhen.getThucung());
            lichhenNew.setTuyChonCanNang(lichhen.getTuyChonCanNang());
            lichhenNew.setDate(lichhen.getDate());
            lichhenNew.setTrangthaica(true);
            lichhenNew.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichhenNew.setSolannhacnho(1);
            lichhenNew.setDoidichvu(false);

            lichhen.setIdkhachhang("demo");
            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            lichhen.setSolannhacnho(0);
            lichhen.setDoidichvu(false);
            if (lichhen.getTrangthaica()){
                lichhen.setTrangthaica(false);
            }else {
                return ResponseEntity.ok("Lỗi ca");
            }
            hoaDonService.addOrUpdate(hoadon);
            lichHenService.addOrUpdate(lichDoi);
            lichHenService.addOrUpdate(lichhenNew);
            lichHenService.addOrUpdate(lichhen);
            return ResponseEntity.ok("Thời gian của lịch hẹn đã được cập nhật.");
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        Lichhen lichhen = lichHenService.findById(id);
        return ResponseEntity.ok(lichhen);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/lich-hom-nay")
    public ResponseEntity<?> lichHomNay() {
        List<Lichhen> listHomNay = lichHenService.listLichHomNay();
        if (listHomNay.isEmpty()) {
            return  ResponseEntity.status(HttpStatus.OK).body("Nay được nghỉ à");
        }
        return ResponseEntity.ok(listHomNay);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @GetMapping("/thanh-toan/{id}")
    public ResponseEntity<?> ThanhToanHoaDon(@PathVariable Integer id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String Email = jwt.getClaimAsString("email");

        Lichhen lichhen = lichHenService.findById(id);
        Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(id);
        if (hoadonOptional.isPresent()) {
            Hoadon hoadon = hoadonOptional.get();
            if (lichhen == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            lichhen.setTrangthai(0);
            lichHenService.addOrUpdate(lichhen);

            if (hoadon.getTrangthai() == 1){
                hoadon.setNgaythanhtoan(LocalDateTime.now());
                hoadon.setTrangthai(2);
                hoadon.setNguoithanhtoan(Email);
                hoaDonService.addOrUpdate(hoadon);
            }

            // Tạo file PDF hóa đơn
//            String thoiGian = hoadon.getIdlichhen().getDate().toString()+ ' ' + hoadon.getIdlichhen().getIdcalichhen().getThoigianca();
//            byte[] pdfBytes = pdfExportService.generateInvoice(hoadon.getNgaythanhtoan().toString(),hoadon.getMagiaodich(),hoadon.getPhuongthucthanhtoan(),hoadon.getIdlichhen().getDichvu().getTendichvu(),hoadon.getSotienbandau(),hoadon.getSotien(),thoiGian);
//
//            hoaDonService.sendHoaDonSauThanhToan(lichhen,pdfBytes);

            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
