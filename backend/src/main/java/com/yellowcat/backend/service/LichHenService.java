package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.LichhenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class LichHenService {

    private final LichhenRepository lichhenRepository;

    @Autowired
    CaLichHenService caLichHenService;

    @Autowired
    private EmailService emailService;

    public LichHenService(LichhenRepository lichhenRepository) {
        this.lichhenRepository = lichhenRepository;
    }

    public List<Lichhen> getAllLich(){
        return lichhenRepository.findAll();
    }
    public Page<Lichhen> getAllPageLichHen(Pageable pageable) {
        return lichhenRepository.findAll(pageable);
    }

    public Optional<Lichhen> getLichHenByDateandCa(LocalDate date,Integer id){
        return lichhenRepository.findByDateAndIdcalichhen_Id(date,id);
    }

    public Page<Lichhen> findByIdUser(Pageable pageable, String idUser){
        return lichhenRepository.findByIdkhachhang(idUser, pageable);
    }

    public Page<Lichhen> findByEmailNguoiDat(Pageable pageable, String userName){
        return lichhenRepository.findByEmailNguoiDat(userName, pageable);
    }

    // Kiểm tra ca đã có trong ngày chưa
    public boolean isCaTrungTrongNgay(LocalDate ngay, int idCaLichHen) {
        return lichhenRepository.existsByDateAndIdcalichhen_Id(ngay, idCaLichHen);
    }

    public Lichhen addOrUpdate(Lichhen lichhen){
        return lichhenRepository.save(lichhen);
    }

    public Lichhen findById(Integer id){
        return lichhenRepository.findById(id).orElse(null);
    }

    // Hàm tự động chạy khi ứng dụng khởi động
    @Scheduled(cron = "0 0 0 * * ?") // Chạy vào 0:00 mỗi ngày
    @Transactional
    public void taoLichHenRongMoiNgay() {
        taoLichHenRong();
        System.out.println("Đã tạo lịch hẹn rỗng mới cho 7 ngày tới vào lúc 0:00.");
    }

    @EventListener(ContextRefreshedEvent.class) // Chạy khi ứng dụng khởi động
    @Transactional
    public void taoLichHenRongKhiKhoiDong() {
        taoLichHenRong();
        System.out.println("Đã tạo lịch hẹn rỗng mới cho 7 ngày tới khi khởi động.");
    }

    private void taoLichHenRong() {
        // Lấy tất cả các ca từ bảng calichhen
        List<Calichhen> danhSachCa = caLichHenService.findAll();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7); // Tạo cho 7 ngày tới

        while (!startDate.isAfter(endDate)) {
            for (Calichhen ca : danhSachCa) {
                // Kiểm tra xem lịch hẹn rỗng đã tồn tại chưa, tránh thêm trùng
                if (!lichhenRepository.existsByDateAndIdcalichhen_Id(startDate, ca.getId())) {
                    Lichhen lichHenRong = new Lichhen();
                    lichHenRong.setDate(startDate); // Chỉ thiết lập ngày
                    lichHenRong.setTrangthai(5); // Gán trạng thái là "Rỗng"
                    lichHenRong.setIdcalichhen(ca); // Gắn ID ca lịch hẹn để liên kết
                    lichHenRong.setEmailNguoiDat("default-email@example.com"); // Giá trị mặc định
                    lichHenRong.setIdkhachhang("demo");
                    lichHenRong.setTrangthaica(false);
                    // Lưu vào bảng lichhen
                    lichhenRepository.save(lichHenRong);
                }
            }
            startDate = startDate.plusDays(1); // Chuyển sang ngày tiếp theo
        }
    }

    @Scheduled(cron = "0 59 23 * * ?") // Chạy vào 23:59 mỗi ngày
    @Transactional
    public void xoaLichHenRongCuoiNgay() {
        xoaLichHenRong();
        System.out.println("Đã xóa lịch hẹn rỗng vào cuối ngày.");
    }

    @EventListener(ContextRefreshedEvent.class) // Chạy khi ứng dụng khởi động
    @Transactional
    public void xoaLichHenRongKhiKhoiDong() {
        xoaLichHenRong();
        System.out.println("Đã xóa lịch hẹn rỗng khi khởi động.");
    }

    private void xoaLichHenRong() {
        LocalDate today = LocalDate.now();
        // Tìm tất cả các bản ghi có email là "default-email@example.com" và ngày nhỏ hơn ngày hôm nay
        List<Lichhen> danhSachLichHenRong = lichhenRepository.findByEmailNguoiDatAndDateBefore("default-email@example.com", today);

        // Xóa tất cả các bản ghi tìm được
        if (!danhSachLichHenRong.isEmpty()) {
            lichhenRepository.deleteAll(danhSachLichHenRong);
            System.out.println("Đã xóa " + danhSachLichHenRong.size() + " lịch hẹn rỗng.");
        } else {
            System.out.println("Không có lịch hẹn rỗng nào để xóa.");
        }
    }

    public void sendEmailWithActions(Lichhen lichhen) {
        try {
            String cancelUrl = "http://localhost:3000/thay-doi-lich/" + lichhen.getId();
            String Url = "http://localhost:3000/chi-tiet-lich/" + lichhen.getId();

            String message = "Chào bạn,\n\n"
                    + "Cảm ơn bạn đã đặt lịch. Bạn có thể sử dụng các liên kết dưới đây để quản lý lịch hẹn của mình:\n\n"
                    + "Hủy hoặc thay đổi thời gian lịch: " + cancelUrl + "\n"
                    + "Chi tiết lịch hẹn: " + Url + "\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ hỗ trợ";

            emailService.sendEmail(lichhen.getEmailNguoiDat(),"Hủy hoặc đổi thời gian lịch",message);
            System.out.println("Email đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }

}
