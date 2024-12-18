package com.yellowcat.backend.service;

import com.yellowcat.backend.DTO.ThongKeResponDTO;
import com.yellowcat.backend.DTO.ThongKeTimeDTO;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.HoadonRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    @Autowired
    HoadonRepository hoadonRepository;

    @Autowired
    DichVuService dichVuService;

    @Autowired
    GiamGiaService giamGiaService;

    @Autowired
    EmailService emailService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 17;
    private static final SecureRandom RANDOM = new SecureRandom();

    public void addOrUpdate(Hoadon hoadon){hoadonRepository.save(hoadon);}

    public Double TinhGiaTien(Integer idDichVu,Hoadon hoadon){
        float giaDichVu = dichVuService.findById(idDichVu)
                .map(Dichvu::getGiatien)
                .orElseThrow(() -> new IllegalArgumentException("Dịch vụ không tồn tại"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Optional<Giamgia> maxGiamGia = giamGiaService.findGiamGiaTheoNgayHienTai()
                .stream()
                .max(Comparator.comparing(Giamgia::getPhantramgiam));
        float phanTramGiam =0;
        phanTramGiam = Math.max(0, Math.min(phanTramGiam, 90));

        String id = authentication.getName();
        List<Hoadon> hoadonList = hoadonRepository.findByIdlichhen_TrangthaicaAndIdlichhen_IdkhachhangAndTrangthai(true,id);
        if (maxGiamGia.isPresent()) {
            if(!hoadonList.isEmpty()){
                for(Hoadon hoadon1 : hoadonList){
//                    Check xem đã dùng voucher chưa ???
                    if (hoadon1.getIdgiamgia() != null && hoadon1.getIdgiamgia().getId() == (maxGiamGia.get().getId())){
                        phanTramGiam =0;
                        System.out.println(1);
                        break;
                    }else {
                        phanTramGiam = maxGiamGia.get().getPhantramgiam();
                        hoadon.setIdgiamgia(maxGiamGia.get()); // Gán giảm giá lớn nhất vào hóa đơn
                        System.out.println(2);
                        break;
                    }
                }
            }else {
                phanTramGiam = maxGiamGia.get().getPhantramgiam();
                hoadon.setIdgiamgia(maxGiamGia.get()); // Gán giảm giá lớn nhất vào hóa đơn
            }
        }
        Double giaTien = (double) (giaDichVu - giaDichVu*phanTramGiam/100);
        return giaTien;
    }
    public List<Hoadon> getAllHoaDonChuaThanhToan(){
        System.out.println(LocalDate.now());
        return hoadonRepository.findByIdlichhen_Date(LocalDate.now());
    }

    public List<Hoadon> getALl(){return hoadonRepository.findAll();}

    public Optional<Hoadon> findById(int id){return hoadonRepository.findById(id);}

    public List<Hoadon> LichSuThanhToanHoaDonTheoTaiKhoan(String email){return hoadonRepository.findByNguoithanhtoanAndPhuongthucthanhtoan(email,LocalDate.now());}

    public Optional<Hoadon> finHoadonByIdLich(Integer id){return hoadonRepository.findByIdlichhen_IdAndTrangthai(id,1);}

    public Optional<Hoadon> finHoadonByIdLich2(Integer id,Integer tt){return hoadonRepository.findByIdlichhen_IdAndTrangthai(id,tt);}


    public List<Hoadon> getList(){
        return hoadonRepository.findByIdlichhen_TrangthaiAndTrangthaiAndDate(8,2,LocalDate.now());
    }

    public  Optional<Hoadon> findHoaDonOnline(String idPayPal)
    {return hoadonRepository.findByMagiaodich(idPayPal);}

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
    public void deleteHoadonById(Integer id){
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
        List<Object[]> rawData =  hoadonRepository.thongKeTheoNam(startDate,endDate);
        return rawData;
    }

    public List<ThongKeResponDTO> Top10KhachHang(LocalDate startDate, LocalDate endDate){
        List<Object[]> rawData =  hoadonRepository.findTopCustomers(startDate,endDate);
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
