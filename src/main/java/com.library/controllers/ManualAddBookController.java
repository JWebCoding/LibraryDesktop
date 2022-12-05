package com.library.controllers;
import com.library.models.BookAttributes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.library.models.QueryFactory;
import org.controlsfx.control.textfield.TextFields;
import java.util.ArrayList;

public class ManualAddBookController {
    BookAttributes bookAttributes=new BookAttributes();
    QueryFactory queryFactory=new QueryFactory();

    //Book Information pane elements
    @FXML TextField textFieldTitle;
    @FXML TextField textFieldSubtitle;
    @FXML TextField textFieldISBN;
    @FXML TextField textFieldPages;
    @FXML TextField textFieldEdition;
    @FXML TextField textFieldCopyright;
    @FXML ChoiceBox<String> choiceBoxLanguage;
    @FXML ChoiceBox<String> choiceBoxGenreType;
    @FXML ChoiceBox<String> choiceBoxGenreName;
    @FXML ChoiceBox<String> choiceBoxSeries;
    @FXML TextField textFieldSeriesPart;
    @FXML RadioButton radioButtonHardcover;
    @FXML RadioButton radioButtonPaperback;
    // New Author pane elements
    @FXML TextField textFieldAuthorName;
    @FXML TextField textFieldAuthorFirstName;
    @FXML TextField textFieldAuthorMiddleName;
    @FXML TextField textFieldAuthorLastName;
    // New Publisher pane elements
    @FXML TextField textFieldPublisherName;
    @FXML TextField textFieldPublisherLocation;
    // New Genre pane elements
    @FXML TextField textFieldGenreName;
    @FXML ChoiceBox<String> choiceBoxNewGenreType;
    // New Series pane elements
    @FXML TextField textFieldNewSeriesName;
    // New Language pane elements
    @FXML TextField textFieldNewLanguageName;
    @FXML TextField textFieldNewLanguageSuffix;
    @FXML TextField textFieldAuthor;
    @FXML TextField textFieldPublisher;
    @FXML TextArea textAreaNotes;

    @FXML CheckBox checkBoxPreserveData;
    @FXML CheckBox checkBoxDisableISBN;

    // Misc elements
    @FXML Label labelNotification;
    @FXML Button buttonAddBook;

    // Declare variables
    String notificationGreen="-fx-text-fill: rgba(0,255,0,1)",notificationRed="-fx-text-fill: rgba(255,0,0,1)",errorColor="-fx-background-color: rgba(255,0,0,.6) ";
    Integer nullInteger=0;

    // This is the array list that will be used to pass collections of elements to the other classes.
    ArrayList<Object> elementsArrayList= new ArrayList<>();
   
    public void initialize() throws Exception {
        ObservableList<String> genreTypes = FXCollections.observableArrayList("Fiction", "Non-Fiction");
        choiceBoxNewGenreType.setItems(genreTypes);
        choiceBoxGenreType.setItems(genreTypes);
        bookAttributes.createAuthorHashMap();
        bookAttributes.createPublisherHashMap();      
        bookAttributes.createGenreHashMaps();
        bookAttributes.createLanguageHashMap();
        bookAttributes.createSeriesHashMap();
        bindTextFieldSuggestions();
        setChoiceBoxContents();

        setValues();
        populateGenreChoiceBoxes();

        closeNotification();
  
    }

    //////////////////////////////////////////////////////////////
    //  Methods for adding new items to the database

