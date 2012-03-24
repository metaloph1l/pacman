package at.ac.foop.pacman.domain;

public abstract class Square {
	//Fields
	Coordinate coordinate; //The coordinate of this square
	
	//Concrete Methods
	/**
	 * This notifies the square that a player has
	 * landed on this square. The Square will
	 * take the appropriate action according to
	 * its current state.
	 * 
	 * TODO: This is a potential point that needs
	 * synchronization
	 * 
	 * @param inboundPlayer The player which arrived on this field
	 */
	abstract public void enter(Player player);
	
	/**
	 * The player has left the square and moved
	 * to another one
	 * @param player
	 */
	abstract public void leave(Player player);
	
	abstract public void reset();
	
	abstract public SquareType getType();
	
	abstract public Integer getPoints();
	
	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}
}
