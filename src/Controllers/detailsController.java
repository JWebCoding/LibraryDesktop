package Controllers;

import Models.Book;
import Models.BookAttributes;
import Models.ConnectionCommands;
import Models.SQLCommands;
import java.math.BigInteger;
import javax.sql.rowset.CachedRowSet;
import org.controlsfx.control.textfield.TextFields;
import javafx.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.stage.Stage;

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
	@FXML Button buttonEdit;
	@FXML Button buttonSave;
	@FXML Button buttonClose;
	@FXML Button buttonDiscard;
	@FXML Button buttonSetFinished;
	@FXML Label labelNotification;
	@FXML
	ToggleButton toggleButtonPaperback;
    @FXML
    ToggleButton toggleButtonHardcover;
    @FXML
    ChoiceBox<String> choiceBoxGenreType;
    @FXML
    ChoiceBox<String> choiceBoxGenreName;
	
	ConnectionCommands connectionCommands=new ConnectionCommands();
	SQLCommands sqlCommands=new SQLCommands();
	BookAttributes bookAttributes=new BookAttributes();
	String title,firstName,middleName,lastName,isbn,series,publisher,genre,language,searchText,notification;
    String tempTitle,tempAuthor,tempISBN,tempYear,tempPageCount,tempGenre,tempSeries,tempSeriesPart,tempPublisher,tempLanguage;
    String notificationGreen="#00ff00",notificationRed="#ff0000";
    Integer id,year,format,edition,finished,pageCount,genreType,seriesPart;
	CachedRowSet bookQuery=null;
	
	public void initialize() throws Exception{
		ObservableList<String> genreTypes = FXCollections.observableArrayList("Fiction", "Non-Fiction");
		choiceBoxGenreType.setItems(genreTypes);
		connectionCommands.getConnectionSettings();
		fillTextfields();
//		populateGenreChoiceBoxes();
	}
	
	public void populateGenreChoiceBoxes () {
        // The values in the choice box are set to null in the addNewGenre function.
        if(choiceBoxGenreType.getValue().equals("Fiction")){
            choiceBoxGenreName.setItems(bookAttributes.FictionGenres);
        }
        else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
            choiceBoxGenreName.setItems(bookAttributes.NonFictionGenres);
        }
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
        middleName=bookQuery.getString("middleName");
        lastName=bookQuery.getString("lastName");
        publisher=bookQuery.getString("publisher_name");
        isbn=bookQuery.getString("isbn");
        year=bookQuery.getInt("copyright");
        genre=bookQuery.getString("genre_name");
        genreType=bookQuery.getInt("genre_type");
        format=bookQuery.getInt("format");
        edition=bookQuery.getInt("edition");
        language=bookQuery.getString("language_name");
        pageCount=bookQuery.getInt("pages");
        
        return new Book(id,title,series,seriesPart,firstName,middleName,lastName,publisher,isbn,year,genre,edition,language,format,pageCount);
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
    		textFieldFormat.setText("Hardcover");
    	} else {
    		textFieldFormat.setText("Paperback");
    	}
    	// Series Part
    	if(book.getSeriesPart()==null) {
    		textFieldSeriesPart.setText("");
    	} else {
    		textFieldSeriesPart.setText(String.valueOf(book.getSeriesPart()));
    	}
    	
    	// Fill the choice boxes and auto-complete fields
    	TextFields.bindAutoCompletion(textFieldSeries,bookAttributes.series);
    	TextFields.bindAutoCompletion(textFieldAuthor,bookAttributes.authors);
    	TextFields.bindAutoCompletion(textFieldPublisher,bookAttributes.publishers);
    	
    	if(genreType==0) {
    		choiceBoxGenreType.setValue("Fiction");
    		choiceBoxGenreName.setItems(bookAttributes.FictionGenres);
    	} else {
    		choiceBoxGenreType.setValue("Non-Fiction");
    		choiceBoxGenreName.setItems(bookAttributes.NonFictionGenres);
    	}
    	
    	
    }
    
    public void editTextfields() {
    	// Hide the edit button and show the save button.
    	buttonEdit.setVisible(false);
    	buttonSave.setVisible(true);
    	buttonSave.setDisable(false);
    	buttonClose.setVisible(false);
    	buttonDiscard.setVisible(true);
    	
    	// Set the textfields to be editable
    	textFieldTitle.setEditable(true);
    	textFieldSeries.setEditable(true);
    	textFieldSeriesPart.setEditable(true);
    	textFieldAuthor.setEditable(true);
    	textFieldPublisher.setEditable(true);
    	textFieldISBN.setEditable(true);
    	textFieldCopyRight.setEditable(true);
    	textFieldPageCount.setEditable(true);
    	textFieldLanguage.setEditable(true);
    	
    	// Changes the visibility of certain objects
    	textFieldGenre.setVisible(false);
    	choiceBoxGenreName.setVisible(true);
    	choiceBoxGenreType.setVisible(true);
    	toggleButtonHardcover.setVisible(true);
    	toggleButtonPaperback.setVisible(true);
    	textFieldFormat.setVisible(false);
    	
    	//Set the current values of the book's attributes into-
    	//temporay variables for potential use
    	tempTitle=textFieldTitle.getText();
    	tempAuthor=textFieldAuthor.getText();
    	tempSeries=textFieldSeries.getText();
    	tempGenre=textFieldGenre.getText();
    	tempPublisher=textFieldPublisher.getText();
    	tempLanguage=textFieldLanguage.getText();
    	tempYear=textFieldCopyRight.getText();
    	if(textFieldSeriesPart.getText()!="") {
    		tempSeriesPart=textFieldSeriesPart.getText();
    	}
    	tempISBN=textFieldISBN.getText();
    	tempPageCount=textFieldPageCount.getText();
    	
    	//Set the "Format" buttons to the correct value
    	if(format==1) {
    		System.out.println("Hard");
    		toggleButtonHardcover.setSelected(true);
    		toggleButtonPaperback.setSelected(false);
    	} else {
    		System.out.println("Paper");
    		toggleButtonHardcover.setSelected(false);
    		toggleButtonPaperback.setSelected(true);
    	}
    	choiceBoxGenreName.setValue(tempGenre);
    	
    }
    
    public void saveChanges() {
    	// Save the changes to the database
    	setFieldsToNotEditable();
    	textFieldGenre.setText(choiceBoxGenreName.getValue());
    	String updateQuery=getNewBookInformation();
    	// Attempt to write the new values to the database
    	try {
    		connectionCommands.writeDatabase(updateQuery);
    	} catch(Exception e) {
    		System.err.println("Error! Unable to update book information!\nError occured in... \nClass: detailsController\nMethod: Save Changes"+e);
    	} finally {
    		
    	}
    	// Re-fill the texfields so that they now contain the most up to date data from the server.
    	try {
    		fillTextfields();
    		showNotification("Book info saved!", notificationGreen);
    	} catch(Exception e) {
    		showNotification("Book info not saved!", notificationRed);
    		System.err.println("Error! Unable to fill book information!\nError occured in... \nClass: detailsController\nMethod: Save Changes"+e);
    	} finally {
    		
    	}
    }
    
    public void discardChanges() {
    	//Reset the contents of the textboxes to their original state
    	setFieldsToNotEditable();
    	textFieldTitle.setText(tempTitle);
    	textFieldAuthor.setText(tempAuthor);
    	textFieldSeries.setText(tempSeries);
    	textFieldSeriesPart.setText(tempSeriesPart);
    	textFieldGenre.setText(tempGenre);
    	textFieldISBN.setText(tempISBN);
    	textFieldPublisher.setText(tempPublisher);
    	textFieldCopyRight.setText(tempYear);
    	textFieldPageCount.setText(tempPageCount);
    	textFieldLanguage.setText(tempLanguage);
    }
    
    private void setFieldsToNotEditable() {
    	// Hide the save button and show the edit button
    	buttonEdit.setVisible(true);
    	buttonSave.setVisible(false);
    	buttonClose.setVisible(true);
    	buttonDiscard.setVisible(false);
    	
    	// Set the textfields to be uneditable
    	textFieldTitle.setEditable(false);
    	textFieldSeries.setEditable(false);
    	textFieldSeriesPart.setEditable(false);
    	textFieldAuthor.setEditable(false);
    	textFieldPublisher.setEditable(false);
    	textFieldISBN.setEditable(false);
    	textFieldCopyRight.setEditable(false);
    	textFieldPageCount.setEditable(false);
    	textFieldLanguage.setEditable(false);
    	// Changes the visibility of certain objects
    	textFieldGenre.setVisible(true);
    	choiceBoxGenreName.setVisible(false);
    	choiceBoxGenreType.setVisible(false);
    	toggleButtonHardcover.setVisible(false);
    	toggleButtonPaperback.setVisible(false);
    	textFieldFormat.setVisible(true);
    }
    
    private String getNewBookInformation() {
    	// Get the updated information for the book
    	int authorID,publisherID,genreID,languageID,seriesID;
    	authorID=bookAttributes.bidiMapAuthors.getKey(textFieldAuthor.getText());
    	publisherID=bookAttributes.bidiMapPublishers.getKey(textFieldPublisher.getText());
    	languageID=bookAttributes.bidiMapLanguages.getKey(textFieldLanguage.getText());
    	seriesID=bookAttributes.bidiMapSeries.getKey(textFieldSeries.getText());
    	if(choiceBoxGenreType.getValue()=="Non-Fiction") {
    		genreID=bookAttributes.bidiMapNonFictionGenres.getKey(textFieldGenre.getText());
    	} else {
    		genreID=bookAttributes.bidiMapFictionGenres.getKey(textFieldGenre.getText());
    	}
    	BigInteger isbn = new BigInteger(textFieldISBN.getText());
    	year=Integer.parseInt(textFieldCopyRight.getText());
    	pageCount=Integer.parseInt(textFieldPageCount.getText());
    	seriesPart=Integer.parseInt(textFieldSeriesPart.getText());
    	if(textFieldSeriesPart.getText()!="") {
    		seriesPart=Integer.parseInt(textFieldSeriesPart.getText());
    	} else {
    		seriesPart=null;
    	}
    	if(toggleButtonHardcover.isSelected()) {
    		format=1;
    	} else {
    		format=0;
    	}
    	
    	// Create an update query and return the result.
    	String query=String.format(sqlCommands.updateBookInformation, authorID,publisherID,title,year,isbn,genreID,seriesID,seriesPart,format,pageCount,languageID,id );
    	return query;
    }
    
    public void showNotification(String notification,String color) {
    	labelNotification.setTextFill(Color.web(color));
        labelNotification.setText(notification);
        labelNotification.setVisible(true);
    }
//    @FXML
//    public void closePane() {
//    	Stage stage=(stage) buttonClose.getScene().getWindow();
//    	stage.close();
//    }
}
