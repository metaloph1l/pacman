package at.ac.foop.pacman.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.client.GameController;
import at.ac.foop.pacman.application.client.GameState;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Player;

/**
 * The main entry point for the gui. It shows the
 * Game Window, displays the labyrinth and handles
 * user input.
 *
 * @author
 * Sebastian Geiger: Intial Design
 * Stefan
 *
 */
public class Client implements Observer{
	
	//Fields
	private final UI ui;
	private final Logger logger;
	public long myID;
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
		this.colorChangeSpeed=colorChangeSpeed;
		this.myID=playerId;
		this.ui=new UI(this);
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
			case GAME_START:
				logger.info("GAME STARTING...");
				this.ui.initializePacmans();
				this.ui.initGameBoard();
				break;
			default: throw new RuntimeException("[Error] Unimplemented GameState type");
		}
	}

}
