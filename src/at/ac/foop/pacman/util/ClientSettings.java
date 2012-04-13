package at.ac.foop.pacman.util;

public interface ClientSettings {

	public static String CLIENT_CONFIG_PATH = "/at/ac/foop/pacman/resources/";
	public static String CLIENT_CONFIG_FILE = "clientconfig.properties";
	public static String CLIENT_CONFIG = ClientSettings.CLIENT_CONFIG_PATH + ClientSettings.CLIENT_CONFIG_FILE;
	
	public static String SERVER_URL = "serverURL";
	public static String DEFAULT_SERVER_URL = "127.0.0.1";
	
	public static String SERVER_PORT = "serverPort";
	public static String DEFAULT_SERVER_PORT = "1099";
}
