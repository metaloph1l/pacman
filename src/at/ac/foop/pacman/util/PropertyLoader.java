package at.ac.foop.pacman.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class handles property files and allows
 * access to multiple files. It's implemented 
 * as a singleton so that all files are only 
 * loaded once.
 */
public class PropertyLoader {

	private static PropertyLoader instance = null;
	private Map<String, Properties> properties = null;
	
	protected PropertyLoader() {
		this.initialize();
	}

	public static PropertyLoader getInstance() {
		if(instance == null) {
			instance = new PropertyLoader();
		}
		return instance;
	}
	
	protected void initialize() {
		this.properties = new HashMap<String, Properties>();
	}
	
	private void loadProperties(String config) {
		InputStream in = this.getClass().getResourceAsStream(config);
		Properties prop = new Properties();
        try {
            prop.load(in);
            properties.put(config, prop);
        } catch (IOException e) {
        	// TODO: exception handling
            e.printStackTrace();
        }
	}
	
	public String getProperty(String prop, String property, String defaultValue) {
		if(properties.containsKey(prop) == false) {
			this.loadProperties(prop);
		}
		
		return properties.get(prop).getProperty(property, defaultValue);
	}
}
