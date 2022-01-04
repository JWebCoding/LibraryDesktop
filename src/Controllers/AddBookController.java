package Controllers;
import Models.BookAttributes;
import Models.ConnectionCommands;
import Models.SQLCommands;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javax.sql.rowset.CachedRowSet;
import org.controlsfx.control.textfield.TextFields;
import java.math.BigInteger;

public class AddBookController {
    ConnectionCommands connectionCommands = new ConnectionCommands();
    SQLCommands sqlCommands = new SQLCommands();
    BookAttributes bookAttributes=new BookAttributes();
    //Book Information pane elements
    @FXML
    TextField textFieldTitle;
    @FXML
    TextField textFieldISBN;
    @FXML
    TextField textFieldPages;
    @FXML
    TextField textFieldEdition;
    @FXML
    TextField textFieldCopyright;
    @FXML
    ChoiceBox<String> choiceBoxPublisher;
    @FXML
    ChoiceBox<String> choiceBoxLanguage;
    @FXML
    ChoiceBox<String> choiceBoxAuthor;
    @FXML
    ChoiceBox<String> choiceBoxGenreType;
    @FXML
    ChoiceBox<String> choiceBoxGenreName;
    @FXML
    ChoiceBox<String> choiceBoxSeries;
    @FXML
    TextField textFieldSeriesPart;
    @FXML
    ToggleButton toggleButtonPaperback;
    @FXML
    ToggleButton toggleButtonHardcover;
    @FXML
    ToggleButton toggleButtonNo;
    @FXML
    ToggleButton toggleButtonYes;
    // New Author pane elements
    @FXML
    TextField textFieldAuthorLname;
    @FXML
    TextField textFieldAuthorMname;
    @FXML
    TextField textFieldAuthorFname;
    @FXML
    TextField textFieldAuthorLocation;
    @FXML
    TextField textFieldAuthorBirth;
    @FXML
    TextField textFieldAuthorDeath;
    // New Publisher pane elements
    @FXML
    TextField textFieldPublisherName;
    @FXML
    TextField textFieldPublisherLocation;
    // New Genre pane elements
    @FXML
    TextField textFieldGenreName;
    @FXML
    ChoiceBox<String> choiceBoxNewGenreType;
    // New Series pane elements
    @FXML
    TextField textFieldNewSeriesName;
    // New Language pane elements
    @FXML
    TextField textFieldNewLanguageName;
    @FXML
    TextField textFieldNewLanguageSuffix;
    @FXML TextField textFieldAuthor;
    @FXML TextField textFieldPublisher;
    
    @FXML Label labelNotificationTitle;
    @FXML Label labelNotificationAuthor;
    @FXML Label labelNotificationPublisher;
    @FXML Label labelNotificationISBN;
    @FXML Label labelNotificationCopyright;
    @FXML Label labelNotificationPages;
    @FXML Label labelNotificationGenre;
    @FXML Label labelNotificationLanguage;

    // Misc elements
    @FXML
    Label labelNotification;
    @FXML
    Button buttonAddBook;

    // Declare variables
    String query;
    String notificationGreen="#00ff00",notificationRed="#ff0000";

    // Arrays
   String arrayAuthors[];
   
    public void initialize() throws Exception {
    	
        ObservableList<String> genreTypes = FXCollections.observableArrayList("Fiction", "Non-Fiction");
        choiceBoxNewGenreType.setItems(genreTypes);
        choiceBoxGenreType.setItems(genreTypes);
        bookAttributes.createAuthorHashMap();
        bookAttributes.createPublisherHashMap();      
        bookAttributes.createFictionHashMap();
        bookAttributes.createNonFictionHashMap();
        bookAttributes.createLanguageHashMap();
        bookAttributes.createSeriesHashMap();
        bindTextfieldSuggestions();
        setChoiceBoxContents();
        setValues();
        populateGenreChoiceBoxes();
        closeNotification();
  
    }

