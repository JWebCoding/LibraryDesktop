package com.library.controllers;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.models.Book;
import com.library.models.GoogleBooksService;
import com.library.models.BookAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AutomaticAddBookController {
    BookAttributes bookAttributes = new BookAttributes();
    public void createVolumeList(String bookData, String infoType) throws IOException {
        JsonNode volumes=getSearchResults(GoogleBooksService.callBookAPI(bookData, infoType));
        ArrayList<Book> volumeList = new ArrayList<Book>();
        System.out.println(volumes.size());
        int i=0;
        while(volumes.get("items").get(i)!=null){
            JsonNode volumeInfo = volumes.get("items").get(i).get("volumeInfo");
            volumeList.add(setNewBookData(volumeInfo));
            i++;
        }
        System.out.println(volumeList);
    }

    private JsonNode getSearchResults (InputStream responseStream) throws IOException{
        // Parse the response and isolate the "volumeInfo" object
        ObjectMapper mapper = new ObjectMapper();
        JsonFactory factory = mapper.getFactory();
        JsonParser parser = factory.createParser(responseStream);
        JsonNode node = mapper.readTree(parser);
        return node;
//        JsonNode volumeInfo = node.get("items").get(0).get("volumeInfo");
    }

    private Book setNewBookData(JsonNode volumeInfo){

        Book newBook=new Book();
        newBook.setTitle(volumeInfo.get("title").textValue());
        newBook.setSubtitle(setVolumeSubtitle(volumeInfo));
        newBook.setAuthor(volumeInfo.get("authors").get(0).textValue());
        newBook.setPublisher(setVolumePublisher(volumeInfo));
        newBook.setCopyright(Integer.parseInt(volumeInfo.get("publishedDate").textValue().substring(0,4)));
        newBook.setIsbn(setVolumeISBN(volumeInfo));
        newBook.setGenre(setVolumeGenre(volumeInfo));
        newBook.setLanguage(volumeInfo.get("language").textValue());
        newBook.setPageCount(volumeInfo.get("pageCount").intValue());

        return newBook;
    }

    private String setVolumeSubtitle(JsonNode volumeInfo){
        String subtitle=null;
        String temp=volumeInfo.get("subtitle").textValue();
        if(!temp.isEmpty()){
            subtitle=temp;
        }
        return subtitle;
    }

    private String setVolumePublisher(JsonNode volumeInfo){
        String publisher=null;
        String temp=volumeInfo.get("publisher").textValue();
        if(!temp.isEmpty()){
            publisher=temp;
        }
        if(!bookAttributes.bidiMapPublishers.containsValue(publisher)){
            // Prompt User to add publisher
        }

        return publisher;
    }

    private int setVolumeCopyright(JsonNode volumeInfo){
        int copyright=0;

        return copyright;
    }

    private String setVolumeLanguage(JsonNode volumeInfo){
        String language=null;

        return language;
    }

    private int setVolumePageCount(JsonNode volumeInfo){
        int pageCount=0;


        return pageCount;
    }

    private String setVolumeGenre(JsonNode volumeInfo){
        String genre=null;
        JsonNode categories=volumeInfo.get("categories");
        if(!categories.isEmpty()){
            genre=categories.get(0).textValue();
        }
        return genre;
    }

    private String setVolumeISBN(JsonNode volumeInfo){
        String isbn=null;
        JsonNode industryIdentifiers=volumeInfo.get("industryIdentifiers");
        if(!industryIdentifiers.isEmpty()){
            isbn=industryIdentifiers.path(0).get("identifier").textValue();
        }
        return isbn;
    }

    private void addBookToDatabase(){

    }

}
