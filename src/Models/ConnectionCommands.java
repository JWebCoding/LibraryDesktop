package Models;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class ConnectionCommands {
    Connection connection=null;
    CachedRowSet cachedRowSet=null;
    Statement statement=null;
    PreparedStatement preparedStatement=null;

    public void createConnection(ConnectionValues connectionValues) throws SQLException {
        connection= DriverManager.getConnection(connectionValues.URL,connectionValues.username,connectionValues.password);
    }

    public void createCachedRowSet(ConnectionValues connectionValues, String query) throws SQLException{
        cachedRowSet= RowSetProvider.newFactory().createCachedRowSet();
        cachedRowSet.setUsername(connectionValues.username);
        cachedRowSet.setPassword(connectionValues.password);
        cachedRowSet.setUrl(connectionValues.URL);
        cachedRowSet.setCommand(query);
    }

    public CachedRowSet readDatabase(int tableID,String query)throws Exception{
        ConnectionValues connectionValues=new ConnectionValues(tableID);
        createConnection(connectionValues);
        createCachedRowSet(connectionValues,query);


        try{
            cachedRowSet.execute();
            return cachedRowSet;
        } catch (Exception e){
            System.out.println("ERROR\nFunction: readDataBase\nClass: SQLCommands\n");
            System.err.print(e);
        } finally {
            connection.close();
        }
        return cachedRowSet;
    }

    public void writeDataBase(int tableID,String query) throws Exception{
        try{
            ConnectionValues connectionValues=new ConnectionValues(tableID);
            Class.forName("com.mysql.cj.jdbc.Driver");
            String username="jweber";
            String password="Mcpoj-117";
            connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/library?"+"user="+username+"&password="+password);
            statement=connection.createStatement();

            preparedStatement=connection.prepareStatement(query);

            preparedStatement.executeUpdate();

        } catch (Exception e){
            throw e;
        }finally {
            System.out.println("Done");
        }
    }
}
