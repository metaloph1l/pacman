package at.ac.foop.pacman.application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

import at.ac.foop.pacman.domain.Direction;

public interface IGame extends Remote {
	/**
	 * This notifies the client that a new map should be downloaded
	 * from the server. The client then needs to pause the game and
	 * download the new map.
	 */
	void notifyMapChange()
	    throws RemoteException;
	
	/**
	 * This sends the map changes of the last clock cycle to the
	 * client.
	 * @param count The current clock cycle of the game
	 * @param directions A list of Key-Value pairs with playerId
	 * and the direction of that player.
	 */
	void clock(int count, Hashtable<Long, Direction> directions)
	    throws RemoteException;
	
	/**
	 * The game allows the color of the Pacmans to rotate in response
	 * to a certain event or based on time. Our current policy will
	 * be that the game changes the color of the Pacmans after a certain
	 * number of clock ticks (e.g. 20).
	 * 
	 * When the server calls this method, then the clients must change
	 * the color of the pacman according to this (backwards) permutation:
	 * 
	 * o(RED) = GREEN;
	 * o(BLUE) = RED;
	 * o(GREEN) = BLUE;
	 */
	void changeColor() throws RemoteException;
}
