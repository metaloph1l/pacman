package at.ac.foop.pacman.ui;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import at.ac.foop.pacman.application.client.GameController;
import at.ac.foop.pacman.application.client.GameState;
import at.ac.foop.pacman.domain.Direction;

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


	private final UI ui;
	private final Logger logger;
	public long myID;

	public int colorChangeSpeed;

	public GameController controller;

	//Constructor
	public Client(GameController controller, int colorChangeSpeed, long playerId) {
		this.controller = controller;
		this.logger = Logger.getLogger(Client.class);
		if(controller != null) {
			controller.addObserver(this);
		}
		//TODO: if the controller object is null, then the UI must inform
		//      the user that the client could not connect to the game server.

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
		//TODO: send cmd to observer
	}

	//TODO: optional: pauserequest

	/**
	 *
	 * call when colors change
	 * restarts Timer
	 *
	 */
	public void colorChanges(){
		//TODO: call this when color changes
		//TODO: optional: PID-Element for actual time

		this.ui.colorChangeAnimation=0;
		this.ui.colorChangeTimer.restart();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		GameState state = controller.removeState();
		switch(state) {
			case NEW_MAP:
				logger.warn("(Unimplemented) New map loadded");
				break;
			case NEW_COLOR:
				logger.warn("(Unimplemented) Color changed");
				break;
			case NEW_PLAYER:
				logger.warn("(Unimplemented) Player joined");
				break;
			case NEW_POSITION: break;
			case NEW_TURN: break;
			case PLAYER_READY: break;
			default: throw new RuntimeException("[Error] Unimplemented GameState type");
		}
	}

}
