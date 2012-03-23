package at.ac.foop.pacman.application;

import java.awt.Point;
import java.rmi.Remote;
import java.util.Hashtable;

import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Square;

public interface IGameServer extends Remote {
	/**
	 * Indicate to the server that we change the
	 * direction of the pacman. The server applies
	 * this at the next possible clock cycle. We do
	 * not need to pass the 
	 * @param playerId Id of a pacman figure
	 * @param direction The direction of this pacman figure
	 * for the next clock cycle. 
	 */
	void changeDirection(Long playerId, Direction direction);
	
	/**
	 *  Signals the server that the player is ready to
	 *  start the game. The client needs to call this
	 *  after a map change.
	 *  The server should not send the first clock message
	 *  unless all clients have signalled they are ready.
	 */
	void ready();
	
	/**
	 * After the client has received a notifyMapChange
	 * message it downloads the map from the server.
	 */
	Square[][] downloadMap();
	
	/**
	 * The client also needs to download the positions of
	 * the pacman figures.
	 * @return Returns a Hashlist of Player Ids and position 
	 */
	Hashtable<Long, Point> getPositions();
}
