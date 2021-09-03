package Controllers;

import Models.Book;
import Models.ConnectionCommands;
import Models.SQLCommands;
import javax.sql.rowset.CachedRowSet;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class detailsController {
	
	@FXML TextField textFieldTitle;
	@FXML TextField textFieldSeries;
	@FXML TextField textFieldSeriesPart;
	@FXML TextField textFieldAuthor;
	@FXML TextField textFieldISBN;
	@FXML TextField textFieldCopyRight;
	@FXML TextField textFieldLanguage;
	@FXML TextField textFieldFormat;
	@FXML TextField textFieldPageCount;
	@FXML TextField textFieldPublisher;
	@FXML TextField textFieldGenre;
	@FXML Button buttonClose;
	@FXML Button buttonSetFinished;
	
	ConnectionCommands connectionCommands=new ConnectionCommands();
	SQLCommands sqlCommands=new SQLCommands();
	String title,firstName,lastName,isbn,series,publisher,genre,language,searchText,notification;
    int id,year,format,edition,finished,pageCount;
    Integer seriesPart;
	CachedRowSet bookQuery=null;
	
	public void initialize() throws Exception{
		connectionCommands.getConnectionSettings();
		fillTextfields();
	}
	
    public void getBookData() throws Exception{
    	String searchQuery=String.format(sqlCommands.selectSpecificBook,Book.getBookID());
    	bookQuery=connectionCommands.readDatabase(searchQuery);
    }

    public Book createBook() throws Exception {
    	getBookData();
    	bookQuery.first();
        id=bookQuery.getInt("bookID");
        title=bookQuery.getString("title");
        series=bookQuery.getString("series_name");
        seriesPart=bookQuery.getInt("series_part");
        if(seriesPart==0) {seriesPart=null;}
        firstName=bookQuery.getString("firstName");
        lastName=bookQuery.getString("lastName");
        publisher=bookQuery.getString("publisher_name");
        isbn=bookQuery.getString("isbn");
        year=bookQuery.getInt("copyright");
        genre=bookQuery.getString("genre_name");
        format=bookQuery.getInt("format");
        edition=bookQuery.getInt("edition");
        language=bookQuery.getString("language_name");
        pageCount=bookQuery.getInt("pages");
        
        return new Book(id,title,series,seriesPart,firstName,lastName,publisher,isbn,year,genre,edition,language,format,pageCount);
    }
    
    public void fillTextfields() throws Exception {
    	Book book = createBook();
    	
    	// Fill in the majority of text boxes
    	textFieldTitle.setText(book.getTitle());
    	textFieldSeries.setText(book.getSeries());
    	textFieldAuthor.setText(book.getAuthor());
    	textFieldPublisher.setText(book.getPublisher());
    	textFieldISBN.setText(book.getIsbn());
    	textFieldCopyRight.setText(String.valueOf(book.getCopyright()));
    	textFieldPageCount.setText(String.valueOf(book.getPageCount()));
    	textFieldLanguage.setText(book.getLanguage());
    	textFieldGenre.setText(book.getGenre());
    	
    	//Logic for the format, finished and series part textboxes.
    	// Format
    	if(format==1) {
    		textFieldFormat.setText("H");
    	} else {
    		textFieldFormat.setText("P");
    	}
    	// Series Part
    	if(book.getSeriesPart()==null) {
    		textFieldSeriesPart.setText("");
    	} else {
    		textFieldSeriesPart.setText(String.valueOf(book.getSeriesPart()));
    	}
    	
    }
    
    public void setBookToFinished() throws Exception {
    	try {
    		String updateQuery=String.format(sqlCommands.updateBookToFinished, Book.getBookID());
    		connectionCommands.writeDatabase(updateQuery);
    	} catch(Exception e) {
    		System.out.println("Error!\nClass: detailsController\nMethod: setBookToFinished"+e);
    	} finally {
    		fillTextfields();
    	}
    }
    
    
//    public void editTextfields() {
//    	// Hide the edit button and show the save button.
//    	buttonEdit.setVisible(false);
//    	buttonEdit.setDisable(true);
//    	buttonSave.setVisible(true);
//    	buttonSave.setDisable(false);
//    	// Set the textfields to be editable
//    	textFieldTitle.setEditable(true);
//    	textFieldSeries.setEditable(true);
//    	textFieldSeriesPart.setEditable(true);
//    	textFieldAuthor.setEditable(true);
//    	textFieldPublisher.setEditable(true);
//    	textFieldISBN.setEditable(true);
//    	textFieldCopyRight.setEditable(true);
//    	textFieldFinished.setEditable(true);
//    	textFieldPageCount.setEditable(true);
//    	textFieldLanguage.setEditable(true);
//    	textFieldGenre.setEditable(true);
//    }
//    
//    public void saveChanges() {
//    	// Hide the save button and show the edit button
//    	buttonEdit.setVisible(true);
//    	buttonEdit.setDisable(false);
//    	buttonSave.setVisible(false);
//    	buttonSave.setDisable(true);
//    	// Set the textfields to be uneditable
//    	textFieldTitle.setEditable(false);
//    	textFieldSeries.setEditable(false);
//    	textFieldSeriesPart.setEditable(false);
//    	textFieldAuthor.setEditable(false);
//    	textFieldPublisher.setEditable(false);
//    	textFieldISBN.setEditable(false);
//    	textFieldCopyRight.setEditable(false);
//    	textFieldFinished.setEditable(false);
//    	textFieldPageCount.setEditable(false);
//    	textFieldLanguage.setEditable(false);
//    	textFieldGenre.setEditable(false);
//    	
//    }
}
