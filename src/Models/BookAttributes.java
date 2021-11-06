package Models;

import org.apache.commons.collections4.bidimap.*;
import org.apache.commons.collections4.BidiMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookAttributes {
	// Lists
	public static ObservableList<String> FictionGenres = FXCollections.observableArrayList();
	public static ObservableList<String> NonFictionGenres = FXCollections.observableArrayList();
	public static ObservableList<String> blankList=FXCollections.observableArrayList();
    public static ObservableList<String> authors = FXCollections.observableArrayList();
    public static ObservableList<String> publishers = FXCollections.observableArrayList();
    public static ObservableList<String> languages = FXCollections.observableArrayList();
    public static ObservableList<String> series = FXCollections.observableArrayList();

    // Hashmaps
    public static BidiMap<Integer, String> bidiMapAuthors = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapPublishers = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapFictionGenres = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapNonFictionGenres = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapLanguages = new TreeBidiMap<>();
    public static BidiMap<Integer, String> bidiMapSeries = new TreeBidiMap<>();
}