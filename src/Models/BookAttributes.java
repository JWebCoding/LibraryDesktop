package Models;

import org.apache.commons.collections4.bidimap.*;
import org.apache.commons.collections4.BidiMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookAttributes {
	// Lists
	public static ObservableList<String> obvListFictionGenres = FXCollections.observableArrayList();
	public static ObservableList<String> obvListNonFictionGenres = FXCollections.observableArrayList();
    public static ObservableList<String> obvListAuthors = FXCollections.observableArrayList();
    public static ObservableList<String> obvListPublishers = FXCollections.observableArrayList();
    public static ObservableList<String> obvListLanguages = FXCollections.observableArrayList();
    public static ObservableList<String> obvListSeries = FXCollections.observableArrayList();

    // Hashmaps
    public static BidiMap<Integer, String> bidiMapAuthors = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapPublishers = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapFictionGenres = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapNonFictionGenres = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapLanguages = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapSeries = new TreeBidiMap<>();
    
    public void printItem(int selection) {

    	BidiMap<Integer, String> hashmap = new TreeBidiMap<>();
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
			System.out.println(bidiMapNonFictionGenres.toString());
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