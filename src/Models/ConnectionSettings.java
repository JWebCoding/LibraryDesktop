package Models;

public class ConnectionSettings {
	public static String URL;
	public static String serverTimeZone;
	public static String username;
	public static String password;
	
	public void setURL(String URL) {this.URL =URL; }
	public void setServerTimeZone(String serverTimeZone) { this.serverTimeZone = serverTimeZone; }
	public void setUsername(String username) { this.username = username; }
	public void setPassword(String password) { this.password = password; }
	
	public String getURL() { return URL; }
	public String getServerTimeZone() { return serverTimeZone; }
	public String getUsername() { return username; }
	public String getPassword() { return password; }
	
}
