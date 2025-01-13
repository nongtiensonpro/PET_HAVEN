package com.yellowcat.backend.service;

import com.yellowcat.backend.DTO.ThongKeResponDTO;
import com.yellowcat.backend.model.*;
import com.yellowcat.backend.repository.HoadonRepository;
import com.yellowcat.backend.repository.HoadondoidichvuRepository;
import com.yellowcat.backend.repository.TuyChonCanNangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class HoaDonService {
    private final HoadonRepository hoadonRepository;
    private final DichVuService dichVuService;
    private final GiamGiaService giamGiaService;
    private final EmailService emailService;
    private final TuyChonCanNangRepository tuyChonCanNangRepository;
    @Autowired
    HoadondoidichvuRepository hoadondoidichvuRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 17;
    private static final SecureRandom RANDOM = new SecureRandom();

    public HoaDonService(HoadonRepository hoadonRepository, DichVuService dichVuService, GiamGiaService giamGiaService, EmailService emailService, TuyChonCanNangRepository tuyChonCanNangRepository) {
        this.hoadonRepository = hoadonRepository;
        this.dichVuService = dichVuService;
        this.giamGiaService = giamGiaService;
        this.emailService = emailService;
        this.tuyChonCanNangRepository = tuyChonCanNangRepository;
    }

    public void addOrUpdate(Hoadon hoadon) {
        hoadonRepository.save(hoadon);
    }

    public Double TinhGiaTien(Integer idDichVu, Hoadon hoadon) {
        Optional<TuyChonCanNang> giaDichVuOptional = tuyChonCanNangRepository.findById(idDichVu);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<Giamgia> maxGiamGia = giamGiaService.findGiamGiaTheoNgayHienTai()
                .stream()
                .max(Comparator.comparing(Giamgia::getPhantramgiam));
        float phanTramGiam = 0;

        String id = authentication.getName();
        List<Hoadon> hoadonList = hoadonRepository.findByIdlichhen_TrangthaicaAndIdlichhen_IdkhachhangAndTrangthai(true, id);

        if (maxGiamGia.isPresent()) {
            float giamGiaPhanTram = maxGiamGia.get().getPhantramgiam();
            if (!hoadonList.isEmpty()) {
                for (Hoadon hoadon1 : hoadonList) {
                    if (hoadon1.getIdgiamgia() != null && hoadon1.getIdgiamgia().getId().equals(maxGiamGia.get().getId())) {
                        phanTramGiam = 0;
                        break;
                    } else {
                        phanTramGiam = giamGiaPhanTram;
                        hoadon.setIdgiamgia(maxGiamGia.get());
                        break;
                    }
                }
            } else {
                phanTramGiam = giamGiaPhanTram;
                hoadon.setIdgiamgia(maxGiamGia.get());
            }
        }

        if (phanTramGiam == 0 && !maxGiamGia.isPresent()) {
            phanTramGiam = 0; // Không có giảm giá
        }

        System.out.println("Phần trăm giảm giá: " + phanTramGiam);

        if (giaDichVuOptional.isPresent()) {
            TuyChonCanNang giaDichVu = giaDichVuOptional.get();
            Double giaTien = giaDichVu.getGiatien() * (1 - phanTramGiam / 100);
            System.out.println("Giá tiền: " + giaTien);
            return giaTien;
        } else {
            throw new IllegalArgumentException("Invalid idDichVu: " + idDichVu);
        }
    }


    public List<Hoadon> getAllHoaDonChuaThanhToan() {
        return hoadonRepository.findByIdlichhen_DateAndIdlichhen_TrangthaicaAndIdlichhen_TrangthaiNot(LocalDate.now(),true,2);
    }

    public List<Hoadon> getALl() {
        return hoadonRepository.findAll();
    }

    public Optional<Hoadon> findById(int id) {
        return hoadonRepository.findById(id);
    }

    public List<Hoadon> LichSuThanhToanHoaDonTheoTaiKhoan(String email) {
        return hoadonRepository.findByNguoithanhtoanAndPhuongthucthanhtoan(email, LocalDate.now());
    }

    public Optional<Hoadon> finHoadonByIdLich(Integer id) {
        return hoadonRepository.findByIdlichhen_Id(id);
    }

    public Optional<Hoadon> finHoadonByIdLich2(Integer id, Integer tt) {
        return hoadonRepository.findByIdlichhen_IdAndTrangthai(id, tt);
    }


    public List<Hoadon> getList() {
        return hoadonRepository.findByIdlichhen_TrangthaiAndTrangthaiAndDate(8, 2, LocalDate.now());
    }

    public Optional<Hoadon> findHoaDonOnline(String idPayPal) {
        return hoadonRepository.findByMagiaodich(idPayPal);
    }

    public static String MaGiaoDichRandom() {
        StringBuilder transactionId = new StringBuilder(LENGTH);

        // Thêm thời gian (milliseconds) vào mã giao dịch để tạo tính duy nhất
        transactionId.append(new Date().getTime());  // Thời gian hiện tại tính bằng milliseconds

        // Thêm phần ngẫu nhiên còn lại để hoàn thiện mã
        for (int i = 0; i < LENGTH - 13; i++) {  // Đảm bảo tổng độ dài là 17 ký tự
            transactionId.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }

        return transactionId.toString();  // Trả về mã giao dịch duy nhất
    }

    public void deleteHoadonById(Integer id) {
        hoadonRepository.deleteById(id);
    }

    @Async
    public void sendHoaDonSauThanhToan(Lichhen lichhen, byte[] pdfBytes) {
        try {
            // Nội dung email
            String subject = "Hóa đơn thanh toán";
            String body = "Cảm ơn bạn đã đặt lịch của chúng tôi. Vui lòng xem hóa đơn đính kèm." +
                    "Nếu gặp bất kì vấn đề gì vui lòng liên hệ fanpage hoặc sdt: 0906194201" +
                    "Nếu trong hôm nay bạn không liên hệ với chúng tôi, lịch của bạn sẽ bị hủy và được hoàn 80% giá trị hóa đơn";

            emailService.sendEmailWithHoaDon(
                    lichhen.getEmailNguoiDat(),  // Địa chỉ email người nhận
                    subject,                     // Tiêu đề email
                    body,                        // Nội dung email
                    pdfBytes,                    // File PDF dưới dạng byte[]
                    "invoice.pdf"                // Tên file đính kèm
            );

            System.out.println("Email hóa đơn đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email hóa đơn thất bại: " + e.getMessage());
        }
    }

    public List<Object[]> thongKeTheoNgay(LocalDate startDate, LocalDate endDate) {
        return hoadonRepository.thongKeTheoNgay(startDate, endDate);
    }

    public List<Object[]> thongKeTheoThang(LocalDate startDate, LocalDate endDate) {
        return hoadonRepository.thongKeTheoThang(startDate, endDate);
    }

    public List<Object[]> thongKeTheoNam(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = hoadonRepository.thongKeTheoNam(startDate, endDate);
        return rawData;
    }

    public List<ThongKeResponDTO> Top10KhachHang(LocalDate startDate, LocalDate endDate) {
        List<Object[]> rawData = hoadonRepository.findTopCustomers(startDate, endDate);
        List<ThongKeResponDTO> result = new ArrayList<>();

        for (Object[] row : rawData) {
            String emailNguoiDat = (String) row[0];  // Email người đặt
            int tongSoLichHen = ((Number) row[1]).intValue();  // Tổng số lịch hẹn
            double tongSoTien = ((Number) row[2]).doubleValue();  // Tổng số tiền

            ThongKeResponDTO dto = new ThongKeResponDTO(emailNguoiDat, tongSoLichHen, tongSoTien);
            result.add(dto);
        }
        return result;
    }

    public LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + date);
        }
    }


}
