package Controllers;
import Models.ConnectionCommands;
import Models.SQLCommands;
import impl.org.controlsfx.autocompletion.SuggestionProvider;
import Models.BookAttributes;
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
   
   // TESTING LIST
   ObservableList<String> tempAuthorList=FXCollections.observableArrayList();
   ObservableList<String> tempPublisherList=FXCollections.observableArrayList();
   ObservableList<String> tempFictionGenreList=FXCollections.observableArrayList();
   ObservableList<String> tempNonFictionGenreList=FXCollections.observableArrayList();
   ObservableList<String> tempSeriesList=FXCollections.observableArrayList();
   ObservableList<String> tempLanguageList=FXCollections.observableArrayList();
   ObservableList<String> blankList=FXCollections.observableArrayList();

    public void initialize() throws Exception {
    	
    	
    	connectionCommands.getConnectionSettings();
        ObservableList<String> genreTypes = FXCollections.observableArrayList("Fiction", "Non-Fiction");
        choiceBoxNewGenreType.setItems(genreTypes);
        choiceBoxGenreType.setItems(genreTypes);
        createAuthorHashMap();
        createPublisherHashMap();      
        createGenreHashMap();
        createLanguageHashMap();
        createSeriesHashMap();
        setValues();
        populateGenreChoiceBoxes();
        closeNotification();
        
        bookAttributes.obvListAuthors.addAll(tempAuthorList);
        bookAttributes.obvListPublishers.addAll(tempPublisherList);
        bookAttributes.obvListFictionGenres.addAll(tempFictionGenreList);
        bookAttributes.obvListNonFictionGenres.addAll(tempNonFictionGenreList); 
        bookAttributes.obvListLanguages.addAll(tempLanguageList);     
        bookAttributes.obvListSeries.addAll(tempSeriesList);
        
    }

    //////////////////////////////////////////////////////////////
    //  Methods for adding new items to the database
    // Add new books to the database
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
            System.out.println(query);
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

    // Ensures that the text fields in the addBook pane
    public boolean validateBookInformation() {
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

    public void emptyBookInformation() {
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

    // Add new author to database
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
            

            // Reset the contents of the textFields and displays a successful notification
            textFieldAuthorFname.setText("");
            textFieldAuthorMname.setText("");
            textFieldAuthorLname.setText("");
            textFieldAuthorLocation.setText("");
            textFieldAuthorBirth.setText("");
            textFieldAuthorDeath.setText("");
            query = "";
            resetTextFieldEffects();

            // Refresh the author hashmap
            createAuthorHashMap();
        }
    }

    // Validate the provided author information
    public boolean validateAuthorInformation() {
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

    // Add new publisher to database
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

            // Reset Publisher
            textFieldPublisherName.setText("");
            textFieldPublisherLocation.setText("");
            query = "";
            resetTextFieldEffects();

            // Refresh the publisher hashmap
            createPublisherHashMap();
        }
    }

    // Validate the provided publisher information
    public boolean validatePublisherInformation() {
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

    // Add new genre to database
    public void addNewGenre() throws Exception {
        if (!validateGenreInformation()) {
            System.out.print("");
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

            // Reset Publisher
            textFieldGenreName.setText("");
            choiceBoxNewGenreType.setValue("");
            query = "";

            // Reset the contents of choiceBoxGenreName
            choiceBoxGenreName.getItems().clear();
            bookAttributes.bidiMapFictionGenres.clear();
            resetTextFieldEffects();

            // Refresh genre hashmap
            createGenreHashMap();
        }
    }

    // Validate the provided genre information
    public boolean validateGenreInformation() {
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

    public void addNewSeries() throws Exception {
    if(!validateSeriesInformation()){ System.out.print(""); }
    else {
            // Create the relevant variables
            String seriesName=textFieldNewSeriesName.getText();

            // Create query and insert into the database
            query=(String.format(sqlCommands.insertIntoSeries,seriesName));
            connectionCommands.writeDatabase(query);
            createSeriesHashMap();
            showNotification(seriesName+"\nwas added.",notificationGreen);

            // Reset obvListSeries box
            textFieldNewSeriesName.setText("");
            resetTextFieldEffects();
        }
    }

    public boolean validateSeriesInformation () {
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        if(textFieldNewSeriesName.getText().isEmpty()) {
            textFieldNewSeriesName.setEffect(colorAdjustRequired);
            showNotification("Please fill the highlighted field",notificationRed);
            return false;
        }
        else{ return true;}
    }

    public void addNewLanguage () throws Exception {
        if (!validateLanguageInformation()) { System.out.print(""); }
        else {
            String languageName=textFieldNewLanguageName.getText();
            String languageSuffix=textFieldNewLanguageSuffix.getText();

            query=String.format(sqlCommands.insertIntoLanguage,languageName,languageSuffix);
            connectionCommands.writeDatabase(query);
            createLanguageHashMap();
            showNotification(languageName+"\nwas added",notificationGreen);

            textFieldNewLanguageName.setText("");
            textFieldNewLanguageSuffix.setText("");
            resetTextFieldEffects();
        }
    }

    public boolean validateLanguageInformation () {
        ColorAdjust colorAdjustRequired = new ColorAdjust();
        colorAdjustRequired.setSaturation(.2);
        Integer errorCount=0;
        if(textFieldNewLanguageName.getText().isEmpty()) { textFieldNewLanguageName.setEffect(colorAdjustRequired); errorCount++; }
        if(textFieldNewLanguageSuffix.getText().isEmpty()) { textFieldNewLanguageSuffix.setEffect(colorAdjustRequired); errorCount++; }
        if (errorCount>0){ showNotification("Please fill the highlighted fields",notificationRed); return false;  }
        else { return true; }
    }

////////////////////////////////////////////////////////////
        // Methods to run during initialization
        public void populateGenreChoiceBoxes () {
            // The values are set to null in the addNewGenre function.
            if(choiceBoxGenreType.getValue().equals("Fiction")){
                choiceBoxGenreName.setItems(tempFictionGenreList);
            }
            else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
                choiceBoxGenreName.setItems(tempNonFictionGenreList);
            }
        }

        public void setValues () {
            choiceBoxGenreType.setValue("Non-Fiction");
            choiceBoxNewGenreType.setValue("Non-Fiction");
            toggleButtonHardcover.setSelected(true);
            toggleButtonPaperback.setSelected(false);
            choiceBoxSeries.setValue("");
        }

        public void resetTextFieldEffects () {
        	labelNotificationTitle.setVisible(false);
        	labelNotificationAuthor.setVisible(false);
        	labelNotificationPublisher.setVisible(false);
        	labelNotificationISBN.setVisible(false);
        	labelNotificationCopyright.setVisible(false);
        	labelNotificationPages.setVisible(false);
        	labelNotificationGenre.setVisible(false);
        	labelNotificationLanguage.setVisible(false);

        }

        public void createAuthorHashMap () throws Exception {
        	String authorFirstName,authorMiddleName, authorLastName, authorFullName;
            int ID;
            
            // Get the Cached Row Set for all Authors in the database
            CachedRowSet authorList = connectionCommands.readDatabase(sqlCommands.selectAllAuthor);
            
            // Clear the contents of the relvant hash-maps, observable lists and choice-boxes.
            bookAttributes.bidiMapAuthors.clear();
            bookAttributes.obvListAuthors.clear();
            choiceBoxAuthor.setItems(blankList);
            
            // Combine the sperate parts of the names from the cached rowset
            while (authorList.next()) {
                ID = authorList.getInt(1);
                authorFirstName = authorList.getString(2);
                authorMiddleName=authorList.getString(3);
                authorLastName = authorList.getString(4);
                if(authorMiddleName==null) {
                	authorFullName = (authorFirstName +" "+ authorLastName);
                } else {
                	authorFullName = (authorFirstName +" "+ authorMiddleName +" "+ authorLastName);
                }
                // Add the obvListAuthors to the hashmap and list
                bookAttributes.bidiMapAuthors.put(ID, authorFullName);
                tempAuthorList.add(authorFullName);
            }
            // Set the contents of the choice box and auto-complete field
            choiceBoxAuthor.setItems(tempAuthorList);
            TextFields.bindAutoCompletion(textFieldAuthor,tempAuthorList);
        }

        public void createPublisherHashMap () throws Exception {
            int ID;
            String publisherName;
            
            // Get the Cached Row Set for all Authors in the database
            CachedRowSet publisherList = connectionCommands.readDatabase(sqlCommands.selectAllPublisher);
            
            // Clear the contents of the relvant hash-maps, observable lists and choice-boxes.
            bookAttributes.bidiMapPublishers.clear();
            bookAttributes.obvListPublishers.clear();
            choiceBoxPublisher.setItems(blankList);
            
            // Get the contents of 
            while (publisherList.next()) {
                ID = publisherList.getInt(1);
                publisherName = publisherList.getString(2);
                bookAttributes.bidiMapPublishers.put(ID, publisherName);
                tempPublisherList.add(publisherName);
            }
            
            // Set the contents of the choice box and auto-complete field
            choiceBoxPublisher.setItems(tempPublisherList);
            TextFields.bindAutoCompletion(textFieldPublisher,tempPublisherList);
        }

        public void createGenreHashMap () throws Exception {
            int ID;
            String genreName;
            CachedRowSet genreList = connectionCommands.readDatabase(sqlCommands.selectAllGenre);
            // Clear the contents of the relvant hash-maps, observable lists and choice-boxes.
            bookAttributes.bidiMapFictionGenres.clear();
            bookAttributes.bidiMapNonFictionGenres.clear();
            bookAttributes.obvListFictionGenres.clear();
            bookAttributes.obvListNonFictionGenres.clear();
            tempFictionGenreList.clear();
            tempNonFictionGenreList.clear();
            choiceBoxGenreName.setItems(blankList);
            
            while (genreList.next()) {
                ID = genreList.getInt(1);
                genreName = genreList.getString(2);
                if(genreList.getInt(3)==0){ bookAttributes.bidiMapFictionGenres.put(ID,genreName); tempFictionGenreList.add(genreName); }
                else if(genreList.getInt(3)==1){ bookAttributes.bidiMapNonFictionGenres.put(ID,genreName); tempNonFictionGenreList.add(genreName); }
            }
        }

        public void createLanguageHashMap () throws Exception {
            int ID;
            String languageName;
            CachedRowSet languageList = connectionCommands.readDatabase(sqlCommands.selectAllLanguage);
         // Clear the contents of the relvant hash-maps, observable lists and choice-boxes.
            bookAttributes.bidiMapLanguages.clear();
            bookAttributes.obvListLanguages.clear();
            tempLanguageList.clear();
            choiceBoxLanguage.setItems(blankList);
            
            while (languageList.next()) {
                ID = languageList.getInt(1);
                languageName = languageList.getString(2);
                bookAttributes.bidiMapLanguages.put(ID, languageName);
                tempLanguageList.add(languageName);
            }
            choiceBoxLanguage.setItems(tempLanguageList);
        }

        public void createSeriesHashMap () throws Exception {
        	
            int ID;
            String seriesName;
            CachedRowSet seriesList = connectionCommands.readDatabase(sqlCommands.selectAllSeries);
            System.out.println(seriesList);
            bookAttributes.bidiMapSeries.clear();
            bookAttributes.obvListSeries.clear();
            tempSeriesList.clear();
            choiceBoxSeries.setItems(blankList);
            
            while (seriesList.next()) {
                ID = seriesList.getInt(1);
                seriesName = seriesList.getString(2);
                bookAttributes.bidiMapSeries.put(ID, seriesName);
                tempSeriesList.add(seriesName);
            }
            
            choiceBoxSeries.setItems(tempSeriesList);
        }
        // Check to ensure that an apostraphe in the title is properly formatted
        public String checkForApostrophes(String text) {
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
        public void showNotification (String notification, String color){
            labelNotification.setTextFill(Color.web(color));
            labelNotification.setText(notification);
            labelNotification.setVisible(true);
        }

        public void closeNotification () {
            labelNotification.setTextFill(Color.web("#ffffff"));
            labelNotification.setText("");
            labelNotification.setVisible(false);
        }
}