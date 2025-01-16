package com.yellowcat.backend.service;

import com.paypal.api.payments.Refund;
import com.paypal.base.rest.PayPalRESTException;
import com.yellowcat.backend.PAY.PayPalService;
import com.yellowcat.backend.model.Hoadon;
import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.LichhenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
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
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
@EnableAsync
@EnableScheduling
public class LichHenService {

    private final LichhenRepository lichhenRepository;
    private final LichHenManager lichHenManager;
    private final EmailService emailService;
    private final HoaDonService hoaDonService;
    private final PayPalService payPalService;

    public LichHenService(LichhenRepository lichhenRepository, LichHenManager lichHenManager, EmailService emailService, HoaDonService hoaDonService, PayPalService payPalService) {
        this.lichhenRepository = lichhenRepository;
        this.lichHenManager = lichHenManager;
        this.emailService = emailService;
        this.hoaDonService = hoaDonService;
        this.payPalService = payPalService;
    }

    public List<Lichhen> getAllLich() {
        return lichhenRepository.findAll();
    }

    public List<Lichhen> getAllPageLichHen() {
        return lichhenRepository.findByTrangthaicaTrueAndEmailNguoiDatNot("default-email@example.com");
    }

    public Optional<Lichhen> getLichHenByDateandCa(LocalDate date, Integer idCa) {
        return lichhenRepository.findByDateAndIdcalichhen_IdAndTrangthai(date, idCa, 5);
    }

    public List<Lichhen> getLichhenByDateAndIdCa(Integer idCa) {
        return lichhenRepository.findByIdcalichhen_IdAndDateAfter(idCa, LocalDate.now());
    }

    public List<Lichhen> listLichHomNay() {
        return lichhenRepository.findByDateAndTrangthaicaTrue(LocalDate.now());
    }

    public void ThemNgayNghi(LocalDate date) {
        lichhenRepository.updateNgayNghi(date);
    }

    public Page<Lichhen> findByIdUser(Pageable pageable, String idUser) {
        return lichhenRepository.findByIdkhachhang(idUser, pageable);
    }

    public Page<Lichhen> findByEmailNguoiDat(Pageable pageable, String userName) {
        return lichhenRepository.findByEmailNguoiDat(userName, pageable);
    }

    // Kiểm tra ca đã có trong ngày chưa
    public boolean isCaTrungTrongNgay(LocalDate ngay, int idCaLichHen) {
        return lichhenRepository.existsByDateAndIdcalichhen_Id(ngay, idCaLichHen);
    }

    public Lichhen addOrUpdate(Lichhen lichhen) {
        return lichhenRepository.save(lichhen);
    }

    public Lichhen findById(Integer id) {
        return lichhenRepository.findById(id).orElse(null);
    }

    public List<Lichhen> getListByDate(LocalDate date) {
        return lichhenRepository.findByDate(date);
    }