    public void addBook() throws Exception {

        // Check if all fields have content
        if (!validateBookInformation()) {
            showNotification("Please fill the\nrequired fields", notificationRed);
        } else {
            // Add the values to the array list.
            elementsArrayList.add(bookAttributes.bidiMapAuthors.getKey(textFieldAuthor.getText()));
            elementsArrayList.add(bookAttributes.bidiMapPublishers.getKey(textFieldPublisher.getText()));
            String title = textFieldTitle.getText();
            elementsArrayList.add(checkForApostrophes(title));
            String subtitle = textFieldSubtitle.getText();
            elementsArrayList.add(checkForApostrophes(subtitle));
            elementsArrayList.add(Integer.parseInt(textFieldCopyright.getText()));

            //Check if ISBN exists
            if(!checkBoxDisableISBN.isSelected()){
                elementsArrayList.add(Long.parseLong(textFieldISBN.getText()));
            }else{
                elementsArrayList.add(nullInteger);
            }

            // Get Book Edition
            if (textFieldEdition.getText().isEmpty()) {
                elementsArrayList.add(nullInteger);
            } else {
                elementsArrayList.add(Integer.parseInt(textFieldEdition.getText()));
            }

            // Get Book Genre
            if(choiceBoxGenreType.getValue().equals("Fiction")){
                elementsArrayList.add(bookAttributes.bidiMapFictionGenres.getKey(choiceBoxGenreName.getValue()));
            }
            else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
                elementsArrayList.add(bookAttributes.bidiMapNonFictionGenres.getKey(choiceBoxGenreName.getValue()));
            }

            elementsArrayList.add(bookAttributes.bidiMapSeries.getKey(choiceBoxSeries.getValue()));

            // Get Book Series Part
            if (textFieldSeriesPart.getText().isEmpty()) {
                elementsArrayList.add(nullInteger);
            } else {
                elementsArrayList.add(Integer.parseInt(textFieldSeriesPart.getText()));
            }

            // Get Book Format
            if (radioButtonHardcover.isSelected()) {
                elementsArrayList.add(1);
            } else {
                elementsArrayList.add(0);
            }
            elementsArrayList.add(Integer.parseInt(textFieldPages.getText()));
            elementsArrayList.add(bookAttributes.bidiMapLanguages.getKey(choiceBoxLanguage.getValue()));
            elementsArrayList.add(textAreaNotes.getText());

            try {
                // Attempt to write the new book to the database and then display an appropriate notification.
            	boolean success=queryFactory.writeToDatabase("insert","book", elementsArrayList);
                if(success){
                    showNotification(title + "\nwas added successfully.", notificationGreen);
                } else {
                    showNotification(title + "\nwas not added properly.", notificationRed);
                }
//                 Save the data in the fields if the requisite checkbox is checked.
                if(!checkBoxPreserveData.isSelected()){
                    emptyBookInformation();
                    setValues();
                }
                resetNewBookTextFieldEffects();
                elementsArrayList.clear();

            } catch(Exception e) {
                showNotification(title + "\nwas not added properly.", notificationRed);
                throw new Exception("Unable to add book:\n"+e);
            } finally {

                elementsArrayList.clear();
            }
        }
    }

    private boolean validateBookInformation() {
        int errorCount = 0;

        if (textFieldTitle.getText().isEmpty()) {
            textFieldTitle.setStyle(errorColor);
            errorCount++;
        }
        if (textFieldAuthor.getText().isEmpty()){
            textFieldAuthor.setStyle(errorColor);
            errorCount++;
        }
        if (textFieldPublisher.getText().isEmpty()){
            textFieldPublisher.setStyle(errorColor);
            errorCount++;
        }
        if (!checkBoxDisableISBN.isSelected()){
            if (textFieldISBN.getText().isEmpty() || textFieldISBN.getText().length()>13 || !textFieldISBN.getText().matches("[0-9]+")) {
                textFieldISBN.setStyle(errorColor);
                errorCount++;
            }
        }
        if (textFieldCopyright.getText().length()>0) {
        	if (textFieldCopyright.getText().length()!=4 || !textFieldCopyright.getText().matches("[0-9]+")) {
                textFieldCopyright.setStyle(errorColor);
                errorCount++;
        	}
        } else {
            textFieldCopyright.setStyle(errorColor);
            errorCount++;
        }
        if (textFieldPages.getText().isEmpty() || !textFieldPages.getText().matches("[0-9]+")) {
            textFieldPages.setStyle(errorColor);
            errorCount++;
        }
        if (choiceBoxGenreName.getValue()==null) {
            choiceBoxGenreName.setStyle(errorColor);
            errorCount++;
        }
        if (choiceBoxLanguage.getValue() == null) {
            choiceBoxLanguage.setStyle(errorColor);
            errorCount++;
        }
        if (textFieldSeriesPart.getText().length()>0) {
        	if (!textFieldSeriesPart.getText().matches("[0-9]+")) {
                textFieldSeriesPart.setStyle(errorColor);
                errorCount++;
        	}
        }
        if (errorCount > 0) {
            showNotification("Please fill the highlighted fields", notificationRed);
            return false;
        }
        return true;
    }

    private void emptyBookInformation() {
        textFieldTitle.setText("");
        textFieldSubtitle.setText("");
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
            showNotification("Please fill the highlighted fields", notificationGreen);
        } else {
            // Add variables into the
            elementsArrayList.add(textFieldAuthorName.getText());

            // Form the author's name to displayed.
            String notificationText=textFieldAuthorName.getText();
            try{
                // Create the query, write new author to database and display notification
                boolean success=queryFactory.writeToDatabase("insert","author",elementsArrayList);
                if(success){
                    showNotification(notificationText+" \nhas been added.", notificationGreen);
                } else {
                    showNotification(notificationText+" \nwas not added.", notificationRed);
                }

                // Refresh the author hashmap & clear the fields
                bookAttributes.createAuthorHashMap();
                elementsArrayList.clear();
                bindTextFieldSuggestions();
                emptyAuthorFields();
            } catch(Exception e) {
                throw new Exception("Unable to add new author:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    private boolean validateAuthorInformation() {
        // Validate textField Contents
        int errorCount = 0;
        if (textFieldAuthorName.getText().isEmpty() || textFieldAuthorName.getText().length()>60) {
            textFieldAuthorName.setStyle(errorColor);
            errorCount++;
        }
        if (errorCount > 0) {
            return false;
        }
        return true;
    }
    
    private void emptyAuthorFields() {
        textFieldAuthorFirstName.setText("");
        textFieldAuthorMiddleName.setText("");
        textFieldAuthorLastName.setText("");
        resetNewAuthorTextFieldEffects();
    }

    public void addNewPublisher() throws Exception {
        if (!validatePublisherInformation()) {
            showNotification("Please fill the highlighted fields", notificationRed);
        } else {
            // Add Elements to the array list
            String publisherName = textFieldPublisherName.getText();
            elementsArrayList.add(checkForApostrophes(publisherName));
            if (!textFieldPublisherLocation.getText().isEmpty()) {
                elementsArrayList.add(textFieldPublisherLocation.getText());
            } else {
                elementsArrayList.add(nullInteger);
            }

            try {
                // Create query and insert into the database
                boolean success=queryFactory.writeToDatabase("insert","publisher",elementsArrayList);
                if(success){
                    showNotification(publisherName + "\nhas been added.", notificationGreen);
                } else {
                    showNotification(publisherName + "\nwas not added.", notificationRed);
                }

                // Refresh the publisher hashmap & clear the fields
                bookAttributes.createPublisherHashMap();
                elementsArrayList.clear();
                bindTextFieldSuggestions();
                emptyPublisherInformation();
            } catch(Exception e) {
                throw new Exception("Unable to add publisher:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    private boolean validatePublisherInformation() {
        // Validate textField Contents
        int errorCount = 0;
        if (textFieldPublisherName.getText().isEmpty()) {
            textFieldPublisherName.setStyle(errorColor);
            errorCount++;
        }
        if (errorCount > 0) {
            return false;
        }
        return true;
    }
    private void emptyPublisherInformation() {
    	textFieldPublisherName.setText("");
        textFieldPublisherLocation.setText("");
        resetNewPublisherTextFieldEffects();
    }

    // Add new genre to database
    public void addNewGenre() throws Exception {
        if (!validateGenreInformation()) {
            showNotification("Please fill the highlighted fields", notificationRed);
        } else {
            // Create the relevant variables
            elementsArrayList.add(textFieldGenreName.getText());
            if (choiceBoxNewGenreType.getValue().equals("Fiction")) {
                elementsArrayList.add(0);
            } else {
                elementsArrayList.add(1);
            }
            try {
                // Create query and insert into the database
                boolean success=queryFactory.writeToDatabase("insert","genre",elementsArrayList);
                if(success){
                    showNotification(elementsArrayList.get(0) + "\nhas been added.", notificationGreen);
                } else {
                    showNotification(elementsArrayList.get(0) + "\nwas not added.", notificationRed);
                }

                // Refresh genre hashmap
                bookAttributes.createGenreHashMaps();
                elementsArrayList.clear();
                emptyGenreInformation();
                populateGenreChoiceBoxes();
            } catch(Exception e) {
                throw new Exception("Unable to add genre:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    // Validate the provided genre information
    private boolean validateGenreInformation() {
        // Validate textField Contents
        int errorCount = 0;
        if (textFieldGenreName.getText().isEmpty()) {
            textFieldGenreName.setStyle(errorColor);
            errorCount++;
        }
        if (errorCount > 0) {
            return false;
        }
        return true;
    }

    private void emptyGenreInformation() {
    	textFieldGenreName.setText("");
        choiceBoxNewGenreType.setValue("Non-Fiction");
        choiceBoxGenreName.getItems().clear();
        resetNewGenreTextFieldEffects();
    }
    
    public void addNewSeries() throws Exception {
    if(!validateSeriesInformation()){
        showNotification("Please fill the highlighted field",notificationRed);
    }
    else {
            // Create the relevant variables
            elementsArrayList.add(textFieldNewSeriesName.getText());
            try {
                // Create query and insert into the database
                boolean success=queryFactory.writeToDatabase("insert","series", elementsArrayList);
                if(success){
                    showNotification(elementsArrayList.get(0)+"\nhas been added.",notificationGreen);
                } else {
                    showNotification(elementsArrayList.get(0)+"\nwas not added.",notificationRed);
                }

                // Reset obvListSeries box
                bookAttributes.createSeriesHashMap();
                elementsArrayList.clear();
                emptySeriesInformation();
                setChoiceBoxContents();
            } catch(Exception e) {
                throw new Exception("Unable to add new series");
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    private boolean validateSeriesInformation () {
        if(textFieldNewSeriesName.getText().isEmpty()) {
            textFieldNewSeriesName.setStyle(errorColor);
            showNotification("Please fill the highlighted field",notificationRed);
            return false;
        }
        else{ return true;}
    }
    
    private void emptySeriesInformation() {
    	textFieldNewSeriesName.setText("");
        resetNewSeriesTextFieldEffects();
    }

    public void addNewLanguage () throws Exception {
        if (!validateLanguageInformation()) {
            showNotification("Please fill the highlighted fields",notificationRed);
        }
        else {
            elementsArrayList.add(textFieldNewLanguageName.getText());
            elementsArrayList.add(textFieldNewLanguageSuffix.getText());

            try {
                boolean success=queryFactory.writeToDatabase("insert", "language",elementsArrayList);
                if(success){
                    showNotification(elementsArrayList.get(0)+"\nhas been added",notificationGreen);
                } else {
                    showNotification(elementsArrayList.get(0)+"\nwas not added",notificationRed);
                }

                bookAttributes.createLanguageHashMap();

                emptyLanguageInformation();
            } catch(Exception e) {
                throw new Exception("Unable to add language:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    private boolean validateLanguageInformation () {
        int errorCount=0;
        if(textFieldNewLanguageName.getText().isEmpty()) {
            textFieldNewLanguageName.setStyle(errorColor);
            errorCount++;
        }
        if(textFieldNewLanguageSuffix.getText().isEmpty()) {
            textFieldNewLanguageSuffix.setStyle(errorColor);
            errorCount++;
        }
        if (errorCount>0){
            return false;
        } else {
            return true;
        }
    }
    
    private void emptyLanguageInformation() {
    	textFieldNewLanguageName.setText("");
        textFieldNewLanguageSuffix.setText("");
        resetNewLanguageTextFieldEffects();
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
            radioButtonHardcover.setSelected(true);
            radioButtonPaperback.setSelected(false);
            choiceBoxSeries.setValue("");
        }
        
        private void bindTextFieldSuggestions() {
        	TextFields.bindAutoCompletion(textFieldAuthor,bookAttributes.obvListAuthors);
        	TextFields.bindAutoCompletion(textFieldPublisher,bookAttributes.obvListPublishers);
        }
        
        private void setChoiceBoxContents() {
        	choiceBoxLanguage.setItems(bookAttributes.obvListLanguages);
        	choiceBoxSeries.setItems(bookAttributes.obvListSeries);

        }

        private void resetNewAuthorTextFieldEffects () {
            textFieldAuthorFirstName.setStyle(null);
            textFieldAuthorMiddleName.setStyle(null);
            textFieldAuthorLastName.setStyle(null);
        }

        private void resetNewBookTextFieldEffects () {
            textFieldTitle.setStyle(null);
            textFieldAuthor.setStyle(null);
            textFieldPublisher.setStyle(null);
            textFieldCopyright.setStyle(null);
            textFieldISBN.setStyle(null);
            textFieldPages.setStyle(null);
            choiceBoxGenreName.setStyle(null);
            choiceBoxLanguage.setStyle(null);
        }

        private void resetNewGenreTextFieldEffects () {
            textFieldGenreName.setStyle(null);
        }

        private void resetNewLanguageTextFieldEffects () {
            textFieldNewLanguageName.setStyle(null);
            textFieldNewLanguageSuffix.setStyle(null);
        }

        private void resetNewPublisherTextFieldEffects () {
            textFieldPublisher.setStyle(null);
        }

        private void resetNewSeriesTextFieldEffects () {
            textFieldNewSeriesName.setStyle(null);
        }



        // Check to ensure that an apostrophe in the title is properly formatted
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

////////////////////////////////////////////////////////////
        // Misc methods
        private void showNotification (String notification, String color){
            labelNotification.setStyle(color);
            labelNotification.setText(notification);
            labelNotification.setVisible(true);
        }

        private void closeNotification () {
            labelNotification.setText("");
            labelNotification.setVisible(false);
        }
}