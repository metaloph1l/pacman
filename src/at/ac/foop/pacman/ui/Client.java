package at.ac.foop.pacman.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.client.GameController;
import at.ac.foop.pacman.application.client.GameState;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Player;

/**
 * The main entry point for the GUI. It create the
 * UI class, and acts as a game observer which receives the
 * game change events and in response updates the UI.
 *
 * @author
 * Sebastian Geiger: Intial Design <br/>
 * Stefan Gahr
 *
 */
public class Client implements Observer{
	
	//Fields
	private final UI ui;
	private final Logger logger;
	private long playerId;
	private Map<Long, Long> gameStatistic;
	public int colorChangeSpeed;

	public GameController controller;
	public Labyrinth map;
	public List<Player> players;

	//Constructor
	public Client(GameController controller, int colorChangeSpeed, long playerId) {
		this.controller = controller;
		this.logger = Logger.getLogger(Client.class);
		if(controller != null) {
			controller.addObserver(this);
		}
		//TODO: if the controller object is null, then the UI must inform
		//      the user that the client could not connect to the game server.

		this.players = new ArrayList<Player>();
		this.colorChangeSpeed = colorChangeSpeed;
		this.setPlayerId(playerId);
		this.ui = new UI(this);
	}

	/**
	 *
	 * Sends new direction to Server
	 *
	 * @param cmd
	 */
	public void sendcmd(Direction cmd) {
		this.controller.movePacman(cmd);
	}
	
	/**
	 *
	 * signals ready for next round
	 *
	 */
	public void signalReady() {
		this.controller.ready();
	}
	
	public Labyrinth getMap() {
		return this.map;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}

	//TODO: optional: pauserequest

	@Override
	public void update(Observable arg0, Object arg1) {
		GameState state = controller.removeState();
		switch(state) {
			case NEW_MAP:
				// we need a deep copy here
				this.map = this.controller.getMap();
				break;
			case NEW_COLOR:
				logger.warn("(Unimplemented) Color changed");
				break;
			case NEW_PLAYER:
				logger.warn("(Unimplemented) Player joined");
				break;
			case NEW_POSITION: break;
			case NEW_POSITIONS: 
				this.players.clear();
				for(int i = 0; i < this.controller.getPlayers().size(); i++) {
					this.players.add(this.controller.getPlayers().get(i));
				}
				//this.map = this.controller.getMap();
				break;
			case NEW_TURN: break;
			case END_OF_ROUND: 
				this.ui.showEndOfRoundScreen(this.controller.getRoundStatistic());
				break;
			case PLAYER_READY: break;
			case GAME_OVER:
				this.gameStatistic = this.controller.getGameStatistic();
				this.ui.showGameOverScreen();
				break;
			case GAME_START:
				logger.info("GAME STARTING...");
				this.ui.initializePacmans();
				this.ui.initGameBoard();
				break;
			default: throw new RuntimeException("[Error] Unimplemented GameState type");
		}
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	public long getGamePointsForPlayer(long playerId) {
		if(this.gameStatistic == null) {
			return 0L;
		}
		if(this.gameStatistic.containsKey(playerId)) {
			return this.gameStatistic.get(playerId);
		}
		else {
			return 0L;
		}
	}

}
