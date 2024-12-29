package com.yellowcat.backend.controller;

import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.DTO.DatLichDTO;
import com.yellowcat.backend.DTO.DatLichMoiDTO;
import com.yellowcat.backend.DTO.DoiLichDTO;
import com.yellowcat.backend.PAY.PayPalService;
import com.yellowcat.backend.model.*;
import com.yellowcat.backend.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.yellowcat.backend.repository.TuyChonCanNangRepository;


@RestController
@RequestMapping("/api/dat-lich")
public class DatLichController {
    @Autowired
    public LichHenService lichHenService;

    @Autowired
    private CaLichHenService caLichHenService;

    @Autowired
    private DichVuService dichVuService;

    @Autowired
    private ThuCungService thuCungService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private NgayNghiService ngayNghiService;

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private TuyChonCanNangRepository tuyChonCanNangRepository;

    @GetMapping("/dat-lich-info")
    public ResponseEntity<Map<String, Object>> getDatLichInfo(@RequestParam("ngay") LocalDate ngay) {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String idUser = auth.getName();

        // Kiểm tra ngày nghỉ (ngày lễ)
        Optional<Ngaynghi> ngaynghiOptional = ngayNghiService.getNgaynghi(ngay);
        if (!ngaynghiOptional.isPresent()) {
            // Lấy các ca có thể đặt
            List<Calichhen> CaLichHen = caLichHenService.getAllByDate(ngay);
            response.put("CaLichHen", CaLichHen);
        }

        // Lấy danh sách dịch vụ
        List<Dichvu> danhSachDichVu = dichVuService.getListTrangThaiTrue();
        response.put("dichVu", danhSachDichVu);

        // Lấy danh sách thú cưng
        List<Thucung> listThuCung = thuCungService.findListThuCungByidChu(idUser);
        response.put("ListThuCung", listThuCung);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/xac-nhan-dat")
    public ResponseEntity<Lichhen> createLichhen(@Valid @RequestBody DatLichMoiDTO datLichDTO) {
        System.out.println("DatLichMoiDTO: " + datLichDTO.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        String idUser = authentication.getName(); // Đây là idUser
        String email = jwt.getClaimAsString("email");
        Optional<Lichhen> lichhenOptional = lichHenService.getLichHenByDateandCa(LocalDate.parse(datLichDTO.getDate()), datLichDTO.getIdcalichhen());

        Optional<Thucung> thucungOptional = thuCungService.findThuCungById(datLichDTO.getIdThuCung());
        if (thucungOptional.isPresent()) {
            Thucung thucung = thucungOptional.get();
            thucung.setIdtaikhoan(idUser);
            thuCungService.saveOrUpdate(thucung);
        } else {
            thuCungService.saveOrUpdate(new Thucung(idUser, datLichDTO.getIdThuCung()));
        }

        Optional<Dichvu> dichvuOptional = dichVuService.findById(datLichDTO.getIdDichVu());
        Dichvu dichvu = dichvuOptional.get();

        if (!lichhenOptional.isPresent()) {
            System.out.println("lịch không tồn tại");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Check xem có lịch đã được đặt chưa
        if (lichhenOptional.get().getTrangthai() != 5) {
            System.out.println("Lịch đã được đặt trước rồi , vui lòng chọn thời gian khác");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Check không cho đặt ca trong quá khứ
        if (!caLichHenService.isCaAvailable(datLichDTO.getIdcalichhen(), LocalDate.parse(datLichDTO.getDate()))) {
            System.out.println("Không được đặt ca trong quá khứ");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Lichhen lichhen = lichhenOptional.get();
        System.out.println(lichhen);
        lichhen.setIdkhachhang(idUser);
        lichhen.setEmailNguoiDat(email);
        lichhen.setTrangthai(4);
        lichhen.setThucung(thucungOptional.orElse(null)); // Fix: Use orElse(null) to handle Optional
        lichhen.setDate(LocalDate.parse(datLichDTO.getDate()));
        lichhen.setDichvu(dichvu);
        lichhen.setSolannhacnho(0);
        if (!lichhen.getTrangthaica()) {
            lichhen.setTrangthaica(true);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Hoadon hoadon = new Hoadon();
        hoadon.setDate(LocalDate.now());
        hoadon.setIdlichhen(lichhen);
        hoadon.setPhuongthucthanhtoan("Offline");
        hoadon.setTrangthai(1);
        Double SoTien = hoaDonService.TinhGiaTien(datLichDTO.getIdDichVu(), hoadon);
        hoadon.setSotienbandau(Double.valueOf(String.valueOf(tuyChonCanNangRepository.findById(datLichDTO.getIdTuyChonCanNang()).get().getGiatien())));
        hoadon.setSotien(SoTien);
        hoadon.setMagiaodich(hoaDonService.MaGiaoDichRandom());
        hoaDonService.addOrUpdate(hoadon);

        Lichhen createLich = lichHenService.addOrUpdate(lichhen);

        lichHenService.sendEmailWithActions(createLich);

        lichHenService.scheduleTrangThaiChange(createLich.getId());

        lichHenService.updateScheduleId(lichhen.getId(), lichhen.getId());

        return new ResponseEntity<>(createLich, HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("/huy-lich/{id}")
    public ResponseEntity<?> huyLichHen(@PathVariable Integer id) throws PayPalRESTException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();
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

        // Check xem có phải chủ lịch không
        if (!lichhen.getIdkhachhang().equalsIgnoreCase(idUser)) {
            System.out.println(1);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không phải là chủ lịch hẹn này.");
        }

        if (lichhen.getTrangthai() == 4 || lichhen.getTrangthai() == 6) {
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
            lichhenNew.setDichvu(lichhen.getDichvu());
            lichhenNew.setDate(lichhen.getDate());
            lichhenNew.setTrangthaica(true);
            lichHenService.addOrUpdate(lichhenNew);

//            Hoàn tiền
            if (lichhen.getTrangthai() == 6) { // Đã thanh toán
                System.out.println(hoadonOptional.getMagiaodich());
                Refund refund = payPalService.refundPayment(hoadonOptional.getMagiaodich(), hoadonOptional.getSotien(), "USD");
                System.out.println(refund);
                if ("completed".equals(refund.getState())) {
                    hoadonOptional.setTrangthai(3); // Trạng thái: thất bại
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
                hoaDonService.deleteHoadonById(hoadonOptional.getId());
            }

            // Cập nhật lịch gốc với trạng thái đã hủy
            lichhen.setIdkhachhang("demo");
            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
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

    @Transactional
    @PutMapping("/thay-doi-thoi-gian/{id}")
    public ResponseEntity<?> thayDoiThoiGian(@PathVariable Integer id, @Valid @RequestBody DoiLichDTO doiLichDTO) {
        Lichhen lichhen = lichHenService.findById(id);
        Lichhen lichhenNew = new Lichhen();
        Optional<Hoadon> hoadonOptional1 = hoaDonService.finHoadonByIdLich2(id, 1);
        Optional<Hoadon> hoadonOptional2 = hoaDonService.finHoadonByIdLich2(id, 2);

        Hoadon hoadonOptional;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String idUser = authentication.getName();

        if (lichhen == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch hẹn không tồn tại.");
        }

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

        // Check xem có phải chủ lịch không
        if (!lichhen.getIdkhachhang().equalsIgnoreCase(idUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không phải là chủ lịch hẹn này.");
        }

        if (lichhen.getTrangthai() == 4 || lichhen.getTrangthai() == 6) {

//          Thay đổi thời gian và ca lịch
            Optional<Lichhen> lichhenDoiOptional = lichHenService.getLichHenByDateandCa(doiLichDTO.getDate(), Integer.parseInt(doiLichDTO.getIdcalichhen()));
            if (!lichhenDoiOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lịch lỗi.");
            }

            Lichhen lichDoi = lichhenDoiOptional.get();
            lichDoi.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichDoi.setIdkhachhang(lichhen.getIdkhachhang());
            lichDoi.setTrangthai(lichhen.getTrangthai());
            lichDoi.setThoigianthaydoi(LocalDateTime.now());
            lichDoi.setThucung(lichhen.getThucung());
            lichDoi.setDichvu(lichhen.getDichvu());
            lichDoi.setTrangthaica(true);
            lichDoi.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichDoi.setSolannhacnho(lichhen.getSolannhacnho());
            lichHenService.addOrUpdate(lichDoi);

//            Cập nhập lại hóa đơn chờ
            hoadonOptional.setIdlichhen(lichDoi);
            hoaDonService.addOrUpdate(hoadonOptional);

//            Cập nhập số lần thay đổi
            lichhen.setSolanthaydoi(lichhen.getSolanthaydoi() + 1);

//            Tạo bản ghi lưu trừ lịch đổi
            lichhenNew.setEmailNguoiDat(lichhen.getEmailNguoiDat());
            lichhenNew.setIdkhachhang(lichhen.getIdkhachhang());
            lichhenNew.setTrangthai(1); // Đặt trạng thái là "Thất bại"
            lichhenNew.setIdcalichhen(lichhen.getIdcalichhen());
            lichhenNew.setThoigianthaydoi(LocalDateTime.now());
            lichhenNew.setThucung(lichhen.getThucung());
            lichhenNew.setDichvu(lichhen.getDichvu());
            lichhenNew.setDate(lichhen.getDate());
            lichhenNew.setTrangthaica(true);
            lichhenNew.setSolanthaydoi(lichhen.getSolanthaydoi());
            lichhenNew.setSolannhacnho(lichhen.getSolannhacnho());
            lichHenService.addOrUpdate(lichhenNew);

            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            lichhen.setIdkhachhang("demo");
            if (lichhen.getTrangthaica()) {
                lichhen.setTrangthaica(false);
            } else {
                return ResponseEntity.ok("Lỗi ca");
            }
            lichHenService.updateScheduleId(id, lichDoi.getId());
            lichHenService.addOrUpdate(lichhen);
            return new ResponseEntity<>(lichDoi, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
