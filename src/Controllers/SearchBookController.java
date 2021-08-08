package Controllers;

import Models.Book;
import Models.ConnectionCommands;
import Models.SQLCommands;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.sql.rowset.CachedRowSet;

import java.io.IOException;
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
    @FXML TableColumn<Book,Integer> tableColumnSeriesPart;
    @FXML TableColumn<Book,String> tableColumnSeries;
    @FXML TableColumn<Book,Integer> tableColumnId;

    @FXML TextField textFieldSearch;
    @FXML Label labelNotification;
    @FXML Label labelBookNumber;

    @FXML ToggleGroup toggleSearchButtons;
    
    @FXML Button buttonEditBook;

    // Declare variables
    String title,firstName,lastName,isbn,series,publisher,genre,language,searchText,notification;
    int id,year,format,edition,finished;
    Integer seriesPart;
    CachedRowSet bookCollection=null;


    public void initialize() throws Exception{
    	connectionCommands.getConnectionSettings();
    	tableColumnId.setCellValueFactory(new PropertyValueFactory("id"));
        tableColumnTitle.setCellValueFactory(new PropertyValueFactory("title"));
        tableColumnSeries.setCellValueFactory(new PropertyValueFactory("series"));
        tableColumnSeriesPart.setCellValueFactory(new PropertyValueFactory("seriesPart"));
        tableColumnAuthor.setCellValueFactory(new PropertyValueFactory("author"));
        tableColumnPublisher.setCellValueFactory(new PropertyValueFactory("publisher"));
        tableColumnISBN.setCellValueFactory(new PropertyValueFactory("isbn"));
        tableColumnYear.setCellValueFactory(new PropertyValueFactory("copyright"));
        tableColumnGenre.setCellValueFactory(new PropertyValueFactory("genre"));
        

        getBookCollectionData();
        fillTable();
        setBookNumberLabel();
    }

    // Get an up to date copy of the MySQL book database
    public void getBookCollectionData() throws Exception{
        bookCollection=connectionCommands.readDatabase(sql.selectTableBookInfo);
    }

    // Construct an instance of the Book class
    public Book createBook() throws SQLException {

        id=bookCollection.getInt("bookID");
        title=bookCollection.getString("title");
        series=bookCollection.getString("series_name");
        seriesPart=bookCollection.getInt("series_part");
        if(seriesPart==0) {
        	seriesPart=null;
        }
        firstName=bookCollection.getString("firstName");
        lastName=bookCollection.getString("lastName");
        publisher=bookCollection.getString("publisher_name");
        isbn=bookCollection.getString("isbn");
        year=bookCollection.getInt("copyright");
        genre=bookCollection.getString("genre_name");

        return new Book(id,title,series,seriesPart,firstName,lastName,publisher,isbn,year,genre);
    }
    
    // Fill the BookNumber label
    public void setBookNumberLabel() {
    	String numOfBooks= String.valueOf(Book.getBookNum());
    	labelBookNumber.setText(numOfBooks);
    }

    // Fill the search table with book information
    public void fillTable() throws Exception {
        tableViewBooks.getItems().clear();
        Book.incrementBookNum();
        bookCollection.first();
        Book bookOne=createBook();
        tableViewBooks.getItems().add(bookOne);
        while (bookCollection.next()){
        	Book.incrementBookNum();
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
                case "Series Name":
                    selectedColumn="series_name";
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
    	try {
    		tableColumnSeriesPart.setPrefWidth(61);
            tableColumnGenre.setPrefWidth(100);
            tableColumnYear.setPrefWidth(54);
            tableColumnISBN.setPrefWidth(117);
            tableColumnPublisher.setPrefWidth(112);
            tableColumnAuthor.setPrefWidth(94);
            tableColumnSeries.setPrefWidth(99);
            tableColumnTitle.setPrefWidth(161);
    	}
    	catch(Exception e) {
        	e.printStackTrace();
        } 
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
    
    public void showBooksRead() {
    	try {
    		tableViewBooks.getItems().clear();
			bookCollection.first();
			while(bookCollection.next()) {
				int bookRead=bookCollection.getInt("finished");
				if(bookRead==1) {
					Book book=createBook();
                    tableViewBooks.getItems().add(book);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void showBooksNotRead() {
    	try {
    		tableViewBooks.getItems().clear();
			bookCollection.first();
			while(bookCollection.next()) {
				int bookRead=bookCollection.getInt("finished");
				if(bookRead==0) {
					Book book=createBook();
                    tableViewBooks.getItems().add(book);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void openScreenDetails() throws IOException {
    	if(tableViewBooks.getSelectionModel().getSelectedItem()!=null) {
	    	Parent root;
	    	try {
	    		Book detailsBook=tableViewBooks.getSelectionModel().getSelectedItem();
	    		Book.setBookID(detailsBook.getId());

	    		root=FXMLLoader.load(getClass().getClassLoader().getResource("FXML/details_edit.fxml"));
	    		Stage detailStage=new Stage();
	    		detailStage.setTitle("Book Details");
	    		detailStage.setScene(new Scene(root,563,450));
	    		detailStage.show();
	    	} catch(IOException e){
	    		e.printStackTrace();
	    	}
    	}
    	else {
    		showNotification("Please select a row\nto show details");
    	}
    	
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
        System.out.println("TEST "+num);
    }
    
    
  
}
