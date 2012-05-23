package at.ac.foop.pacman.application.gameserver;

import java.net.ConnectException;
import java.nio.channels.AlreadyConnectedException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.domain.Coordinate;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Field;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.LabyrinthGenerator;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.domain.Player;
import at.ac.foop.pacman.domain.PlayerSlot;
import at.ac.foop.pacman.domain.Square;
import at.ac.foop.pacman.domain.SquareType;
import at.ac.foop.pacman.util.MethodCallBuilder;

/**
 *
 * @author Phil
 */
public class GameController extends UnicastRemoteObject implements IGameServer {
	//Constants

	public static int CLOCK_LENGTH = 500;
	public static int CLOCKS_PER_ROUND = 100;
	public static int NUM_OF_ROUNDS_PER_GAME = 3;
	private static GameController instance = null;
	//Fields
	private final List<PlayerSlot> players;
	private final Map<Long, Boolean> connected;
	private final Map<Long, IGame> callbacks;
	private final Map<Long, Boolean> ready;
	private final Logger logger;
	private int round;
	private int clock;
	private Labyrinth map;
	private boolean play;
	private Timer timer;

	//Constructors
	private GameController() throws RemoteException {
		this.round = 0;
		this.clock = 0;
		this.play = true;
		connected = new Hashtable<Long, Boolean>();
		callbacks = new Hashtable<Long, IGame>();
		ready = new Hashtable<Long, Boolean>();

		players = new ArrayList<PlayerSlot>(Player.PLAYER_COUNT);

		for (int count = 1; count <= Player.PLAYER_COUNT; count++) {
			PlayerSlot player = new PlayerSlot(new Long(count), "Player" + count);
			players.add(player);
		}
		logger = Logger.getLogger(GameController.class);
	}

	//Concrete Methods
	/**
	 * Start the game and play NUMBER_OF_ROUNDS_PER_GAME
	 * rounds. Each round play
	 */
	public void initializeRound() {
		if (round < NUM_OF_ROUNDS_PER_GAME) {
			this.map = LabyrinthGenerator.generateLabyrinth();
			
			List<Coordinate> pacmanCoords = LabyrinthGenerator.getPacmanPositions();
			System.out.println("PacManCoords: " + pacmanCoords);
			if(pacmanCoords == null || pacmanCoords.size() < Player.PLAYER_COUNT) {
				// log and don't play round
				// TODO: maybe notify clients?
				logger.error("ERROR - Couldn't get enough pacman coordinates");
				return;
			}
			
			Map<Long, Coordinate> coords = new HashMap<Long, Coordinate>();
			List<Player> currentPlayers = new ArrayList<Player>();
			for (PlayerSlot player : players) {
				this.map.getSquare(pacmanCoords.get(0));
				//Square pacmanPos = new Field(0);
				Square squarePac = this.map.getSquare(pacmanCoords.get(0));
				squarePac.enter(player.getPlayer());
				player.getPlayer().initPacman(squarePac);
				pacmanCoords.remove(0);
				player.getPlayer().getPacman().setAlive(true);
				// TODO: notify clients of current pacman positions
				
				coords.put(player.getPlayerId(), player.getPlayer().getLocation().getCoordinate());
				currentPlayers.add(player.getPlayer());
			}
			
			for (PlayerSlot player : players) {
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyMapChange", this.map));
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayers", currentPlayers));
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPositions", coords));
			}

			play = true;
		}
	}

	private void playRound() {
		List<Square> checkSquares = this.movePacmans();

		if (checkSquares.size() != Player.PLAYER_COUNT) {
			this.resolveConflict(checkSquares);
		} else {
			Map<Long, Direction> directions = new HashMap<Long, Direction>();

			for (PlayerSlot player : players) {
				directions.put(player.getPlayerId(), player.getDirection());
				System.out.println("CurrentPlayerWithDir: " + player.getPlayerId() + " " + player.getDirection());
			}
			for (PlayerSlot player : players) {
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyClock", new Integer(clock), directions));
				/*try {
					player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyClock", clock, directions));
					player.getCallback().notifyClock(clock, directions);
				} catch (RemoteException ex) {
					logger.error("ERROR", ex);
				}*/
			}

			this.clock++;
		}
	}

