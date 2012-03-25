package at.ac.foop.pacman.domain;

public class Square {
	//Fields
	//WallType type; //decides which kind of square this object is
	int points; //The number of points of this square
	Player occupant; //The current player on this square or null for no player
	
	//Constructors
	public Square(int points) {
		this.points = points;
	}
	
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
	public void landedOn(Player inboundPlayer) {
		if(this.occupant == null) {
			this.occupant = inboundPlayer;
			if(this.points > 0) {
				inboundPlayer.addPoints(points);
				this.points = 0;
			}
		} else {
			/**
			 * This does not mean that the inbound player is eaten
			 * instead the occupant that receives the eat message is
			 * responsible for deciding who eats whom. If the occupant
			 * has been eaten then true is returned and the occupant
			 * is replaced by the inbound player.
			 */
			boolean eaten = this.occupant.eat(inboundPlayer);
			if(eaten) {
				this.occupant = inboundPlayer;
			}
		}
	}
	
	/**
	 * The player has left the square and moved
	 * to another one
	 * @param player
	 */
	public void leave(Player player) {
		if(this.occupant == player) {
			this.occupant = null;
		} else {
			throw new RuntimeException("A Player that " +
					"is not on this field can not leave this field.");
		}
	}
}