    //////////////////////////////////////////////////////////////
    //  Methods for adding new items to the database

    public void addBook() throws Exception {
    	Integer authorID,publisherID,pages,edition,copyright,genreID=0,languageID,seriesID,seriesPart,format=null;
    	String title;
    	
        // Check if all fields have content
        if (!validateBookInformation()) {
            showNotification("Please fill the highlighted fields", notificationGreen);
        } else {
            // Get the values for the different variables.
            title = textFieldTitle.getText();
            title=checkForApostrophes(title);
            authorID = bookAttributes.bidiMapAuthors.getKey(textFieldAuthor.getText());
            publisherID = bookAttributes.bidiMapPublishers.getKey(textFieldPublisher.getText());
            pages = Integer.parseInt(textFieldPages.getText());
            copyright = Integer.parseInt(textFieldCopyright.getText());
            languageID = bookAttributes.bidiMapLanguages.getKey(choiceBoxLanguage.getValue());
            seriesID = bookAttributes.bidiMapSeries.getKey(choiceBoxSeries.getValue());
            
            // If statements for the values that need them
            if(textFieldISBN.getText().equals("null")){
                BigInteger isbn = new BigInteger("null");
            }
            BigInteger isbn = new BigInteger(textFieldISBN.getText());
            
            // Acquire genreID value
            if(choiceBoxGenreType.getValue().equals("Fiction")){
                genreID = bookAttributes.bidiMapFictionGenres.getKey(choiceBoxGenreName.getValue());
            }
            else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
                genreID=bookAttributes.bidiMapNonFictionGenres.getKey(choiceBoxGenreName.getValue());
            }
            
            if (textFieldEdition.getText().isEmpty()) {
                edition = null;
            } else {
                edition = Integer.parseInt(textFieldEdition.getText());
            }
            
            if (textFieldSeriesPart.getText().isEmpty()) {
                seriesPart = null;
            } else {
                seriesPart = Integer.parseInt(textFieldSeriesPart.getText());
            }
            
            if (toggleButtonHardcover.isSelected()) {
                format = 1;
            } else {
                format = 0;
            }

            query = String.format(sqlCommands.insertIntoBook, authorID, publisherID, title, copyright, isbn, edition, genreID, seriesPart, format, pages, languageID, seriesID);
            try {
            	connectionCommands.writeDatabase(query);
            	emptyBookInformation();
                resetTextFieldEffects();
                setValues();
                showNotification(title + "\nwas added successfully.", notificationGreen);
            } catch(Exception e) {
            	System.err.print("Unable to add book");
            } finally {
            	
            }
        }
    }

    private boolean validateBookInformation() {
        int errorCount = 0;
        if (textFieldTitle.getText().isBlank()) {
        	labelNotificationTitle.setVisible(true);
            errorCount++;
        }
        if (textFieldISBN.getText().isBlank() || textFieldISBN.getText().length()>13 || textFieldISBN.getText().matches("[0-9]+")==false) {
        	labelNotificationISBN.setVisible(true);
            errorCount++;
        }
        if (textFieldCopyright.getText().length()>0) {
        	if (textFieldCopyright.getText().length()!=4 || textFieldCopyright.getText().matches("[0-9]+")==false) {
        		labelNotificationCopyright.setVisible(true);
                errorCount++;
        	}
        }
        if (textFieldPages.getText().isBlank() || textFieldPages.getText().matches("[0-9]+")==false) {
        	labelNotificationPages.setVisible(true);
            errorCount++;
        }
        if (textFieldEdition.getText().length()>0) {
        	if (textFieldEdition.getText().matches("[0-9]+")==false) {
                errorCount++;
        	}
        }
        if (choiceBoxGenreType.getValue() == null || choiceBoxGenreName.getValue()==null) {
        	labelNotificationGenre.setVisible(true);
            errorCount++;
        }
        if (choiceBoxLanguage.getValue() == null) {
        	labelNotificationLanguage.setVisible(true);
            errorCount++;
        }
        if (choiceBoxSeries.getValue() == null) {
            errorCount++;
        }
        if (textFieldSeriesPart.getText().length()>0) {
        	if (textFieldSeriesPart.getText().matches("[0-9]+")==false) {
                errorCount++;
        	}
        }
        if (errorCount > 0) {
            showNotification("Please fill the highlighted fields", "ff0000");
            return false;
        }
        return true;
    }

    private void emptyBookInformation() {
        textFieldTitle.setText("");
        textFieldAuthor.setText("");
        textFieldPublisher.setText("");
        textFieldISBN.setText("");
        textFieldCopyright.setText("");
        textFieldPages.setText("");
        textFieldEdition.setText("");
        choiceBoxGenreType.setValue("Non-Fiction");
        choiceBoxGenreName.setValue("");
        choiceBoxLanguage.setValue("");
        choiceBoxSeries.setValue("");
        textFieldSeriesPart.setText("");
    }

    public void addNewAuthor() throws Exception {
        if (!validateAuthorInformation()) {
        	
        } else {
        	String authorFirstName,authorMiddleName,authorLastName,authorLocation = null;
        	Integer authorYearBirth = null,authorYearDeath = null;
            // Pulls the proper data into the appropriate variables
            authorFirstName = textFieldAuthorFname.getText();
            if(textFieldAuthorMname.getText()=="") {
            	authorMiddleName="NULL";
            } else {
            	authorMiddleName=("'"+textFieldAuthorMname.getText()+"'");
            }
            authorLastName = textFieldAuthorLname.getText();
           
            if (!textFieldAuthorLocation.getText().isEmpty()) {
            	authorLocation = textFieldAuthorLocation.getText();
            }
            
            if (!textFieldAuthorBirth.getText().isEmpty()) {
                authorYearDeath = Integer.parseInt(textFieldAuthorBirth.getText());
            }
            if (!textFieldAuthorDeath.getText().isEmpty()) {
                authorYearDeath = Integer.parseInt(textFieldAuthorDeath.getText());
            }

            // Create query and write new author to database
            query = String.format(sqlCommands.insertIntoAuthor, authorFirstName, authorMiddleName, authorLastName, authorLocation, authorYearBirth, authorYearDeath);
            connectionCommands.writeDatabase(query);
            if(authorMiddleName=="NULL") {
            	showNotification(authorFirstName + " " + authorLastName + "\nhas been added.", notificationGreen);
            } else {
            	showNotification(authorFirstName + " " +authorMiddleName + " " + authorLastName + "\nhas been added.", notificationGreen);
            }

            // Refresh the author hashmap & clear the fields
            bookAttributes.createAuthorHashMap();
            bindTextfieldSuggestions();
            emptyAuthorFields();
        }
    }

    private boolean validateAuthorInformation() {
        // Validate textField Contents
        int errorCount = 0;
        if (textFieldAuthorFname.getText().isEmpty()) {
            errorCount++;
        }
        if (textFieldAuthorLname.getText().isEmpty()) {
            errorCount++;
        }
        if (errorCount > 0) {
            showNotification("Please fill the highlighted fields", notificationRed);
            return false;
        }
        return true;
    }
    
    private void emptyAuthorFields() {
    	textFieldAuthorFname.setText("");
        textFieldAuthorMname.setText("");
        textFieldAuthorLname.setText("");
        textFieldAuthorLocation.setText("");
        textFieldAuthorBirth.setText("");
        textFieldAuthorDeath.setText("");
        query = "";
        resetTextFieldEffects();
    }

    public void addNewPublisher() throws Exception {
        if (!validatePublisherInformation()) {
        } else {
            // Create the relevant variables
            String publisherName = textFieldPublisherName.getText();
            publisherName=checkForApostrophes(publisherName);
            String publisherLocation = null;
            if (!textFieldPublisherLocation.getText().isEmpty()) {
            	publisherLocation = textFieldPublisherLocation.getText();
            }

            // Create query and insert into the database
            query = String.format(sqlCommands.insertIntoPublisher, publisherName, publisherLocation);
            connectionCommands.writeDatabase(query);
            showNotification(publisherName + "\nhas been added.", notificationGreen);

            // Refresh the publisher hashmap & clear the fields
            bookAttributes.createPublisherHashMap();
            bindTextfieldSuggestions();
            emptyPublisherInformation();
        }
    }

    private boolean validatePublisherInformation() {
        // Validate textField Contents
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        int errorCount = 0;
        if (textFieldPublisherName.getText().isEmpty()) {
            textFieldPublisherName.setEffect(colorAdjustRequired);
            errorCount++;
        }
        if (errorCount > 0) {
            showNotification("Please fill the highlighted fields", notificationRed);
            return false;
        }
        return true;
    }
    private void emptyPublisherInformation() {
    	textFieldPublisherName.setText("");
        textFieldPublisherLocation.setText("");
        query = "";
        resetTextFieldEffects();
    }

    // Add new genre to database
    public void addNewGenre() throws Exception {
        if (!validateGenreInformation()) {

        } else {
            // Create the relevant variables
            String genreName = textFieldGenreName.getText();
            Integer genreType = null;
            if (choiceBoxNewGenreType.getValue().equals("Fiction")) {
                genreType = 0;
            } else {
                genreType = 1;
            }
            
            // Create query and insert into the database
            query = String.format(sqlCommands.insertIntoGenre, genreName, genreType);
            connectionCommands.writeDatabase(query);
            showNotification(genreName + "\nhas been added.", notificationGreen);

            // Refresh genre hashmap
            if(genreType==1) {
            	bookAttributes.createNonFictionHashMap();
            } else {
            	bookAttributes.createFictionHashMap();
            }
            
            emptyGenreInformation();
            populateGenreChoiceBoxes();
        }
    }

    // Validate the provided genre information
    private boolean validateGenreInformation() {
        // Validate textField Contents
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        int errorCount = 0;
        if (textFieldGenreName.getText().isEmpty()) {
            textFieldGenreName.setEffect(colorAdjustRequired);
            errorCount++;
        }
        if (choiceBoxNewGenreType.getValue() == null) {
            choiceBoxNewGenreType.setEffect(colorAdjustRequired);
        }
        if (errorCount > 0) {
            showNotification("Please fill the highlighted fields", notificationRed);
            return false;
        }
        return true;
    }

    private void emptyGenreInformation() {
    	textFieldGenreName.setText("");
        choiceBoxNewGenreType.setValue("Non-Fiction");
        query = "";
//        choiceBoxGenreName.getItems().clear();
        resetTextFieldEffects();
    }
    
    public void addNewSeries() throws Exception {
    if(!validateSeriesInformation()){ }
    else {
            // Create the relevant variables
            String seriesName=textFieldNewSeriesName.getText();

            // Create query and insert into the database
            query=(String.format(sqlCommands.insertIntoSeries,seriesName));
            connectionCommands.writeDatabase(query);
            showNotification(seriesName+"\nwas added.",notificationGreen);

            // Reset obvListSeries box
            bookAttributes.createSeriesHashMap();
            emptySeriesInformation();
            setChoiceBoxContents();
        }
    }

    private boolean validateSeriesInformation () {
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        if(textFieldNewSeriesName.getText().isEmpty()) {
            textFieldNewSeriesName.setEffect(colorAdjustRequired);
            showNotification("Please fill the highlighted field",notificationRed);
            return false;
        }
        else{ return true;}
    }
    
    private void emptySeriesInformation() {
    	textFieldNewSeriesName.setText("");
        resetTextFieldEffects();
    }

    public void addNewLanguage () throws Exception {
        if (!validateLanguageInformation()) { }
        else {
            String languageName=textFieldNewLanguageName.getText();
            String languageSuffix=textFieldNewLanguageSuffix.getText();

            query=String.format(sqlCommands.insertIntoLanguage,languageName,languageSuffix);
            connectionCommands.writeDatabase(query);
            showNotification(languageName+"\nwas added",notificationGreen);

            bookAttributes.createLanguageHashMap();
            emptyLanguageInformation();
        }
    }

    private boolean validateLanguageInformation () {
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        Integer errorCount=0;
        if(textFieldNewLanguageName.getText().isEmpty()) { textFieldNewLanguageName.setEffect(colorAdjustRequired); errorCount++; }
        if(textFieldNewLanguageSuffix.getText().isEmpty()) { textFieldNewLanguageSuffix.setEffect(colorAdjustRequired); errorCount++; }
        if (errorCount>0){ showNotification("Please fill the highlighted fields",notificationRed); return false;  }
        else { return true; }
    }
    
    private void emptyLanguageInformation() {
    	textFieldNewLanguageName.setText("");
        textFieldNewLanguageSuffix.setText("");
        resetTextFieldEffects();
    }

