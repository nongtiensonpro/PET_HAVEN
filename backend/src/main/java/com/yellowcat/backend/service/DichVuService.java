package com.yellowcat.backend.service;


import com.yellowcat.backend.DTO.DichVuDTO;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.model.TuyChonCanNang;
import com.yellowcat.backend.model.TuyChonDichVu;
import com.yellowcat.backend.repository.DichvuRepository;
import com.yellowcat.backend.repository.TuyChonCanNangRepository;
import com.yellowcat.backend.repository.TuyChonDichVuRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DichVuService {

    private final DichvuRepository dichvuRepository;
    private final TuyChonCanNangRepository tuyChonCanNangRepository;
    private final TuyChonDichVuRepository tuyChonDichVuRepository;

    public DichVuService(DichvuRepository dichvuRepository, TuyChonCanNangRepository tuyChonCanNangRepository, TuyChonDichVuRepository tuyChonDichVuRepository) {
        this.dichvuRepository = dichvuRepository;
        this.tuyChonCanNangRepository = tuyChonCanNangRepository;
        this.tuyChonDichVuRepository = tuyChonDichVuRepository;
    }


    public Page<Dichvu> getAllDichVu(Pageable pageable) {
        return dichvuRepository.findAll(pageable);
    }

    public List<Dichvu> getAll(){
        return dichvuRepository.findAll();
    }

    public Dichvu addOrUpdateDichVu(Dichvu dichvu) {
        return dichvuRepository.save(dichvu);
    }
    public Optional<Dichvu> findById(int id) {
        return dichvuRepository.findById((long) id);
    }

    public void deleteDichVu(int id) {
        dichvuRepository.deleteById((long) id);
    }

    public Page<Dichvu> FindByNameDV(String tenDV,Pageable pageable){
        return dichvuRepository.findByTendichvuContainingIgnoreCase(tenDV,pageable);
    }
    public List<Dichvu> getListTrangThaiTrue(){return dichvuRepository.findByTrangthaiTrue();}

    @Transactional
    public void themDichVu(DichVuDTO request) {
        // Thêm dịch vụ
        Dichvu dichVu = new Dichvu();
        dichVu.setTendichvu(request.getTenDichVu());
        dichVu.setMota(request.getMoTa());
        dichVu.setAnh(request.getAnh());
        dichVu.setTrangthai(request.isTrangThai());
        dichVu.setHien(true);
        dichVu = dichvuRepository.save(dichVu);

        // Thêm tùy chọn dịch vụ
        for (DichVuDTO.TuyChonDichVuRequest tcRequest : request.getTuyChonDichVu()) {
            TuyChonDichVu tuyChon = new TuyChonDichVu();
            tuyChon.setDichvu(dichVu);
            tuyChon.setTentuychon(tcRequest.getTenTuyChon());
            tuyChon.setMota(tcRequest.getMoTa());
            tuyChon.setTrangthai(tcRequest.isTrangThai());
            tuyChon = tuyChonDichVuRepository.save(tuyChon);

            // Thêm tùy chọn cân nặng
            for (DichVuDTO.TuyChonDichVuRequest.TuyChonCanNangRequest cnRequest : tcRequest.getTuyChonCanNang()) {
                TuyChonCanNang canNang = new TuyChonCanNang();
                canNang.setTuyChonDichVu(tuyChon);
                canNang.setCannangmin(cnRequest.getCanNangMin());
                canNang.setCannangmax(cnRequest.getCanNangMax());
                canNang.setGiatien(cnRequest.getGiaTien());
                canNang.setTrangthai(cnRequest.isTrangThai());
                tuyChonCanNangRepository.save(canNang);
            }
        }
    }

    @Transactional
    public void updateDichVu(Long idDichVu, DichVuDTO request) {
        // Tìm dịch vụ cần cập nhật
        Dichvu dichVu = dichvuRepository.findById(idDichVu)
                .orElseThrow(() -> new RuntimeException("Dịch vụ không tồn tại!"));

        // Cập nhật thông tin dịch vụ
        dichVu.setTendichvu(request.getTenDichVu());
        dichVu.setMota(request.getMoTa());
        dichVu.setAnh(request.getAnh());
        dichVu.setTrangthai(request.isTrangThai());
        dichVu.setHien(request.isHien());
        dichvuRepository.save(dichVu);

        // Xử lý các tùy chọn dịch vụ
        for (DichVuDTO.TuyChonDichVuRequest tcRequest : request.getTuyChonDichVu()) {
            TuyChonDichVu tuyChon;
            if (tcRequest.getId() != null) {
                // Cập nhật tùy chọn nếu đã tồn tại
                tuyChon = tuyChonDichVuRepository.findById(tcRequest.getId())
                        .orElseThrow(() -> new RuntimeException("Tùy chọn dịch vụ không tồn tại!"));
            } else {
                // Tạo mới tùy chọn
                tuyChon = new TuyChonDichVu();
                tuyChon.setId(dichVu.getId());
            }

            tuyChon.setTentuychon(tcRequest.getTenTuyChon());
            tuyChon.setMota(tcRequest.getMoTa());
            tuyChon.setTrangthai(tcRequest.isTrangThai());
            tuyChon = tuyChonDichVuRepository.save(tuyChon);

            // Xử lý các tùy chọn cân nặng
            for (DichVuDTO.TuyChonDichVuRequest.TuyChonCanNangRequest cnRequest : tcRequest.getTuyChonCanNang()) {
                TuyChonCanNang canNang;
                if (cnRequest.getId() != null) {
                    // Cập nhật cân nặng nếu đã tồn tại
                    canNang = tuyChonCanNangRepository.findById(cnRequest.getId())
                            .orElseThrow(() -> new RuntimeException("Tùy chọn cân nặng không tồn tại!"));
                } else {
                    // Tạo mới tùy chọn cân nặng
                    canNang = new TuyChonCanNang();
                    canNang.setId(tuyChon.getId());
                }

                canNang.setCannangmin(cnRequest.getCanNangMin());
                canNang.setCannangmax(cnRequest.getCanNangMax());
                canNang.setGiatien(cnRequest.getGiaTien());
                canNang.setTrangthai(cnRequest.isTrangThai());
                tuyChonCanNangRepository.save(canNang);
            }
        }
    }
}
