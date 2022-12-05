package com.library.models;

import javax.sql.rowset.CachedRowSet;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

public class QueryFactory {
    ConnectionCommands connectionCommands = new ConnectionCommands();

    // Use for general "select" statements
    public CachedRowSet readFromDatabase(String elementType) throws SQLException {
        String queryType="select";
        try{
            // Create the new query and then execute it
            CallableStatement query = generateQuery(queryType,elementType);
            return connectionCommands.readDatabase(query);
        } catch (SQLException e){
            throw new SQLException(e);
        }
    }

    // Use for specific "select" statements
    public CachedRowSet readFromDatabase(String elementType, ArrayList<Object> parameters) throws SQLException{
        String queryType="select";
        try{
            // Create the new query and then execute it
            CallableStatement query = generateQuery(queryType,elementType,parameters);
            return connectionCommands.readDatabase(query);
        } catch (SQLException e){
            throw new SQLException(e);
        }
    }

    // Use for insert/update/delete statements.
    public boolean writeToDatabase(String queryType, String elementType, ArrayList<Object> parameters) throws SQLException {
        try{
            // Create the new query and then execute it
            CallableStatement query = generateQuery(queryType,elementType, parameters);
            connectionCommands.writeDatabase(query);
            // Return "true" if the write was successful so that the appropriate error message can be printed on the GUI
            return true;
        } catch(SQLException e){
            throw new SQLException(e);
        }
    }

    // Generate queries for writing to the database.
    // Uses an array list
    private CallableStatement generateQuery(String queryType, String elementType, ArrayList<Object> parameters) throws SQLException {
        // Create a connection
        connectionCommands.getConnectionSettings();
        connectionCommands.testServerConnection();
        // Get the exact name of the stored procedure to be called.
        String procedureName = determineProcedureName(queryType,elementType);
        // Create the new statement
        CallableStatement newStatement = connectionCommands.connection.prepareCall("{ call "+procedureName+"}");
        try {
            // Add the parameters from the array list to the new statement.
            int parameterCount = determineParameterCount(procedureName);
            for(int i=0;i<parameterCount;i++){
                Object newParameter=parameters.get(i).getClass();
                // If the parameter is either an Integer or int
                if(newParameter == Integer.class || newParameter == int.class){
                    newStatement.setInt(i+1, (Integer) parameters.get(i));

                    // if the Parameter is Long
                } else if(newParameter == Long.class){
                    newStatement.setLong(i+1, (Long) parameters.get(i));

                    // If the parameter is a String
                } else if(newParameter == String.class){
                    if (parameters.get(i).equals("")){
                        newStatement.setNull(i+1, Types.VARCHAR);
                    } else {
                        newStatement.setString(i+1,(String) parameters.get(i));
                    }
                }
            }
        } catch (SQLException  e){
            e.printStackTrace();
        }
        return newStatement;
    }

    // Generate queries for reading from the database.
    // Does no require an array list
    private CallableStatement generateQuery(String queryType, String elementType) throws SQLException {
        // Create a connection
        connectionCommands.getConnectionSettings();
        connectionCommands.testServerConnection();
        // Get the exact name of the stored procedure to be called.
        String procedureName = determineProcedureName(queryType,elementType);
        // Create the new statement
        return connectionCommands.connection.prepareCall("{ call "+procedureName+"}");
    }

    private int determineParameterCount(String procedureName){
        // Return the number of parameters needed in the statement.
        int count=0;
        for(int i=0;i<procedureName.length();i++){
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
                case "specificBook" -> "GetSpecificBook(?)";
                case "genre" -> "GetAllGenres";
                case "language" -> "GetAllLanguages";
                case "publisher" -> "GetAllPublishers";
                case "series" -> "GetAllSeries";
                case "user" -> "GetSpecificUser(?)";
                default -> throw new UnsupportedOperationException(defaultErrorString);
            };
        }else if(queryType.equals("insert")){
            return switch (elementType) {
                case "author" -> "CreateNewAuthor(?)";
                case "book" -> "CreateNewBook(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                case "genre" -> "CreateNewGenre(?, ?)";
                case "language" -> "CreateNewLanguage(?, ?)";
                case "publisher" -> "CreateNewPublisher(?, ?)";
                case "series" -> "CreateNewSeries(?)";
                default -> throw new UnsupportedOperationException(defaultErrorString);
            };
        }else if(queryType.equals("update")){
            if(elementType.equals("book")){
                return "UpdateSpecificBook(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            } else {
                throw new UnsupportedOperationException(defaultErrorString);
            }
        }else if(queryType.equals("delete")){
            if(elementType.equals("book")){
                return "DeleteBook(?)";
            } else {
                throw new UnsupportedOperationException(defaultErrorString);
            }
        } else {
            return(elementType + " was not found. Please use either Select, Insert or Update");
        }
    }
}
