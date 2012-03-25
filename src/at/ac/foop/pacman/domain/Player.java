package at.ac.foop.pacman.domain;

public class Player {
	//Fields
	Long id; //Uniquely identified the player
	String name; //The name of the player (for statistics)
	int points; //current number of points of the player
	Pacman pacman; //the pacman that represents the player on the board. If null then player has been eaten;
	Labyrinth map; //the current map in this round 

	//Constructors

	//Concrete Methods
	public void takeTurn() {
		Square currentSquare = pacman.getLocation();
		Direction direction = pacman.getDirection();
		Square nextSquare = map.getSquare(currentSquare, direction);
		pacman.setLocation(nextSquare);
		/*
		 * Notify the square that this player has landed on it.
		 */
		nextSquare.landedOn(this);
		/*
		 * Notify the old square that the player has left the square
		 */
		currentSquare.leave(this);
	}

	/**
	 * The eat method decides if this player is eaten by another player.
	 * @param player The other player that challenged this player
	 * @return true if this player has been eaten by the other player.<br />
	 * false other wise.
	 */
	public boolean eat(Player player) {
		return false;
	}

	//Getters and Setters
	public void addPoints(int points) {
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	public Square getLocation() {
		return pacman.getLocation();
	}
}
