package at.ac.foop.pacman.application;

import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Square;
import java.util.Map;

public interface IGameServer extends Remote {
	/**
	 * Issues a connect signal to the server by the client.
	 * 
	 * @param callback The callback of the client.
	 * @returns The player id or null if all player spots are occupied.
	 */
	Long connect(IGame callback) throws RemoteException;
	
	/**
	 * Issues a disconnect signal to the server by the client.
	 * 
	 * @param callback The callback of the client. Used for authenticating the client.
	 * @param playerId The id of the player that needs to be disconnected.
	 */
	void disconnect(Long playerId) throws RemoteException;

	/**
	 * Indicate to the server that we change the
	 * direction of the pacman. The server applies
	 * this at the next possible clock cycle. We do
	 * not need to pass the 
	 * @param playerId Id of a pacman figure
	 * @param direction The direction of this pacman figure
	 * for the next clock cycle. 
	 */
	void changeDirection(Long playerId, Direction direction)
	    throws RemoteException;

	/**
	 * Signals the server that the player is ready to
	 * start the game. The client needs to call this
	 * after a map change.
	 * 
	 * Before calling this method a player name must have been
	 * set. Otherwise this method throws an exception.
	 * 
	 * The server should not send the first clock message
	 * unless all clients have signaled they are ready.
	 * @param playerId Id of the player that is ready.
	 */
	void ready(Long playerId) throws RemoteException;
	
	/**
	 * Sets the player name with the given id. Must not be null.
	 * 
	 * @param playerId Id of the player.
	 * @param name New name of the player.
	 */
	void setName(Long playerId, String name) throws RemoteException;

	/**
	 * After the client has received a notifyMapChange
	 * message it downloads the map from the server.
	 */
	Labyrinth downloadMap() throws RemoteException;

	/**
	 * The client also needs to download the positions of
	 * the pacman figures.
	 * @return Returns a Hashlist of Player Ids and position 
	 */
	Map<Long, Point> getPositions() throws RemoteException;
}
