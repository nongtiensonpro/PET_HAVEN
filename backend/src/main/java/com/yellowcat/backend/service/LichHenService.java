package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Lichhen;
import com.yellowcat.backend.repository.LichhenRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LichHenService {

    private final LichhenRepository lichhenRepository;

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

    public Lichhen addOrUpdate(Lichhen lichhen){
        return lichhenRepository.save(lichhen);
    }

    public Lichhen findById(Integer id){
        return lichhenRepository.findById(id).orElse(null);
    }

}
