package Models;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class ConnectionCommands {
    // Vairables
	Connection connection=null;
	CachedRowSet cachedRowSet=null;
	ResultSet resultSet=null;
	Statement statement=null;
	PreparedStatement preparedStatement =null;
	
	String serverURL;
	String serverTimeZone;
	String URL2;
	String URL;
	String username;
	String password;
	
	public void getConnectionSettings() {
		// Get the connection values and chek if they exist.
		Document document = null;
		NodeList nodeList=null;
		Element element=null;
		String[] settingsArray=new String[]{"serverURL","serverTimeZone","username","password"};
		
		// Attempt to open file
		try {
			File settingsFile=new File("settings.xml");
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder=documentFactory.newDocumentBuilder();
			document = documentBuilder.parse(settingsFile);
			
		} catch(Exception e) {
			System.out.println("\nError!\nUnable to open file: settings.xml\nPlease check file location.");
			e.printStackTrace();
		}
		
		// Get "settings" element
		try {
		nodeList=document.getElementsByTagName("settings");
		element= (Element) nodeList.item(0);
		
		} catch(Exception e) {
			System.out.println("\nError!\nSettings not found. Please check settings.xml");
			e.printStackTrace();
		}
		
		// Get server connection settings
		try {
			String setting; // The specific setting field (serverURL, password etc etc).
			String param; // The parameter used for the setting (192.168.0.33, 12345 etc etc).
			
			// Iterate through the settings in the array and find their matching pairs in the settings.XML file.
			for(int i=0;i<4;i++) {
				setting=settingsArray[i];
				param=element.getElementsByTagName(setting).item(0).getTextContent();
				
				// Check if the given parameter is empty
				if(param=="") {
					String errorMessage=String.format("\nERROR!\nThe following setting: '%s' is empty. Please Check the file: 'settings.xml' for errors.",setting);
					System.err.println(errorMessage);
				}
				// Assign the paramater to its appropriate setting.
				switch (setting) {
				case "serverURL": URL=param;
					URL2=URL;
					break;
				case "serverTimeZone": serverTimeZone=param;
					break;
				case "username": username=param;
					break;
				case "password": password=param;
					break;
				default:
					break;
				}
			}
		} catch(Exception e) {
			System.err.println("\nERROR!\nUnable to find needed settings! Please check file: settings.xml");
		} 
	}
	
	private void createServerConnection() {
		// Create a connection to the server and test its validity
		
		// Check if the connection values exist.
		if(URL!=null||serverTimeZone!=""||username!=""||password!="") {
			// Attempt to create a connection
			try {
				connection=DriverManager.getConnection(URL, username, password);
				URL=URL2;
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				// Determine if the connection will work and close it if not..
				if(connection==null) {
					System.err.println("Unable to establish a connection to the server.");
				}
				else {
					System.err.println("Connection to MYSQL server established!");
				}
			}
		}
		else {
			System.err.println("Setting values not found");
		}
	}
	
	private void closeServerConnection() {
		try {
			connection.close();
			System.err.println("Connection terminated.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createCachedRowSet() {
		// Create a resultSet from the preparedStatement and populate a cached rowset with the data.
		try {
			resultSet=preparedStatement.executeQuery();
			RowSetFactory factory = RowSetProvider.newFactory();
			cachedRowSet = factory.createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch(Exception e) {
			System.err.println("Unable to create a CachedRowSet.");
		}
		
	}
	
	private void createPreparedStatement(String query) {
		try {
			preparedStatement=connection.prepareStatement(query);
		} catch(Exception e) {
			System.err.println("Unable to create PreparedStatement.");
		}
		
	}
	
	public void testServerConnection() {
		createServerConnection();
	}
	
	public void writeDatabase(String query) {
		createServerConnection();
		createPreparedStatement(query);
		try {
			int row=preparedStatement.executeUpdate();
		} catch(Exception e) {
			System.err.println("Unable to execute update query.");
			e.printStackTrace();
		}
		
	}
	
	public CachedRowSet readDatabase(String query) {
		createServerConnection();
		createPreparedStatement(query);
		createCachedRowSet();
		return cachedRowSet;
	}
	
	// Testing methods
	private void testPrintString(String string) {
		System.out.println(string);
	}
	private void testPrintInt(Connection connection2) {
		System.out.println(connection2);
	}
}