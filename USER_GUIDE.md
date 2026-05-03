# User Guide
This guide details how the server works from the client-side. 

In the future, everything should be intuitive to the user, but that's not the case.

## Pages
### Home Page
The home page has a search form (see [Meme Properties](#meme-properties)), a count of how many results, and the rest of the page is the results. Clicking on a result will pull up the static file hosted on the server.

By default, only restriction level-0 results are shown.

### Upload Page
The upload page is available at `../upload`. The form is identical to the search form. The upload page has a better tag input, so tags will show up in boxes as you enter them. Click on a tag to remove it.

If a file fails to upload, you will get an error message.

The original name of a file is ignored.

### Edit Page
The edit page is available at `../edit`. You'll need a query parameter specifying the UUID of the meme you wish to edit, i.e. `?id=12345678-1234-...`

## Meme Properties
Memes can hold the following properties, most of which are used for indexing and search functionality.

| Property      | Description                                           |
| ---           | ---                                                   |
| id            | A UUID to identify the meme                           |
| filePath      | The file path of the meme                             |
| template      | The template or original version of the meme          |
| type          | Type of file: image, video, animated, or other        |
| text          | Text visually present in the meme. <br> More comprehensive is better, for searching |
| description   | Anything else significant to aid in searches          |
| name          | A short description of the meme                       |
| origin        | Name of the original creator or link to post          |
| year          | Year the meme was posted. "0" is the default and should be treated as "unknown" |
| tags          | Keywords for genres or collections                    |
| restriction   | An integer 0, 1, or 2 for marking memes as obscene <br> 0: Not offensive, could be rated PG <br> 1: A little offensive, would be rated PG-13. Includes swears and insensitive humor <br> 2: Agreeably offensive, 18+. Covers sex jokes, vulgar language, and hate speech |

See [Meme.java] for the code and more documentation.

[Meme.java]: src/main/java/com/zeter/meme/Meme.java