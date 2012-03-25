package at.ac.foop.pacman.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapLoader {
	
	private static MapLoader instance = null;
	private List<String> mapFiles = null;
	private Map<String, String[][]> maps = null;
	
	protected MapLoader() {
	}

	public static MapLoader getInstance() {
		if(instance == null) {
			instance = new MapLoader();
		}
		return instance;
	}

	/**
	 * loads a map names (fileNames) from a folder
	 * 
	 */
	public void loadMaps() {		
		File mapDir = new File(MapLoader.getMapFolderPath());

		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(MapLoader.getMapFileExtension());
		    }
		};
		
		this.mapFiles = Arrays.asList(mapDir.list(filter));
	}
	
	/**
	 * loads a random map from a folder
	 * 
	 * @return array representing the map
	 */
	public String[][] loadRandomMap() {
		if((this.mapFiles == null) || (this.mapFiles.size() == 0)) {
			this.loadMaps();
		}
		
		int mapNumber = (int) (Math.random() * this.mapFiles.size());
		String mapName = this.mapFiles.get(mapNumber);
		
		if ((this.maps != null) && (this.maps.containsKey(mapName))) {
			return this.maps.get(mapName);
		}
		else {
			String[][] map = this.readFile(mapName);
			if (map == null) {
				return null;
			}
			else {
				if(this.maps == null) {
					this.maps = new HashMap<String, String[][]>();
				}
				this.maps.put(mapName, map);
				return map;
			}
		}
	}
	
	/**
	 * This reads a map file and stores its 
	 * values as an array. The map has to 
	 * be consistent (each row has to have
	 * the same number of columns). The file 
	 * itself has to end with a newline character.
	 * 
	 * @param fileName The filename of the map
	 * @return array representing the map
	 */
	private String[][] readFile(String fileName) {
		String[][] mapArr = null;
		try {
			LineNumberReader lnr = new LineNumberReader(
											new FileReader(
												MapLoader.getMapFolderPath() + File.separator +  fileName));
			
			// TODO: maybe read line by line and build array afterwards
			lnr.mark(64000);
			// jump to the end of the file
			lnr.skip(Long.MAX_VALUE);
			int rowNumbers = lnr.getLineNumber();
			// reset the file
			lnr.reset();
			
			int columnNumbers = lnr.readLine().split(ServerSettings.MAP_FIELD_SEPARATOR).length;
			lnr.reset();
			
			mapArr = new String[rowNumbers][columnNumbers];
			
			String strLine = null;
			int i = 0;
			while ((strLine = lnr.readLine()) != null) {
				mapArr[i] = strLine.split(ServerSettings.MAP_FIELD_SEPARATOR);
				i++;
		  	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return mapArr;
	}
	
	public static String getMapFolderPath() {
		return PropertyLoader.getInstance().getProperty(ServerSettings.SERVER_CONFIG, 
															ServerSettings.MAPS_FOLDER, 
															ServerSettings.DEFAULT_MAPS_FOLDER);
	}
	
	public static String getMapFileExtension() {
		return PropertyLoader.getInstance().getProperty(ServerSettings.SERVER_CONFIG, 
															ServerSettings.MAP_EXT, 
															ServerSettings.DEFAULT_MAP_EXT);
	}
}
