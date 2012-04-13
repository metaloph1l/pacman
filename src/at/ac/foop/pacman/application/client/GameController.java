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
import java.util.Map;

/**
 * The client game controllers function is to receive the calls
 * from the server instance and simulate the game. Here we also
 * need to send the direction change of the user to the server.
 *  
 * @author S. Sebastian Geiger
 *
 */
public class GameController extends Observable implements IGame {
	List<Player> players;
	Labyrinth map;
	int count;
	private IGameServer server;
	
	GameController(IGameServer server) {
		this.server = server;
	}

	public void init(String name) throws RemoteException {
		//Initialize the players
		players = new ArrayList<Player>(Player.PLAYER_COUNT);
		for(int i=0; i< Player.PLAYER_COUNT; i++) {
			players.add(new Player());
		}
		try {
			//1. Connect to the server
			Long playerId = this.server.connect(this);

			//2. Download the map and the player positions
			this.map = this.server.downloadMap();

			//3. Setup a representation of the game locally
			//   that includes: Players, Pacmans, Labyrinth, etc.
			// TODO: This players are only placeholders for the real players
			//       that get sent to the client once the game starts!
			Map<Long, Point> positions = server.getPositions();
			for(Long id : positions.keySet()) {
				Pacman pacman = new Pacman();
				//Set the start color according to the id
				pacman.setColor(PacmanColor.values()[id.intValue()]);
				players.get(id.intValue()).setPacman(pacman);
				Point point = positions.get(id);
				//Give the pacman the square on which is should be
				pacman.setLocation(map.getSquare(point.x, point.y));
			}
			
			//4. Finally signal the server that we are ready
			this.server.setName(playerId, name);
			this.server.ready(playerId);
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
		
		System.out.println("MAPChanged!!!");
	}

	@Override
	public void clock(int count, Map<Long, Direction> directions)
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

	@Override
	public void startRound(List<Player> players) throws RemoteException {
		this.players = players;
		//TODO: update representation according to the sent players.
	}
}
