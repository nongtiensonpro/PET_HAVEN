package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.LichhenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class LichHenService {

    private final LichhenRepository lichhenRepository;

    @Autowired
    CaLichHenService caLichHenService;

    public LichHenService(LichhenRepository lichhenRepository) {
        this.lichhenRepository = lichhenRepository;
    }

    public List<Lichhen> getAllLich(){
        return lichhenRepository.findAll();
    }
    public Page<Lichhen> getAllPageLichHen(Pageable pageable) {
        return lichhenRepository.findAll(pageable);
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
    @EventListener(ContextRefreshedEvent.class)
    public void taoLichHenRongKhiKhoiDong() {
        // Lấy tất cả các ca từ bảng calichhen
        List<Calichhen> danhSachCa = caLichHenService.findAll();

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7); // Tạo cho 7 ngày tới

        while (!startDate.isAfter(endDate)) {
            for (Calichhen ca : danhSachCa) {
                // Kiểm tra xem lịch hẹn rỗng đã tồn tại chưa, tránh thêm trùng
                if (!lichhenRepository.existsByDateAndIdcalichhen_Id(startDate, ca.getId())) {

//                    Calichhen calichhen = new Calichhen();
//                    calichhen.setId(ca.getId());

                    Lichhen lichHenRong = new Lichhen();
                    lichHenRong.setDate(startDate); // Chỉ thiết lập ngày
                    lichHenRong.setTrangthai(5); // Gán trạng thái là "Rỗng"
                    lichHenRong.setIdcalichhen(ca); // Gắn ID ca lịch hẹn để liên kết
                    if (lichHenRong.getEmailNguoiDat() == null) {
                        lichHenRong.setEmailNguoiDat("default-email@example.com"); // Giá trị mặc định
                    }
                    lichHenRong.setIdkhachhang("demo");
                    // Lưu vào bảng lichhen
                    lichhenRepository.save(lichHenRong);
                }
            }
            startDate = startDate.plusDays(1); // Chuyển sang ngày tiếp theo
        }
    }
}