	private List<Square> movePacmans() {
		List<Square> checkSquares = new ArrayList<Square>();

		for (PlayerSlot player : players) {
			Pacman pacman = player.getPlayer().getPacman();

			if (pacman.isAlive()) {
				Square currentSquare = pacman.getLocation();
				Direction direction = pacman.getDirection();
				Square nextSquare = map.getSquare(currentSquare, direction);



				if (SquareType.WALL.equals(nextSquare.getType())) {
					//Nothing to do. The player hits a wall.
					checkSquares.add(currentSquare);
				} else {
					currentSquare.leave(player.getPlayer());
					nextSquare.enter(player.getPlayer());

					checkSquares.add(nextSquare);
				}
			}
		}

		return checkSquares;
	}

	public void start() {
		if (this.timer == null) {
			this.timer = new Timer();
		} else {
			this.timer.cancel();
		}

		GameTimerTask tt = new GameTimerTask();

		this.initializeRound();

		for (PlayerSlot playerWrap : this.players) {
			Player player = playerWrap.getPlayer();
			
			playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyGameStarting"));
			/*try {
				playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyGameStarting"));
				playerWrap.getCallback().notifyGameStarting();
			} catch (RemoteException ex) {
				logger.error("ERROR", ex);
			}*/
		}

		timer.schedule(tt, 3000, CLOCK_LENGTH);
	}

	public static GameController getCurrentInstance() {
		synchronized ("getInstance".intern()) {
			if (instance == null) {
				try {
					instance = new GameController();
				} catch (RemoteException ex) {
					Logger.getLogger(GameController.class).error("ERROR", ex);
				}
			}
		}

		return instance;
	}

	public void endRound() {
		this.play = false;
		round++;

		//TODO somehow signal clients that a new round starts and wait for ready up of all clients.
		timer.cancel();
	}

	public Labyrinth getMap() {
		return map;
	}

	@Override
	public void changeDirection(Long playerId, Direction direction) {
		if (playerId < 0 || playerId > players.size() - 1) {
			throw new IllegalArgumentException("player id out of bounds.");
		}

		PlayerSlot playerSlot = players.get(playerId.intValue());

		playerSlot.getPlayer().getPacman().setDirection(direction);
	}

	@Override
	public void ready(Long playerId) {
		if (playerId < 0 || playerId > players.size()) {
			throw new IllegalArgumentException("player id out of bounds.");
		}

		PlayerSlot playerSlot = players.get(playerId.intValue() - 1);

		if (playerSlot.getName() == null) {
			throw new RuntimeException("player name must be set before ready is called.");
		}

		playerSlot.setReady(true);
		boolean allReady = true;

		for (PlayerSlot playerWrap : this.players) {
			if(playerWrap.getCallback() != null) {
				playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyReady", playerId));
			}
			
			/*try {
				if(playerWrap.getCallback() != null) {
					playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyReady", playerId));
					playerWrap.getCallback().notifyReady(playerId);
				}
			} catch (RemoteException ex) {
				Throwable cause = ex.getCause();
				if(cause instanceof ConnectException) {
					//TODO: Player has gone offline
					logger.warn("Player offline");
				} else {
					logger.error("ERROR", ex);
				}
			}*/
			
			if (!playerWrap.isReady()) {
				allReady = false;
			}
		}

		if (allReady) {
			this.start();
		}
	}

	@Override
	public Long connect(IGame callback) throws RemoteException {
		if(callbacks.containsValue(callback)) {
			throw new RemoteException("You are already connected.", new AlreadyConnectedException());
		}

		Long playerId = null;

		for (PlayerSlot player : players) {
			if (!player.isConnected()) {
				playerId = player.getPlayerId();
				callbacks.put(playerId, callback);
				player.setCallback(callback);
				player.setConnected(true);
				logger.info("[Player " + playerId + "] Connected");
				List<Player> currentPlayers = new ArrayList<Player>();

				for (PlayerSlot playerWrap : players) {
					currentPlayers.add(playerWrap.getPlayer());
				}
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayers", currentPlayers));
				//player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyMapChange", this.map));
				/*try {
					callback.notifyPlayers(currentPlayers);
				} catch (RemoteException ex) {
					logger.error("ERROR", ex);
				}*/

				break;
			}
		}
		
		return playerId;
	}
	
