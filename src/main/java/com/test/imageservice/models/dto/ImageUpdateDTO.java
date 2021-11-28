package com.test.imageservice.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class ImageUpdateDTO {

    private String imageName;

    private List<String> tags;
}