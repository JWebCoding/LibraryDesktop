package controllers;
import models.BookAttributes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import models.QueryFactory;
import org.controlsfx.control.textfield.TextFields;
import java.util.ArrayList;

public class AddBookController {
    BookAttributes bookAttributes=new BookAttributes();
    QueryFactory queryFactory=new QueryFactory();

    //Book Information pane elements
    @FXML TextField textFieldTitle;
    @FXML TextField textFieldISBN;
    @FXML TextField textFieldPages;
    @FXML TextField textFieldEdition;
    @FXML TextField textFieldCopyright;
    @FXML ChoiceBox<String> choiceBoxPublisher;
    @FXML ChoiceBox<String> choiceBoxLanguage;
    @FXML ChoiceBox<String> choiceBoxAuthor;
    @FXML ChoiceBox<String> choiceBoxGenreType;
    @FXML ChoiceBox<String> choiceBoxGenreName;
    @FXML ChoiceBox<String> choiceBoxSeries;
    @FXML TextField textFieldSeriesPart;
    @FXML ToggleButton toggleButtonPaperback;
    @FXML ToggleButton toggleButtonHardcover;
    // New Author pane elements
    @FXML TextField textFieldAuthorFirstName;
    @FXML TextField textFieldAuthorMiddleName;
    @FXML TextField textFieldAuthorLastName;
    @FXML TextField textFieldAuthorLocation;
    @FXML TextField textFieldAuthorBirth;
    @FXML TextField textFieldAuthorDeath;
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
    
    @FXML Label labelNotificationTitle;
    @FXML Label labelNotificationAuthor;
    @FXML Label labelNotificationPublisher;
    @FXML Label labelNotificationISBN;
    @FXML Label labelNotificationCopyright;
    @FXML Label labelNotificationPages;
    @FXML Label labelNotificationGenre;
    @FXML Label labelNotificationLanguage;

    @FXML CheckBox checkBoxSaveData;

    // Misc elements
    @FXML Label labelNotification;
    @FXML Button buttonAddBook;

