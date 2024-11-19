package com.yellowcat.backend.service;

import com.yellowcat.backend.model.Danhgia;
import com.yellowcat.backend.repository.DanhgiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DanhGiaService {
    @Autowired
    DanhgiaRepository DanhGiaRepository;

    public void addOrUpdateDanhGia(Danhgia danhgia) {
        DanhGiaRepository.save(danhgia);
    }

    public List<Danhgia> getAllDanhGiaTheoDichVu(Integer idDichVu) {
        return DanhGiaRepository.findByIdhoadon_Idlichhen_Dichvu_Id(idDichVu);
    }

    public Optional<Danhgia> getDanhGiaoTheoHD(Integer idHd ,String idTk){
        return DanhGiaRepository.findByIdhoadon_IdAndIdtaikhoan(idHd, idTk);
    }

    public Optional<Danhgia> getDanhGiaById(Integer id){
        return DanhGiaRepository.findById(id);
    }

}
