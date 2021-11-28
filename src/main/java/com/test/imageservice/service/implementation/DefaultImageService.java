package com.test.imageservice.service.implementation;

import com.test.imageservice.dao.implementation.DefaultImageDAO;
import com.test.imageservice.models.Account;
import com.test.imageservice.models.Image;
import com.test.imageservice.models.Tag;
import com.test.imageservice.models.TagContainer;
import com.test.imageservice.models.dto.ImageSearchDTO;
import com.test.imageservice.presentation.exception.ForbiddenException;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import com.test.imageservice.repository.ImageRepository;
import com.test.imageservice.service.ImageService;
import com.test.imageservice.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class DefaultImageService implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private DefaultImageDAO defaultImageDAO;

    @Override
    public Image saveNewImage(MultipartFile file, String reference, Account account)
            throws InvalidContentTypeException {

        String contentType = file.getContentType();

        if(contentType != null && contentType.contains("image/")){

            Image image = new Image();
            String imageName = file.getOriginalFilename();
            if (imageName!=null) {
                imageName = imageName.substring(0, imageName.lastIndexOf('.'));
            }
            image.setImageName(imageName);
            image.setContentType(file.getContentType());
            image.setSize(file.getSize());
            image.setReference(reference);
            image.setAccount(account);
            return imageRepository.save(image);
        }
        throw new InvalidContentTypeException();
    }

    @Override
    public void deleteImage(Long imageId, Account account)
            throws ForbiddenException, NotFoundException {

        final String DELETE_PROCESS_FAILED_WARN = "Deletion process failed for image with id - " + imageId + ". Cause: ";
        String message = "";

        Optional<Image> object = imageRepository.findById(imageId);
        if(object.isPresent()){

            Image image = object.get();
            if(imageBelongToUser(image, account)){

                imageRepository.delete(image);
                log.info("Image with id - {} successfully deleted", imageId);
            }else {
                message = "This user can not delete this image";
                log.warn(DELETE_PROCESS_FAILED_WARN + message);
                throw new ForbiddenException(message);
            }

        }else {
            message = "Image is not found";
            log.warn(DELETE_PROCESS_FAILED_WARN + message);
            throw new NotFoundException(message);
        }
    }


    @Override
    public List<Image> getImages(Account account) {

        return imageRepository.findByAccount(account);
    }

    @Override
    @Transactional
    public void deleteAccountImages(Account account) {

        imageRepository.deleteByAccount(account);
    }

    @Override
    public Image updateImage(Long imageId, String imageName, Account account)
            throws Exception {

            if (imageName.length() > 3) {
                Optional<Image> object = imageRepository.findById(imageId);
                if (object.isPresent()) {
                    Image image = object.get();
                    if (imageBelongToUser(image, account)) {
                        image.setImageName(imageName);
                        imageRepository.save(image);
                        return image;
                    }
                    throw new ForbiddenException("This user can not modify this image");
                }
                throw new NotFoundException("Image not found");
            }
            throw new InputException("Image name is too short");
    }


    @Override
    public Image addTags(Long id, TagContainer tagContainer, Account account)
            throws NotFoundException, ForbiddenException {

        Optional<Image> object = imageRepository.findById(id);
        if(object.isPresent()){
            Image image =  object.get();

            if(imageBelongToUser(image, account)){

                List<Tag> tags = new ArrayList<>();
                for(String tagName : tagContainer.getTagNames()){
                    Tag tag = tagService.getTagByName(tagName);
                    if(tag == null){
                        tag = new Tag();
                        tag.setTagName(tagName);
                        tagService.save(tag);
                    }
                    tags.add(tag);
                }
                image.getTags().addAll(tags);

                return imageRepository.save(image);
            }
            throw  new ForbiddenException("User cant update this image");
        }
        throw new NotFoundException("Image not found");
    }

    @Override
    public Image getImage(Long imageId)
            throws NotFoundException {

        Optional<Image> object = imageRepository.findById(imageId);
        if(object.isPresent()){

            return object.get();
        }
        throw new NotFoundException("Image not found");
    }

    @Override
    public Page<Image> searchByAllProperties(ImageSearchDTO imageSearchDTO, int page, int size)
            throws InputException {

        Pageable pageable = PageRequest.of(page, size);
        return defaultImageDAO.searchByAllProperties(imageSearchDTO, pageable);
    }

    private boolean imageBelongToUser(Image image, Account account) {

        return Objects.equals(image.getAccount(), account);
    }
}