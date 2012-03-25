package at.ac.foop.pacman.domain;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Phil
 */
public class Game {
	//Constants

	public static int CLOCK_LENGTH = 500;
	public static int CLOCKS_PER_ROUND = 100;
	public static int NUM_OF_ROUNDS_PER_GAME = 3;
	private static Game instance = null;
	//Fields
	int round;
	int clock;
	List<Player> players;
	Labyrinth map;
	boolean play;
	private Timer timer;

	//Constructors
	private Game(List<Player> players) {
		this.round = 0;
		this.clock = 0;
		this.players = players;
		this.play = true;
	}

	//Concrete Methods
	/**
	 * Start the game and play NUMBER_OF_ROUNDS_PER_GAME
	 * rounds. Each round play
	 */
	public void init() {
		if (round < NUM_OF_ROUNDS_PER_GAME) {
			this.map = LabyrinthGenerator.generateLabyrinth();

			for (Player player : players) {
				player.setMap(map);
			}
			
			play = true;
		}
	}

	private void playRound() {
		for (Player player : players) {
			player.takeTurn();
		}

		this.clock++;
	}

	public void start() {
		if (this.timer == null) {
			this.timer = new Timer();
		} else {
			this.timer.cancel();
		}

		GameTimerTask tt = new GameTimerTask();

		timer.schedule(tt, CLOCK_LENGTH);
	}

	public void end() {
		this.timer.cancel();
	}

	
	//TOD refactor into timertask?
	public void play() {
		if (clock < CLOCKS_PER_ROUND && play) {
			playRound();
		}
	}

	public static void generateNewInstance(List<Player> players) {
		synchronized ("getInstance".intern()) {
			instance = new Game(players);
		}
	}

	public static Game getCurrentInstance() {
		synchronized ("getInstance".intern()) {
			if (instance == null) {
				throw new RuntimeException("There currently is no game instance running.");
			}
		}

		return instance;
	}

	public void endRound() {
		this.play = false;
		round++;
		
		//TODO somehow signal clients that a new round starts and wait for ready up of all clients.
	}

	public class GameTimerTask extends TimerTask {

		@Override
		public void run() {
			Game.this.play();
		}
	}
}
