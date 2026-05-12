package com.zeter.memedb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.zeter.memedb.Library.*;
import com.zeter.memedb.dto.EditDto;
import com.zeter.memedb.dto.FilterDto;
import com.zeter.memedb.dto.MemeDto;

@Controller
public class MemeController {

    private final static String FILE_DEST = "meme\\src\\main\\resources\\static\\files\\";

    @Autowired
    private MemeRepository repository;
    
    /**
     * The default home page. All memes with a restriction level of 0 are displayed.
     * 
     * @param model
     * @return
     */
    @GetMapping("")
    public String index(Model model) {
        // Get all memes with a restriction level of 0 and add them to the output
        List<Meme> memes = repository.findAll();
        FilterDto filters = new FilterDto();
        filters.setRestriction(0);
        model.addAttribute("files", filterMemes(memes, filters));
        model.addAttribute("filterDto", new FilterDto());
        return "home";
    }

    @PostMapping("")
    public String indexWithFilters(Model model, FilterDto dto) {
        List<Meme> memes = repository.findAll();
        model.addAttribute("files", filterMemes(memes, dto));
        model.addAttribute("filterDto", dto);
        return "home";
    }

    /**
     * Sends the client the meme upload form
     * 
     * @param model
     * @return
     */
    @GetMapping("/upload")
    public String getUploadPage(Model model) {
        
        // DTO for collecting form information
        model.addAttribute("meme", new MemeDto());

        // List of tags, ordered by how common they are
        // Exclude the "needs_tags" and "needs_data" tags
        List<TagEntry> tags = getAllTags();
        tags.removeIf((tag) -> tag.name.equals("needs_tags") || tag.name.equals("needs_data"));
        model.addAttribute("tags", tags);

        return "uploadForm";
    }