	@Override
	public void disconnect(Long playerId) {
		if (playerId < 0 || playerId > players.size()) {
			throw new IllegalArgumentException("player id must be valid when disconnecting.");
		}

		/*if(callback == null) {
		throw new IllegalArgumentException("callback must not be null when disconnecting.");
		}*/

		PlayerSlot player = players.get(playerId.intValue() - 1);

		//if(player.getCallback() == callback) {
		player.setConnected(false);
		player.setReady(false);
		player.setCallback(null);

		for (PlayerSlot playerWrap : this.players) {
			if(playerWrap.getPlayerId() != playerId) {
				playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayer", player.getPlayer()));
			}
			
			/*try {
				playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayer", player.getPlayer()));
				playerWrap.getCallback().notifyPlayer(player.getPlayer());
			} catch (RemoteException ex) {
				logger.error("ERROR", ex);
			}*/
		}
		
		logger.info("[Player " + playerId + "] disconnected");
		
		//TODO implement effects on running game
		//}
		//else {
		//	throw new IllegalArgumentException("callback must be valid when disconnecting.");
		//}
	}

	@Override
	public void setName(Long playerId, String name) {
		if (playerId < 0 || playerId > players.size()) {
			throw new IllegalArgumentException("player id must be valid when setting the name.");
		}

		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("player name must not be null.");
		}

		PlayerSlot player = players.get(playerId.intValue() - 1);
		player.setName(name);

		for (PlayerSlot playerWrap : this.players) {
			if(playerWrap.getCallback() != null) {
				playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayer", player.getPlayer()));
			}
			/*try {
				if(playerWrap.getCallback() != null) {
					playerWrap.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayer", player.getPlayer()));
					playerWrap.getCallback().notifyPlayer(player.getPlayer());
				}
			} catch (RemoteException ex) {
				Throwable cause = ex.getCause();
				if(cause instanceof ConnectException) {
					//TODO: Player has gone offline
					logger.warn("Player offline");
				} else {
					logger.error("ERROR", ex);
				}
			}*/
		}
	}

	private void resolveConflict(List<Square> checkSquares) {
		if (checkSquares.size() == 2) {
			//Two players on the same field -> find out which and decide which player won
			Square conflictingSquare = null;
			List<Player> conflictingPlayers = null;

			for (Square square : checkSquares) {
				if (SquareType.FIELD.equals(square.getType())) {
					Field field = (Field) square;

					List<Player> squarePlayers = null;

					squarePlayers = field.getPlayers();

					if (squarePlayers != null && squarePlayers.size() > 1) {
						conflictingSquare = square;
						conflictingPlayers = squarePlayers;
					}
				}
			}

			if (conflictingSquare == null || conflictingPlayers == null) {
				throw new RuntimeException("Could not find conflicting square.");
			}

			Player player1, player2;
			Pacman pacman1, pacman2, winner, loser;

			player1 = conflictingPlayers.get(0);
			player2 = conflictingPlayers.get(1);
			pacman1 = player1.getPacman();
			pacman2 = player2.getPacman();

			winner = Pacman.getWinner(pacman1, pacman2);

			if (winner == player1.getPacman()) {
				player2.sendBounty(player1);
			} else {
				player1.sendBounty(player2);
			}


		} else if (checkSquares.size() == 1) {
			//Three players on the same field -> draw
		}
	}

	public class GameTimerTask extends TimerTask {

		@Override
		public void run() {
			if (clock < CLOCKS_PER_ROUND && play) {
				GameController.this.playRound();
			} else {
				//TODO: Improve message
				logger.warn(String.format("Timer is cancelling itself because of clock={0}, play={1}", new Object[]{clock, play}));
				this.cancel();
			}
		}
	}
}
