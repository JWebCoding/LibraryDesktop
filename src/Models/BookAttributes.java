package Models;
import org.apache.commons.collections4.bidimap.*;
import javax.sql.rowset.CachedRowSet;
import org.apache.commons.collections4.BidiMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookAttributes {
	ConnectionCommands connectionCommands =new ConnectionCommands();
	SQLCommands sqlCommands = new SQLCommands();
	
	// Lists
    public ObservableList<String> obvListAuthors = FXCollections.observableArrayList();
    public ObservableList<String> obvListPublishers = FXCollections.observableArrayList();
	public ObservableList<String> obvListFictionGenres = FXCollections.observableArrayList();
	public ObservableList<String> obvListNonFictionGenres = FXCollections.observableArrayList();
    public ObservableList<String> obvListLanguages = FXCollections.observableArrayList();
    public ObservableList<String> obvListSeries = FXCollections.observableArrayList();
    public ObservableList<String> obvListEmptyList = FXCollections.observableArrayList();

    // Hashmaps
    public BidiMap<Integer, String> bidiMapAuthors = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapPublishers = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapFictionGenres = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapNonFictionGenres = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapLanguages = new TreeBidiMap<>();
    public BidiMap<Integer, String> bidiMapSeries = new TreeBidiMap<>();
    
    public void createAuthorHashMap() {
    	// Declare Variables
    	String authorFirstName,authorMiddleName, authorLastName, authorFullName;
        int ID;
        long start=0, end=0;
        ObservableList<String> tempAuthorList=FXCollections.observableArrayList();
        // Get the Cached Row Set for all Authors in the database
        CachedRowSet authorList = connectionCommands.readDatabase(sqlCommands.selectAllAuthor);

        // Clear the contents of the relvant hash-maps and observable lists.
        bidiMapAuthors.clear();
        obvListAuthors.clear();
        
        // Combine the sperate parts of the names from the cached rowset
        try {
        	start=System.currentTimeMillis();
        	while (authorList.next()) {
        		// Get the ID and name of the author
                ID = authorList.getInt(1);
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
                bidiMapAuthors.put(ID, authorFullName);
                
        	}
        	obvListAuthors.addAll(tempAuthorList);
        	end=System.currentTimeMillis();
                
        } catch(Exception e) {
        	printRowSetErrorMessage("authorList", e);
        } 
//        System.out.println(end-start);
    }
    
    public void createPublisherHashMap() {
    	int ID;
    	long start=0, end=0;
        String publisherName;
        ObservableList<String> tempPublisherList=FXCollections.observableArrayList();
        // Get the Cached Row Set for all Authors in the database
        CachedRowSet publisherList = connectionCommands.readDatabase(sqlCommands.selectAllPublisher);

        // Clear the contents of the relvant hash-maps, observable lists and choice-boxes.
        bidiMapPublishers.clear();
        obvListPublishers.clear();
        
        try {
        	start=System.currentTimeMillis();
        	// Get the contents of publisherName
            while (publisherList.next()) {
                ID = publisherList.getInt(1);
                publisherName = publisherList.getString(2);
                
                // Add publisher to hashmap and list
                tempPublisherList.add(publisherName);
                bidiMapPublishers.put(ID, publisherName);
                
            }
            obvListPublishers.addAll(tempPublisherList);
            end=System.currentTimeMillis();
            
        } catch(Exception e) {
        	printRowSetErrorMessage("publisherList", e);
        }
//        System.out.println(end-start);
    }
    
    public void createFictionHashMap() {
    	int ID;
        String genreName;
        CachedRowSet fictionGenreList = connectionCommands.readDatabase(sqlCommands.selectFictionGenre);

        // Clear the contents of the relvant hash-maps and observable lists.
        bidiMapFictionGenres.clear();
        obvListFictionGenres.clear();
        
        try {
        	// Get the contents of fictionGenreList
        	while (fictionGenreList.next()) {
                ID = fictionGenreList.getInt(1);
                genreName = fictionGenreList.getString(2);
                
                // Add genre to hashmap and list
                bidiMapFictionGenres.put(ID,genreName); 
                obvListFictionGenres.add(genreName);
        	}
        } catch(Exception e) {
        	printRowSetErrorMessage("fictionGenreList", e);
        }
    }
    
    public void createNonFictionHashMap() {
    	int ID;
        String genreName;
        CachedRowSet nonFictionGenreList = connectionCommands.readDatabase(sqlCommands.selectNonFictionGenre);

        // Clear the contents of the relvant hash-maps and observable lists.
        bidiMapNonFictionGenres.clear();
        obvListNonFictionGenres.clear();
        
        try {
        	// Get the contents of fictionGenreList
        	while (nonFictionGenreList.next()) {
                ID = nonFictionGenreList.getInt(1);
                genreName = nonFictionGenreList.getString(2);
                
                // Add genre to hashmap and list
                bidiMapNonFictionGenres.put(ID,genreName); 
                obvListNonFictionGenres.add(genreName);
        	}
        } catch(Exception e) {
        	printRowSetErrorMessage("nonFictionGenreList", e);
        }
    }
    
    public void createLanguageHashMap() {
    	int ID;
        String languageName;
        CachedRowSet languageList = connectionCommands.readDatabase(sqlCommands.selectAllLanguage);

        // Clear the contents of the relvant hash-maps and observable lists.
        bidiMapLanguages.clear();
        obvListLanguages.clear();
        
        try {
        	// Get the contents of language list
        	while (languageList.next()) {
                ID = languageList.getInt(1);
                languageName = languageList.getString(2);
                
                
                bidiMapLanguages.put(ID, languageName);
                obvListLanguages.add(languageName);
            }
        } catch(Exception e) {
        	printRowSetErrorMessage("languageList", e);
        }     
    }
    
    public void createSeriesHashMap() {
    	int ID;
        String seriesName;
        CachedRowSet seriesList = connectionCommands.readDatabase(sqlCommands.selectAllSeries);
        bidiMapSeries.clear();
        obvListSeries.clear();

        try {
        	while (seriesList.next()) {
                ID = seriesList.getInt(1);
                seriesName = seriesList.getString(2);
                
                bidiMapSeries.put(ID, seriesName);
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
		case 1: 
			System.out.println(obvListAuthors.toString());
			break;
		case 2: 
			System.out.println(obvListPublishers.toString());
			break;
		case 3: 
			System.out.println(obvListFictionGenres.toString());
			break;
		case 4: 
			System.out.println(obvListNonFictionGenres.toString());
			break;
		case 5: 
			System.out.println(obvListLanguages.toString());
			break;
		case 6: 
			System.out.println(obvListSeries.toString());
			break;
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + selection);
		}
    }
    
    private void printHashMap(int selection) {
    	switch (selection) {
		case 1: 
			System.out.println(bidiMapAuthors.toString());
			break;
		case 2: 
			System.out.println(bidiMapPublishers.toString());
			break;
		case 3: 
			System.out.println(bidiMapFictionGenres.toString());
			break;
		case 4: 
			System.out.println(bidiMapFictionGenres.toString());
			break;
		case 5: 
			System.out.println(bidiMapLanguages.toString());
			break;
		case 6: 
			System.out.println(bidiMapSeries.toString());
			break;
		
		default:
			throw new IllegalArgumentException("Unexpected value: " + selection);
		}
    }
    
}