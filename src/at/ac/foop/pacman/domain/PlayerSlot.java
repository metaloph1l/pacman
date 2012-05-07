package at.ac.foop.pacman.domain;

import at.ac.foop.pacman.application.IGame;
import at.ac.foop.pacman.util.MethodCall;

/**
 *
 * @author Phil
 */
public final class PlayerSlot {
	private Player player;	
	private PlayerSlotThread playerSlotThread;
	private IGame callback;
	
	private boolean connected;
	private boolean ready;
	
	public PlayerSlot(Long playerId, String defaultName) {
		this.reset();
		
		this.player = new Player(playerId, defaultName);
		playerSlotThread = new PlayerSlotThread(playerId);
		new Thread(playerSlotThread).start();
	}
	
	public void notifyPlayer(MethodCall m) {
		this.playerSlotThread.addMethodCall(m);
	}
	
	public void reset() {
		this.connected = false;
		this.callback = null;
		this.ready = false;
		if(this.playerSlotThread != null) {
			this.playerSlotThread.stop();
		}
	}

	public IGame getCallback() {
		return callback;
	}

	public void setCallback(IGame callback) {
		this.callback = callback;
		this.playerSlotThread.setCallback(callback);
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
	
	public Direction getDirection() {
		return this.player.getPacman().getDirection();
	}
}
