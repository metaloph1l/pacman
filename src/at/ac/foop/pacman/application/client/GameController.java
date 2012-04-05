package at.ac.foop.pacman.application.client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.domain.PacmanColor;
import at.ac.foop.pacman.domain.Player;
import at.ac.foop.pacman.domain.Square;

/**
 * The client game controllers function is to receive the calls
 * from the server instance and simulate the game. Here we also
 * need to send the direction change of the user to the server.
 *  
 * @author S. Sebastian Geiger
 *
 */
public class GameController extends Observable implements IGame {
	public static int NUM_PLAYERS = 3;
	List<Player> players;
	Labyrinth map;
	int count;
	private IGameServer server;

	public void init() {
		//Initialize the players
		players = new ArrayList<Player>(NUM_PLAYERS);
		for(int i=0; i< NUM_PLAYERS; i++) {
			players.add(new Player());
		}
		try {
			//1. Connect to the server

			//2. Download the map and the player positions
			Square[][] map = this.server.downloadMap();
			this.map = new Labyrinth(map);

			//3. Setup a representation of the game locally
			//   that includes: Players, Pacmans, Labyrinth, etc.
			Hashtable<Long, Point> positions = server.getPositions();
			for(Long id : positions.keySet()) {
				Pacman pacman = new Pacman();
				//Set the start color according to the id
				pacman.setColor(PacmanColor.values()[id.intValue()]);
				players.get(id.intValue()).setPacman(pacman);
				Point point = positions.get(id);
				//Give the pacman the square on which is should be
				pacman.setLocation(map[point.x][point.y]);
			}
			
			//4. Finally signal the server that we are ready
			this.server.ready();
		} catch(RemoteException e) {
			//TODO: handle any server exception here
		}
		//NOTE: This controller has no play method. Every change
		//      to the game come from the server through the RMI
		//      methods.
	}

	@Override
	public void notifyMapChange() throws RemoteException {
		//This indicates that the server wants to change the map.
		//TODO: We need to pause the game and download the map on a separate thread
	}

	@Override
	public void clock(int count, Hashtable<Long, Direction> directions)
			throws RemoteException {
		for(Long key : directions.keySet()) {
			Direction direction = directions.get(key);
			Player player = players.get(key.intValue());
			player.setDirection(direction);
		}
		this.count = count;
	}

	@Override
	public void changeColor() throws RemoteException {
		//notifiy the UI that we are changing colors
		//TODO: the UI must not update itself while we change colors
		for(Player player : players) {
			player.changeColor();
		}
		//notify the UI that colors have been changed
	}
}
