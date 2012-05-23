package at.ac.foop.pacman.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Phil
 * Stefan
 */
final public class Field extends Square implements Serializable {
	private List<Player> occupants; //The current player on this square or null for no player
	private Integer points; //The number of points of this square
	
	public Field(Integer points) {
		this.occupants = null;
		this.points = points;
	}
	
	@Override
	public void enter(Player inboundPlayer) {
		if(this.occupants == null) {
			this.occupants = new ArrayList<Player>();
		}
		
		if(this.occupants.contains(inboundPlayer) == false) {
			this.occupants.add(inboundPlayer);
		}
	}
	
	@Override
	public void leave(Player player) {
		if(this.occupants != null && this.occupants.remove(player)) {
		} else {
			throw new RuntimeException("A Player that " +
					"is not on this field can not leave this field.");
		}
	}
	
	public List<Player> getPlayers() {
		return new ArrayList<Player>(this.occupants);
	}

	@Override
	public void reset() {
		this.occupants = null;
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
