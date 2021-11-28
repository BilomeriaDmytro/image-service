package com.test.imageservice.service.implementation;

import com.test.imageservice.dao.implementation.DefaultImageDAO;
import com.test.imageservice.models.Account;
import com.test.imageservice.models.Image;
import com.test.imageservice.models.Tag;
import com.test.imageservice.models.TagContainer;
import com.test.imageservice.presentation.exception.ForbiddenException;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import com.test.imageservice.repository.ImageRepository;
import com.test.imageservice.service.ImageService;
import com.test.imageservice.service.TagService;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
class DefaultImageServiceTest {

    @Autowired
    @InjectMocks
    private DefaultImageService defaultImageService;

    @MockBean
    private ImageRepository imageRepository;

    @MockBean
    private TagService tagService;

    @MockBean
    private DefaultImageDAO defaultImageDAO;

    @Test
    void saveNewImage() throws InvalidContentTypeException {

        String reference = "reference";
        MockMultipartFile file1 = new MockMultipartFile("file1", "newImage1.jpeg", String.valueOf(MediaType.IMAGE_JPEG), ("gfdsgfds".getBytes()));
        Account account = new Account();
        account.setUsername("user");

        when(imageRepository.save(any(Image.class))).thenAnswer(returnsFirstArg());

        Image image = defaultImageService.saveNewImage(file1, reference, account);

        assertEquals(image.getReference(), reference);
        assertEquals(image.getAccount(), account);
        assertEquals(image.getContentType(), file1.getContentType());
    }

    @Test
    void saveNewImageFailure_InvalidContentType() {

        String reference = "reference";
        MockMultipartFile file1 = new MockMultipartFile("file1", "newImage1.jpeg", String.valueOf(MediaType.APPLICATION_XML), ("gfdsgfds".getBytes()));
        Account account = new Account();
        account.setUsername("user");

        when(imageRepository.save(any(Image.class))).thenAnswer(returnsFirstArg());

        assertThrows(InvalidContentTypeException.class, () -> {
            defaultImageService.saveNewImage(file1, reference, account);
        });
    }

    @Test
    void deleteImageFailure_NotFound() {

        long imageId = 26L;

        Account account = new Account();
        account.setId(1L);

        Image image = new Image();
        image.setTags(new ArrayList<>());
        image.setAccount(account);

        when(imageRepository.findById(imageId)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            defaultImageService.deleteImage(imageId,account);
        });
    }

    @Test
    void deleteImageFailure_Forbidden() {

        long imageId = 26L;

        Account imageOwner = new Account();
        imageOwner.setUsername("imageOwner");

        Account account = new Account();
        account.setUsername("user");

        Image image = new Image();
        image.setTags(new ArrayList<>());
        image.setAccount(imageOwner);

        when(imageRepository.findById(imageId)).thenReturn(java.util.Optional.of(image));

        assertThrows(ForbiddenException.class, () -> {
            defaultImageService.deleteImage(imageId,account);
        });
    }

    @Test
    void updateImageSuccess() throws Exception {
        long imageId = 26L;
        String newImageName = "newImageName";

        Account account = new Account();
        account.setId(1L);

        Image image = new Image();
        image.setTags(new ArrayList<>());
        image.setAccount(account);

        when(imageRepository.findById(imageId)).thenReturn(java.util.Optional.of(image));
        when(imageRepository.save(any(Image.class))).thenAnswer(returnsFirstArg());

        Image updatedImage = defaultImageService.updateImage(imageId, newImageName, account);

        assertEquals(updatedImage.getImageName(), newImageName);
    }

    @Test
    void updateImageFailure_TooShort() {
        long imageId = 1L;
        String newImageName = "";

        Account account = new Account();
        account.setId(1L);

        Image image = new Image();
        image.setTags(new ArrayList<>());
        image.setAccount(account);

        when(imageRepository.findById(imageId)).thenReturn(java.util.Optional.of(image));
        when(imageRepository.save(any(Image.class))).thenAnswer(returnsFirstArg());
        when(tagService.getTagByName(anyString())).thenReturn(null);

        assertThrows(InputException.class, () -> {
            defaultImageService.updateImage(imageId, newImageName, account);
        });
    }

    @Test
    void addTagsSuccess()
            throws ForbiddenException, NotFoundException {

        long imageId = 26L;

        Account account = new Account();
        account.setId(1L);

        Image image = new Image();
        image.setTags(new ArrayList<>());
        image.setAccount(account);

        TagContainer tagContainer = new TagContainer();
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Tag1");
        tagNames.add("Tag2");
        tagContainer.setTagNames(tagNames);

        when(imageRepository.findById(imageId)).thenReturn(java.util.Optional.of(image));
        when(imageRepository.save(any(Image.class))).thenAnswer(returnsFirstArg());
        when(tagService.getTagByName(anyString())).thenReturn(null);

        Image updatedImage = defaultImageService.addTags(imageId, tagContainer ,account);

        List<Tag> tags = updatedImage.getTags();
        List<String> updatedTags = tags.stream().map(Tag::getTagName).collect(Collectors.toList());

        assertEquals(updatedTags, tagNames);
    }

    @Test
    void addTagsFailure_NotFound() {

        long imageId = 26L;
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->{
            defaultImageService.addTags(imageId, new TagContainer() ,new Account());
        });
    }

    @Test
    void addTagsFailure_Forbidden() {

        long imageId = 26L;
        Account imageOwner = new Account();
        imageOwner.setUsername("owner");
        Account account = new Account();
        account.setUsername("user");
        Image image = new Image();
        image.setId(imageId);
        image.setAccount(imageOwner);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        assertThrows(ForbiddenException.class, () ->{
            defaultImageService.addTags(imageId, new TagContainer() ,account);
        });
    }

    @Test
    void getImageFailure_NotFound() {
        long imageId = 26L;

        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            defaultImageService.getImage(imageId);
        });
    }
}