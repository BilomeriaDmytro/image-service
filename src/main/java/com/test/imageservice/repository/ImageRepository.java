package com.test.imageservice.repository;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByAccount(Account account);

    void deleteByAccount(Account account);
}