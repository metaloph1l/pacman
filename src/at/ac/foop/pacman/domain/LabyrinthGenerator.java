package at.ac.foop.pacman.domain;

import java.util.ArrayList;
import java.util.List;

import at.ac.foop.pacman.util.MapLoader;
import at.ac.foop.pacman.util.PropertyLoader;
import at.ac.foop.pacman.util.ServerSettings;

public class LabyrinthGenerator {
	
	public static Labyrinth generateLabyrinth() {
		return new Labyrinth(LabyrinthGenerator.generateSquares());
	}
	
	public static Square[][] generateSquares() {
		String[][] mapArrStr = MapLoader.getInstance().loadRandomMap();
		if(mapArrStr == null) {
			// TODO: ExceptionHandling
			return null;
		}
		Square[][] map = new Square[mapArrStr.length][mapArrStr[0].length];

		Integer fieldPoints = Integer.parseInt(PropertyLoader.getInstance().
			getProperty(ServerSettings.SERVER_CONFIG,
			ServerSettings.FIELD_POINTS,
			ServerSettings.DEFAULT_FIELD_POINTS)
		);

		for (int i = 0; i < mapArrStr.length; i++) {
			for(int j = 0; j < mapArrStr[i].length; j++) {
				if(mapArrStr[i][j].equals("W")) {
					// create wall field
					map[i][j] = new Wall();
				}
				else if(mapArrStr[i][j].equals("E")) {
					// create empty field
					map[i][j] = new Field(fieldPoints);
				}
				else if(mapArrStr[i][j].equals("P")) {
					map[i][j] = new Field(0);
				}
				else {
					// default: empty field
					map[i][j] = new Field(fieldPoints);
				}
				Coordinate coord = new Coordinate();
				coord.setX(i);
				coord.setY(j);
				map[i][j].setCoordinate(coord);
			}
		}
		return map;
	}
	
	public static List<Coordinate> getPacmanPositions() {
		List<Coordinate> positions = new ArrayList<Coordinate>();
		String[][] mapArrStr = MapLoader.getInstance().getLastMap();
		if(mapArrStr == null) {
			// TODO: ExceptionHandling
			return null;
		}
		
		for (int i = 0; i < mapArrStr.length; i++) {
			for(int j = 0; j < mapArrStr[i].length; j++) {
				if(mapArrStr[i][j].equals("P")) {
					Coordinate coord = new Coordinate();
					coord.setX(i);
					coord.setY(j);
					positions.add(coord);
				}
			}
		}
		return positions;
	}
}
