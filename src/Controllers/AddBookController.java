package Controllers;
import Models.ConnectionCommands;
import Models.SQLCommands;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import org.apache.commons.collections4.BidiMap;
import javax.sql.rowset.CachedRowSet;
import org.apache.commons.collections4.bidimap.*;
import org.controlsfx.control.textfield.TextFields;
import java.math.BigInteger;

public class AddBookController {
    ConnectionCommands connectionCommands = new ConnectionCommands();
    SQLCommands sqlCommands = new SQLCommands();
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

    // Lists
    ObservableList<String> FictionGenres = FXCollections.observableArrayList();
    ObservableList<String> NonFictionGenres = FXCollections.observableArrayList();
    ObservableList<String> blankList=FXCollections.observableArrayList();

    // Hashmaps
    BidiMap<Integer, String> bidiMapAuthors = new TreeBidiMap<>();
    BidiMap<Integer, String> bidiMapPublishers = new TreeBidiMap<>();
    BidiMap<Integer, String> bidiMapFictionGenres = new TreeBidiMap<>();
    BidiMap<Integer, String> bidiMapNonFictionGenres = new TreeBidiMap<>();
    BidiMap<Integer, String> bidiMapLanguages = new TreeBidiMap<>();
    BidiMap<Integer, String> bidiMapSeries = new TreeBidiMap<>();
    
