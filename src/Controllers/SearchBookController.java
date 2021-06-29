package Controllers;

import Models.Book;
import Models.ConnectionCommands;
import Models.SQLCommands;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;

public class SearchBookController {

    ConnectionCommands connectionCommands=new ConnectionCommands();
    SQLCommands sql =new SQLCommands();
    @FXML TableView<Book> tableViewBooks;
    @FXML TableColumn<Book,String> tableColumnTitle;
    @FXML TableColumn<Book,String> tableColumnAuthor;
    @FXML TableColumn<Book,String> tableColumnPublisher;
    @FXML TableColumn<Book,String> tableColumnISBN;
    @FXML TableColumn<Book,Integer> tableColumnYear;
    @FXML TableColumn<Book,String> tableColumnGenre;
    @FXML TableColumn<Book,String> tableColumnFormat;
    @FXML TableColumn<Book,String> tableColumnSeries;

    @FXML TextField textFieldSearch;
    @FXML Label labelNotification;

    @FXML ToggleGroup toggleSearchButtons;

    // Declare variables
    String title,firstName,lastName,isbn,series,publisher,genre,language,searchText,notification;
    int id,series_part,year,format,edition,finished;
    CachedRowSet bookCollection=null;


    public void initialize() throws Exception{
        tableColumnTitle.setCellValueFactory(new PropertyValueFactory("title"));
        tableColumnSeries.setCellValueFactory(new PropertyValueFactory("series"));
        tableColumnAuthor.setCellValueFactory(new PropertyValueFactory("author"));
        tableColumnPublisher.setCellValueFactory(new PropertyValueFactory("publisher"));
        tableColumnISBN.setCellValueFactory(new PropertyValueFactory("isbn"));
        tableColumnYear.setCellValueFactory(new PropertyValueFactory("copyright"));
        tableColumnGenre.setCellValueFactory(new PropertyValueFactory("genre"));
        tableColumnFormat.setCellValueFactory(new PropertyValueFactory("format"));

        getBookCollectionData();
        fillTable();
    }

    // Get an up to date copy of the MySQL book database
    public void getBookCollectionData() throws Exception{
        bookCollection=connectionCommands.readDatabase(0,sql.selectTableBookInfo);
    }

    // Construct an instance of the Book class
    public Book createBook() throws SQLException {

        id=bookCollection.getInt("bookID");
        title=bookCollection.getString("title");
        series=bookCollection.getString("series_name");
        series_part=bookCollection.getInt("series_part");
        firstName=bookCollection.getString("firstName");
        lastName=bookCollection.getString("lastName");
        publisher=bookCollection.getString("publisher_name");
        isbn=bookCollection.getString("isbn");
        year=bookCollection.getInt("copyright");
        genre=bookCollection.getString("genre_name");
        format=bookCollection.getInt("format");
//            edition=bookCollection.getInt("edition");
//            language=bookCollection.getString("language");
//            finished=bookCollection.getInt("finished");

        return new Book(id,title,series,firstName,lastName,publisher,isbn,year,genre,format);
    }

    // Fill the search table with book information
    public void fillTable() throws Exception {
        tableViewBooks.getItems().clear();
        bookCollection.first();
        Book bookOne=createBook();
        tableViewBooks.getItems().add(bookOne);
        while (bookCollection.next()){
            Book book=createBook();
            tableViewBooks.getItems().add(book);
        }
    }

    // Search the contents of the BookCollection based on a given query
    public void searchBooks() throws SQLException {
        // Check if there is too little or too much text
        if(textFieldSearch.getText().equals("")){
            showNotification("No Search Query");
        }
        else if(textFieldSearch.getText().length()>45){
            showNotification("Please keep the\n search length below\n45 characters");
        }
        else {
            String searchFor,selectedColumn="";

            // Reset the table and cachedrowset
            tableViewBooks.getItems().clear();
            bookCollection.first();

            // Determine which button is selected and set the search text to the appropriate column
            RadioButton selectedRadioButton= (RadioButton) toggleSearchButtons.getSelectedToggle();
            switch (selectedRadioButton.getText()){
                case "Title":
                    selectedColumn="title";
                    break;
                case "Publisher":
                    selectedColumn="publisher_name";
                    break;
                case "ISBN":
                    selectedColumn="isbn";
                    break;
                case "Genre":
                    selectedColumn="genre_name";
                    break;
                case "Language":
                    selectedColumn="language";
                    break;
            }

            // Grab the search criteria and set it to lowercase
            searchFor=textFieldSearch.getText().toLowerCase();
            if(selectedRadioButton.getText().equals("Author")) {
            	while(bookCollection.next()){
                    // Select the contents of the current row to be searched and set it to lowercase
                    String firstName=bookCollection.getString("firstName").toLowerCase();
                    String lastName=bookCollection.getString("lastName").toLowerCase();
                    if(firstName.contains(searchFor) || lastName.contains(searchFor)){
                    	
                        Book book=createBook();
                        tableViewBooks.getItems().add(book);
                    }
                }
            }
            else {
            	// Iterate through the row set and match books to the search query
                while(bookCollection.next()){
                    // Select the contents of the current row to be searched and set it to lowercase
                    String bookAttribute=bookCollection.getString(selectedColumn).toLowerCase();
                    if(bookAttribute.contains(searchFor)){
                        Book book=createBook();
                        tableViewBooks.getItems().add(book);
                    }
                }
            
            }
        }
    }

    // Reset the table columns to their default width
    public void resetColumnWidths() throws Exception {

        tableColumnFormat.setPrefWidth(61);
        tableColumnGenre.setPrefWidth(100);
        tableColumnYear.setPrefWidth(54);
        tableColumnISBN.setPrefWidth(117);
        tableColumnPublisher.setPrefWidth(112);
        tableColumnAuthor.setPrefWidth(94);
        tableColumnSeries.setPrefWidth(99);
        tableColumnTitle.setPrefWidth(161);
    }

    // Reset the contents of the table
    public void resetTableContents() throws Exception {
        fillTable();
    }

    // Refresh the table and book collection with new information
    public void refreshTable() throws Exception{
        getBookCollectionData();
        fillTable();
    }

    public void showNotification(String notification){
        labelNotification.setText(notification);
        labelNotification.setVisible(true);
    }

    public void hideNotification(){
        labelNotification.setText("");
        labelNotification.setVisible(false);
    }

    public void printTest(int num){
        System.out.println("TEST"+num);
    }
}
