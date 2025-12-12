package fpt.edu.vn.gms.service.impl;

import lombok.extern.slf4j.Slf4j;
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
            if (file == null || file.isEmpty()) return null;

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
}
