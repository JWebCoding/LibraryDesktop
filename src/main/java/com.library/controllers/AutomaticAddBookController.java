package com.library.controllers;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.automatic.VolumeCreation;
import com.library.models.Book;
import com.library.models.GoogleBooksService;
import com.library.models.BookAttributes;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class AutomaticAddBookController {
    BookAttributes bookAttributes = new BookAttributes();
    VolumeCreation volumeCreation = new VolumeCreation();

    public void initialize() throws Exception {
        createHashMaps();
    }

    private void createHashMaps() throws SQLException {
        bookAttributes.createAuthorHashMap();
        bookAttributes.createPublisherHashMap();
        bookAttributes.createGenreHashMaps();
        bookAttributes.createLanguageHashMap();
        bookAttributes.createSeriesHashMap();
    }


    public ArrayList<Book> createVolumeList(String bookData, String infoType) throws IOException {
        JsonNode volumes=getSearchResults(GoogleBooksService.callBookAPI(bookData, infoType));

        /* IF Statement to check if there's content*/

        assert volumes != null;
        return volumeCreation.returnVolumeList(volumes);
    }

    private JsonNode getSearchResults (InputStream responseStream) throws IOException{
        // Parse the response and isolate the "volumeInfo" object
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(responseStream);
            return mapper.readTree(parser);
        } catch (Exception e){
            System.err.println("Error getting search Data"+ e);
        }
        return null;
    }

    private void addBookToDatabase(){
        // Steps
        // 1: Confirm all required fields have content
        // 2: Check if content in fields exists in the different fields
        // 3: Create new entries in the appropriate fields whenever empty.


    }

    public void checkForExistingVolumeData() {

    }

}
