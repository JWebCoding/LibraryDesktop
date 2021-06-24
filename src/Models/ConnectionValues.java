package Models;


public class ConnectionValues {
    public String username;
    public String password;
    public String URL;
    public String databaseName;

    // Constructor method for class
    public ConnectionValues(int tableID){
        getConnectionValues(tableID);
    }
    // Empty constructor
    ConnectionValues(){

    }
    // Set the values for connecting to the server
    public void getConnectionValues(int tableID){
        String serverURL="jdbc:mysql://localhost:3306";
        String serverTimeZone="?serverTimezone=CST";
        username="root";
        password="Mcpoj-117";

        switch (tableID){
            case 0: databaseName="/library"; break;
            case 1: databaseName="/library.book"; break;
            case 2: databaseName="/library.genre"; break;
            case 3: databaseName="/library.publisher"; break;
            case 4: databaseName="/library.user"; break;
            case 5: databaseName="/author"; break;
            default:databaseName="ERROR"; break;
        }
//        URL=serverURL+databaseName+serverTimeZone;
        URL=serverURL+databaseName;
    }
}
