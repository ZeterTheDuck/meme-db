package com.zeter.memedb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import com.zeter.memedb.dto.FilterDto;

public class FilteredMemeList {

    private TreeMap<Integer, ArrayList<Meme>> map;

    private FilterDto dto;

    /**
     * The text used for filtering. Should be used instead of dto.getTextFilter()
     */
    private String text;

    /**
     * The text used for filtering, pre-represented as a list of individual Strings
     */
    private List<String> textList;

    /**
     * Whether to filter text or not
     */
    private boolean filterStrings;

    public FilteredMemeList(FilterDto dto) {
        this.map = new TreeMap<>((o1, o2) -> Integer.compare(o2, o1));
        this.dto = dto;

        // Format the filter in this DTO for simplicity
        text = formatFilterString(this.dto.getTextFilter());
        textList = Library.stringToList(text);

        if (text == null || text.equals("")) {
            filterStrings = false;
        } else {
            filterStrings = true;
        }
    }

    /**
     * Adds a meme to this collection, if it matches the filters set
     * 
     * @param meme - Meme to add
     * @return if the input meme was added
     */
    public boolean add(Meme meme) {
        // Basic tests
        if (meme.getRestriction() > dto.getRestriction()) {
            return false;
        }

        if (dto.getFileType() != null && meme.getFileType() != null && !meme.getFileType().equals(dto.getFileType())) {
            return false;
        }

        if (!(dto.getTags() == null || dto.getTags() == "")
                && !meme.getTags().containsAll(Library.stringToList(dto.getTags()))) {
            return false;
        }

        // SECTION Text tests
        if (!filterStrings) {
            // The relavance of the meme doesn't matter for sorting, just add it
            addToMap(meme, 0);
            return true;
        }

        // Test for exact test matches. If there is a match, add with a relavance of Integer.MAX_VALUE
        if (matchExact(meme)) {
            addToMap(meme, Integer.MAX_VALUE);
            return true;
        }

        int relavance = 0;
        List<String> memeWords = Library.stringToList(formatFilterString(meme.getName() + " " + meme.getTemplate() + " " + meme.getText() + " " + meme.getDescription()));
        for (String word : textList) {
            int count = count(word, memeWords);
            if (count <= 0) {
                // All words in text search must be present at least once
                return false;
            }
            relavance += count;
        }

        addToMap(meme, relavance);
        return true;
        // !SECTION
    }

    /**
     * Adds a collection of memes to this collection
     * 
     * @param memes - Collection of memes
     */
    public void add(Collection<Meme> memes) {
        for (Meme meme : memes) {
            add(meme);
        }
    }

    /**
     * Exports this set as an ordered list of Memes
     * 
     * @return memes ordered according to how relavant they are to the text filter
     */
    public List<Meme> export() {
        ArrayList<Meme> output = new ArrayList<>();
        
        for (ArrayList<Meme> memes : map.values()) {
            output.addAll(memes);
        }

        return output;
    }

    /**
     * Helper method to add a meme to the map used for sorting
     * 
     * @param meme - meme to add
     * @param rel - relavance of the meme. Used for sorting.
     */
    private void addToMap(Meme meme, int rel) {
        if (map.containsKey(rel)) {
            map.get(rel).add(meme);
        } else {
            map.put(rel, new ArrayList<Meme>(Collections.singletonList(meme)));
        }
    }

    /**
     * Formats a string to match a simplified format of characters, for easier text searching
     * 
     * @param input - String input. Can be null.
     * @return formatted string. Guaranteed to be non-null.
     */
    private String formatFilterString(String input) {
        if (input == null || input.equals("")) {
            return "";
        }

        /** Characters to replace with a space */
        final String REPLACE_SPACE = "'\\n";
        /** Characters to remove */
        final String REMOVE = "[^a-z0-9\\ ]";

        return (" " + input + " ").toLowerCase().replaceAll(REPLACE_SPACE, " ").replaceAll(REMOVE, "").replaceAll("\\s{2,}", " ");
    }

    /**
     * Determines if the text on a meme matches the {@link #text filter text}.
     * 
     * @param meme - meme to check
     * @return whether the text on this meme exactly matches the filter text or not
     */
    private boolean matchExact(Meme meme) {
        // FIXME don't do anything if the text is straight up null
        if (!(meme.getText() == null || meme.getText().equals("")) && formatFilterString(meme.getText()).contains(text)) {
            return true;
        }

        if (!(meme.getDescription() == null || meme.getDescription().equals("")) && formatFilterString(meme.getDescription()).contains(text)) {
            return true;
        }

        if (!(meme.getName() == null || meme.getName().equals("")) && formatFilterString(meme.getName()).contains(text)) {
            return true;
        }

        if (!(meme.getTemplate() == null || meme.getTemplate().equals("")) && formatFilterString(meme.getTemplate()).contains(text)) {
            return true;
        }

        // Else
        return false;
    }

    /**
     * Helper method to count the number of times one string occurs in a collection of strings
     * 
     * @param filterWord - String to test against the collection
     * @param colWords - Collection of strings
     * @return number of times the input occurs in the filter text
     */
    private int count(String filterWord, Collection<String> colWords) {
        int output = 0;
        for (String word : colWords) {
            if (word.equals(filterWord)) {
                output++;
            }
        }
        return output;
    }

}
