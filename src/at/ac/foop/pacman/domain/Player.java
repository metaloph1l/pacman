package at.ac.foop.pacman.domain;

public class Player {
	//Fields
	Long id; //Uniquely identified the player
	String name; //The name of the player (for statistics)
	int points; //current number of points of the player
	Pacman pacman; //the pacman that represents the player on the board. If null then player has been eaten;

	//Constructors

	//Concrete Methods
	public void takeTurn() {
		
	}

	public void eat(Player player) {
		
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
