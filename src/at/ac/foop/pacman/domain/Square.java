package at.ac.foop.pacman.domain;

import javax.management.RuntimeErrorException;

public class Square {
	//Fields
	//WallType type; //decides which 
	int points; //The number of points of this square
	Player player; //The current player on this square or null for no player
	
	//Constructors
	
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
	 * @param player The player which arrived on this field
	 */
	public void landedOn(Player player) {
		if(this.player == null) {
			this.player = player;
			player.addPoints(points);
			this.points = 0;
		} else {
			//decide which player eats the other one
			//and call eat on that one
		}
	}
	
	/**
	 * The player has left the square and moved
	 * to another one
	 * @param player
	 */
	public void leave(Player player) {
		if(this.player == player) {
			this.player = null;
		} else {
			throw new RuntimeException("A Player that " +
					"is not on this field can not leave this field.");
		}
	}
}
