package com.test.imageservice.presentation;

import com.test.imageservice.models.Account;
import com.test.imageservice.models.Image;
import com.test.imageservice.models.TagContainer;
import com.test.imageservice.models.dto.ImageDTO;
import com.test.imageservice.models.dto.ImageSearchDTO;
import com.test.imageservice.presentation.exception.ForbiddenException;
import com.test.imageservice.presentation.exception.ImageUploadException;
import com.test.imageservice.presentation.exception.InputException;
import com.test.imageservice.presentation.exception.NotFoundException;
import com.test.imageservice.presentation.response.ResponseMessage;
import com.test.imageservice.service.AccountService;
import com.test.imageservice.service.ImageService;
import com.test.imageservice.service.StorageService;
import com.test.imageservice.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private TagService tagService;

    @PostMapping(value = "/uploadFile")
    public ResponseEntity<ResponseMessage> uploadImage(@RequestParam("files") MultipartFile[] files) {

        String message = "";
        try {
            List<String> failedFilesNames = new ArrayList<>();

            Arrays.stream(files).forEach(file -> {
                try{

                    Account account = accountService.getCurrentUser();
                    String reference = storageService.save(file, account);
                    imageService.saveNewImage(file, reference, account);
                }catch (Exception e){

                    failedFilesNames.add(file.getOriginalFilename());
                }
            });

            if(!failedFilesNames.isEmpty()){
                throw new ImageUploadException(failedFilesNames);
            }

            message = "All files uploaded successfully!";
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseMessage(message));

        } catch (ImageUploadException e) {
            message = "Failed to upload next files: " + e.getFailedFileNames();
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseMessage(message));
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ResponseMessage> deleteUserImage(@RequestParam Long imageId)
            throws ForbiddenException, NotFoundException {

        String message = "";

        Account account = accountService.getCurrentUser();
        imageService.deleteImage(imageId, account);
        storageService.delete(imageId);

        message = "Image was successfully deleted";
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage(message));
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<ResponseMessage> deleteAllUserImages(){

        Account account = accountService.getCurrentUser();
        imageService.deleteAccountImages(account);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage("Images successfully deleted"));
    }

    @PostMapping("/update")
    public ResponseEntity<ResponseMessage> updateUserImage(@RequestParam Long imageId,
                                                           @Nullable @RequestParam String imageName,
                                                           @Nullable @RequestBody TagContainer tagContainer)
            throws Exception {

        Account account = accountService.getCurrentUser();
        String message = "";
        if(imageName != null){

            Image image = imageService.updateImage(imageId, imageName, account);
            message = "Image successfully updated";
            if(image == null){
                message = "Empty s provided";
                return ResponseEntity
                        .status(HttpStatus.EXPECTATION_FAILED)
                        .body(new ResponseMessage(message));
            }
        }

        if(tagContainer != null && tagContainer.getTagNames() != null){
            imageService.addTags(imageId,tagContainer,account);
            message = "Image successfully updated";
        }

        if (message.equals("")){
            message = "No updates provided";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseMessage(message));
    }

    @GetMapping("/get/all")
    public List<ImageDTO> getAllUserImages(){
        Account account = accountService.getCurrentUser();
        List<Image> images = imageService.getImages(account);
        return ImageDTO.convertImageList(images);
    }

    @GetMapping("/get")
    public ImageDTO getImage(@RequestParam Long imageId)
            throws NotFoundException {

        Image image = imageService.getImage(imageId);
        return new ImageDTO(image);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam int page,
                                 @RequestParam int size,
                                 @RequestBody ImageSearchDTO imageSearchDTO) throws InputException {

        Page<Image> images = imageService.searchByAllProperties(imageSearchDTO, page, size);
        List<ImageDTO> imageDTOS = ImageDTO.convertImageList(images.getContent());

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", images.getNumber());
        response.put("totalItems", images.getTotalElements());
        response.put("totalPages", images.getTotalPages());
        response.put("images", imageDTOS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}