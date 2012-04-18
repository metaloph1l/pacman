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
	 * This notifies that a new map is being loaded. The
     * client needs to notify the UI and set enqueue the
     * correct GameState.
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
	
	/**
	 * When ever a new player has connected to the server and
	 * set its name. The server must inform all client of the
	 * new name of the other client.
	 * @param id The id of the client for which the name is
	 * @param name The name of the client which joined the game
	 */
	void notifyNameChange(Long id, String name) throws RemoteException;
}
