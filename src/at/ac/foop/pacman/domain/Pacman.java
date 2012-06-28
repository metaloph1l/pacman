package at.ac.foop.pacman.domain;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
*
* @author Phil
* Sebastian Geiger
* Stefan
*/
public class Pacman implements Serializable {
	//Fields
	private PacmanColor color; //the color of this pacman
	private Direction direction; //the current direction of the pacman
	Square location;
	boolean alive;

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

	/**
	 * Colors are changed according to the following specification
	 * 
	 * o(RED) = GREEN;
	 * o(BLUE) = RED;
	 * o(GREEN) = BLUE;
	 */
	public void changeColor() {
		switch(this.color) {
		case RED:
			this.color = PacmanColor.GREEN;
			break;
		case BLUE: 
			this.color = PacmanColor.RED;
			break;
		case GREEN:
			this.color = PacmanColor.BLUE;
			break;
		default:			
			break;
		}
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
	/**
	 * The eat method decides if this player is eaten by another player.
	 * @param player The other player that challenged this player
	 * @return true if this player has been eaten by the other player.<br />
	 * false other wise.
	 */
	public static Pacman getWinner(Pacman pacman1, Pacman pacman2) {
		if(pacman1 == null || pacman2 == null) {
			throw new RuntimeException("pacmans must not be null.");
		}
		
		PacmanColor color1, color2;
		
		color1 = pacman1.getColor();
		color2 = pacman2.getColor();
		
		if(color1 == null || color2 == null) {
			throw new RuntimeException("colors must not be null.");
		}

		if (color1.equals(color2)) {
			throw new RuntimeException("Encountered two pacmans with the same color.");
		}
		
		Logger.getLogger(Pacman.class).debug("Pacman1 color: " + pacman1.getColor());
		Logger.getLogger(Pacman.class).debug("Pacman2 color: " + pacman2.getColor());
		
		int result = (Pacman.getNumber(color1)-Pacman.getNumber(color2))%3;
		
		if(result == 1) {
			return pacman1;
		}
		else {
			return pacman2;
		}
	}
	
	private static int getNumber(PacmanColor color) {
		switch(color) {
			case RED:
				return 1;
			case BLUE:
				return 2;
			case GREEN:
				return 3;
			default:
				throw new RuntimeException("Unknown color");
		}
	}
}
