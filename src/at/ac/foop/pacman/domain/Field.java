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
	
	public void resolveConflict() {
		if(this.occupants.size() == 3) {
			// Three players on the same field -> draw
		}else if (this.occupants.size() > 1) {
			for(int i = 0; i < this.occupants.size(); i++) {
				System.out.println(this.occupants.get(i));
			}

			Player player1, player2;
			Pacman pacman1, pacman2, winner, loser;

			player1 = this.occupants.get(0);
			player2 = this.occupants.get(1);
			pacman1 = player1.getPacman();
			pacman2 = player2.getPacman();

			winner = Pacman.getWinner(pacman1, pacman2);

			if (winner == player1.getPacman()) {
				player2.getPacman().setAlive(false);
				this.leave(player2);
				player2.getPacman().setLocation(null);
				player2.sendBounty(player1);
			} else {
				player1.getPacman().setAlive(false);
				this.leave(player1);
				player1.getPacman().setLocation(null);
				player1.sendBounty(player2);
			}
		}
		else {
			// NOTHING TODO
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

	public void resetPlayers() {
		this.occupants = null;
	}
	
	@Override
	public SquareType getType() {
		return SquareType.FIELD;
	}
	
	@Override
	public Integer getPoints() {
		return this.points;
	}
	
	@Override
	public Integer consumePoints() {
		int retPoints = this.points;
		this.points = 0;
		return retPoints;
	}
}
