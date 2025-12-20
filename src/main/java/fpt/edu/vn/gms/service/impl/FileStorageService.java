package fpt.edu.vn.gms.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {

    private static final String DIR = "uploads/stock-receipts/";

    public String upload(MultipartFile file) {
        try {
            if (file == null || file.isEmpty())
                return null;

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(DIR);

            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            String url = "/files/" + fileName;
            log.info("[UPLOAD] Saved file: {}", url);
            return url;

        } catch (Exception ex) {
            log.error("[UPLOAD] Failed", ex);
            throw new RuntimeException("Cannot upload file", ex);
        }
    }

    public Resource download(String fileName) {
        try {
            if (fileName == null || fileName.isEmpty()) {
                throw new IllegalArgumentException("File name cannot be empty");
            }

            // Lấy tên file từ URL nếu có (ví dụ: /files/1234567890_file.pdf ->
            // 1234567890_file.pdf)
            String actualFileName = fileName;
            if (fileName.contains("/")) {
                actualFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }

            Path filePath = Paths.get(DIR).resolve(actualFileName).normalize();

            if (!Files.exists(filePath)) {
                log.error("[DOWNLOAD] File not found: {}", filePath);
                throw new RuntimeException("File not found: " + actualFileName);
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                log.error("[DOWNLOAD] File is not readable: {}", filePath);
                throw new RuntimeException("File is not readable: " + actualFileName);
            }

            log.info("[DOWNLOAD] Serving file: {}", actualFileName);
            return resource;

        } catch (Exception ex) {
            log.error("[DOWNLOAD] Failed to download file: {}", fileName, ex);
            throw new RuntimeException("Cannot download file: " + fileName, ex);
        }
    }
}
