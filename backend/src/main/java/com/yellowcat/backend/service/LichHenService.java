package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Calichhen;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.LichhenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
@EnableAsync
@EnableScheduling
public class LichHenService {

    private final LichhenRepository lichhenRepository;

    @Autowired
    LichHenManager lichHenManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private HoaDonService hoaDonService;

    public LichHenService(LichhenRepository lichhenRepository) {
        this.lichhenRepository = lichhenRepository;
    }

    public List<Lichhen> getAllLich(){
        return lichhenRepository.findAll();
    }
    public Page<Lichhen> getAllPageLichHen(Pageable pageable) {
        return lichhenRepository.findAll(pageable);
    }

    public Optional<Lichhen> getLichHenByDateandCa(LocalDate date,Integer idCa){
        return lichhenRepository.findByDateAndIdcalichhen_IdAndTrangthai(date,idCa,5);
    }

    public List<Lichhen> getLichhenByDateAndIdCa(Integer idCa){
        return lichhenRepository.findByIdcalichhen_IdAndDateAfter(idCa,LocalDate.now());
    }

    public List<Lichhen> listLichHomNay(){
        return lichhenRepository.findByDateAndTrangthaicaTrue(LocalDate.now());
    }

    public void ThemNgayNghi(LocalDate date){
        lichhenRepository.updateNgayNghi(date);
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

    public List<Lichhen> getListByDate(LocalDate date){
        return lichhenRepository.findByDate(date);
    }

    public Page<Lichhen> findAllLichWithTrangThai(Pageable pageable,boolean tt,LocalDate date){
        return lichhenRepository.findByTrangthaicaAndDateAfter(tt,date,pageable);
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

    void taoLichHenRong(){
        lichHenManager.taoLichHenRong();
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

    @Async
    public void sendEmailWithActions(Lichhen lichhen) {
        try {
            String Url = "http://localhost:3000/chi-tiet-lich/" + lichhen.getId();

            String message = "Chào bạn,\n\n"
                    + "Cảm ơn bạn đã đặt lịch."
                    + "Hẹn gặp bạn vào: " + lichhen.getIdcalichhen().getThoigianca()+ " Ngày: " +lichhen.getDate()
                    + " Bạn có thể sử dụng các liên kết dưới đây để quản lý lịch hẹn của mình:\n\n"
                    + "Chi tiết lịch hẹn: " + Url + "\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ hỗ trợ";

            emailService.sendEmail(lichhen.getEmailNguoiDat(),"Hủy hoặc đổi thời gian lịch",message);
            System.out.println("Email đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }

    private AtomicBoolean isCancelled = new AtomicBoolean(false);  // Biến flag để kiểm tra

    public void cancelScheduleChange() {
        isCancelled.set(true);  // Đặt flag hủy thành true
    }

    // Sau 20p tự động đổi trạng thái thành hủy
    @PersistenceContext
    private EntityManager entityManager;

    @Async
    public CompletableFuture<ResponseEntity<String>> scheduleTrangThaiChange(Integer lichhenId) {
        try {
            if (isCancelled.get()) {
                System.out.println("Tiến trình bị hủy.");
                return CompletableFuture.completedFuture(ResponseEntity.ok().build()); // Nếu tiến trình bị hủy thì kết thúc
            }
            Thread.sleep(2 * 60 * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build()); // Ngừng xử lý nếu bị gián đoạn
        }

        // Xóa cache Hibernate để chắc chắn lấy dữ liệu từ DB
        entityManager.clear();

        Optional<Lichhen> lichhenOptional = lichhenRepository.findById(lichhenId);
        if (!lichhenOptional.isPresent()) {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
        Lichhen lichhen1 = lichhenOptional.get();

        if (lichhen1.getTrangthai() == 4) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String idUser = authentication.getName();
            Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(lichhen1.getId());
            Optional<Lichhen> lichhenOptional1 = lichhenRepository.findById(lichhen1.getId());
            if (!lichhenOptional1.isPresent()) {
                return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
            }
            Lichhen lichhen = lichhenOptional1.get();

            // Check xem có phải chủ lịch không
            if (!lichhen.getIdkhachhang().equalsIgnoreCase(idUser)) {
                return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
            }


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
                lichhenRepository.save(lichhenNew);

                // Hủy hóa đơn chờ
                Hoadon hoadonNew = hoadonOptional.get();
                hoaDonService.deleteHoadonById(hoadonNew.getId());

                // Cập nhật lịch gốc với trạng thái đã hủy
                lichhen.setIdkhachhang("demo");
                lichhen.setTrangthai(5);
                lichhen.setEmailNguoiDat("default-email@example.com");
                if (lichhen.getTrangthaica()) {
                    lichhen.setTrangthaica(false);
                } else {
                    return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
                }
                lichhenRepository.save(lichhen);
                cancelScheduleChange();
                return CompletableFuture.completedFuture(ResponseEntity.ok("Lịch hẹn đã được hủy thành công."));
            }

        return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lịch hẹn không thể hủy vì trạng thái không hợp lệ."));
    }


    @Scheduled(fixedRate = 60000)  // Mỗi phút
    public void checkAppointments() {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Truy vấn các ca lịch hẹn trong cùng ngày và với giờ cụ thể
        List<Lichhen> lichhens = lichhenRepository.findByDate(currentDate);

        for (Lichhen lichhen : lichhens) {
            LocalTime appointmentTime = lichhen.getIdcalichhen().getThoigianca();

            // Tính toán sự chênh lệch giữa thời gian hiện tại và thời gian ca lịch hẹn
            Duration duration = Duration.between(currentTime, appointmentTime);

            //gửi thông báo
            if (duration.toMinutes() <= 60 && duration.toMinutes() > 0 && lichhen.getSolannhacnho()<1 && !lichhen.getEmailNguoiDat().equalsIgnoreCase("default-email@example.com")) {
                sendEmailNhacNho(lichhen,String.valueOf(duration.toMinutes()));
                lichhen.setSolannhacnho(lichhen.getSolannhacnho()+1);
                lichhenRepository.save(lichhen);
            }
        }
    }

    @Async
    public void sendEmailNhacNho(Lichhen lichhen,String gio) {
        try {
            String message = "Chào bạn,\n\n"
                    + "Cảm ơn bạn đã đặt lịch."
                    + "Còn" + gio + " phút nữa là đến lịch hẹn của bạn "
                    + "Xin hãy sắp xếp thời gian , lịch hẹn của bạn sẽ bắt đầu lúc: " + lichhen.getDate() + ' ' + lichhen.getIdcalichhen().getThoigianca();

            emailService.sendEmail(lichhen.getEmailNguoiDat(),"Nhắc nhở lịch hẹn",message);
            System.out.println("Email nhắc nhở đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }
}
