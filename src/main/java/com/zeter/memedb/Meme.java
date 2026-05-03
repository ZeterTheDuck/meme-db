package com.zeter.memedb;

import java.util.ArrayList;
import java.util.List;

import com.zeter.memedb.Library.*;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstraction of a meme. Should be filled out by the user.
 */
@Entity
public class Meme {
    
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * The file path of this meme
     */
    @Getter @Setter
    private String filePath;

    /**
     * The meme template or original version used, if applicable
     */
    @Getter @Setter
    private String template;

    /**
     * The type of file
     * @see Library.FileType
     */
    @Enumerated(EnumType.STRING)
    @Getter @Setter
    private FileType fileType;

    /**
     * All text on this meme
     */
    @Getter @Setter
    @Column(columnDefinition = "TEXT")
    private String text;

    /**
     * A description of this meme
     */
    @Getter @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * A short description/name of this meme
     */
    @Getter @Setter
    private String name;

    /**
     * The original post of this meme, or the author
     */
    @Getter @Setter
    private String origin;

    /**
     * The year this meme was made
     * <p> Default of 0 means "unknown"
     */
    @Getter @Setter
    private int year;

    /**
     * A list of tags identifying this meme. Used for genres and collections.
     */
    @Getter @Setter
    @ElementCollection
    private List<String> tags;

    /**
     * An integer 0, 1, or 2, based on how offensive this meme is:
     * <ul><li> 0: Not offensive, could be rated PG
     * <li> 1: A little offensive, would be rated PG-13. Includes swears and insensitive humor. 
     * <li> 2: Agreeably offensive, 18+. Covers sex jokes, vulgar language, and racism/hate speech. Should only be shared with the close bros.
     */
    @Getter @Setter
    private int restriction;

    public Meme() {
        this.tags = new ArrayList<>();
    }

    /**
     * Hash Code for this object can simply be its ID, since that is unique.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
