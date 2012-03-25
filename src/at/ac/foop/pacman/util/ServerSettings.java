package at.ac.foop.pacman.util;

public interface ServerSettings {

	public static String SERVER_CONFIG_PATH = "/at/ac/foop/pacman/resources/";
	public static String SERVER_CONFIG_FILE = "serverconfig.properties";
	public static String SERVER_CONFIG = ServerSettings.SERVER_CONFIG_PATH + ServerSettings.SERVER_CONFIG_FILE;

	public static String MAPS_FOLDER = "mapFolder";
	public static String DEFAULT_MAPS_FOLDER = "maps";
	
	public static String MAP_EXT = "mapExt";
	public static String DEFAULT_MAP_EXT = "map";
	public static String MAP_FIELD_SEPARATOR = " ";
	
	public static String FIELD_POINTS = "fieldPoints";
	public static String DEFAULT_FIELD_POINTS = "1000";
}
