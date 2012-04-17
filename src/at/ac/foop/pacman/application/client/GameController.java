package at.ac.foop.pacman.application.client;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.domain.PacmanColor;
import at.ac.foop.pacman.domain.Player;
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
	Labyrinth nextMap;
	int count;
	private IGameServer server; //Interface to the game server
	private Long playerId; //The id of this clients player
	private Queue<GameState> states = new LinkedList<GameState>();
	
	GameController(IGameServer server) {
		this.server = server;
	}

	public void init(String name) throws RemoteException {
		//Initialize the players
		players = new ArrayList<Player>(Player.PLAYER_COUNT);
		for(int i=0; i< Player.PLAYER_COUNT; i++) {
			Player player = new Player();
			player.setId(new Long(i));
			players.add(player);
		}
		try {
			//1. Connect to the server
			playerId = this.server.connect(this);
			
			//2. Inform the server of this players name
			this.server.setName(playerId, name);

			//3. Setup a representation of the game locally
			//   that includes: Players, Pacmans, Labyrinth, etc.
			// TODO: This players are only placeholders for the real players
			//       that get sent to the client once the game starts!
			Map<Long, Point> positions = server.getPositions();
			for(Long id : positions.keySet()) {
				Player player = players.get(id.intValue());
				if(player.getId().equals(this.playerId)) {
					player.setName(name);
				}
				Pacman pacman = new Pacman();
				//Set the start color according to the id
				pacman.setColor(PacmanColor.values()[id.intValue()]);
				player.setPacman(pacman);
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
	
	/**
	 * This should be uses by the user interface to retrieve the
	 * current list of players. The user interface should
	 * poll the list of players when it receives an notification
	 * and the NEW_PLAYER state is enqueued.
	 * @return
	 */
	public List<Player> getPlayers() {
		return this.players;
	}
	
	/**
	 * Get the current map.
	 * The UI should call this in response to the
	 * NEW_MAP state.
	 */
	public Labyrinth getMap() {
		return this.map;
	}

	@Override
	public void notifyMapChange(Labyrinth map) throws RemoteException {
		//This indicates that the server wants to change the map.
		//set the next map
		nextMap = map;
		System.out.println("A new map has been received");
		//notify the UI that a new map has been downloaded
		states.add(GameState.NEW_MAP);
		this.notifyObservers();
	}

	@Override
	public void clock(int count, Map<Long, Direction> directions)
			throws RemoteException {
		for(Long key : directions.keySet()) {
			Direction direction = directions.get(key);
			Player player = players.get(key.intValue());
			player.setDirection(direction);
			player.takeTurn();
		}
		this.count = count;
		//notify the UI that the player positions have changed
		this.notifyObservers();
	}

	@Override
	public void changeColor() throws RemoteException {
		//notify the UI that we are changing colors
		for(Player player : players) {
			player.changeColor();
		}
		//notify the UI that colors have been changed
		this.states.add(GameState.NEW_COLOR);
		this.notifyObservers();
	}

	@Override
	public void startRound(List<Player> players) throws RemoteException {
		this.players = players;
		//TODO: update representation according to the sent players.
	}

	@Override
	public void setName(Long id, String name) throws RemoteException {
		players.get(id.intValue()).setName(name);
		states.add(GameState.NEW_PLAYER);
		//notify the UI that a player name has changed
		this.notifyObservers();
	}

	@Override
	public void setReady(Long id) throws RemoteException {
		//Not implemented so far.
	}

	public GameState removeState() {
		return states.remove();
	}
}
