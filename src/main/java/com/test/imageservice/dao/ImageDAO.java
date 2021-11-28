package com.test.imageservice.dao;

import com.test.imageservice.models.Image;
import com.test.imageservice.models.dto.ImageSearchDTO;
import com.test.imageservice.presentation.exception.InputException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ImageDAO {

    Page<Image> searchByAllProperties(ImageSearchDTO imageSearchDTO, Pageable pageable) throws InputException;
}