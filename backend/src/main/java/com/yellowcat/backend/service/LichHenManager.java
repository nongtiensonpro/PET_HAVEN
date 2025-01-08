package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.CalichhenRepository;
import com.yellowcat.backend.repository.LichhenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LichHenManager {

    private final LichhenRepository lichhenRepository;
    private final CalichhenRepository calichhenRepository;

    public LichHenManager(LichhenRepository lichhenRepository, CalichhenRepository calichhenRepository) {
        this.lichhenRepository = lichhenRepository;
        this.calichhenRepository = calichhenRepository;
    }

    public void taoLichHenRong() {
        // Lấy tất cả các ca từ bảng calichhen
        List<Calichhen> danhSachCa = calichhenRepository.findAll();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7); // Tạo cho 7 ngày tới

        while (!startDate.isAfter(endDate)) {
            for (Calichhen ca : danhSachCa) {
                if (ca.getTrangthai()){
                    // Kiểm tra xem lịch hẹn rỗng đã tồn tại chưa, tránh thêm trùng
                    if (!lichhenRepository.existsByDateAndIdcalichhen_Id(startDate, ca.getId())) {
                        Lichhen lichHenRong = new Lichhen();
                        lichHenRong.setDate(startDate); // Chỉ thiết lập ngày
                        lichHenRong.setTrangthai(5); // Gán trạng thái là "Rỗng"
                        lichHenRong.setIdcalichhen(ca); // Gắn ID ca lịch hẹn để liên kết
                        lichHenRong.setEmailNguoiDat("default-email@example.com"); // Giá trị mặc định
                        lichHenRong.setIdkhachhang("demo");
                        lichHenRong.setTrangthaica(false);
                        lichHenRong.setSolanthaydoi(0);
                        lichHenRong.setSolannhacnho(0);
                        // Lưu vào bảng lichhen
                        lichhenRepository.save(lichHenRong);
                    }
                }
            }
            startDate = startDate.plusDays(1); // Chuyển sang ngày tiếp theo
        }
    }
}