package com.yellowcat.backend.controller;


import com.yellowcat.backend.model.Dichvu;
import com.yellowcat.backend.service.DichVuService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/api/dich-vu")
public class DichVuController {
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DichVuService dichVuService;

    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }

    @RequestMapping("/all")
    public Page<Dichvu> getAllDichVu(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10); // 10 items per page
        return dichVuService.getAllDichVu(pageable);
    }

    //Hàm kiểm tra tên file
    public boolean isValidFileName(String fileName) {
        // Kiểm tra tên file không chứa các ký tự như "..", "/", "\", và chỉ chứa ký tự hợp lệ
        return fileName != null && !fileName.contains("..") && fileName.matches("[a-zA-Z0-9._-]+");
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PostMapping("/add")
    public ResponseEntity<String> createDichVu(
            @RequestParam("tenDichVu") String tenDichVu,
            @RequestParam("moTa") String moTa,
            @RequestParam("giaTien") Integer giaTien,
            @RequestParam("trangThai") Boolean trangThai,
            @RequestParam("file") MultipartFile file) {

        try {
            // Xác thực tên file và loại bỏ các ký tự nguy hiểm
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (!isValidFileName(fileName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên file không hợp lệ.");
            }

            // Kiểm tra kiểu file
            String contentType = file.getContentType();
            if (!Arrays.asList("image/png", "image/jpeg", "image/gif").contains(contentType)) {
                throw new SecurityException("Kiểu file không hợp lệ: " + contentType);
            }

            // Lưu file vào thư mục static
            Path copyLocation = Paths.get(uploadDir + File.separator + fileName);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            // Tạo URL ảnh
            String imageUrl = "http://localhost:8080/images/AvatarDichVu/" + fileName;

            // Tạo đối tượng Dichvu
            Dichvu dichvu = new Dichvu();
            dichvu.setTendichvu(tenDichVu);
            dichvu.setMota(moTa);
            dichvu.setGiatien(giaTien);
            dichvu.setTrangthai(trangThai);
            dichvu.setAnh(imageUrl);

            // Lưu DichVu vào database
            dichVuService.addOrUpdateDichVu(dichvu);

            return ResponseEntity.ok("Dịch vụ tạo thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể tạo dịch vụ.");
        }
    }



    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateone( @RequestParam("tenDichVu") String tenDichVu,
                                             @RequestParam("moTa") String moTa,
                                             @RequestParam("giaTien") Integer giaTien,
                                             @RequestParam("trangThai") Boolean trangThai,
                                            @RequestParam(value = "file", required = false) MultipartFile file,
                                            @PathVariable int id) {
        Optional<Dichvu> dichvu1 = dichVuService.findById(id);

        // Kiểm tra xem dichvu1 có tồn tại không
        if (!dichvu1.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Dichvu dichvu2 = dichvu1.get();
        dichvu2.setTendichvu(tenDichVu);
        dichvu2.setMota(moTa);
        dichvu2.setGiatien(giaTien);

        // Kiểm tra nếu người dùng có upload ảnh mới
        if (file != null && !file.isEmpty()) {
            try {
                // Làm sạch tên file và kiểm tra
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                if (!isValidFileName(fileName)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tên file không hợp lệ.");
                }

                // Kiểm tra và giới hạn loại file
                String contentType = file.getContentType();
                if (!Arrays.asList("image/png", "image/jpeg", "image/gif").contains(contentType)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Kiểu file không được hỗ trợ.");
                }

                // Lưu file vào thư mục static
                Path copyLocation = Paths.get(uploadDir + File.separator + fileName);
                Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

                // Tạo URL ảnh mới
                String imageUrl = "http://localhost:8080/images/AvatarDichVu/" + fileName;
                dichvu2.setAnh(imageUrl);

            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể cập nhật ảnh.");
            }
        }

        dichVuService.addOrUpdateDichVu(dichvu2);
        return ResponseEntity.ok("Dịch vụ cập nhật thành công!");
    }



    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable int id) {
        dichVuService.deleteDichVu(id);
        return ResponseEntity.status(HttpStatus.OK).body("Dịch vụ đã được xóa thành công");

    }

    @GetMapping("/find")
    public ResponseEntity<Page<Dichvu>> findDichVu(@RequestParam String namedv,@RequestParam(defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page,10);
        Page<Dichvu> dichvus = dichVuService.FindByNameDV(namedv,pageable);
        return ResponseEntity.ok(dichvus);
    }

    @PreAuthorize("hasAnyRole('admin', 'manager')")
    @PutMapping("/update-trang-thai/{id}")
    public ResponseEntity<Dichvu> updateTrangThai(@PathVariable Integer id) {
        Optional<Dichvu> dichvu1 = dichVuService.findById(id);
        Dichvu dichvu = dichvu1.get();
        if (dichvu.getTrangthai()){
            dichvu.setTrangthai(false);
        }else {
            dichvu.setTrangthai(true);
        }
        dichVuService.addOrUpdateDichVu(dichvu);
        return ResponseEntity.ok(dichvu);
    }
}
