package at.ac.foop.pacman.application;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Player;
import at.ac.foop.pacman.domain.GameOutcome;
import at.ac.foop.pacman.domain.PlayerOutcome;
import java.util.List;
import java.util.Map;

public interface IGame extends Remote {
	/**
	 * This notifies that a new map is being loaded.
	 * 
	 * @param labyrinth The new map.
	 */
	void notifyMapChange(Labyrinth labyrinth) throws RemoteException;
	
	/**
	 * This notifies the client to update its representation
	 * of the players in the game. This method is used by the
	 * server once the client connects to the game.
	 * 
	 * @param players The new player objects.
	 */
	void notifyPlayers(List<Player> players) throws RemoteException;
	
	/**
	 * This notifies the client to update its representation
	 * of the supplied player. The player id is included in
	 * the object.
	 * 
	 * @param player The player to update.
	 */
	void notifyPlayer(Player player) throws RemoteException;

	/**
	 * This sends a Map of scores to the client.
	 * 
	 * @param statistics The mapping of player ids to scores.
	 */
	void notifyScore(Map<Long, Long> statistics) throws RemoteException;

	/**
	 * Notifies the client that the player with the provided
	 * player id is now ready for playing.
	 * 
	 * @param playerId The player id of the player that is now ready.
	 */
	void notifyReady(Long playerId) throws RemoteException;
	
	/**
	 * This sends the map changes of the last clock cycle to the
	 * client.
	 * @param count The current clock cycle of the game
	 * @param directions A list of Key-Value pairs with playerId
	 * and the direction of that player.
	 */
	void notifyClock(int count, Map<Long, Direction> directions)
	    throws RemoteException;
	
	/**
	 * Notifies the client that all players are ready and 
	 * the game is about to be started within a short period of time.
	 * 
	 * When this message is received the client can start to send direction
	 * changes to the server. They are then incorporated in the first
	 * clock signal of the server.
	 */
	void notifyGameStarting() throws RemoteException;
	
	/**
	 * Notifies the client that the game is over and sends along
	 * the player ids of the winning and losing player.
	 * 
	 * @param winnerId The player id of the player that won.
	 * @param loserId The player id of the player that lost.
	 */
	void notifyGameOver(GameOutcome type, Map<Long,PlayerOutcome> outcome) throws RemoteException;
	
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
	void notifyColorChange() throws RemoteException;
}
