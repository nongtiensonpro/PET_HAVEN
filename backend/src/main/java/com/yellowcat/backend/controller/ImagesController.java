package com.yellowcat.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/images")
public class ImagesController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Làm sạch tên file
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            // Kiểm tra tính hợp lệ của tên file
            if (!isValidFileName(fileName)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name.");
            }

            // Giới hạn loại file (ví dụ: chỉ cho phép file hình ảnh)
            String contentType = file.getContentType();
            if (!isSupportedContentType(contentType)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported file type.");
            }

            // Tạo đường dẫn file
            Path copyLocation = Paths.get(uploadDir + File.separator + fileName);

            // Lưu file vào thư mục static
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload the file.");
        }
    }

    // Hàm kiểm tra tính hợp lệ của tên file
    private boolean isValidFileName(String fileName) {
        // Loại bỏ các tên file chứa ".." và chỉ cho phép các ký tự hợp lệ
        return fileName != null && !fileName.contains("..") && fileName.matches("[a-zA-Z0-9._-]+");
    }

    // Hàm kiểm tra loại file được hỗ trợ
    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/gif");
    }
}