    public Page<Lichhen> findAllLichWithTrangThai(Pageable pageable, boolean tt, LocalDate date) {
        return lichhenRepository.findByTrangthaicaAndDateAfter(tt, date, pageable);
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

    void taoLichHenRong() {
        lichHenManager.taoLichHenRong();
    }

    @Scheduled(cron = "0 59 23 * * ?") // Chạy vào 23:59 mỗi ngày
    @Transactional
    public void xoaLichHenRongCuoiNgay() {
        xoaLichHenRong();
        System.out.println("Đã xóa lịch hẹn rỗng vào cuối ngày.");
        hoanTienLichKoDung();
        System.out.println("Đã hoàn tiền những lịch hẹn ko sử dụng vào cuối ngày.");
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
                    + "Hẹn gặp bạn vào: " + lichhen.getIdcalichhen().getThoigianca() + " Ngày: " + lichhen.getDate()
                    + "Hãy thanh toán ngay để giữ chỗ cho lịch hẹn này nhé !!!"
                    + " Bạn có thể sử dụng các liên kết dưới đây để quản lý lịch hẹn của mình:\n\n"
                    + "Chi tiết lịch hẹn: " + Url + "\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ hỗ trợ";

            emailService.sendEmail(lichhen.getEmailNguoiDat(), "Hủy hoặc đổi thời gian lịch", message);
            System.out.println("Email đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }

    private AtomicBoolean isCancelled = new AtomicBoolean(false);  // Biến flag để kiểm tra

    public void cancelScheduleChange() {
        isCancelled.set(true);  // Đặt flag hủy thành true
    }

    private ConcurrentHashMap<Integer, Integer> scheduleMap = new ConcurrentHashMap<>();

    public void updateScheduleId(Integer oldId, Integer newId) {
        scheduleMap.putIfAbsent(oldId, oldId);
        if (scheduleMap.containsKey(oldId)) {
            scheduleMap.clear();
            scheduleMap.put(oldId, newId);
            System.out.println(scheduleMap);
        }


    }

    // Sau 20p tự động đổi trạng thái thành hủy
    @PersistenceContext
    private EntityManager entityManager;

    @Async
    public CompletableFuture<ResponseEntity<String>> scheduleTrangThaiChange(Integer lichhenId) {
//        scheduleMap.putIfAbsent(lichhenId, lichhenId);
        Integer currentId;
        try {
            if (isCancelled.get()) {
                System.out.println("Tiến trình bị hủy.");
                return CompletableFuture.completedFuture(ResponseEntity.ok().build()); // Nếu tiến trình bị hủy thì kết thúc
            }
            Thread.sleep( 20* 60 * 1000);
            // Lấy ID mới từ Map nếu có
            currentId = scheduleMap.getOrDefault(lichhenId, lichhenId);
            System.out.println(currentId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().build()); // Ngừng xử lý nếu bị gián đoạn
        }

        // Xóa cache Hibernate để chắc chắn lấy dữ liệu từ DB
        entityManager.clear();

        Optional<Lichhen> lichhenOptional = lichhenRepository.findById(currentId);
        if (!lichhenOptional.isPresent()) {
            return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
        }
        Lichhen lichhen1 = lichhenOptional.get();
        System.out.println(1);
        if (lichhen1.getTrangthai() == 4) {
            System.out.println(2);
            Optional<Hoadon> hoadonOptional = hoaDonService.finHoadonByIdLich(lichhen1.getId());
            Optional<Lichhen> lichhenOptional1 = lichhenRepository.findById(lichhen1.getId());
            if (!lichhenOptional1.isPresent()) {
                return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
            }
            Lichhen lichhen = lichhenOptional1.get();

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
            lichhenNew.setDoidichvu(false);

            // Cập nhật lịch gốc với trạng thái đã hủy
            lichhen.setIdkhachhang("demo");
            lichhen.setTrangthai(5);
            lichhen.setEmailNguoiDat("default-email@example.com");
            lichhen.setThucung(null);
            lichhen.setTuyChonCanNang(null);
            lichhen.setDoidichvu(false);
            if (lichhen.getTrangthaica()) {
                lichhen.setTrangthaica(false);
            } else {
                return CompletableFuture.completedFuture(ResponseEntity.badRequest().build());
            }

            lichhenRepository.save(lichhenNew);
            lichhenRepository.save(lichhen);
            if (hoadonOptional.isPresent()){
                System.out.println(3);
                if (hoadonOptional.get().getTrangthai() == 1){
                    Hoadon hoadon = hoadonOptional.get();
                    hoadon.setIdlichhen(lichhenNew);
                    hoadon.setTrangthai(3);
                    hoaDonService.addOrUpdate(hoadon);
                }
            }
            return CompletableFuture.completedFuture(ResponseEntity.ok("Lịch hẹn đã được hủy thành công."));
        } else if (lichhen1.getTrangthai() == 6) {
            System.out.println(4);
            Optional<Lichhen> lichhenOptional1 = lichhenRepository.findById(lichhen1.getId());
            if (!lichhenOptional1.isPresent()) {
                return CompletableFuture.completedFuture(ResponseEntity.notFound().build());
            }
            Lichhen lichhen = lichhenOptional1.get();
            lichhen.setTrangthai(8); //Chờ sử dụng
            lichhenRepository.save(lichhen);
            return CompletableFuture.completedFuture(ResponseEntity.ok("Lịch hẹn đã được đổi thành chờ sử dụng."));
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
            if (duration.toMinutes() <= 60 && duration.toMinutes() > 0 && lichhen.getThoigianhuy() == null && lichhen.getSolannhacnho() < 1 && !lichhen.getEmailNguoiDat().equalsIgnoreCase("default-email@example.com")) {
                sendEmailNhacNho(lichhen, String.valueOf(duration.toMinutes()));
                lichhen.setSolannhacnho(lichhen.getSolannhacnho() + 1);
                lichhenRepository.save(lichhen);
            }
        }
    }

    @Async
    public void sendEmailNhacNho(Lichhen lichhen, String gio) {
        try {
            String message = "Chào bạn,<br><br>"
                    + "Cảm ơn bạn đã đặt lịch.<br><br>"
                    + "Còn " + gio + " phút nữa là đến lịch hẹn của bạn. "
                    + "Xin hãy sắp xếp thời gian, lịch hẹn của bạn sẽ bắt đầu lúc: "
                    + lichhen.getDate() + " " + lichhen.getIdcalichhen().getThoigianca() + "<br><br>"
                    + "Nếu gặp bất kỳ vấn đề gì vui lòng liên hệ fanpage hoặc sdt: 0906194201.<br><br>"
                    + "Nếu trong hôm nay bạn không check-in hoặc liên hệ với chúng tôi, "
                    + "lịch của bạn sẽ bị hủy và được hoàn 80% giá trị hóa đơn nếu đã thanh toán.<br><br>";

            emailService.sendEmail(lichhen.getEmailNguoiDat(), "Nhắc nhở lịch hẹn", message);
            System.out.println("Email nhắc nhở đã được gửi thành công.");
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }

    private void hoanTienLichKoDung() {
        try {
            List<Hoadon> hoadonList = hoaDonService.getList();
            if (!hoadonList.isEmpty()) {
                for (Hoadon hoadon : hoadonList) {
                    // Kiểm tra nếu trạng thái là 8 (Chờ sử dụng) và có ngày đặt trùng với hôm nay thì sẽ hoàn tiền
                    if (hoadon.getIdlichhen().getTrangthai() == 8 && hoadon.getIdlichhen().getDate().equals(LocalDate.now())) {
                        String magiaodich = hoadon.getMagiaodich();
                        Double sotien = hoadon.getSotien();

                        if (magiaodich == null || magiaodich.isEmpty()) {
                            System.err.println("Mã giao dịch không hợp lệ cho hóa đơn ID: " + hoadon.getId());
                            continue;
                        }
                        if (sotien == null || sotien <= 0) {
                            System.err.println("Số tiền không hợp lệ cho hóa đơn ID: " + hoadon.getId());
                            continue;
                        }

                        // Thực hiện refund qua PayPal
                        try {
                            Refund refund = payPalService.refundPayment(magiaodich,  Math.round(sotien * 80 / 100 * 100.0) / 100.0, "USD");
                            if ("completed".equals(refund.getState())) {
                                // Cập nhật trạng thái hóa đơn
                                hoadon.setTrangthai(3); // Thất bại
                                hoaDonService.addOrUpdate(hoadon);

                                // Cập nhật trạng thái lịch hẹn
                                Lichhen lichhen = hoadon.getIdlichhen();
                                lichhen.setTrangthai(7); // Đã hoàn tiền
                                lichhenRepository.save(lichhen);

                                System.out.println("Hoàn tiền thành công cho hóa đơn ID: " + hoadon.getId());
                            } else {
                                System.err.println("Hoàn tiền không thành công, trạng thái: " + refund.getState());
                            }
                        } catch (PayPalRESTException e) {
                            System.err.println("Lỗi khi hoàn tiền qua PayPal cho hóa đơn ID: " + hoadon.getId());
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi xảy ra khi xử lý hoàn tiền: ");
            e.printStackTrace();
        }
    }

}
