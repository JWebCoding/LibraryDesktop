package com.library.models;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.io.*;
import java.util.Objects;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class ConnectionCommands {
    // Variables
	Connection connection=null;
	CachedRowSet cachedRowSet=null;
	ResultSet resultSet=null;
	
	ConnectionSettings connectionSettings=new ConnectionSettings();
	
	String serverURL;
	String serverTimeZone;
	String URL2;
	String URL;
	String username;
	String password;
	
	public void getConnectionSettings() {
		// Get the connection values and chek if they exist.
		Document document = null;
		NodeList nodeList;
		Element element=null;
		String[] settingsArray=new String[]{"serverURL","serverTimeZone","username","password"};
		
		// Attempt to open file
		try {
			File settingsFile=new File("settings.xml");
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder=documentFactory.newDocumentBuilder();
			document = documentBuilder.parse(settingsFile);
			
		} catch(SAXException | IOException e) {
			System.err.println("\nError!\nUnable to open file: settings.xml\nPlease check file location.");
		} catch (ParserConfigurationException e) {
			System.err.println("\nError!\nUnable to parse the file: settings.xml");
		}

		// Get "settings" element
		try {
		nodeList=document.getElementsByTagName("settings");
		element= (Element) nodeList.item(0);
		
		} catch(Exception e) {
			System.out.println("\nError!\nNo settings found in file. Please check settings.xml");
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
				if(Objects.equals(param, "")) {
					String errorMessage=String.format("\nERROR!\nThe following setting: '%s' is empty. Please Check the file: 'settings.xml' for missing data.",setting);
					System.err.println(errorMessage);
				}
				// Assign the paramater to its appropriate setting.
				switch (setting) {
					case "serverURL" -> {
						connectionSettings.setURL(param);
						URL2 = URL;
					}
					case "serverTimeZone" -> connectionSettings.setServerTimeZone(param);
					case "username" -> connectionSettings.setUsername(param);
					case "password" -> connectionSettings.setPassword(param);
					default -> {
					}
				}
			}

		} catch(Exception e) {
			System.err.println("\nERROR!\nUnable to find needed settings! Please check file: settings.xml");
		} 
	}
	
	private void createServerConnection() {
		// Create a connection to the server and test its validity
		URL=connectionSettings.getURL();
		username=connectionSettings.getUsername();
		password=connectionSettings.getPassword();
		// Check if the connection values exist.
		if(URL!=null||serverTimeZone!=""||username!=""||password!="") {
			// Attempt to create a connection
			try {
				connection = DriverManager.getConnection(URL, username, password);
				URL = URL2;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				// Determine if the connection will work and close it if not.
				if(connection==null) {
					System.err.println("Unable to establish a connection to the server.");
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
	
	private void createCachedRowSet(CallableStatement callableStatement) throws SQLException {
		// Create a resultSet from the preparedStatement and populate a cached rowset with the data.
		try {
			resultSet=callableStatement.executeQuery();
			RowSetFactory factory = RowSetProvider.newFactory();
			cachedRowSet = factory.createCachedRowSet();
			cachedRowSet.populate(resultSet);
		} catch(Exception e) {
			throw new SQLException(e);
		}

	}
	
	public void testServerConnection() {
		createServerConnection();
	}
	
	public void writeDatabase(CallableStatement query) {
		createServerConnection();
		try {
			query.executeUpdate();
		} catch(Exception e) {
			System.err.println("Unable to execute update query.");
			e.printStackTrace();
		}
		
	}
	
	public CachedRowSet readDatabase(CallableStatement query) throws SQLException {
		createServerConnection();
		createCachedRowSet(query);
		return cachedRowSet;
	}
}