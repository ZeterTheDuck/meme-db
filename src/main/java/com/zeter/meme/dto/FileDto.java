package com.zeter.meme.dto;

import com.zeter.meme.Library.FileType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FileDto {

    private String path, name, id;

    private FileType type;

    public FileDto() {
        
    }

    public FileDto(String filePath, FileType fileType, String name, String id) {
        this.path = filePath;
        this.type = fileType;
        this.name = name;
        this.id = id;
    }
}
