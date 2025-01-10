package com.yellowcat.backend.service;

import com.yellowcat.backend.DTO.DichVuDTO;
import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.model.TuyChonCanNang;
import com.yellowcat.backend.model.TuyChonDichVu;
import com.yellowcat.backend.repository.DichvuRepository;
import com.yellowcat.backend.repository.TuyChonCanNangRepository;
import com.yellowcat.backend.repository.TuyChonDichVuRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DichVuService {

    private final DichvuRepository dichvuRepository;
    private final TuyChonCanNangRepository tuyChonCanNangRepository;
    private final TuyChonDichVuRepository tuyChonDichVuRepository;
    private final CloudinaryService cloudinaryService;

    public DichVuService(DichvuRepository dichvuRepository, TuyChonCanNangRepository tuyChonCanNangRepository, TuyChonDichVuRepository tuyChonDichVuRepository, CloudinaryService cloudinaryService) {
        this.dichvuRepository = dichvuRepository;
        this.tuyChonCanNangRepository = tuyChonCanNangRepository;
        this.tuyChonDichVuRepository = tuyChonDichVuRepository;
        this.cloudinaryService = cloudinaryService;
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

    public boolean isValidFileName(String fileName) {
        // Kiểm tra tên file không chứa các ký tự như "..", "/", "\", và chỉ chứa ký tự hợp lệ
        return fileName != null && !fileName.contains("..") && fileName.matches("[a-zA-Z0-9._-]+");
    }

    @Transactional
    public void themDichVu(DichVuDTO request, MultipartFile file) {
        try {
            String imageUrl;

            // Kiểm tra nếu người dùng không upload ảnh
            if (file == null || file.isEmpty()) {
                imageUrl = "http://localhost:8080/images/AvatarDichVu/default-avatar.jpg";
            } else {
                // Xác thực tên file và loại bỏ các ký tự nguy hiểm
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());

                if (!isValidFileName(fileName)) {
                    throw new IllegalArgumentException("Tên file không hợp lệ.");
                }

                // Tạo tệp tạm thời
                File tempFile = File.createTempFile("upload_", fileName);
                file.transferTo(tempFile);  // Chuyển MultipartFile thành File

                // Upload file lên Cloudinary
                Map<?, ?> uploadResult = cloudinaryService.uploadFile(tempFile);
                imageUrl = uploadResult.get("url").toString();

                // Xóa file tạm sau khi upload
                if (!tempFile.delete()) {
                    System.err.println("Không thể xóa file tạm.");
                }
            }

            // Thêm dịch vụ
            Dichvu dichVu = new Dichvu();
            dichVu.setTendichvu(request.getTenDichVu());
            dichVu.setMota(request.getMoTa());
            dichVu.setAnh(imageUrl); // Gắn URL của ảnh vào đây
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
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi xử lý file: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }


    @Transactional
    public void capNhatDichVu(int id, DichVuDTO request, MultipartFile file) {
        try {
            // 1. Tìm dịch vụ theo ID
            Dichvu dichVu = dichvuRepository.findById((long) id)
                    .orElseThrow(() -> new NotFoundException("Dịch vụ không tồn tại"));

            // 2. Cập nhật thông tin cơ bản
            dichVu.setTendichvu(request.getTenDichVu());
            dichVu.setMota(request.getMoTa());
            dichVu.setTrangthai(request.isTrangThai());

            // 3. Cập nhật ảnh nếu có file mới
            if (file != null && !file.isEmpty()) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                File tempFile = File.createTempFile("upload_", fileName);
                file.transferTo(tempFile);

                Map<?, ?> uploadResult = cloudinaryService.uploadFile(tempFile);
                dichVu.setAnh(uploadResult.get("url").toString());

                tempFile.delete();
            }

            // 4. Cập nhật hoặc thêm mới tùy chọn dịch vụ
            for (DichVuDTO.TuyChonDichVuRequest tcRequest : request.getTuyChonDichVu()) {
                TuyChonDichVu tuyChon = null;

                // Tìm tùy chọn dịch vụ theo ID
                if (tcRequest.getId() != null) {
                    tuyChon = tuyChonDichVuRepository.findById(tcRequest.getId())
                            .orElse(null);
                }

                // Nếu tồn tại, cập nhật thông tin
                if (tuyChon != null) {
                    tuyChon.setTentuychon(tcRequest.getTenTuyChon());
                    tuyChon.setMota(tcRequest.getMoTa());
                    tuyChon.setTrangthai(tcRequest.isTrangThai());
                } else {
                    // Nếu không tồn tại, tạo mới
                    tuyChon = new TuyChonDichVu();
                    tuyChon.setDichvu(dichVu);
                    tuyChon.setTentuychon(tcRequest.getTenTuyChon());
                    tuyChon.setMota(tcRequest.getMoTa());
                    tuyChon.setTrangthai(tcRequest.isTrangThai());
                    tuyChonDichVuRepository.save(tuyChon);
                }

                // 5. Cập nhật hoặc thêm mới tùy chọn cân nặng
                for (DichVuDTO.TuyChonDichVuRequest.TuyChonCanNangRequest cnRequest : tcRequest.getTuyChonCanNang()) {
                    TuyChonCanNang canNang = null;

                    // Tìm tùy chọn cân nặng theo ID
                    if (cnRequest.getId() != null) {
                        canNang = tuyChonCanNangRepository.findById(cnRequest.getId())
                                .orElse(null);
                    }

                    // Nếu tồn tại, cập nhật thông tin
                    if (canNang != null) {
                        canNang.setCannangmin(cnRequest.getCanNangMin());
                        canNang.setCannangmax(cnRequest.getCanNangMax());
                        canNang.setGiatien(cnRequest.getGiaTien());
                        canNang.setTrangthai(cnRequest.isTrangThai());
                    } else {
                        // Nếu không tồn tại, tạo mới
                        canNang = new TuyChonCanNang();
                        canNang.setTuyChonDichVu(tuyChon);
                        canNang.setCannangmin(cnRequest.getCanNangMin());
                        canNang.setCannangmax(cnRequest.getCanNangMax());
                        canNang.setGiatien(cnRequest.getGiaTien());
                        canNang.setTrangthai(cnRequest.isTrangThai());
                        tuyChonCanNangRepository.save(canNang);
                    }
                }
            }

            // 6. Lưu dịch vụ
            dichvuRepository.save(dichVu);

        } catch (IOException ex) {
            throw new RuntimeException("Lỗi xử lý file: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

}
