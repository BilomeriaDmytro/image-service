package com.test.imageservice.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImageSearchDTO {

    private String imageName;

    private String contentType;

    private Long size;

    private String reference;

    private List<String> tags;
}