    // Arrays
   String arrayAuthors[];

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
        
    }

    //////////////////////////////////////////////////////////////
    //  Methods for adding new items to the database
    // Add new books to the database
    public void addBook() throws Exception {
        // Check if all fields have content
        if (!validateBookInformation()) {
            showNotification("Please fill the highlighted fields", notificationGreen);
        } else {
            // Get the values for the different variables.
            String title = textFieldTitle.getText();
            Integer authorID = bidiMapAuthors.getKey(textFieldAuthor.getText());
            Integer publisherID = bidiMapPublishers.getKey(textFieldPublisher.getText());
            if(textFieldISBN.getText().equals("null")){
                BigInteger isbn = new BigInteger("null");
            }
            BigInteger isbn = new BigInteger(textFieldISBN.getText());

            Integer pages = Integer.parseInt(textFieldPages.getText());
            Integer edition;
            if (textFieldEdition.getText().isEmpty()) {
                edition = null;
            } else {
                edition = Integer.parseInt(textFieldEdition.getText());
            }
            Integer copyright = Integer.parseInt(textFieldCopyright.getText());
            // Acquire genreID value
            Integer genreID=0;
            if(choiceBoxGenreType.getValue().equals("Fiction")){
                genreID = bidiMapFictionGenres.getKey(choiceBoxGenreName.getValue());
            }
            else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
                genreID=bidiMapNonFictionGenres.getKey(choiceBoxGenreName.getValue());
            }
            Integer languageID = bidiMapLanguages.getKey(choiceBoxLanguage.getValue());
            Integer seriesID = bidiMapSeries.getKey(choiceBoxSeries.getValue());
            Integer seriesPart;
            if (textFieldSeriesPart.getText().isEmpty()) {
                seriesPart = null;
            } else {
                seriesPart = Integer.parseInt(textFieldSeriesPart.getText());
            }
            Integer format = null;
            if (toggleButtonHardcover.isSelected()) {
                format = 1;
            } else {
                format = 0;
            }

            query = String.format(sqlCommands.insetIntoBook, authorID, publisherID, title, copyright, isbn, edition, genreID, seriesPart, format, pages, languageID, seriesID);
            try {
            	connectionCommands.writeDatabase(query);
            } catch(Exception e) {
            	System.err.print("Unable to add book");
            } finally {
            	emptyBookInformation();
                resetTextFieldEffects();
                setValues();
                showNotification(title + "\nwas added successfully.", notificationGreen);
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
            System.out.print("");
        } else {
            // Pulls the proper data into a the appropriate variables
            String authorFirstName = textFieldAuthorFname.getText();
            String authorLastName = textFieldAuthorLname.getText();
            String authorLocation = null;
            if (!textFieldAuthorLocation.getText().isEmpty()) {
            	authorLocation = textFieldAuthorLocation.getText();
            }
            Integer authorYearBirth = null;
            Integer authorYearDeath = null;
            if (!textFieldAuthorBirth.getText().isEmpty()) {
                authorYearDeath = Integer.parseInt(textFieldAuthorBirth.getText());
            }
            if (!textFieldAuthorDeath.getText().isEmpty()) {
                authorYearDeath = Integer.parseInt(textFieldAuthorDeath.getText());
            }

            // Create query and write new author to database
            query = String.format(sqlCommands.insertIntoAuthor, authorFirstName, authorLastName, authorLocation, authorYearBirth, authorYearDeath);
            connectionCommands.writeDatabase(query);
            showNotification(authorFirstName + " " + authorLastName + "\nhas been added.", notificationGreen);

            // Reset the contents of the textFields and displays a successful notification
            textFieldAuthorFname.setText("");
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
            System.out.println("1");
        }
        if (textFieldAuthorLname.getText().isEmpty()) {
        	System.out.println("2");
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
            System.out.print("");
        } else {
            // Create the relevant variables
            String publisherName = textFieldPublisherName.getText();
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

            // Reset series box
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
            // The values in the choice box are set to null in the addNewGenre function.
            if(choiceBoxGenreType.getValue().equals("Fiction")){
                choiceBoxGenreName.setItems(FictionGenres);
            }
            else if(choiceBoxGenreType.getValue().equals("Non-Fiction")){
                choiceBoxGenreName.setItems(NonFictionGenres);
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
            int ID;
            // Get the Cached Row Set for all Authors in the database
            String authorFirstName, authorLastName, authorFullName;
            CachedRowSet authorList = connectionCommands.readDatabase(sqlCommands.selectAllAuthor);
            // Set the authorList array to the length of the result set so it can be filled.
            
            ObservableList<String> authors = FXCollections.observableArrayList();
            
            while (authorList.next()) {
                ID = authorList.getInt(1);
                authorFirstName = authorList.getString(2);
                authorLastName = authorList.getString(3);
                authorFullName = (authorFirstName + " " + authorLastName);
                bidiMapAuthors.put(ID, authorFullName);
                authors.add(authorFullName);
            }
            choiceBoxAuthor.setItems(authors);
            TextFields.bindAutoCompletion(textFieldAuthor,authors);
        }

        public void createPublisherHashMap () throws Exception {
            int ID;
            String publisherName;
            CachedRowSet publisherList = connectionCommands.readDatabase(sqlCommands.selectAllPublisher);
            ObservableList<String> publishers = FXCollections.observableArrayList();

            while (publisherList.next()) {
                ID = publisherList.getInt(1);
                publisherName = publisherList.getString(2);
                bidiMapPublishers.put(ID, publisherName);
                publishers.add(publisherName);
            }
            choiceBoxPublisher.setItems(publishers);
            TextFields.bindAutoCompletion(textFieldPublisher,publishers);
        }

        public void createGenreHashMap () throws Exception {
            int ID;
            String genreName;
            CachedRowSet genreList = connectionCommands.readDatabase(sqlCommands.selectAllGenre);

            while (genreList.next()) {
                ID = genreList.getInt(1);
                genreName = genreList.getString(2);
                if(genreList.getInt(3)==0){ bidiMapFictionGenres.put(ID,genreName); FictionGenres.add(genreName); }
                else if(genreList.getInt(3)==1){ bidiMapNonFictionGenres.put(ID,genreName); NonFictionGenres.add(genreName); }
            }
        }

        public void createLanguageHashMap () throws Exception {
            int ID;
            String languageName;
            CachedRowSet languageList = connectionCommands.readDatabase(sqlCommands.selectAllLanguage);
            ObservableList<String> languages = FXCollections.observableArrayList();

            while (languageList.next()) {
                ID = languageList.getInt(1);
                languageName = languageList.getString(2);
                bidiMapLanguages.put(ID, languageName);
                languages.add(languageName);
            }
            choiceBoxLanguage.setItems(languages);
        }

        public void createSeriesHashMap () throws Exception {
            int ID;
            String seriesName;
            CachedRowSet seriesList = connectionCommands.readDatabase(sqlCommands.selectAllSeries);
            ObservableList<String> series = FXCollections.observableArrayList();

            while (seriesList.next()) {
                ID = seriesList.getInt(1);
                seriesName = seriesList.getString(2);
                bidiMapSeries.put(ID, seriesName);
                series.add(seriesName);
            }
            choiceBoxSeries.setItems(series);
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