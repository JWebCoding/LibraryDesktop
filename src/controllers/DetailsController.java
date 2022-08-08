package controllers;

import javafx.stage.Stage;
import models.Book;
import models.BookAttributes;
import java.util.ArrayList;
import java.util.Objects;
import javax.sql.rowset.CachedRowSet;
import models.QueryFactory;
import org.controlsfx.control.textfield.TextFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

public class DetailsController {

	@FXML TextField textFieldTitle;
	@FXML TextField textFieldSeries;
	@FXML TextField textFieldSeriesPart;
	@FXML TextField textFieldAuthor;
	@FXML TextField textFieldISBN;
	@FXML TextField textFieldCopyRight;
	@FXML TextField textFieldLanguage;
	@FXML TextField textFieldEdition;
	@FXML TextField textFieldFormat;
	@FXML TextField textFieldPageCount;
	@FXML TextField textFieldPublisher;
	@FXML TextField textFieldGenre;
	@FXML TextArea textAreaNotes;
	@FXML Button buttonEdit;
	@FXML Button buttonSave;
	@FXML Button buttonClose;
	@FXML Button buttonDiscard;
	@FXML Label labelNotification;
	@FXML
	ToggleButton toggleButtonPaperback;
    @FXML
    ToggleButton toggleButtonHardcover;
    @FXML
    ChoiceBox<String> choiceBoxGenreType;
    @FXML
    ChoiceBox<String> choiceBoxGenreName;

    QueryFactory queryFactory = new QueryFactory();
	BookAttributes bookAttributes=new BookAttributes();
	String title,firstName,middleName,lastName,isbn,series,publisher,genre,language,notes;
    String tempTitle,tempAuthor,tempISBN,tempCopyright,tempEdition,tempPageCount,tempGenre,tempSeries,tempSeriesPart,tempPublisher,tempLanguage,tempNotes;
    String notificationGreen="#00ff00",notificationRed="#ff0000";
    Integer id,copyright,format,edition,pageCount,genreType,seriesPart;
	CachedRowSet bookQuery=null;

	public void initialize() throws Exception{
		ObservableList<String> genreTypes = FXCollections.observableArrayList("Fiction", "Non-Fiction");
		choiceBoxGenreType.setItems(genreTypes);
		bookAttributes.createAuthorHashMap();
		bookAttributes.createPublisherHashMap();
		bookAttributes.createGenreHashMaps();
		bookAttributes.createLanguageHashMap();
		bookAttributes.createSeriesHashMap();
		fillTextfields();
		populateGenreChoiceBoxes();
	}

