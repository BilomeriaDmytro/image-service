package com.test.imageservice.service;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.Image;
import com.test.imageservice.models.TagContainer;
import com.test.imageservice.models.dto.ImageSearchDTO;
import com.test.imageservice.presentation.exception.ForbiddenException;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImageService {

    Image saveNewImage(MultipartFile file, String reference, Account account) throws InvalidContentTypeException;

    Image getImage(Long id) throws NotFoundException;

    List<Image> getImages(Account account);

    Image updateImage(Long id, String imageName, Account account) throws Exception;

    Image addTags(Long id, TagContainer tagContainer, Account account) throws NotFoundException, ForbiddenException;

    void deleteImage(Long id, Account account) throws ForbiddenException, NotFoundException;

    void deleteAccountImages(Account account);

    Page<Image> searchByAllProperties(ImageSearchDTO imageSearchDTO, int page, int size) throws InputException;
}