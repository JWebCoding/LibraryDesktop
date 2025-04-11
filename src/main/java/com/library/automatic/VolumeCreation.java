package com.library.automatic;

import com.fasterxml.jackson.databind.JsonNode;
import com.library.models.Book;

import java.util.ArrayList;

public class VolumeCreation {


    public ArrayList<Book> returnVolumeList(JsonNode volumes){
        ArrayList<Book> volumeList = new ArrayList<>();
        int i=0;
        while(volumes.get("items").get(i)!=null){
            JsonNode volumeInfo = volumes.get("items").get(i).get("volumeInfo");
            volumeList.add(setNewBookData(volumeInfo));
            i++;
        }

        return volumeList;
    }

    private Book setNewBookData(JsonNode volumeInfo){
        Book newBook=new Book();
        newBook.setTitle(setStringVolumeInfo(volumeInfo,"title"));
        newBook.setSubtitle(setStringVolumeInfo(volumeInfo,"subtitle"));
        newBook.setAuthor(volumeInfo.get("authors").get(0).textValue());
        newBook.setPublisher(setStringVolumeInfo(volumeInfo,"publisher"));
        newBook.setCopyright(Integer.parseInt(volumeInfo.get("publishedDate").textValue()
                .substring(0,4)));
        newBook.setIsbn(setVolumeISBN(volumeInfo));
        newBook.setGenre(setVolumeGenre(volumeInfo));
        newBook.setLanguage(setStringVolumeInfo(volumeInfo,"language"));
        newBook.setPageCount(setVolumePageCount(volumeInfo));

        return newBook;
    }

    /*Only use for more generic situations where data is in the first layer of the Node.
    * Otherwise, */
    private String setStringVolumeInfo(JsonNode volumeInfo, String desiredInfo) {
        String newInfo=null;
        try {
            String temp=volumeInfo.get(desiredInfo).textValue();
            if(!temp.isEmpty()){
                newInfo=temp;
            }
            return newInfo;
        } catch (Exception e) {
            throw new RuntimeException("The desired field: "+desiredInfo+"\nCould not be found!");
        }

    }
    // Not needed for now but might want again in the future.
    /*private String setVolumeLanguage(JsonNode volumeInfo){
        String language=null;
        JsonNode categories=volumeInfo.get("language");
        if(!categories.isEmpty()){
            language=categories.get(0).textValue();
        }

        return language;
    }*/

    private int setVolumePageCount(JsonNode volumeInfo){
        int pageCount=0;
        JsonNode categories=volumeInfo.get("pageCount");

        if(!categories.isNull()){
            int tempPageCount=categories.asInt();

            if(tempPageCount != 0){
                pageCount = tempPageCount;
            }
        }
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

}
