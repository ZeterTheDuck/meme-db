package com.zeter.memedb.dto;

import com.zeter.memedb.Library.FileType;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FilterDto {
    /**
     * Only files with this level of restriction or lower will be shown
     */
    private int restriction;

    /**
     * Tags to filter by. Uses a logical AND, that is, only files that match every tag will be displayed
     */
    private String tags;

    /** If null, do not filter by File Type */
    private FileType fileType;

    /**
     * Only files with a name, description, or text that contain this String will be shown
     * 
     * @implNote ignores all non-alphanumeric characters and capitalization
     */
    private String textFilter;

    // Blank Constructor
    public FilterDto() {

    }
}
