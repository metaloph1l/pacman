package at.ac.foop.pacman.domain;

import java.util.List;

/**
 *
 * @author Phil
 */
public class Game {
	//Constants
	public static int CLOCKS_PER_ROUND = 100;
	public static int NUM_OF_ROUNDS_PER_GAME = 3;
	
	//Fields
	int round;
	int clock;
	List<Player> players;
	
	//Constructors
	
	//Concrete Methods
	/**
	 * Start the game and play NUMBER_OF_ROUNDS_PER_GAME
	 * rounds. Each round play
	 */
	public void playGame() {
		while(round < NUM_OF_ROUNDS_PER_GAME) {
			while(clock < CLOCKS_PER_ROUND) {
				playRound();
			}
			round++;
		}
	}
	
	private void playRound() {
		for(Player player : players) {
			player.takeTurn();
		}
		this.clock++;
	}
}