////////////////////////////////////////////////////////////
        // Methods to run during initialization
        public void populateGenreChoiceBoxes () {
            // The values are set to null in the addNewGenre function.
            if(choiceBoxGenreType.getValue().equals("Fiction")){
                choiceBoxGenreName.setItems(bookAttributes.obvListFictionGenres);
            }
            else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
                choiceBoxGenreName.setItems(bookAttributes.obvListNonFictionGenres);
            }
        }

        private void setValues () {
            choiceBoxGenreType.setValue("Non-Fiction");
            choiceBoxNewGenreType.setValue("Non-Fiction");
            toggleButtonHardcover.setSelected(true);
            toggleButtonPaperback.setSelected(false);
            choiceBoxSeries.setValue("");
        }
        
        private void bindTextfieldSuggestions() {
        	TextFields.bindAutoCompletion(textFieldAuthor,bookAttributes.obvListAuthors);
        	TextFields.bindAutoCompletion(textFieldPublisher,bookAttributes.obvListPublishers);
        }
        
        private void setChoiceBoxContents() {
        	choiceBoxAuthor.setItems(bookAttributes.obvListAuthors);
        	choiceBoxPublisher.setItems(bookAttributes.obvListPublishers);
        	choiceBoxLanguage.setItems(bookAttributes.obvListLanguages);
        	choiceBoxSeries.setItems(bookAttributes.obvListSeries);
        }

        private void resetTextFieldEffects () {
        	labelNotificationTitle.setVisible(false);
        	labelNotificationAuthor.setVisible(false);
        	labelNotificationPublisher.setVisible(false);
        	labelNotificationISBN.setVisible(false);
        	labelNotificationCopyright.setVisible(false);
        	labelNotificationPages.setVisible(false);
        	labelNotificationGenre.setVisible(false);
        	labelNotificationLanguage.setVisible(false);

        }

        // Check to ensure that an apostraphe in the title is properly formatted
        private String checkForApostrophes(String text) {
        	String string=text;
    		int location=0;
    		
    		if(string.contains("'")) {
    			location=string.indexOf("'");
    			StringBuilder sb=new StringBuilder(string);
        		sb.insert(location, "'");
        		string=sb.toString();
    		}
        	return string;
        }

////////////////////////////////////////////////////////////
        // Misc methods
        private void showNotification (String notification, String color){
            labelNotification.setTextFill(Color.web(color));
            labelNotification.setText(notification);
            labelNotification.setVisible(true);
        }

        private void closeNotification () {
            labelNotification.setTextFill(Color.web("#ffffff"));
            labelNotification.setText("");
            labelNotification.setVisible(false);
        }
}