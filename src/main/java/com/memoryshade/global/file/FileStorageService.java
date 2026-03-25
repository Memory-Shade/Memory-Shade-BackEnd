package com.memoryshade.global.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  String uploadImage(MultipartFile file);
}