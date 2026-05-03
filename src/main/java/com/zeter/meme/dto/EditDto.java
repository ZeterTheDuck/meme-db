package com.zeter.meme.dto;

import com.zeter.meme.Library.FileType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EditDto {

    private String origin, description, text, name, template, tags;

    private int restriction, year;

    private FileType fileType;

    private String memeId;

    // Blank Constructor
    public EditDto() {

    }
}