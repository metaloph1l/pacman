package at.ac.foop.pacman.application.gameserver;

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
import at.ac.foop.pacman.domain.GameOutcome;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.LabyrinthGenerator;
import at.ac.foop.pacman.domain.Pacman;
import at.ac.foop.pacman.domain.Player;
import at.ac.foop.pacman.domain.PlayerOutcome;
import at.ac.foop.pacman.domain.PlayerSlot;
import at.ac.foop.pacman.domain.Square;
import at.ac.foop.pacman.domain.SquareType;
import at.ac.foop.pacman.util.MethodCallBuilder;

/**
 * The server GameController contains both the RMI logic and the necessary logic to handle the server side parts of
 * the game. All calls from the client are received here and then applied to the domain objects.
 * 
 * @author Philipp Grandits
 */
public class GameController extends UnicastRemoteObject implements IGameServer {
	//Constants

	private static final long serialVersionUID = 1432136270483528145L;
	public static int CLOCK_LENGTH = 500;
	public static int CLOCKS_PER_ROUND = 500;
	public static int COLOR_CHANGE_CLOCKS = 50;
	public static int NUM_OF_ROUNDS_PER_GAME = 2;
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
		if (round > NUM_OF_ROUNDS_PER_GAME) {
			// We are done with one game -> reset all Values and run new Game
			for (PlayerSlot player : players) {
				player.resetValues();
			}
		}
		this.map = LabyrinthGenerator.generateLabyrinth();
		
		List<Coordinate> pacmanCoords = LabyrinthGenerator.getPacmanPositions();
		if(pacmanCoords == null || pacmanCoords.size() < Player.PLAYER_COUNT) {
			// log and don't play round
			// TODO: maybe notify clients?
			logger.error("ERROR - Couldn't get enough pacman coordinates");
			return;
		}
		
		// first notify players of new map (no pacmans or anything is set on the map yet)
		for (PlayerSlot player : players) {
			player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyMapChange", this.map));
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
			
			coords.put(player.getPlayerId(), player.getPlayer().getLocation().getCoordinate());
			currentPlayers.add(player.getPlayer());
		}
		
		for (PlayerSlot player : players) {
			player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPlayers", currentPlayers));
			player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyPositions", coords));
		}