    /**
     * Handles the form submission of a meme
     * 
     * @param file
     * @param redirectAttributes
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(MemeDto dto, RedirectAttributes redirectAttributes)
            throws IOException {

        // Create a blank meme to generate a unique ID
        Meme meme = new Meme();
        repository.save(meme);

        String extension = dto.getFile().getOriginalFilename().replaceFirst(".+(?=\\.)", ""); // gets just the file extension

        File file = new File(FILE_DEST + meme.getId() + extension);
        file.createNewFile();

        dto.getFile().transferTo(file.toPath());

        // Load inputted info into database entry
        meme.setFilePath("/files/" + meme.getId() + extension);

        meme.setFileType(dto.getFileType());

        meme.setTags(new ArrayList<>(Arrays.asList(dto.getTags()))); // REVIEW if this throws an error if getTags() returns null
        if (meme.getTags().size() == 0) {
            meme.getTags().add("needs_tags");
        }
        meme.getTags().add("needs_data"); // might not actually, but just add this to every new entry

        if (dto.getRestriction() < 0) {
            meme.setRestriction(0);
        } else if (dto.getRestriction() > 2) {
            meme.setRestriction(2);
        } else {
            meme.setRestriction(dto.getRestriction());
        }

        meme.setName(dto.getName());

        meme.setOrigin(dto.getOrigin());

        meme.setYear(dto.getYear());

        meme.setTemplate(dto.getTemplate());
        
        meme.setText(dto.getText());

        meme.setDescription(dto.getDescription());

        if (meme.getFileType().equals(FileType.VIDEO)) {
            try {
                dumpVideoFrame(meme);
            } catch (Exception e) {
                // Video failed to make a thumbnail. Abort meme save, return an error
                repository.delete(meme);
                file.delete();
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        repository.save(meme);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Displays the edit page to the user if an ID is provided, or a blank page if not.
     * 
     * @param id - the UUID of the meme to edit
     * @param model
     * @return
     */
    @GetMapping("/edit")
    public String getMemeEditPage(@RequestParam(required = false) String id, Model model) {
        
        if (id == null) {
            return "editIndex";
        }

        if (!repository.existsById(id)) {
            // Give a 404 page
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Meme meme = repository.findMemeById(id);

        // Make new DTO and populate it
        EditDto dto = new EditDto();
        dto.setTags(Library.listToString(meme.getTags()));
        dto.setOrigin(meme.getOrigin());
        dto.setDescription(meme.getDescription());
        dto.setText(meme.getText());
        dto.setName(meme.getName());
        dto.setTemplate(meme.getTemplate());
        dto.setRestriction(meme.getRestriction());
        dto.setYear(meme.getYear());
        dto.setFileType(meme.getFileType());
        dto.setMemeId(meme.getId());

        model.addAttribute("meme", dto);
        return "editForm";
    }

    /**
     * Handles the editing of a meme.
     * 
     * @param dto
     * @param redirectAttributes
     * @param model
     * @return
     */
    @PostMapping("/edit")
    public ResponseEntity<?> handleMemeEdit(EditDto dto, RedirectAttributes redirectAttributes, Model model) {

        Meme meme = repository.findMemeById(dto.getMemeId());

        meme.setFileType(dto.getFileType());

        if (!dto.getTags().equals("")) {
            meme.setTags(Library.stringToList(dto.getTags()));
        }

        if (dto.getRestriction() < 0) {
            meme.setRestriction(0);
        } else if (dto.getRestriction() > 2) {
            meme.setRestriction(2);
        } else {
            meme.setRestriction(dto.getRestriction());
        }

        meme.setName(dto.getName());

        meme.setOrigin(dto.getOrigin());

        meme.setYear(dto.getYear());

        meme.setTemplate(dto.getTemplate());
        
        meme.setText(dto.getText());

        meme.setDescription(dto.getDescription());

        // In case the type got changed to video
        if (meme.getFileType().equals(FileType.VIDEO)) {
            try {
                dumpVideoFrame(meme);
            } catch (Exception e) {
                // Do not update, send an error
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        repository.save(meme);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/tags")
    public String getTagList(Model model) {
        model.addAttribute("tags", getAllTags());
        return "tags";
    }

    /**
     * Gets all tags stored on this server, associated with the quantity of files with said tag
     * 
     * @return a list of name-count pairs, ordered by highest quantity to lowest
     */
    private List<TagEntry> getAllTags() {
        List<Meme> memes = repository.findAll();
        HashCounter<String> counter = new HashCounter<>();
        for (Meme meme : memes) {
            for (String tag : meme.getTags()) {
                counter.increment(tag);
            }
        }

        List<TagEntry> output = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : counter.entrySet()) {
            output.add(new TagEntry(entry));
        }
        output.sort((o1,o2) -> {
            if (o1.count == o2.count) {
                return o1.name.compareTo(o2.name);
            } else {
                return o2.count - o1.count;
            }
        });

        return output;
    }

    /**
     * Class to represent a String-int pair, for counting the occurences of a tag
     */
    private class TagEntry {
        public String name;
        public int count;

        private TagEntry(Map.Entry<String, Integer> entry) {
            name = entry.getKey();
            count = entry.getValue();
        }
    }

    /**
     * Utility mapping for making changes. Shouldn't do anything in normal use.
     * 
     * @return
     */
    @GetMapping("/test")
    public String testPath() {
        // Add the "needs_data" tag to every existing item
        // for (Meme meme : repository.findAll()) {
        //     meme.getTags().add("needs_data");
        //     repository.save(meme);
        // }
        return null;
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleStorageFileNotFound(RuntimeException exc) {
        if (exc instanceof ResponseStatusException) {
            try {
                // Return HTTP code corresponding to the exception
                return new ResponseEntity<>(HttpStatusCode.valueOf(Integer.parseInt(exc.getMessage().substring(0,3))));
            } catch (Exception e) {
                // idk, just move on
            }
        }

        // If not an HTTP error, print the error and return a 500 error
        exc.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }

    /**
     * Gets the 3rd frame of a video from a meme source. Based off of yoinked code from stackoverflow
     * 
     * @author maghoumi
     * 
     * @param meme
     * @throws Exception
     */
    private static void dumpVideoFrame(Meme meme) throws Exception {
        FFmpegFrameGrabber g = new FFmpegFrameGrabber("meme/src/main/resources/static" + meme.getFilePath());
        g.start();
        try {
            Java2DFrameConverter converter = new Java2DFrameConverter();

            for (int i = 0 ; i < 3 ; i++) {
                
                Frame frame = g.grabImage(); // It is important to use grabImage() to get a frame that can be turned into a BufferedImage

                BufferedImage bi = converter.convert(frame);

                if (i == 2) {
                    ImageIO.write(bi, "png", new File("meme/src/main/resources/static/files/thumbnails/" + meme.getId() + "_thumb" + ".png"));
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            g.stop(); // Close the file stream
        }
    }

    /**
     * Filters entries in a List based on a FilterDto, so that the ones remaining match the DTO
     * 
     * @param memes
     * @param dto
     * @return
     */
    private List<Meme> filterMemes(List<Meme> memes, FilterDto dto) {

        FilteredMemeList memeSet = new FilteredMemeList(dto);
        memeSet.add(memes);

        return memeSet.export();
    }
}
