package at.ac.foop.pacman.application;

import java.rmi.Remote;
import java.util.Hashtable;

import at.ac.foop.pacman.domain.Direction;

public interface IGame extends Remote {
	/**
	 * This notifies the client that a new map should be downloaded
	 * from the server. The client then needs to pause the game and
	 * download the new map.
	 */
	void notifyMapChange();
	
	/**
	 * This sends the map changes of the last clock cycle to the
	 * client.
	 * @param count The current clock cycle of the game
	 * @param directions A list of Key-Value pairs with playerId
	 * and the direction of that player.
	 */
	void clock(int count, Hashtable<Long, Direction> directions);
}
