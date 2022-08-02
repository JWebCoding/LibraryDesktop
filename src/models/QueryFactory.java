package models;

import javax.sql.rowset.CachedRowSet;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class QueryFactory {
    ConnectionCommands connectionCommands = new ConnectionCommands();

    public CachedRowSet readFromDatabase(String elementType) throws SQLException {
        String queryType="select";
        try{
            // Create the new query and then execute it
            CallableStatement query = generateQuery(queryType,elementType.toLowerCase());
            return connectionCommands.readDatabase(query);
        } catch (Exception e){
            throw new SQLException(e);
        }


    }

    public boolean writeToDatabase(String queryType, String elementType, ArrayList<Object> parameters) throws SQLException {
        try{
            // Create the new query and then execute it
            CallableStatement query = generateQuery(queryType.toLowerCase(),elementType.toLowerCase(), parameters);
            connectionCommands.writeDatabase(query);
            // Return "true" if the write was successful so that the appropriate error message can be printed on the GUI
            return true;
        } catch(Exception e){
            throw new SQLException(e);
        }
    }

    // Generate queries for writing to the database.
    private CallableStatement generateQuery(String queryType, String elementType, ArrayList<Object> parameters) throws SQLException {
        // Get the exact name of the stored procedure to be called.
        String procedureName = determineProcedureName(queryType,elementType);
        // Create the new statement
        CallableStatement newStatement = connectionCommands.connection.prepareCall("{"+procedureName+"}");
        // Add the parameters from the array list to the new statement.
        int parameterCount = determineParameterCount(procedureName);
        for(int i=0;i<parameterCount;i++){
            if(parameters.get(i).getClass() == Integer.class){
                newStatement.setInt(i+1, (Integer) parameters.get(i));
            } else if(parameters.get(i).getClass() == String.class){
                newStatement.setString(i+1, (String) parameters.get(i));
            }
        }
        return newStatement;
    }

    // Generate queries for reading from the database.
    private CallableStatement generateQuery(String queryType, String elementType) throws SQLException {
        // Get the exact name of the stored procedure to be called.
        String procedureName = determineProcedureName(queryType,elementType);
        // Create the new statement
        return connectionCommands.connection.prepareCall("{"+procedureName+"}");
    }

    private int determineParameterCount(String procedureName){
        // Return the number of parameters needed in the statement.
        int count=0;
        for(int i=5;i<procedureName.length();i++){
            Character character=procedureName.charAt(i);
            if(character.equals('?')){
                count++;
            }
        }
        return count;
    }

    private String determineProcedureName(String queryType, String elementType) {
        // A series of if/else and switch statements to find the exact stored procedure that needs to be called.
        String defaultErrorString=(elementType + " was not found under" + elementType + ". Please check the data provided.");
        if(queryType.equals("select")){
            return switch (elementType) {
                case "author" -> "GetAllAuthors";
                case "book" -> "GetAllBooks";
                case "genre" -> "GetAllGenres";
                case "language" -> "GetAllLanguages";
                case "publisher" -> "GetAllPublishers";
                case "series" -> "GetAllSeries";
                default -> throw new UnsupportedOperationException(defaultErrorString);
            };
        }else if(queryType.equals("insert")){
            return switch (elementType) {
                case "author" -> "CreateNewAuthor(?, ?)";
                case "book" -> "CreateNewBook(?,?,?,?,?,?,?,?,?,?,?,?)";
                case "genre" -> "CreateNewGenre(?, ?)";
                case "language" -> "CreateNewLanguage(?, ?)";
                case "publisher" -> "CreateNewPublisher(?, ?)";
                case "series" -> "CreateNewSeries(?)";
                default -> throw new UnsupportedOperationException(defaultErrorString);
            };
        }else if(queryType.equals("update")){
            if(elementType.equals("book")){
                return "EditSpecificBook(?,?,?,?,?,?,?,?,?,?,?,?,?)";
            } else {
                throw new UnsupportedOperationException(defaultErrorString);
            }
        } else {
            return(elementType + " was not found. Please use either Select, Insert or Update");
        }
    }
}
