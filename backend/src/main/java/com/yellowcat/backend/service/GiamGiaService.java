package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.repository.GiamgiaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class GiamGiaService {

    private final GiamgiaRepository giamgiaRepository;

    public GiamGiaService(GiamgiaRepository giamgiaRepository) {
        this.giamgiaRepository = giamgiaRepository;
    }


    public List<Giamgia> getGiamgia() {
        return giamgiaRepository.findAll();
    }

    public Giamgia addOrUpdate(Giamgia giamgia) {
        List<Giamgia> giamgiaList = giamgiaRepository.findAll();
        if (giamgiaList.isEmpty()) {
            return giamgiaRepository.save(giamgia); // Lưu mới nếu danh sách rỗng
        } else {
            LocalDate ngaybd = giamgia.getNgaybatdau();
            LocalDate ngaykt = giamgia.getNgayketthuc();

            for (Giamgia s : giamgiaList) {
                // Kiểm tra nếu ngày có xung đột
                if ((ngaybd.isBefore(s.getNgayketthuc()) && ngaykt.isAfter(s.getNgaybatdau()))
                        || ngaybd.isEqual(s.getNgaybatdau()) || ngaykt.isEqual(s.getNgayketthuc())) {
                    throw new IllegalArgumentException("Khoảng thời gian bị trùng với mã giảm giá khác");
                }
            }
        }
        // Lưu mới hoặc cập nhật nếu không có xung đột
        return giamgiaRepository.save(giamgia);
    }

    public List<Giamgia> findGiamGiaTheoNgayHienTai(){
        return giamgiaRepository.findByNgaybatdauLessThanEqualAndNgayketthucGreaterThanEqualAndTrangthai(LocalDate.now(),LocalDate.now(),true);
    }

    public Optional<Giamgia> findById(Integer id){
        return giamgiaRepository.findById(id);
    }

}
