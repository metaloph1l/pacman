package at.ac.foop.pacman.application.gameserver;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.application.IGameServer;
import at.ac.foop.pacman.domain.Direction;
import at.ac.foop.pacman.domain.Labyrinth;
import at.ac.foop.pacman.domain.Player;
import at.ac.foop.pacman.domain.Square;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Phil
 */
public class Gameserver implements IGameServer {

	private List<PlayerWrapper> players;
	private Map<Long, Boolean> connected;
	private Map<Long, IGame> callbacks;
	private Map<Long, Boolean> ready;
	private GameController gameController;

	public Gameserver() {
		players = new ArrayList<PlayerWrapper>(Player.PLAYER_COUNT);

		for (int count = 0; count < Player.PLAYER_COUNT; count++) {
			PlayerWrapper player = new PlayerWrapper(1L);
			players.add(player);
		}
	}

	public static void main(String[] args) {
	}

	@Override
	public void changeDirection(Long playerId, Direction direction) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void ready(Long playerId) {
		if (playerId < 0 || playerId > players.size() - 1) {
			throw new IllegalArgumentException("player id out of bounds.");
		}

		PlayerWrapper playerWrapper = players.get(playerId.intValue());

		if (playerWrapper.getName() == null) {
			throw new RuntimeException("player name must be set before ready is called.");
		}

		playerWrapper.setReady(true);
		boolean allReady = true;

		for (PlayerWrapper playerWrap : this.players) {
			if (!playerWrap.isReady()) {
				allReady = false;
			}
		}

		if (allReady) {
			List<Player> gamePlayers = new ArrayList<Player>();

			for (PlayerWrapper playerWrap : this.players) {
				Player player = playerWrap.getPlayer();
				gamePlayers.add(player);
			}

			GameController.generateNewInstance(gamePlayers);
			gameController = GameController.getCurrentInstance();
			
			gameController.start();			
		}
	}

	@Override
	public Labyrinth downloadMap() {
		if (gameController == null) {
			return null;
		}

		return gameController.getMap();
	}

	@Override
	public Map<Long, Point> getPositions() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Long connect(IGame callback) {
		Long playerId = null;

		for (PlayerWrapper player : players) {
			if (!player.isConnected()) {
				playerId = player.getPlayerId();

				player.setCallback(callback);
				player.setConnected(true);

				break;
			}
		}

		return playerId;
	}

	@Override
	public void disconnect(Long playerId) {
		if (playerId < 0 || playerId > players.size() - 1) {
			throw new IllegalArgumentException("player id must be valid when disconnecting.");
		}

		/*if(callback == null) {
		throw new IllegalArgumentException("callback must not be null when disconnecting.");
		}*/

		PlayerWrapper player = players.get(playerId.intValue());

		//if(player.getCallback() == callback) {
		player.setConnected(false);
		player.setReady(false);
		player.setCallback(null);

		//TODO implement effects on running game
		//}
		//else {
		//	throw new IllegalArgumentException("callback must be valid when disconnecting.");
		//}
	}

	@Override
	public void setName(Long playerId, String name) {
		if (playerId < 0 || playerId > players.size() - 1) {
			throw new IllegalArgumentException("player id must be valid when disconnecting.");
		}

		if (name == null || name.equals("")) {
			throw new IllegalArgumentException("player name must not be null.");
		}

		PlayerWrapper player = players.get(playerId.intValue());
		player.setName(name);
	}
}
