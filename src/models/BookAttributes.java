package models;
import org.apache.commons.collections4.bidimap.*;
import javax.sql.rowset.CachedRowSet;
import org.apache.commons.collections4.BidiMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public class BookAttributes {
    QueryFactory queryFactory = new QueryFactory();
	
	// Lists
    public ObservableList<String> obvListAuthors = FXCollections.observableArrayList();
    public ObservableList<String> obvListPublishers = FXCollections.observableArrayList();
	public ObservableList<String> obvListFictionGenres = FXCollections.observableArrayList();
	public ObservableList<String> obvListNonFictionGenres = FXCollections.observableArrayList();
    public ObservableList<String> obvListLanguages = FXCollections.observableArrayList();
    public ObservableList<String> obvListSeries = FXCollections.observableArrayList();

    // Hashmaps
    public BidiMap<Integer, String> bidiMapAuthors = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapPublishers = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapFictionGenres = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapNonFictionGenres = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapLanguages = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapSeries = new TreeBidiMap<>();
    
    public void createAuthorHashMap() throws SQLException {
    	// Declare Variables
    	String authorFirstName,authorMiddleName, authorLastName, authorFullName;
        int id;
        ObservableList<String> tempAuthorList=FXCollections.observableArrayList();
        // Get the Cached Row Set for all Authors in the database
        CachedRowSet authorList = queryFactory.readFromDatabase("author");

        // Clear the contents of the relevant hash-maps and observable lists.
        bidiMapAuthors.clear();
        obvListAuthors.clear();
        
        // Combine the seperate parts of the names from the cached rowset
        try {
        	while (authorList.next()) {
        		// Get the id and name of the author
                id = authorList.getInt(1);
                authorFirstName = authorList.getString(2);
                authorMiddleName=authorList.getString(3);
                authorLastName = authorList.getString(4);
                if(authorMiddleName==null) {
                	authorFullName = (authorFirstName +" "+ authorLastName);
                } else {
                	authorFullName = (authorFirstName +" "+ authorMiddleName +" "+ authorLastName);
                }
                // Add author to hashmap and list
                tempAuthorList.add(authorFullName);
                bidiMapAuthors.put(id, authorFullName);
                
        	}
        	obvListAuthors.addAll(tempAuthorList);
                
        } catch(Exception e) {
        	printRowSetErrorMessage("authorList", e);
        }
    }
    
    public void createPublisherHashMap() throws SQLException {
    	int id;
        String publisherName;
        ObservableList<String> tempPublisherList=FXCollections.observableArrayList();
        // Get the Cached Row Set for all Authors in the database
        CachedRowSet publisherList = queryFactory.readFromDatabase("publisher");

        // Clear the contents of the relvant hash-maps, observable lists and choice-boxes.
        bidiMapPublishers.clear();
        obvListPublishers.clear();
        
        try {
        	// Get the contents of publisherName
            while (publisherList.next()) {
                id = publisherList.getInt(1);
                publisherName = publisherList.getString(2);
                
                // Add publisher to hashmap and list
                tempPublisherList.add(publisherName);
                bidiMapPublishers.put(id, publisherName);
                
            }
            obvListPublishers.addAll(tempPublisherList);
            
        } catch(Exception e) {
        	printRowSetErrorMessage("publisherList", e);
        }
    }

    public void createGenreHashMaps() throws SQLException {
        int id;
        boolean genreType;
        String genreName;

        CachedRowSet genreList = queryFactory.readFromDatabase("genre");

        // Clear the contents of the relvant hash-maps and observable lists.
        bidiMapFictionGenres.clear();
        obvListFictionGenres.clear();
        bidiMapNonFictionGenres.clear();
        obvListNonFictionGenres.clear();

        // Iterate through the cachedRowSet and put each genre into it's respective map and list.
        try{
            while (genreList.next()){
                id = genreList.getInt("genreID");
                genreType = genreList.getBoolean("genre_type");
                genreName = genreList.getString("genre_name");
                // Non-fiction=1 and Fiction=0
                if(genreType){
                    // Add genre to hashmap and list
                    bidiMapNonFictionGenres.put(id,genreName);
                    obvListNonFictionGenres.add(genreName);
                } else {
                    // Add genre to hashmap and list
                    bidiMapFictionGenres.put(id,genreName);
                    obvListFictionGenres.add(genreName);
                }
            }
        } catch(Exception e){
            printRowSetErrorMessage("fictionGenreList", e);
        }
    }
    
    
    public void createLanguageHashMap() throws SQLException {
    	int id;
        String languageName;
        CachedRowSet languageList = queryFactory.readFromDatabase("language");

        // Clear the contents of the relevant hash-maps and observable lists.
        bidiMapLanguages.clear();
        obvListLanguages.clear();
        
        try {
        	// Get the contents of language list
        	while (languageList.next()) {
                id = languageList.getInt(1);
                languageName = languageList.getString(2);
                
                
                bidiMapLanguages.put(id, languageName);
                obvListLanguages.add(languageName);
            }
        } catch(Exception e) {
        	printRowSetErrorMessage("languageList", e);
        }     
    }
    
    public void createSeriesHashMap() throws SQLException {
    	int id;
        String seriesName;
        CachedRowSet seriesList = queryFactory.readFromDatabase("series");
        bidiMapSeries.clear();
        obvListSeries.clear();

        try {
        	while (seriesList.next()) {
                id = seriesList.getInt(1);
                seriesName = seriesList.getString(2);
                
                bidiMapSeries.put(id, seriesName);
                obvListSeries.add(seriesName);
        	}
        } catch(Exception e) {
        	printRowSetErrorMessage("seriesList", e);
        }
    }
    
    // Print Methods
    private void printRowSetErrorMessage(String rowset, Exception e) {
    	System.err.println("Unable to read the CachedRowSet: "+rowset+".\nException: "+e);
    }
    
    private void printLists(int selection) {
        switch (selection) {
            case 1 -> System.out.println(obvListAuthors.toString());
            case 2 -> System.out.println(obvListPublishers.toString());
            case 3 -> System.out.println(obvListFictionGenres.toString());
            case 4 -> System.out.println(obvListNonFictionGenres.toString());
            case 5 -> System.out.println(obvListLanguages.toString());
            case 6 -> System.out.println(obvListSeries.toString());
            default -> throw new IllegalArgumentException("Unexpected value: " + selection);
        }
    }
    
    private void printHashMap(int selection) {
        switch (selection) {
            case 1 -> System.out.println(bidiMapAuthors.toString());
            case 2 -> System.out.println(bidiMapPublishers.toString());
            case 3 -> System.out.println(bidiMapNonFictionGenres.toString());
            case 4 -> System.out.println(bidiMapFictionGenres.toString());
            case 5 -> System.out.println(bidiMapLanguages.toString());
            case 6 -> System.out.println(bidiMapSeries.toString());
            default -> throw new IllegalArgumentException("Unexpected value: " + selection);
        }
    }
    
}