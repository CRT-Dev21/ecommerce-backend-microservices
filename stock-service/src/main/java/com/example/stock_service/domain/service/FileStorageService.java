package com.example.stock_service.domain.service;

import com.example.stock_service.model.ProductStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
public class FileStorageService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public Mono<String> storeProductImage(FilePart filePart, ProductStock product) {

        return Mono.using(
                () -> {
                    try {
                        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
                        Files.createDirectories(uploadPath);
                        return uploadPath;
                    } catch (IOException e) {
                        throw new RuntimeException("Error creating directories", e);
                    }
                },
                uploadPath -> {
                    String finalFileName = product.generateImageName();
                    String tempFileName = "temp_" + System.currentTimeMillis() +
                            getFileExtension(filePart.filename());

                    Path tempFilePath = uploadPath.resolve(tempFileName);
                    Path finalFilePath = uploadPath.resolve(finalFileName);

                    return filePart.transferTo(tempFilePath)
                            .then(Mono.fromCallable(() -> {
                                try {
                                    Files.move(tempFilePath, finalFilePath, StandardCopyOption.REPLACE_EXISTING);
                                    return finalFileName;
                                } catch (IOException e) {
                                    throw new RuntimeException("Error moving file", e);
                                }
                            }))
                            .doOnSuccess(name -> log.info("Image saved successfully: {}", name))
                            .doOnError(e -> log.error("Error storing image", e));
                },
                uploadPath -> {}
        ).onErrorResume(e -> {
            log.error("Complete error in storeProductImage", e);
            return Mono.error(e);
        });
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."));
    }
}
