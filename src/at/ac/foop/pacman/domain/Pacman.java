package at.ac.foop.pacman.domain;

/**
*
* @author Phil
* Sebastian Geiger
*/
public class Pacman {
	//Fields
	private PacmanColor color; //the color of this pacman
	private Direction direction; //the current direction of the pacman
	Square location;

	//Concrete Methods
	public void eat(Pacman pacman) {
		
	}

	//Getters and Setters
	public Square getLocation() {
		return this.location;
	}

	public void setLocation(Square square) {
		this.location = square;
	}
	
	public Direction getDirection() {
		return this.direction;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public PacmanColor getColor() {
		return color;
	}

	public void setColor(PacmanColor color) {
		this.color = color;
	}
}
