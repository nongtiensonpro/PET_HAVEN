package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Giamgia;
import com.yellowcat.backend.repository.GiamgiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Giamgia addOrUpdate(Giamgia giamgia){
        return giamgiaRepository.save(giamgia);
    }


    public List<Giamgia> findGiamGiaTheoNgayHienTai(){
        return giamgiaRepository.findByNgaybatdauLessThanEqualAndNgayketthucGreaterThanEqualAndTrangthai(LocalDate.now(),LocalDate.now(),true);
    }

    public Optional<Giamgia> findById(Integer id){
        return giamgiaRepository.findById(id);
    }

}
