package com.memoryshade.global.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

  @Value("${file.upload-dir}")
  private String uploadDir;

  @Override
  public String uploadImage(MultipartFile file) {
    try {
      if (file == null || file.isEmpty()) {
        throw new IllegalArgumentException("업로드할 파일이 비어 있습니다.");
      }

      String originalFilename = file.getOriginalFilename();
      log.info("파일 업로드 시작 - originalFilename={}, uploadDir={}", originalFilename, uploadDir);

      String extension = getExtension(originalFilename);
      String storedFilename = UUID.randomUUID() + "." + extension;

      Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir).toAbsolutePath().normalize();
      Files.createDirectories(uploadPath);

      Path targetPath = uploadPath.resolve(storedFilename);
      file.transferTo(targetPath.toFile());

      log.info("파일 업로드 완료 - savedPath={}", targetPath);

      return "/uploads/" + storedFilename;

    } catch (IOException e) {
      log.error("파일 업로드 실패 - IO 예외", e);
      throw new RuntimeException("파일 업로드에 실패했습니다.", e);
    } catch (Exception e) {
      log.error("파일 업로드 실패 - 예외 발생", e);
      throw new RuntimeException("파일 업로드에 실패했습니다.", e);
    }
  }

  private String getExtension(String filename) {
    if (!StringUtils.hasText(filename) || !filename.contains(".")) {
      return "jpg";
    }

    return filename.substring(filename.lastIndexOf('.') + 1);
  }
}