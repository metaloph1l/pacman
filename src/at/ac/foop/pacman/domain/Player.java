package at.ac.foop.pacman.domain;

public class Player {
	//Fields

	Long id; //Uniquely identified the player
	String name; //The name of the player (for statistics)
	private Integer points; //current number of points of the player
	private Pacman pacman; //the pacman that represents the player on the board. If null then player has been eaten;
	Labyrinth map; //the current map in this round 
	boolean alive = true;

	//Constructors
	//Concrete Methods
	public void takeTurn() {
		if (alive) {
			Square currentSquare = pacman.getLocation();
			Direction direction = pacman.getDirection();
			Square nextSquare = map.getSquare(currentSquare, direction);

			if (SquareType.WALL.equals(nextSquare.getType())) {
				pacman.setLocation(nextSquare);
				/*
				 * Notify the square that this player has landed on it.
				 */
				nextSquare.enter(this);
				/*
				 * Notify the old square that the player has left the square
				 */
				currentSquare.leave(this);
			}
		}
	}

	/**
	 * The eat method decides if this player is eaten by another player.
	 * @param player The other player that challenged this player
	 * @return true if this player has been eaten by the other player.<br />
	 * false other wise.
	 */
	public boolean eat(Player player) {
		//TODO check if called correctly
		PacmanColor color = player.getPacman().getColor();

		if (this.pacman.getColor().equals(color)) {
			throw new RuntimeException("Encountered two pacmans with the same color.");
		}

		//TODO refactor switch statement into Pacman class -> currently switch + eat method redundant
		switch (this.pacman.getColor()) {
			case RED:
				if (PacmanColor.BLUE.equals(color)) {
					this.sendBounty(player);
				} else if (PacmanColor.GREEN.equals(color)) {
					player.sendBounty(this);
				} else {
					throw new RuntimeException("Unknown color");
				}

				break;
			case BLUE:
				if (PacmanColor.GREEN.equals(color)) {
					this.sendBounty(player);
				} else if (PacmanColor.RED.equals(color)) {
					player.sendBounty(this);
				} else {
					throw new RuntimeException("Unknown color");
				}

				break;
			case GREEN:
				if (PacmanColor.BLUE.equals(color)) {
					player.sendBounty(this);
				} else if (PacmanColor.RED.equals(color)) {
					this.sendBounty(player);
				} else {
					throw new RuntimeException("Unknown color");
				}

				break;
			default:
				throw new RuntimeException("Unknown color");
		}
		
		Game.getCurrentInstance().endRound();
        return false; //TODO: return correct boolean
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	private void sendBounty(Player player) {
		this.alive = false;
		player.setPoints(this.points + player.getPoints());
		points = 0;
	}

	public Pacman getPacman() {
		return pacman;
	}

	public void setPacman(Pacman pacman) {
		this.pacman = pacman;
	}

	//Getters and Setters
	public void addPoints(int points) {
		this.points = points;
	}

	public Square getLocation() {
		return pacman.getLocation();
	}

	public void setMap(Labyrinth map) {
		this.getPacman().setDirection(Direction.NONE);
		this.map = map;
	}
}
