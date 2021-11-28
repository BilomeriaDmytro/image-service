package com.test.imageservice.service;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.Image;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String save(MultipartFile file, Account account);

    void delete(Long id);
}