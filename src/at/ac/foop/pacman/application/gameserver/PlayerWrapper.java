package at.ac.foop.pacman.application.gameserver;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.domain.Player;

/**
 *
 * @author Phil
 */
public final class PlayerWrapper {
	private Player player;	
	private IGame callback;
	
	private boolean connected;
	private boolean ready;
	
	public PlayerWrapper(Long playerId) {
		this.reset();
		
		this.player = new Player(playerId, null);
	}
	
	public void reset() {
		this.connected = false;
		this.callback = null;
		this.ready = false;
	}

	public IGame getCallback() {
		return callback;
	}

	public void setCallback(IGame callback) {
		this.callback = callback;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public Long getPlayerId() {
		return player.getId();
	}

	public void setPlayerId(Long playerId) {
		this.player.setId(playerId);
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public String getName() {
		return player.getName();
	}

	public void setName(String name) {
		this.player.setName(name);
	}
	
	public Player getPlayer() {
		return this.player;
	}
}
