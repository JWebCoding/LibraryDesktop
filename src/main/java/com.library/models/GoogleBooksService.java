package com.library.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GoogleBooksService {

    public static InputStream callBookAPI(String bookData, String infoType) throws IOException {
        // Get book information from books api
        URL url= new URL(constructURL(bookData, infoType));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        InputStream responseStream = connection.getInputStream();
//        JsonNode volumeInfo = parseResponse(responseStream);

        return connection.getInputStream();
    }

    private static String constructURL(String bookInfo, String infoType){

        String finalURL;
        String baseURL="https://www.googleapis.com/books/v1/volumes?q=";
        String APIKey="&key=AIzaSyDUvX51t5rWFW3zaWsnzK8y_rgJh_OZFOc";

        finalURL=baseURL+infoType+":"+bookInfo+APIKey;
        return finalURL;
    }

//    private static JsonNode parseResponse(InputStream responseStream) throws IOException {
//        // Parse the response and isolate the "volumeInfo" object
//        ObjectMapper mapper = new ObjectMapper();
//        JsonFactory factory = mapper.getFactory();
//        JsonParser parser = factory.createParser(responseStream);
//        JsonNode node = mapper.readTree(parser);
//        JsonNode volumeInfo = node.get("items").get(0).get("volumeInfo");
//
//        return volumeInfo;
//    }
}