    // Declare variables
    String query;
    String notificationGreen="#00ff00",notificationRed="#ff0000";

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
        bindTextfieldSuggestions();
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
            showNotification("Please fill the highlighted fields", notificationGreen);
        } else {
            // Add the values to the array list.
            elementsArrayList.add(bookAttributes.bidiMapAuthors.getKey(textFieldAuthor.getText()));
            elementsArrayList.add(bookAttributes.bidiMapPublishers.getKey(textFieldPublisher.getText()));
            String title = textFieldTitle.getText();
            elementsArrayList.add(checkForApostrophes(title));
            elementsArrayList.add(Integer.parseInt(textFieldCopyright.getText()));
            elementsArrayList.add(Long.parseLong(textFieldISBN.getText()));
            // Get Book Edition
            if (textFieldEdition.getText().isEmpty()) {
                elementsArrayList.add(null);
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
                elementsArrayList.add(null);
            } else {
                elementsArrayList.add(Integer.parseInt(textFieldSeriesPart.getText()));
            }

            // Get Book Format
            if (toggleButtonHardcover.isSelected()) {
                elementsArrayList.add(1);
            } else {
                elementsArrayList.add(0);
            }
            elementsArrayList.add(Integer.parseInt(textFieldPages.getText()));
            elementsArrayList.add(bookAttributes.bidiMapLanguages.getKey(choiceBoxLanguage.getValue()));
            elementsArrayList.add("");

            try {
                // Attempt to write the new book to the database and then display an appropriate notification.
            	boolean success=queryFactory.writeToDatabase("insert","book", elementsArrayList);
                if(success){
                    showNotification(title + "\nwas added successfully.", notificationGreen);
                } else {
                    showNotification(title + "\nwas not added properly.", notificationRed);
                }
                // Save the data in the fields if the requisite checkbox is checked.
                if(!checkBoxSaveData.isSelected()){
                    emptyBookInformation();
                    setValues();
                }
                resetTextFieldEffects();
                elementsArrayList.clear();

            } catch(Exception e) {
                throw new Exception("Unable to add book:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    private boolean validateBookInformation() {
        int errorCount = 0;
        if (textFieldTitle.getText().isEmpty()) {
        	labelNotificationTitle.setVisible(true);
            errorCount++;
        }
        if (textFieldISBN.getText().isEmpty() || textFieldISBN.getText().length()>13 || !textFieldISBN.getText().matches("[0-9]+")) {
        	labelNotificationISBN.setVisible(true);
            errorCount++;
        }
        if (textFieldCopyright.getText().length()>0) {
        	if (textFieldCopyright.getText().length()!=4 || !textFieldCopyright.getText().matches("[0-9]+")) {
        		labelNotificationCopyright.setVisible(true);
                errorCount++;
        	}
        }
        if (textFieldPages.getText().isEmpty() || !textFieldPages.getText().matches("[0-9]+")) {
        	labelNotificationPages.setVisible(true);
            errorCount++;
        }
        if (textFieldEdition.getText().length()>0) {
        	if (!textFieldEdition.getText().matches("[0-9]+")) {
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
        	if (!textFieldSeriesPart.getText().matches("[0-9]+")) {
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
            showNotification("Please fill the highlighted fields", notificationGreen);
        } else {
            // Add variables into the
            elementsArrayList.add(textFieldAuthorFirstName.getText());
            if(textFieldAuthorMiddleName.getText().isEmpty()) {
                elementsArrayList.add("");
            } else {
                elementsArrayList.add(textFieldAuthorMiddleName.getText());
            }
            elementsArrayList.add(textFieldAuthorLastName.getText());

            // Form the author's name to displayed.
            String notificationText;
            if(textFieldAuthorMiddleName.getText().isEmpty()) {
                notificationText=(elementsArrayList.get(0) + " " + elementsArrayList.get(2));
            } else {
                notificationText=(elementsArrayList.get(0) + " " +elementsArrayList.get(1) + " " + elementsArrayList.get(2));
            }
            try{
                // Create the query, write new author to database and display notifcation
                boolean success=queryFactory.writeToDatabase("insert","author",elementsArrayList);
                if(success){
                    if(textFieldAuthorMiddleName.getText().isEmpty()) {
                        showNotification(notificationText+" \nhas been added.", notificationGreen);
                    }
                } else {
                    if(textFieldAuthorMiddleName.getText().isEmpty()) {
                        showNotification(notificationText+" \nwas not added.", notificationRed);
                    }
                }

                // Refresh the author hashmap & clear the fields
                bookAttributes.createAuthorHashMap();
                elementsArrayList.clear();
                bindTextfieldSuggestions();
                emptyAuthorFields();
            } catch(Exception e) {
                throw new Exception("Unable to add author:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
        }
    }

    private boolean validateAuthorInformation() {
        // Validate textField Contents
        int errorCount = 0;
        if (textFieldAuthorFirstName.getText().isEmpty()) {
            errorCount++;
        }
        if (textFieldAuthorLastName.getText().isEmpty()) {
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
        textFieldAuthorLocation.setText("");
        textFieldAuthorBirth.setText("");
        textFieldAuthorDeath.setText("");
        query = "";
        resetTextFieldEffects();
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
                elementsArrayList.add(null);
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
                bindTextfieldSuggestions();
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
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        int errorCount = 0;
        if (textFieldPublisherName.getText().isEmpty()) {
            textFieldPublisherName.setEffect(colorAdjustRequired);
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
        query = "";
        resetTextFieldEffects();
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
                    showNotification(elementsArrayList.get(1) + "\nhas been added.", notificationGreen);
                } else {
                    showNotification(elementsArrayList.get(1) + "\nwas not added.", notificationRed);
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
            return false;
        }
        return true;
    }

    private void emptyGenreInformation() {
    	textFieldGenreName.setText("");
        choiceBoxNewGenreType.setValue("Non-Fiction");
        query = "";
        choiceBoxGenreName.getItems().clear();
        resetTextFieldEffects();
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
                throw new Exception("Unable to add series:\n"+e);
            } finally {
                elementsArrayList.clear();
            }
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
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        int errorCount=0;
        if(textFieldNewLanguageName.getText().isEmpty()) {
            textFieldNewLanguageName.setEffect(colorAdjustRequired); errorCount++;
        }
        if(textFieldNewLanguageSuffix.getText().isEmpty()) {
            textFieldNewLanguageSuffix.setEffect(colorAdjustRequired); errorCount++;
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