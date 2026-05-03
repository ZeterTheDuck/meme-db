package com.zeter.meme.dto;

import org.springframework.web.multipart.MultipartFile;

import com.zeter.meme.Library.FileType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemeDto {

    private String origin, description, text, name, template;

    private String[] tags;

    private int restriction, year;

    private FileType fileType;

    private MultipartFile file;

    // Empty Constructor
    public MemeDto() {

    }
}