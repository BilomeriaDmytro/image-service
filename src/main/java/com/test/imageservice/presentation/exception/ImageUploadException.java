package com.test.imageservice.presentation.exception;

import lombok.Getter;
import java.util.List;

@Getter
public class ImageUploadException extends Exception{

    private final List<String> failedFileNames;

    public ImageUploadException(List<String> failedFileNames){
        super("Failed to upload next files: " + failedFileNames);
        this.failedFileNames = failedFileNames;
    }
}