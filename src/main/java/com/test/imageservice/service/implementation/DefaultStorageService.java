package com.test.imageservice.service.implementation;

import com.test.imageservice.models.Account;
import com.test.imageservice.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DefaultStorageService implements StorageService {

    public String save(MultipartFile file, Account account){
        return "storage/account-"+ account.getId() + "/" + Math.random() + "_" + file.getOriginalFilename();
    }

    public void delete(Long id){

    }
}