		play = true;
	}
	
	private Map<Long, Long> currentStatistics() {
		Map<Long, Long> statistics = new HashMap<Long, Long>();
		for (PlayerSlot player : players) {
			statistics.put(player.getPlayerId(), player.getPlayer().getPoints());
		}
		return statistics;
	}
	
	private Map<Long, Long> currentRoundStatistics() {
		Map<Long, Long> statistics = new HashMap<Long, Long>();
		for (PlayerSlot player : players) {
			statistics.put(player.getPlayerId(), player.getPlayer().getRoundPoints());
		}
		return statistics;
	}

	private void playRound() {
		// this.playerOutput();
		int activePlayers = 0;
		for (PlayerSlot player : players) {
			if(player.getPlayer().getPacman().isAlive()) {
				activePlayers++;
			}
		}
		
		if (activePlayers == 1) {
			this.endRound();
			return;
		}
		
		/*
		 * The game allows the color of the Pacmans to rotate in response
		 * to a certain event or based on time. Our current policy will
		 * be that the game changes the color of the Pacmans after a certain
		 * number of clock ticks (e.g. 20).
		 * 
		 */
		if((this.clock) > 0 && (this.clock%GameController.COLOR_CHANGE_CLOCKS == 0)) {
			for (PlayerSlot player : players) {
				player.getPlayer().changeColor();
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyColorChange"));
			}
		}
		List<Square> checkSquares = this.movePacmans();

		if (checkSquares.size() != Player.PLAYER_COUNT) {
			for (Square square : checkSquares) {
				((Field)square).resolveConflict();
			}
			//this.resolveConflict(checkSquares);
		} 
		
		Map<Long, Direction> directions = new HashMap<Long, Direction>();

		for (PlayerSlot player : players) {
			if(player.getPlayer().getPacman().isAlive() == false) {
				// TODO: NotifyDead? player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyClock", new Integer(clock), directions));
				directions.put(player.getPlayerId(), Direction.NONE);
			}
			directions.put(player.getPlayerId(), player.getDirection());
			//System.out.println("CurrentPlayerWithDir: " + player.getPlayerId() + " " + player.getDirection());
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

	private List<Square> movePacmans() {
		List<Square> checkSquares = new ArrayList<Square>();
		//logger.info("-------------- BEFORE MOVE ----------------------");
		//this.playerOutput();
		for (PlayerSlot player : players) {
			Pacman pacman = player.getPlayer().getPacman();

			if (pacman.isAlive()) {
				Square currentSquare = pacman.getLocation();
				Direction direction = pacman.getDirection();
				Square nextSquare = map.getSquare(currentSquare, direction);


				if (SquareType.WALL.equals(nextSquare.getType())) {
					//Nothing to do. The player hits a wall.
					if(checkSquares.contains(currentSquare) == false) {
						checkSquares.add(currentSquare);
					}
				} else {
					currentSquare.leave(player.getPlayer());
					nextSquare.enter(player.getPlayer());
					player.getPlayer().getPacman().setLocation(nextSquare);
					player.getPlayer().addRoundPoints(nextSquare.consumePoints());
					

					if(checkSquares.contains(nextSquare) == false) {
						checkSquares.add(nextSquare);
					}
				}
			}
		}
		//logger.info("-------------- AFTER MOVE ----------------------");
		//this.playerOutput();
		
		/*logger.info("-------------- CHECK SQUARES OUTPUT ----------------------");
		for(int i = 0; i < checkSquares.size(); i++) {
			System.out.println("CHECK SQUARE: " + checkSquares.get(i));
		}*/

		return checkSquares;
	}
	
	@SuppressWarnings("unused")
	private void playerOutput() {
		logger.debug("------------------- DEBUG PLAYER OUTPUT ------------------------");
		for (PlayerSlot player : this.players) {
			if (player == null) {
				continue;
			}
			Pacman pacman = player.getPlayer().getPacman();
			if (pacman == null) {
				continue;
			}
			
			Square currentSquare = pacman.getLocation();
			if(currentSquare != null) {
				Square currentSquareMap = map.getSquare(currentSquare, Direction.NONE);
	
				logger.debug("CURRENT PLAYER: " + player.getPlayer());
				logger.debug("CURRENT SQUARE: " + currentSquare.getCoordinate());
				logger.debug("CURRENT SQUARE: " + currentSquare);
				for(int i = 0; i < ((Field)currentSquare).getPlayers().size(); i++) {
					logger.debug("PLAYER ON SQUARE: " + ((Field)currentSquare).getPlayers().get(i));
				}
				
				logger.debug("CURRENT SQUARE FROM MAP: " + currentSquareMap.getCoordinate());
				logger.debug("CURRENT SQUARE FROM MAP: " + currentSquareMap);
				if(((Field)currentSquareMap).getPlayers().size() == 0) {
					logger.debug("NO PLAYERS ON SQUARE");
				}
				for(int i = 0; i < ((Field)currentSquareMap).getPlayers().size(); i++) {
					logger.debug("PLAYER ON SQUARE: " + ((Field)currentSquareMap).getPlayers().get(i));
				}
			}
			else {
				logger.debug("CURRENT PLAYER: " + player.getPlayer());
				logger.debug("CURRENT SQUARE: " + null);
			}
		}
	}

	public void start() {
		if (this.timer == null) {
			this.timer = new Timer();
		} else {
			// we can't cancel the timer and reuse it
			this.timer.cancel();
			this.timer = new Timer();
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
		//timer.schedule(tt, 500, CLOCK_LENGTH);
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
		timer.cancel();
		
		//TODO somehow signal clients that a new round starts and wait for ready up of all clients.
		if(this.round == GameController.NUM_OF_ROUNDS_PER_GAME){
			this.round = 0;
			Map<Long, PlayerOutcome> outcome = new HashMap<Long, PlayerOutcome>();
			List<Long> winnerIds = new ArrayList<Long>();
			Long maxPoints = 0L;
			
			for (PlayerSlot player : players) {
				if(maxPoints <= player.getPlayer().getPoints()) {
					maxPoints = player.getPlayer().getPoints();
				}
			}
			
			for (PlayerSlot player : players) {
				player.getPlayer().addPoints(player.getPlayer().getRoundPoints());
				player.getPlayer().setRoundPoints(0L);
				if(maxPoints.equals(player.getPlayer().getPoints())) {
					winnerIds.add(player.getPlayerId());
				}
			}
			
			for (PlayerSlot player : players) {
				if(winnerIds.contains(player.getPlayerId())) {
					outcome.put(player.getPlayerId(), PlayerOutcome.Victory);
				}
				else {
					outcome.put(player.getPlayerId(), PlayerOutcome.Lose);
				}
			}
			GameOutcome gameResult = null; 
			if(winnerIds.size() == 3) {
				gameResult = GameOutcome.Draw;
			}
			else {
				gameResult = GameOutcome.Normal;
			}
			
			Map<Long, Long> statistics = this.currentStatistics();
			for (PlayerSlot player : players) {
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyStatistics", statistics));
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyGameOver", gameResult, outcome));
				player.resetValues();
			}
		}
		else {
			Map<Long, Long> statistics = this.currentRoundStatistics();
			for (PlayerSlot player : players) {
				player.notifyPlayer(MethodCallBuilder.getMethodCall("notifyRoundFinished", statistics));
				player.getPlayer().addPoints(player.getPlayer().getRoundPoints());
				player.getPlayer().setRoundPoints(0L);
				player.setReady(false);
			}
		}
	}

	public Labyrinth getMap() {
		return map;
	}

	@Override
	public void changeDirection(Long playerId, Direction direction) {
		if (playerId < 0 || playerId > players.size()) {
			throw new IllegalArgumentException("player id out of bounds.");
		}

		PlayerSlot playerSlot = players.get(playerId.intValue() - 1);

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

	@SuppressWarnings("unused")
	private void resolveConflict(List<Square> checkSquares) {
		int activePlayers = 0;
		for (PlayerSlot playerWrap : this.players) {
			if(playerWrap.getPlayer().getPacman().isAlive()) {
				activePlayers++;
			}
		}
		
		if ((checkSquares.size() == 2 && activePlayers > 2) || (checkSquares.size() == 1 && activePlayers == 2)) {
			//Two players on the same field -> find out which and decide which player won
			logger.info("TWO PLAYERS ON SAME FIELD");
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

			logger.info("ConflictingSquare-Players: " + conflictingSquare + " " + conflictingPlayers);
			if (conflictingSquare == null || conflictingPlayers == null) {
				throw new RuntimeException("Could not find conflicting square.");
			}

			Player player1, player2;
			Pacman pacman1, pacman2, winner, loser;

			player1 = ((Field)conflictingSquare).getPlayers().get(0);
			player2 = ((Field)conflictingSquare).getPlayers().get(1);
			pacman1 = player1.getPacman();
			pacman2 = player2.getPacman();

			winner = Pacman.getWinner(pacman1, pacman2);

			if (winner == player1.getPacman()) {
				player2.getPacman().setAlive(false);
				conflictingSquare.leave(player2);
				player2.getPacman().setLocation(null);
				player2.sendBounty(player1);
			} else {
				player1.getPacman().setAlive(false);
				conflictingSquare.leave(player1);
				player1.getPacman().setLocation(null);
				player1.sendBounty(player2);
			}


		} else if (checkSquares.size() == 1 && activePlayers == 3) {
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