	private void populateGenreChoiceBoxes () {
        // The values in the choice box are set to null in the addNewGenre function.
        if(choiceBoxGenreType.getValue().equals("Fiction")){
            choiceBoxGenreName.setItems(bookAttributes.obvListFictionGenres);
        }
        else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
            choiceBoxGenreName.setItems(bookAttributes.obvListNonFictionGenres);
        }
    }

    private void getBookData() throws Exception{
		ArrayList<Object> bookIDList = new ArrayList<>();
		bookIDList.add(Book.getBookID());
    	bookQuery=queryFactory.readFromDatabase("specificBook",bookIDList);
    }

    private Book createBook() throws Exception {
    	getBookData();
    	bookQuery.first();

        id=bookQuery.getInt("bookID");
        title=bookQuery.getString("title");
        series=bookQuery.getString("series_name");
        seriesPart=bookQuery.getInt("series_part");
        if(seriesPart==0) {seriesPart=null;}
        firstName=bookQuery.getString("first_name");
        middleName=bookQuery.getString("middle_name");
        lastName=bookQuery.getString("last_name");
        publisher=bookQuery.getString("publisher_name");
        isbn=bookQuery.getString("isbn");
		edition=bookQuery.getInt("edition");
        copyright=bookQuery.getInt("copyright");
        genre=bookQuery.getString("genre_name");
        genreType=bookQuery.getInt("genre_type");
        format=bookQuery.getInt("format");
        edition=bookQuery.getInt("edition");
        language=bookQuery.getString("language_name");
        pageCount=bookQuery.getInt("pages");
		notes=bookQuery.getString("notes");

        return new Book(id,title,series,seriesPart,firstName,middleName,lastName,publisher,isbn,copyright,genre,edition,language,format,pageCount,notes);
    }

    private void fillTextfields() throws Exception {
    	genreType=null;
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
		textAreaNotes.setText(book.getNotes());

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
    	TextFields.bindAutoCompletion(textFieldSeries,bookAttributes.obvListSeries);
    	TextFields.bindAutoCompletion(textFieldAuthor,bookAttributes.obvListAuthors);
    	TextFields.bindAutoCompletion(textFieldPublisher,bookAttributes.obvListPublishers);
    	if(genreType==0) {
    		choiceBoxGenreType.setValue("Fiction");
    	} else {
    		choiceBoxGenreType.setValue("Non-Fiction");
    	}
    }

    public void enableFieldsEditable() {
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
		textFieldEdition.setEditable(true);
    	textFieldCopyRight.setEditable(true);
    	textFieldPageCount.setEditable(true);
    	textFieldLanguage.setEditable(true);
		textAreaNotes.setEditable(true);

    	// Changes the visibility of certain objects
    	textFieldGenre.setVisible(false);
    	choiceBoxGenreName.setVisible(true);
    	choiceBoxGenreType.setVisible(true);
    	toggleButtonHardcover.setVisible(true);
    	toggleButtonPaperback.setVisible(true);
    	textFieldFormat.setVisible(false);

    	//Set the current values of the book's attributes into-
    	//temporary variables for potential use
    	tempTitle=textFieldTitle.getText();
    	tempAuthor=textFieldAuthor.getText();
    	tempSeries=textFieldSeries.getText();
    	tempGenre=textFieldGenre.getText();
    	tempPublisher=textFieldPublisher.getText();
    	tempLanguage=textFieldLanguage.getText();
    	tempCopyright=textFieldCopyRight.getText();
		tempEdition=textFieldEdition.getText();
    	if(!Objects.equals(textFieldSeriesPart.getText(), "")) {
    		tempSeriesPart=textFieldSeriesPart.getText();
    	}
    	tempISBN=textFieldISBN.getText();
    	tempPageCount=textFieldPageCount.getText();
		tempNotes=textAreaNotes.getText();

    	//Set the "Format" buttons to the correct value
    	if(format==1) {
    		toggleButtonHardcover.setSelected(true);
    		toggleButtonPaperback.setSelected(false);
    	} else {
    		toggleButtonHardcover.setSelected(false);
    		toggleButtonPaperback.setSelected(true);
    	}
    	choiceBoxGenreName.setValue(tempGenre);

    }

    public void saveChanges() {
		disableFieldsEditable();
    	textFieldGenre.setText(choiceBoxGenreName.getValue());
    	// Attempt to write the new values to the database
    	try {
			ArrayList<Object> editedBookData=getEditedBookInformation();
			boolean success=queryFactory.writeToDatabase("update","book", editedBookData);
			// Re-fill the text-fields so that they now contain the most up-to-date data from the server.
			if(success){
				fillTextfields();
				showNotification("Book info saved!", notificationGreen);
			} else  {
				showNotification("An Error occurred. Please check fields.", notificationGreen);
			}
    	} catch(Exception e) {
			showNotification("Book info not saved!", notificationRed);
    		System.err.println("Error! Unable to update book information in database!\nError occurred in... \nClass: detailsController\nMethod: Save Changes: "+e);
			e.printStackTrace();
    	}
    }

    public void discardChanges() {
    	//Reset the contents of the textboxes to their original state
		disableFieldsEditable();
    	textFieldTitle.setText(tempTitle);
    	textFieldAuthor.setText(tempAuthor);
    	textFieldSeries.setText(tempSeries);
    	textFieldSeriesPart.setText(tempSeriesPart);
    	textFieldGenre.setText(tempGenre);
		textFieldEdition.setText(tempEdition);
    	textFieldISBN.setText(tempISBN);
    	textFieldPublisher.setText(tempPublisher);
    	textFieldCopyRight.setText(tempCopyright);
    	textFieldPageCount.setText(tempPageCount);
    	textFieldLanguage.setText(tempLanguage);
		textAreaNotes.setText(tempNotes);
    }

    private void disableFieldsEditable() {
    	// Hide the save button and show the edit button
    	buttonEdit.setVisible(true);
    	buttonSave.setVisible(false);
    	buttonClose.setVisible(true);
    	buttonDiscard.setVisible(false);

    	// Set the text-fields to be uneditable
    	textFieldTitle.setEditable(false);
    	textFieldSeries.setEditable(false);
    	textFieldSeriesPart.setEditable(false);
    	textFieldAuthor.setEditable(false);
    	textFieldPublisher.setEditable(false);
    	textFieldISBN.setEditable(false);
		textFieldEdition.setEditable(false);
    	textFieldCopyRight.setEditable(false);
    	textFieldPageCount.setEditable(false);
    	textFieldLanguage.setEditable(false);
		textAreaNotes.setEditable(false);
    	// Changes the visibility of certain objects
    	textFieldGenre.setVisible(true);
    	choiceBoxGenreName.setVisible(false);
    	choiceBoxGenreType.setVisible(false);
    	toggleButtonHardcover.setVisible(false);
    	toggleButtonPaperback.setVisible(false);
    	textFieldFormat.setVisible(true);
    }

    // Get this in the correct order!
    private ArrayList<Object> getEditedBookInformation() {
		Integer nullInteger=0;
		ArrayList<Object> editsList =new ArrayList<>();
    	// Add the edited information to an ArrayList
		editsList.add(Book.getBookID());
        editsList.add(bookAttributes.bidiMapAuthors.getKey(textFieldAuthor.getText()));
		editsList.add(bookAttributes.bidiMapPublishers.getKey(textFieldPublisher.getText()));
    	title=textFieldTitle.getText();
		editsList.add(checkForApostrophes(title));
		editsList.add(Integer.parseInt(textFieldCopyRight.getText()));
		editsList.add(Long.parseLong(textFieldISBN.getText()));
		// Get the edition
		if(textFieldEdition.getText().isEmpty()){
			editsList.add(nullInteger);
		} else {
			editsList.add(Integer.parseInt(textFieldEdition.getText()));
		}
		// Get the genre
        if(choiceBoxGenreType.getValue().equals("Non-Fiction")) {
			editsList.add(bookAttributes.bidiMapNonFictionGenres.getKey(textFieldGenre.getText()));
        } else {
			editsList.add(bookAttributes.bidiMapFictionGenres.getKey(textFieldGenre.getText()));
        }
		// Get the series
		editsList.add(bookAttributes.bidiMapSeries.getKey(textFieldSeries.getText()));
		// Get the series part
        if (textFieldSeriesPart.getText().isEmpty()) {
			editsList.add(nullInteger);
        } else {
			editsList.add(Integer.parseInt(textFieldSeriesPart.getText()));
        }
		// get the format
        if(toggleButtonHardcover.isSelected()) {
			editsList.add(1);
        } else {
			editsList.add(0);
        }
		editsList.add(Integer.parseInt(textFieldPageCount.getText()));
		editsList.add(bookAttributes.bidiMapLanguages.getKey(textFieldLanguage.getText()));
		if(textAreaNotes.getText()==null){
			editsList.add("");
		} else {
			editsList.add(textAreaNotes.getText());
		}

		return editsList;
    }

	private String checkForApostrophes(String text) {
		String string=text;
		int location;

		if(string.contains("'")) {
			location=string.indexOf("'");
			StringBuilder sb=new StringBuilder(string);
			sb.insert(location, "'");
			string=sb.toString();
		}
		return string;
	}

    private void showNotification(String notification,String color) {
    	labelNotification.setTextFill(Color.web(color));
        labelNotification.setText(notification);
        labelNotification.setVisible(true);
    }

	public void closeWindow(){
		Stage stage = (Stage) buttonClose.getScene().getWindow();
		stage.close();
	}

}
