//작업자 : 한수민

package com.app.sketchbook.user.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.app.sketchbook.user.exception.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    //경로 지정
    public FileStorageService() {
        this.fileStorageLocation = Paths.get("C:/images/").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 불가", e);
        }
    }

    //파일 저장
    public String storeFile(MultipartFile file, Long userId, String fileType) {
        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if(fileExtension==null || !isAllowedFileType(fileExtension)) {
            throw new FileStorageException("파일 확장자는 JPG, JPEG, PNG만 가능합니다.");
        }
        String fileName = userId + "_" + fileType + "_" + System.nanoTime() + "." + fileExtension;
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/images/" + fileName + "?timestamp=" + System.currentTimeMillis();
        } catch (IOException e) {
            throw new FileStorageException("파일 저장 불가");
        }
    }

    //파일 확장자가 허용된 형식인지 확인
    public boolean isAllowedFileType(String fileExtension) {
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png");
    }
}