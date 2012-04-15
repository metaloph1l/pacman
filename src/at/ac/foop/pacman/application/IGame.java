package at.ac.foop.pacman.application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Player;
import java.util.List;
import java.util.Map;

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
	void clock(int count, Map<Long, Direction> directions)
	    throws RemoteException;
	
	void startRound(List<Player> players) throws RemoteException;
	
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
	
	/**
	 * When ever a new player has connected to the server and
	 * set its name. The server must inform all client of the
	 * new name of the other client.
	 * @param id The id of the client for which the name is
	 * @param name The name of the client which joined the game
	 */
	void setName(Long id, String name) throws RemoteException;
	
	/**
	 * The server calls this method to inform the client
	 * that another player is ready. This should only be
	 * used by clients to update the Ui to display which
	 * other users are ready. It must not affect the clients
	 * game logic or game controller.
	 * 
	 * The implementation of this method is optional.
	 * @param id The id of another player that has become
	 * ready.
	 */
	void setReady(Long id) throws RemoteException;
}
