package com.library.controllers;

import com.library.Main;
import com.library.models.Book;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.library.models.QueryFactory;
import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchBookController {
    QueryFactory queryFactory = new QueryFactory();

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
    String title,subtitle,firstName,middleName,lastName,isbn,series,publisher,genre;
    int id,year;
    Integer seriesPart;
    ArrayList<Object> elementsArrayList= new ArrayList<>();
    CachedRowSet bookCollection=null;


    public void initialize() throws Exception{
    	Book.resetBookNum();
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

    // Get an up-to-date copy of the MySQL book database
    private void getBookCollectionData() throws Exception{
        try{
            bookCollection=queryFactory.readFromDatabase("book");
        } catch (Exception e){
            throw new Exception(e);
        }

    }

    // Construct an instance of the Book class
    private Book createBook() throws SQLException {

        id=bookCollection.getInt("bookID");
        title=bookCollection.getString("title");
        subtitle=bookCollection.getString("subtitle");
        series=bookCollection.getString("series_name");
        seriesPart=bookCollection.getInt("series_part");
        if(seriesPart==0) {
        	seriesPart=null;
        }
        firstName=bookCollection.getString("first_name");
        middleName=bookCollection.getString("middle_name");
        lastName=bookCollection.getString("last_name");
        publisher=bookCollection.getString("publisher_name");
        isbn=bookCollection.getString("isbn");
        year=bookCollection.getInt("copyright");
        genre=bookCollection.getString("genre_name");

        return new Book(id,title,subtitle,series,seriesPart,firstName,middleName,lastName,publisher,isbn,year,genre);
    }
    
    // Fill the BookNumber label
    private void setBookNumberLabel() {
    	String numOfBooks= String.valueOf(Book.getBookNum());
    	labelBookNumber.setText(numOfBooks);
    }

    // Fill the search table with book information
    private void fillTable() throws SQLException {
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

            // Reset the table and cached row set
            tableViewBooks.getItems().clear();
            bookCollection.first();

            // Determine which button is selected and set the search text to the appropriate column
            RadioButton selectedRadioButton= (RadioButton) toggleSearchButtons.getSelectedToggle();
            switch (selectedRadioButton.getText()) {
                case "Title" -> selectedColumn = "title";
                case "Publisher" -> selectedColumn = "publisher_name";
                case "ISBN" -> selectedColumn = "isbn";
                case "Genre" -> selectedColumn = "genre_name";
                case "Series Name" -> selectedColumn = "series_name";
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
        	throw new Exception(e);
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
    
    public void removeBook() throws Exception {
    	if(tableViewBooks.getSelectionModel().getSelectedItem()==null) {
    		showNotification("Please select a book to be deleted.");
    	}else {
            try{
                Book removedBook=tableViewBooks.getSelectionModel().getSelectedItem();
                elementsArrayList.add(removedBook.getId());
                queryFactory.writeToDatabase("delete","book",elementsArrayList);
                showNotification(removedBook.getTitle()+"\ndeleted from system.");
            } catch (Exception e) {
                throw new SQLException(e);
            } finally {
                elementsArrayList.clear();
                refreshTable();
            }
    	}
    }

    public void openScreenDetails() throws IOException {
    	if(tableViewBooks.getSelectionModel().getSelectedItem()!=null) {
	    	try {
	    		Book detailsBook=tableViewBooks.getSelectionModel().getSelectedItem();
	    		Book.setBookID(detailsBook.getId());

                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/DetailsEditPane.fxml"));
                Parent root = fxmlLoader.load();

	    		Stage detailStage=new Stage();
	    		detailStage.setTitle("Book Details");
                detailStage.setResizable(false);
	    		detailStage.setScene(new Scene(root,547,539));
	    		detailStage.show();
	    	} catch(IOException e){
	    		throw new IOException(e);
	    	}
    	}
    	else {
    		showNotification("Please select a row\nto show details");
    	}
    	
    }

    private void showNotification(String notification){
        labelNotification.setText(notification);
        labelNotification.setVisible(true);
    }

    private void hideNotification(){
        labelNotification.setText("");
        labelNotification.setVisible(false);
    }
    
    
  
}
