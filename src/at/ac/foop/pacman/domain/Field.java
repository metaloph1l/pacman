package at.ac.foop.pacman.domain;

/**
 *
 * @author Phil
 */
final public class Field extends Square {
	private Player occupant; //The current player on this square or null for no player
	private Integer points; //The number of points of this square
	
	public Field(Integer points) {
		this.reset();
	}
	
	@Override
	public void enter(Player inboundPlayer) {
		if(this.occupant == null) {
			this.occupant = inboundPlayer;
			
			if(points != 0) {
				inboundPlayer.addPoints(points);
				points = 0;
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
	
	@Override
	public void leave(Player player) {
		if(this.occupant == player) {
			this.occupant = null;
		} else {
			throw new RuntimeException("A Player that " +
					"is not on this field can not leave this field.");
		}
	}
	
	public Player getPlayer() {
		return this.occupant;
	}

	@Override
	public void reset() {
		this.occupant = null;
		this.points = 0;
	}
	
	@Override
	public SquareType getType() {
		return SquareType.FIELD;
	}
	
	@Override
	public Integer getPoints() {
		return points;
	}
}
