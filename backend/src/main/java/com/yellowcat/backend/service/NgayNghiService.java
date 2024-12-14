package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Ngaynghi;
import com.yellowcat.backend.repository.NgaynghiRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@EnableAsync
public class NgayNghiService {
    @Autowired
    NgaynghiRepository ngaynghiRepository;

    public void addOrUpdate(Ngaynghi ngaynghi) {
        ngaynghiRepository.save(ngaynghi);
    }

    public Optional<Ngaynghi> getNgaynghi(LocalDate date) {
        return ngaynghiRepository.findByNgaynghiAndAndTrangthai(date,true);
    }

    public Optional<Ngaynghi> getNgaynghibyid(int id) {return ngaynghiRepository.findById(id);}

    public List<Ngaynghi> getAllNgaynghi() {
        return ngaynghiRepository.findAll();
    }
    @Scheduled(cron = "0 0 0 * * ?") // Chạy vào 0:00 mỗi ngày
    @Transactional
    public void taoLichHenRongMoiNgay() {
        checkNgayNghi();
        System.out.println("Đã cập nhập trạng thái ngày nghỉ cuối ngày");
    }

    @EventListener(ContextRefreshedEvent.class) // Chạy khi ứng dụng khởi động
    @Transactional
    public void taoLichHenRongKhiKhoiDong() {
        checkNgayNghi();
        System.out.println("Đã cập nhập trạng thái ngày nghỉ khi khởi động");
    }

    private void checkNgayNghi(){
        List<Ngaynghi> ngaynghi = ngaynghiRepository.findByTrangthaiTrueAndNgaynghi(LocalDate.now());
        if (!ngaynghi.isEmpty()){
            for (Ngaynghi n:ngaynghi){
                    n.setTrangthai(false);
                    ngaynghiRepository.save(n);
            }
        }
    }

}
