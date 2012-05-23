package at.ac.foop.pacman.application.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Queue;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.domain.Coordinate;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.GameOutcome;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.domain.PacmanColor;
import at.ac.foop.pacman.domain.Player;
import at.ac.foop.pacman.domain.PlayerOutcome;
import at.ac.foop.pacman.domain.Square;
import at.ac.foop.pacman.domain.Field;
import at.ac.foop.pacman.domain.SquareType;

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
	private final IGameServer server; //Interface to the game server
	private Long playerId; //The id of this clients player
	private final Queue<GameState> states = new LinkedList<GameState>();
	private final Logger logger;

	GameController(IGameServer server) {
		this.server = server;
		logger = Logger.getLogger(GameController.class);
	}

	public void init() throws RemoteException {
		//Initialize the players
		players = new ArrayList<Player>(Player.PLAYER_COUNT);
		for(int i=0; i< Player.PLAYER_COUNT; i++) {
			Player player = new Player();
			player.setId(new Long(i));
			players.add(player);
		}
		//NOTE: This controller has no play method. Every change
		//      to the game come from the server through the RMI
		//      methods.
	}
	
	public void connect(String name) throws RemoteException {
		try {
			//1. Connect to the server
			playerId = this.server.connect(this);
			logger.info("Client connected to server");

			//2. Inform the server of this players name
			this.server.setName(playerId, name);

			Player player = players.get(playerId.intValue() - 1);
			if(player.getId().equals(this.playerId)) {
				player.setName(name);
			}

			//4. Finally signal the server that we are ready
			this.server.ready(playerId);
		} catch(RemoteException e) {
			//TODO: handle any server exception here
			logger.error("Error", e);
		}
	}
	
	public void disconnect() throws RemoteException {
		try {
			if(this.server != null) {
				this.server.disconnect(playerId);
			}
		} catch(RemoteException e) {
			//TODO: handle any server exception here
			logger.error("Error", e);
		}
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
	
	public void movePacman(Direction dir) {
		try {
			this.server.changeDirection(this.playerId, dir);
		} catch (RemoteException e) {
			// TODO: Error Handling
			logger.error("Error", e);
		}
	}

	@Override
	public void notifyMapChange(Labyrinth labyrinth) throws RemoteException {
		//This indicates that the server wants to change the map.
		//set the next map
		this.map = labyrinth;
		nextMap = map;
		logger.info("A new map has been received");
		//notify the UI that a new map has been downloaded
		states.add(GameState.NEW_MAP);
		
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
	}

	@Override
	public void notifyPositions(Map<Long, Coordinate> positions) {
		//3. Setup a representation of the game locally
		//   that includes: Players, Pacmans, Labyrinth, etc.
		// TODO: This players are only placeholders for the real players
		//       that get sent to the client once the game starts!
		// logger.info("IN NOTIFY POSITIONS START");
		// this.playerOutput();
		for(Long id : positions.keySet()) {
			Player player = players.get(id.intValue() - 1);
			Pacman pacman = player.getPacman();
			//Set the start color according to the id
			
			Coordinate point = positions.get(id);
			//Give the pacman the square on which is should be
			map.getSquare(point.getX(), point.getY()).enter(player);
			pacman.setLocation(map.getSquare(point.getX(), point.getY()));
			pacman.setColor(PacmanColor.values()[id.intValue() - 1]);
			//player.setPacman(pacman);
			
			
			/* states.add(GameState.NEW_POSITION);
			this.setChanged();
			this.notifyObservers();
			this.clearChanged(); */
		}
		
		states.add(GameState.NEW_POSITIONS);
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
		
		// logger.info("IN NOTIFY POSITIONS END");
		// this.playerOutput();
	}

	@Override
	public void notifyClock(Integer count, Map<Long, Direction> directions)
			throws RemoteException {
		for(Long key : directions.keySet()) {
			Direction direction = directions.get(key);
			Player player = players.get(key.intValue() - 1);
			player.setDirection(direction);
			this.movePacmans();
		}
		this.count = count;
		//notify the UI that the player positions have changed
		this.notifyObservers();
	}
	
	private void playerOutput() {
		for (Player player : players) {
			if (player == null) {
				continue;
			}
			Pacman pacman = player.getPacman();
			if (pacman == null) {
				continue;
			}
			
			Square currentSquare = pacman.getLocation();
			Square currentSquareMap = map.getSquare(currentSquare, Direction.NONE);

			logger.info("CURRENT PLAYER: " + player);
			logger.info("CURRENT SQUARE: " + currentSquare.getCoordinate());
			logger.info("CURRENT SQUARE: " + currentSquare);
			for(int i = 0; i < ((Field)currentSquare).getPlayers().size(); i++) {
				logger.info("PLAYER ON SQUARE: " + ((Field)currentSquare).getPlayers().get(i));
			}
			
			logger.info("CURRENT SQUARE FROM MAP: " + currentSquareMap.getCoordinate());
			logger.info("CURRENT SQUARE FROM MAP: " + currentSquareMap);
			for(int i = 0; i < ((Field)currentSquareMap).getPlayers().size(); i++) {
				logger.info("PLAYER ON SQUARE: " + ((Field)currentSquareMap).getPlayers().get(i));
			}
		}
	}

	private void movePacmans() {
		for (Player player : players) {
			Pacman pacman = player.getPacman();

			if (pacman.isAlive()) {
				Square currentSquare = pacman.getLocation();
				Direction direction = pacman.getDirection();
				Square nextSquare = map.getSquare(currentSquare, direction);



				if (SquareType.WALL.equals(nextSquare.getType())) {
					//Nothing to do. The player hits a wall.
				} else {
					currentSquare.leave(player);
					nextSquare.enter(player);
					player.getPacman().setLocation(nextSquare);
				}
			}
		}
	}

	@Override
	public void notifyColorChange() throws RemoteException {
		//notify the UI that we are changing colors
		for(Player player : players) {
			player.changeColor();
		}
		//notify the UI that colors have been changed
		this.states.add(GameState.NEW_COLOR);
		this.notifyObservers();
	}

	@Override
	public void notifyPlayers(List<Player> players) throws RemoteException {
		this.players = players;
		//logger.info("IN NOTIFY PLAYERS");
		//this.playerOutput();
		//logger.info("[Unimplemented] Player list received.");
	}
	@Override
	public void notifyPlayer(Player player) throws RemoteException {
		logger.info("[Unimplemented] Single Player update received.");
	}

	@Override
	public void notifyScore(Map<Long, Long> statistics) throws RemoteException {
		logger.info("[Unimplemented] New scores received.");
	}

	@Override
	public void notifyReady(Long playerId) throws RemoteException {
		logger.info("[Unimplemented] Player is ready");
	}

	@Override
	public void notifyGameStarting() throws RemoteException {
		states.add(GameState.GAME_START);
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
		logger.info("[Unimplemented] Game has started.");
	}

	@Override
	public void notifyGameOver(GameOutcome type, Map<Long, PlayerOutcome> outcome) throws RemoteException {
		logger.info("[Unimplemented] Game over.");
	}

	public GameState removeState() {
		return states.remove();
	}

	@Override
	public void notifyNameChange(Long id, String name) throws RemoteException {
		players.get(id.intValue()).setName(name);
		states.add(GameState.NEW_PLAYER);
		//notify the UI that a player name has changed
		this.setChanged();
		this.notifyObservers();
		this.clearChanged();
    }
}
