package com.test.imageservice.models.dto;

import com.test.imageservice.models.Image;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ImageDTO {

    private Long id;

    private String imageName;

    private String contentType;

    private Long size;

    private String reference;

    private Long  accountId;

    public ImageDTO(Image image){
        this.id = image.getId();
        this.imageName = image.getImageName();
        this.contentType = image.getContentType();
        this.size = image.getSize();
        this.reference = image.getReference();
        this.accountId = image.getAccount().getId();
    }

    public static List<ImageDTO> convertImageList(List <Image> images){
        List<ImageDTO> dtos = new ArrayList<>();
        images.forEach(image -> dtos.add(new ImageDTO(image)));
        return dtos;
    }
}